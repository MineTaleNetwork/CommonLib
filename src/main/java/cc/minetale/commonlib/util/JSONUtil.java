package cc.minetale.commonlib.util;

import cc.minetale.commonlib.CommonLib;
import com.google.gson.reflect.TypeToken;
import org.bson.Document;

import java.lang.reflect.Type;
import java.util.List;

public class JSONUtil {

    public static <T> Type getTypeToken(T T) {
        return new TypeToken<List<T>>(){}.getType();
    }

    public static <T> T fromDocument(Document document, Class<T> clazz) {
        if(document == null) return null;

        return fromJson(document.toJson(), clazz);
    }

    public static Document toDocument(Object object) {
        return Document.parse(toJson(object));
    }

    public static <T> T fromJson(String json, Class<T> clazz) {
        return CommonLib.getGson().fromJson(json, clazz);
    }

    public static <T> T fromJson(String json, Type type) {
        return CommonLib.getGson().fromJson(json, type);
    }

    public static String toJson(Object object) {
        return CommonLib.getGson().toJson(object);
    }

}
