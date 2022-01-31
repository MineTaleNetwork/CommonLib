package cc.minetale.commonlib.util;

import cc.minetale.commonlib.CommonLib;
import lombok.experimental.UtilityClass;

import java.io.IOException;

@UtilityClass
public class JsonUtil {

    public static String writeToJson(Object object) {
        try {
            final var mapper = CommonLib.getJsonMapper();

            return mapper.writeValueAsString(object);
        } catch(IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static <T> T readFromJson(String json, Class<T> clazz) {
        try {
            final var mapper = CommonLib.getJsonMapper();

            return mapper.readValue(json, clazz);
        } catch(IOException e) {
            e.printStackTrace();
        }

        return null;
    }

}
