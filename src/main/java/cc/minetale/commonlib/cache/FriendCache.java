package cc.minetale.commonlib.cache;

import cc.minetale.commonlib.friend.FriendRequest;
import cc.minetale.commonlib.util.Redis;
import redis.clients.jedis.params.SetParams;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class FriendCache {

    public static CompletableFuture<Set<FriendRequest>> getOutgoingRequests(UUID player) {
        return new CompletableFuture<Set<FriendRequest>>()
                .completeAsync(() -> Redis.runRedisCommand(jedis -> {
                    var friendRequests = new HashSet<FriendRequest>();

                    for(var request : jedis.keys(getKey(player.toString(), "*"))) {
                        var key = request.split(":");

                        friendRequests.add(new FriendRequest(
                                UUID.fromString(key[2]),
                                UUID.fromString(key[3]),
                                jedis.pttl(request)
                        ));
                    }

                    return friendRequests;
                }));
    }

    public static CompletableFuture<Set<FriendRequest>> getIncomingRequests(UUID player) {
        return new CompletableFuture<Set<FriendRequest>>()
                .completeAsync(() -> Redis.runRedisCommand(jedis -> {
                    var friendRequests = new HashSet<FriendRequest>();

                    for(var request : jedis.keys(getKey("*", player.toString()))) {
                        var key = request.split(":");

                        friendRequests.add(new FriendRequest(
                                UUID.fromString(key[2]),
                                UUID.fromString(key[3]),
                                jedis.pttl(request)
                        ));
                    }

                    return friendRequests;
                }));
    }

    public static CompletableFuture<Boolean> hasRequest(UUID player, UUID target) {
        return new CompletableFuture<Boolean>()
                .completeAsync(() -> Redis.runRedisCommand(jedis -> jedis.exists(
                        getKey(player.toString(), target.toString())
                )));
    }

    public static CompletableFuture<Void> removeCache(UUID player, UUID target) {
        return CompletableFuture.runAsync(() -> Redis.runRedisCommand(jedis -> jedis.del(
                getKey(player.toString(), target.toString())
        )));
    }

    public static CompletableFuture<Void> updateCache(UUID player, UUID target) {
        return CompletableFuture.runAsync(() -> Redis.runRedisCommand(jedis -> jedis.set(
                getKey(player.toString(), target.toString()),
                "",
                new SetParams().ex(TimeUnit.DAYS.toSeconds(7))
        )));
    }

    public static String getKey(String player, String target) {
        return "minetale:friend-requests:" + player + ":" + target;
    }

}
