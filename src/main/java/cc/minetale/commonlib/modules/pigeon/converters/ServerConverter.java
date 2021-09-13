package cc.minetale.commonlib.modules.pigeon.converters;

import cc.minetale.commonlib.modules.network.server.Server;
import cc.minetale.pigeon.Converter;
import cc.minetale.pigeon.converters.StringConverter;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.lang.reflect.Type;

public class ServerConverter extends Converter<Server> {

    public ServerConverter() { super(Server.class, true, false); }

    @Override
    public Server convertToValue(Type fieldType, JsonElement element) {
        return Utils.convertToValue(element);
    }

    @Override
    public JsonElement convertToSimple(Type fieldType, Server value) {
        return Utils.convertToSimple(value);
    }

    public static class Utils {
        public static Server convertToValue(JsonElement element) {
            if(!element.isJsonObject()) { return null; }
            var data = element.getAsJsonObject();

            var server = new Server();

            server.setName(data.get("name").getAsString());
            server.setStartTime(data.get("startTime").getAsLong());
            server.setData(ServerDataConverter.Utils.convertToValue(data.get("data")));

            return server;
        }

        public static JsonElement convertToSimple(Server value) {
            var data = new JsonObject();

            data.add("name", StringConverter.Utils.convertToSimple(value.getName()));
            data.add("startTime", new JsonPrimitive(value.getStartTime()));
            data.add("data", ServerDataConverter.Utils.convertToSimple(value.getData()));

            return data;
        }
    }

}
