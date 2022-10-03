package org.apache.commons.collections4.keyvalue;

import java.util.Map;
import org.apache.commons.collections4.KeyValue;
import org.apache.commons.collections4.Unmodifiable;

public final class UnmodifiableMapEntry<K, V> extends AbstractMapEntry<K, V> implements Unmodifiable
{
    public UnmodifiableMapEntry(final K key, final V value) {
        super(key, value);
    }
    
    public UnmodifiableMapEntry(final KeyValue<? extends K, ? extends V> pair) {
        super(pair.getKey(), pair.getValue());
    }
    
    public UnmodifiableMapEntry(final Map.Entry<? extends K, ? extends V> entry) {
        super(entry.getKey(), entry.getValue());
    }
    
    @Override
    public V setValue(final V value) {
        throw new UnsupportedOperationException("setValue() is not supported");
    }
}
