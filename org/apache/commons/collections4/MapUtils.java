package org.apache.commons.collections4;

import java.util.TreeMap;
import org.apache.commons.collections4.map.AbstractSortedMapDecorator;
import org.apache.commons.collections4.map.AbstractMapDecorator;
import org.apache.commons.collections4.map.LazySortedMap;
import org.apache.commons.collections4.map.FixedSizeSortedMap;
import org.apache.commons.collections4.map.TransformedSortedMap;
import org.apache.commons.collections4.map.PredicatedSortedMap;
import org.apache.commons.collections4.map.UnmodifiableSortedMap;
import org.apache.commons.collections4.map.MultiValueMap;
import java.util.Collection;
import org.apache.commons.collections4.map.ListOrderedMap;
import org.apache.commons.collections4.map.LazyMap;
import org.apache.commons.collections4.map.FixedSizeMap;
import org.apache.commons.collections4.map.TransformedMap;
import org.apache.commons.collections4.map.PredicatedMap;
import org.apache.commons.collections4.map.UnmodifiableMap;
import java.util.Collections;
import java.util.Deque;
import java.util.ArrayDeque;
import java.io.PrintStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.Iterator;
import java.util.Properties;
import java.text.ParseException;
import java.text.NumberFormat;
import java.util.Map;
import java.util.SortedMap;

public class MapUtils
{
    public static final SortedMap EMPTY_SORTED_MAP;
    private static final String INDENT_STRING = "    ";
    
    private MapUtils() {
    }
    
    public static <K, V> V getObject(final Map<? super K, V> map, final K key) {
        if (map != null) {
            return map.get(key);
        }
        return null;
    }
    
    public static <K> String getString(final Map<? super K, ?> map, final K key) {
        if (map != null) {
            final Object answer = map.get(key);
            if (answer != null) {
                return answer.toString();
            }
        }
        return null;
    }
    
    public static <K> Boolean getBoolean(final Map<? super K, ?> map, final K key) {
        if (map != null) {
            final Object answer = map.get(key);
            if (answer != null) {
                if (answer instanceof Boolean) {
                    return (Boolean)answer;
                }
                if (answer instanceof String) {
                    return Boolean.valueOf((String)answer);
                }
                if (answer instanceof Number) {
                    final Number n = (Number)answer;
                    return (n.intValue() != 0) ? Boolean.TRUE : Boolean.FALSE;
                }
            }
        }
        return null;
    }
    
    public static <K> Number getNumber(final Map<? super K, ?> map, final K key) {
        if (map != null) {
            final Object answer = map.get(key);
            if (answer != null) {
                if (answer instanceof Number) {
                    return (Number)answer;
                }
                if (answer instanceof String) {
                    try {
                        final String text = (String)answer;
                        return NumberFormat.getInstance().parse(text);
                    }
                    catch (final ParseException ex) {}
                }
            }
        }
        return null;
    }
    
    public static <K> Byte getByte(final Map<? super K, ?> map, final K key) {
        final Number answer = getNumber(map, key);
        if (answer == null) {
            return null;
        }
        if (answer instanceof Byte) {
            return (Byte)answer;
        }
        return answer.byteValue();
    }
    
    public static <K> Short getShort(final Map<? super K, ?> map, final K key) {
        final Number answer = getNumber(map, key);
        if (answer == null) {
            return null;
        }
        if (answer instanceof Short) {
            return (Short)answer;
        }
        return answer.shortValue();
    }
    
    public static <K> Integer getInteger(final Map<? super K, ?> map, final K key) {
        final Number answer = getNumber(map, key);
        if (answer == null) {
            return null;
        }
        if (answer instanceof Integer) {
            return (Integer)answer;
        }
        return answer.intValue();
    }
    
    public static <K> Long getLong(final Map<? super K, ?> map, final K key) {
        final Number answer = getNumber(map, key);
        if (answer == null) {
            return null;
        }
        if (answer instanceof Long) {
            return (Long)answer;
        }
        return answer.longValue();
    }
    
    public static <K> Float getFloat(final Map<? super K, ?> map, final K key) {
        final Number answer = getNumber(map, key);
        if (answer == null) {
            return null;
        }
        if (answer instanceof Float) {
            return (Float)answer;
        }
        return answer.floatValue();
    }
    
