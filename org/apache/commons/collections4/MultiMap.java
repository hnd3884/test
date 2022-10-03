package org.apache.commons.collections4;

import java.util.Collection;

@Deprecated
public interface MultiMap<K, V> extends IterableMap<K, Object>
{
    boolean removeMapping(final K p0, final V p1);
    
    int size();
    
    Object get(final Object p0);
    
    boolean containsValue(final Object p0);
    
    Object put(final K p0, final Object p1);
    
    Object remove(final Object p0);
    
    Collection<Object> values();
}
