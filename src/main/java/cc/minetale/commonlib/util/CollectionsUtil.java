package cc.minetale.commonlib.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CollectionsUtil {

    public static <T> List<T> genericListToType(List<?> list, Class<T> type) {
        ArrayList<T> convertedList = new ArrayList<>();
        for(Object element : list) {
            convertedList.add(type.cast(element));
        }
        return convertedList;
    }

    public static <K, V> Map<K, V> genericMapToType(Map<?, ?> map, Class<K> keyType, Class<V> valueType) {
        HashMap<K, V> convertedMap = new HashMap<>();
        for(Map.Entry<?, ?> ent : map.entrySet()) {
            convertedMap.put(keyType.cast(ent.getKey()), valueType.cast(ent.getValue()));
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

}