    public static <K> Double getDouble(final Map<? super K, ?> map, final K key) {
        final Number answer = getNumber(map, key);
        if (answer == null) {
            return null;
        }
        if (answer instanceof Double) {
            return (Double)answer;
        }
        return answer.doubleValue();
    }
    
    public static <K> Map<?, ?> getMap(final Map<? super K, ?> map, final K key) {
        if (map != null) {
            final Object answer = map.get(key);
            if (answer != null && answer instanceof Map) {
                return (Map)answer;
            }
        }
        return null;
    }
    
    public static <K, V> V getObject(final Map<K, V> map, final K key, final V defaultValue) {
        if (map != null) {
            final V answer = map.get(key);
            if (answer != null) {
                return answer;
            }
        }
        return defaultValue;
    }
    
    public static <K> String getString(final Map<? super K, ?> map, final K key, final String defaultValue) {
        String answer = getString(map, key);
        if (answer == null) {
            answer = defaultValue;
        }
        return answer;
    }
    
    public static <K> Boolean getBoolean(final Map<? super K, ?> map, final K key, final Boolean defaultValue) {
        Boolean answer = getBoolean(map, key);
        if (answer == null) {
            answer = defaultValue;
        }
        return answer;
    }
    
    public static <K> Number getNumber(final Map<? super K, ?> map, final K key, final Number defaultValue) {
        Number answer = getNumber(map, key);
        if (answer == null) {
            answer = defaultValue;
        }
        return answer;
    }
    
    public static <K> Byte getByte(final Map<? super K, ?> map, final K key, final Byte defaultValue) {
        Byte answer = getByte(map, key);
        if (answer == null) {
            answer = defaultValue;
        }
        return answer;
    }
    
    public static <K> Short getShort(final Map<? super K, ?> map, final K key, final Short defaultValue) {
        Short answer = getShort(map, key);
        if (answer == null) {
            answer = defaultValue;
        }
        return answer;
    }
    
    public static <K> Integer getInteger(final Map<? super K, ?> map, final K key, final Integer defaultValue) {
        Integer answer = getInteger(map, key);
        if (answer == null) {
            answer = defaultValue;
        }
        return answer;
    }
    
    public static <K> Long getLong(final Map<? super K, ?> map, final K key, final Long defaultValue) {
        Long answer = getLong(map, key);
        if (answer == null) {
            answer = defaultValue;
        }
        return answer;
    }
    
    public static <K> Float getFloat(final Map<? super K, ?> map, final K key, final Float defaultValue) {
        Float answer = getFloat(map, key);
        if (answer == null) {
            answer = defaultValue;
        }
        return answer;
    }
    
    public static <K> Double getDouble(final Map<? super K, ?> map, final K key, final Double defaultValue) {
        Double answer = getDouble(map, key);
        if (answer == null) {
            answer = defaultValue;
        }
        return answer;
    }
    
    public static <K> Map<?, ?> getMap(final Map<? super K, ?> map, final K key, final Map<?, ?> defaultValue) {
        Map<?, ?> answer = getMap(map, key);
        if (answer == null) {
            answer = defaultValue;
        }
        return answer;
    }
    
    public static <K> boolean getBooleanValue(final Map<? super K, ?> map, final K key) {
        return Boolean.TRUE.equals(getBoolean(map, key));
    }
    
    public static <K> byte getByteValue(final Map<? super K, ?> map, final K key) {
        final Byte byteObject = getByte(map, key);
        if (byteObject == null) {
            return 0;
        }
        return byteObject;
    }
    
    public static <K> short getShortValue(final Map<? super K, ?> map, final K key) {
        final Short shortObject = getShort(map, key);
        if (shortObject == null) {
            return 0;
        }
        return shortObject;
    }
    
    public static <K> int getIntValue(final Map<? super K, ?> map, final K key) {
        final Integer integerObject = getInteger(map, key);
        if (integerObject == null) {
            return 0;
        }
        return integerObject;
    }
    
    public static <K> long getLongValue(final Map<? super K, ?> map, final K key) {
        final Long longObject = getLong(map, key);
        if (longObject == null) {
            return 0L;
        }
        return longObject;
    }
    
    public static <K> float getFloatValue(final Map<? super K, ?> map, final K key) {
        final Float floatObject = getFloat(map, key);
        if (floatObject == null) {
            return 0.0f;
        }
        return floatObject;
    }
    
    public static <K> double getDoubleValue(final Map<? super K, ?> map, final K key) {
        final Double doubleObject = getDouble(map, key);
        if (doubleObject == null) {
            return 0.0;
        }
        return doubleObject;
    }
    
