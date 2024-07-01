package tech.zmario.everything.api.configurations.adapters;

public abstract class ObjectAdapter<T, F> {

    private final Class<?> clazz;

    protected ObjectAdapter(Class<?> clazz) {
        this.clazz = clazz;
    }

    public abstract T get(F config, String key);

    public abstract void set(F config, String key, Object value);

    public Class<?> getClazz() {
        return clazz;
    }
}
