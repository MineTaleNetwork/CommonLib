package cc.minetale.commonlib.grant;

import cc.minetale.commonlib.CommonLib;
import cc.minetale.commonlib.util.Database;
import cc.minetale.commonlib.util.ProvidableObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Getter
@Setter
public class Grant extends ProvidableObject {

    private Rank rank;

    public static final Grant DEFAULT_GRANT = new Grant("DEFAULT", null, null, 0L, "Default", Integer.MAX_VALUE,  Rank.MEMBER);

    public Grant(String id, UUID playerId, UUID addedById, long addedAt, String addedReason, long duration, Rank rank) {
        super(id, playerId, addedById, addedAt, addedReason, duration);

        this.rank = rank;
    }

    /**
     * Default constructor used for Jackson.
     */
    public Grant() {}

    public boolean isDefault() {
        return rank == Rank.MEMBER;
    }

    public static CompletableFuture<Grant> getGrant(String id) {
        return new CompletableFuture<Grant>()
                .completeAsync(() -> {
                    var document = Database.getGrantsCollection().find(Filters.eq("_id", id)).first();

                    if (document != null) {
                        try {
                            return CommonLib.getMapper().readValue(document.toJson(), Grant.class);
                        } catch (JsonProcessingException e) {
                            e.printStackTrace();
                        }
                    }

                    return null;
                });
    }

    public static CompletableFuture<List<Grant>> getGrants(UUID uuid) {
        return new CompletableFuture<List<Grant>>()
                .completeAsync(() -> {
                    var grants = new ArrayList<Grant>();

                    for (var document : Database.getGrantsCollection().find(Filters.eq("playerId", uuid.toString()))) {
                        try {
                            grants.add(CommonLib.getMapper().readValue(document.toJson(), Grant.class));
                        } catch (JsonProcessingException e) {
                            e.printStackTrace();
                            return null;
                        }
                    }

                    return grants;
                });
    }

    public CompletableFuture<UpdateResult> save() {
        return new CompletableFuture<UpdateResult>()
                .completeAsync(() -> {
                    try {
                        return Database.getGrantsCollection()
                                .replaceOne(
                                        Filters.eq("_id", getId()),
                                        Document.parse(CommonLib.getMapper().writeValueAsString(this)),
                                        new ReplaceOptions().upsert(true)
                                );
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }

                    return UpdateResult.unacknowledged();
                });
    }

    public CompletableFuture<DeleteResult> delete() {
        return new CompletableFuture<DeleteResult>()
                .completeAsync(() -> Database.getGrantsCollection()
                        .deleteOne(
                                Filters.eq("_id", getId())
                        ));
    }

}