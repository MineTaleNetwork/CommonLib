package cc.minetale.commonlib.grant;

import cc.minetale.commonlib.CommonLib;
import cc.minetale.commonlib.rank.RankAPI;
import cc.minetale.commonlib.util.Util;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.UUID;

@Getter @Setter
public class Grant {

    @Getter private static final MongoCollection<Document> collection = CommonLib.getCommonLib().getMongoDatabase().getCollection("grants");

    public static Grant getDefaultGrant(UUID playerUUID) {
        return new Grant(playerUUID);
    }

    private String id;
    private UUID playerUUID;
    private UUID rankUUID;
    private UUID addedByUUID;
    private long addedAt;
    private String addedReason;
    private long duration;
    private UUID removedByUUID;
    private long removedAt;
    private String removedReason;
    private boolean removed;
    @Accessors(fluent = true)
    private final GrantAPI api;

    public Grant() {
        this.api = new GrantAPI(this);
    }

    public Grant(UUID playerUUID, UUID rankUUID, UUID addedByUUID, long addedAt, String addedReason, long duration) {
        this.id = Util.generateId();
        this.playerUUID = playerUUID;
        this.rankUUID = rankUUID;
        this.addedByUUID = addedByUUID;
        this.addedAt = addedAt;
        this.addedReason = addedReason;
        this.duration = duration;
        this.api = new GrantAPI(this);
    }

    public Grant(UUID playerUUID) {
        this.id = "DEFAULT";
        this.playerUUID = playerUUID;
        this.rankUUID = RankAPI.getDefaultRank().getUuid();
        this.addedByUUID = null;
        this.addedAt = 0;
        this.addedReason = "Default";
        this.duration = Integer.MAX_VALUE;
        this.api = new GrantAPI(this);
    }

    private Grant(Document document) {
        this.id = document.getString("_id");
        this.api = new GrantAPI(this);
        this.load(document);
    }

    public static Grant fromDocument(@NotNull Document document) {
        return new Grant(document);
    }

    public static Grant getGrant(String id) {
        var document = collection.find(Filters.eq("_id", id)).first();

        if (document != null)
            return new Grant(document);

        return null;
    }

    public void delete() {
        collection.deleteOne(Filters.eq("_id", this.id));
    }

    private void load(@Nullable Document document) {
        document = (document != null) ? document : collection.find(Filters.eq(this.getId())).first();

        if (document != null) {
            this.id = document.getString("_id");

            this.playerUUID = UUID.fromString(document.getString("playerUUID"));
            this.rankUUID = UUID.fromString(document.getString("rankUUID"));

            this.addedByUUID = document.getString("addedByUUID") != null ? UUID.fromString(document.getString("addedByUUID")) : null;
            this.addedAt = document.getLong("addedAt");
            this.addedReason = document.getString("addedReason");
            this.duration = document.getLong("duration");

            this.removed = document.getBoolean("removed");

            if (this.removed) {
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
        document.put("rankUUID", this.rankUUID.toString());

        document.put("addedByUUID", this.addedByUUID != null ? this.addedByUUID.toString() : null);
        document.put("addedAt", this.addedAt);
        document.put("addedReason", this.addedReason);
        document.put("duration", this.duration);

        document.put("removed", this.removed);
        document.put("removedAt", Objects.requireNonNullElse(this.removedAt, 0L));
        document.put("removedByUUID", this.removedByUUID != null ? this.removedByUUID.toString() : null);
        document.put("removedReason", this.removedReason != null ? this.removedReason : null);

        return document;
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof Grant && ((Grant) object).rankUUID.equals(this.rankUUID);
    }

}