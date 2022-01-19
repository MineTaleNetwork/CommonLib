package cc.minetale.commonlib.util;

import cc.minetale.commonlib.CommonLib;
import cc.minetale.commonlib.cache.ProfileCache;
import cc.minetale.commonlib.cache.UUIDCache;
import cc.minetale.commonlib.grant.Grant;
import cc.minetale.commonlib.profile.CachedProfile;
import cc.minetale.commonlib.profile.Profile;
import cc.minetale.commonlib.punishment.Punishment;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.JsonSyntaxException;
import com.mongodb.client.model.Filters;
import lombok.SneakyThrows;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class ProfileUtil {

    @SneakyThrows
    public static CompletableFuture<Profile> retrieveOrCreateProfile(UUID uuid, String username) {
        return new CompletableFuture<Profile>()
                .completeAsync(() -> {
                    try {
                        var databaseProfile = fromDatabase(uuid).get();

                        if(databaseProfile != null) {
                            return databaseProfile;
                        }
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                        return null;
                    }

                    return new Profile(uuid, username);
                });
    }

    public static CompletableFuture<CachedProfile> getCachedProfile(UUID uuid) {
        return new CompletableFuture<CachedProfile>()
                .completeAsync(() -> {
                    try {
                        var cachedProfile = fromCache(uuid).get();

                        if (cachedProfile != null) {
                            return cachedProfile;
                        }

                        var databaseProfile = fromDatabase(uuid).get();

                        if (databaseProfile != null) {
                            return new CachedProfile(databaseProfile);
                        }
                    } catch (InterruptedException | ExecutionException ignored) {}

                    return null;
                });
    }

    public static CompletableFuture<List<CachedProfile>> getProfiles(List<UUID> uuids) {
        return new CompletableFuture<List<CachedProfile>>()
                .completeAsync(() -> {
                    var profiles = new ArrayList<CachedProfile>();

                    List<String> jsonArray;

                    try (var redis = CommonLib.getJedisPool().getResource()) {
                        jsonArray = redis.mget(uuids.stream()
                                .map(uuid -> ProfileCache.getKey(uuid.toString()))
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
                            .find(Filters.in("_id", nonCachedUuids.stream().map(UUID::toString).toList()));

                    try {
                        for (var document : documents) {
                            var profile = CommonLib.getMapper().readValue(document.toJson(), Profile.class);

                            ProfileCache.createCachedProfile(profile);
                            UUIDCache.updateCache(profile.getUsername(), profile.getUuid());
                            profiles.add(new CachedProfile(profile));
                        }
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }


                    for (var profileJson : jsonArray) {
                        try {
                            profiles.add(CommonLib.getMapper().readValue(profileJson, CachedProfile.class));
                        } catch (JsonProcessingException e) {
                            e.printStackTrace();
                        }
                    }

                    return profiles;
                });
    }

    public static CompletableFuture<Profile> getProfile(UUID uuid) {
        return new CompletableFuture<Profile>()
                .completeAsync(() -> {
                    try {
                        var cachedProfile = fromCache(uuid).get();

                        if (cachedProfile != null) {
                            var profile = cachedProfile.getProfile();

                            UUIDCache.updateCache(profile.getUsername(), profile.getUuid());

                            return profile;
                        }

                        var databaseProfile = fromDatabase(uuid).get();

                        if (databaseProfile != null) {
                            ProfileCache.updateProfile(databaseProfile);
                            UUIDCache.updateCache(databaseProfile.getUsername(), databaseProfile.getUuid());

                            return databaseProfile;
                        }
                    } catch (InterruptedException | ExecutionException ignored) {}

                    return null;
                });
    }

    public static CompletableFuture<Profile> getProfile(String username) {
        return new CompletableFuture<Profile>()
                .completeAsync(() -> {
                    try {
                        var uuid = UUIDCache.getUuid(username).get();
                        var cachedProfile = fromCache(uuid).get();

                        if (cachedProfile != null) {
                            return cachedProfile.getProfile();
                        }

                        var databaseProfile = fromDatabase(uuid).get();

                        if (databaseProfile != null) {
                            ProfileCache.createCachedProfile(databaseProfile);
                            return databaseProfile;
                        }
                    } catch (InterruptedException | ExecutionException ignored) {}

                    return null;
                });
    }

    public static CompletableFuture<Profile> fromDatabase(UUID uuid) {
        return new CompletableFuture<Profile>()
                .completeAsync(() -> {
                    var collection = Database.getProfilesCollection();
                    var document = collection.find(Filters.eq("_id", uuid.toString())).first();

                    if (document == null) {
                        return null;
                    }

                    try {
                        var profile = CommonLib.getMapper().readValue(document.toJson(), Profile.class);

                        try {
                            var grants = Grant.getGrants(profile.getUuid()).get(3, TimeUnit.SECONDS);
                            profile.setGrants(grants);

                            var punishments = Punishment.getPunishments(profile.getUuid()).get(3, TimeUnit.SECONDS);
                            profile.setPunishments(punishments);
                        } catch (InterruptedException | ExecutionException | TimeoutException ignored) {
                            return null;
                        }

                        return profile;
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }

                    return null;
                });
    }

    public static CompletableFuture<CachedProfile> fromCache(UUID uuid) {
        return new CompletableFuture<CachedProfile>()
                .completeAsync(() -> {
                    String profile;

                    try {
                        return ((profile = Redis.runRedisCommand(jedis -> jedis.get(ProfileCache.getKey(uuid.toString())))) != null) ?
                                CommonLib.getMapper().readValue(profile, CachedProfile.class) : null;
                    } catch (JsonProcessingException ignored) {}

                    return null;
                });
    }

    public static CompletableFuture<CachedProfile> fromCache(String username) {
        return new CompletableFuture<CachedProfile>()
                .completeAsync(() -> {
                    try {
                        var uuid = UUIDCache.getUuid(username).get();

                        if (uuid != null) {
                            return fromCache(uuid).get();
                        }
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }

                    return null;
                });
    }

}
