package cc.minetale.commonlib.profile;

import cc.minetale.commonlib.CommonLib;
import cc.minetale.commonlib.api.Grant;
import cc.minetale.commonlib.pigeon.payloads.grant.GrantAddPayload;
import cc.minetale.commonlib.pigeon.payloads.grant.GrantExpirePayload;
import cc.minetale.commonlib.pigeon.payloads.grant.GrantRemovePayload;
import cc.minetale.commonlib.pigeon.payloads.profile.ProfileRequestPayload;
import cc.minetale.commonlib.pigeon.payloads.profile.ProfileUpdatePayload;
import cc.minetale.commonlib.api.Punishment;
import cc.minetale.commonlib.pigeon.payloads.punishment.PunishmentAddPayload;
import cc.minetale.commonlib.pigeon.payloads.punishment.PunishmentExpirePayload;
import cc.minetale.commonlib.pigeon.payloads.punishment.PunishmentRemovePayload;
import cc.minetale.commonlib.util.MC;
import cc.minetale.commonlib.util.PigeonUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Getter @Setter @Builder @AllArgsConstructor
public class Profile {

    private UUID id;
    private List<String> punishments;
    private List<Punishment> cachedPunishments;
    private List<String> grants;
    private List<Grant> cachedGrants;
    private List<UUID> ignored;
    private List<UUID> friends;
    private Options optionsProfile;
    private Staff staffProfile;
    private String name;
    private String currentAddress;
    private String discord;
    private Grant grant;
    private int gold;
    private long firstSeen;
    private long lastSeen;
    private long experience;

    /**
     * <strong>Usage reserved for Blitz.</strong> <br>
     */
    public Profile(String username, UUID id) {
        this.id = id;
        this.name = username;
        this.punishments = new ArrayList<>();
        this.cachedPunishments = new ArrayList<>();
        this.grants = new ArrayList<>();
        this.cachedGrants = new ArrayList<>();
        this.ignored = new ArrayList<>();
        this.friends = new ArrayList<>();
        this.optionsProfile = new Options();
        this.staffProfile = new Staff();
    }

    public static @Nullable Profile fromDocument(Document document) {
        if (document != null) {
            var ignoredList = new ArrayList<UUID>();
            for (String ignored : document.getList("ignored", String.class)) {
                UUID ignoredId = UUID.fromString(ignored);
                ignoredList.add(ignoredId);
            }

            var friendsList = new ArrayList<UUID>();
            for (String friends : document.getList("friends", String.class)) {
                UUID friendId = UUID.fromString(friends);
                friendsList.add(friendId);
            }

            var punishmentsList = new ArrayList<>(document.getList("punishments", String.class));
            var grantsList = new ArrayList<>(document.getList("grants", String.class));

            var profile = Profile.builder()
                    .id(UUID.fromString(document.getString("_id")))
                    .name(document.getString("name"))
                    .firstSeen(document.getLong("firstSeen"))
                    .lastSeen(document.getLong("lastSeen"))
                    .currentAddress(document.getString("currentAddress"))
                    .optionsProfile(new Options(document.get("optionsProfile", Document.class)))
                    .staffProfile(new Staff(document.get("staffProfile", Document.class)))
                    .discord(document.getString("discord"))
                    .gold(document.getInteger("gold"))
                    .experience(document.getLong("experience"))
                    .ignored(ignoredList)
                    .friends(friendsList)
                    .punishments(punishmentsList)
                    .cachedPunishments(new ArrayList<>(punishmentsList.stream().map(Punishment::getPunishment).filter(Objects::nonNull).collect(Collectors.toList()))) // TODO -> Find documents in bulk
                    .grants(grantsList)
                    .cachedGrants(new ArrayList<>(grantsList.stream().map(Grant::getGrant).filter(Objects::nonNull).collect(Collectors.toList()))); // TODO -> Find documents in bulk

            var builtProfile = profile.build();

            builtProfile.validate();

            return builtProfile;
        }

        return null;
    }

