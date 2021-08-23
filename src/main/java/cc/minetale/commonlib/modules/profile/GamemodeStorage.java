package cc.minetale.commonlib.modules.profile;

import cc.minetale.commonlib.modules.network.Gamemode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class GamemodeStorage {
    @Getter private Gamemode gamemode;
    @Getter private Map<String, StorageValue> values = Collections.synchronizedMap(new HashMap<>());

    public GamemodeStorage(@NotNull Gamemode gamemode) {
        this.gamemode = gamemode;
    }

    public GamemodeStorage(String gamemodeName, Document document) {
        this.gamemode = Gamemode.getByName(gamemodeName);

        for(Map.Entry<String, Object> ent : document.entrySet()) {
            String name = ent.getKey();

            var valueDocument = (Document) ent.getValue();
            var value = new StorageValue(valueDocument.get("value"),
                    name,
                    this,
                    valueDocument.getBoolean("isWritable"));

            this.values.put(name, value);
        }
    }

    public boolean add(StorageValue value) {
        String name = value.getName();
        if(name == null || name.isEmpty()) { return false; }

        if(values.containsKey(name) || value.getStorage() != null) { return false; }

        value.setStorage(this);

        values.put(name, value);
        return true;
    }

    public StorageValue set(StorageValue value) {
        String name = value.getName();
        if(name == null || name.isEmpty()) { return null; }

        value.setStorage(this);

        return values.put(name, value);
    }

    public boolean ensure(StorageValue value) {
        if(!has(value.getName())) {
            return add(value);
        } else {
            return false;
        }
    }

    public StorageValue remove(String name) {
        return values.remove(name);
    }

    public StorageValue get(String name) {
        return values.get(name);
    }

    public boolean has(String name) {
        return values.containsKey(name) && values.get(name) != null;
    }

    @AllArgsConstructor
    public static class StorageValue {
        @Getter private Object value;

        @Getter @Setter private String name;

        @Nullable @Getter @Setter private GamemodeStorage storage; //Storage this value is apart of, can't add to new GamemodeStorage if this value isn't null
        @Getter private final boolean isWritable; //Can you change this value? If set to true, the only way to change this is to recreate the value

        public StorageValue(String name, Object initialValue, boolean isWritable) {
            this.name = name;
            this.value = initialValue;
            this.isWritable = isWritable;
        }

        public void setValue(Object value) {
            if(this.isWritable) {
                this.value = value;
            }
        }

        public int asInt() {
            return (int) this.value;
        }

        public float asFloat() {
            return (float) this.value;
        }

        public double asDouble() {
            return (double) this.value;
        }

        public long asLong() {
            return (long) this.value;
        }

        public short asShort() {
            return (short) this.value;
        }

        public byte asByte() {
            return (byte) this.value;
        }

        public boolean asBoolean() {
            Object value = this.value;
            if (value instanceof Boolean) {
                return (Boolean)value;
            } else if (value instanceof Number) {
                return ((Number)value).intValue() != 0;
            } else if (value instanceof String) {
                return Boolean.parseBoolean((String)value);
            } else {
                return value != null;
            }
        }

        @NotNull
        public String asString() {
            return this.value == null ? "" : this.value.toString();
        }
    }
}
