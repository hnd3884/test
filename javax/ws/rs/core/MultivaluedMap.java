package javax.ws.rs.core;

import java.util.List;
import java.util.Map;

public interface MultivaluedMap<K, V> extends Map<K, List<V>>
{
    void putSingle(final K p0, final V p1);
    
    void add(final K p0, final V p1);
    
    V getFirst(final K p0);
    
    void addAll(final K p0, final V... p1);
    
    void addAll(final K p0, final List<V> p1);
    
    void addFirst(final K p0, final V p1);
    
    boolean equalsIgnoreValueOrder(final MultivaluedMap<K, V> p0);
}
