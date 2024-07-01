package tech.zmario.everything.bukkit.minigames.loader.map.impl.bukkit;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.entity.Player;
import org.bukkit.generator.ChunkGenerator;
import org.jetbrains.annotations.NotNull;
import tech.zmario.everything.bukkit.minigames.GameHandler;
import tech.zmario.everything.bukkit.minigames.loader.MapLoader;
import tech.zmario.everything.bukkit.minigames.loader.map.TemplateMap;
import tech.zmario.everything.bukkit.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Random;

public class BukkitMapLoader extends MapLoader<World> {

    public BukkitMapLoader(GameHandler gameHandler) {
        super(gameHandler);
    }

    @Override
    public TemplateMap<World> loadMap(String name, boolean readOnly) {
        World world = Utils.SERVER.createWorld(WorldCreator.name(name));

        if (world == null) return createMap(name);

        if (readOnly) {
            world.setAutoSave(false);
            world.setKeepSpawnInMemory(false);
        }

        return new TemplateBukkitMap(world, readOnly);
    }

    @Override
    public boolean unloadMap(String name) {
        TemplateMap<World> map = getMap(name);

        if (map == null) return false;

        for (Player player : map.getMap().getPlayers())
            player.teleport(Utils.SERVER.getWorlds().get(0).getSpawnLocation());

        return Utils.SERVER.unloadWorld(map.getMap(), !map.isReadOnly());
    }

    @Override
    public TemplateMap<World> createMap(String name) {
        WorldCreator creator = WorldCreator.name(name)
                .type(WorldType.FLAT)
                .environment(World.Environment.NORMAL)
                .generator(VoidChunkGenerator.INSTANCE)
                .generateStructures(false);
        World world = Utils.SERVER.createWorld(creator);

        return new TemplateBukkitMap(world, false);
    }

    @Override
    public TemplateMap<?> cloneMap(TemplateMap<?> template) {
        String name = template.getName() + "_clone_" + System.currentTimeMillis();

        File source = new File(Utils.SERVER.getWorldContainer(), template.getName());
        File target = new File(Utils.SERVER.getWorldContainer(), name);

        try {
            Files.copy(source.toPath(), target.toPath());
        } catch (IOException e) {
            getGameHandler().getEverything().getLogger().severe("Failed to clone world folder: " + name);
            return null;
        }

        new File(target, "level.dat_mcr").delete();
        new File(target, "session.lock").delete();
        new File(target, "uid.dat").delete();
        new File(target, "level.dat").delete();
        new File(target, "level.dat_old").delete();

        return loadMap(name, false);
    }

    @Override
    public boolean deleteMap(String name) {
        File worldFolder = new File(Utils.SERVER.getWorldContainer(), name);

        if (!worldFolder.exists()) return false;

        try {
            Files.delete(worldFolder.toPath());

            return true;
        } catch (IOException e) {
            getGameHandler().getEverything().getLogger().severe("Failed to delete world folder: " + name);
            return false;
        }
    }

    @Override
    public String getIdentifier() {
        return "bukkit";
    }

    private static class VoidChunkGenerator extends ChunkGenerator {

        public static final VoidChunkGenerator INSTANCE = new VoidChunkGenerator();

        @Override
        public @NotNull ChunkData generateChunkData(@NotNull World world, @NotNull Random random, int x, int z, @NotNull BiomeGrid biome) {
            return createChunkData(world);
        }

        @Override
        public boolean canSpawn(@NotNull World world, int x, int z) {
            return true;
        }

        @Override
        public @NotNull Location getFixedSpawnLocation(@NotNull World world, @NotNull Random random) {
            return new Location(world, 0, 64, 0);
        }
    }
}
