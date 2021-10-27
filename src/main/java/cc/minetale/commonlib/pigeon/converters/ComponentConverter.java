package cc.minetale.commonlib.pigeon.converters;

import cc.minetale.commonlib.util.MC;
import cc.minetale.pigeon.Converter;
import cc.minetale.pigeon.converters.StringConverter;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.kyori.adventure.text.Component;

import java.lang.reflect.Type;

public class ComponentConverter extends Converter<Component> {

    public ComponentConverter() { super(Component.class, true, false); }

    @Override
    public Component convertToValue(Type fieldType, JsonElement element) {
        return Utils.convertToValue(element);
    }

    @Override
    public JsonElement convertToSimple(Type fieldType, Component value) {
        return Utils.convertToSimple(value);
    }

    public static class Utils {
        public static Component convertToValue(JsonElement element) {
            var data = element.getAsJsonObject();

            return MC.Style.fromGson(StringConverter.Utils.convertToValue(data.get("component")));
        }

        public static JsonElement convertToSimple(Component value) {
            JsonObject data = new JsonObject();

            data.add("component", StringConverter.Utils.convertToSimple(MC.Style.toGson(value)));

            return data;
        }
    }

}
