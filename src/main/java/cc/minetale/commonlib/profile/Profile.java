package cc.minetale.commonlib.profile;

import cc.minetale.commonlib.CommonLib;
import cc.minetale.commonlib.grant.Grant;
import cc.minetale.commonlib.network.Gamemode;
import cc.minetale.commonlib.pigeon.payloads.profile.ProfileCreatePayload;
import cc.minetale.commonlib.pigeon.payloads.profile.ProfileRequestPayload;
import cc.minetale.commonlib.pigeon.payloads.profile.ProfileUpdatePayload;
import cc.minetale.commonlib.punishment.Punishment;
import cc.minetale.commonlib.util.PigeonUtil;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Getter @Setter
public class Profile {

    @Getter private static final MongoCollection<Document> collection = CommonLib.getCommonLib().getMongoDatabase().getCollection("profiles");

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
    private String searchableName;
    private String currentAddress;
    private String discord;
    private Grant grant;
    private int gold;
    private long firstSeen;
    private long lastSeen;
    private long experience;
    private Map<Gamemode, GamemodeStorage> gamemodeStorages;
    @Accessors(fluent = true)
    private final ProfileAPI api;

    public Profile() {
        this.api = new ProfileAPI(this);
    }

    /**
     * <strong>Usage reserved for Atom.</strong> <br>
     * Use {@linkplain Profile#createProfile(String, UUID)} instead.
     */
    public Profile(String username, UUID id) {
        this.id = id;
        this.name = username;
        this.searchableName = username.toUpperCase();
        this.punishments = new ArrayList<>();
        this.cachedPunishments = new ArrayList<>();
        this.grants = new ArrayList<>();
        this.cachedGrants = new ArrayList<>();
        this.ignored = new ArrayList<>();
        this.friends = new ArrayList<>();
        this.optionsProfile = new Options();
        this.staffProfile = new Staff();
        this.gamemodeStorages = new HashMap<>();
        this.api = new ProfileAPI(this);
        this.load(null);
    }

    private Profile(Document document) {
        this.id = UUID.fromString(document.getString("_id"));
        this.api = new ProfileAPI(this);
        this.load(document);
    }

    public static Profile createProfile(@NotNull String name, @NotNull UUID uuid) {
        var profile = new Profile(name, uuid);
        PigeonUtil.getPigeon()
                .sendTo(new ProfileCreatePayload(profile, null), PigeonUtil.GeneralUnits.ATOM.getUnit());
        return profile;
    }

    public static Profile fromDocument(@NotNull Document document) {
        return new Profile(document);
    }

    public static CompletableFuture<Profile> getProfile(@NotNull UUID id) {
        var future = new CompletableFuture<Profile>();

        PigeonUtil.getPigeon()
                .sendTo(new ProfileRequestPayload(id, payload -> handleProfileRetrieval(
                        future, payload)),
                        PigeonUtil.GeneralUnits.ATOM.getUnit());

        return future;
    }

    public static CompletableFuture<Profile> getProfile(@NotNull String name) {
        var future = new CompletableFuture<Profile>();

        PigeonUtil.getPigeon()
                .sendTo(new ProfileRequestPayload(name, payload -> handleProfileRetrieval(
                        future, payload)),
                        PigeonUtil.GeneralUnits.ATOM.getUnit());

        return future;
    }

    public static CompletableFuture<Profile> getProfile(@Nullable String name, @NotNull UUID id) {
        var future = new CompletableFuture<Profile>();

        PigeonUtil.getPigeon()
                .sendTo(new ProfileRequestPayload(name, id, payload -> handleProfileRetrieval(
                        future, payload)),
                        PigeonUtil.GeneralUnits.ATOM.getUnit());

        return future;
    }

    private static void handleProfileRetrieval(CompletableFuture<Profile> future, ProfileRequestPayload payload) {
        if(payload.getResult().isSuccessful()) {
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
                        PigeonUtil.GeneralUnits.ATOM.getUnit());

        return future;
    }

