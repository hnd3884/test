package org.apache.commons.collections4.map;

import java.util.ListIterator;
import org.apache.commons.collections4.ResettableIterator;
import org.apache.commons.collections4.keyvalue.AbstractMapEntry;
import java.util.AbstractSet;
import org.apache.commons.collections4.iterators.AbstractUntypedIteratorDecorator;
import java.util.AbstractList;
import org.apache.commons.collections4.MapIterator;
import org.apache.commons.collections4.list.UnmodifiableList;
import java.util.Set;
import java.util.Iterator;
import java.util.NoSuchElementException;
import org.apache.commons.collections4.OrderedMapIterator;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.io.Serializable;
import org.apache.commons.collections4.OrderedMap;

public class ListOrderedMap<K, V> extends AbstractMapDecorator<K, V> implements OrderedMap<K, V>, Serializable
{
    private static final long serialVersionUID = 2728177751851003750L;
    private final List<K> insertOrder;
    
    public static <K, V> ListOrderedMap<K, V> listOrderedMap(final Map<K, V> map) {
        return new ListOrderedMap<K, V>(map);
    }
    
    public ListOrderedMap() {
        this((Map)new HashMap());
    }
    
    protected ListOrderedMap(final Map<K, V> map) {
        super(map);
        (this.insertOrder = new ArrayList<K>()).addAll((Collection<? extends K>)this.decorated().keySet());
    }
    