    public static <K> boolean getBooleanValue(final Map<? super K, ?> map, final K key, final boolean defaultValue) {
        final Boolean booleanObject = getBoolean(map, key);
        if (booleanObject == null) {
            return defaultValue;
        }
        return booleanObject;
    }
    
    public static <K> byte getByteValue(final Map<? super K, ?> map, final K key, final byte defaultValue) {
        final Byte byteObject = getByte(map, key);
        if (byteObject == null) {
            return defaultValue;
        }
        return byteObject;
    }
    
    public static <K> short getShortValue(final Map<? super K, ?> map, final K key, final short defaultValue) {
        final Short shortObject = getShort(map, key);
        if (shortObject == null) {
            return defaultValue;
        }
        return shortObject;
    }
    
    public static <K> int getIntValue(final Map<? super K, ?> map, final K key, final int defaultValue) {
        final Integer integerObject = getInteger(map, key);
        if (integerObject == null) {
            return defaultValue;
        }
        return integerObject;
    }
    
    public static <K> long getLongValue(final Map<? super K, ?> map, final K key, final long defaultValue) {
        final Long longObject = getLong(map, key);
        if (longObject == null) {
            return defaultValue;
        }
        return longObject;
    }
    
    public static <K> float getFloatValue(final Map<? super K, ?> map, final K key, final float defaultValue) {
        final Float floatObject = getFloat(map, key);
        if (floatObject == null) {
            return defaultValue;
        }
        return floatObject;
    }
    
    public static <K> double getDoubleValue(final Map<? super K, ?> map, final K key, final double defaultValue) {
        final Double doubleObject = getDouble(map, key);
        if (doubleObject == null) {
            return defaultValue;
        }
        return doubleObject;
    }
    
    public static <K, V> Properties toProperties(final Map<K, V> map) {
        final Properties answer = new Properties();
        if (map != null) {
            for (final Map.Entry<?, ?> entry3 : map.entrySet()) {
                final Map.Entry<K, V> entry2 = (Map.Entry<K, V>)entry3;
                final Object key = entry3.getKey();
                final Object value = entry3.getValue();
                answer.put(key, value);
            }
        }
        return answer;
    }
    
    public static Map<String, Object> toMap(final ResourceBundle resourceBundle) {
        final Enumeration<String> enumeration = resourceBundle.getKeys();
        final Map<String, Object> map = new HashMap<String, Object>();
        while (enumeration.hasMoreElements()) {
            final String key = enumeration.nextElement();
            final Object value = resourceBundle.getObject(key);
            map.put(key, value);
        }
        return map;
    }
    
    public static void verbosePrint(final PrintStream out, final Object label, final Map<?, ?> map) {
        verbosePrintInternal(out, label, map, new ArrayDeque<Map<?, ?>>(), false);
    }
    
    public static void debugPrint(final PrintStream out, final Object label, final Map<?, ?> map) {
        verbosePrintInternal(out, label, map, new ArrayDeque<Map<?, ?>>(), true);
    }
    
    private static void verbosePrintInternal(final PrintStream out, final Object label, final Map<?, ?> map, final Deque<Map<?, ?>> lineage, final boolean debug) {
        printIndent(out, lineage.size());
        if (map == null) {
            if (label != null) {
                out.print(label);
                out.print(" = ");
            }
            out.println("null");
            return;
        }
        if (label != null) {
            out.print(label);
            out.println(" = ");
        }
        printIndent(out, lineage.size());
        out.println("{");
        lineage.addLast(map);
        for (final Map.Entry<?, ?> entry : map.entrySet()) {
            final Object childKey = entry.getKey();
            final Object childValue = entry.getValue();
            if (childValue instanceof Map && !lineage.contains(childValue)) {
                verbosePrintInternal(out, (childKey == null) ? "null" : childKey, (Map<?, ?>)childValue, lineage, debug);
            }
            else {
                printIndent(out, lineage.size());
                out.print(childKey);
                out.print(" = ");
                final int lineageIndex = IterableUtils.indexOf(lineage, (Predicate<? super Map<?, ?>>)PredicateUtils.equalPredicate(childValue));
                if (lineageIndex == -1) {
                    out.print(childValue);
                }
                else if (lineage.size() - 1 == lineageIndex) {
                    out.print("(this Map)");
                }
                else {
                    out.print("(ancestor[" + (lineage.size() - 1 - lineageIndex - 1) + "] Map)");
                }
                if (debug && childValue != null) {
                    out.print(' ');
                    out.println(childValue.getClass().getName());
                }
                else {
                    out.println();
                }
            }
        }
        lineage.removeLast();
        printIndent(out, lineage.size());
        out.println(debug ? ("} " + map.getClass().getName()) : "}");
    }
    
