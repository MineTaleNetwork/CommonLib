package cc.minetale.commonlib.profile;

import cc.minetale.commonlib.CommonLib;
import cc.minetale.commonlib.api.Punishment;
import cc.minetale.commonlib.cache.GrantCache;
import cc.minetale.commonlib.cache.PunishmentCache;
import cc.minetale.commonlib.pigeon.payloads.profile.ProfileUpdatePayload;
import cc.minetale.commonlib.redis.RedisUtil;
import cc.minetale.commonlib.util.Database;
import cc.minetale.commonlib.util.JSONUtil;
import cc.minetale.commonlib.util.PigeonUtil;
import cc.minetale.commonlib.cache.UUIDCache;
import com.google.gson.JsonSyntaxException;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class ProfileUtil {

    public static CompletableFuture<@Nullable Profile> getProfile(UUID uuid) {
        return new CompletableFuture<Profile>()
                .completeAsync(() -> {
                    try {
                        var cachedProfile = getFromCache(uuid).get();

                        if (cachedProfile != null) {
                            return cachedProfile;
                        }

                        var databaseProfile = getFromDatabase(uuid).get();

                        if (databaseProfile != null) {
                            updateCache(databaseProfile);
                            UUIDCache.updateCache(databaseProfile.getName(), databaseProfile.getUuid());

                            return databaseProfile;
                        }
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }

                    return null;
                });
    }

    public static CompletableFuture<@Nullable Profile> getProfile(String name) {
        return new CompletableFuture<Profile>()
                .completeAsync(() -> {
                    try {
                        var cachedProfile = getFromCache(name).get();

                        if (cachedProfile != null) {
                            return cachedProfile;
                        }

                        var databaseProfile = getFromDatabase(name).get();

                        if (databaseProfile != null) {
                            updateCache(databaseProfile);
                            UUIDCache.updateCache(databaseProfile.getName(), databaseProfile.getUuid());

                            return databaseProfile;
                        }
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }

                    return null;
                });
    }

    public static CompletableFuture<@Nullable Profile> getFromDatabase(UUID uuid) {
        return new CompletableFuture<Profile>()
                .completeAsync(() -> {
                    var collection = Database.getProfilesCollection();
                    var document = collection.find(Filters.eq("_id", uuid.toString())).first();

                    if(document == null) { return null; }

                    return JSONUtil.fromDocument(document, Profile.class);
                });
    }

    public static CompletableFuture<@Nullable Profile> getFromDatabase(String name) {
        return new CompletableFuture<Profile>()
                .completeAsync(() -> {
                    var collection = Database.getProfilesCollection();
                    var document = collection.find(Filters.eq("search", name.toUpperCase())).first();

                    if(document == null) { return null; }

                    return JSONUtil.fromDocument(document, Profile.class);
                });
    }

    public static CompletableFuture<@Nullable Profile> getFromCache(UUID uuid) {
        return new CompletableFuture<Profile>()
                .completeAsync(() -> {
                    var profileJson = RedisUtil.runRedisCommand(redis -> redis.get("minetale:profile-cache:" + uuid));

                    if(profileJson != null) {
                        try {
                            return JSONUtil.fromJson(profileJson, Profile.class);
                        } catch (JsonSyntaxException e) {
                            e.printStackTrace();
                        }
                    }

                    return null;
                });
    }

    public static CompletableFuture<@Nullable Profile> getFromCache(String name) {
        return new CompletableFuture<Profile>()
                .completeAsync(() -> {
                    try {
                        var uuid = UUIDCache.getFromCache(name).get();

                        if(uuid != null) {
                            return getFromCache(uuid).get();
                        }
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }

                    return null;
                });
    }

    public static void saveProfile(@NotNull Profile profile) {
        var collection = Database.getProfilesCollection();

        collection.replaceOne(
                Filters.eq(profile.getUuid().toString()),
                JSONUtil.toDocument(profile),
                new ReplaceOptions().upsert(true)
        );
    }

    public static void updateCache(Profile profile) {
        new Thread(() -> {
            GrantCache.updateCache(profile);
            PunishmentCache.updateCache(profile);

            RedisUtil.runRedisCommand(redis -> {
                var pipeline = redis.pipelined();

                var key = "minetale:profile-cache:" + profile.getUuid();

                pipeline.set(key, CommonLib.getGson().toJson(profile));
                pipeline.expire(key, 7200);
                pipeline.sync();

                return null;
            });

            saveProfile(profile);

            PigeonUtil.broadcast(new ProfileUpdatePayload(profile.getUuid()));
        }).start();
    }

}
