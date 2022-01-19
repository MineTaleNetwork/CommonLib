package cc.minetale.commonlib.punishment;

import cc.minetale.commonlib.CommonLib;
import cc.minetale.commonlib.util.Database;
import cc.minetale.commonlib.util.Message;
import cc.minetale.commonlib.util.ProvidableObject;
import cc.minetale.commonlib.util.TimeUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.CompletableFuture;

@Getter
public class Punishment extends ProvidableObject {

    private final PunishmentType type;

    public Punishment(String id, UUID playerId, UUID addedById, long addedAt, String addedReason, long duration, PunishmentType type) {
        super(id, playerId, addedById, addedAt, addedReason, duration);

        this.type = type;
    }

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
                        try {
                            return CommonLib.getMapper().readValue(document.toJson(), Punishment.class);
                        } catch (JsonProcessingException e) {
                            e.printStackTrace();
                        }
                    }

                    return null;
                });
    }

    public static CompletableFuture<@NotNull List<Punishment>> getPunishments(UUID uuid) {
        return new CompletableFuture<List<Punishment>>()
                .completeAsync(() -> {
                    var punishments = new ArrayList<Punishment>();

                    for (var document : Database.getPunishmentsCollection().find(Filters.eq("playerId", uuid.toString()))) {
                        try {
                            punishments.add(CommonLib.getMapper().readValue(document.toJson(), Punishment.class));
                        } catch (JsonProcessingException e) {
                            e.printStackTrace();
                        }
                    }

                    return punishments;
                });
    }

    public CompletableFuture<UpdateResult> save() {
        return new CompletableFuture<UpdateResult>()
                .completeAsync(() -> {
                    try {
                        return Database.getPunishmentsCollection()
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
                .completeAsync(() -> Database.getPunishmentsCollection()
                        .deleteOne(
                                Filters.eq("_id", getId())
                        ));
    }

}