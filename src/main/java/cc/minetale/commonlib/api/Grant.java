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

@Getter @Setter
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

    @Override
    public String toString() {
        return "Grant{" +
                "id='" + id + '\'' +
                ", playerId=" + playerId +
                ", rank=" + rank +
                ", addedById=" + addedById +
                ", addedAt=" + addedAt +
                ", addedReason='" + addedReason + '\'' +
                ", duration=" + duration +
                ", removedById=" + removedById +
                ", removedAt=" + removedAt +
                ", removedReason='" + removedReason + '\'' +
                ", removed=" + removed +
                '}';
    }

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

    public static @Nullable Grant fromDocument(Document document) {
        if (document != null) {
            var grant = createGrant(
                    document.getString("_id"),
                    UUID.fromString(document.getString("playerId")),
                    Rank.valueOf(document.getString("rank")),
                    document.getString("addedById") != null ? UUID.fromString(document.getString("addedById")) : null,
                    document.getLong("addedAt"),
                    document.getString("addedReason"),
                    document.getLong("duration")
            );

            grant.setRemoved(document.getBoolean("removed"));

            if(grant.isRemoved()) {
                grant.setRemovedAt(document.getLong("removedAt"));
                grant.setRemovedById(document.getString("removedById") != null ? UUID.fromString(document.getString("removedById")) : null);
                grant.setRemovedReason(document.getString("removedReason"));
            }

            return grant;
        }

        return null;
    }

    public static @Nullable Grant getGrant(String id) {
        var document = Database.getGrantsCollection().find(Filters.eq("_id", id)).first();

        if (document != null)
            return fromDocument(document);

        return null;
    }

    public void delete() {
        Database.getGrantsCollection().deleteOne(Filters.eq("_id", this.id));
    }

    public void save() {
        Database.getGrantsCollection().replaceOne(Filters.eq("_id", this.id), toDocument(), new ReplaceOptions().upsert(true));
    }

    public Document toDocument() {
        return new Document()
                .append("_id", this.id)
                .append("playerId", this.playerId.toString())
                .append("rank", this.rank.name())
                .append("addedById", this.addedById != null ? this.addedById.toString() : null)
                .append("addedAt", this.addedAt)
                .append("addedReason", this.addedReason)
                .append("duration", this.duration)
                .append("removed", this.removed)
                .append("removedAt", this.removedAt)
                .append("removedById", this.removedById != null ? this.removedById.toString() : null)
                .append("removedReason", this.removedReason != null ? this.removedReason : null);
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