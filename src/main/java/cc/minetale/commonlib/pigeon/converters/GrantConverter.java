package cc.minetale.commonlib.pigeon.converters;

import cc.minetale.commonlib.grant.Grant;
import cc.minetale.commonlib.grant.Rank;
import cc.minetale.commonlib.util.JsonChain;
import cc.minetale.pigeon.Converter;
import cc.minetale.pigeon.converters.StringConverter;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;

import java.lang.reflect.Type;
import java.util.UUID;

public class GrantConverter extends Converter<Grant> {

    public GrantConverter() {
        super(Grant.class, true, false);
    }

    @Override
    public Grant convertToValue(Type fieldType, JsonElement element) {
        return Utils.convertToValue(element);
    }

    @Override
    public JsonElement convertToSimple(Type fieldType, Grant value) {
        return Utils.convertToSimple(value);
    }

    public static class Utils {

        public static Grant convertToValue(JsonElement element) {
            if (!element.isJsonObject()) {
                return null;
            }
            var data = element.getAsJsonObject();

            var grant = new Grant(
                    data.get("id").getAsString(),
                    data.get("playerId") != null ? UUID.fromString(data.get("playerId").getAsString()) : null,
                    Rank.valueOf(data.get("rank").getAsString()),
                    data.get("addedById") != null ? UUID.fromString(data.get("addedById").getAsString()) : null,
                    data.get("addedAt").getAsLong(),
                    StringConverter.Utils.convertToValue(data.get("addedReason")),
                    data.get("duration").getAsLong()
            );

            grant.setRemoved(data.get("removed").getAsBoolean());

            if (grant.isRemoved()) {
                grant.setRemovedById(data.get("removedById") != null ? UUID.fromString(data.get("removedById").getAsString()) : null);
                grant.setRemovedAt(data.get("removedAt").getAsLong());
                grant.setRemovedReason(StringConverter.Utils.convertToValue(data.get("removedReason")));
            }

            return grant;
        }

        public static JsonElement convertToSimple(Grant value) {
            var removed = value.isRemoved();

            var data = new JsonChain()
                    .add("id", new JsonPrimitive(value.getId()))
                    .add("playerId", value.getPlayerId() != null ? new JsonPrimitive(value.getPlayerId().toString()) : JsonNull.INSTANCE)
                    .add("rank", new JsonPrimitive(value.getRank().name()))
                    .add("addedById", value.getAddedById() != null ? new JsonPrimitive(value.getAddedById().toString()) : JsonNull.INSTANCE)
                    .add("addedAt", new JsonPrimitive(value.getAddedAt()))
                    .add("addedReason", StringConverter.Utils.convertToSimple(value.getAddedReason()))
                    .add("duration", new JsonPrimitive(value.getDuration()))
                    .add("removed", new JsonPrimitive(removed));


            if (removed) {
                data.add("removedById", value.getRemovedById() != null ? new JsonPrimitive(value.getRemovedById().toString()) : JsonNull.INSTANCE)
                        .add("removedAt", new JsonPrimitive(value.getRemovedAt()))
                        .add("removedReason", StringConverter.Utils.convertToSimple(value.getRemovedReason()));
            }

            return data.get();
        }

    }

}