    private void writeObject(final ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeObject(this.map);
    }
    
    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.map = (Map)in.readObject();
    }
    
    @Override
    public OrderedMapIterator<K, V> mapIterator() {
        return new ListOrderedMapIterator<K, V>(this);
    }
    
    @Override
    public K firstKey() {
        if (this.size() == 0) {
            throw new NoSuchElementException("Map is empty");
        }
        return this.insertOrder.get(0);
    }
    
    @Override
    public K lastKey() {
        if (this.size() == 0) {
            throw new NoSuchElementException("Map is empty");
        }
        return this.insertOrder.get(this.size() - 1);
    }
    
    @Override
    public K nextKey(final Object key) {
        final int index = this.insertOrder.indexOf(key);
        if (index >= 0 && index < this.size() - 1) {
            return this.insertOrder.get(index + 1);
        }
        return null;
    }
    
    @Override
    public K previousKey(final Object key) {
        final int index = this.insertOrder.indexOf(key);
        if (index > 0) {
            return this.insertOrder.get(index - 1);
        }
        return null;
    }
    
    @Override
    public V put(final K key, final V value) {
        if (this.decorated().containsKey(key)) {
            return this.decorated().put(key, value);
        }
        final V result = this.decorated().put(key, value);
        this.insertOrder.add(key);
        return result;
    }
    
    @Override
    public void putAll(final Map<? extends K, ? extends V> map) {
        for (final Map.Entry<? extends K, ? extends V> entry : map.entrySet()) {
            this.put(entry.getKey(), entry.getValue());
        }
    }
    
    public void putAll(int index, final Map<? extends K, ? extends V> map) {
        if (index < 0 || index > this.insertOrder.size()) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + this.insertOrder.size());
        }
        for (final Map.Entry<? extends K, ? extends V> entry : map.entrySet()) {
            final K key = (K)entry.getKey();
            final boolean contains = this.containsKey(key);
            this.put(index, entry.getKey(), entry.getValue());
            if (!contains) {
                ++index;
            }
            else {
                index = this.indexOf(entry.getKey()) + 1;
            }
        }
    }
    
    @Override
    public V remove(final Object key) {
        V result = null;
        if (this.decorated().containsKey(key)) {
            result = this.decorated().remove(key);
            this.insertOrder.remove(key);
        }
        return result;
    }
    
    @Override
    public void clear() {
        this.decorated().clear();
        this.insertOrder.clear();
    }
    
    @Override
    public Set<K> keySet() {
        return new KeySetView<K>(this);
    }
    
    public List<K> keyList() {
        return UnmodifiableList.unmodifiableList((List<? extends K>)this.insertOrder);
    }
    
    @Override
    public Collection<V> values() {
        return new ValuesView<V>(this);
    }
    
    public List<V> valueList() {
        return new ValuesView<V>(this);
    }
    
    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        return (Set<Map.Entry<K, V>>)new EntrySetView((ListOrderedMap<Object, Object>)this, (List<Object>)this.insertOrder);
    }
    
    @Override
    public String toString() {
        if (this.isEmpty()) {
            return "{}";
        }
        final StringBuilder buf = new StringBuilder();
        buf.append('{');
        boolean first = true;
        for (final Map.Entry<K, V> entry : this.entrySet()) {
            final K key = entry.getKey();
            final V value = entry.getValue();
            if (first) {
                first = false;
            }
            else {
                buf.append(", ");
            }
            buf.append((key == this) ? "(this Map)" : key);
            buf.append('=');
            buf.append((value == this) ? "(this Map)" : value);
        }
        buf.append('}');
        return buf.toString();
    }
    
    public K get(final int index) {
        return this.insertOrder.get(index);
    }
    
    public V getValue(final int index) {
        return this.get(this.insertOrder.get(index));
    }
    
    public int indexOf(final Object key) {
        return this.insertOrder.indexOf(key);
    }
    
    public V setValue(final int index, final V value) {
        final K key = this.insertOrder.get(index);
        return this.put(key, value);
    }
    
    public V put(int index, final K key, final V value) {
        if (index < 0 || index > this.insertOrder.size()) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + this.insertOrder.size());
        }
        final Map<K, V> m = this.decorated();
        if (m.containsKey(key)) {
            final V result = m.remove(key);
            final int pos = this.insertOrder.indexOf(key);
            this.insertOrder.remove(pos);
            if (pos < index) {
                --index;
            }
            this.insertOrder.add(index, key);
            m.put(key, value);
            return result;
        }
        this.insertOrder.add(index, key);
        m.put(key, value);
        return null;
    }
    
    public V remove(final int index) {
        return this.remove(this.get(index));
    }
    
    public List<K> asList() {
        return this.keyList();
    }
    
    static class ValuesView<V> extends AbstractList<V>
    {
        private final ListOrderedMap<Object, V> parent;
        
        ValuesView(final ListOrderedMap<?, V> parent) {
            this.parent = (ListOrderedMap<Object, V>)parent;
        }
        
        @Override
        public int size() {
            return this.parent.size();
        }
        
        @Override
        public boolean contains(final Object value) {
            return this.parent.containsValue(value);
        }
        
        @Override
        public void clear() {
            this.parent.clear();
        }
        
        @Override
        public Iterator<V> iterator() {
            return new AbstractUntypedIteratorDecorator<Map.Entry<Object, V>, V>(this.parent.entrySet().iterator()) {
                @Override
                public V next() {
                    return (V)this.getIterator().next().getValue();
                }
            };
        }
        
        @Override
        public V get(final int index) {
            return this.parent.getValue(index);
        }
        
        @Override
        public V set(final int index, final V value) {
            return this.parent.setValue(index, value);
        }
        
        @Override
        public V remove(final int index) {
            return this.parent.remove(index);
        }
    }
    
    static class KeySetView<K> extends AbstractSet<K>
    {
        private final ListOrderedMap<K, Object> parent;
        
        KeySetView(final ListOrderedMap<K, ?> parent) {
            this.parent = (ListOrderedMap<K, Object>)parent;
        }
        
        @Override
        public int size() {
            return this.parent.size();
        }
        
        @Override
        public boolean contains(final Object value) {
            return this.parent.containsKey(value);
        }
        
        @Override
        public void clear() {
            this.parent.clear();
        }
        
        @Override
        public Iterator<K> iterator() {
            return new AbstractUntypedIteratorDecorator<Map.Entry<K, Object>, K>(this.parent.entrySet().iterator()) {
                @Override
                public K next() {
                    return (K)this.getIterator().next().getKey();
                }
            };
        }
    }
    
    static class EntrySetView<K, V> extends AbstractSet<Map.Entry<K, V>>
    {
        private final ListOrderedMap<K, V> parent;
        private final List<K> insertOrder;
        private Set<Map.Entry<K, V>> entrySet;
        
        public EntrySetView(final ListOrderedMap<K, V> parent, final List<K> insertOrder) {
            this.parent = parent;
            this.insertOrder = insertOrder;
        }
        
        private Set<Map.Entry<K, V>> getEntrySet() {
            if (this.entrySet == null) {
                this.entrySet = this.parent.decorated().entrySet();
            }
            return this.entrySet;
        }
        
        @Override
        public int size() {
            return this.parent.size();
        }
        
        @Override
        public boolean isEmpty() {
            return this.parent.isEmpty();
        }
        
        @Override
        public boolean contains(final Object obj) {
            return this.getEntrySet().contains(obj);
        }
        
        @Override
        public boolean containsAll(final Collection<?> coll) {
            return this.getEntrySet().containsAll(coll);
        }
        
        @Override
        public boolean remove(final Object obj) {
            if (!(obj instanceof Map.Entry)) {
                return false;
            }
            if (this.getEntrySet().contains(obj)) {
                final Object key = ((Map.Entry)obj).getKey();
                this.parent.remove(key);
                return true;
            }
            return false;
        }
        
        @Override
        public void clear() {
            this.parent.clear();
        }
        
        @Override
        public boolean equals(final Object obj) {
            return obj == this || this.getEntrySet().equals(obj);
        }
        
        @Override
        public int hashCode() {
            return this.getEntrySet().hashCode();
        }
        
        @Override
        public String toString() {
            return this.getEntrySet().toString();
        }
        
        @Override
        public Iterator<Map.Entry<K, V>> iterator() {
            return (Iterator<Map.Entry<K, V>>)new ListOrderedIterator((ListOrderedMap<Object, Object>)this.parent, (List<Object>)this.insertOrder);
        }
    }
    
    static class ListOrderedIterator<K, V> extends AbstractUntypedIteratorDecorator<K, Map.Entry<K, V>>
    {
        private final ListOrderedMap<K, V> parent;
        private K last;
        
        ListOrderedIterator(final ListOrderedMap<K, V> parent, final List<K> insertOrder) {
            super(insertOrder.iterator());
            this.last = null;
            this.parent = parent;
        }
        
        @Override
        public Map.Entry<K, V> next() {
            this.last = this.getIterator().next();
            return new ListOrderedMapEntry<K, V>(this.parent, this.last);
        }
        
        @Override
        public void remove() {
            super.remove();
            this.parent.decorated().remove(this.last);
        }
    }
    
    static class ListOrderedMapEntry<K, V> extends AbstractMapEntry<K, V>
    {
        private final ListOrderedMap<K, V> parent;
        
        ListOrderedMapEntry(final ListOrderedMap<K, V> parent, final K key) {
            super(key, null);
            this.parent = parent;
        }
        
        @Override
        public V getValue() {
            return this.parent.get(this.getKey());
        }
        
        @Override
        public V setValue(final V value) {
            return this.parent.decorated().put(this.getKey(), value);
        }
    }
    
    static class ListOrderedMapIterator<K, V> implements OrderedMapIterator<K, V>, ResettableIterator<K>
    {
        private final ListOrderedMap<K, V> parent;
        private ListIterator<K> iterator;
        private K last;
        private boolean readable;
        
        ListOrderedMapIterator(final ListOrderedMap<K, V> parent) {
            this.last = null;
            this.readable = false;
            this.parent = parent;
            this.iterator = ((ListOrderedMap<Object, Object>)parent).insertOrder.listIterator();
        }
        
        @Override
        public boolean hasNext() {
            return this.iterator.hasNext();
        }
        
        @Override
        public K next() {
            this.last = this.iterator.next();
            this.readable = true;
            return this.last;
        }
        
        @Override
        public boolean hasPrevious() {
            return this.iterator.hasPrevious();
        }
        
        @Override
        public K previous() {
            this.last = this.iterator.previous();
            this.readable = true;
            return this.last;
        }
        
        @Override
        public void remove() {
            if (!this.readable) {
                throw new IllegalStateException("remove() can only be called once after next()");
            }
            this.iterator.remove();
            this.parent.map.remove(this.last);
            this.readable = false;
        }
        
        @Override
        public K getKey() {
            if (!this.readable) {
                throw new IllegalStateException("getKey() can only be called after next() and before remove()");
            }
            return this.last;
        }
        
        @Override
        public V getValue() {
            if (!this.readable) {
                throw new IllegalStateException("getValue() can only be called after next() and before remove()");
            }
            return this.parent.get(this.last);
        }
        
        @Override
        public V setValue(final V value) {
            if (!this.readable) {
                throw new IllegalStateException("setValue() can only be called after next() and before remove()");
            }
            return this.parent.map.put(this.last, value);
        }
        
        @Override
        public void reset() {
            this.iterator = ((ListOrderedMap<Object, Object>)this.parent).insertOrder.listIterator();
            this.last = null;
            this.readable = false;
        }
        
        @Override
        public String toString() {
            if (this.readable) {
                return "Iterator[" + this.getKey() + "=" + this.getValue() + "]";
            }
            return "Iterator[]";
        }
    }
}
