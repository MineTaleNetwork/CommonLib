package cc.minetale.commonlib.cache;

import cc.minetale.commonlib.CommonLib;
import cc.minetale.commonlib.api.Grant;
import cc.minetale.commonlib.profile.Profile;
import cc.minetale.commonlib.redis.RedisUtil;
import cc.minetale.commonlib.util.JSONUtil;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class GrantCache {

    public static void createManualCache(Profile profile, List<Grant> grants) {
        new Thread(() -> RedisUtil.runRedisCommand(redis -> {
            var pipeline = redis.pipelined();

            var key = "minetale:grant-cache:" + profile.getUuid();

            pipeline.set(key, CommonLib.getGson().toJson(grants));
            pipeline.expire(key, 7200);
            pipeline.sync();

            return null;
        })).start();
    }

    public static void createCache(Profile profile) {
        new Thread(() -> RedisUtil.runRedisCommand(redis -> {
            var pipeline = redis.pipelined();

            var key = "minetale:grant-cache:" + profile.getUuid();

            pipeline.set(key, CommonLib.getGson().toJson(Grant.getGrants(profile)));
            pipeline.expire(key, 7200);
            pipeline.sync();

            return null;
        })).start();
    }

    public static void updateCache(Profile profile) {
        new Thread(() -> RedisUtil.runRedisCommand(redis -> {
            var pipeline = redis.pipelined();

            var key = "minetale:grant-cache:" + profile.getUuid();

            pipeline.set(key, CommonLib.getGson().toJson(profile.getGrants()));
            pipeline.expire(key, 7200);
            pipeline.sync();

            return null;
        })).start();
    }

    public static CompletableFuture<List<Grant>> getCachedGrants(Profile profile) {
        return new CompletableFuture<List<Grant>>()
                .completeAsync(() -> {
                    var jsonGrants = RedisUtil.runRedisCommand(redis -> redis.get("minetale:grant-cache:" + profile.getUuid()));

                    if (jsonGrants != null) {
                        return JSONUtil.fromJson(jsonGrants, JSONUtil.getTypeToken(Grant.class));
                    }

                    var grants = Grant.getGrants(profile);

                    createManualCache(profile, grants);

                    return grants;
                });
    }

}
