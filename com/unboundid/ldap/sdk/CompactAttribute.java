package com.unboundid.ldap.sdk;

import com.unboundid.util.StaticUtils;
import java.util.concurrent.ConcurrentHashMap;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import java.io.Serializable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
final class CompactAttribute implements Serializable
{
    private static final int MAX_CACHED_NAMES = 1000;
    private static final ConcurrentHashMap<String, String> cachedNames;
    private static final long serialVersionUID = 9056952830029621727L;
    private final byte[][] values;
    private final String name;
    
    CompactAttribute(final Attribute attribute) {
        this.name = internName(attribute.getName());
        this.values = attribute.getValueByteArrays();
    }
    
    private static String internName(final String name) {
        String s = CompactAttribute.cachedNames.get(name);
        if (s == null) {
            if (CompactAttribute.cachedNames.size() >= 1000) {
                CompactAttribute.cachedNames.clear();
            }
            CompactAttribute.cachedNames.put(name, name);
            s = name;
        }
        return s;
    }
    
    String getName() {
        return this.name;
    }
    
    byte[][] getByteValues() {
        return this.values;
    }
    
    String[] getStringValues() {
        final String[] stringValues = new String[this.values.length];
        for (int i = 0; i < this.values.length; ++i) {
            stringValues[i] = StaticUtils.toUTF8String(this.values[i]);
        }
        return stringValues;
    }
    
    Attribute toAttribute() {
        return new Attribute(this.name, this.values);
    }
    
    static {
        cachedNames = new ConcurrentHashMap<String, String>(StaticUtils.computeMapCapacity(1000));
    }
}