    public static CompletableFuture<List<Profile>> getProfilesByNames(@NotNull List<String> names) {
        var future = new CompletableFuture<List<Profile>>();

        PigeonUtil.getPigeon()
                .sendTo(ProfileRequestPayload.bulkRequestByNames(names,
                                payload -> future.complete(Objects.requireNonNullElse(payload.getProfiles(), new ArrayList<>()))),
                        PigeonUtil.GeneralUnits.ATOM.getUnit());

        return future;
    }

    public static CompletableFuture<List<Profile>> getProfiles(List<String> names, List<UUID> ids) {
        var future = new CompletableFuture<List<Profile>>();

        PigeonUtil.getPigeon()
                .sendTo(new ProfileRequestPayload(names, ids,
                                payload -> future.complete(Objects.requireNonNullElse(payload.getProfiles(), new ArrayList<>()))),
                        PigeonUtil.GeneralUnits.ATOM.getUnit());

        return future;
    }

    public static CompletableFuture<List<Profile>> getProfiles(Map<UUID, String> info) {
        var future = new CompletableFuture<List<Profile>>();

        PigeonUtil.getPigeon()
                .sendTo(new ProfileRequestPayload(info,
                                payload -> future.complete(Objects.requireNonNullElse(payload.getProfiles(), new ArrayList<>()))),
                        PigeonUtil.GeneralUnits.ATOM.getUnit());

        return future;
    }

    /**
     * Updates the {@linkplain Profile} on Atom. <br>
     * Use this to make sure a {@linkplain Profile} is up-to-date when it changes or leaves a server.
     */
    public CompletableFuture<ProfileQueryResult> update() {
        var future = new CompletableFuture<ProfileQueryResult>();

        PigeonUtil.getPigeon()
                .sendTo(new ProfileUpdatePayload(this, payload -> future.complete(payload.getResult())), PigeonUtil.GeneralUnits.ATOM.getUnit());

        return future;
    }

    public void reloadGrant() {
        this.grant = this.api.getActiveGrant();
    }

    private void load(@Nullable Document document) {
        document = (document != null) ? document : collection.find(Filters.eq(this.getId().toString())).first();

        if (document != null) {
            if (this.name == null) {
                this.name = document.getString("name");
            }

            this.searchableName = this.name.toUpperCase();
            this.firstSeen = document.getLong("firstSeen");
            this.lastSeen = document.getLong("lastSeen");
            this.currentAddress = document.getString("currentAddress");

            this.optionsProfile = new Options(document.get("optionsProfile", Document.class));
            this.staffProfile = new Staff(document.get("staffProfile", Document.class));

            this.discord = document.getString("discordId");

            this.gold = document.getInteger("gold");

            this.experience = document.getLong("experience");

            this.gamemodeStorages = new HashMap<>();
            for (Map.Entry<String, Object> ent : document.get("gamemodeStorages", Document.class).entrySet()) {
                this.gamemodeStorages.put(Gamemode.getByName(ent.getKey()), new GamemodeStorage(ent.getKey(), (Document) ent.getValue()));
            }

            this.ignored = new ArrayList<>();
            for (String ignored : document.getList("ignored", String.class)) {
                UUID ignoredUUID = UUID.fromString(ignored);
                this.ignored.add(ignoredUUID);
            }

            this.friends = new ArrayList<>();
            for (String friends : document.getList("friends", String.class)) {
                UUID friendUUID = UUID.fromString(friends);
                this.friends.add(friendUUID);
            }

            this.punishments = new ArrayList<>();
            this.punishments.addAll(document.getList("punishments", String.class));

            this.cachedPunishments = new ArrayList<>();
            this.cachedPunishments.addAll(this.punishments.stream().map(Punishment::getPunishment).filter(Objects::nonNull).collect(Collectors.toList()));

            this.grants = new ArrayList<>();
            this.grants.addAll(document.getList("grants", String.class));

            this.cachedGrants = new ArrayList<>();
            this.cachedGrants.addAll(this.grants.stream().map(Grant::getGrant).filter(Objects::nonNull).collect(Collectors.toList()));
        }

        this.api.validatePunishments();
        this.reloadGrant();
    }

