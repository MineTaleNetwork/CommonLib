package cc.minetale.commonlib.cache;

import cc.minetale.commonlib.CommonLib;
import cc.minetale.commonlib.api.Punishment;
import cc.minetale.commonlib.profile.Profile;
import cc.minetale.commonlib.redis.RedisUtil;
import cc.minetale.commonlib.util.JSONUtil;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class PunishmentCache {

    public static void createManualCache(Profile profile, List<Punishment> punishments) {
        new Thread(() -> RedisUtil.runRedisCommand(redis -> {
            var pipeline = redis.pipelined();

            var key = "minetale:punishment-cache:" + profile.getUuid();

            pipeline.set(key, CommonLib.getGson().toJson(punishments));
            pipeline.expire(key, 7200);
            pipeline.sync();

            return null;
        })).start();
    }

    public static void createCache(Profile profile) {
        new Thread(() -> RedisUtil.runRedisCommand(redis -> {
            var pipeline = redis.pipelined();

            var key = "minetale:punishment-cache:" + profile.getUuid();

            pipeline.set(key, CommonLib.getGson().toJson(Punishment.getPunishments(profile)));
            pipeline.expire(key, 7200);
            pipeline.sync();

            return null;
        })).start();
    }

    public static void updateCache(Profile profile) {
        new Thread(() -> RedisUtil.runRedisCommand(redis -> {
            var pipeline = redis.pipelined();

            var key = "minetale:punishment-cache:" + profile.getUuid();

            pipeline.set(key, CommonLib.getGson().toJson(profile.getPunishments()));
            pipeline.expire(key, 7200);
            pipeline.sync();

            return null;
        })).start();
    }

    public static CompletableFuture<List<Punishment>> getCachedPunishments(Profile profile) {
        return new CompletableFuture<List<Punishment>>()
                .completeAsync(() -> {
                    var jsonPunishments = RedisUtil.runRedisCommand(redis -> redis.get("minetale:punishment-cache:" + profile.getUuid()));

                    if (jsonPunishments != null) {
                        return JSONUtil.fromJson(jsonPunishments, JSONUtil.getTypeToken(Punishment.class));
                    }

                    var punishments = Punishment.getPunishments(profile);

                    createManualCache(profile, punishments);

                    return punishments;
                });
    }

}
