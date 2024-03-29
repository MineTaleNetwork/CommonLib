package cc.minetale.commonlib.util;

import cc.minetale.commonlib.cache.ProfileCache;
import cc.minetale.commonlib.cache.UUIDCache;
import cc.minetale.commonlib.grant.Grant;
import cc.minetale.commonlib.profile.CachedProfile;
import cc.minetale.commonlib.profile.Profile;
import cc.minetale.commonlib.profile.ProfileRetrieval;
import cc.minetale.commonlib.punishment.Punishment;
import com.mongodb.client.model.Filters;
import lombok.experimental.UtilityClass;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@UtilityClass
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

                        UUIDCache.pushCache(uuid, name);

                        if(response != null) {
                            switch (response.response()) {
                                case RETRIEVED -> {
                                    return response.profile();
                                }
                                case NOT_FOUND -> {
                                    var profile = new Profile(uuid, name);

                                    ProfileCache.pushCache(profile);

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

                        if (response.response() == ProfileRetrieval.Response.RETRIEVED) {
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

                    var jsonProfiles = Redis.runRedisCommand(jedis -> jedis.hmget(ProfileCache.getKey(),
                            players.stream()
                                    .map(UUID::toString)
                                    .toArray(String[]::new)
                    ));

                    var uuidQueue = new ArrayList<UUID>();

                    if (jsonProfiles != null) {
                        for (int i = 0; i < jsonProfiles.size(); i++) {
                            var profile = jsonProfiles.get(i);

                            if (profile == null) {
                                uuidQueue.add(players.get(i));
                            } else {
                                var cachedProfile = JsonUtil.readFromJson(profile, CachedProfile.class);

                                if(cachedProfile != null) {
                                    profiles.add(cachedProfile);
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

                                if (response != null && response.response() == ProfileRetrieval.Response.RETRIEVED) {
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

    public static CompletableFuture<Profile> getProfile(UUID uuid) {
        return new CompletableFuture<Profile>()
                .completeAsync(() -> {
                    try {
                        var cachedProfile = fromCache(uuid).get();

                        if (cachedProfile != null) {
                            return cachedProfile.getProfile();
                        }

                        var response = fromDatabase(uuid).get();

                        if (response != null && response.response() == ProfileRetrieval.Response.RETRIEVED) {
                            return response.profile();
                        }
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }

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

                        var response = fromDatabase(uuid).get();

                        if (response != null && response.response() == ProfileRetrieval.Response.RETRIEVED) {
                            var profile = response.profile();

                            ProfileCache.pushCache(profile);

                            return profile;
                        }
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }

                    return null;
                });
    }

    public static CompletableFuture<ProfileRetrieval> fromDocument(Document document) {
        return new CompletableFuture<ProfileRetrieval>()
                .completeAsync(() -> {
                    if (document == null) {
                        return ProfileRetrieval.NOT_FOUND;
                    }

                    var profile = BsonUtil.readFromBson(document, Profile.class);

                    if(profile != null) {

                        var uuid = profile.getUuid();

                        try {
                            var grants = Grant.getGrants(uuid).get();

                            if (grants != null) {
                                profile.setGrants(grants);
                            } else {
                                return ProfileRetrieval.FAILED;
                            }

                            var punishments = Punishment.getPunishments(uuid).get();

                            if (punishments != null) {
                                profile.setPunishments(punishments);
                            } else {
                                return ProfileRetrieval.FAILED;
                            }
                        } catch (InterruptedException | ExecutionException e) {
                            e.printStackTrace();
                            return ProfileRetrieval.FAILED;
                        }

                        ProfileCache.pushCache(profile);

                        return new ProfileRetrieval(ProfileRetrieval.Response.RETRIEVED, profile);
                    }

                    return ProfileRetrieval.FAILED;
                });
    }

    public static CompletableFuture<ProfileRetrieval> fromDatabase(UUID uuid) {
        return new CompletableFuture<ProfileRetrieval>()
                .completeAsync(() -> {
                    var document = Database.getProfilesCollection().find(Filters.eq("_id", uuid.toString())).first();

                    try {
                        return fromDocument(document).get();
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }

                    return ProfileRetrieval.FAILED;
                });
    }

    public static CompletableFuture<CachedProfile> fromCache(UUID uuid) {
        return new CompletableFuture<CachedProfile>()
                .completeAsync(() -> {
                    try {
                        return ProfileCache.getCache(uuid).get();
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }

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