    private static void printIndent(final PrintStream out, final int indent) {
        for (int i = 0; i < indent; ++i) {
            out.print("    ");
        }
    }
    
    public static <K, V> Map<V, K> invertMap(final Map<K, V> map) {
        final Map<V, K> out = new HashMap<V, K>(map.size());
        for (final Map.Entry<K, V> entry : map.entrySet()) {
            out.put(entry.getValue(), entry.getKey());
        }
        return out;
    }
    
    public static <K> void safeAddToMap(final Map<? super K, Object> map, final K key, final Object value) throws NullPointerException {
        map.put(key, (value == null) ? "" : value);
    }
    
    public static <K, V> Map<K, V> putAll(final Map<K, V> map, final Object[] array) {
        if (map == null) {
            throw new NullPointerException("The map must not be null");
        }
        if (array == null || array.length == 0) {
            return map;
        }
        final Object obj = array[0];
        if (obj instanceof Map.Entry) {
            for (final Object element : array) {
                final Map.Entry<K, V> entry = (Map.Entry<K, V>)element;
                map.put(entry.getKey(), entry.getValue());
            }
        }
        else if (obj instanceof KeyValue) {
            for (final Object element : array) {
                final KeyValue<K, V> keyval = (KeyValue<K, V>)element;
                map.put(keyval.getKey(), keyval.getValue());
            }
        }
        else if (obj instanceof Object[]) {
            for (int i = 0; i < array.length; ++i) {
                final Object[] sub = (Object[])array[i];
                if (sub == null || sub.length < 2) {
                    throw new IllegalArgumentException("Invalid array element: " + i);
                }
                map.put((K)sub[0], (V)sub[1]);
            }
        }
        else {
            int i = 0;
            while (i < array.length - 1) {
                map.put((K)array[i++], (V)array[i++]);
            }
        }
        return map;
    }
    
    public static <K, V> Map<K, V> emptyIfNull(final Map<K, V> map) {
        return (map == null) ? Collections.emptyMap() : map;
    }
    
    public static boolean isEmpty(final Map<?, ?> map) {
        return map == null || map.isEmpty();
    }
    
    public static boolean isNotEmpty(final Map<?, ?> map) {
        return !isEmpty(map);
    }
    
    public static <K, V> Map<K, V> synchronizedMap(final Map<K, V> map) {
        return Collections.synchronizedMap(map);
    }
    
    public static <K, V> Map<K, V> unmodifiableMap(final Map<? extends K, ? extends V> map) {
        return UnmodifiableMap.unmodifiableMap(map);
    }
    
    public static <K, V> IterableMap<K, V> predicatedMap(final Map<K, V> map, final Predicate<? super K> keyPred, final Predicate<? super V> valuePred) {
        return PredicatedMap.predicatedMap(map, keyPred, valuePred);
    }
    
    public static <K, V> IterableMap<K, V> transformedMap(final Map<K, V> map, final Transformer<? super K, ? extends K> keyTransformer, final Transformer<? super V, ? extends V> valueTransformer) {
        return TransformedMap.transformingMap(map, keyTransformer, valueTransformer);
    }
    
    public static <K, V> IterableMap<K, V> fixedSizeMap(final Map<K, V> map) {
        return FixedSizeMap.fixedSizeMap(map);
    }
    
    public static <K, V> IterableMap<K, V> lazyMap(final Map<K, V> map, final Factory<? extends V> factory) {
        return LazyMap.lazyMap(map, factory);
    }
    
    public static <K, V> IterableMap<K, V> lazyMap(final Map<K, V> map, final Transformer<? super K, ? extends V> transformerFactory) {
        return LazyMap.lazyMap(map, transformerFactory);
    }
    
    public static <K, V> OrderedMap<K, V> orderedMap(final Map<K, V> map) {
        return ListOrderedMap.listOrderedMap(map);
    }
    
    @Deprecated
    public static <K, V> MultiValueMap<K, V> multiValueMap(final Map<K, ? super Collection<V>> map) {
        return MultiValueMap.multiValueMap(map);
    }
    
