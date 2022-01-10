package cc.minetale.commonlib.cache;

import cc.minetale.commonlib.CommonLib;
import cc.minetale.commonlib.grant.Grant;
import cc.minetale.commonlib.pigeon.payloads.profile.ProfileUpdatePayload;
import cc.minetale.commonlib.profile.CachedProfile;
import cc.minetale.commonlib.profile.Profile;
import cc.minetale.commonlib.punishment.Punishment;
import cc.minetale.commonlib.util.Database;
import cc.minetale.commonlib.util.PigeonUtil;
import com.google.gson.JsonSyntaxException;
import com.mongodb.client.model.Filters;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class ProfileCache {

    public static CompletableFuture<List<Profile>> getProfiles(List<UUID> uuids) {
        return new CompletableFuture<List<Profile>>()
                .completeAsync(() -> {
                    var gson = CommonLib.getGson();
                    var profiles = new ArrayList<Profile>();

                    List<String> jsonArray;

                    try (var redis = CommonLib.getJedisPool().getResource()) {
                        jsonArray = redis.mget(uuids.stream()
                                .map(uuid -> "minetale:profile-cache:" + uuid)
                                .toArray(String[]::new));
                    }

                    var index = 0;
                    var iterator = jsonArray.iterator();
                    var nonCachedUuids = new ArrayList<UUID>();

                    while (iterator.hasNext()) {
                        if (iterator.next() == null) {
                            nonCachedUuids.add(uuids.get(index));
                            iterator.remove();
                        }

                        index++;
                    }

                    var documents = Database.getProfilesCollection()
                            .find(Filters.in("_id", nonCachedUuids));

                    for (var document : documents)
                        profiles.add(gson.fromJson(document.toJson(), Profile.class));

                    for (var profileJson : jsonArray) {
                        profiles.add(gson.fromJson(profileJson, CachedProfile.class).getProfile());
                    }

                    return profiles;
                });
    }

    public static CompletableFuture<@Nullable Profile> getProfile(UUID uuid) {
        return new CompletableFuture<Profile>()
                .completeAsync(() -> {
                    try {
                        var cachedProfile = getFromCache(uuid).get();

                        if (cachedProfile != null) {
                            var profile = cachedProfile.getProfile();

                            profile.setGrants(cachedProfile.getGrants());
                            profile.setPunishments(cachedProfile.getPunishments());

                            UUIDCache.updateCache(profile.getName(), profile.getUuid());

                            return profile;
                        }

                        var databaseProfile = getFromDatabase(uuid).get();

                        if (databaseProfile != null) {
                            var grants = Grant.getGrants(databaseProfile).get(3, TimeUnit.SECONDS);
                            databaseProfile.setGrants(grants);

                            var punishments = Punishment.getPunishments(databaseProfile).get(3, TimeUnit.SECONDS);
                            databaseProfile.setPunishments(punishments);

                            updateCache(databaseProfile);
                            UUIDCache.updateCache(databaseProfile.getName(), databaseProfile.getUuid());

                            return databaseProfile;
                        }
                    } catch (InterruptedException | ExecutionException | TimeoutException ignored) {}

                    return null;
                });
    }

    public static CompletableFuture<@Nullable Profile> getProfile(String name) {
        return new CompletableFuture<Profile>()
                .completeAsync(() -> {
                    try {
                        var cachedProfile = getFromCache(name).get();

                        if (cachedProfile != null) {
                            var profile = cachedProfile.getProfile();

                            profile.setGrants(cachedProfile.getGrants());
                            profile.setPunishments(cachedProfile.getPunishments());

                            UUIDCache.updateCache(profile.getName(), profile.getUuid());

                            return profile;
                        }

                        var databaseProfile = getFromDatabase(name).get();

                        if (databaseProfile != null) {
                            var grants = Grant.getGrants(databaseProfile).get(3, TimeUnit.SECONDS);
                            databaseProfile.setGrants(grants);

                            var punishments = Punishment.getPunishments(databaseProfile).get(3, TimeUnit.SECONDS);
                            databaseProfile.setPunishments(punishments);

                            updateCache(databaseProfile);
                            UUIDCache.updateCache(databaseProfile.getName(), databaseProfile.getUuid());

                            return databaseProfile;
                        }
                    } catch (InterruptedException | ExecutionException | TimeoutException ignored) {}

                    return null;
                });
    }

    public static CompletableFuture<@Nullable Profile> getFromDatabase(UUID uuid) {
        return new CompletableFuture<Profile>()
                .completeAsync(() -> {
                    var collection = Database.getProfilesCollection();
                    var document = collection.find(Filters.eq("_id", uuid.toString())).first();

                    if (document == null) {
                        return null;
                    }

                    return CommonLib.getGson().fromJson(document.toJson(), Profile.class);
                });
    }

    public static CompletableFuture<@Nullable Profile> getFromDatabase(String name) {
        return new CompletableFuture<Profile>()
                .completeAsync(() -> {
                    var collection = Database.getProfilesCollection();
                    var document = collection.find(Filters.eq("search", name.toUpperCase())).first();

                    if (document == null) {
                        return null;
                    }

                    return CommonLib.getGson().fromJson(document.toJson(), Profile.class);
                });
    }

    public static CompletableFuture<@Nullable CachedProfile> getFromCache(UUID uuid) {
        return new CompletableFuture<CachedProfile>()
                .completeAsync(() -> {
                    String profileJson;

                    try (var redis = CommonLib.getJedisPool().getResource()) {
                        profileJson = redis.get("minetale:profile-cache:" + uuid);
                    }

                    if (profileJson != null) {
                        try {
                            return CommonLib.getGson().fromJson(profileJson, CachedProfile.class);
                        } catch (JsonSyntaxException e) {
                            e.printStackTrace();
                        }
                    }

                    return null;
                });
    }

    public static CompletableFuture<@Nullable CachedProfile> getFromCache(String name) {
        return new CompletableFuture<CachedProfile>()
                .completeAsync(() -> {
                    try {
                        var uuid = UUIDCache.getFromCache(name).get();

                        if (uuid != null) {
                            return getFromCache(uuid).get();
                        }
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }

                    return null;
                });
    }

    public static CompletableFuture<Void> updateCacheAsync(Profile profile) {
        return CompletableFuture.runAsync(() -> updateCache(profile));
    }

    public static void updateCache(Profile profile) {
        try (var redis = CommonLib.getJedisPool().getResource()) {
            var pipeline = redis.pipelined();

            var key = "minetale:profile-cache:" + profile.getUuid();

            pipeline.set(key, CommonLib.getGson().toJson(new CachedProfile(profile, null)));
            pipeline.expire(key, TimeUnit.DAYS.toSeconds(2));
            pipeline.sync();
        }

        profile.save()
                .thenRun(() -> PigeonUtil.broadcast(new ProfileUpdatePayload(profile.getUuid())));
    }

}
