package cc.minetale.commonlib.cache;

import cc.minetale.commonlib.util.Redis;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class UUIDCache {

    public static CompletableFuture<String> getName(UUID uuid) {
        return new CompletableFuture<String>()
                .completeAsync(() -> Redis.runRedisCommand(jedis -> jedis.hget(getUuidToNameKey(), uuid.toString())));
    }

    public static CompletableFuture<UUID> getUuid(String name) {
        return new CompletableFuture<UUID>()
                .completeAsync(() -> {
                    String uuid = Redis.runRedisCommand(jedis -> jedis.hget(getNameToUuidKey(), name.toUpperCase()));

                    if(uuid != null) {
                        return UUID.fromString(uuid);
                    }

                    return null;
                });
    }

    public static CompletableFuture<Void> pushCache(UUID uuid, String name) {
        return CompletableFuture.runAsync(() -> Redis.runRedisCommand(jedis -> {
            var oldName = jedis.hget(
                    getUuidToNameKey(),
                    uuid.toString()
            );

            var pipeline = jedis.pipelined();

            if(oldName != null && !oldName.equalsIgnoreCase(name)) {
                pipeline.del(oldName);
            }

            pipeline.hset(
                    getNameToUuidKey(),
                    name.toUpperCase(),
                    uuid.toString()
            );

            pipeline.hset(
                    getUuidToNameKey(),
                    uuid.toString(),
                    name
            );

            pipeline.sync();

            return null;
        }));
    }

    public static String getNameToUuidKey() {
        return "minetale:name-to-uuid";
    }

    public static String getUuidToNameKey() {
        return "minetale:uuid-to-name";
    }

}
