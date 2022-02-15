package cc.minetale.commonlib.cache;

import cc.minetale.commonlib.util.Redis;
import cc.minetale.commonlib.util.Request;
import lombok.Getter;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public record RequestCache(String key, int ttl) {

    @Getter private static final RequestCache partyRequest;
    @Getter private static final RequestCache friendRequest;

    static {
        partyRequest = new RequestCache("party-request", 30 * 60);
        friendRequest = new RequestCache("friend-request", 7 * 24 * 60 * 60);
    }

    public CompletableFuture<Set<Request>> getOutgoingRequests(UUID player) {
        return new CompletableFuture<Set<Request>>().completeAsync(() -> {
            var rawRequests = Redis.runRedisCommand(jedis -> jedis.smembers(getOutgoingKey(player)));
            var requests = new HashSet<Request>();

            if (rawRequests != null && !rawRequests.isEmpty()) {
                for (var request : rawRequests) {
                    var split = request.split(":");

                    requests.add(new Request(
                            player,
                            UUID.fromString(split[0]),
                            (Long.parseLong(split[1]) * 1000L) - System.currentTimeMillis()
                    ));
                }
            }

            return requests;
        });
    }

    public CompletableFuture<Set<Request>> getIncomingRequests(UUID player) {
        return new CompletableFuture<Set<Request>>().completeAsync(() -> {
            var rawRequests = Redis.runRedisCommand(jedis -> jedis.smembers(getIncomingKey(player)));
            var requests = new HashSet<Request>();

            if (rawRequests != null && !rawRequests.isEmpty()) {
                for (var request : rawRequests) {
                    var split = request.split(":");

                    requests.add(new Request(
                            UUID.fromString(split[0]),
                            player,
                            (Long.parseLong(split[1]) * 1000L) - System.currentTimeMillis()
                    ));
                }
            }

            return requests;
        });
    }

    public CompletableFuture<Void> pushCache(UUID inviter, UUID target) {
        return CompletableFuture.runAsync(() -> {
            var cur = System.currentTimeMillis();

            var outgoing = target + ":" + (cur + ttl * 1000L);
            var incoming = inviter + ":" + (cur + ttl * 1000L);

            Redis.runRedisCommand(jedis -> {
                var pipeline = jedis.pipelined();

                pipeline.sadd(getOutgoingKey(inviter), outgoing);
                pipeline.sadd(getIncomingKey(target), incoming);

                pipeline.sync();

                return null;
            });

            Redis.runRedisCommand(jedis -> {
                var pipeline = jedis.pipelined();

                pipeline.sendCommand(Redis.CustomCommand.EXPIREMEMBER, getOutgoingKey(inviter), outgoing, String.valueOf(ttl));
                pipeline.sendCommand(Redis.CustomCommand.EXPIREMEMBER, getIncomingKey(target), incoming, String.valueOf(ttl));

                pipeline.sync();

                return null;
            });
        });
    }

    public String getOutgoingKey(UUID identifier) {
        return "minetale:outgoing-" + key + ":" + identifier;
    }

    public String getIncomingKey(UUID identifier) {
        return "minetale:incoming-" + key + ":" + identifier;
    }


}
