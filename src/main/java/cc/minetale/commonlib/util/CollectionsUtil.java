package cc.minetale.commonlib.util;

import lombok.experimental.UtilityClass;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@UtilityClass
public class CollectionsUtil {

    /**
     * Returns a new list from a type-erased list after casting its values to provided type. <br>
     * All values that don't match the provided type and during casting threw an {@linkplain ClassCastException} will be skipped. <br>
     * <br>
     * Useful when we lost the type on a list, and we're sure what it is. <br>
     * Good alternative to {@linkplain Collections#checkedList} if we only care about type checking once.
     * @param list List with unknown values to convert to a typed list
     * @param type Type to cast the list's values to
     * @return A new {@linkplain HashMap} with casted keys and values
     */
    public static <T> List<T> genericListToType(List<?> list, Class<T> type) {
        ArrayList<T> convertedList = new ArrayList<>();
        for(Object element : list) {
            try {
                convertedList.add(type.cast(element));
            } catch(ClassCastException ignored) { }
        }
        return convertedList;
    }

    /**
     * Returns a new map from a type-erased map after casting its keys and values to provided types. <br>
     * All keys or keys whose values don't match the provided type and during casting threw an {@linkplain ClassCastException} will be skipped. <br>
     * <br>
     * Useful when we lost the types on a map, and we're sure what they are. <br>
     * Good alternative to {@linkplain Collections#checkedMap} if we only care about type checking once.
     * @param map Map with unknown keys and values to convert to a typed map
     * @param keyType Type to cast the map's keys to
     * @param valueType Type to cast the map's values to
     * @return A new {@linkplain HashMap} with casted keys and values
     */
    public static <K, V> Map<K, V> genericMapToType(Map<?, ?> map, Class<K> keyType, Class<V> valueType) {
        HashMap<K, V> convertedMap = new HashMap<>();
        for(Map.Entry<?, ?> ent : map.entrySet()) {
            try {
                convertedMap.put(keyType.cast(ent.getKey()), valueType.cast(ent.getValue()));
            } catch(ClassCastException ignored) { }
        }
        return convertedMap;
    }

    /**
     * Combines both lists into one map using one list as keys and the other as values. <br>
     * It does so using lists' order, so an element from the keys list at 5th index will be mapped to an element inside the values list at 5th index.
     * @param keys Keys used for the combined map
     * @param values Values used for the combined map
     * @return Combined map
     * @throws IllegalArgumentException When sizes of both lists don't match.
     */
    public static <K, V> Map<K, V> combine(List<K> keys, List<V> values) {
        if(keys.size() != values.size())
            throw new IllegalArgumentException("Sizes of both lists don't match.");

        return IntStream.range(0, keys.size()).boxed()
                .collect(Collectors.toMap(keys::get, values::get));
    }

    /**
     * Fills a map with the provided keys mapped to a single value.
     * @param map Map to fill
     * @param keys Keys to fill the map with
     * @param value Value to map the keys to
     */
    public static <K, V> void fill(Map<K, V> map, Collection<K> keys, V value) {
        for(K key : keys) {
            map.put(key, value);
        }
    }

}
