package cc.minetale.commonlib.cache;

import cc.minetale.commonlib.CommonLib;
import cc.minetale.commonlib.pigeon.payloads.profile.ProfileUpdatePayload;
import cc.minetale.commonlib.profile.CachedProfile;
import cc.minetale.commonlib.profile.Profile;
import cc.minetale.commonlib.util.PigeonUtil;
import cc.minetale.commonlib.util.ProfileUtil;
import cc.minetale.commonlib.util.Redis;
import com.fasterxml.jackson.core.JsonProcessingException;
import redis.clients.jedis.params.SetParams;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class ProfileCache {

    public static CompletableFuture<Void> updateStatus(UUID uuid, String server) {
        return CompletableFuture.runAsync(() -> {
            try {
                var cachedProfile = ProfileUtil.getCachedProfile(uuid).get();

                if (cachedProfile != null) {
                    cachedProfile.setServer(server);

                    try (var redis = CommonLib.getJedisPool().getResource()) {
                        redis.set(
                                getKey(uuid.toString()),
                                CommonLib.getMapper().writeValueAsString(cachedProfile),
                                SetParams.setParams().ex(TimeUnit.DAYS.toSeconds(2))
                        );
                    }
                }
            } catch (InterruptedException | ExecutionException | JsonProcessingException ignored) {}
        });
    }

    public static CompletableFuture<Void> updateProfile(Profile profile) {
        return CompletableFuture.runAsync(() -> {
            try {
                var uuid = profile.getUuid();
                var oldCached = ProfileUtil.getCachedProfile(uuid).get();

                if (oldCached != null) {
                    var newCached = new CachedProfile(oldCached, profile);

                    try (var redis = CommonLib.getJedisPool().getResource()) {
                        redis.set(
                                getKey(uuid.toString()),
                                CommonLib.getMapper().writeValueAsString(newCached),
                                SetParams.setParams().ex(TimeUnit.DAYS.toSeconds(2))
                        );
                    }

                    profile.save()
                            .thenRun(() -> PigeonUtil.broadcast(new ProfileUpdatePayload(profile.getUuid())));
                }
            } catch (InterruptedException | ExecutionException | JsonProcessingException ignored) {}
        });
    }

    public static CompletableFuture<Void> createCachedProfile(Profile profile) {
        return CompletableFuture.runAsync(() -> {
            var cachedProfile = new CachedProfile(profile);

            Redis.runRedisCommand(jedis -> {
                try {
                    return jedis.set(
                            getKey(cachedProfile.getProfile().getUuid().toString()),
                            CommonLib.getMapper().writeValueAsString(cachedProfile),
                            SetParams.setParams().ex(TimeUnit.DAYS.toSeconds(2))
                    );
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }

                return null;
            });
        });
    }

    public static String getKey(String player) {
        return "minetale:profile-cache:" + player;
    }

}