    @Deprecated
    public static <K, V, C extends Collection<V>> MultiValueMap<K, V> multiValueMap(final Map<K, C> map, final Class<C> collectionClass) {
        return MultiValueMap.multiValueMap(map, collectionClass);
    }
    
    @Deprecated
    public static <K, V, C extends Collection<V>> MultiValueMap<K, V> multiValueMap(final Map<K, C> map, final Factory<C> collectionFactory) {
        return MultiValueMap.multiValueMap(map, collectionFactory);
    }
    
    public static <K, V> SortedMap<K, V> synchronizedSortedMap(final SortedMap<K, V> map) {
        return Collections.synchronizedSortedMap(map);
    }
    
    public static <K, V> SortedMap<K, V> unmodifiableSortedMap(final SortedMap<K, ? extends V> map) {
        return UnmodifiableSortedMap.unmodifiableSortedMap(map);
    }
    
    public static <K, V> SortedMap<K, V> predicatedSortedMap(final SortedMap<K, V> map, final Predicate<? super K> keyPred, final Predicate<? super V> valuePred) {
        return PredicatedSortedMap.predicatedSortedMap(map, keyPred, valuePred);
    }
    
    public static <K, V> SortedMap<K, V> transformedSortedMap(final SortedMap<K, V> map, final Transformer<? super K, ? extends K> keyTransformer, final Transformer<? super V, ? extends V> valueTransformer) {
        return TransformedSortedMap.transformingSortedMap(map, keyTransformer, valueTransformer);
    }
    
    public static <K, V> SortedMap<K, V> fixedSizeSortedMap(final SortedMap<K, V> map) {
        return FixedSizeSortedMap.fixedSizeSortedMap(map);
    }
    
    public static <K, V> SortedMap<K, V> lazySortedMap(final SortedMap<K, V> map, final Factory<? extends V> factory) {
        return LazySortedMap.lazySortedMap(map, factory);
    }
    
    public static <K, V> SortedMap<K, V> lazySortedMap(final SortedMap<K, V> map, final Transformer<? super K, ? extends V> transformerFactory) {
        return LazySortedMap.lazySortedMap(map, transformerFactory);
    }
    
    public static <K, V> void populateMap(final Map<K, V> map, final Iterable<? extends V> elements, final Transformer<V, K> keyTransformer) {
        populateMap((Map<K, Object>)map, elements, (Transformer<Object, K>)keyTransformer, TransformerUtils.nopTransformer());
    }
    
    public static <K, V, E> void populateMap(final Map<K, V> map, final Iterable<? extends E> elements, final Transformer<E, K> keyTransformer, final Transformer<E, V> valueTransformer) {
        for (final E temp : elements) {
            map.put(keyTransformer.transform(temp), valueTransformer.transform(temp));
        }
    }
    
    public static <K, V> void populateMap(final MultiMap<K, V> map, final Iterable<? extends V> elements, final Transformer<V, K> keyTransformer) {
        populateMap((MultiMap<K, Object>)map, elements, (Transformer<Object, K>)keyTransformer, TransformerUtils.nopTransformer());
    }
    
    public static <K, V, E> void populateMap(final MultiMap<K, V> map, final Iterable<? extends E> elements, final Transformer<E, K> keyTransformer, final Transformer<E, V> valueTransformer) {
        for (final E temp : elements) {
            map.put(keyTransformer.transform(temp), valueTransformer.transform(temp));
        }
    }
    
    public static <K, V> IterableMap<K, V> iterableMap(final Map<K, V> map) {
        if (map == null) {
            throw new NullPointerException("Map must not be null");
        }
        return (map instanceof IterableMap) ? ((IterableMap)map) : new AbstractMapDecorator<K, V>(map) {};
    }
    
    public static <K, V> IterableSortedMap<K, V> iterableSortedMap(final SortedMap<K, V> sortedMap) {
        if (sortedMap == null) {
            throw new NullPointerException("Map must not be null");
        }
        return (sortedMap instanceof IterableSortedMap) ? ((IterableSortedMap)sortedMap) : new AbstractSortedMapDecorator<K, V>(sortedMap) {};
    }
    
    static {
        EMPTY_SORTED_MAP = UnmodifiableSortedMap.unmodifiableSortedMap((SortedMap<Object, ?>)new TreeMap<Object, Object>());
    }
}
