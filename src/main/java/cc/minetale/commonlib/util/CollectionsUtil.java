package cc.minetale.commonlib.util;

import lombok.experimental.UtilityClass;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;
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

    /**
     * Returns a partial map of the provided map only with the provided keys.
     * @param map The map to take a subset of
     * @param keys The keys to take from the map
     * @param mapSupplier Map implementation to use
     * @return Subset of the original map
     */
    public static <K, V> Map<K, V> subset(Map<K, V> map, Collection<K> keys, Supplier<Map<K, V>> mapSupplier) {
        Map<K, V> subset = mapSupplier.get();
        for(K key : keys) {
            subset.put(key, map.get(key));
        }
        return subset;
    }

    /**
     * Uses a {@linkplain HashMap} as the implementation. <br>
     * See {@linkplain #subset(Map, Collection, Supplier)}.
     * */
    public static <K, V> Map<K, V> subset(Map<K, V> map, Collection<K> keys) {
        return subset(map, keys, HashMap::new);
    }

    /**
     * Returns a new map from a map of collections that contains all possible values from these collections for each key. <br><br>
     * {@code
     *      { key1 = [1, 2],<br/>
     *          key2 = [3, 4] }<br/><br/>
     *
     *      After:<br/><br/>
     *
     *      [
     *          {
     *              key1 = 1,
     *              key2 = 3
     *          },<br/>
     *          {
     *              key1 = 2,
     *              key2 = 3
     *          },<br/>
     *          {
     *              key1 = 1,
     *              key2 = 4
     *          },<br/>
     *          {
     *              key1 = 2,
     *              key2 = 4
     *          }
     *      ]
     * }
     * @param map The map to get permutations of
     * @return All possible permutations
     */
    public static <K, V> List<Map<K, V>> permutations(Map<K, ? extends Collection<V>> map) {
        List<Map<K, V>> permutations = new LinkedList<>();
        permutations(map, new HashMap<>(), new ArrayList<>(map.keySet()), permutations);
        return permutations;
    }

    private static <K, V> void permutations(Map<K, ? extends Collection<V>> map,
                                            Map<K, V> currentPermutation, List<K> keysLeft,
                                            List<Map<K, V>> finalList) {

        K key = keysLeft.get(0);
        keysLeft = keysLeft.subList(1, keysLeft.size());
        for (V value : map.get(key)) {
            Map<K, V> permutation = new HashMap<>(currentPermutation);

            permutation.put(key, value);

            if (keysLeft.isEmpty()) {
                finalList.add(permutation);
            } else {
                permutations(map, permutation, keysLeft, finalList);
            }
        }
    }

    /**
     * Returns the first element from a collection.
     * @param col Collection to get the element from
     * @return The first element
     */
    public static <T> T first(Collection<T> col) {
        if(col.isEmpty()) { return null; }

        return col.iterator().next();
    }

    /**
     * Returns the last element from a collection.
     * @param col Collection to get the element from
     * @return The last element
     */
    public static <T> T last(Collection<T> col) {
        if(col.isEmpty()) { return null; }

        T e = null;
        for (T t : col) { e = t; }

        return e;
    }

    /**
     * Returns the first element from a list.
     * @param list List to get the element from
     * @return The first element
     */
    public static <T> T first(List<T> list) {
        if(list.isEmpty()) { return null; }
        return list.get(0);
    }

    /**
     * Returns the last element from a list.
     * @param list List to get the element from
     * @return The last element
     */
    public static <T> T last(List<T> list) {
        if(list.isEmpty()) { return null; }
        return list.get(list.size() - 1);
    }

    /**
     * Returns a random element from a collection.
     * @param col Collection to get the element from
     * @return A random element
     */
    public static <T> T random(Collection<T> col) {
        if(col.isEmpty()) { return null; }

        var rng = ThreadLocalRandom.current().nextDouble();

        return col.stream()
                .skip((long) (col.size() * rng))
                .findFirst()
                .orElse(null);
    }

    /**
     * Returns a random entry from a map.
     * @param map Map to get the entry from
     * @return A random entry
     */
    public static <K, V> Map.Entry<K, V> randomEntry(Map<K, V> map) {
        if(map.isEmpty()) { return null; }

        var rng = ThreadLocalRandom.current().nextDouble();

        return map.entrySet().stream()
                .skip((long) (map.size() * rng))
                .findFirst()
                .orElse(null);
    }

    /**
     * Returns a random element from a collection based on their weight.
     * @param col Collection to get the element from
     * @param weightFunc Function to get/calculate the weight of each element
     * @return A random element
     */
    public static <T> T weightedRandom(Collection<T> col, ToDoubleFunction<T> weightFunc) {
        if(col.isEmpty()) { return null; }

        var rng = ThreadLocalRandom.current().nextDouble();
        var total = col.stream()
                .mapToDouble(weightFunc)
                .sum();

        var selected = rng * total;

        double currentWeight = 0;
        for(var element : col) {
            currentWeight += weightFunc.applyAsDouble(element);
            if(selected < currentWeight) { return element; }
        }

        return null;
    }

    /**
     * Returns a random entry from a map based on the entry's value weight.
     * @param map Map to get the entry from
     * @param weightFunc Function to get/calculate the weight of each value
     * @return A random entry
     */
    public static <K, V> Map.Entry<K, V> weightedRandomByValue(Map<K, V> map, ToDoubleFunction<V> weightFunc) {
        if(map.isEmpty()) { return null; }

        var rng = ThreadLocalRandom.current().nextDouble();
        var total = map.values().stream()
                .mapToDouble(weightFunc)
                .sum();

        var selected = rng * total;

        double currentWeight = 0;
        for(var entry : map.entrySet()) {
            var value = entry.getValue();

            currentWeight += weightFunc.applyAsDouble(value);
            if(selected < currentWeight) { return entry; }
        }

        return null;
    }

    /**
     * Returns a random entry from a map based on the entry's key weight.
     * @param map Map to get the entry from
     * @param weightFunc Function to get/calculate the weight of each key
     * @return A random entry
     */
    public static <K, V> Map.Entry<K, V> weightedRandomByKey(Map<K, V> map, ToDoubleFunction<K> weightFunc) {
        if(map.isEmpty()) { return null; }

        var rng = ThreadLocalRandom.current().nextDouble();
        var total = map.keySet().stream()
                .mapToDouble(weightFunc)
                .sum();

        var selected = rng * total;

        double currentWeight = 0;
        for(var entry : map.entrySet()) {
            var key = entry.getKey();

            currentWeight += weightFunc.applyAsDouble(key);
            if(selected < currentWeight) { return entry; }
        }

        return null;
    }

    /**
     * Returns a random entry from a map based on the entry's weight.
     * @param map Map to get the entry from
     * @param weightFunc Function to get/calculate the weight of each entry
     * @return A random entry
     */
    public static <K, V> Map.Entry<K, V> weightedRandomByEntry(Map<K, V> map, ToDoubleFunction<Map.Entry<K, V>> weightFunc) {
        if(map.isEmpty()) { return null; }

        var rng = ThreadLocalRandom.current().nextDouble();
        var total = map.entrySet().stream()
                .mapToDouble(weightFunc)
                .sum();

        var selected = rng * total;

        double currentWeight = 0;
        for(var entry : map.entrySet()) {
            currentWeight += weightFunc.applyAsDouble(entry);
            if(selected < currentWeight) { return entry; }
        }

        return null;
    }

}
