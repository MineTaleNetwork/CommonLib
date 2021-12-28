package cc.minetale.commonlib.cache;

import cc.minetale.commonlib.redis.RedisUtil;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class UUIDCache {

    public static void updateCache(String name, UUID uuid) {
        new Thread(() -> RedisUtil.runRedisCommand(redis -> redis.hset("minetale:uuid-cache", name.toUpperCase(), uuid.toString()))).start();
    }

    public static CompletableFuture<@Nullable UUID> getFromCache(String name) {
        return new CompletableFuture<UUID>()
                .completeAsync(() -> {
                    var uuid = RedisUtil.runRedisCommand(redis -> redis.hget("minetale:uuid-cache", name.toUpperCase()));

                    if(uuid != null) {
                        return UUID.fromString(uuid);
                    }

                    return null;
                });
    }

}
