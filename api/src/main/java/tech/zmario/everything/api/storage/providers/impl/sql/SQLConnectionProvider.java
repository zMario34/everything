package tech.zmario.everything.api.storage.providers.impl.sql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import net.byteflux.libby.Library;
import tech.zmario.everything.api.EverythingLibrary;
import tech.zmario.everything.api.storage.providers.ConnectionProvider;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetProvider;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

public class SQLConnectionProvider extends ConnectionProvider<Connection> {

    private static final Library[] LIBRARIES;

    static {
        int javaVersion = Integer.parseInt(System.getProperty("java.version").split("\\.")[1]);

        LIBRARIES = new Library[]{
                Library.builder()
                        .groupId("com.zaxxer")
                        .artifactId("HikariCP")
                        .version(javaVersion < 11 ? "4.0.3" : "5.1.0")
                        .build(),
                Library.builder()
                        .groupId("mysql")
                        .artifactId("mysql-connector-java")
                        .version("8.0.23")
                        .build()
        };
    }

    private final EverythingLibrary library;
    private final String identifier;
    private HikariDataSource dataSource;

    private SQLConnectionProvider(EverythingLibrary library, String identifier, String uri) {
        super(library, identifier, uri);
        this.library = library;
        this.identifier = identifier;
    }

    public static SQLConnectionProvider create(EverythingLibrary library, String identifier, String uri) {
        return new SQLConnectionProvider(library, identifier, uri);
    }

    @Override
    public void connect() {
        HikariConfig config = new HikariConfig();

        config.setJdbcUrl(getUri());

        config.setDriverClassName("com.mysql.cj.jdbc.Driver");

        config.setMaximumPoolSize(2);
        config.setConnectionTimeout(5000);
        config.setLeakDetectionThreshold(5000);

        config.setPoolName("Everything Lib (" + identifier + ")");

        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        dataSource = new HikariDataSource(config);
    }

    @Override
    public void disconnect() {
        super.disconnect();

        if (dataSource != null && !dataSource.isClosed()) dataSource.close();
    }

    @Override
    public Connection getConnection() {
        try {
            return dataSource.getConnection();
        } catch (Exception e) {
            library.getLogger().log(Level.SEVERE, "Failed to get connection from pool", e);
            return null;
        }
    }

    @Override
    public Library[] getLibraries() {
        return LIBRARIES;
    }

    public CachedRowSet query(String query, Object... objects) {
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            for (int i = 0; i < objects.length; i++) {
                if (objects[i].getClass().isArray()) {
                    for (Object object : (Object[]) objects[i]) {
                        preparedStatement.setObject(i + 1, object);
                    }
                } else {
                    preparedStatement.setObject(i + 1, objects[i]);
                }
            }

            CachedRowSet cachedRowSet = RowSetProvider.newFactory().createCachedRowSet();
            cachedRowSet.populate(preparedStatement.executeQuery());

            return cachedRowSet;
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    public void update(String query, Object... objects) {
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            for (int i = 0; i < objects.length; i++)
                preparedStatement.setObject(i + 1, objects[i]);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    public void batchUpdate(String query, Object[][]... objects) {
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            for (Object[] object : objects) {
                for (int i = 0; i < object.length; i++)
                    preparedStatement.setObject(i + 1, object[i]);

                preparedStatement.addBatch();
            }

            preparedStatement.executeBatch();
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    public CompletableFuture<CachedRowSet> queryAsync(String query, Object... objects) {
        return CompletableFuture.supplyAsync(() -> query(query, objects), getExecutor());
    }

    public CompletableFuture<Void> batchUpdateAsync(String query, Object[]... objects) {
        return CompletableFuture.runAsync(() -> batchUpdate(query, objects), getExecutor());
    }

    public CompletableFuture<Void> updateAsync(String query, Object... objects) {
        return CompletableFuture.runAsync(() -> update(query, objects), getExecutor());
    }
}
