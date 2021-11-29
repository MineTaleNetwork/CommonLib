package cc.minetale.commonlib.api;

import cc.minetale.commonlib.util.Database;
import cc.minetale.commonlib.util.TimeUtil;
import cc.minetale.commonlib.util.StringUtil;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.Objects;
import java.util.UUID;

@Getter @Setter @Builder @AllArgsConstructor
public class Grant {

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

    public static Comparator<Grant> COMPARATOR = Comparator.comparingInt(grant -> grant.getRank().getWeight());

    public Grant(UUID playerId, Rank rank, UUID addedById, long addedAt, String addedReason, long duration) {
        this.id = StringUtil.generateId();
        this.playerId = playerId;
        this.rank = rank;
        this.addedById = addedById;
        this.addedAt = addedAt;
        this.addedReason = addedReason;
        this.duration = duration;
    }

    public Grant(UUID playerId) {
        this.id = "DEFAULT";
        this.playerId = playerId;
        this.rank = Rank.DEFAULT;
        this.addedById = null;
        this.addedAt = 0;
        this.addedReason = "Default";
        this.duration = Integer.MAX_VALUE;
    }

    public static @Nullable Grant fromDocument(Document document) {
        if (document != null) {
            var grant = Grant.builder()
                    .id(document.getString("_id"))
                    .playerId(UUID.fromString(document.getString("playerId")))
                    .rank(Rank.valueOf(document.getString("rank")))
                    .addedById(document.getString("addedById") != null ? UUID.fromString(document.getString("addedById")) : null)
                    .addedAt(document.getLong("addedAt"))
                    .addedReason(document.getString("addedReason"))
                    .duration(document.getLong("duration"))
                    .removed(document.getBoolean("removed"));

            if(grant.removed) {
                grant.removedAt(document.getLong("removedAt"))
                        .removedById(document.getString("removedById") != null ? UUID.fromString(document.getString("removedById")) : null)
                        .removedReason(document.getString("removedReason"));
            }

            return grant.build();
        }

        return null;
    }

    public static @Nullable Grant getGrant(String id) {
        var document = Database.getDatabase().getGrantsCollection().find(Filters.eq("_id", id)).first();

        if (document != null)
            return fromDocument(document);

        return null;
    }

    public static Grant getDefaultGrant(UUID playerId) {
        return new Grant(playerId);
    }

    public void delete() {
        Database.getDatabase().getGrantsCollection().deleteOne(Filters.eq("_id", this.id));
    }

    public void save() {
        Database.getDatabase().getGrantsCollection().replaceOne(Filters.eq("_id", this.id), toDocument(), new ReplaceOptions().upsert(true));
    }

    public Document toDocument() {
        var document = new Document();

        document.put("_id", this.id);
        document.put("playerId", this.playerId.toString());
        document.put("rank", this.rank.name());

        document.put("addedById", this.addedById != null ? this.addedById.toString() : null);
        document.put("addedAt", this.addedAt);
        document.put("addedReason", this.addedReason);
        document.put("duration", this.duration);

        document.put("removed", this.removed);
        document.put("removedAt", Objects.requireNonNullElse(this.removedAt, 0L));
        document.put("removedById", this.removedById != null ? this.removedById.toString() : null);
        document.put("removedReason", this.removedReason != null ? this.removedReason : null);

        return document;
    }

    /**
     * Returns if the Punishment is permanent or not.
     */
    public boolean isPermanent() {
        return this.duration == Integer.MAX_VALUE;
    }

    /**
     * Returns if the Grant is active or not.
     */
    public boolean isActive() {
        return !this.removed && (this.isPermanent() || this.getMillisRemaining() < 0L);
    }

    /**
     * Returns if the Grant is default or not.
     */
    public boolean isDefault() {
        return this.id.equals("DEFAULT");
    }

    /**
     * Returns the remaining amount of milliseconds of the Grant.
     */
    public long getMillisRemaining() {
        return (this.addedAt + this.duration) - System.currentTimeMillis();
    }

    /**
     * Returns if the Grant has expired or not.
     */
    public boolean hasExpired() {
        return (!this.isPermanent()) && (System.currentTimeMillis() >= this.addedAt + this.duration);
    }

    /**
     * Returns the Duration String.
     */
    public String getDurationText() {
        if (this.isPermanent() || this.duration == 0) {
            return "Permanent";
        } else {
            return TimeUtil.millisToRoundedTime(this.duration);
        }
    }

    /**
     * Returns the Remaining String.
     */
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

    /**
     * Removes a Grant.
     */
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