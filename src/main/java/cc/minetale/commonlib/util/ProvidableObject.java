package cc.minetale.commonlib.util;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@EqualsAndHashCode(of = { "id" })
@RequiredArgsConstructor
public abstract class ProvidableObject {

    @JsonProperty("_id")
    private final String id;
    private final UUID playerId;
    private final UUID addedById;
    private final long addedAt;
    private final String addedReason;
    private final long duration;
    private UUID removedById;
    private long removedAt;
    private String removedReason;

    public boolean isRemoved() {
        return removedAt != 0L;
    }

    public boolean isPermanent() {
        return duration == -1;
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
