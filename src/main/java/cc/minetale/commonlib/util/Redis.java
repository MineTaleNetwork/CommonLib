package cc.minetale.commonlib.util;

import cc.minetale.commonlib.CommonLib;
import lombok.experimental.UtilityClass;
import redis.clients.jedis.CommandArguments;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.commands.JedisCommands;
import redis.clients.jedis.commands.ProtocolCommand;
import redis.clients.jedis.util.SafeEncoder;

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

    public static Integer ttlMember(String key, String member) {
        return (Integer) runRedisCommand(jedis -> jedis.sendCommand(CustomCommand.TTL, key, member));
    }

    public static Integer expireMember(String key, String member, int ttl) {
        return (Integer) runRedisCommand(jedis -> jedis.sendCommand(CustomCommand.EXPIREMEMBER, key, member, String.valueOf(ttl)));
    }

    public interface RedisCommand<T> {
        T execute(Jedis paramJedis);
    }

    public enum CustomCommand implements ProtocolCommand {
        EXPIREMEMBER("EXPIREMEMBER"),
        TTL("TTL");

        private final byte[] raw;

        CustomCommand(String command) {
            this.raw = SafeEncoder.encode(command);
        }

        public byte[] getRaw() {
            return this.raw;
        }
    }

}
