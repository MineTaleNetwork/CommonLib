package cc.minetale.commonlib.util;

import cc.minetale.commonlib.CommonLib;
import lombok.experimental.UtilityClass;
import redis.clients.jedis.Jedis;

@UtilityClass
public class Redis {

    public static <T> T runRedisCommand(RedisCommand<T> redisCommand) {
        try(var jedis = CommonLib.getJedisPool().getResource()) {
            return redisCommand.execute(jedis);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public interface RedisCommand<T> {
        T execute(Jedis paramJedis);
    }

}
