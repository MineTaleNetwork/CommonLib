package cc.minetale.commonlib.modules.pigeon;

import cc.minetale.pigeon.Converter;
import cc.minetale.pigeon.Pigeon;
import cc.minetale.pigeon.converters.ListConverter;
import cc.minetale.pigeon.converters.MapConverter;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Map;

/**
 * @deprecated See {@link DocumentHelper}.
 */
@Deprecated
public class UnknownTypeHelper {

    public static JsonObject convertToSimple(Object value) {
        var data = new JsonObject();

        Class<?> type = value.getClass();

        data.add("type", new JsonPrimitive(value.getClass().getName()));
        if(type.getGenericSuperclass() instanceof ParameterizedType) {
            if(value instanceof List) {
                List list = (List) value;

                if(!list.isEmpty()) {
                    data.add("parameters", new JsonArray());
                    data.add("value", ListConverter.Utils.convertToSimple(list, ((ParameterizedType) type.getGenericSuperclass()).getActualTypeArguments()[0]));
                } else {
                    data.add("value", new JsonArray());
                }
            } else if(value instanceof Map) {
                Map map = (Map) value;

                if(!map.isEmpty()) {
                    data.add("value", MapConverter.Utils.convertToSimple(map,
                            (Class<?>) ((ParameterizedType) type.getGenericSuperclass()).getActualTypeArguments()[0],
                            (Class<?>) ((ParameterizedType) type.getGenericSuperclass()).getActualTypeArguments()[1]));
                } else {
                    data.add("value", new JsonArray());
                }
            } else {
                try {
                    throw new IllegalArgumentException("Tried to convert an unsupported (yet) type of \"" + type.getCanonicalName() + "\".");
                } catch(IllegalArgumentException e) {
                    e.printStackTrace();
                }
            }
        } else {
            Converter<Object> converter = Pigeon.getPigeon()
                    .getConvertersRegistry().getConverterForType(type);

            data.add("value", converter.convertToSimple(type, value));
        }

        return data;
    }

}
