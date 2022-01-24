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

    public static CompletableFuture<Void> updateLastMessaged(UUID uuid, UUID lastMessaged) {
        return CompletableFuture.runAsync(() -> {
            try {
                var cachedProfile = ProfileUtil.getCachedProfile(uuid).get();

                if (cachedProfile != null) {
                    cachedProfile.setLastMessaged(lastMessaged);

                    updateCache(cachedProfile).get();
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });
    }

    public static CompletableFuture<Void> updateStatus(UUID uuid, String server) {
        return CompletableFuture.runAsync(() -> {
            try {
                var cachedProfile = ProfileUtil.getCachedProfile(uuid).get();

                if (cachedProfile != null) {
                    cachedProfile.setServer(server);

                    updateCache(cachedProfile).get();
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });
    }

    public static CompletableFuture<Void> updateProfile(Profile profile) {
        return CompletableFuture.runAsync(() -> {
            try {
                var cachedProfile = ProfileUtil.getCachedProfile(profile.getUuid()).get();

                if (cachedProfile != null) {
                    var newProfile = new CachedProfile(profile);
                    newProfile.setServer(cachedProfile.getServer());

                    updateCache(newProfile).get();
                }

                PigeonUtil.broadcast(new ProfileUpdatePayload(profile.getUuid()));
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });
    }

    public static CompletableFuture<Void> updateCache(CachedProfile cachedProfile) {
        return CompletableFuture.runAsync(() -> Redis.runRedisCommand(jedis -> {
            try {
                return jedis.set(
                        getKey(cachedProfile.getProfile().getUuid().toString()),
                        CommonLib.getJsonMapper().writeValueAsString(cachedProfile),
                        SetParams.setParams().ex(TimeUnit.HOURS.toSeconds(12))
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
