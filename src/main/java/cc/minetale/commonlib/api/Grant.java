package cc.minetale.commonlib.api;

import cc.minetale.commonlib.profile.Profile;
import cc.minetale.commonlib.util.Database;
import cc.minetale.commonlib.util.JSONUtil;
import cc.minetale.commonlib.util.TimeUtil;
import cc.minetale.commonlib.util.StringUtil;
import com.google.gson.annotations.SerializedName;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;
import org.jetbrains.annotations.Nullable;

import java.util.*;

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

    public static final Comparator<Grant> COMPARATOR = Comparator.comparingInt(grant -> grant.getRank().getWeight());
    public static final Grant DEFAULT_GRANT = createGrant("DEFAULT", null, Rank.MEMBER, null, 0L, "Default", Integer.MAX_VALUE);

    public static Grant createGrant(String id, UUID playerId, Rank rank, UUID addedById, long addedAt, String addedReason, long duration) {
        var grant = new Grant();

        grant.setId(id != null ? id : StringUtil.generateId());
        grant.setPlayerId(playerId);
        grant.setRank(rank);
        grant.setAddedById(addedById);
        grant.setAddedAt(addedAt);
        grant.setAddedReason(addedReason);
        grant.setDuration(duration);

        return grant;
    }

    public static List<Grant> getGrants(Profile profile) {
        return getGrants(profile.getUuid());
    }

    public static List<Grant> getGrants(UUID uuid) {
        var grants = new ArrayList<Grant>();

        for (var document : Database.getGrantsCollection().find(Filters.eq("playerId", uuid))) {
            grants.add(JSONUtil.fromDocument(document, Grant.class));
        }

        return grants;
    }

    public static @Nullable Grant getGrant(String id) {
        var document = Database.getGrantsCollection().find(Filters.eq("_id", id)).first();

        if (document != null)
            return JSONUtil.fromDocument(document, Grant.class);

        return null;
    }

    public void delete() {
        Database.getGrantsCollection().deleteOne(Filters.eq("_id", this.id));
    }

    public void save() {
        Database.getGrantsCollection().replaceOne(Filters.eq("_id", this.id), JSONUtil.toDocument(this), new ReplaceOptions().upsert(true));
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
        if (this.isPermanent() || this.duration == 0) {
            return "Permanent";
        } else {
            return TimeUtil.millisToRoundedTime(this.duration);
        }
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

    public void remove(@Nullable UUID removedBy, Long removedAt, String removedReason) {
        this.removed = true;
        this.removedAt = removedAt;
        this.removedById = removedBy;
        this.removedReason = removedReason;

        this.save();
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