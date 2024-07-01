package tech.zmario.everything.api.addons;

import tech.zmario.everything.api.EverythingLibrary;

import java.io.File;

public abstract class Addon {

    private final EverythingLibrary library;
    private final File dataFolder;

    public Addon(EverythingLibrary library, File dataFolder) {
        this.library = library;
        this.dataFolder = dataFolder;
    }

    public abstract void enable();

    public abstract void disable();

    public EverythingLibrary getLibrary() {
        return library;
    }

    public File getDataFolder() {
        return dataFolder;
    }
}
