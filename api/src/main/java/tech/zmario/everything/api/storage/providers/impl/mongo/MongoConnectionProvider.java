package tech.zmario.everything.api.storage.providers.impl.mongo;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import net.byteflux.libby.Library;
import org.bson.Document;
import tech.zmario.everything.api.EverythingLibrary;
import tech.zmario.everything.api.storage.providers.ConnectionProvider;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MongoConnectionProvider extends ConnectionProvider<MongoCollection<Document>> {

    private static final Library[] LIBRARIES = new Library[]{
            Library.builder()
                    .groupId("org.mongodb")
                    .artifactId("mongodb-driver-sync")
                    .version("4.2.3")
                    .build()
    };

    private final String collectionName;
    private MongoClient client;
    private MongoCollection<Document> collection;

    protected MongoConnectionProvider(EverythingLibrary library, String identifier, String uri, String collectionName) {
        super(library, identifier, uri);
        this.collectionName = collectionName;
    }

    public static MongoConnectionProvider create(EverythingLibrary library, String identifier, String uri,
                                                 String collectionName) {
        return new MongoConnectionProvider(library, identifier, uri, collectionName);
    }

    @Override
    public void connect() {
        String uri = getUri();
        String database = uri.replace("mongodb://", "").split("/")[1];

        client = MongoClients.create(uri);

        MongoDatabase mongoDatabase = client.getDatabase(database);

        collection = mongoDatabase.getCollection(collectionName);
    }

    @Override
    public void disconnect() {
        super.disconnect();

        if (client != null) client.close();
    }

    @Override
    public MongoCollection<Document> getConnection() {
        return collection;
    }

    @Override
    public Library[] getLibraries() {
        return LIBRARIES;
    }

    public CompletableFuture<Void> insert(Document document) {
        return CompletableFuture.runAsync(() -> collection.insertOne(document), getExecutor());
    }

    public CompletableFuture<Void> delete(Document document) {
        return CompletableFuture.runAsync(() -> collection.deleteOne(document), getExecutor());
    }

    public CompletableFuture<Void> update(Document document) {
        return CompletableFuture.runAsync(() -> collection.replaceOne(document, document), getExecutor());
    }

    public CompletableFuture<Document> find(Document document) {
        return CompletableFuture.supplyAsync(() -> collection.find(document).first(), getExecutor());
    }

    public MongoCollection<Document> getCollection() {
        return collection;
    }
}
