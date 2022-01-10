package cc.minetale.commonlib.profile;

import cc.minetale.commonlib.CommonLib;
import cc.minetale.commonlib.cache.ProfileCache;
import cc.minetale.commonlib.grant.Grant;
import cc.minetale.commonlib.pigeon.payloads.grant.GrantAddPayload;
import cc.minetale.commonlib.pigeon.payloads.grant.GrantRemovePayload;
import cc.minetale.commonlib.pigeon.payloads.punishment.PunishmentAddPayload;
import cc.minetale.commonlib.pigeon.payloads.punishment.PunishmentRemovePayload;
import cc.minetale.commonlib.punishment.Punishment;
import cc.minetale.commonlib.punishment.PunishmentType;
import cc.minetale.commonlib.util.Database;
import cc.minetale.commonlib.util.PigeonUtil;
import com.google.gson.annotations.SerializedName;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import org.bson.Document;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentSkipListMap;

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

    public CompletableFuture<DeleteResult> delete() {
        return new CompletableFuture<DeleteResult>()
                .completeAsync(() -> Database.getProfilesCollection()
                        .deleteOne(
                                Filters.eq(this.uuid.toString())
                        ));
    }

    public CompletableFuture<UpdateResult> save() {
        return new CompletableFuture<UpdateResult>()
                .completeAsync(() -> Database.getProfilesCollection()
                        .replaceOne(
                                Filters.eq(this.uuid.toString()),
                                Document.parse(CommonLib.getGson().toJson(this)),
                                new ReplaceOptions().upsert(true)
                        ));
    }

    public boolean isIgnoring(Profile profile) {
        return this.ignored.contains(profile.getUuid());
    }

    public int getPunishmentCountByType(PunishmentType type) {
        return (int) this.punishments.stream().filter(punishment -> punishment.getPunishmentType() == type).count();
    }

    public Punishment getActivePunishmentByType(PunishmentType type) {
        for (var punishment : this.punishments)
            if (punishment.getPunishmentType() == type && punishment.isActive())
                return punishment;

        return null;
    }

    public Punishment getActiveBan() {
        var punishment = this.getActivePunishmentByType(PunishmentType.BLACKLIST);

        if (punishment != null)
            return punishment;

        return this.getActivePunishmentByType(PunishmentType.BAN);
    }

    public void checkPunishments() {
        for (var punishment : this.punishments) {
            if (!punishment.isRemoved() && punishment.hasExpired()) {
                this.expirePunishment(punishment);
            }
        }
    }

    public void addPunishment(Punishment punishment) {
        this.punishments.add(punishment);

        punishment.save();
        ProfileCache.updateCacheAsync(this)
                .thenRun(() -> PigeonUtil.broadcast(new PunishmentAddPayload(this.uuid, punishment.getId())));

        for(var provider : CommonLib.getProviders()) {
            provider.addPunishment(punishment);
        }
    }

    public void removePunishment(Punishment punishment, @Nullable UUID removedByUUID, Long removedAt, String removedReason) {
        punishment.remove(removedByUUID, removedAt, removedReason);
        ProfileCache.updateCacheAsync(this)
                .thenRun(() -> PigeonUtil.broadcast(new PunishmentRemovePayload(this.uuid, punishment.getId())));

        for(var provider : CommonLib.getProviders()) {
            provider.removePunishment(punishment);
        }
    }

    public void expirePunishment(Punishment punishment) {
        punishment.remove(null, punishment.getAddedAt() + punishment.getDuration(), "Punishment Expired");

        // TODO -> Payload and update cache

        for(var provider : CommonLib.getProviders()) {
            provider.expirePunishment(punishment);
        }
    }

    public List<Grant> getSortedGrants() {
        var activeGrants = new ArrayList<>(this.grants);

        var sorter = (Comparator<Grant>) (grant1, grant2) -> Boolean.compare(grant2.isActive(), grant1.isActive());
        activeGrants.sort(sorter.thenComparingInt(grant -> grant.getRank().getWeight()));

        return activeGrants;
    }

    public void checkGrants() {
        var expiredGrants = new ArrayList<Grant>();

        for(var grant : this.grants) {
            if(!grant.isRemoved() && grant.hasExpired()) {
                expiredGrants.add(grant);
            }
        }

        if(!expiredGrants.isEmpty()) {
            for(var grant : expiredGrants) {
                expireGrant(grant);
            }

            ProfileCache.updateCache(this);
        }

        this.activateNextGrant();
    }

    public void activateNextGrant() {
        var activeGrants = new ConcurrentSkipListMap<Integer, Grant>();

        for(var grant : this.grants) {
            if(grant.isActive()) {
                activeGrants.put(grant.getRank().getWeight(), grant);
            }
        }

        var grantEntry = activeGrants.firstEntry();

        if(grantEntry != null) {
            this.grant = grantEntry.getValue();
        } else {
            this.grant = Grant.DEFAULT_GRANT;
        }
    }

    public void addGrant(Grant grant) {
        if (grant.isDefault()) return;

        this.grants.add(grant);

        grant.save()
                .thenRun(() -> ProfileCache.updateCacheAsync(this)
                        .thenRun(() -> PigeonUtil.broadcast(new GrantAddPayload(this.uuid, grant.getId()))));

        for(var provider : CommonLib.getProviders()) {
            provider.addGrant(grant);
        }
    }

    public void removeGrant(Grant grant, UUID removedBy, Long removedAt, String removedReason) {
        if (grant.isDefault())
            return;

        grant.remove(removedBy, removedAt, removedReason)
                .thenRun(() -> ProfileCache.updateCacheAsync(this))
                .thenRun(() -> PigeonUtil.broadcast(new GrantRemovePayload(this.uuid, grant.getId())));

        for(var provider : CommonLib.getProviders()) {
            provider.removeGrant(grant);
        }
    }

    public void expireGrant(Grant grant) {
        grant.remove(null, grant.getAddedAt() + grant.getDuration(), "Grant Expired");

        // TODO -> Payload and update cache

        for(var provider : CommonLib.getProviders()) {
            provider.expireGrant(grant);
        }
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