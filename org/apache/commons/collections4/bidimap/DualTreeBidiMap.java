package org.apache.commons.collections4.bidimap;

import java.util.Collection;
import java.util.ArrayList;
import java.util.ListIterator;
import org.apache.commons.collections4.ResettableIterator;
import org.apache.commons.collections4.map.AbstractSortedMapDecorator;
import org.apache.commons.collections4.MapIterator;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import org.apache.commons.collections4.OrderedBidiMap;
import org.apache.commons.collections4.OrderedMapIterator;
import java.util.Iterator;
import org.apache.commons.collections4.OrderedMap;
import java.util.SortedMap;
import org.apache.commons.collections4.BidiMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.Comparator;
import java.io.Serializable;
import org.apache.commons.collections4.SortedBidiMap;

public class DualTreeBidiMap<K, V> extends AbstractDualBidiMap<K, V> implements SortedBidiMap<K, V>, Serializable
{
    private static final long serialVersionUID = 721969328361809L;
    private final Comparator<? super K> comparator;
    private final Comparator<? super V> valueComparator;
    
    public DualTreeBidiMap() {
        super(new TreeMap(), new TreeMap());
        this.comparator = null;
        this.valueComparator = null;
    }
    
    public DualTreeBidiMap(final Map<? extends K, ? extends V> map) {
        super(new TreeMap(), new TreeMap());
        this.putAll(map);
        this.comparator = null;
        this.valueComparator = null;
    }
    
    public DualTreeBidiMap(final Comparator<? super K> keyComparator, final Comparator<? super V> valueComparator) {
        super(new TreeMap(keyComparator), new TreeMap(valueComparator));
        this.comparator = keyComparator;
        this.valueComparator = valueComparator;
    }
    
    protected DualTreeBidiMap(final Map<K, V> normalMap, final Map<V, K> reverseMap, final BidiMap<V, K> inverseBidiMap) {
        super(normalMap, reverseMap, inverseBidiMap);
        this.comparator = ((SortedMap)normalMap).comparator();
        this.valueComparator = ((SortedMap)reverseMap).comparator();
    }
    
    @Override
    protected DualTreeBidiMap<V, K> createBidiMap(final Map<V, K> normalMap, final Map<K, V> reverseMap, final BidiMap<K, V> inverseMap) {
        return (DualTreeBidiMap<V, K>)new DualTreeBidiMap((Map<Object, Object>)normalMap, (Map<Object, Object>)reverseMap, (BidiMap<Object, Object>)inverseMap);
    }
    
    @Override
    public Comparator<? super K> comparator() {
        return ((SortedMap)this.normalMap).comparator();
    }
    
    @Override
    public Comparator<? super V> valueComparator() {
        return ((SortedMap)this.reverseMap).comparator();
    }
    
    @Override
    public K firstKey() {
        return ((SortedMap)this.normalMap).firstKey();
    }
    
    @Override
    public K lastKey() {
        return ((SortedMap)this.normalMap).lastKey();
    }
    
    @Override
    public K nextKey(final K key) {
        if (this.isEmpty()) {
            return null;
        }
        if (this.normalMap instanceof OrderedMap) {
            return ((OrderedMap)this.normalMap).nextKey(key);
        }
        final SortedMap<K, V> sm = (SortedMap)this.normalMap;
        final Iterator<K> it = sm.tailMap(key).keySet().iterator();
        it.next();
        if (it.hasNext()) {
            return it.next();
        }
        return null;
    }
    
    @Override
    public K previousKey(final K key) {
        if (this.isEmpty()) {
            return null;
        }
        if (this.normalMap instanceof OrderedMap) {
            return ((OrderedMap)this.normalMap).previousKey(key);
        }
        final SortedMap<K, V> sm = (SortedMap)this.normalMap;
        final SortedMap<K, V> hm = sm.headMap(key);
        if (hm.isEmpty()) {
            return null;
        }
        return hm.lastKey();
    }
    
    @Override
    public OrderedMapIterator<K, V> mapIterator() {
        return new BidiOrderedMapIterator<K, V>(this);
    }
    
    public SortedBidiMap<V, K> inverseSortedBidiMap() {
        return this.inverseBidiMap();
    }
    
    public OrderedBidiMap<V, K> inverseOrderedBidiMap() {
        return this.inverseBidiMap();
    }
    
    @Override
    public SortedMap<K, V> headMap(final K toKey) {
        final SortedMap<K, V> sub = ((SortedMap)this.normalMap).headMap(toKey);
        return new ViewMap<K, V>(this, sub);
    }
    
    @Override
    public SortedMap<K, V> tailMap(final K fromKey) {
        final SortedMap<K, V> sub = ((SortedMap)this.normalMap).tailMap(fromKey);
        return new ViewMap<K, V>(this, sub);
    }
    
