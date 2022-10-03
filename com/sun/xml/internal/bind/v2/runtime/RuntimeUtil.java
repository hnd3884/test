package com.sun.xml.internal.bind.v2.runtime;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.util.Iterator;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class RuntimeUtil
{
    public static final Map<Class, Class> boxToPrimitive;
    public static final Map<Class, Class> primitiveToBox;
    
    private static String getTypeName(final Object o) {
        return o.getClass().getName();
    }
    
    static {
        final Map<Class, Class> b = new HashMap<Class, Class>();
        b.put(Byte.TYPE, Byte.class);
        b.put(Short.TYPE, Short.class);
        b.put(Integer.TYPE, Integer.class);
        b.put(Long.TYPE, Long.class);
        b.put(Character.TYPE, Character.class);
        b.put(Boolean.TYPE, Boolean.class);
        b.put(Float.TYPE, Float.class);
        b.put(Double.TYPE, Double.class);
        b.put(Void.TYPE, Void.class);
        primitiveToBox = Collections.unmodifiableMap((Map<? extends Class, ? extends Class>)b);
        final Map<Class, Class> p = new HashMap<Class, Class>();
        for (final Map.Entry<Class, Class> e : b.entrySet()) {
            p.put(e.getValue(), e.getKey());
        }
        boxToPrimitive = Collections.unmodifiableMap((Map<? extends Class, ? extends Class>)p);
    }
    
    public static final class ToStringAdapter extends XmlAdapter<String, Object>
    {
        @Override
        public Object unmarshal(final String s) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public String marshal(final Object o) {
            if (o == null) {
                return null;
            }
            return o.toString();
        }
    }
}
