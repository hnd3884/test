package javax.ws.rs.core;

import java.util.Set;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public abstract class AbstractMultivaluedMap<K, V> implements MultivaluedMap<K, V>
{
    protected final Map<K, List<V>> store;
    
    public AbstractMultivaluedMap(final Map<K, List<V>> store) {
        if (store == null) {
            throw new NullPointerException("Underlying store must not be 'null'.");
        }
        this.store = store;
    }
    
    @Override
    public final void putSingle(final K key, final V value) {
        final List<V> values = this.getValues(key);
        values.clear();
        if (value != null) {
            values.add(value);
        }
        else {
            this.addNull(values);
        }
    }
    
    protected void addNull(final List<V> values) {
    }
    
    protected void addFirstNull(final List<V> values) {
    }
    
    @Override
    public final void add(final K key, final V value) {
        final List<V> values = this.getValues(key);
        if (value != null) {
            values.add(value);
        }
        else {
            this.addNull(values);
        }
    }
    
    @Override
    public final void addAll(final K key, final V... newValues) {
        if (newValues == null) {
            throw new NullPointerException("Supplied array of values must not be null.");
        }
        if (newValues.length == 0) {
            return;
        }
        final List<V> values = this.getValues(key);
        for (final V value : newValues) {
            if (value != null) {
                values.add(value);
            }
            else {
                this.addNull(values);
            }
        }
    }
    
    @Override
    public final void addAll(final K key, final List<V> valueList) {
        if (valueList == null) {
            throw new NullPointerException("Supplied list of values must not be null.");
        }
        if (valueList.isEmpty()) {
            return;
        }
        final List<V> values = this.getValues(key);
        for (final V value : valueList) {
            if (value != null) {
                values.add(value);
            }
            else {
                this.addNull(values);
            }
        }
    }
    
    @Override
    public final V getFirst(final K key) {
        final List<V> values = this.store.get(key);
        if (values != null && values.size() > 0) {
            return values.get(0);
        }
        return null;
    }
    
    @Override
    public final void addFirst(final K key, final V value) {
        final List<V> values = this.getValues(key);
        if (value != null) {
            values.add(0, value);
        }
        else {
            this.addFirstNull(values);
        }
    }
    
    protected final List<V> getValues(final K key) {
        List<V> l = this.store.get(key);
        if (l == null) {
            l = new LinkedList<V>();
            this.store.put(key, l);
        }
        return l;
    }
    
    @Override
    public String toString() {
        return this.store.toString();
    }
    
    @Override
    public int hashCode() {
        return this.store.hashCode();
    }
    
    @Override
    public boolean equals(final Object o) {
        return this.store.equals(o);
    }
    
    @Override
    public Collection<List<V>> values() {
        return this.store.values();
    }
    
    @Override
    public int size() {
        return this.store.size();
    }
    
    @Override
    public List<V> remove(final Object key) {
        return this.store.remove(key);
    }
    
    @Override
    public void putAll(final Map<? extends K, ? extends List<V>> m) {
        this.store.putAll(m);
    }
    
    @Override
    public List<V> put(final K key, final List<V> value) {
        return this.store.put(key, value);
    }
    
    @Override
    public Set<K> keySet() {
        return this.store.keySet();
    }
    
    @Override
    public boolean isEmpty() {
        return this.store.isEmpty();
    }
    
    @Override
    public List<V> get(final Object key) {
        return this.store.get(key);
    }
    
    @Override
    public Set<Map.Entry<K, List<V>>> entrySet() {
        return this.store.entrySet();
    }
    
    @Override
    public boolean containsValue(final Object value) {
        return this.store.containsValue(value);
    }
    
    @Override
    public boolean containsKey(final Object key) {
        return this.store.containsKey(key);
    }
    
    @Override
    public void clear() {
        this.store.clear();
    }
    
    @Override
    public boolean equalsIgnoreValueOrder(final MultivaluedMap<K, V> omap) {
        if (this == omap) {
            return true;
        }
        if (!this.keySet().equals(omap.keySet())) {
            return false;
        }
        for (final Map.Entry<K, List<V>> e : this.entrySet()) {
            final List<V> olist = omap.get(e.getKey());
            if (e.getValue().size() != olist.size()) {
                return false;
            }
            for (final V v : e.getValue()) {
                if (!olist.contains(v)) {
                    return false;
                }
            }
        }
        return true;
    }
}
