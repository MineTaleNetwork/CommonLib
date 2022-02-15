package cc.minetale.commonlib.util;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;


@Getter
@Setter
@EqualsAndHashCode(of = { "id" })
public abstract class ProvidableObject {

    @JsonProperty("_id")
    private String id;
    private UUID playerId;
    private UUID addedById;
    private long addedAt;
    private String addedReason;
    private long duration;
    private UUID removedById;
    private long removedAt;
    private String removedReason;

    public ProvidableObject(String id, UUID playerId, UUID addedById, long addedAt, String addedReason, long duration) {
        this.id = id;
        this.playerId = playerId;
        this.addedById = addedById;
        this.addedAt = addedAt;
        this.addedReason = addedReason;
        this.duration = duration;
    }

    /**
     * Default constructor used for Jackson.
     */
    public ProvidableObject() {}

    public boolean isRemoved() {
        return removedAt != 0L;
    }

    public boolean isPermanent() {
        return duration == Integer.MAX_VALUE;
    }

    public boolean isActive() {
        return !isRemoved() && (isPermanent() || !hasExpired());
    }

    public long getMillisRemaining() {
        return (addedAt + duration) - System.currentTimeMillis();
    }

    public boolean hasExpired() {
        return (!isPermanent()) && (System.currentTimeMillis() >= addedAt + duration);
    }

    public String getDurationText() {
        return (isPermanent() || duration == 0) ? "Permanent" : TimeUtil.millisToRoundedTime(duration);
    }

    public String getTimeRemaining() {
        if (isRemoved()) {
            return "Removed";
        }

        if (isPermanent()) {
            return "Permanent";
        }

        if (hasExpired()) {
            return "Expired";
        }

        return TimeUtil.millisToRoundedTime((addedAt + duration) - System.currentTimeMillis());
    }

}