    public static CompletableFuture<Profile> getProfile(@NotNull UUID id) {
        var future = new CompletableFuture<Profile>();

        PigeonUtil.getPigeon()
                .sendTo(new ProfileRequestPayload(id, payload -> handleProfileRetrieval(future, payload)),
                        PigeonUtil.GeneralUnits.BLITZ.getUnit());

        return future;
    }

    public static CompletableFuture<Profile> getProfile(@NotNull String name) {
        var future = new CompletableFuture<Profile>();

        PigeonUtil.getPigeon()
                .sendTo(new ProfileRequestPayload(name, payload -> handleProfileRetrieval(
                        future, payload)),
                        PigeonUtil.GeneralUnits.BLITZ.getUnit());

        return future;
    }

    public static CompletableFuture<Profile> getProfile(@Nullable String name, @NotNull UUID id) {
        var future = new CompletableFuture<Profile>();

        PigeonUtil.getPigeon()
                .sendTo(new ProfileRequestPayload(name, id, payload -> handleProfileRetrieval(
                        future, payload)),
                        PigeonUtil.GeneralUnits.BLITZ.getUnit());

        return future;
    }

    private static void handleProfileRetrieval(CompletableFuture<Profile> future, ProfileRequestPayload payload) {
        var result = payload.getResult();

        if(result != null && result.isSuccessful() && payload.getProfiles() != null && payload.getProfiles().size() >= 1) {
            future.complete(payload.getProfiles().get(0));
        } else {
            future.complete(null);
        }
    }

    public static CompletableFuture<List<Profile>> getProfilesByIds(@NotNull List<UUID> ids) {
        var future = new CompletableFuture<List<Profile>>();

        PigeonUtil.getPigeon()
                .sendTo(ProfileRequestPayload.bulkRequestByIds(ids,
                                payload -> future.complete(Objects.requireNonNullElse(payload.getProfiles(), new ArrayList<>()))),
                        PigeonUtil.GeneralUnits.BLITZ.getUnit());

        return future;
    }

    public static CompletableFuture<List<Profile>> getProfilesByNames(@NotNull List<String> names) {
        var future = new CompletableFuture<List<Profile>>();

        PigeonUtil.getPigeon()
                .sendTo(ProfileRequestPayload.bulkRequestByNames(names,
                                payload -> future.complete(Objects.requireNonNullElse(payload.getProfiles(), new ArrayList<>()))),
                        PigeonUtil.GeneralUnits.BLITZ.getUnit());

        return future;
    }

    public static CompletableFuture<List<Profile>> getProfiles(List<String> names, List<UUID> ids) {
        var future = new CompletableFuture<List<Profile>>();

        PigeonUtil.getPigeon()
                .sendTo(new ProfileRequestPayload(names, ids,
                                payload -> future.complete(Objects.requireNonNullElse(payload.getProfiles(), new ArrayList<>()))),
                        PigeonUtil.GeneralUnits.BLITZ.getUnit());

        return future;
    }

    public static CompletableFuture<List<Profile>> getProfiles(Map<UUID, String> info) {
        var future = new CompletableFuture<List<Profile>>();

        PigeonUtil.getPigeon()
                .sendTo(new ProfileRequestPayload(info,
                                payload -> future.complete(Objects.requireNonNullElse(payload.getProfiles(), new ArrayList<>()))),
                        PigeonUtil.GeneralUnits.BLITZ.getUnit());

        return future;
    }

    /**
     * Updates the {@linkplain Profile} on Blitz. <br>
     * Use this to make sure a {@linkplain Profile} is up-to-date when it changes or leaves a server.
     */
    public CompletableFuture<ProfileQueryResult> update() {
        var future = new CompletableFuture<ProfileQueryResult>();

        PigeonUtil.getPigeon()
                .sendTo(new ProfileUpdatePayload(this, payload -> future.complete(payload.getResult())), PigeonUtil.GeneralUnits.BLITZ.getUnit());

        return future;
    }

