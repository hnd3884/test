package javax.ws.rs.core;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.io.Serializable;

public class MultivaluedHashMap<K, V> extends AbstractMultivaluedMap<K, V> implements Serializable
{
    private static final long serialVersionUID = -6052320403766368902L;
    
    public MultivaluedHashMap() {
        super(new HashMap());
    }
    
    public MultivaluedHashMap(final int initialCapacity) {
        super(new HashMap(initialCapacity));
    }
    
    public MultivaluedHashMap(final int initialCapacity, final float loadFactor) {
        super(new HashMap(initialCapacity, loadFactor));
    }
    
    public MultivaluedHashMap(final MultivaluedMap<? extends K, ? extends V> map) {
        this();
        this.putAll(map);
    }
    
    private <T extends K, U extends V> void putAll(final MultivaluedMap<T, U> map) {
        for (final Map.Entry<T, List<U>> e : map.entrySet()) {
            this.store.put(e.getKey(), new ArrayList<V>((Collection<? extends V>)e.getValue()));
        }
    }
    
    public MultivaluedHashMap(final Map<? extends K, ? extends V> map) {
        this();
        for (final Map.Entry<? extends K, ? extends V> e : map.entrySet()) {
            this.putSingle((K)e.getKey(), (V)e.getValue());
        }
    }
}
