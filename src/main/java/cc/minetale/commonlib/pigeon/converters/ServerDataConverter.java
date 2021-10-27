package cc.minetale.commonlib.pigeon.converters;

import cc.minetale.commonlib.network.server.ServerData;
import cc.minetale.pigeon.Converter;
import cc.minetale.pigeon.converters.*;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.lang.reflect.Type;
import java.util.UUID;

public class ServerDataConverter extends Converter<ServerData> {

    public ServerDataConverter() { super(ServerData.class, true, false); }

    @Override
    public ServerData convertToValue(Type fieldType, JsonElement element) {
        return Utils.convertToValue(element);
    }

    @Override
    public JsonElement convertToSimple(Type fieldType, ServerData value) {
        return Utils.convertToSimple(value);
    }

    public static class Utils {
        public static ServerData convertToValue(JsonElement element) {
            if(!element.isJsonObject()) { return null; }
            var data = element.getAsJsonObject();

            var serverData = new ServerData();

            serverData.setMaxPlayers(data.get("maxPlayers").getAsInt());
            serverData.setTps(data.get("tps").getAsDouble());
            serverData.setMetadata(MapConverter.Utils.convertToValue(data.get("metadata"), String.class, String.class));
            serverData.setOnlinePlayers(ListConverter.Utils.convertToValue(data.get("onlinePlayers"), UUID.class));

            return serverData;
        }

        public static JsonElement convertToSimple(ServerData value) {
            var data = new JsonObject();

            data.add("maxPlayers", new JsonPrimitive(value.getMaxPlayers()));
            data.add("tps", new JsonPrimitive(value.getTps()));
            data.add("metadata", MapConverter.Utils.convertToSimple(value.getMetadata(), String.class, String.class));
            data.add("onlinePlayers", ListConverter.Utils.convertToSimple(value.getOnlinePlayers(), UUID.class));

            return data;
        }
    }

}
