package org.apache.commons.collections;

import java.util.TreeMap;
import org.apache.commons.collections.map.LazySortedMap;
import org.apache.commons.collections.map.FixedSizeSortedMap;
import org.apache.commons.collections.map.TransformedSortedMap;
import org.apache.commons.collections.map.TypedSortedMap;
import org.apache.commons.collections.map.PredicatedSortedMap;
import org.apache.commons.collections.map.UnmodifiableSortedMap;
import org.apache.commons.collections.map.MultiValueMap;
import org.apache.commons.collections.map.ListOrderedMap;
import org.apache.commons.collections.map.LazyMap;
import org.apache.commons.collections.map.FixedSizeMap;
import org.apache.commons.collections.map.TransformedMap;
import org.apache.commons.collections.map.TypedMap;
import org.apache.commons.collections.map.PredicatedMap;
import org.apache.commons.collections.map.UnmodifiableMap;
import java.util.Collections;
import java.io.PrintStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.Iterator;
import java.util.Properties;
import java.text.ParseException;
import java.text.NumberFormat;
import java.util.SortedMap;
import java.util.Map;

public class MapUtils
{
    public static final Map EMPTY_MAP;
    public static final SortedMap EMPTY_SORTED_MAP;
    private static final String INDENT_STRING = "    ";
    
    public static Object getObject(final Map map, final Object key) {
        if (map != null) {
            return map.get(key);
        }
        return null;
    }
    
    public static String getString(final Map map, final Object key) {
        if (map != null) {
            final Object answer = map.get(key);
            if (answer != null) {
                return answer.toString();
            }
        }
        return null;
    }
    
    public static Boolean getBoolean(final Map map, final Object key) {
        if (map != null) {
            final Object answer = map.get(key);
            if (answer != null) {
                if (answer instanceof Boolean) {
                    return (Boolean)answer;
                }
                if (answer instanceof String) {
                    return new Boolean((String)answer);
                }
                if (answer instanceof Number) {
                    final Number n = (Number)answer;
                    return (n.intValue() != 0) ? Boolean.TRUE : Boolean.FALSE;
                }
            }
        }
        return null;
    }
    