    /**
     * <strong>Usage reserved for Blitz.</strong>
     */
    public Document toDocument() {
        var document = new Document();

        document.put("_id", this.id.toString());
        document.put("name", this.name);
        document.put("search", this.name.toUpperCase());
        document.put("firstSeen", this.firstSeen);
        document.put("lastSeen", this.lastSeen);
        document.put("currentAddress", this.currentAddress);
        document.put("optionsProfile", this.optionsProfile.toDocument());
        document.put("staffProfile", this.staffProfile.toDocument());
        document.put("discord", this.discord);
        document.put("gold", this.gold);
        document.put("experience", this.experience);
        document.put("grants", this.grants);
        document.put("punishments", this.punishments);
        document.put("ignored", this.ignored);
        document.put("friends", this.friends);

        return document;
    }

    /**
     * Sets the profiles Grant to the highest Grant.
     */
    public void reloadGrant() {
        this.grant = this.getActiveGrant();
    }

    /**
     * Validates the profiles Punishments and Grants.
     */
    public void validate() {
        this.validatePunishments();
        this.reloadGrant();
    }

    /**
     * Returns if the Profile is ignoring another Profile.
     */
    public boolean isIgnoring(Profile profile) {
        return this.ignored.contains(profile.getId());
    }

    /**
     * Returns the amount of Punishments the Profile has by a Type.
     */
    public int getPunishmentCountByType(Punishment.Type type) {
        int i = 0;

        for (Punishment punishment : this.cachedPunishments)
            if (punishment.getType() == type)
                i++;

        return i;
    }

    /**
     * Returns the active Punishment by a Type.
     */
    public Punishment getActivePunishmentByType(Punishment.Type type) {
        for (Punishment punishment : this.cachedPunishments)
            if (punishment.getType() == type && !punishment.isRemoved() && !punishment.hasExpired())
                return punishment;

        return null;
    }

    /**
     * Returns either an active Ban or Blacklist.
     */
    public Punishment getActiveBan() {
        Punishment punishment = this.getActivePunishmentByType(Punishment.Type.BLACKLIST);

        if (punishment != null)
            return punishment;

        return this.getActivePunishmentByType(Punishment.Type.BAN);
    }

    /**
     * Validates the Profile's Punishments.
     */
    public void validatePunishments() {
        for (Punishment punishment : this.cachedPunishments)
            if (!punishment.isRemoved() && punishment.hasExpired())
                this.expirePunishment(punishment, System.currentTimeMillis());
    }

    /**
     * Adds a Punishment to the Profile.
     */
    public void addPunishment(Punishment punishment) {
        this.punishments.add(punishment.getId());
        this.cachedPunishments.add(punishment);

        punishment.save();

        this.update();

        PigeonUtil.broadcast(new PunishmentAddPayload(this.getId(), punishment.getId()));

        CommonLib.getCommonLib().getApiListeners().forEach(provider -> provider.punishmentAdd(punishment));
    }

    /**
     * Removes a Punishment from the Profile.
     */
    public void removePunishment(Punishment punishment, @Nullable UUID removedByUUID, Long removedAt, String removedReason) {
        punishment.remove(removedByUUID, removedAt, removedReason);

        this.update();

        PigeonUtil.broadcast(new PunishmentRemovePayload(this.getId(), punishment.getId()));

        CommonLib.getCommonLib().getApiListeners().forEach(provider -> provider.punishmentRemove(punishment));
    }

    /**
     * Expires a Punishment on the Profile.
     */
    public void expirePunishment(Punishment punishment, Long removedAt) {
        punishment.remove(null, removedAt, "Punishment Expired");

        this.update();

        PigeonUtil.broadcast(new PunishmentExpirePayload(this.getId(), punishment.getId()));

        CommonLib.getCommonLib().getApiListeners().forEach(provider -> provider.punishmentExpire(punishment));
    }

    /**
     * Validates the Profile's Grants.
     */
    public void validateGrants() {
        for (Grant grant : this.cachedGrants)
            if (!grant.isRemoved() && grant.hasExpired())
                this.expireGrant(grant, System.currentTimeMillis());
    }

