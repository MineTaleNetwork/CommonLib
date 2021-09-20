package cc.minetale.commonlib.modules.profile;

import cc.minetale.commonlib.CommonLib;
import cc.minetale.commonlib.modules.grant.Grant;
import cc.minetale.commonlib.modules.grant.GrantAPI;
import cc.minetale.commonlib.modules.network.Gamemode;
import cc.minetale.commonlib.modules.pigeon.payloads.grant.GrantAddPayload;
import cc.minetale.commonlib.modules.pigeon.payloads.grant.GrantExpirePayload;
import cc.minetale.commonlib.modules.pigeon.payloads.grant.GrantRemovePayload;
import cc.minetale.commonlib.modules.pigeon.payloads.punishment.PunishmentAddPayload;
import cc.minetale.commonlib.modules.pigeon.payloads.punishment.PunishmentExpirePayload;
import cc.minetale.commonlib.modules.pigeon.payloads.punishment.PunishmentRemovePayload;
import cc.minetale.commonlib.modules.punishment.Punishment;
import cc.minetale.commonlib.modules.rank.Rank;
import cc.minetale.commonlib.util.MC;
import cc.minetale.commonlib.util.PigeonUtil;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ProfileAPI {

    private final Profile profile;
    private final CommonLib commonLib;

    public ProfileAPI(Profile profile) {
        this.profile = profile;
        this.commonLib = CommonLib.getCommonLib();
    }

    /**
     * Returns if the Profile is ignoring another Profile.
     */
    public boolean isIgnoring(Profile profile) {
        return this.profile.getIgnored().contains(profile.getId());
    }

    /**
     * Returns the amount of Punishments the Profile has by a Type.
     */
    public int getPunishmentCountByType(Punishment.Type type) {
        int i = 0;

        for (Punishment punishment : this.profile.getCachedPunishments()) {

            if (punishment.getType() == type) i++;
        }

        return i;
    }

    /**
     * Returns the active Punishment by a Type.
     */
    public Punishment getActivePunishmentByType(Punishment.Type type) {
        for (Punishment punishment : this.profile.getCachedPunishments()) {
            if (punishment.getType() == type && !punishment.isRemoved() && !punishment.api().hasExpired()) {
                return punishment;
            }
        }

        return null;
    }

    /**
     * Returns either an active Ban or Blacklist.
     */
    public Punishment getActiveBan() {
        Punishment punishment = this.getActivePunishmentByType(Punishment.Type.BLACKLIST);

        if (punishment != null) {
            return punishment;
        }

        return this.getActivePunishmentByType(Punishment.Type.BAN);
    }

    /**
     * Validates the Profile's Punishments.
     */
    public void validatePunishments() {
        for (Punishment punishment : this.profile.getCachedPunishments()) {
            if (!punishment.isRemoved() && punishment.api().hasExpired()) {
                this.expirePunishment(punishment, System.currentTimeMillis());
            }
        }
    }

    /**
     * Adds a Punishment to the Profile.
     */
    public void addPunishment(Punishment punishment) {
        this.profile.getPunishments().add(punishment.getId());
        this.profile.getCachedPunishments().add(punishment);

        punishment.save();
        this.profile.update();

        PigeonUtil.broadcast(new PunishmentAddPayload(this.profile, punishment.getId()));

        CommonLib.getCommonLib().getApiListeners().forEach(provider -> provider.punishmentAdd(punishment));
    }

    /**
     * Removes a Punishment from the Profile.
     */
    public void removePunishment(Punishment punishment, @Nullable UUID removedByUUID, Long removedAt, String removedReason) {
        punishment.api().remove(removedByUUID, removedAt, removedReason);

        this.profile.update();

        PigeonUtil.broadcast(new PunishmentRemovePayload(this.profile, punishment.getId()));

        CommonLib.getCommonLib().getApiListeners().forEach(provider -> provider.punishmentRemove(punishment));
    }

    /**
     * Expires a Punishment on the Profile.
     */
    public void expirePunishment(Punishment punishment, Long removedAt) {
        punishment.api().remove(null, removedAt, "Punishment Expired");

        this.profile.update();

        PigeonUtil.broadcast(new PunishmentExpirePayload(punishment.getId()));

        CommonLib.getCommonLib().getApiListeners().forEach(provider -> provider.punishmentExpire(punishment));
    }

    /**
     * Validates the Profile's Grants.
     */
    public void validateGrants() {
        for (final var it = this.profile.getCachedGrants().iterator(); it.hasNext(); ) {
            Grant grant = it.next();

            if (Rank.getRank(grant.getRankUUID(), true) == null) {
                this.profile.getGrants().remove(grant.getId());
                grant.delete();
                it.remove();
            } else if (!grant.isRemoved() && grant.api().hasExpired()) {
                this.expireGrant(grant, System.currentTimeMillis());
            }
        }
    }

    /**
     * Returns the active Grant of the Profile.
     */
    public Grant getActiveGrant() {
        this.validateGrants();

        List<Grant> activeGrants = new ArrayList<>();

        for (Grant grant : this.profile.getCachedGrants()) {
            if (!grant.isRemoved() && !grant.api().hasExpired()) {
                activeGrants.add(grant);
            }
        }

        return activeGrants.stream().min(GrantAPI.COMPARATOR).orElse(Grant.getDefaultGrant(this.profile.getId()));
    }

    /**
     * Adds a new Grant to the Profile.
     */
    public void addGrant(Grant grant) {
        if (grant.api().isDefault())
            return;

        this.profile.getGrants().add(grant.getId());
        this.profile.getCachedGrants().add(grant);

        grant.save();
        this.profile.update();

        PigeonUtil.broadcast(new GrantAddPayload(this.profile, grant.getId()));

        CommonLib.getCommonLib().getApiListeners().forEach(provider -> provider.grantAdd(grant));
    }

    /**
     * Removes a Grant from the Profile.
     */
    public void removeGrant(Grant grant, UUID removedBy, Long removedAt, String removedReason) {
        if (grant.api().isDefault())
            return;

        grant.api().remove(removedBy, removedAt, removedReason);
        this.profile.update();

        PigeonUtil.broadcast(new GrantRemovePayload(this.profile, grant.getId()));

        CommonLib.getCommonLib().getApiListeners().forEach(provider -> provider.grantRemove(grant));
    }

    /**
     * Expires a Grant on the Profile.
     */
    public void expireGrant(Grant grant, Long removedAt) {
        if (grant.api().isDefault())
            return;

        grant.api().remove(null, removedAt, "Grant Expired");
        this.profile.update();

        PigeonUtil.broadcast(new GrantExpirePayload(this.profile, grant.getId()));

        CommonLib.getCommonLib().getApiListeners().forEach(provider -> provider.grantExpire(grant));
    }

    public boolean addGamemodeStorage(GamemodeStorage storage) {
        var gamemodeStorages = this.profile.getGamemodeStorages();

        var gamemode = storage.getGamemode();
        if (gamemodeStorages.containsKey(gamemode)) {
            return false;
        }

        gamemodeStorages.put(gamemode, storage);
        return true;
    }

    public GamemodeStorage getGamemodeStorage(Gamemode gamemode) {
        return this.profile.getGamemodeStorages().get(gamemode);
    }

    public boolean hasGamemodeStorage(Gamemode gamemode) {
        return this.profile.getGamemodeStorages().containsKey(gamemode);
    }

    public boolean ensureGamemodeStorage(Gamemode gamemode, List<GamemodeStorage.StorageValue> values) {
        var anyEnsured = false;

        if (hasGamemodeStorage(gamemode)) {
            var storage = getGamemodeStorage(gamemode);
            for (GamemodeStorage.StorageValue value : values) {
                if (storage.ensure(value)) {
                    anyEnsured = true;
                }
            }
        } else {
            anyEnsured = true;

            var storage = new GamemodeStorage(gamemode);
            for (GamemodeStorage.StorageValue value : values) {
                storage.add(value);
            }

            addGamemodeStorage(storage);
        }

        return anyEnsured;
    }

    public Component getChatFormat() {
        return getColoredPrefix().append(this.getColoredName());
    }

    public Component getColoredName() {
        Rank rank = this.profile.api().getActiveGrant().api().getRank();
        return Component.text(this.profile.getName(), MC.CC.valueOf(rank.getColor()).getTextColor());
    }

    public Component getColoredPrefix() {
        Rank rank = this.profile.api().getActiveGrant().api().getRank();
        return rank.getPrefix() != null ? MC.Style.fromLegacy(rank.getPrefix()) : Component.empty();
    }

}
