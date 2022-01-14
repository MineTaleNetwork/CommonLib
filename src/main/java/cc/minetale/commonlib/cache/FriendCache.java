package cc.minetale.commonlib.cache;

import cc.minetale.commonlib.CommonLib;
import redis.clients.jedis.params.SetParams;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class FriendCache {

    public static CompletableFuture<Set<UUID>> getOutgoingRequests(UUID player) {
        return new CompletableFuture<Set<UUID>>()
                .completeAsync(() -> {
                    Set<String> stringRequests;

                    try (var redis = CommonLib.getJedisPool().getResource()) {
                        stringRequests = redis.keys(getKey(player.toString(), "*"));
                    }

                    Set<UUID> uuidRequests = new HashSet<>();

                    for(var request : stringRequests) {
                        uuidRequests.add(UUID.fromString(request.split(":")[3]));
                    }

                    return uuidRequests;
                });
    }

    public static CompletableFuture<Set<UUID>> getIncomingRequests(UUID player) {
        return new CompletableFuture<Set<UUID>>()
                .completeAsync(() -> {
                    Set<String> stringRequests;

                    try (var redis = CommonLib.getJedisPool().getResource()) {
                        stringRequests = redis.keys(getKey("*", player.toString()));
                    }

                    Set<UUID> uuidRequests = new HashSet<>();

                    for(var request : stringRequests) {
                        uuidRequests.add(UUID.fromString(request.split(":")[2]));
                    }

                    return uuidRequests;
                });
    }

    public static CompletableFuture<Boolean> hasRequest(UUID player, UUID target) {
        return new CompletableFuture<Boolean>()
                .completeAsync(() -> {
                    boolean request;

                    try (var redis = CommonLib.getJedisPool().getResource()) {
                        request = redis.exists(getKey(player.toString(), target.toString()));
                    }

                    return request;
                });
    }

    public static CompletableFuture<Void> removeCache(UUID player, UUID target) {
        return CompletableFuture.runAsync(() -> {
            try (var redis = CommonLib.getJedisPool().getResource()) {
                redis.del(getKey(player.toString(), target.toString()));
            }
        });

    }

    public static CompletableFuture<Void> updateCacheAsync(UUID player, UUID target) {
        return CompletableFuture.runAsync(() -> updateCache(player, target));
    }

    public static CompletableFuture<Void> updateCache(UUID player, UUID target) {
        return CompletableFuture.runAsync(() -> {
            try (var redis = CommonLib.getJedisPool().getResource()) {
                redis.set(getKey(player.toString(), target.toString()), "true", new SetParams().ex(TimeUnit.DAYS.toSeconds(7)));
            }
        });
    }

    public static String getKey(String player, String target) {
        return "minetale:friend-requests:" + player + ":" + target;
    }

}
