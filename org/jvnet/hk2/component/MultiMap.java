package org.jvnet.hk2.component;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Collections;
import java.util.Set;
import java.util.LinkedList;
import java.util.Iterator;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.io.Serializable;

public class MultiMap<K, V> implements Serializable, Cloneable
{
    private static final long serialVersionUID = 893592003056170756L;
    private final Map<K, List<V>> store;
    private static final String NEWLINE;
    
    public MultiMap() {
        this.store = new LinkedHashMap<K, List<V>>();
    }
    
    public MultiMap(final MultiMap<K, V> base) {
        this();
        for (final Map.Entry<K, List<V>> e : base.entrySet()) {
            final List<V> value = this.newList((Collection<? extends V>)e.getValue());
            if (!value.isEmpty()) {
                this.store.put(e.getKey(), this.newList((Collection<? extends V>)e.getValue()));
            }
        }
    }
    
    private List<V> newList(final Collection<? extends V> initialVals) {
        if (null == initialVals) {
            return new LinkedList<V>();
        }
        return new LinkedList<V>(initialVals);
    }
    
    public Set<K> keySet() {
        return this.store.keySet();
    }
    
    public final void add(final K k, final V v) {
        List<V> l = this.store.get(k);
        if (l == null) {
            l = this.newList(null);
            this.store.put(k, l);
        }
        l.add(v);
    }
    
    public void set(final K k, final Collection<? extends V> v) {
        final List<V> addMe = this.newList(v);
        if (addMe.isEmpty()) {
            this.store.remove(k);
        }
        else {
            this.store.put(k, this.newList(v));
        }
    }
    
    public void set(final K k, final V v) {
        final List<V> vlist = this.newList(null);
        vlist.add(v);
        this.store.put(k, vlist);
    }
    
    public final List<V> get(final K k) {
        final List<V> l = this.store.get(k);
        if (l == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList((List<? extends V>)l);
    }
    
    public void mergeAll(final MultiMap<K, V> another) {
        if (another == null) {
            return;
        }
        for (final Map.Entry<K, List<V>> entry : another.entrySet()) {
            List<V> ourList = this.store.get(entry.getKey());
            if (null == ourList) {
                ourList = this.newList((Collection<? extends V>)entry.getValue());
                if (ourList.isEmpty()) {
                    continue;
                }
                this.store.put(entry.getKey(), ourList);
            }
            else {
                for (final V v : entry.getValue()) {
                    if (!ourList.contains(v)) {
                        ourList.add(v);
                    }
                }
            }
        }
    }
    
    private final List<V> _get(final K k) {
        final List<V> l = this.store.get(k);
        if (l == null) {
            return Collections.emptyList();
        }
        return l;
    }
    
    public boolean containsKey(final K k) {
        return !this.get(k).isEmpty();
    }
    
    public boolean contains(final K k1, final V k2) {
        final List<V> list = this._get(k1);
        return list.contains(k2);
    }
    
    public List<V> remove(final K key) {
        return this.store.remove(key);
    }
    
    public boolean remove(final K key, final V entry) {
        final List<V> list = this.store.get(key);
        if (list == null) {
            return false;
        }
        final boolean retVal = list.remove(entry);
        if (list.isEmpty()) {
            this.store.remove(key);
        }
        return retVal;
    }
    
    public V getOne(final K k) {
        return this.getFirst(k);
    }
    
    private V getFirst(final K k) {
        final List<V> lst = this.store.get(k);
        if (null == lst) {
            return null;
        }
        if (lst.isEmpty()) {
            return null;
        }
        return lst.get(0);
    }
    
    public Set<Map.Entry<K, List<V>>> entrySet() {
        return this.store.entrySet();
    }
    
    public String toCommaSeparatedString() {
        final StringBuilder buf = new StringBuilder();
        for (final Map.Entry<K, List<V>> e : this.entrySet()) {
            for (final V v : e.getValue()) {
                if (buf.length() > 0) {
                    buf.append(',');
                }
                buf.append(e.getKey()).append('=').append(v);
            }
        }
        return buf.toString();
    }
    
    public MultiMap<K, V> clone() throws CloneNotSupportedException {
        super.clone();
        return new MultiMap<K, V>(this);
    }
    
    public int size() {
        return this.store.size();
    }
    
    @Override
    public int hashCode() {
        return this.store.hashCode();
    }
    
    @Override
    public boolean equals(final Object another) {
        if (another == null || !(another instanceof MultiMap)) {
            return false;
        }
        final MultiMap<K, V> other = (MultiMap<K, V>)another;
        return this.store.equals(other.store);
    }
    
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("{");
        for (final K key : this.store.keySet()) {
            builder.append(key).append(": ");
            builder.append(this.store.get(key));
            builder.append(MultiMap.NEWLINE);
        }
        builder.append("}");
        return builder.toString();
    }
    
    static {
        NEWLINE = AccessController.doPrivileged((PrivilegedAction<String>)new PrivilegedAction<String>() {
            @Override
            public String run() {
                return System.getProperty("line.separator");
            }
        });
    }
}
