package cc.minetale.commonlib.api;

import cc.minetale.commonlib.pigeon.payloads.grant.GrantAddPayload;
import cc.minetale.commonlib.pigeon.payloads.grant.GrantRemovePayload;
import cc.minetale.commonlib.pigeon.payloads.profile.ProfileRequestPayload;
import cc.minetale.commonlib.pigeon.payloads.profile.ProfileUpdatePayload;
import cc.minetale.commonlib.pigeon.payloads.punishment.PunishmentAddPayload;
import cc.minetale.commonlib.pigeon.payloads.punishment.PunishmentRemovePayload;
import cc.minetale.commonlib.util.PigeonUtil;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Getter @Setter
public class Profile {

    private UUID id;
    private List<String> punishments = new ArrayList<>();
    private List<Punishment> cachedPunishments = new ArrayList<>();
    private List<String> grants = new ArrayList<>();
    private List<Grant> cachedGrants = new ArrayList<>();
    private List<UUID> ignored = new ArrayList<>();
    private List<UUID> friends = new ArrayList<>();
    private Options optionsProfile = new Options();
    private Staff staffProfile = new Staff();
    private String name;
    private String currentAddress;
    private String discord;
    private Grant grant;
    private int gold;
    private long firstSeen;
    private long lastSeen;
    private long experience;

    public static Profile createBlitzProfile(UUID uuid, String username) {
        var profile = new Profile();

        profile.setId(uuid);
        profile.setName(username);
        profile.reloadGrant();

        return profile;
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

            var profile = new Profile();

            profile.setId(UUID.fromString(document.getString("_id")));
            profile.setName(document.getString("name"));
            profile.setFirstSeen(document.getLong("firstSeen"));
            profile.setLastSeen(document.getLong("lastSeen"));
            profile.setCurrentAddress(document.getString("currentAddress"));
            profile.setOptionsProfile(new Options(document.get("optionsProfile", Document.class)));
            profile.setStaffProfile(new Staff(document.get("staffProfile", Document.class)));
            profile.setDiscord(document.getString("discord"));
            profile.setGold(document.getInteger("gold"));
            profile.setExperience(document.getLong("experience"));
            profile.setIgnored(ignoredList);
            profile.setFriends(friendsList);
            profile.setPunishments(punishmentsList);
            profile.setGrants(grantsList);
            profile.setCachedPunishments(new ArrayList<>(punishmentsList.stream().map(Punishment::getPunishment).filter(Objects::nonNull).collect(Collectors.toList())));
            profile.setCachedGrants(new ArrayList<>(grantsList.stream().map(Grant::getGrant).filter(Objects::nonNull).collect(Collectors.toList())));
            profile.validate();

            return profile;
        }

