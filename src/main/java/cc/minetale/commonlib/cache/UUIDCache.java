package cc.minetale.commonlib.cache;

import cc.minetale.commonlib.util.Redis;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class UUIDCache {

    public static CompletableFuture<String> getName(UUID uuid) {
        return new CompletableFuture<String>()
                .completeAsync(() -> Redis.runRedisCommand(jedis -> jedis.hget(
                        getUuidKey(),
                        uuid.toString()
                )));
    }

    public static CompletableFuture<UUID> getUuid(String name) {
        return new CompletableFuture<UUID>()
                .completeAsync(() -> {
                    String uuid;

                    return ((uuid = Redis.runRedisCommand(jedis -> jedis.hget(
                            getNameKey(),
                            name.toUpperCase()
                    ))) != null) ? UUID.fromString(uuid) : null;
                });
    }

    public static CompletableFuture<Void> updateCache(UUID uuid, String name) {
        return CompletableFuture.runAsync(() -> Redis.runRedisCommand(jedis -> {
            var oldName = jedis.hget(
                    getUuidKey(),
                    uuid.toString()
            );

            if(oldName != null && !oldName.equalsIgnoreCase(name)) {
                jedis.del(oldName);
            }

            jedis.hset(
                    getNameKey(),
                    name.toUpperCase(),
                    uuid.toString()
            );

            jedis.hset(
                    getUuidKey(),
                    uuid.toString(),
                    name
            );

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
