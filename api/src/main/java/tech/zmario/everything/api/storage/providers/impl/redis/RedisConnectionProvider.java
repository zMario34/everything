package tech.zmario.everything.api.storage.providers.impl.redis;

import com.google.gson.Gson;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisFuture;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.async.RedisAsyncCommands;
import io.lettuce.core.pubsub.RedisPubSubListener;
import io.lettuce.core.pubsub.api.async.RedisPubSubAsyncCommands;
import net.byteflux.libby.Library;
import tech.zmario.everything.api.EverythingLibrary;
import tech.zmario.everything.api.storage.providers.ConnectionProvider;
import tech.zmario.everything.api.storage.providers.impl.redis.codec.SerializedObjectCodec;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletionStage;

public class RedisConnectionProvider extends ConnectionProvider<RedisClient> {

    private static final Gson GSON = new Gson();
    private static final Library[] LIBRARIES = new Library[]{
            Library.builder()
                    .groupId("io.lettuce")
                    .artifactId("lettuce-core")
                    .version("6.1.5")
                    .build()
    };
    private RedisClient client;
    private StatefulRedisConnection<String, String> connection;
    private RedisAsyncCommands<String, String> commands;
    private RedisPubSubAsyncCommands<String, Object> pubCommands;
    private RedisPubSubAsyncCommands<String, Object> subCommands;

    protected RedisConnectionProvider(EverythingLibrary library, String identifier, String uri) {
        super(library, identifier, uri);
    }

    public static RedisConnectionProvider create(EverythingLibrary library, String identifier, String uri) {
        return new RedisConnectionProvider(library, identifier, uri);
    }

    @Override
    public void connect() {
        client = RedisClient.create(RedisURI.create(getUri()));

        connection = client.connect();
        commands = connection.async();
        pubCommands = client.connectPubSub(new SerializedObjectCodec()).async();
        subCommands = client.connectPubSub(new SerializedObjectCodec()).async();
    }

    @Override
    public void disconnect() {
        super.disconnect();

        pubCommands.getStatefulConnection().close();
        subCommands.getStatefulConnection().close();
        connection.close();
    }

    @Override
    public RedisClient getConnection() {
        return client;
    }

    @Override
    public Library[] getLibraries() {
        return LIBRARIES;
    }

    public RedisFuture<String> set(String key, Object object) {
        return commands.set(key, GSON.toJson(object));
    }

    public <T> CompletionStage<T> get(String key, Class<T> clazz) {
        return commands.get(key).thenApply(string -> GSON.fromJson(string, clazz));
    }

    public RedisFuture<Long> exists(String key) {
        return commands.exists(key);
    }

    public RedisFuture<Long> delete(String key) {
        return commands.del(key);
    }

    public RedisFuture<Boolean> expire(String key, long seconds) {
        return commands.expire(key, seconds);
    }

    public RedisFuture<Long> ttl(String key) {
        return commands.ttl(key);
    }

    public RedisFuture<Long> incr(String key) {
        return commands.incr(key);
    }

    public RedisFuture<Long> decr(String key) {
        return commands.decr(key);
    }

    public RedisFuture<Boolean> hset(String key, String field, Object object) {
        return commands.hset(key, field, GSON.toJson(object));
    }

    public <T> CompletionStage<T> hget(String key, String field, Class<T> clazz) {
        return commands.hget(key, field).thenApply(string -> GSON.fromJson(string, clazz));
    }

    public RedisFuture<Boolean> hexists(String key, String field) {
        return commands.hexists(key, field);
    }

    public <T> CompletionStage<List<T>> hgetAll(String key, Class<T> clazz) {
        return commands.hgetall(key).thenApply(map -> {
            List<T> list = new ArrayList<>();

            for (Map.Entry<String, String> entry : map.entrySet()) {
                list.add(GSON.fromJson(entry.getValue(), clazz));
            }

            return list;
        });
    }

    public RedisFuture<Long> delete(String key, String field) {
        return commands.hdel(key, field);
    }

    public void subscribe(RedisPubSubListener<String, Object> listener, String... channels) {
        subCommands.getStatefulConnection().addListener(listener);
        subCommands.subscribe(channels);
    }

    public void publish(String channel, Object message) {
        pubCommands.publish(channel, message);
    }

    public RedisAsyncCommands<String, String> getCommands() {
        return commands;
    }

    public RedisPubSubAsyncCommands<String, Object> getPubCommands() {
        return pubCommands;
    }

    public RedisPubSubAsyncCommands<String, Object> getSubCommands() {
        return subCommands;
    }
}
