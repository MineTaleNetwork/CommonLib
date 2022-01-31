package cc.minetale.commonlib.cache.type;

import cc.minetale.commonlib.util.Redis;
import cc.minetale.commonlib.util.Request;
import redis.clients.jedis.params.ScanParams;
import redis.clients.jedis.resps.ScanResult;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class RequestCache extends BaseCache {

    public RequestCache(String cacheName, Long ttl) {
        super(cacheName, ttl);
    }

    public CompletableFuture<Set<Request>> getIncoming(UUID identifier) {
        return getRequests("*", identifier.toString());
    }

    public CompletableFuture<Set<Request>> getOutgoing(UUID identifier) {
        return getRequests(identifier.toString(), "*");
    }

    private CompletableFuture<Set<Request>> getRequests(String identifier, String target) {
        return new CompletableFuture<Set<Request>>()
                .completeAsync(() -> {
                    var requests = new HashSet<Request>();

                    try {
                        var keys = scan(identifier, target).get();

                        for (var json : keys) {
                            var key = json.split(":");
                            var playerUuid = UUID.fromString(key[2]);
                            var targetUuid = UUID.fromString(key[3]);
                            var ttl = ttl(playerUuid.toString(), targetUuid.toString()).get();

                            requests.add(new Request(
                                    playerUuid,
                                    targetUuid,
                                    ttl
                            ));
                        }

                        return requests;
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }

                    return requests;
                });

    }

    private CompletableFuture<Set<String>> scan(String identifier, String target) {
        CompletableFuture<Set<String>> future = new CompletableFuture<>();

        future.completeAsync(() -> {
            final var params = new ScanParams().count(5000).match("minetale:" + getCacheName() + ":" + identifier + ":" + target);
            final Set<String> result = new HashSet<>();

            var cursor = "0";
            do {
                var currentCursor = cursor;
                ScanResult<String> scanResult = Redis.runRedisCommand(jedis -> jedis.scan(currentCursor, params));
                if (scanResult == null)
                    return result;

                result.addAll(scanResult.getResult());

                cursor = scanResult.getCursor();
            } while (!cursor.equals("0"));

            return result;
        });

        return future;
    }


}
