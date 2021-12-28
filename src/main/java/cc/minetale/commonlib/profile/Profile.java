package cc.minetale.commonlib.profile;

import cc.minetale.commonlib.api.Grant;
import cc.minetale.commonlib.api.Punishment;
import cc.minetale.commonlib.cache.GrantCache;
import cc.minetale.commonlib.pigeon.payloads.grant.GrantAddPayload;
import cc.minetale.commonlib.pigeon.payloads.grant.GrantRemovePayload;
import cc.minetale.commonlib.pigeon.payloads.punishment.PunishmentAddPayload;
import cc.minetale.commonlib.pigeon.payloads.punishment.PunishmentRemovePayload;
import cc.minetale.commonlib.util.PigeonUtil;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter @Setter
public class Profile {

    @SerializedName("_id")
    private UUID uuid;
    private String name;
    private String search;
    private String currentAddress;
    private String discord;
    private int gold;
    private long firstSeen;
    private long lastSeen;
    private long experience;
    private List<UUID> ignored = new ArrayList<>();
    private List<UUID> friends = new ArrayList<>();
    private Options optionsProfile = new Options();
    private Staff staffProfile = new Staff();

    private transient Grant grant = Grant.DEFAULT_GRANT;
    private transient List<Punishment> punishments = new ArrayList<>();
    private transient List<Grant> grants = new ArrayList<>();

    public boolean isIgnoring(Profile profile) {
        return this.ignored.contains(profile.getUuid());
    }

    public int getPunishmentCountByType(Punishment.Type type) {
        return (int) this.punishments.stream().filter(punishment -> punishment.getType() == type).count();
    }

    public Punishment getActivePunishmentByType(Punishment.Type type) {
        for (var punishment : this.punishments)
            if (punishment.getType() == type && punishment.isActive())
                return punishment;

        return null;
    }

    public Punishment getActiveBan() {
        var punishment = this.getActivePunishmentByType(Punishment.Type.BLACKLIST);

        if (punishment != null)
            return punishment;

        return this.getActivePunishmentByType(Punishment.Type.BAN);
    }

    public void checkPunishments() {
        for (var punishment : this.punishments)
            if (!punishment.isRemoved() && punishment.hasExpired())
                this.expirePunishment(punishment, System.currentTimeMillis());
    }

    public void addPunishment(Punishment punishment) {
        this.punishments.add(punishment);

        punishment.save();
        ProfileUtil.updateCache(this);

        PigeonUtil.broadcast(new PunishmentAddPayload(this.uuid, punishment.getId()));
    }

    public void removePunishment(Punishment punishment, @Nullable UUID removedByUUID, Long removedAt, String removedReason) {
        punishment.remove(removedByUUID, removedAt, removedReason);
        ProfileUtil.updateCache(this);

        PigeonUtil.broadcast(new PunishmentRemovePayload(this.uuid, punishment.getId()));
    }

    public void expirePunishment(Punishment punishment, long removedAt) {
        punishment.remove(null, removedAt, "Punishment Expired");
    }

    public List<Grant> getSortedGrants() {
        var sortedGrants = new ArrayList<Grant>();
        var activeGrants = new ArrayList<>(this.grants);
        var removedGrants = new ArrayList<>(this.grants);

        activeGrants.removeIf(Grant::isRemoved);
        removedGrants.removeIf(Grant::isActive);

        activeGrants.sort(Grant.COMPARATOR);
        removedGrants.sort(Grant.COMPARATOR);

        sortedGrants.addAll(activeGrants);
        sortedGrants.addAll(removedGrants);

        return sortedGrants;
    }

    public void checkGrants() {
        var expiredGrants = new ArrayList<>(this.grants)
                .stream()
                .filter(grant -> !grant.isRemoved() && grant.hasExpired());

        if(!this.grants.isEmpty()) {
            expiredGrants.forEach(grant -> grant.remove(null, grant.getAddedAt() + grant.getDuration(), "Grant Expired"));

            GrantCache.updateCache(this);
        }

        this.grant = new ArrayList<>(this.grants)
                .stream()
                .filter(Grant::isActive)
                .min(Grant.COMPARATOR)
                .orElse(Grant.DEFAULT_GRANT);
    }

    public void addGrant(Grant grant) {
        if (grant.isDefault())
            return;

        this.grants.add(grant);

        grant.save();
        ProfileUtil.updateCache(this);

        PigeonUtil.broadcast(new GrantAddPayload(this.uuid, grant.getId()));
    }

    public void removeGrant(Grant grant, UUID removedBy, Long removedAt, String removedReason) {
        if (grant.isDefault())
            return;

        grant.remove(removedBy, removedAt, removedReason);
        ProfileUtil.updateCache(this);

        PigeonUtil.broadcast(new GrantRemovePayload(this.uuid, grant.getId()));
    }

    public void expireGrant(Grant grant, long removedAt) {
        if (grant.isDefault())
            return;

        grant.remove(null, removedAt, "Grant Expired");
    }

    public Component getChatFormat() {
        return Component.text().append(
                this.getColoredPrefix(),
                Component.space(),
                this.getColoredName()
        ).build();
    }

    public Component getColoredName() {
        return Component.text(this.name, this.grant.getRank().getColor());
    }

    public Component getColoredPrefix() {
        return this.grant.getRank().getPrefix();
    }

    @Override
    public boolean equals(Object object) {
        return this == object || object instanceof Profile other && other.getUuid().equals(this.uuid);
    }

    @Override
    public int hashCode() {
        return this.uuid.hashCode();
    }

    @Getter @Setter
    public static class Options {

        private boolean receivingPartyRequests = true;
        private boolean receivingFriendRequests = true;
        private boolean receivingPublicChat = true;
        private boolean receivingConversations = true;
        private boolean receivingMessageSounds = true;

    }

    @Getter @Setter
    public static class Staff {

        private String twoFactorKey = "";
        private boolean receivingStaffMessages = true;
        private boolean locked;

    }

}