    public static Number getNumber(final Map map, final Object key) {
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
    
    public static Byte getByte(final Map map, final Object key) {
        final Number answer = getNumber(map, key);
        if (answer == null) {
            return null;
        }
        if (answer instanceof Byte) {
            return (Byte)answer;
        }
        return new Byte(answer.byteValue());
    }
    
    public static Short getShort(final Map map, final Object key) {
        final Number answer = getNumber(map, key);
        if (answer == null) {
            return null;
        }
        if (answer instanceof Short) {
            return (Short)answer;
        }
        return new Short(answer.shortValue());
    }
    
    public static Integer getInteger(final Map map, final Object key) {
        final Number answer = getNumber(map, key);
        if (answer == null) {
            return null;
        }
        if (answer instanceof Integer) {
            return (Integer)answer;
        }
        return new Integer(answer.intValue());
    }
    
    public static Long getLong(final Map map, final Object key) {
        final Number answer = getNumber(map, key);
        if (answer == null) {
            return null;
        }
        if (answer instanceof Long) {
            return (Long)answer;
        }
        return new Long(answer.longValue());
    }
    
    public static Float getFloat(final Map map, final Object key) {
        final Number answer = getNumber(map, key);
        if (answer == null) {
            return null;
        }
        if (answer instanceof Float) {
            return (Float)answer;
        }
        return new Float(answer.floatValue());
    }
    
    public static Double getDouble(final Map map, final Object key) {
        final Number answer = getNumber(map, key);
        if (answer == null) {
            return null;
        }
        if (answer instanceof Double) {
            return (Double)answer;
        }
        return new Double(answer.doubleValue());
    }
    
    public static Map getMap(final Map map, final Object key) {
        if (map != null) {
            final Object answer = map.get(key);
            if (answer != null && answer instanceof Map) {
                return (Map)answer;
            }
        }
        return null;
    }
    
    public static Object getObject(final Map map, final Object key, final Object defaultValue) {
        if (map != null) {
            final Object answer = map.get(key);
            if (answer != null) {
                return answer;
            }
        }
        return defaultValue;
    }
    
    public static String getString(final Map map, final Object key, final String defaultValue) {
        String answer = getString(map, key);
        if (answer == null) {
            answer = defaultValue;
        }
        return answer;
    }
    
    public static Boolean getBoolean(final Map map, final Object key, final Boolean defaultValue) {
        Boolean answer = getBoolean(map, key);
        if (answer == null) {
            answer = defaultValue;
        }
        return answer;
    }
    
    public static Number getNumber(final Map map, final Object key, final Number defaultValue) {
        Number answer = getNumber(map, key);
        if (answer == null) {
            answer = defaultValue;
        }
        return answer;
    }
    
    public static Byte getByte(final Map map, final Object key, final Byte defaultValue) {
        Byte answer = getByte(map, key);
        if (answer == null) {
            answer = defaultValue;
        }
        return answer;
    }
    
    public static Short getShort(final Map map, final Object key, final Short defaultValue) {
        Short answer = getShort(map, key);
        if (answer == null) {
            answer = defaultValue;
        }
        return answer;
    }
    
    public static Integer getInteger(final Map map, final Object key, final Integer defaultValue) {
        Integer answer = getInteger(map, key);
        if (answer == null) {
            answer = defaultValue;
        }
        return answer;
    }
    
    public static Long getLong(final Map map, final Object key, final Long defaultValue) {
        Long answer = getLong(map, key);
        if (answer == null) {
            answer = defaultValue;
        }
        return answer;
    }
    
    public static Float getFloat(final Map map, final Object key, final Float defaultValue) {
        Float answer = getFloat(map, key);
        if (answer == null) {
            answer = defaultValue;
        }
        return answer;
    }
    
    public static Double getDouble(final Map map, final Object key, final Double defaultValue) {
        Double answer = getDouble(map, key);
        if (answer == null) {
            answer = defaultValue;
        }
        return answer;
    }
    
    public static Map getMap(final Map map, final Object key, final Map defaultValue) {
        Map answer = getMap(map, key);
        if (answer == null) {
            answer = defaultValue;
        }
        return answer;
    }
    
    public static boolean getBooleanValue(final Map map, final Object key) {
        final Boolean booleanObject = getBoolean(map, key);
        return booleanObject != null && booleanObject;
    }
    
    public static byte getByteValue(final Map map, final Object key) {
        final Byte byteObject = getByte(map, key);
        if (byteObject == null) {
            return 0;
        }
        return byteObject;
    }
    
    public static short getShortValue(final Map map, final Object key) {
        final Short shortObject = getShort(map, key);
        if (shortObject == null) {
            return 0;
        }
        return shortObject;
    }
    
    public static int getIntValue(final Map map, final Object key) {
        final Integer integerObject = getInteger(map, key);
        if (integerObject == null) {
            return 0;
        }
        return integerObject;
    }
    
    public static long getLongValue(final Map map, final Object key) {
        final Long longObject = getLong(map, key);
        if (longObject == null) {
            return 0L;
        }
        return longObject;
    }
    
    public static float getFloatValue(final Map map, final Object key) {
        final Float floatObject = getFloat(map, key);
        if (floatObject == null) {
            return 0.0f;
        }
        return floatObject;
    }
    
    public static double getDoubleValue(final Map map, final Object key) {
        final Double doubleObject = getDouble(map, key);
        if (doubleObject == null) {
            return 0.0;
        }
        return doubleObject;
    }
    
    public static boolean getBooleanValue(final Map map, final Object key, final boolean defaultValue) {
        final Boolean booleanObject = getBoolean(map, key);
        if (booleanObject == null) {
            return defaultValue;
        }
        return booleanObject;
    }
    
    public static byte getByteValue(final Map map, final Object key, final byte defaultValue) {
        final Byte byteObject = getByte(map, key);
        if (byteObject == null) {
            return defaultValue;
        }
        return byteObject;
    }
    
    public static short getShortValue(final Map map, final Object key, final short defaultValue) {
        final Short shortObject = getShort(map, key);
        if (shortObject == null) {
            return defaultValue;
        }
        return shortObject;
    }
    
    public static int getIntValue(final Map map, final Object key, final int defaultValue) {
        final Integer integerObject = getInteger(map, key);
        if (integerObject == null) {
            return defaultValue;
        }
        return integerObject;
    }
    
    public static long getLongValue(final Map map, final Object key, final long defaultValue) {
        final Long longObject = getLong(map, key);
        if (longObject == null) {
            return defaultValue;
        }
        return longObject;
    }
    
    public static float getFloatValue(final Map map, final Object key, final float defaultValue) {
        final Float floatObject = getFloat(map, key);
        if (floatObject == null) {
            return defaultValue;
        }
        return floatObject;
    }
    
    public static double getDoubleValue(final Map map, final Object key, final double defaultValue) {
        final Double doubleObject = getDouble(map, key);
        if (doubleObject == null) {
            return defaultValue;
        }
        return doubleObject;
    }
    
    public static Properties toProperties(final Map map) {
        final Properties answer = new Properties();
        if (map != null) {
            final Iterator iter = map.entrySet().iterator();
            while (iter.hasNext()) {
                final Map.Entry entry = iter.next();
                final Object key = entry.getKey();
                final Object value = entry.getValue();
                answer.put(key, value);
            }
        }
        return answer;
    }
    
    public static Map toMap(final ResourceBundle resourceBundle) {
        final Enumeration enumeration = resourceBundle.getKeys();
        final Map map = new HashMap();
        while (enumeration.hasMoreElements()) {
            final String key = enumeration.nextElement();
            final Object value = resourceBundle.getObject(key);
            map.put(key, value);
        }
        return map;
    }
    
    public static void verbosePrint(final PrintStream out, final Object label, final Map map) {
        verbosePrintInternal(out, label, map, new ArrayStack(), false);
    }
    
    public static void debugPrint(final PrintStream out, final Object label, final Map map) {
        verbosePrintInternal(out, label, map, new ArrayStack(), true);
    }
    
    protected static void logInfo(final Exception ex) {
        System.out.println("INFO: Exception: " + ex);
    }
    
    private static void verbosePrintInternal(final PrintStream out, final Object label, final Map map, final ArrayStack lineage, final boolean debug) {
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
        lineage.push(map);
        final Iterator it = map.entrySet().iterator();
        while (it.hasNext()) {
            final Map.Entry entry = it.next();
            final Object childKey = entry.getKey();
            final Object childValue = entry.getValue();
            if (childValue instanceof Map && !lineage.contains(childValue)) {
                verbosePrintInternal(out, (childKey == null) ? "null" : childKey, (Map)childValue, lineage, debug);
            }
            else {
                printIndent(out, lineage.size());
                out.print(childKey);
                out.print(" = ");
                final int lineageIndex = lineage.indexOf(childValue);
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
        lineage.pop();
        printIndent(out, lineage.size());
        out.println(debug ? ("} " + map.getClass().getName()) : "}");
    }
    
    private static void printIndent(final PrintStream out, final int indent) {
        for (int i = 0; i < indent; ++i) {
            out.print("    ");
        }
    }
    
    public static Map invertMap(final Map map) {
        final Map out = new HashMap(map.size());
        final Iterator it = map.entrySet().iterator();
        while (it.hasNext()) {
            final Map.Entry entry = it.next();
            out.put(entry.getValue(), entry.getKey());
        }
        return out;
    }
    
    public static void safeAddToMap(final Map map, final Object key, final Object value) throws NullPointerException {
        if (value == null) {
            map.put(key, "");
        }
        else {
            map.put(key, value);
        }
    }
    
    public static Map putAll(final Map map, final Object[] array) {
        map.size();
        if (array == null || array.length == 0) {
            return map;
        }
        final Object obj = array[0];
        if (obj instanceof Map.Entry) {
            for (int i = 0; i < array.length; ++i) {
                final Map.Entry entry = (Map.Entry)array[i];
                map.put(entry.getKey(), entry.getValue());
            }
        }
        else if (obj instanceof KeyValue) {
            for (int i = 0; i < array.length; ++i) {
                final KeyValue keyval = (KeyValue)array[i];
                map.put(keyval.getKey(), keyval.getValue());
            }
        }
        else if (obj instanceof Object[]) {
            for (int i = 0; i < array.length; ++i) {
                final Object[] sub = (Object[])array[i];
                if (sub == null || sub.length < 2) {
                    throw new IllegalArgumentException("Invalid array element: " + i);
                }
                map.put(sub[0], sub[1]);
            }
        }
        else {
            int i = 0;
            while (i < array.length - 1) {
                map.put(array[i++], array[i++]);
            }
        }
        return map;
    }
    
    public static boolean isEmpty(final Map map) {
        return map == null || map.isEmpty();
    }
    
    public static boolean isNotEmpty(final Map map) {
        return !isEmpty(map);
    }
    
    public static Map synchronizedMap(final Map map) {
        return Collections.synchronizedMap((Map<Object, Object>)map);
    }
    
    public static Map unmodifiableMap(final Map map) {
        return UnmodifiableMap.decorate(map);
    }
    
    public static Map predicatedMap(final Map map, final Predicate keyPred, final Predicate valuePred) {
        return PredicatedMap.decorate(map, keyPred, valuePred);
    }
    
    public static Map typedMap(final Map map, final Class keyType, final Class valueType) {
        return TypedMap.decorate(map, keyType, valueType);
    }
    
    public static Map transformedMap(final Map map, final Transformer keyTransformer, final Transformer valueTransformer) {
        return TransformedMap.decorate(map, keyTransformer, valueTransformer);
    }
    
    public static Map fixedSizeMap(final Map map) {
        return FixedSizeMap.decorate(map);
    }
    
    public static Map lazyMap(final Map map, final Factory factory) {
        return LazyMap.decorate(map, factory);
    }
    
    public static Map lazyMap(final Map map, final Transformer transformerFactory) {
        return LazyMap.decorate(map, transformerFactory);
    }
    
    public static Map orderedMap(final Map map) {
        return ListOrderedMap.decorate(map);
    }
    
    public static Map multiValueMap(final Map map) {
        return MultiValueMap.decorate(map);
    }
    
    public static Map multiValueMap(final Map map, final Class collectionClass) {
        return MultiValueMap.decorate(map, collectionClass);
    }
    
    public static Map multiValueMap(final Map map, final Factory collectionFactory) {
        return MultiValueMap.decorate(map, collectionFactory);
    }
    
    public static Map synchronizedSortedMap(final SortedMap map) {
        return Collections.synchronizedSortedMap((SortedMap<Object, Object>)map);
    }
    
    public static Map unmodifiableSortedMap(final SortedMap map) {
        return UnmodifiableSortedMap.decorate(map);
    }
    
    public static SortedMap predicatedSortedMap(final SortedMap map, final Predicate keyPred, final Predicate valuePred) {
        return PredicatedSortedMap.decorate(map, keyPred, valuePred);
    }
    
    public static SortedMap typedSortedMap(final SortedMap map, final Class keyType, final Class valueType) {
        return TypedSortedMap.decorate(map, keyType, valueType);
    }
    
    public static SortedMap transformedSortedMap(final SortedMap map, final Transformer keyTransformer, final Transformer valueTransformer) {
        return TransformedSortedMap.decorate(map, keyTransformer, valueTransformer);
    }
    
    public static SortedMap fixedSizeSortedMap(final SortedMap map) {
        return FixedSizeSortedMap.decorate(map);
    }
    
    public static SortedMap lazySortedMap(final SortedMap map, final Factory factory) {
        return LazySortedMap.decorate(map, factory);
    }
    
    public static SortedMap lazySortedMap(final SortedMap map, final Transformer transformerFactory) {
        return LazySortedMap.decorate(map, transformerFactory);
    }
    
    static {
        EMPTY_MAP = UnmodifiableMap.decorate(new HashMap(1));
        EMPTY_SORTED_MAP = UnmodifiableSortedMap.decorate(new TreeMap());
    }
}
