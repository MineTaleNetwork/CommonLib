package cc.minetale.commonlib.util;

import cc.minetale.commonlib.CommonLib;
import cc.minetale.commonlib.cache.ProfileCache;
import cc.minetale.commonlib.cache.UUIDCache;
import cc.minetale.commonlib.grant.Grant;
import cc.minetale.commonlib.profile.CachedProfile;
import cc.minetale.commonlib.profile.Profile;
import cc.minetale.commonlib.punishment.Punishment;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.mongodb.client.model.Filters;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class ProfileUtil {

    /**
     * Retrieves a profile with the given parameters, if the
     * profile is not found it will create a new profile.
     * <p>
     * This can return NULL if there was an issue retrieving
     * the profile from the database, this is to ensure we don't
     * overwrite their profile if it actually does exist.
     *
     * @param uuid The players uuid
     * @param name The players name
     * @return The players profile
     */
    public static CompletableFuture<Profile> retrieveProfile(UUID uuid, String name) {
        return new CompletableFuture<Profile>()
                .completeAsync(() -> {
                    try {
                        var response = fromDatabase(uuid).get();

                        if(response != null) {
                            switch (response.response()) {
                                case RETRIEVED -> {
                                    return response.profile();
                                }
                                case NOT_FOUND -> {
                                    var profile = new Profile(uuid, name);

                                    ProfileCache.writeCachedProfile(new CachedProfile(profile));
                                    UUIDCache.updateCache(uuid, name);

                                    return profile;
                                }
                            }
                        }
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }

                    return null;
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

                        var response = fromDatabase(uuid).get();

                        if (response != null && response.response() == Retrieval.Response.RETRIEVED) {
                            return new CachedProfile(response.profile());
                        }
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }

                    return null;
                });
    }

    public static CompletableFuture<List<CachedProfile>> getProfiles(List<UUID> players) {
        return new CompletableFuture<List<CachedProfile>>()
                .completeAsync(() -> {
                    var profiles = new ArrayList<CachedProfile>();

                    var jsonProfiles = Redis.runRedisCommand(jedis -> jedis.mget(
                            players.stream()
                                    .map(uuid -> ProfileCache.getKey(uuid.toString()))
                                    .toArray(String[]::new)
                    ));

                    var uuidQueue = new ArrayList<UUID>();

                    if (jsonProfiles != null) {
                        for (int i = 0; i < jsonProfiles.size(); i++) {
                            var profile = jsonProfiles.get(i);

                            if (profile == null) {
                                uuidQueue.add(players.get(i));
                            } else {
                                try {
                                    profiles.add(CommonLib.getMapper().readValue(profile, CachedProfile.class));
                                } catch (JsonProcessingException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }

                    if (profiles.size() != players.size()) {
                        var documents = Database.getProfilesCollection()
                                .find(Filters.in("_id", uuidQueue.stream()
                                        .map(UUID::toString)
                                        .toList())
                                );


                        for (var document : documents) {
                            try {
                                var response = fromDocument(document).get();

                                if (response != null && response.response() == Retrieval.Response.RETRIEVED) {
                                    profiles.add(new CachedProfile(response.profile()));
                                }
                            } catch (ExecutionException | InterruptedException e) {
                                e.printStackTrace();
                            }
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
                            return cachedProfile.getProfile();
                        }

                        var response = fromDatabase(player).get();

                        if (response != null && response.response() == Retrieval.Response.RETRIEVED) {
                            return response.profile();
                        }
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }

                    return null;
                });
    }

    public static CompletableFuture<Profile> getProfile(String player) {
        return new CompletableFuture<Profile>()
                .completeAsync(() -> {
                    try {
                        var uuid = UUIDCache.getUuid(player).get();

                        var cachedProfile = fromCache(uuid).get();

                        if (cachedProfile != null) {
                            return cachedProfile.getProfile();
                        }

                        var response = fromDatabase(uuid).get();

                        if (response != null && response.response() == Retrieval.Response.RETRIEVED) {
                            var profile = response.profile();

                            ProfileCache.writeCachedProfile(new CachedProfile(profile));

                            return profile;
                        }
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }

                    return null;
                });
    }

    public static CompletableFuture<Retrieval> fromDocument(Document document) {
        return new CompletableFuture<Retrieval>()
                .completeAsync(() -> {
                    if (document == null) {
                        return Retrieval.NOT_FOUND;
                    }

                    try {
                        var profile = CommonLib.getMapper().readValue(document.toJson(), Profile.class);
                        var uuid = profile.getUuid();

                        try {
                            var grants = Grant.getGrants(uuid).get();

                            if(grants != null) {
                                profile.setGrants(grants);
                            } else {
                                return Retrieval.FAILED;
                            }

                            var punishments = Punishment.getPunishments(uuid).get();

                            if(punishments != null) {
                                profile.setPunishments(punishments);
                            } else {
                                return Retrieval.FAILED;
                            }
                        } catch (InterruptedException | ExecutionException e) {
                            e.printStackTrace();
                            return Retrieval.FAILED;
                        }

                        ProfileCache.writeCachedProfile(new CachedProfile(profile));
                        UUIDCache.updateCache(uuid, profile.getName());

                        return new Retrieval(Retrieval.Response.RETRIEVED, profile);
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }

                    return Retrieval.FAILED;
                });
    }

    public static CompletableFuture<Retrieval> fromDatabase(UUID player) {
        return new CompletableFuture<Retrieval>()
                .completeAsync(() -> {
                    var document = Database.getProfilesCollection().find(Filters.eq("_id", player.toString())).first();

                    try {
                        return fromDocument(document).get();
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }

                    return Retrieval.FAILED;
                });
    }

    public static CompletableFuture<CachedProfile> fromCache(UUID uuid) {
        return new CompletableFuture<CachedProfile>()
                .completeAsync(() -> {
                    String profile;

                    try {
                        return ((profile = Redis.runRedisCommand(jedis -> jedis.get(ProfileCache.getKey(uuid.toString())))) != null) ?
                                CommonLib.getMapper().readValue(profile, CachedProfile.class) : null;
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }

                    return null;
                });
    }

    public static CompletableFuture<CachedProfile> fromCache(String name) {
        return new CompletableFuture<CachedProfile>()
                .completeAsync(() -> {
                    try {
                        var uuid = UUIDCache.getUuid(name).get();

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
