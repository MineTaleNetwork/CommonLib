package cc.minetale.commonlib.modules.grant;

import cc.minetale.commonlib.modules.rank.Rank;
import cc.minetale.commonlib.util.TimeUtil;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.UUID;

public class GrantAPI {

    private final Grant grant;

    public GrantAPI(Grant grant) {
        this.grant = grant;
    }

    public static Comparator<Grant> COMPARATOR = (grant1, grant2) -> {
        Rank rank1 = Rank.getRank(grant1.getRankUUID(), true);
        Rank rank2 = Rank.getRank(grant2.getRankUUID(), true);

        if(rank1 != null && rank2 != null) {
            return rank1.getWeight() - rank2.getWeight();
        }

        return 0;
    };

    /**
     * Returns if the Punishment is permanent or not.
     */
    public boolean isPermanent() {
        return this.grant.getDuration() == Integer.MAX_VALUE;
    }

    /**
     * Returns if the Grant is active or not.
     */
    public boolean isActive() {
        return !this.grant.isRemoved() && (this.isPermanent() || this.getMillisRemaining() < 0L);
    }

    /**
     * Returns the remaining amount of milliseconds of the Grant.
     */
    public long getMillisRemaining() {
        return (this.grant.getAddedAt() + this.grant.getDuration()) - System.currentTimeMillis();
    }

    /**
     * Returns if the Grant has expired or not.
     */
    public boolean hasExpired() {
        return (!isPermanent()) && (System.currentTimeMillis() >= this.grant.getAddedAt() + this.grant.getDuration());
    }

    /**
     * Returns the Duration String.
     */
    public String getDurationText() {
        if (this.isPermanent() || this.grant.getDuration() == 0) {
            return "Permanent";
        } else {
            return TimeUtil.millisToRoundedTime(this.grant.getDuration());
        }
    }

    /**
     * Returns the Remaining String.
     */
    public String getTimeRemaining() {
        if (this.grant.isRemoved()) {
            return "Removed";
        }

        if (this.isPermanent()) {
            return "Permanent";
        }

        if (hasExpired()) {
            return "Expired";
        }

        return TimeUtil.millisToRoundedTime((this.grant.getAddedAt() + this.grant.getDuration()) - System.currentTimeMillis());
    }

    /**
     * Removes a Grant.
     */
    public void remove(@Nullable UUID removedBy, Long removedAt, String removedReason) {
        this.grant.setRemovedByUUID(removedBy);
        this.grant.setRemovedAt(removedAt);
        this.grant.setRemovedReason(removedReason);
        this.grant.setRemoved(true);
        this.grant.save();
    }

    /**
     * Returns the Rank from the Grant.
     */
    public Rank getRank() {
        return Rank.getRank(this.grant.getRankUUID(), true);
    }

}
