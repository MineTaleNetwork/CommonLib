package cc.minetale.commonlib.modules.pigeon.converters;

import cc.minetale.commonlib.modules.grant.Grant;
import cc.minetale.pigeon.Converter;
import cc.minetale.pigeon.converters.StringConverter;
import cc.minetale.pigeon.converters.UUIDConverter;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.lang.reflect.Type;

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

            var grant = new Grant();

            grant.setId(data.get("id").getAsString());
            grant.setPlayerUUID(UUIDConverter.Utils.convertToValue(data.get("playerUUID")));
            grant.setRankUUID(UUIDConverter.Utils.convertToValue(data.get("rankUUID")));

            var addedByUUID = data.get("addedByUUID");
            if(addedByUUID != null)
                grant.setAddedByUUID(UUIDConverter.Utils.convertToValue(addedByUUID));

            grant.setAddedAt(data.get("addedAt").getAsLong());
            grant.setAddedReason(StringConverter.Utils.convertToValue(data.get("addedReason")));
            grant.setDuration(data.get("duration").getAsLong());

            var removed = data.get("removed").getAsBoolean();
            if(removed) {
                grant.setRemovedByUUID(UUIDConverter.Utils.convertToValue(data.get("removedByUUID")));
                grant.setRemovedAt(data.get("removedAt").getAsLong());
                grant.setRemovedReason(StringConverter.Utils.convertToValue(data.get("removedReason")));
            }
            grant.setRemoved(removed);

            return grant;
        }

        public static JsonElement convertToSimple(Grant value) {
            var data = new JsonObject();

            data.add("id", new JsonPrimitive(value.getId()));
            data.add("playerUUID", UUIDConverter.Utils.convertToSimple(value.getPlayerUUID()));
            data.add("rankUUID", UUIDConverter.Utils.convertToSimple(value.getRankUUID()));

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
