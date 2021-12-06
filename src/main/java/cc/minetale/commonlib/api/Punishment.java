package cc.minetale.commonlib.api;

import cc.minetale.commonlib.util.Database;
import cc.minetale.commonlib.util.MC;
import cc.minetale.commonlib.util.TimeUtil;
import cc.minetale.commonlib.util.StringUtil;
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

@Getter @Setter @Builder @AllArgsConstructor
public class Punishment {

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

    public Punishment(UUID playerId, Type type, UUID addedById, long addedAt, String addedReason, long duration) {
        this.id = StringUtil.generateId();
        this.playerId = playerId;
        this.type = type;
        this.addedById = addedById;
        this.addedAt = addedAt;
        this.addedReason = addedReason;
        this.duration = duration;
    }

    public static @Nullable Punishment fromDocument(Document document) {
        if(document != null) {
            var punishment = Punishment.builder()
                    .id(document.getString("_id"))
                    .playerId(UUID.fromString(document.getString("playerId")))
                    .type(Type.valueOf(document.getString("type")))
                    .addedById(document.getString("addedById") != null ? UUID.fromString(document.getString("addedById")) : null)
                    .addedAt(document.getLong("addedAt"))
                    .addedReason(document.getString("addedReason"))
                    .duration(document.getLong("duration"))
                    .removed(document.getBoolean("removed"));

            if(punishment.removed) {
                punishment.removedAt(document.getLong("removedAt"))
                        .removedById(document.getString("removedById") != null ? UUID.fromString(document.getString("removedById")) : null)
                        .removedReason(document.getString("removedReason"));
            }
            
            return punishment.build();
        }
        
        return null;
    }

    public static @Nullable Punishment getPunishment(String id) {
        var document = Database.getDatabase().getPunishmentsCollection().find(Filters.eq("_id", id)).first();

        if (document != null)
            return fromDocument(document);

        return null;
    }

    public void delete() {
        Database.getDatabase().getPunishmentsCollection().deleteOne(Filters.eq("_id", this.id));
    }

    public void save() {
        Database.getDatabase().getPunishmentsCollection().replaceOne(Filters.eq("_id", this.id), toDocument(), new ReplaceOptions().upsert(true));
    }

    public Document toDocument() {
        var document = new Document();

        document.put("_id", this.id);
        document.put("playerId", this.playerId.toString());
        document.put("type", this.type.name());

        document.put("addedById", this.addedById != null ? this.addedById.toString() : null);
        document.put("addedAt", this.addedAt);
        document.put("addedReason", this.addedReason);
        document.put("duration", this.duration);

        document.put("removed", this.removed);
        document.put("removedAt", Objects.requireNonNullElse(this.removedAt, 0L));
        document.put("removedById", this.removedById != null ? this.removedById.toString() : null);
        document.put("removedReason", this.removedReason != null ? this.removedReason : null);

        return document;
    }

    /**
     * Returns if the Punishment is permanent or not.
     */
    public boolean isPermanent() {
        return this.type == Punishment.Type.BLACKLIST || this.duration == Integer.MAX_VALUE;
    }

    /**
     * Returns if the Punishment is active or not.
     */
    public boolean isActive() {
        return !this.removed && (this.isPermanent() || this.getMillisRemaining() < 0L);
    }

    /**
     * Returns the remaining amount of milliseconds of the Punishment.
     */
    public long getMillisRemaining() {
        return (this.addedAt + this.duration) - System.currentTimeMillis();
    }

    /**
     * Returns if the Punishment has expired or not.
     */
    public boolean hasExpired() {
        return (!this.isPermanent()) && (System.currentTimeMillis() >= this.addedAt + this.duration);
    }

    /**
     * Returns the Duration String.
     */
    public String getDurationText() {
        if (this.isPermanent() || this.duration == 0) {
            return "Permanent";
        } else {
            return TimeUtil.millisToRoundedTime(this.duration);
        }
    }

    /**
     * Returns the Remaining String.
     */
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

    /**
     * Returns the Context String.
     */
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

    /**
     * Gets a list of Components that form the Ban Message.
     */
    public List<Component> getBanMessage(boolean initial) {
        Date date = new Date(this.getAddedAt());

        return Arrays.asList(
                MC.SEPARATOR_80,
                Component.text(
                        (initial ? "You have been " : "You are ") +
                        this.getContext() +
                        (!this.isPermanent() ? " for " + this.getTimeRemaining() : "") +
                        ".", NamedTextColor.RED
                ),
                Component.empty(),
                Component.text()
                        .append(
                                Component.text("Reason: ", NamedTextColor.GRAY)
                        )
                        .append(
                                Component.text(this.addedReason, NamedTextColor.WHITE)
                        ).build(),
                Component.text()
                        .append(
                                Component.text("Added On: ", NamedTextColor.GRAY)
                        )
                        .append(
                                Component.text(TimeUtil.dateToString(date, true), NamedTextColor.WHITE)
                        ).build(),
                Component.text()
                        .append(
                                Component.text("Punishment ID: ", NamedTextColor.GRAY)
                        )
                        .append(
                                Component.text(this.id, NamedTextColor.WHITE)
                        ).build(),
                Component.empty(),
                Component.text()
                        .append(
                                Component.text("Appeal At: ", NamedTextColor.GRAY)
                        )
                        .append(
                                Component.text("https://minetale.cc/discord", NamedTextColor.AQUA, TextDecoration.UNDERLINED)
                        ).build(),
                MC.SEPARATOR_80
        );
    }

    /**
     * Removes a Punishment.
     */
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