package cc.minetale.commonlib.cache;

import cc.minetale.commonlib.CommonLib;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class UUIDCache {

    public static CompletableFuture<@Nullable UUID> getFromCache(String name) {
        return new CompletableFuture<UUID>()
                .completeAsync(() -> {
                    String uuidString;

                    try (var redis = CommonLib.getJedisPool().getResource()) {
                        uuidString = redis.hget("minetale:uuid-cache", name.toUpperCase());
                    }

                    if(uuidString != null) {
                        return UUID.fromString(uuidString);
                    }

                    return null;
                });
    }

    public static CompletableFuture<Void> updateCacheAsync(String name, UUID uuid) {
        return CompletableFuture.runAsync(() -> updateCache(name, uuid));
    }

    public static void updateCache(String name, UUID uuid) {
        try (var redis = CommonLib.getJedisPool().getResource()) {
            redis.hset("minetale:uuid-cache", name.toUpperCase(), uuid.toString());
        }
    }

}
