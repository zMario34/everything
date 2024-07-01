package tech.zmario.everything.bukkit.minigames.loader.map.impl.slime;

import com.grinderwolf.swm.api.world.SlimeWorld;
import com.grinderwolf.swm.api.world.properties.SlimeProperties;
import tech.zmario.everything.bukkit.minigames.loader.map.TemplateMap;

public class TemplateSlimeMap extends TemplateMap<SlimeWorld> {

    public TemplateSlimeMap(SlimeWorld map, boolean readOnly) {
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
        return new double[]{
                getMap().getPropertyMap().getInt(SlimeProperties.SPAWN_X),
                getMap().getPropertyMap().getInt(SlimeProperties.SPAWN_Y),
                getMap().getPropertyMap().getInt(SlimeProperties.SPAWN_Z)
        };
    }
}
