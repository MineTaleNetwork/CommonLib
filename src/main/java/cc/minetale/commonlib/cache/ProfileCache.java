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

    // TODO -> Maybe pipeline?
    public static CompletableFuture<Void> pushCache(CachedProfile profile) {
        var uuid = profile.getProfile().getUuid().toString();

        return CompletableFuture.runAsync(() -> {
            Redis.runRedisCommand(jedis ->
                jedis.hset(
                        "minetale:profile-cache",
                        uuid,
                        JsonUtil.writeToJson(profile)
                ));

            Redis.expireMember("minetale:profile-cache", uuid, 12 * 60 * 60);
        });
    }

    public CompletableFuture<Void> updateParty(UUID player, UUID party) {
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

    public CompletableFuture<Void> updateLastMessaged(UUID player, UUID lastMessaged) {
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

    public CompletableFuture<Void> updateStatus(UUID uuid, String server) {
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

    public CompletableFuture<Void> updateProfile(Profile profile) {
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

}
