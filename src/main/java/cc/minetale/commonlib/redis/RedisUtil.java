package cc.minetale.commonlib.redis;

import redis.clients.jedis.JedisPool;

public class RedisUtil {

    private static JedisPool jedisPool;

    public static void init() {
        jedisPool = new JedisPool("127.0.0.1", 6379);
    }

    public static <T> T runRedisCommand(RedisCommand<T> redisCommand) {
        var jedis = jedisPool.getResource();
        T result = null;

        try {
            result = redisCommand.execute(jedis);
        } catch (Exception e) {
            e.printStackTrace();

            if (jedis != null) {
                jedisPool.returnBrokenResource(jedis);
                jedis = null;
            }
        } finally {
            if (jedis != null) {
                jedisPool.returnResource(jedis);
            }
        }

        return result;
    }

}
