package cc.minetale.commonlib.cache;

import cc.minetale.commonlib.pigeon.payloads.profile.ProfileUpdatePayload;
import cc.minetale.commonlib.profile.CachedProfile;
import cc.minetale.commonlib.profile.Profile;
import cc.minetale.commonlib.util.JsonUtil;
import cc.minetale.commonlib.util.PigeonUtil;
import cc.minetale.commonlib.util.ProfileUtil;
import cc.minetale.commonlib.util.Redis;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class ProfileCache {

    public static CompletableFuture<Void> updateParty(UUID player, UUID party) {
        return CompletableFuture.runAsync(() -> {
            try {
                var cachedProfile = ProfileUtil.getCachedProfile(player).get();

                if (cachedProfile != null) {
                    cachedProfile.setParty(party);

                    pushCache(cachedProfile).get();
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });
    }

    public static CompletableFuture<Void> updateLastMessaged(UUID player, UUID lastMessaged) {
        return CompletableFuture.runAsync(() -> {
            try {
                var cachedProfile = ProfileUtil.getCachedProfile(player).get();

                if (cachedProfile != null) {
                    cachedProfile.setLastMessaged(lastMessaged);

                    pushCache(cachedProfile).get();
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

                    pushCache(cachedProfile).get();
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
                    cachedProfile.setProfile(profile);
                    cachedProfile.setGrants(profile.getGrants());
                    cachedProfile.setPunishments(profile.getPunishments());

                    pushCache(cachedProfile).get();

                    PigeonUtil.broadcast(new ProfileUpdatePayload(profile.getUuid()));
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });
    }

    public static CompletableFuture<Void> pushCache(Profile profile) {
        return pushCache(new CachedProfile(profile));
    }

    public static CompletableFuture<Void> pushCache(CachedProfile profile) {
        var uuid = profile.getProfile().getUuid().toString();
        var json = JsonUtil.writeToJson(profile);

        if(json == null) {
            return CompletableFuture.completedFuture(null);
        }

        return CompletableFuture.runAsync(() -> {
            Redis.runRedisCommand(jedis -> jedis.hset(
                    getKey(),
                    uuid,
                    json
            ));

            Redis.expireMember(getKey(), uuid, 12 * 60 * 60);
        });
    }

    public static CompletableFuture<CachedProfile> getCache(UUID uuid) {
        return new CompletableFuture<CachedProfile>()
                .completeAsync(() -> JsonUtil.readFromJson(Redis.runRedisCommand(jedis -> jedis.hget(getKey(), uuid.toString())), CachedProfile.class));
    }

    public static String getKey() {
        return "minetale:profile-cache";
    }

}
