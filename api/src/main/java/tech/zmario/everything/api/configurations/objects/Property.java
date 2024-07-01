package tech.zmario.everything.api.configurations.objects;

public class Property<T> {

    private final String key;
    private T defaultValue;

    public Property(String key, T defaultValue) {
        this.key = key;
        this.defaultValue = defaultValue;
    }

    public static <T> Property<T> create(String key, T defaultValue) {
        return new Property<>(key, defaultValue);
    }

    public String getKey() {
        return key;
    }

    public T getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(T value) {
        this.defaultValue = value;
    }

    @Override
    public String toString() {
        return "Property{" +
                "key='" + key + '\'' +
                ", value=" + defaultValue +
                '}';
    }
}
