package cc.minetale.commonlib.cache;

import cc.minetale.commonlib.cache.type.BaseCache;
import cc.minetale.commonlib.pigeon.payloads.profile.ProfileUpdatePayload;
import cc.minetale.commonlib.profile.Profile;
import cc.minetale.commonlib.util.JsonUtil;
import cc.minetale.commonlib.util.PigeonUtil;
import cc.minetale.commonlib.util.ProfileUtil;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class ProfileCache extends BaseCache {

    public ProfileCache() {
        super("profile-cache", TimeUnit.HOURS.toMillis(12L));
    }

    public CompletableFuture<Void> updateParty(UUID player, UUID party) {
        return CompletableFuture.runAsync(() -> {
            try {
                var cachedProfile = ProfileUtil.getCachedProfile(player).get();

                if (cachedProfile != null) {
                    cachedProfile.setParty(party);

                    update(JsonUtil.writeToJson(cachedProfile)).get();
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

                    update(JsonUtil.writeToJson(cachedProfile)).get();
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

                    update(JsonUtil.writeToJson(cachedProfile)).get();
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

                    update(JsonUtil.writeToJson(cachedProfile)).get();

                    PigeonUtil.broadcast(new ProfileUpdatePayload(profile.getUuid()));
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });
    }

}