    @Override
    public SortedMap<K, V> subMap(final K fromKey, final K toKey) {
        final SortedMap<K, V> sub = ((SortedMap)this.normalMap).subMap(fromKey, toKey);
        return new ViewMap<K, V>(this, sub);
    }
    
    @Override
    public SortedBidiMap<V, K> inverseBidiMap() {
        return (SortedBidiMap)super.inverseBidiMap();
    }
    
    private void writeObject(final ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeObject(this.normalMap);
    }
    
    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.normalMap = (Map<K, V>)new TreeMap<Object, Object>((Comparator<? super K>)this.comparator);
        this.reverseMap = (Map<V, K>)new TreeMap<Object, Object>((Comparator<? super V>)this.valueComparator);
        final Map<K, V> map = (Map<K, V>)in.readObject();
        this.putAll((Map<? extends K, ? extends V>)map);
    }
    
    protected static class ViewMap<K, V> extends AbstractSortedMapDecorator<K, V>
    {
        protected ViewMap(final DualTreeBidiMap<K, V> bidi, final SortedMap<K, V> sm) {
            super(new DualTreeBidiMap(sm, bidi.reverseMap, bidi.inverseBidiMap));
        }
        
        @Override
        public boolean containsValue(final Object value) {
            return this.decorated().normalMap.containsValue(value);
        }
        
        @Override
        public void clear() {
            final Iterator<K> it = this.keySet().iterator();
            while (it.hasNext()) {
                it.next();
                it.remove();
            }
        }
        
        @Override
        public SortedMap<K, V> headMap(final K toKey) {
            return new ViewMap(this.decorated(), (SortedMap<Object, Object>)super.headMap(toKey));
        }
        
        @Override
        public SortedMap<K, V> tailMap(final K fromKey) {
            return new ViewMap(this.decorated(), (SortedMap<Object, Object>)super.tailMap(fromKey));
        }
        
        @Override
        public SortedMap<K, V> subMap(final K fromKey, final K toKey) {
            return new ViewMap(this.decorated(), (SortedMap<Object, Object>)super.subMap(fromKey, toKey));
        }
        
        @Override
        protected DualTreeBidiMap<K, V> decorated() {
            return (DualTreeBidiMap)super.decorated();
        }
        
        @Override
        public K previousKey(final K key) {
            return this.decorated().previousKey(key);
        }
        
        @Override
        public K nextKey(final K key) {
            return this.decorated().nextKey(key);
        }
    }
    
    protected static class BidiOrderedMapIterator<K, V> implements OrderedMapIterator<K, V>, ResettableIterator<K>
    {
        private final AbstractDualBidiMap<K, V> parent;
        private ListIterator<Map.Entry<K, V>> iterator;
        private Map.Entry<K, V> last;
        
        protected BidiOrderedMapIterator(final AbstractDualBidiMap<K, V> parent) {
            this.last = null;
            this.parent = parent;
            this.iterator = new ArrayList<Map.Entry<K, V>>(parent.entrySet()).listIterator();
        }
        
        @Override
        public boolean hasNext() {
            return this.iterator.hasNext();
        }
        
        @Override
        public K next() {
            this.last = this.iterator.next();
            return this.last.getKey();
        }
        
        @Override
        public boolean hasPrevious() {
            return this.iterator.hasPrevious();
        }
        
        @Override
        public K previous() {
            this.last = this.iterator.previous();
            return this.last.getKey();
        }
        
        @Override
        public void remove() {
            this.iterator.remove();
            this.parent.remove(this.last.getKey());
            this.last = null;
        }
        
        @Override
        public K getKey() {
            if (this.last == null) {
                throw new IllegalStateException("Iterator getKey() can only be called after next() and before remove()");
            }
            return this.last.getKey();
        }
        
        @Override
        public V getValue() {
            if (this.last == null) {
                throw new IllegalStateException("Iterator getValue() can only be called after next() and before remove()");
            }
            return this.last.getValue();
        }
        
        @Override
        public V setValue(final V value) {
            if (this.last == null) {
                throw new IllegalStateException("Iterator setValue() can only be called after next() and before remove()");
            }
            if (this.parent.reverseMap.containsKey(value) && this.parent.reverseMap.get(value) != this.last.getKey()) {
                throw new IllegalArgumentException("Cannot use setValue() when the object being set is already in the map");
            }
            final V oldValue = this.parent.put(this.last.getKey(), value);
            this.last.setValue(value);
            return oldValue;
        }
        
        @Override
        public void reset() {
            this.iterator = new ArrayList<Map.Entry<K, V>>(this.parent.entrySet()).listIterator();
            this.last = null;
        }
        
        @Override
        public String toString() {
            if (this.last != null) {
                return "MapIterator[" + this.getKey() + "=" + this.getValue() + "]";
            }
            return "MapIterator[]";
        }
    }
}
