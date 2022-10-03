package org.apache.poi.util;

import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.function.Function;
import java.util.LinkedHashMap;
import java.util.Collections;
import java.util.Map;
import java.util.function.Supplier;

@Internal
public final class GenericRecordUtil
{
    private GenericRecordUtil() {
    }
    
    public static Map<String, Supplier<?>> getGenericProperties(final String val1, final Supplier<?> sup1) {
        return Collections.unmodifiableMap((Map<? extends String, ? extends Supplier<?>>)Collections.singletonMap(val1, sup1));
    }
    
    public static Map<String, Supplier<?>> getGenericProperties(final String val1, final Supplier<?> sup1, final String val2, final Supplier<?> sup2) {
        return getGenericProperties(val1, sup1, val2, sup2, null, null, null, null, null, null, null, null);
    }
    
    public static Map<String, Supplier<?>> getGenericProperties(final String val1, final Supplier<?> sup1, final String val2, final Supplier<?> sup2, final String val3, final Supplier<?> sup3) {
        return getGenericProperties(val1, sup1, val2, sup2, val3, sup3, null, null, null, null, null, null);
    }
    
    public static Map<String, Supplier<?>> getGenericProperties(final String val1, final Supplier<?> sup1, final String val2, final Supplier<?> sup2, final String val3, final Supplier<?> sup3, final String val4, final Supplier<?> sup4) {
        return getGenericProperties(val1, sup1, val2, sup2, val3, sup3, val4, sup4, null, null, null, null);
    }
    
    public static Map<String, Supplier<?>> getGenericProperties(final String val1, final Supplier<?> sup1, final String val2, final Supplier<?> sup2, final String val3, final Supplier<?> sup3, final String val4, final Supplier<?> sup4, final String val5, final Supplier<?> sup5) {
        return getGenericProperties(val1, sup1, val2, sup2, val3, sup3, val4, sup4, val5, sup5, null, null);
    }
    
    public static Map<String, Supplier<?>> getGenericProperties(final String val1, final Supplier<?> sup1, final String val2, final Supplier<?> sup2, final String val3, final Supplier<?> sup3, final String val4, final Supplier<?> sup4, final String val5, final Supplier<?> sup5, final String val6, final Supplier<?> sup6) {
        final Map<String, Supplier<?>> m = new LinkedHashMap<String, Supplier<?>>();
        final String[] vals = { val1, val2, val3, val4, val5, val6 };
        final Supplier<?>[] sups = { sup1, sup2, sup3, sup4, sup5, sup6 };
        for (int i = 0; i < vals.length && vals[i] != null; ++i) {
            assert sups[i] != null;
            if ("base".equals(vals[i])) {
                final Object baseMap = sups[i].get();
                assert baseMap instanceof Map;
                m.putAll((Map<? extends String, ? extends Supplier<?>>)baseMap);
            }
            else {
                m.put(vals[i], sups[i]);
            }
        }
        return Collections.unmodifiableMap((Map<? extends String, ? extends Supplier<?>>)m);
    }
    
    public static <T extends Enum> Supplier<T> safeEnum(final T[] values, final Supplier<Number> ordinal) {
        return safeEnum(values, ordinal, null);
    }
    
    public static <T extends Enum> Supplier<T> safeEnum(final T[] values, final Supplier<Number> ordinal, final T defaultVal) {
        final int ord = ordinal.get().intValue();
        return (Supplier<T>)(() -> (0 <= ord && ord < values.length) ? values[ord] : defaultVal);
    }
    
    public static Supplier<AnnotatedFlag> getBitsAsString(final Supplier<Number> flags, final int[] masks, final String[] names) {
        return () -> new AnnotatedFlag(flags, masks, names, false);
    }
    
    public static Supplier<AnnotatedFlag> getEnumBitsAsString(final Supplier<Number> flags, final int[] masks, final String[] names) {
        return () -> new AnnotatedFlag(flags, masks, names, true);
    }
    
    public static class AnnotatedFlag
    {
        private final Supplier<Number> value;
        private final Map<Integer, String> masks;
        private final boolean exactMatch;
        
        AnnotatedFlag(final Supplier<Number> value, final int[] masks, final String[] names, final boolean exactMatch) {
            this.masks = new LinkedHashMap<Integer, String>();
            assert masks.length == names.length;
            this.value = value;
            this.exactMatch = exactMatch;
            for (int i = 0; i < masks.length; ++i) {
                this.masks.put(masks[i], names[i]);
            }
        }
        
        public Supplier<Number> getValue() {
            return this.value;
        }
        
        public String getDescription() {
            final int val = this.value.get().intValue();
            return this.masks.entrySet().stream().filter(e -> this.match(val, e.getKey())).map((Function<? super Object, ?>)Map.Entry::getValue).collect((Collector<? super Object, ?, String>)Collectors.joining(" | "));
        }
        
        private boolean match(final int val, final int mask) {
            return this.exactMatch ? (val == mask) : ((val & mask) == mask);
        }
    }
}