        return null;
    }

    public static CompletableFuture<Profile> getProfile(@NotNull UUID id) {
        var future = new CompletableFuture<Profile>();

        PigeonUtil.sendTo(new ProfileRequestPayload(id, payload -> handleProfileRetrieval(future, payload)),
                PigeonUtil.GeneralUnits.BLITZ.getUnit());

        return future;
    }

    public static CompletableFuture<Profile> getProfile(@NotNull String name) {
        var future = new CompletableFuture<Profile>();

        PigeonUtil.sendTo(new ProfileRequestPayload(name, payload -> handleProfileRetrieval(future, payload)),
                PigeonUtil.GeneralUnits.BLITZ.getUnit());

        return future;
    }

    public static CompletableFuture<Profile> getProfile(@Nullable String name, @NotNull UUID id) {
        var future = new CompletableFuture<Profile>();

        PigeonUtil.sendTo(new ProfileRequestPayload(name, id, payload -> handleProfileRetrieval(future, payload)),
                PigeonUtil.GeneralUnits.BLITZ.getUnit());

        return future;
    }

    private static void handleProfileRetrieval(CompletableFuture<Profile> future, ProfileRequestPayload payload) {
        var result = payload.getResult();

        if (result != null && result.isSuccessful() && payload.getProfiles() != null && payload.getProfiles().size() >= 1) {
            future.complete(payload.getProfiles().get(0));
        } else {
            future.complete(null);
        }
    }

    public static CompletableFuture<List<Profile>> getProfilesByIds(@NotNull List<UUID> ids) {
        var future = new CompletableFuture<List<Profile>>();

        PigeonUtil.sendTo(ProfileRequestPayload.bulkRequestByIds(ids,
                        payload -> future.complete(Objects.requireNonNullElse(payload.getProfiles(), new ArrayList<>()))),
                PigeonUtil.GeneralUnits.BLITZ.getUnit());

        return future;
    }

    public static CompletableFuture<List<Profile>> getProfilesByNames(@NotNull List<String> names) {
        var future = new CompletableFuture<List<Profile>>();

        PigeonUtil.sendTo(ProfileRequestPayload.bulkRequestByNames(names,
                        payload -> future.complete(Objects.requireNonNullElse(payload.getProfiles(), new ArrayList<>()))),
                PigeonUtil.GeneralUnits.BLITZ.getUnit());

        return future;
    }

    public static CompletableFuture<List<Profile>> getProfiles(List<String> names, List<UUID> ids) {
        var future = new CompletableFuture<List<Profile>>();

        PigeonUtil.sendTo(new ProfileRequestPayload(names, ids,
                        payload -> future.complete(Objects.requireNonNullElse(payload.getProfiles(), new ArrayList<>()))),
                PigeonUtil.GeneralUnits.BLITZ.getUnit());

        return future;
    }

    public static CompletableFuture<List<Profile>> getProfiles(Map<UUID, String> info) {
        var future = new CompletableFuture<List<Profile>>();

        PigeonUtil.sendTo(new ProfileRequestPayload(info,
                        payload -> future.complete(Objects.requireNonNullElse(payload.getProfiles(), new ArrayList<>()))),
                PigeonUtil.GeneralUnits.BLITZ.getUnit());

        return future;
    }

    public CompletableFuture<ProfileQueryResult> update() {
        var future = new CompletableFuture<ProfileQueryResult>();

        PigeonUtil.sendTo(new ProfileUpdatePayload(this, payload -> future.complete(payload.getResult())), PigeonUtil.GeneralUnits.BLITZ.getUnit());

        return future;
    }

    /**
     * <strong>Usage reserved for Blitz.</strong>
     */
    public Document toDocument() {
        return new Document()
                .append("_id", this.id.toString())
                .append("name", this.name)
                .append("search", this.name.toUpperCase())
                .append("firstSeen", this.firstSeen)
                .append("lastSeen", this.lastSeen)
                .append("currentAddress", this.currentAddress)
                .append("optionsProfile", this.optionsProfile.toDocument())
                .append("staffProfile", this.staffProfile.toDocument())
                .append("discord", this.discord)
                .append("gold", this.gold)
                .append("experience", this.experience)
                .append("grants", this.grants)
                .append("punishments", this.punishments)
                .append("ignored", this.ignored)
                .append("friends", this.friends);
    }

    public void reloadGrant() {
        this.grant = this.getActiveGrant();
    }

    public void validate() {
        this.validatePunishments();
        this.reloadGrant();
    }

    public boolean isIgnoring(Profile profile) {
        return this.ignored.contains(profile.getId());
    }

    public int getPunishmentCountByType(Punishment.Type type) {
        return (int) this.cachedPunishments.stream().filter(punishment -> punishment.getType() == type).count();
    }

    public Punishment getActivePunishmentByType(Punishment.Type type) {
        for (Punishment punishment : this.cachedPunishments)
            if (punishment.getType() == type && !punishment.isRemoved() && !punishment.hasExpired())
                return punishment;

        return null;
    }

    public Punishment getActiveBan() {
        Punishment punishment = this.getActivePunishmentByType(Punishment.Type.BLACKLIST);

        if (punishment != null)
            return punishment;

        return this.getActivePunishmentByType(Punishment.Type.BAN);
    }

    public void validatePunishments() {
        for (Punishment punishment : this.cachedPunishments)
            if (!punishment.isRemoved() && punishment.hasExpired())
                this.expirePunishment(punishment, System.currentTimeMillis());
    }

    public void addPunishment(Punishment punishment) {
        this.punishments.add(punishment.getId());
        this.cachedPunishments.add(punishment);

        punishment.save();
        this.update();

        PigeonUtil.broadcast(new PunishmentAddPayload(this.getId(), punishment.getId()));
    }

    public void removePunishment(Punishment punishment, @Nullable UUID removedByUUID, Long removedAt, String removedReason) {
        punishment.remove(removedByUUID, removedAt, removedReason);
        this.update();

        PigeonUtil.broadcast(new PunishmentRemovePayload(this.getId(), punishment.getId()));
    }

    public void expirePunishment(Punishment punishment, long removedAt) {
        punishment.remove(null, removedAt, "Punishment Expired");
    }

    public void validateGrants() {
        for (var grant : this.cachedGrants)
            if (!grant.isRemoved() && grant.hasExpired())
                this.expireGrant(grant, System.currentTimeMillis());
    }

    public List<Grant> getSortedGrants() {
        List<Grant> sortedGrants = new ArrayList<>();

        List<Grant> activeGrants = new ArrayList<>(this.cachedGrants);
        List<Grant> removedGrants = new ArrayList<>(this.cachedGrants);

        activeGrants.removeIf(Grant::isRemoved);
        removedGrants.removeIf(Grant::isActive);

        activeGrants.sort(Grant.COMPARATOR);
        removedGrants.sort(Grant.COMPARATOR);

        sortedGrants.addAll(activeGrants);
        sortedGrants.addAll(removedGrants);

        return sortedGrants;
    }

    public Grant getActiveGrant() {
        this.validateGrants();

        List<Grant> activeGrants = new ArrayList<>();

        for (Grant grant : this.cachedGrants)
            if (!grant.isRemoved() && !grant.hasExpired())
                activeGrants.add(grant);

        return activeGrants.stream()
                .min(Grant.COMPARATOR)
                .orElse(Grant.DEFAULT_GRANT);
    }

    public void addGrant(Grant grant) {
        if (grant.isDefault())
            return;

        this.grants.add(grant.getId());
        this.cachedGrants.add(grant);

        grant.save();
        this.update();

        PigeonUtil.broadcast(new GrantAddPayload(this.id, grant.getId()));
    }

    public void removeGrant(Grant grant, UUID removedBy, Long removedAt, String removedReason) {
        if (grant.isDefault())
            return;

        grant.remove(removedBy, removedAt, removedReason);
        this.update();

        PigeonUtil.broadcast(new GrantRemovePayload(this.id, grant.getId()));
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

    @Getter @Setter
    public static class Options {

        private boolean receivingPartyRequests = true;
        private boolean receivingFriendRequests = true;
        private boolean receivingPublicChat = true;
        private boolean receivingConversations = true;
        private boolean receivingMessageSounds = true;

        public Options() {
        }

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

    @Getter @Setter
    public static class Staff {

        private String twoFactorKey = "";
        private boolean receivingStaffMessages = true;
        private boolean locked;

        public Staff() {
        }

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