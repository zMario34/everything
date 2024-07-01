package tech.zmario.everything.bukkit.minigames.loader.map;

public abstract class TemplateMap<T> {

    private final T map;
    private final boolean readOnly;

    public TemplateMap(T map, boolean readOnly) {
        this.map = map;
        this.readOnly = readOnly;
    }

    public T getMap() {
        return map;
    }

    public abstract String getName();

    public abstract double[] getDefaultSpawn();

    public boolean isReadOnly() {
        return readOnly;
    }
}
