package cc.minetale.commonlib.pigeon.converters;

import cc.minetale.commonlib.api.Punishment;
import cc.minetale.pigeon.Converter;
import cc.minetale.pigeon.converters.EnumConverter;
import cc.minetale.pigeon.converters.StringConverter;
import cc.minetale.pigeon.converters.UUIDConverter;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.lang.reflect.Type;
import java.util.UUID;

public class PunishmentConverter extends Converter<Punishment> {

    public PunishmentConverter() { super(Punishment.class, true, false); }

    @Override
    public Punishment convertToValue(Type fieldType, JsonElement element) {
        return Utils.convertToValue(element);
    }

    @Override
    public JsonElement convertToSimple(Type fieldType, Punishment value) {
        return Utils.convertToSimple(value);
    }

    public static class Utils {

        public static Punishment convertToValue(JsonElement element) {
            if(!element.isJsonObject()) { return null; }
            var data = element.getAsJsonObject();

            var removed = data.get("removed").getAsBoolean();

            var punishment = Punishment.builder()
                    .id(data.get("id").getAsString())
                    .playerId(UUIDConverter.Utils.convertToValue(data.get("playedId")))
                    .type(EnumConverter.Utils.convertToValue(Punishment.Type.class, data.get("type")))
                    .addedById(data.get("addedById") != null ? UUIDConverter.Utils.convertToValue(data.get("addedById")) : null)
                    .addedAt(data.get("addedAt").getAsLong())
                    .addedReason(StringConverter.Utils.convertToValue(data.get("addedReason")))
                    .duration(data.get("duration").getAsLong())
                    .removed(removed);

            if(removed) {
                punishment.removedAt(data.get("removedAt").getAsLong())
                        .removedById(data.get("removedById") != null ? UUID.fromString(data.get("removedByUUID").getAsString()) : null)
                        .removedReason(StringConverter.Utils.convertToValue(data.get("removedReason")));
            }

            return punishment.build();
        }

        public static JsonElement convertToSimple(Punishment value) {
            var data = new JsonObject();

            data.add("id", new JsonPrimitive(value.getId()));
            data.add("playerId", UUIDConverter.Utils.convertToSimple(value.getPlayerId()));
            data.add("type", EnumConverter.Utils.convertToSimple(value.getType()));
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
