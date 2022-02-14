package cc.minetale.commonlib.cache;

import cc.minetale.commonlib.util.Redis;
import cc.minetale.commonlib.util.Request;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class FriendRequestCache {

    public static CompletableFuture<Set<Request>> getOutgoingRequests(UUID player) {
        return new CompletableFuture<Set<Request>>().completeAsync(() -> {
            var rawRequests = Redis.runRedisCommand(jedis -> jedis.smembers(getOutgoingKey(player)));
            var requests = new HashSet<Request>();

            if (rawRequests != null && !rawRequests.isEmpty()) {
                for (var request : rawRequests) {
                    var split = request.split(":");

                    requests.add(new Request(player, UUID.fromString(split[0]), (Long.parseLong(split[1]) * 1000L) - System.currentTimeMillis()));
                }
            }

            return requests;
        });
    }

    public static CompletableFuture<Set<Request>> getIncomingRequests(UUID player) {
        return new CompletableFuture<Set<Request>>().completeAsync(() -> {
            var rawRequests = Redis.runRedisCommand(jedis -> jedis.smembers(getIncomingKey(player)));
            var requests = new HashSet<Request>();

            if (rawRequests != null && !rawRequests.isEmpty()) {
                for (var request : rawRequests) {
                    var split = request.split(":");

                    requests.add(new Request(UUID.fromString(split[0]), player, (Long.parseLong(split[1]) * 1000L) - System.currentTimeMillis()));
                }
            }

            return requests;
        });
    }

    public static CompletableFuture<Void> putRequest(UUID inviter, UUID target) {
        return CompletableFuture.runAsync(() -> {
            var ttl = 604800;
            var cur = System.currentTimeMillis();

            var outgoing = target + ":" + (cur + ttl * 1000);
            var incoming = inviter + ":" + (cur + ttl * 1000);

            Redis.runRedisCommand(jedis -> jedis.sadd(getOutgoingKey(inviter), outgoing));
            Redis.runRedisCommand(jedis -> jedis.sadd(getIncomingKey(target), incoming));

            Redis.expireMember(getOutgoingKey(inviter), outgoing, ttl);
            Redis.expireMember(getIncomingKey(target), incoming, ttl);
        });
    }

    public static String getOutgoingKey(UUID player) {
        return "minetale:outgoing-friend-request:" + player;
    }

    public static String getIncomingKey(UUID player) {
        return "minetale:incoming-friend-request:" + player;
    }


}
