package cc.minetale.commonlib.cache;

import cc.minetale.commonlib.util.Redis;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class UUIDCache {

    public static CompletableFuture<String> getName(UUID uuid) {
        return new CompletableFuture<String>()
                .completeAsync(() -> Redis.runRedisCommand(jedis -> jedis.hget(getUuidKey(), uuid.toString())));
    }

    public static CompletableFuture<UUID> getUuid(String name) {
        return new CompletableFuture<UUID>()
                .completeAsync(() -> {
                    String uuid;
                    return ((uuid = Redis.runRedisCommand(jedis -> jedis.hget(getNameKey(), name.toUpperCase()))) != null) ?
                            UUID.fromString(uuid) : null;
                });
    }

    public static CompletableFuture<Void> updateCache(String name, UUID uuid) {
        return CompletableFuture.runAsync(() -> Redis.runRedisCommand(jedis -> {
            var pipeline = jedis.pipelined();
            var oldName = pipeline.hget(getUuidKey(), uuid.toString()).get();

            if(!oldName.equalsIgnoreCase(name)) {
                pipeline.del(oldName);
            }

            pipeline.hset(getNameKey(), name.toUpperCase(), uuid.toString());
            pipeline.hset(getUuidKey(), uuid.toString(), name);

            pipeline.sync();

            return null;
        }));
    }

    public static String getNameKey() {
        return "minetale:name-to-uuid";
    }

    public static String getUuidKey() {
        return "minetale:uuid-to-name";
    }

}
