package cc.minetale.commonlib.cache;

import cc.minetale.commonlib.CommonLib;
import cc.minetale.commonlib.friend.FriendRequest;
import redis.clients.jedis.params.SetParams;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class FriendCache {

    public static CompletableFuture<Set<FriendRequest>> getOutgoingRequests(UUID player) {
        return new CompletableFuture<Set<FriendRequest>>()
                .completeAsync(() -> {
                    try (var redis = CommonLib.getJedisPool().getResource()) {
                        var response = redis.keys(getKey(player.toString(), "*"));

                        var friendRequests = new HashSet<FriendRequest>();

                        for(var request : response) {
                            var key = request.split(":");

                            friendRequests.add(new FriendRequest(UUID.fromString(key[2]), UUID.fromString(key[3]), redis.pttl(request)));
                        }

                        return friendRequests;
                    }
                });
    }

    public static CompletableFuture<Set<FriendRequest>> getIncomingRequests(UUID player) {
        return new CompletableFuture<Set<FriendRequest>>()
                .completeAsync(() -> {
                    try (var redis = CommonLib.getJedisPool().getResource()) {
                        var response = redis.keys(getKey("*", player.toString()));

                        var friendRequests = new HashSet<FriendRequest>();

                        for(var request : response) {
                            var key = request.split(":");

                            friendRequests.add(new FriendRequest(UUID.fromString(key[2]), UUID.fromString(key[3]), redis.pttl(request)));
                        }

                        return friendRequests;
                    }
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
