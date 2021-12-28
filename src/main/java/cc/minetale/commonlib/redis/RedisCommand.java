package cc.minetale.commonlib.redis;

import redis.clients.jedis.Jedis;

public interface RedisCommand<T> {
    T execute(Jedis paramJedis);
}
