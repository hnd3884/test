package org.apache.commons.collections.keyvalue;

import java.util.Map;
import org.apache.commons.collections.KeyValue;
import org.apache.commons.collections.Unmodifiable;

public final class UnmodifiableMapEntry extends AbstractMapEntry implements Unmodifiable
{
    public UnmodifiableMapEntry(final Object key, final Object value) {
        super(key, value);
    }
    
    public UnmodifiableMapEntry(final KeyValue pair) {
        super(pair.getKey(), pair.getValue());
    }
    
    public UnmodifiableMapEntry(final Map.Entry entry) {
        super(entry.getKey(), entry.getValue());
    }
    
    public Object setValue(final Object value) {
        throw new UnsupportedOperationException("setValue() is not supported");
    }
}
