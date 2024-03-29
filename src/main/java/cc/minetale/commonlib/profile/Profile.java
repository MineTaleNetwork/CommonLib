package cc.minetale.commonlib.profile;

import cc.minetale.commonlib.CommonLib;
import cc.minetale.commonlib.cache.ProfileCache;
import cc.minetale.commonlib.grant.Grant;
import cc.minetale.commonlib.pigeon.payloads.grant.GrantAddPayload;
import cc.minetale.commonlib.pigeon.payloads.grant.GrantExpirePayload;
import cc.minetale.commonlib.pigeon.payloads.grant.GrantRemovePayload;
import cc.minetale.commonlib.pigeon.payloads.punishment.PunishmentAddPayload;
import cc.minetale.commonlib.pigeon.payloads.punishment.PunishmentExpirePayload;
import cc.minetale.commonlib.pigeon.payloads.punishment.PunishmentRemovePayload;
import cc.minetale.commonlib.punishment.Punishment;
import cc.minetale.commonlib.punishment.PunishmentType;
import cc.minetale.commonlib.util.BsonUtil;
import cc.minetale.commonlib.util.Database;
import cc.minetale.commonlib.util.AsyncUtil;
import cc.minetale.commonlib.util.PigeonUtil;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import com.mongodb.client.result.UpdateResult;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentSkipListMap;

@Getter
@Setter
@EqualsAndHashCode(of = {"uuid"}, callSuper = false)
public class Profile extends AbstractProfile {

    @JsonProperty("_id")
    private UUID uuid;
    private String username;
    private String currentAddress;
    private String discord;
    private long gold;
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

    public Profile(UUID uuid, String username) {
        this.uuid = uuid;
        this.username = username;
    }

    public Profile(UUID uuid) {
        this.uuid = uuid;
    }

    /**
     * Default constructor used for Jackson.
     */
    public Profile() {
    }

    /**
     * Update a player's profile in our database
     * with data from the profile object.
     *
     * @return The update result of saving
     */
    public CompletableFuture<UpdateResult> save() {
        return new CompletableFuture<UpdateResult>()
                .completeAsync(() -> {
                    var document = BsonUtil.writeToBson(this);

                    if (document != null) {
                        return Database.getProfilesCollection()
                                .replaceOne(
                                        Filters.eq(this.uuid.toString()),
                                        document,
                                        new ReplaceOptions().upsert(true)
                                );
                    }

                    return UpdateResult.unacknowledged();
                });
    }

    /**
     * Check if the specified profile is being ignored.
     *
     * @param profile The profile
     * @return Whether profile is being ignored
     */
    public boolean isIgnoring(Profile profile) {
        return this.ignored.contains(profile.getUuid());
    }

    /**
     * Check if the specified profile is friends.
     *
     * @param profile The profile
     * @return Whether profile is friends
     */
    public boolean isFriends(Profile profile) {
        return friends.contains(profile.getUuid());
    }

    /**
     * Check if the profile is a staff member.
     *
     * @return Whether the profile is staff
     */
    public boolean isStaff() {
        return grant.getRank().isStaff();
    }

    /**
     * Removes a specific friend.
     *
     * @param profile The profile
     */
    public void removeFriend(Profile profile) {
        friends.remove(profile.getUuid());
    }

    /**
     * Returns the amount of punishments the
     * profile has by a punishment type.
     *
     * @param type A punishment type
     * @return The amount of punishments of that type
     */
    public int getPunishmentCountByType(PunishmentType type) {
        return (int) this.punishments.stream().filter(punishment -> punishment.getType() == type).count();
    }

    /**
     * Returns the active punishment
     * by punishment type if it exists.
     *
     * @param type The punishment type
     * @return An active punishment
     */
    public Punishment getActivePunishmentByType(PunishmentType type) {
        for (var punishment : this.punishments) {
            if (punishment.getType() == type && punishment.isActive()) {
                return punishment;
            }
        }

        return null;
    }

    /**
     * Expires and removes punishments
     * that haven't been removed yet.
     */
    public void expirePunishments() {
        for (var punishment : this.punishments) {
            if (!punishment.isRemoved() && punishment.hasExpired()) {
                this.expirePunishment(punishment);
            }
        }
    }

    /**
     * Issue a new punishment.
     *
     * @param punishment The punishment being issued
     */
    public void issuePunishment(Punishment punishment) {
        this.punishments.add(punishment);

        AsyncUtil.runInOrder(
                punishment.save(),
                ProfileCache.updateProfile(this),
                CompletableFuture.runAsync(() -> PigeonUtil.broadcast(new PunishmentAddPayload(this.uuid, punishment)))
        );

        for (var provider : CommonLib.getProviders()) {
            provider.addPunishment(punishment);
        }
    }

    /**
     * Removes the specified punishment from the profile.
     *
     * @param punishment    The punishment being removed
     * @param removedBy     The uuid of the profile removing the punishment which can be null to represent the console
     * @param removedAt     The time in milliseconds when the punishment was removed
     * @param removedReason The reason why the punishment was removed
     */
    public void removePunishment(Punishment punishment, @Nullable UUID removedBy, long removedAt, String removedReason) {
        punishment.setRemovedById(removedBy);
        punishment.setRemovedAt(removedAt);
        punishment.setRemovedReason(removedReason);

        AsyncUtil.runInOrder(
                punishment.save(),
                ProfileCache.updateProfile(this),
                CompletableFuture.runAsync(() -> PigeonUtil.broadcast(new PunishmentRemovePayload(this.uuid, punishment)))
        );

        for (var provider : CommonLib.getProviders()) {
            provider.removePunishment(punishment);
        }
    }

