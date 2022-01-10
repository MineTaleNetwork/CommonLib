package cc.minetale.commonlib.grant;

import cc.minetale.commonlib.CommonLib;
import cc.minetale.commonlib.profile.Profile;
import cc.minetale.commonlib.util.Database;
import cc.minetale.commonlib.util.TimeUtil;
import cc.minetale.commonlib.util.StringUtil;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Getter @Setter
public class Grant {

    @SerializedName("_id")
    private String id;
    private UUID playerId;
    private Rank rank;
    private UUID addedById;
    private long addedAt;
    private String addedReason;
    private long duration;
    private UUID removedById;
    private long removedAt;
    private String removedReason;
    private boolean removed;

    public static final Type LIST_TYPE_TOKEN = new TypeToken<List<Grant>>(){}.getType();
    public static final Grant DEFAULT_GRANT = new Grant("DEFAULT", null, Rank.MEMBER, null, 0L, "Default", Integer.MAX_VALUE);

    public Grant(String id, UUID playerId, Rank rank, UUID addedById, long addedAt, String addedReason, long duration) {
        this.id = id != null ? id : StringUtil.generateId();
        this.playerId = playerId;
        this.rank = rank;
        this.addedById = addedById;
        this.addedAt = addedAt;
        this.addedReason = addedReason;
        this.duration = duration;
    }

    public static CompletableFuture<@NotNull List<Grant>> getGrants(Profile profile) {
        return getGrants(profile.getUuid());
    }

    public static CompletableFuture<@NotNull List<Grant>> getGrants(UUID uuid) {
        return new CompletableFuture<List<Grant>>()
                .completeAsync(() -> {
                    var grants = new ArrayList<Grant>();

                    for (var document : Database.getGrantsCollection().find(Filters.eq("playerId", uuid.toString()))) {
                        grants.add(CommonLib.getGson().fromJson(document.toJson(), Grant.class));
                    }

                    return grants;
                });
    }

    public static CompletableFuture<@Nullable Grant> getGrant(String id) {
        return new CompletableFuture<Grant>()
                .completeAsync(() -> {
                    var document = Database.getGrantsCollection().find(Filters.eq("_id", id)).first();

                    if (document != null)
                        return CommonLib.getGson().fromJson(document.toJson(), Grant.class);

                    return null;
                });
    }

    public CompletableFuture<UpdateResult> remove(@Nullable UUID removedBy, Long removedAt, String removedReason) {
        this.removed = true;
        this.removedAt = removedAt;
        this.removedById = removedBy;
        this.removedReason = removedReason;

        return this.save();
    }

    public CompletableFuture<DeleteResult> delete() {
        return new CompletableFuture<DeleteResult>()
                .completeAsync(() -> Database.getGrantsCollection()
                        .deleteOne(
                                Filters.eq("_id", this.id)
                        ));
    }

    public CompletableFuture<UpdateResult> save() {
        return new CompletableFuture<UpdateResult>()
                .completeAsync(() -> Database.getGrantsCollection()
                        .replaceOne(
                                Filters.eq("_id", this.id),
                                Document.parse(CommonLib.getGson().toJson(this)),
                                new ReplaceOptions().upsert(true)
                        ));
    }

    public boolean isPermanent() {
        return this.duration == Integer.MAX_VALUE;
    }

    public boolean isActive() {
        return !this.removed && (this.isPermanent() || this.getMillisRemaining() > 0L);
    }

    public boolean isDefault() {
        return this.id.equals("DEFAULT");
    }

    public long getMillisRemaining() {
        return (this.addedAt + this.duration) - System.currentTimeMillis();
    }

    public boolean hasExpired() {
        return (!this.isPermanent()) && (System.currentTimeMillis() >= this.addedAt + this.duration);
    }

    public String getDurationText() {
        return (this.isPermanent() || this.duration == 0) ? "Permanent" : TimeUtil.millisToRoundedTime(this.duration);
    }

    public String getTimeRemaining() {
        if (this.removed) {
            return "Removed";
        }

        if (this.isPermanent()) {
            return "Permanent";
        }

        if (this.hasExpired()) {
            return "Expired";
        }

        return TimeUtil.millisToRoundedTime((this.addedAt + this.duration) - System.currentTimeMillis());
    }

    @Override
    public boolean equals(Object object) {
        return this == object || object instanceof Grant other && other.getId().equalsIgnoreCase(this.id);
    }

    @Override
    public int hashCode() {
        return this.id.hashCode();
    }

}