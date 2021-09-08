package cc.minetale.commonlib.modules.pigeon.converters;

import cc.minetale.commonlib.modules.punishment.Punishment;
import cc.minetale.pigeon.Converter;
import cc.minetale.pigeon.converters.EnumConverter;
import cc.minetale.pigeon.converters.StringConverter;
import cc.minetale.pigeon.converters.UUIDConverter;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.lang.reflect.Type;

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

            var punishment = new Punishment();

            punishment.setId(data.get("id").getAsString());
            punishment.setPlayerUUID(UUIDConverter.Utils.convertToValue(data.get("playerUUID")));
            punishment.setType(EnumConverter.Utils.convertToValue(Type.class, data.get("type")));

            var addedByUUID = data.get("addedByUUID");
            if(addedByUUID != null)
                punishment.setAddedByUUID(UUIDConverter.Utils.convertToValue(addedByUUID));

            punishment.setAddedAt(data.get("addedAt").getAsLong());
            punishment.setAddedReason(StringConverter.Utils.convertToValue(data.get("addedReason")));
            punishment.setDuration(data.get("duration").getAsLong());

            var removed = data.get("removed").getAsBoolean();
            if(removed) {
                punishment.setRemovedByUUID(UUIDConverter.Utils.convertToValue(data.get("removedByUUID")));
                punishment.setRemovedAt(data.get("removedAt").getAsLong());
                punishment.setRemovedReason(StringConverter.Utils.convertToValue(data.get("removedReason")));
            }
            punishment.setRemoved(removed);

            return punishment;
        }

        public static JsonElement convertToSimple(Punishment value) {
            var data = new JsonObject();

            data.add("id", new JsonPrimitive(value.getId()));
            data.add("playerUUID", UUIDConverter.Utils.convertToSimple(value.getPlayerUUID()));
            data.add("type", EnumConverter.Utils.convertToSimple(value.getType()));

            var addedByUUID = value.getAddedByUUID();
            if(addedByUUID != null) {
                data.add("addedByUUID", UUIDConverter.Utils.convertToSimple(addedByUUID));
            } else {
                data.add("addedByUUID", JsonNull.INSTANCE);
            }

            data.add("addedAt", new JsonPrimitive(value.getAddedAt()));
            data.add("addedReason", StringConverter.Utils.convertToSimple(value.getAddedReason()));
            data.add("duration", new JsonPrimitive(value.getDuration()));

            var removed = value.isRemoved();
            if(removed) {
                data.add("removedByUUID", UUIDConverter.Utils.convertToSimple(value.getRemovedByUUID()));
                data.add("removedAt", new JsonPrimitive(value.getRemovedAt()));
                data.add("removedReason", StringConverter.Utils.convertToSimple(value.getRemovedReason()));
            }
            data.add("removed", new JsonPrimitive(removed));

            return data;
        }
    }

}
