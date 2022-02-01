package cc.minetale.commonlib.cache.type;

import cc.minetale.commonlib.util.Redis;
import lombok.Getter;
import redis.clients.jedis.params.SetParams;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Getter
public class BaseCache {

    private final String cacheName;
    private final Long ttl;

    public BaseCache(String cacheName, Long ttl) {
        this.cacheName = cacheName;
        this.ttl = ttl;
    }

    public CompletableFuture<Long> ttl(Object... keys) {
        return new CompletableFuture<Long>().completeAsync(() -> Redis.runRedisCommand(jedis ->
                jedis.pttl(getKey(keys))));
    }

    public CompletableFuture<Boolean> has(Object... keys) {
        return new CompletableFuture<Boolean>().completeAsync(() -> Redis.runRedisCommand(jedis ->
                jedis.exists(getKey(keys))));
    }

    public CompletableFuture<Void> remove(Object... keys) {
        return CompletableFuture.runAsync(() -> Redis.runRedisCommand(jedis ->
                jedis.del(getKey(keys))));
    }

    public CompletableFuture<Void> update(String value, Object... keys) {
        return CompletableFuture.runAsync(() -> Redis.runRedisCommand(jedis ->
                jedis.set(getKey(keys), value, ttl != null ? SetParams.setParams().px(ttl) : null)));
    }

    public CompletableFuture<String> get(Object... keys) {
        return new CompletableFuture<String>().completeAsync(() -> Redis.runRedisCommand(jedis ->
                jedis.get(getKey(keys))));
    }

    public String getKey(Object... keys) {
        StringBuilder base = new StringBuilder("minetale:" + cacheName);

        for(var key : keys) {
            base.append(":").append(key.toString());
        }

        return base.toString();
    }

}
