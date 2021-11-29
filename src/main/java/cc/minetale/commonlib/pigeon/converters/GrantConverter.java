package cc.minetale.commonlib.pigeon.converters;

import cc.minetale.commonlib.api.Grant;
import cc.minetale.commonlib.api.Rank;
import cc.minetale.pigeon.Converter;
import cc.minetale.pigeon.converters.StringConverter;
import cc.minetale.pigeon.converters.UUIDConverter;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.lang.reflect.Type;
import java.util.UUID;

public class GrantConverter extends Converter<Grant> {

    public GrantConverter() { super(Grant.class, true, false); }

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
            if(!element.isJsonObject()) { return null; }
            var data = element.getAsJsonObject();

            var removed = data.get("removed").getAsBoolean();

            var grant = Grant.builder()
                    .id(data.get("_id").getAsString())
                    .playerId(UUID.fromString(data.get("playerId").getAsString()))
                    .rank(Rank.valueOf(data.get("rank").getAsString()))
                    .addedById(data.get("addedById") != null ? UUID.fromString(data.get("addedById").getAsString()) : null)
                    .addedAt(data.get("addedAt").getAsLong())
                    .addedReason(StringConverter.Utils.convertToValue(data.get("addedReason")))
                    .duration(data.get("duration").getAsLong())
                    .removed(removed);

            if(removed) {
                grant.removedAt(data.get("removedAt").getAsLong())
                        .removedById(data.get("removedById") != null ? UUID.fromString(data.get("removedById").getAsString()) : null)
                        .removedReason(StringConverter.Utils.convertToValue(data.get("removedReason")));
            }

            return grant.build();
        }

        public static JsonElement convertToSimple(Grant value) {
            var data = new JsonObject();

            data.add("id", new JsonPrimitive(value.getId()));
            data.add("playerId", UUIDConverter.Utils.convertToSimple(value.getPlayerId()));
            data.add("rank", new JsonPrimitive(value.getRank().name()));
            data.add("addedById", value.getAddedById() != null ? UUIDConverter.Utils.convertToSimple(value.getAddedById()) : JsonNull.INSTANCE);
            data.add("addedAt", new JsonPrimitive(value.getAddedAt()));
            data.add("addedReason", StringConverter.Utils.convertToSimple(value.getAddedReason()));
            data.add("duration", new JsonPrimitive(value.getDuration()));

            var removed = value.isRemoved();
            data.add("removed", new JsonPrimitive(removed));

            if(removed) {
                data.add("removedById", UUIDConverter.Utils.convertToSimple(value.getRemovedById()));
                data.add("removedAt", new JsonPrimitive(value.getRemovedAt()));
                data.add("removedReason", StringConverter.Utils.convertToSimple(value.getRemovedReason()));
            }

            return data;
        }

    }

}