    /**
     * <strong>Usage reserved for Atom.</strong>
     */
    public Document toDocument() {
        var document = new Document();
        document.put("_id", this.id.toString());
        document.put("name", this.name);
        document.put("searchableName", this.name.toUpperCase());
        document.put("firstSeen", Objects.requireNonNullElse(this.firstSeen, System.currentTimeMillis()));
        document.put("lastSeen", Objects.requireNonNullElse(this.firstSeen, System.currentTimeMillis()));
        document.put("currentAddress", this.currentAddress);

        document.put("optionsProfile", this.optionsProfile.toDocument());
        document.put("staffProfile", this.staffProfile.toDocument());

        document.put("discord", this.discord);

        document.put("gold", this.gold);
        document.put("experience", this.experience);

        var storagesDocument = new Document();
        for (Map.Entry<Gamemode, GamemodeStorage> ent : this.gamemodeStorages.entrySet()) {
            var storageDocument = new Document();
            for (Map.Entry<String, GamemodeStorage.StorageValue> valueEnt : ent.getValue().getValues().entrySet()) {
                storageDocument.append(valueEnt.getKey(),
                        new Document("value", valueEnt.getValue().getValue())
                                .append("isWritable", valueEnt.getValue().isWritable()));
            }
            storagesDocument.put(ent.getKey().getName(), storageDocument);
        }
        document.put("gamemodeStorages", storagesDocument);

        document.put("grants", this.grants);

        document.put("punishments", this.punishments);

        document.put("ignored", this.ignored);

        document.put("friends", this.friends);

        return document;
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof Profile) {
            Profile other = (Profile) object;

            return other.id.equals(this.id);
        }

        return false;
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
        private int visibilityIndex = 0;

        public Options() {
        }

        public Options(Document document) {
            this.receivingPartyRequests = document.getBoolean("receivingPartyRequests");
            this.receivingFriendRequests = document.getBoolean("receivingFriendRequests");
            this.receivingPublicChat = document.getBoolean("receivingPublicChat");
            this.receivingConversations = document.getBoolean("receivingConversations");
            this.receivingMessageSounds = document.getBoolean("receivingMessageSounds");
            this.visibilityIndex = document.getInteger("visibilityIndex");
        }

        public Document toDocument() {
            return new Document()
                    .append("receivingPartyRequests", this.receivingPartyRequests)
                    .append("receivingFriendRequests", this.receivingFriendRequests)
                    .append("receivingPublicChat", this.receivingPublicChat)
                    .append("receivingConversations", this.receivingConversations)
                    .append("receivingMessageSounds", this.receivingMessageSounds)
                    .append("visibilityIndex", this.visibilityIndex);
        }

    }

    @Getter @Setter
    public static class Staff {

        private String twoFactorKey = "";
        private boolean receivingStaffMessages = true;
        private boolean twoFactor;
        private boolean locked;
        private boolean operator;

        public Staff() {
        }

        public Staff(Document document) {
            this.twoFactorKey = document.getString("twoFactorKey");
            this.receivingStaffMessages = document.getBoolean("receivingStaffMessages");
            this.twoFactor = document.getBoolean("twoFactor");
            this.locked = document.getBoolean("locked");
            this.operator = document.getBoolean("operator");
        }

        public Document toDocument() {
            return new Document()
                    .append("twoFactorKey", this.twoFactorKey)
                    .append("receivingStaffMessages", this.receivingStaffMessages)
                    .append("twoFactor", this.twoFactor)
                    .append("locked", this.locked)
                    .append("operator", this.operator);
        }

    }

}