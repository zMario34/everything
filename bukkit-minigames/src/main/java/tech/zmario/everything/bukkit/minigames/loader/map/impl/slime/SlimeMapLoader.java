package tech.zmario.everything.bukkit.minigames.loader.map.impl.slime;

import com.grinderwolf.swm.api.SlimePlugin;
import com.grinderwolf.swm.api.exceptions.*;
import com.grinderwolf.swm.api.loaders.SlimeLoader;
import com.grinderwolf.swm.api.world.SlimeWorld;
import com.grinderwolf.swm.api.world.properties.SlimeProperties;
import com.grinderwolf.swm.api.world.properties.SlimePropertyMap;
import org.bukkit.World;
import org.bukkit.entity.Player;
import tech.zmario.everything.bukkit.minigames.GameHandler;
import tech.zmario.everything.bukkit.minigames.loader.map.TemplateMap;
import tech.zmario.everything.bukkit.minigames.loader.MapLoader;
import tech.zmario.everything.bukkit.utils.Utils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SlimeMapLoader extends MapLoader<SlimeWorld> {

    private static final SlimePropertyMap DEFAULT_PROPERTIES = new SlimePropertyMap();

    static {
        DEFAULT_PROPERTIES.setBoolean(SlimeProperties.ALLOW_ANIMALS, false);
        DEFAULT_PROPERTIES.setBoolean(SlimeProperties.ALLOW_MONSTERS, false);
        DEFAULT_PROPERTIES.setBoolean(SlimeProperties.PVP, true);
    }

    private final SlimePlugin slimePlugin = (SlimePlugin) Utils.SERVER.getPluginManager().getPlugin("SlimeWorldManager");
    private final Map<String, SlimeWorld> savedWorlds = new HashMap<>();
    private String loaderType;

    public SlimeMapLoader(GameHandler gameHandler, String loader) {
        super(gameHandler);

        setLoaderType(loader);
    }

    @Override
    public TemplateMap<SlimeWorld> loadMap(String name, boolean readOnly) {
        SlimeLoader loader = getLoader();
        try {
            SlimeWorld world;

            if (loader.worldExists(name)) {
                world = slimePlugin.loadWorld(loader, name, readOnly, DEFAULT_PROPERTIES);
            } else {
                world = slimePlugin.createEmptyWorld(loader, name, false, DEFAULT_PROPERTIES);
            }

            generateWorld(world);

            return new TemplateSlimeMap(world, readOnly);
        } catch (UnknownWorldException | WorldInUseException | IOException | CorruptedWorldException |
                 NewerFormatException | WorldAlreadyExistsException e) {
            getGameHandler().getEverything().getLogger().severe("Failed to load world " + name);
        }

        return null;
    }

    @Override
    public boolean unloadMap(String name) {
        World world = Utils.SERVER.getWorld(name);

        if (world != null) {
            for (Player player : world.getPlayers()) {
                player.teleport(Utils.SERVER.getWorlds().get(0).getSpawnLocation());
            }

            return Utils.SERVER.unloadWorld(world, false);
        }

        return false;
    }

    @Override
    public TemplateMap<SlimeWorld> createMap(String name) {
        return null;
    }

    @Override
    public TemplateMap<?> cloneMap(TemplateMap<?> template) {
        SlimeWorld oldWorld = savedWorlds.get(template.getName());
        SlimeWorld clonedWorld = oldWorld.clone(template.getName() + "_clone_" + System.nanoTime());

        generateWorld(clonedWorld);

        return new TemplateSlimeMap(clonedWorld, false);
    }

    @Override
    public boolean deleteMap(String name) {
        try {
            getLoader().deleteWorld(name);
            savedWorlds.remove(name);

            return true;
        } catch (UnknownWorldException | IOException e) {
            getGameHandler().getEverything().getLogger().severe("Failed to delete world " + name);
            return false;
        }
    }

    @Override
    public String getIdentifier() {
        return "slime";
    }

    public void generateWorld(SlimeWorld world) {
        Utils.SERVER.getScheduler().runTask(getGameHandler().getEverything().getPlugin(), () -> {
            slimePlugin.generateWorld(world);
            savedWorlds.put(world.getName(), world);
        });
    }

    public void setLoaderType(String loaderType) {
        this.loaderType = loaderType;
    }

    public SlimeLoader getLoader() {
        SlimeLoader loader = slimePlugin.getLoader(loaderType);

        if (loader == null) {
            throw new IllegalArgumentException("Loader type " + loaderType + " does not exist");
        }

        return loader;
    }
}
