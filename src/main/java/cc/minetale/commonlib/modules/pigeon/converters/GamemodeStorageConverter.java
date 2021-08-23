package cc.minetale.commonlib.modules.pigeon.converters;

import cc.minetale.commonlib.modules.profile.GamemodeStorage;
import cc.minetale.pigeon.Converter;
import cc.minetale.pigeon.Pigeon;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.lang.reflect.Type;
import java.util.Map;

public class GamemodeStorageConverter extends Converter<GamemodeStorage> {

    public GamemodeStorageConverter() { super(GamemodeStorage.class, true, false); }

    @Override
    public GamemodeStorage convertToValue(Type fieldType, JsonElement element) {
        return Utils.convertToValue(element);
    }

    @Override
    public JsonElement convertToSimple(Type fieldType, GamemodeStorage value) {
        return Utils.convertToSimple(value);
    }

    public static class Utils {
        public static GamemodeStorage convertToValue(JsonElement element) {
            if(!element.isJsonObject()) { return null; }
            var data = element.getAsJsonObject();

            var gamemodeStorage = new GamemodeStorage(GamemodeConverter.Utils.convertToValue(data.get("gamemode")));

            try {
                for(Map.Entry<String, JsonElement> ent : data.getAsJsonObject("values").entrySet()) {
                    var storageValueObj = ent.getValue().getAsJsonObject();

                    Class<?> type = Class.forName(storageValueObj.get("type").getAsString());
                    var converter = Pigeon.getPigeon()
                            .getConvertersRegistry().getConverterForType(type);

                    var name = ent.getKey();
                    var value = converter.convertToValue(type, storageValueObj.get("value"));
                    var isWritable = storageValueObj.get("isWritable").getAsBoolean();

                    var storageValue = new GamemodeStorage.StorageValue(name, value, isWritable);

                    gamemodeStorage.add(storageValue);
                }
            } catch(ClassNotFoundException e) {
                e.printStackTrace();
                return null;
            }

            return gamemodeStorage;
        }

        public static JsonElement convertToSimple(GamemodeStorage value) {
            var data = new JsonObject();

            data.add("gamemode", GamemodeConverter.Utils.convertToSimple(value.getGamemode()));

            var values = new JsonObject();
            for(Map.Entry<String, GamemodeStorage.StorageValue> ent : value.getValues().entrySet()) {
                var valueObj = new JsonObject();
                var storageValue = ent.getValue();

                valueObj.add("name", new JsonPrimitive(storageValue.getName()));

                var storedValue = storageValue.getValue();
                var type = storedValue.getClass();

                Converter<Object> converter = Pigeon.getPigeon()
                        .getConvertersRegistry().getConverterForType(type);

                valueObj.add("type", new JsonPrimitive(type.getCanonicalName()));
                valueObj.add("value", converter.convertToSimple(type, storageValue));

                valueObj.add("isWritable", new JsonPrimitive(storageValue.isWritable()));

                values.add(ent.getKey(), valueObj);
            }

            data.add("values", values);

            return data;
        }
    }

}
