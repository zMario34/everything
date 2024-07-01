package tech.zmario.everything.api.storage.providers;

import net.byteflux.libby.Library;
import net.byteflux.libby.LibraryManager;
import tech.zmario.everything.api.EverythingLibrary;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public abstract class ConnectionProvider<T> {

    private final EverythingLibrary library;
    private final String identifier;
    private final String uri;

    private ExecutorService executor;

    protected ConnectionProvider(EverythingLibrary library, String identifier, String uri) {
        this.library = library;
        this.identifier = identifier;
        this.uri = uri;

        LibraryManager libraryManager = library.getLibraryManager();

        libraryManager.addMavenCentral();
        libraryManager.addJitPack();

        for (Library dependency : getLibraries()) libraryManager.loadLibrary(dependency);
    }

    public abstract void connect();

    public void disconnect() {
        if (executor != null) {
            executor.shutdown();

            try {
                if (!executor.awaitTermination(5, TimeUnit.SECONDS))
                    executor.shutdownNow();
            } catch (InterruptedException e) {
                executor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }

    public abstract T getConnection();

    public String getIdentifier() {
        return identifier;
    }

    public String getUri() {
        return uri;
    }

    public ExecutorService getExecutor() {
        if (executor == null) {
            executor = Executors.newSingleThreadExecutor();
        }

        return executor;
    }

    public EverythingLibrary getLibrary() {
        return library;
    }

    public abstract Library[] getLibraries();

}
