package com.sun.xml.internal.bind.v2.util;

import java.util.Iterator;
import java.util.Map;

public class TypeCast
{
    public static <K, V> Map<K, V> checkedCast(final Map<?, ?> m, final Class<K> keyType, final Class<V> valueType) {
        if (m == null) {
            return null;
        }
        for (final Map.Entry e : m.entrySet()) {
            if (!keyType.isInstance(e.getKey())) {
                throw new ClassCastException(e.getKey().getClass().toString());
            }
            if (!valueType.isInstance(e.getValue())) {
                throw new ClassCastException(e.getValue().getClass().toString());
            }
        }
        return (Map<K, V>)m;
    }
}
