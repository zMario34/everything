package tech.zmario.everything.api.addons;

import java.util.Map;

public class AddonProperties {

    private final String name;
    private final String version;
    private final String author;
    private final String description;
    private final String mainClassName;

    public AddonProperties(Map<String, Object> properties) {
        this.name = (String) properties.get("name");
        this.version = (String) properties.get("version");
        this.author = (String) properties.get("author");
        this.description = (String) properties.get("description");
        this.mainClassName = (String) properties.get("mainClassName");
    }

    public String getName() {
        return name;
    }

    public String getAuthor() {
        return author;
    }

    public String getDescription() {
        return description;
    }

    public String getMainClassName() {
        return mainClassName;
    }

    public String getVersion() {
        return version;
    }

    @Override
    public String toString() {
        return "AddonProperties{" +
                "name='" + name + '\'' +
                ", version='" + version + '\'' +
                ", author='" + author + '\'' +
                ", description='" + description + '\'' +
                ", mainClassName='" + mainClassName + '\'' +
                '}';
    }
}
