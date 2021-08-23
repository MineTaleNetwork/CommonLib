package cc.minetale.commonlib.modules.punishment;

import cc.minetale.commonlib.CommonLib;
import cc.minetale.commonlib.util.Util;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

@Getter @Setter
public class Punishment {

    @Getter private static final MongoCollection<Document> collection = CommonLib.getCommonLib().getMongoDatabase().getCollection("punishments");

    private String id;
    private UUID playerUUID;
    private Type type;
    private UUID addedByUUID;
    private long addedAt;
    private String addedReason;
    private long duration;
    private UUID removedByUUID;
    private Long removedAt;
    private String removedReason;
    private boolean removed;
    @Accessors(fluent = true)
    private final PunishmentAPI api;

    public Punishment() {
        this.api = new PunishmentAPI(this);
    }

    public Punishment(UUID playerUUID, Type type, UUID addedByUUID, long addedAt, String addedReason, long duration) {
        this.id = Util.generateId();
        this.playerUUID = playerUUID;
        this.type = type;
        this.addedByUUID = addedByUUID;
        this.addedAt = addedAt;
        this.addedReason = addedReason;
        this.duration = duration;
        this.api = new PunishmentAPI(this);
    }

    private Punishment(String id) {
        this.id = id;
        this.api = new PunishmentAPI(this);
    }

    private Punishment(Document document) {
        this.id = document.getString("_id");
        this.api = new PunishmentAPI(this);
        this.load(document);
    }

    public static Punishment fromDocument(@NotNull Document document) {
        return new Punishment(document);
    }

    public static Punishment getPunishment(String id) {
        var document = collection.find(Filters.eq("_id", id)).first();

        if (document != null)
            return new Punishment(document);

        return null;
    }

    public void delete() {
        collection.deleteOne(Filters.eq("_id", this.id));
    }

    private void load(@Nullable Document document) {
        document = (document != null) ? document : collection.find(Filters.eq(this.getId())).first();

        if(document != null) {
            this.playerUUID = UUID.fromString(document.getString("playerUUID"));
            this.type = Type.valueOf(document.getString("type"));

            this.addedByUUID = document.getString("addedByUUID") != null ? UUID.fromString(document.getString("addedByUUID")) : null;
            this.addedAt = document.getLong("addedAt");
            this.addedReason = document.getString("addedReason");
            this.duration = document.getLong("duration");

            this.removed = document.getBoolean("removed");

            if(this.removed) {
                this.removedAt = document.getLong("removedAt");
                this.removedByUUID = document.getString("removedByUUID") != null ? UUID.fromString(document.getString("removedByUUID")) : null;
                this.removedReason = document.getString("removedReason");
            }
        }
    }

    public void save() {
        collection.replaceOne(Filters.eq("_id", this.id), toDocument(), new ReplaceOptions().upsert(true));
    }

    public Document toDocument() {
        var document = new Document();

        document.put("_id", this.id);
        document.put("playerUUID", this.playerUUID.toString());
        document.put("type", this.type.name());

        document.put("addedByUUID", this.addedByUUID != null ? this.addedByUUID.toString() : null);
        document.put("addedAt", this.addedAt);
        document.put("addedReason", this.addedReason);
        document.put("duration", this.duration);

        document.put("removed", this.removed);
        document.put("removedAt", this.removedAt != null ? this.removedAt : null);
        document.put("removedByUUID", this.removedByUUID != null ? this.removedByUUID.toString() : null);
        document.put("removedReason", this.removedReason != null ? this.removedReason : null);

        return document;
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof Punishment && ((Punishment) object).id.equals(this.id);
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