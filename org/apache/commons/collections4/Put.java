package org.apache.commons.collections4;

import java.util.Map;

public interface Put<K, V>
{
    void clear();
    
    Object put(final K p0, final V p1);
    
    void putAll(final Map<? extends K, ? extends V> p0);
}
