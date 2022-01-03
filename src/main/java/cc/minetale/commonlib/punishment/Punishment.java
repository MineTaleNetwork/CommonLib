package cc.minetale.commonlib.punishment;

import cc.minetale.commonlib.CommonLib;
import cc.minetale.commonlib.profile.Profile;
import cc.minetale.commonlib.util.Database;
import cc.minetale.commonlib.util.MC;
import cc.minetale.commonlib.util.StringUtil;
import cc.minetale.commonlib.util.TimeUtil;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Getter @Setter
public class Punishment {

    @SerializedName("_id")
    private String id;
    private UUID playerId;
    private PunishmentType punishmentType;
    private UUID addedById;
    private long addedAt;
    private String addedReason;
    private long duration;
    private UUID removedById;
    private long removedAt;
    private String removedReason;
    private boolean removed;

    public static final Type LIST_TYPE_TOKEN = new TypeToken<List<Punishment>>(){}.getType();

    public Punishment(String id, UUID playerId, PunishmentType punishmentType, UUID addedById, long addedAt, String addedReason, long duration) {
        this.id = id != null ? id : StringUtil.generateId();
        this.playerId = playerId;
        this.punishmentType = punishmentType;
        this.addedById = addedById;
        this.addedAt = addedAt;
        this.addedReason = addedReason;
        this.duration = duration;
    }

    public static CompletableFuture<@NotNull ArrayList<Punishment>> getPunishments(Profile profile) {
        return getPunishments(profile.getUuid());
    }

    public static CompletableFuture<@NotNull ArrayList<Punishment>> getPunishments(UUID uuid) {
        return new CompletableFuture<ArrayList<Punishment>>()
                .completeAsync(() -> {
                    var punishments = new ArrayList<Punishment>();

                    for (var document : Database.getPunishmentsCollection().find(Filters.eq("playerId", uuid.toString()))) {
                        punishments.add(CommonLib.getGson().fromJson(document.toJson(), Punishment.class));
                    }

                    return punishments;
                });
    }

    public static CompletableFuture<@Nullable Punishment> getPunishment(String id) {
        return new CompletableFuture<Punishment>()
                .completeAsync(() -> {
                    var document = Database.getPunishmentsCollection().find(Filters.eq("_id", id)).first();

                    if (document != null)
                        return CommonLib.getGson().fromJson(document.toJson(), Punishment.class);

                    return null;
                });
    }

    public CompletableFuture<UpdateResult> remove(@Nullable UUID removedBy, Long removedAt, String removedReason) {
        this.removed = true;
        this.removedAt = removedAt;
        this.removedById = removedBy;
        this.removedReason = removedReason;

        return this.save();
    }

    public CompletableFuture<DeleteResult> delete() {
        return new CompletableFuture<DeleteResult>()
                .completeAsync(() -> Database.getPunishmentsCollection()
                        .deleteOne(
                                Filters.eq("_id", this.id)
                        ));
    }

    public CompletableFuture<UpdateResult> save() {
        return new CompletableFuture<UpdateResult>()
                .completeAsync(() -> Database.getPunishmentsCollection()
                        .replaceOne(
                                Filters.eq("_id", this.id),
                                Document.parse(CommonLib.getGson().toJson(this)),
                                new ReplaceOptions().upsert(true)
                        ));
    }

    public boolean isPermanent() {
        return this.punishmentType == PunishmentType.BLACKLIST || this.duration == Integer.MAX_VALUE;
    }

    public boolean isActive() {
        return !this.removed && (this.isPermanent() || !this.hasExpired());
    }

    public long getMillisRemaining() {
        return (this.addedAt + this.duration) - System.currentTimeMillis();
    }

    public boolean hasExpired() {
        return (!this.isPermanent()) && (System.currentTimeMillis() >= this.addedAt + this.duration);
    }

    public String getDurationText() {
        return (this.isPermanent() || this.duration == 0) ? "Permanent" : TimeUtil.millisToRoundedTime(this.duration);
    }

    public String getTimeRemaining() {
        if (this.removed) {
            return "Removed";
        }

        if (this.isPermanent()) {
            return "Permanent";
        }

        if (hasExpired()) {
            return "Expired";
        }

        return TimeUtil.millisToRoundedTime((this.addedAt + this.duration) - System.currentTimeMillis());
    }

    public String getContext() {
        if (!(this.punishmentType.isBan() || this.punishmentType == PunishmentType.MUTE)) {
            return this.removed ? this.punishmentType.getUndoContext() : this.punishmentType.getContext();
        }

        if (this.isPermanent()) {
            return (this.removed ? this.punishmentType.getUndoContext() : "permanently " + this.punishmentType.getContext());
        } else {
            return (this.removed ? this.punishmentType.getUndoContext() : "temporarily " + this.punishmentType.getContext());
        }
    }

    public List<Component> getPunishmentMessage() {
        switch (this.punishmentType) {
            case BLACKLIST, BAN, MUTE -> {
                return Arrays.asList(
                        MC.SEPARATOR_80,
                        Component.text(
                                "You are " +
                                        this.getContext() +
                                        (!this.isPermanent() ? " for " + this.getTimeRemaining() : "") +
                                        ".", NamedTextColor.RED
                        ),
                        Component.empty(),
                        Component.text()
                                .append(
                                        Component.text("Reason: ", NamedTextColor.GRAY),
                                        Component.text(this.addedReason, NamedTextColor.WHITE)
                                ).build(),
                        Component.text()
                                .append(
                                        Component.text("Added On: ", NamedTextColor.GRAY),
                                        Component.text(TimeUtil.dateToString(new Date(this.getAddedAt()), true), NamedTextColor.WHITE)
                                ).build(),
                        Component.text()
                                .append(
                                        Component.text("Punishment ID: ", NamedTextColor.GRAY),
                                        Component.text(this.id, NamedTextColor.WHITE)
                                ).build(),
                        Component.empty(),
                        Component.text()
                                .append(
                                        Component.text("Appeal At: ", NamedTextColor.GRAY),
                                        Component.text("https://minetale.cc/discord", NamedTextColor.AQUA, TextDecoration.UNDERLINED)
                                ).build(),
                        MC.SEPARATOR_80
                );
            }
            case WARN -> {
                return Arrays.asList(
                        MC.SEPARATOR_80,
                        Component.text("You have been warned", NamedTextColor.RED),
                        Component.text()
                                .append(
                                        Component.text("Reason: ", NamedTextColor.GRAY),
                                        Component.text(this.addedReason, NamedTextColor.WHITE)
                                ).build(),
                        Component.empty(),
                        Component.text("Failure to correct your actions will result in a punishment.", NamedTextColor.GRAY),
                        MC.SEPARATOR_80
                );
            }
        }

        return Collections.emptyList();
    }

    @Override
    public boolean equals(Object object) {
        return this == object || object instanceof Punishment other && other.getId().equalsIgnoreCase(this.id);
    }

    public NamedTextColor getPunishmentColor() {
        switch (this.punishmentType) {
            case BLACKLIST -> { return NamedTextColor.RED; }
            case BAN -> { return NamedTextColor.GOLD; }
            case MUTE -> { return NamedTextColor.GREEN; }
            case WARN -> { return NamedTextColor.BLUE; }
            default -> { return NamedTextColor.WHITE; }
        }
    }

    @Override
    public int hashCode() {
        return this.id.hashCode();
    }

}