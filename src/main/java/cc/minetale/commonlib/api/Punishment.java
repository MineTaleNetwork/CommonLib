package cc.minetale.commonlib.api;

import cc.minetale.commonlib.profile.Profile;
import cc.minetale.commonlib.util.*;
import com.google.gson.annotations.SerializedName;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bson.Document;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@Getter @Setter
public class Punishment {

    @SerializedName("_id")
    private String id;
    private UUID playerId;
    private Type type;
    private UUID addedById;
    private long addedAt;
    private String addedReason;
    private long duration;
    private UUID removedById;
    private long removedAt;
    private String removedReason;
    private boolean removed;

    public static Punishment createPunishment(String id, UUID playerId, Type type, UUID addedById, long addedAt, String addedReason, long duration) {
        var punishment = new Punishment();

        punishment.setId(id != null ? id : StringUtil.generateId());
        punishment.setPlayerId(playerId);
        punishment.setType(type);
        punishment.setAddedById(addedById);
        punishment.setAddedAt(addedAt);
        punishment.setAddedReason(addedReason);
        punishment.setDuration(duration);

        return punishment;
    }

    public static List<Punishment> getPunishments(Profile profile) {
        return getPunishments(profile.getUuid());
    }

    public static List<Punishment> getPunishments(UUID uuid) {
        var punishments = new ArrayList<Punishment>();

        for (var document : Database.getPunishmentsCollection().find(Filters.eq("playerId", uuid))) {
            punishments.add(JSONUtil.fromDocument(document, Punishment.class));
        }

        return punishments;
    }

    public static @Nullable Punishment getPunishment(String id) {
        var document = Database.getPunishmentsCollection().find(Filters.eq("_id", id)).first();

        if (document != null)
            return JSONUtil.fromDocument(document, Punishment.class);

        return null;
    }

    public void delete() {
        Database.getPunishmentsCollection().deleteOne(Filters.eq("_id", this.id));
    }

    public void save() {
        Database.getPunishmentsCollection().replaceOne(Filters.eq("_id", this.id), JSONUtil.toDocument(this), new ReplaceOptions().upsert(true));
    }

    public boolean isPermanent() {
        return this.type == Punishment.Type.BLACKLIST || this.duration == Integer.MAX_VALUE;
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
        if (this.isPermanent() || this.duration == 0) {
            return "Permanent";
        } else {
            return TimeUtil.millisToRoundedTime(this.duration);
        }
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
        if (!(this.type == Punishment.Type.BAN || this.type == Punishment.Type.MUTE)) {
            return this.removed ? this.type.getUndoContext() : this.type.getContext();
        }

        if (this.isPermanent()) {
            return (this.removed ? this.type.getUndoContext() : "permanently " + this.type.getContext());
        } else {
            return (this.removed ? this.type.getUndoContext() : "temporarily " + this.type.getContext());
        }
    }

    public List<Component> getPunishmentMessage() {
        switch (this.type) {
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

    public void remove(@Nullable UUID removedBy, Long removedAt, String removedReason) {
        this.removed = true;
        this.removedAt = removedAt;
        this.removedById = removedBy;
        this.removedReason = removedReason;
        
        this.save();
    }

    @Override
    public boolean equals(Object object) {
        return this == object || object instanceof Punishment other && other.getId().equalsIgnoreCase(this.id);
    }

    public NamedTextColor getPunishmentColor() {
        switch (this.type) {
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

    @Getter
    @AllArgsConstructor
    public enum Type {
        BLACKLIST("Blacklist", "blacklisted", "unblacklisted", true, true),
        BAN("Ban", "banned", "unbanned", true, true),
        MUTE("Mute", "muted", "unmuted", false, true),
        WARN("Warning", "warned", null, false, false);

        private final String readable;
        private final String context;
        private final String undoContext;
        private final boolean ban;
        private final boolean removable;
    }

}