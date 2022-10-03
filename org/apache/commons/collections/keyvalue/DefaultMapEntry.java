package org.apache.commons.collections.keyvalue;

import java.util.Map;
import org.apache.commons.collections.KeyValue;

public final class DefaultMapEntry extends AbstractMapEntry
{
    public DefaultMapEntry(final Object key, final Object value) {
        super(key, value);
    }
    
    public DefaultMapEntry(final KeyValue pair) {
        super(pair.getKey(), pair.getValue());
    }
    
    public DefaultMapEntry(final Map.Entry entry) {
        super(entry.getKey(), entry.getValue());
    }
}
