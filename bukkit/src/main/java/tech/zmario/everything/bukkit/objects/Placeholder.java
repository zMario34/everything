package tech.zmario.everything.bukkit.objects;

public class Placeholder {

    private final String key;
    private final String value;

    private Placeholder(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public static Placeholder of(String key, String value) {
        return new Placeholder(key, value);
    }

    public static Placeholder of(String key, int value) {
        return new Placeholder(key, value + "");
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
