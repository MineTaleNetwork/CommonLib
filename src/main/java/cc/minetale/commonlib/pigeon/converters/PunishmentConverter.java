package cc.minetale.commonlib.pigeon.converters;

import cc.minetale.commonlib.punishment.Punishment;
import cc.minetale.commonlib.punishment.PunishmentType;
import cc.minetale.pigeon.Converter;
import cc.minetale.pigeon.converters.EnumConverter;
import cc.minetale.pigeon.converters.StringConverter;
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

            var punishment = new Punishment(
                    data.get("id").getAsString(),
                    UUID.fromString(data.get("playerId").getAsString()),
                    EnumConverter.Utils.convertToValue(PunishmentType.class, data.get("type")),
                    data.get("addedById") != null ? UUID.fromString(data.get("addedById").getAsString()) : null,
                    data.get("addedAt").getAsLong(),
                    StringConverter.Utils.convertToValue(data.get("addedReason")),
                    data.get("duration").getAsLong()
            );

            punishment.setRemoved(data.get("removed").getAsBoolean());

            if(punishment.isRemoved()) {
                punishment.setRemovedAt(data.get("removedAt").getAsLong());
                punishment.setRemovedById(data.get("removedById") != null ? UUID.fromString(data.get("removedById").getAsString()) : null);
                punishment.setRemovedReason(StringConverter.Utils.convertToValue(data.get("removedReason")));
            }

            return punishment;
        }

        public static JsonElement convertToSimple(Punishment value) {
            var data = new JsonObject();

            data.add("id", new JsonPrimitive(value.getId()));
            data.add("playerId", new JsonPrimitive(value.getPlayerId().toString()));
            data.add("type", EnumConverter.Utils.convertToSimple(value.getPunishmentType()));
            data.add("addedById", value.getAddedById() != null ? new JsonPrimitive(value.getAddedById().toString()) : JsonNull.INSTANCE);
            data.add("addedAt", new JsonPrimitive(value.getAddedAt()));
            data.add("addedReason", StringConverter.Utils.convertToSimple(value.getAddedReason()));
            data.add("duration", new JsonPrimitive(value.getDuration()));

            var removed = value.isRemoved();
            data.add("removed", new JsonPrimitive(removed));

            if(removed) {
                data.add("removedById", new JsonPrimitive(value.getRemovedById().toString()));
                data.add("removedAt", new JsonPrimitive(value.getRemovedAt()));
                data.add("removedReason", StringConverter.Utils.convertToSimple(value.getRemovedReason()));
            }

            return data;
        }

    }

}
