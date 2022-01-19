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

                    writeCachedProfile(cachedProfile).get();
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });
    }

    public static CompletableFuture<Void> updateProfile(Profile profile) {
        return CompletableFuture.runAsync(() -> {
            try {
                profile.save().get();

                var cachedProfile = ProfileUtil.getCachedProfile(profile.getUuid()).get();

                if (cachedProfile != null) {
                    var newProfile = new CachedProfile(profile);
                    newProfile.setServer(cachedProfile.getServer());

                    writeCachedProfile(cachedProfile).get();
                }

                PigeonUtil.broadcast(new ProfileUpdatePayload(profile.getUuid()));
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });
    }

    public static CompletableFuture<Void> writeCachedProfile(CachedProfile cachedProfile) {
        return CompletableFuture.runAsync(() -> Redis.runRedisCommand(jedis -> {
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
        }));
    }

    public static String getKey(String player) {
        return "minetale:profile-cache:" + player;
    }

}
