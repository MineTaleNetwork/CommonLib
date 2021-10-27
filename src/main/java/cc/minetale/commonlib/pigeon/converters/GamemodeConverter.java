package cc.minetale.commonlib.pigeon.converters;

import cc.minetale.commonlib.network.Gamemode;
import cc.minetale.pigeon.Converter;
import cc.minetale.pigeon.converters.StringConverter;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.lang.reflect.Type;

public class GamemodeConverter extends Converter<Gamemode> {

    public GamemodeConverter() { super(Gamemode.class, true, false); }

    @Override
    public Gamemode convertToValue(Type fieldType, JsonElement element) {
        return Utils.convertToValue(element);
    }

    @Override
    public JsonElement convertToSimple(Type fieldType, Gamemode value) {
        return Utils.convertToSimple(value);
    }

    public static class Utils {
        public static Gamemode convertToValue(JsonElement element) {
            var data = element.getAsJsonObject();

            return new Gamemode(
                    StringConverter.Utils.convertToValue(data.get("name")),
                    data.get("totalPlayers").getAsInt(),
                    RecordValueConverter.Utils.convertToValue(data.get("totalPlayersRecord"))
            );
        }

        public static JsonElement convertToSimple(Gamemode value) {
            JsonObject data = new JsonObject();

            data.add("name", StringConverter.Utils.convertToSimple(value.getName()));
            data.add("totalPlayers", new JsonPrimitive(value.getTotalPlayers()));
            data.add("totalPlayersRecord", RecordValueConverter.Utils.convertToSimple(value.getTotalPlayersRecord()));

            return data;
        }
    }

}
