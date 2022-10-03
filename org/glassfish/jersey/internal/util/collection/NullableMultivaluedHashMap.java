package org.glassfish.jersey.internal.util.collection;

import java.util.List;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.MultivaluedHashMap;

public class NullableMultivaluedHashMap<K, V> extends MultivaluedHashMap<K, V>
{
    public NullableMultivaluedHashMap() {
    }
    
    public NullableMultivaluedHashMap(final int initialCapacity) {
        super(initialCapacity);
    }
    
    public NullableMultivaluedHashMap(final int initialCapacity, final float loadFactor) {
        super(initialCapacity, loadFactor);
    }
    
    public NullableMultivaluedHashMap(final MultivaluedMap<? extends K, ? extends V> map) {
        super((MultivaluedMap)map);
    }
    
    protected void addFirstNull(final List<V> values) {
        values.add(null);
    }
    
    protected void addNull(final List<V> values) {
        values.add(null);
    }
}
