package org.apache.commons.collections4;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public interface Get<K, V>
{
    boolean containsKey(final Object p0);
    
    boolean containsValue(final Object p0);
    
    Set<Map.Entry<K, V>> entrySet();
    
    V get(final Object p0);
    
    V remove(final Object p0);
    
    boolean isEmpty();
    
    Set<K> keySet();
    
    int size();
    
    Collection<V> values();
}
