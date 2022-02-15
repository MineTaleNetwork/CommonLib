package cc.minetale.commonlib.punishment;

import cc.minetale.commonlib.util.*;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.CompletableFuture;

@Getter
@Setter
public class Punishment extends ProvidableObject {

    private PunishmentType type;

    public Punishment(String id, UUID playerId, UUID addedById, long addedAt, String addedReason, long duration, PunishmentType type) {
        super(id, playerId, addedById, addedAt, addedReason, duration);

        this.type = type;
    }

    /**
     * Default constructor used for Jackson.
     */
    public Punishment() {}

    public String getContext() {
        if (isPermanent()) {
            return (this.isRemoved() ? type.getUndoContext() : "permanently " + type.getContext());
        } else {
            return (this.isRemoved() ? type.getUndoContext() : "temporarily " + type.getContext());
        }
    }

    public List<Component> getPunishmentMessage() {
        return Arrays.asList(
                Message.chatSeparator(),
                Component.text(
                        "You are " + getContext() + (!isPermanent() ? " for " + getTimeRemaining() : "") + ".", NamedTextColor.RED
                ),
                Component.empty(),
                Component.text()
                        .append(
                                Component.text("Reason: ", NamedTextColor.GRAY),
                                Component.text(getAddedReason(), NamedTextColor.WHITE)
                        ).build(),
                Component.text()
                        .append(
                                Component.text("Added On: ", NamedTextColor.GRAY),
                                Component.text(TimeUtil.dateToString(new Date(getAddedAt()), true), NamedTextColor.WHITE)
                        ).build(),
                Component.text()
                        .append(
                                Component.text("Punishment ID: ", NamedTextColor.GRAY),
                                Component.text(getId(), NamedTextColor.WHITE)
                        ).build(),
                Component.empty(),
                Component.text()
                        .append(
                                Component.text("Appeal At: ", NamedTextColor.GRAY),
                                Component.text("https://minetale.cc/discord", NamedTextColor.AQUA, TextDecoration.UNDERLINED)
                        ).build(),
                Message.chatSeparator()
                );
    }

    public static CompletableFuture<@Nullable Punishment> getPunishment(String id) {
        return new CompletableFuture<Punishment>()
                .completeAsync(() -> {
                    var document = Database.getPunishmentsCollection().find(Filters.eq("_id", id)).first();

                    if (document != null) {
                        return JsonUtil.readFromJson(document.toJson(), Punishment.class);
                    }

                    return null;
                });
    }

    public static CompletableFuture<ArrayList<Punishment>> getPunishments(UUID uuid) {
        return new CompletableFuture<ArrayList<Punishment>>()
                .completeAsync(() -> {
                    var punishments = new ArrayList<Punishment>();

                    for (var document : Database.getPunishmentsCollection().find(Filters.eq("playerId", uuid.toString()))) {
                        punishments.add(JsonUtil.readFromJson(document.toJson(), Punishment.class));
                    }

                    return punishments;
                });
    }

    public CompletableFuture<UpdateResult> save() {
        return new CompletableFuture<UpdateResult>()
                .completeAsync(() -> {
                    var document = BsonUtil.writeToBson(this);

                    if (document != null) {
                        return Database.getPunishmentsCollection()
                                .replaceOne(
                                        Filters.eq(getId()),
                                        document,
                                        new ReplaceOptions().upsert(true)
                                );
                    }

                    return UpdateResult.unacknowledged();
                });
    }

    public CompletableFuture<DeleteResult> delete() {
        return new CompletableFuture<DeleteResult>()
                .completeAsync(() -> Database.getPunishmentsCollection()
                        .deleteOne(
                                Filters.eq(getId())
                        ));
    }

}