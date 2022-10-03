package org.apache.commons.collections4.keyvalue;

import java.util.Map;
import org.apache.commons.collections4.KeyValue;

public final class DefaultMapEntry<K, V> extends AbstractMapEntry<K, V>
{
    public DefaultMapEntry(final K key, final V value) {
        super(key, value);
    }
    
    public DefaultMapEntry(final KeyValue<? extends K, ? extends V> pair) {
        super(pair.getKey(), pair.getValue());
    }
    
    public DefaultMapEntry(final Map.Entry<? extends K, ? extends V> entry) {
        super(entry.getKey(), entry.getValue());
    }
}
