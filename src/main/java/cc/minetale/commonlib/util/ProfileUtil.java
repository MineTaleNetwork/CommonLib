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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class ProfileUtil {

    public static CompletableFuture<Profile> getOrCreateDatabaseProfile(UUID uuid, String name) {
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

                    return new Profile(uuid, name);
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

    public static CompletableFuture<List<CachedProfile>> getProfiles(List<UUID> players) {
        return new CompletableFuture<List<CachedProfile>>()
                .completeAsync(() -> {
                    var profiles = new ArrayList<CachedProfile>();

                    List<String> jsonArray;

                    try (var redis = CommonLib.getJedisPool().getResource()) {
                        jsonArray = redis.mget(players.stream()
                                .map(uuid -> ProfileCache.getKey(uuid.toString()))
                                .toArray(String[]::new));
                    }

                    var index = 0;
                    var iterator = jsonArray.iterator();
                    var nonCachedUuids = new ArrayList<UUID>();

                    while (iterator.hasNext()) {
                        if (iterator.next() == null) {
                            nonCachedUuids.add(players.get(index));
                            iterator.remove();
                        }

                        index++;
                    }

                    var documents = Database.getProfilesCollection()
                            .find(Filters.in("_id", nonCachedUuids.stream().map(UUID::toString).toList()));

                    var keyValues = new ArrayList<String>();

                    try (var pipeline = CommonLib.getJedisPool().getResource().pipelined()) {
                        for (var document : documents) {
                            var profile = CommonLib.getMapper().readValue(document.toJson(), Profile.class);

                            keyValues.add(ProfileCache.getKey(profile.getUuid().toString()));
                            keyValues.add(CommonLib.getMapper().writeValueAsString(new CachedProfile(profile)));

                            keyValues.add(UUIDCache.getKey(UUIDCache.getKey(profile.getName())));
                            keyValues.add(profile.getUuid().toString());

                            profiles.add(new CachedProfile(profile));
                        }

                        var keys = keyValues.toArray(new String[0]);

                        if(keys.length % 4 == 0) {
                            pipeline.mset(keys);

                            for(int i = 0; i < keys.length; i++) {
                                if(i % 2 == 1) continue;

                                if(i % 4 == 2) {
                                    pipeline.expire(keys[i], TimeUnit.DAYS.toSeconds(4));
                                } else {
                                    pipeline.expire(keys[i], TimeUnit.DAYS.toSeconds(2));
                                }
                            }

                            pipeline.sync();
                        } else {
                            // TODO -> Something fucked up really bad
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

    public static CompletableFuture<Profile> getProfile(UUID player) {
        return new CompletableFuture<Profile>()
                .completeAsync(() -> {
                    try {
                        var cachedProfile = fromCache(player).get();

                        if (cachedProfile != null) {
                            var profile = cachedProfile.getProfile();

                            UUIDCache.updateCache(profile.getName(), profile.getUuid());

                            return profile;
                        }

                        var databaseProfile = fromDatabase(player).get();

                        if (databaseProfile != null) {
                            ProfileCache.updateProfile(databaseProfile);
                            UUIDCache.updateCache(databaseProfile.getName(), databaseProfile.getUuid());

                            return databaseProfile;
                        }
                    } catch (InterruptedException | ExecutionException ignored) {}

                    return null;
                });
    }

    public static CompletableFuture<Profile> getProfile(String player) {
        return new CompletableFuture<Profile>()
                .completeAsync(() -> {
                    try {
                        var cachedProfile = fromCache(player).get();

                        if (cachedProfile != null) {
                            var profile = cachedProfile.getProfile();

                            UUIDCache.updateCache(profile.getName(), profile.getUuid());

                            return profile;
                        }

                        var databaseProfile = fromDatabase(player).get();

                        if (databaseProfile != null) {
                            ProfileCache.updateProfile(databaseProfile);
                            UUIDCache.updateCache(databaseProfile.getName(), databaseProfile.getUuid());

                            return databaseProfile;
                        }
                    } catch (InterruptedException | ExecutionException ignored) {}

                    return null;
                });
    }

    public static CompletableFuture<Profile> fromDatabase(UUID player) {
        return new CompletableFuture<Profile>()
                .completeAsync(() -> {
                    var collection = Database.getProfilesCollection();
                    var document = collection.find(Filters.eq("_id", player.toString())).first();

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

    public static CompletableFuture<Profile> fromDatabase(String name) {
        return new CompletableFuture<Profile>()
                .completeAsync(() -> {
                    var collection = Database.getProfilesCollection();
                    var document = collection.find(Filters.eq("search", name.toUpperCase())).first();

                    if (document == null) {
                        return null;
                    }

                    try {
                        return CommonLib.getMapper().readValue(document.toJson(), Profile.class);
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }

                    return null;
                });
    }

    public static CompletableFuture<CachedProfile> fromCache(UUID uuid) {
        return new CompletableFuture<CachedProfile>()
                .completeAsync(() -> {
                    String profileJson;

                    try (var redis = CommonLib.getJedisPool().getResource()) {
                        profileJson = redis.get(ProfileCache.getKey(uuid.toString()));
                    }

                    if (profileJson != null) {
                        try {
                            return CommonLib.getMapper().readValue(profileJson, CachedProfile.class);
                        } catch (JsonSyntaxException | JsonProcessingException e) {
                            e.printStackTrace();
                        }
                    }

                    return null;
                });
    }

    public static CompletableFuture<CachedProfile> fromCache(String name) {
        return new CompletableFuture<CachedProfile>()
                .completeAsync(() -> {
                    try {
                        var uuid = UUIDCache.getFromCache(name).get();

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
