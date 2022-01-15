package cc.minetale.commonlib.cache;

import cc.minetale.commonlib.CommonLib;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class UUIDCache {

    public static CompletableFuture<String> getName(UUID uuid) {
        return new CompletableFuture<String>()
                .completeAsync(() -> {
                    try (var redis = CommonLib.getJedisPool().getResource()) {
                        var name = redis.hget(getUuidKey(), uuid.toString());

                        if(name != null) {
                            return name;
                        }
                    }

                    return null;
                });
    }

    public static CompletableFuture<UUID> getUuid(String name) {
        return new CompletableFuture<UUID>()
                .completeAsync(() -> {
                    try (var redis = CommonLib.getJedisPool().getResource()) {
                        var uuid = redis.hget(getNameKey(), name.toUpperCase());

                        if(uuid != null) {
                            return UUID.fromString(uuid);
                        }
                    }

                    return null;
                });
    }

    public static CompletableFuture<Void> updateCache(String name, UUID uuid) {
        return CompletableFuture.runAsync(() -> {
                try (var pipeline = CommonLib.getJedisPool().getResource().pipelined()) {
                    var oldName = pipeline.hget(getUuidKey(), uuid.toString()).get();

                    if(!oldName.equalsIgnoreCase(name.toUpperCase())) {
                        pipeline.del(oldName);
                    }

                    pipeline.hset(getNameKey(), name.toUpperCase(), uuid.toString());
                    pipeline.hset(getUuidKey(), uuid.toString(), name);

                    pipeline.sync();
                }
        });
    }

    public static String getNameKey() {
        return "minetale:name-to-uuid";
    }

    public static String getUuidKey() {
        return "minetale:uuid-to-name";
    }

}
