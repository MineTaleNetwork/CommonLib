package cc.minetale.commonlib.punishment;

import cc.minetale.commonlib.util.TimeUtil;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class PunishmentAPI {

    private final Punishment punishment;

    public PunishmentAPI(Punishment punishment) {
        this.punishment = punishment;
    }

    /**
     * Returns if the Punishment is permanent or not.
     */
    public boolean isPermanent() {
        return this.punishment.getType() == Punishment.Type.BLACKLIST || this.punishment.getDuration() == Integer.MAX_VALUE;
    }

    /**
     * Returns if the Punishment is active or not.
     */
    public boolean isActive() {
        return !this.punishment.isRemoved() && (this.isPermanent() || this.getMillisRemaining() < 0L);
    }

    /**
     * Returns the remaining amount of milliseconds of the Punishment.
     */
    public long getMillisRemaining() {
        return (this.punishment.getAddedAt() + this.punishment.getDuration()) - System.currentTimeMillis();
    }

    /**
     * Returns if the Punishment has expired or not.
     */
    public boolean hasExpired() {
        return (!this.isPermanent()) && (System.currentTimeMillis() >= this.punishment.getAddedAt() + this.punishment.getDuration());
    }

    /**
     * Returns the Duration String.
     */
    public String getDurationText() {
        if (this.isPermanent() || this.punishment.getDuration() == 0) {
            return "Permanent";
        } else {
            return TimeUtil.millisToRoundedTime(this.punishment.getDuration());
        }
    }

    /**
     * Returns the Remaining String.
     */
    public String getTimeRemaining() {
        if (this.punishment.isRemoved()) {
            return "Removed";
        }

        if (this.isPermanent()) {
            return "Permanent";
        }

        if (hasExpired()) {
            return "Expired";
        }

        return TimeUtil.millisToRoundedTime((this.punishment.getAddedAt() + this.punishment.getDuration()) - System.currentTimeMillis());
    }

    /**
     * Returns the Context String.
     */
    public String getContext() {
        if (!(this.punishment.getType() == Punishment.Type.BAN || this.punishment.getType() == Punishment.Type.MUTE)) {
            return this.punishment.isRemoved() ? this.punishment.getType().getUndoContext() : this.punishment.getType().getContext();
        }

        if (this.isPermanent()) {
            return (this.punishment.isRemoved() ? this.punishment.getType().getUndoContext() : "permanently " + this.punishment.getType().getContext());
        } else {
            return (this.punishment.isRemoved() ? this.punishment.getType().getUndoContext() : "temporarily " + this.punishment.getType().getContext());
        }
    }

    /**
     * Removes a Punishment.
     */
    public void remove(@Nullable UUID removedBy, Long removedAt, String removedReason) {
        this.punishment.setRemovedByUUID(removedBy);
        this.punishment.setRemoved(true);
        this.punishment.setRemovedAt(removedAt);
        this.punishment.setRemovedReason(removedReason);
        this.punishment.save();
    }

}
