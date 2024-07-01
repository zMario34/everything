package tech.zmario.everything.bukkit.minigames.loader.map.impl.bukkit;

import org.bukkit.Location;
import org.bukkit.World;
import tech.zmario.everything.bukkit.minigames.loader.map.TemplateMap;

public class TemplateBukkitMap extends TemplateMap<World> {

    public TemplateBukkitMap(World map, boolean readOnly) {
        super(map, readOnly);
    }

    @Override
    public String getName() {
        String name = getMap().getName();

        if (name.contains("_clone_")) name = name.substring(0, name.indexOf("_clone_"));

        return name;
    }

    @Override
    public double[] getDefaultSpawn() {
        Location spawnLocation = getMap().getSpawnLocation();

        return new double[]{
                spawnLocation.getX(),
                spawnLocation.getY(),
                spawnLocation.getZ()
        };
    }
}
