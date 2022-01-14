package cc.minetale.commonlib.cache;

import cc.minetale.commonlib.CommonLib;
import redis.clients.jedis.params.SetParams;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class UUIDCache {

    public static CompletableFuture<UUID> getFromCache(String name) {
        return new CompletableFuture<UUID>()
                .completeAsync(() -> {
                    String uuidString;

                    try (var redis = CommonLib.getJedisPool().getResource()) {
                        uuidString = redis.get(getKey(name) + name.toUpperCase());
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
        CompletableFuture.runAsync(() -> {
            try (var redis = CommonLib.getJedisPool().getResource()) {
                redis.set(getKey(name), uuid.toString(), SetParams.setParams().ex(TimeUnit.DAYS.toSeconds(4)));
            }
        });
    }

    public static String getKey(String name) {
        return "minetale:uuid-cache:" + name.toUpperCase();
    }

}