    /**
     * Returns the active Grant of the Profile.
     */
    public Grant getActiveGrant() {
        this.validateGrants();

        List<Grant> activeGrants = new ArrayList<>();

        for (Grant grant : this.cachedGrants)
            if (!grant.isRemoved() && !grant.hasExpired())
                activeGrants.add(grant);

        return activeGrants.stream()
                .min(Grant.COMPARATOR)
                .orElse(Grant.getDefaultGrant(this.getId()));
    }

    /**
     * Adds a new Grant to the Profile.
     */
    public void addGrant(Grant grant) {
        if (grant.isDefault())
            return;

        this.grants.add(grant.getId());
        this.cachedGrants.add(grant);

        grant.save();
        this.update();

        PigeonUtil.broadcast(new GrantAddPayload(this.id, grant.getId()));

        CommonLib.getCommonLib().getApiListeners().forEach(provider -> provider.grantAdd(grant));
    }

    /**
     * Removes a Grant from the Profile.
     */
    public void removeGrant(Grant grant, UUID removedBy, Long removedAt, String removedReason) {
        if (grant.isDefault())
            return;

        grant.remove(removedBy, removedAt, removedReason);
        this.update();

        PigeonUtil.broadcast(new GrantRemovePayload(this.id, grant.getId()));

        CommonLib.getCommonLib().getApiListeners().forEach(provider -> provider.grantRemove(grant));
    }

    /**
     * Expires a Grant on the Profile.
     */
    public void expireGrant(Grant grant, Long removedAt) {
        if (grant.isDefault())
            return;

        grant.remove(null, removedAt, "Grant Expired");
        this.update();

        PigeonUtil.broadcast(new GrantExpirePayload(this.id, grant.getId()));

        CommonLib.getCommonLib().getApiListeners().forEach(provider -> provider.grantExpire(grant));
    }

    public Component getChatFormat() {
        return Component.text().append(
                getColoredPrefix(),
                Component.space(),
                this.getColoredName()
        ).build();
    }

    public Component getColoredName() {
        return Component.text(this.name, this.getActiveGrant().getRank().getColor());
    }

    public Component getColoredPrefix() {
        return this.getActiveGrant().getRank().getPrefix();
    }

    @Override
    public boolean equals(Object object) {
        return this == object || object instanceof Profile other && other.getId().equals(this.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Getter @Setter @Builder @AllArgsConstructor
    public static class Options {

        private boolean receivingPartyRequests = true;
        private boolean receivingFriendRequests = true;
        private boolean receivingPublicChat = true;
        private boolean receivingConversations = true;
        private boolean receivingMessageSounds = true;

        public Options() {}

        public Options(Document document) {
            this.receivingPartyRequests = document.getBoolean("receivingPartyRequests");
            this.receivingFriendRequests = document.getBoolean("receivingFriendRequests");
            this.receivingPublicChat = document.getBoolean("receivingPublicChat");
            this.receivingConversations = document.getBoolean("receivingConversations");
            this.receivingMessageSounds = document.getBoolean("receivingMessageSounds");
        }

        public Document toDocument() {
            return new Document()
                    .append("receivingPartyRequests", this.receivingPartyRequests)
                    .append("receivingFriendRequests", this.receivingFriendRequests)
                    .append("receivingPublicChat", this.receivingPublicChat)
                    .append("receivingConversations", this.receivingConversations)
                    .append("receivingMessageSounds", this.receivingMessageSounds);
        }

    }

    @Getter @Setter @Builder @AllArgsConstructor
    public static class Staff {

        private String twoFactorKey = "";
        private boolean receivingStaffMessages = true;
        private boolean locked;

        public Staff() {}

        public Staff(Document document) {
            this.twoFactorKey = document.getString("twoFactorKey");
            this.receivingStaffMessages = document.getBoolean("receivingStaffMessages");
            this.locked = document.getBoolean("locked");
        }

        public Document toDocument() {
            return new Document()
                    .append("twoFactorKey", this.twoFactorKey)
                    .append("receivingStaffMessages", this.receivingStaffMessages)
                    .append("locked", this.locked);
        }

    }

}