    /**
     * Removes the specified punishment from
     * the profile with the expired reason.
     *
     * @param punishment The punishment being removed
     */
    public void expirePunishment(Punishment punishment) {
        punishment.setRemovedById(null);
        punishment.setRemovedAt(punishment.getAddedAt() + punishment.getDuration());
        punishment.setRemovedReason("Punishment Expired");

        AsyncUtil.runInOrder(
                punishment.save(),
                ProfileCache.updateProfile(this),
                CompletableFuture.runAsync(() -> PigeonUtil.broadcast(new PunishmentExpirePayload(this.uuid, punishment)))
        );

        for (var provider : CommonLib.getProviders()) {
            provider.expirePunishment(punishment);
        }
    }

    /**
     * Returns a list of grants that have been sorted
     * first by if the grant is active and then by
     * the grant's rank weight.
     *
     * @return The profiles sorted grants
     */
    public List<Grant> getSortedGrants() {
        var activeGrants = new ArrayList<>(this.grants);

        var sorter = (Comparator<Grant>) (grant1, grant2) -> {
            var activeCompare = Boolean.compare(grant2.isActive(), grant1.isActive());

            if (activeCompare != 0) {
                return activeCompare;
            }

            var rank1 = grant1.getRank();
            var rank2 = grant2.getRank();

            return Integer.compare(rank1.ordinal(), rank2.ordinal());
        };

        activeGrants.sort(sorter);

        return activeGrants;
    }

    /**
     * Checks and removes any grant that needs to be removed.
     * The profiles next grant is also activated.
     */
    public void checkGrants() {
        for (var grant : this.grants) {
            if (!grant.isRemoved() && grant.hasExpired()) {
                expireGrant(grant);
            }
        }

        this.activateNextGrant();
    }

    /**
     * Activates the profiles next grant.
     */
    public void activateNextGrant() {
        var activeGrants = new ConcurrentSkipListMap<Integer, Grant>();

        for (var grant : this.grants) {
            if (grant.isActive()) {
                activeGrants.put(grant.getRank().ordinal(), grant);
            }
        }

        var grantEntry = activeGrants.firstEntry();

        if (grantEntry != null) {
            this.grant = grantEntry.getValue();
        } else {
            this.grant = Grant.DEFAULT_GRANT;
        }
    }

    /**
     * Issues a new grant.
     *
     * @param grant The grant being issued
     */
    public void issueGrant(Grant grant) {
        if (grant.isDefault()) { return; }

        this.grants.add(grant);

        AsyncUtil.runInOrder(
                grant.save(),
                ProfileCache.updateProfile(this),
                CompletableFuture.runAsync(() -> PigeonUtil.broadcast(new GrantAddPayload(this.uuid, grant.getId())))
        );

        for (var provider : CommonLib.getProviders()) {
            provider.addGrant(grant);
        }
    }

    /**
     * Removes the specified grant from the profile
     *
     * @param grant         The grant being removed
     * @param removedBy     The uuid of the profile removing the grant which can be null to represent the console
     * @param removedAt     The time in milliseconds when the grant was removed
     * @param removedReason The reason why the grant was removed
     */
    public void removeGrant(Grant grant, @Nullable UUID removedBy, long removedAt, String removedReason) {
        if (grant.isDefault()) { return; }

        grant.setRemovedById(removedBy);
        grant.setRemovedAt(removedAt);
        grant.setRemovedReason(removedReason);

        AsyncUtil.runInOrder(
                grant.save(),
                ProfileCache.updateProfile(this),
                CompletableFuture.runAsync(() -> PigeonUtil.broadcast(new GrantRemovePayload(this.uuid, grant.getId())))
        );

        for (var provider : CommonLib.getProviders()) {
            provider.removeGrant(grant);
        }
    }

    /**
     * Removes the specified grant from
     * the profile with the expired reason.
     *
     * @param grant The grant being removed
     */
    public void expireGrant(Grant grant) {
        if (grant.isDefault()) { return; }

        grant.setRemovedById(null);
        grant.setRemovedAt(grant.getAddedAt() + grant.getDuration());
        grant.setRemovedReason("Grant Expired");

        AsyncUtil.runInOrder(
                grant.save(),
                ProfileCache.updateProfile(this),
                CompletableFuture.runAsync(() -> PigeonUtil.broadcast(new GrantExpirePayload(this.uuid, grant.getId())))
        );

        for (var provider : CommonLib.getProviders()) {
            provider.expireGrant(grant);
        }
    }

    /**
     * Returns a decorated component
     * of the profiles chat format.
     *
     * @return The decorated component
     */
    @Override
    public Component getChatFormat() {
        return Component.text().append(
                this.getColoredPrefix(),
                Component.space(),
                this.getColoredName()
        ).build();
    }

    /**
     * Returns a decorated component of the
     * profiles name colored their grant's rank color.
     *
     * @return The decorated component
     */
    @Override
    public Component getColoredName() {
        return Component.text(this.username, this.grant.getRank().getColor());
    }

    /**
     * Returns a decorated component of the
     * profile's grant's rank prefix.
     *
     * @return The decorated component
     */
    @Override
    public Component getColoredPrefix() {
        return this.grant.getRank().getPrefix();
    }

    @Getter
    @Setter
    public static class Options {
        private boolean receivingPartyRequests = true;
        private boolean receivingFriendRequests = true;
        private boolean receivingPublicChat = true;
        private boolean receivingConversations = true;
        private boolean receivingMessageSounds = true;
    }

    @Getter
    @Setter
    public static class Staff {
        private String twoFactorKey = "";
        private boolean receivingStaffMessages = true;
        private boolean locked;
    }

}