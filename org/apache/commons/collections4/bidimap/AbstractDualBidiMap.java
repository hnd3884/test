package org.apache.commons.collections4.bidimap;

import org.apache.commons.collections4.ResettableIterator;
import org.apache.commons.collections4.keyvalue.AbstractMapEntryDecorator;
import org.apache.commons.collections4.iterators.AbstractIteratorDecorator;
import org.apache.commons.collections4.collection.AbstractCollectionDecorator;
import java.util.Collection;
import org.apache.commons.collections4.MapIterator;
import java.util.Iterator;
import java.util.Set;
import java.util.Map;
import org.apache.commons.collections4.BidiMap;

public abstract class AbstractDualBidiMap<K, V> implements BidiMap<K, V>
{
    transient Map<K, V> normalMap;
    transient Map<V, K> reverseMap;
    transient BidiMap<V, K> inverseBidiMap;
    transient Set<K> keySet;
    transient Set<V> values;
    transient Set<Map.Entry<K, V>> entrySet;
    
    protected AbstractDualBidiMap() {
        this.inverseBidiMap = null;
        this.keySet = null;
        this.values = null;
        this.entrySet = null;
    }
    
    protected AbstractDualBidiMap(final Map<K, V> normalMap, final Map<V, K> reverseMap) {
        this.inverseBidiMap = null;
        this.keySet = null;
        this.values = null;
        this.entrySet = null;
        this.normalMap = normalMap;
        this.reverseMap = reverseMap;
    }
    
    protected AbstractDualBidiMap(final Map<K, V> normalMap, final Map<V, K> reverseMap, final BidiMap<V, K> inverseBidiMap) {
        this.inverseBidiMap = null;
        this.keySet = null;
        this.values = null;
        this.entrySet = null;
        this.normalMap = normalMap;
        this.reverseMap = reverseMap;
        this.inverseBidiMap = inverseBidiMap;
    }
    
    protected abstract BidiMap<V, K> createBidiMap(final Map<V, K> p0, final Map<K, V> p1, final BidiMap<K, V> p2);
    
    @Override
    public V get(final Object key) {
        return this.normalMap.get(key);
    }
    
    @Override
    public int size() {
        return this.normalMap.size();
    }
    
    @Override
    public boolean isEmpty() {
        return this.normalMap.isEmpty();
    }
    
    @Override
    public boolean containsKey(final Object key) {
        return this.normalMap.containsKey(key);
    }
    
    @Override
    public boolean equals(final Object obj) {
        return this.normalMap.equals(obj);
    }
    
    @Override
    public int hashCode() {
        return this.normalMap.hashCode();
    }
    
    @Override
    public String toString() {
        return this.normalMap.toString();
    }
    
    @Override
    public V put(final K key, final V value) {
        if (this.normalMap.containsKey(key)) {
            this.reverseMap.remove(this.normalMap.get(key));
        }
        if (this.reverseMap.containsKey(value)) {
            this.normalMap.remove(this.reverseMap.get(value));
        }
        final V obj = this.normalMap.put(key, value);
        this.reverseMap.put(value, key);
        return obj;
    }
    
    @Override
    public void putAll(final Map<? extends K, ? extends V> map) {
        for (final Map.Entry<? extends K, ? extends V> entry : map.entrySet()) {
            this.put(entry.getKey(), entry.getValue());
        }
    }
    
    @Override
    public V remove(final Object key) {
        V value = null;
        if (this.normalMap.containsKey(key)) {
            value = this.normalMap.remove(key);
            this.reverseMap.remove(value);
        }
        return value;
    }
    
    @Override
    public void clear() {
        this.normalMap.clear();
        this.reverseMap.clear();
    }
    
    @Override
    public boolean containsValue(final Object value) {
        return this.reverseMap.containsKey(value);
    }
    
    @Override
    public MapIterator<K, V> mapIterator() {
        return new BidiMapIterator<K, V>(this);
    }
    
    @Override
    public K getKey(final Object value) {
        return this.reverseMap.get(value);
    }
    
    @Override
    public K removeValue(final Object value) {
        K key = null;
        if (this.reverseMap.containsKey(value)) {
            key = this.reverseMap.remove(value);
            this.normalMap.remove(key);
        }
        return key;
    }
    
    @Override
    public BidiMap<V, K> inverseBidiMap() {
        if (this.inverseBidiMap == null) {
            this.inverseBidiMap = this.createBidiMap(this.reverseMap, this.normalMap, this);
        }
        return this.inverseBidiMap;
    }
    
    @Override
    public Set<K> keySet() {
        if (this.keySet == null) {
            this.keySet = new KeySet<K>(this);
        }
        return this.keySet;
    }
    
    protected Iterator<K> createKeySetIterator(final Iterator<K> iterator) {
        return (Iterator<K>)new KeySetIterator((Iterator<Object>)iterator, (AbstractDualBidiMap<Object, ?>)this);
    }
    
    @Override
    public Set<V> values() {
        if (this.values == null) {
            this.values = new Values<V>(this);
        }
        return this.values;
    }
    
    protected Iterator<V> createValuesIterator(final Iterator<V> iterator) {
        return (Iterator<V>)new ValuesIterator((Iterator<Object>)iterator, (AbstractDualBidiMap<?, Object>)this);
    }
    
    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        if (this.entrySet == null) {
            this.entrySet = new EntrySet<K, V>(this);
        }
        return this.entrySet;
    }
    
    protected Iterator<Map.Entry<K, V>> createEntrySetIterator(final Iterator<Map.Entry<K, V>> iterator) {
        return (Iterator<Map.Entry<K, V>>)new EntrySetIterator((Iterator<Map.Entry<Object, Object>>)iterator, (AbstractDualBidiMap<Object, Object>)this);
    }
    
    protected abstract static class View<K, V, E> extends AbstractCollectionDecorator<E>
    {
        private static final long serialVersionUID = 4621510560119690639L;
        protected final AbstractDualBidiMap<K, V> parent;
        
        protected View(final Collection<E> coll, final AbstractDualBidiMap<K, V> parent) {
            super(coll);
            this.parent = parent;
        }
        
        @Override
        public boolean equals(final Object object) {
            return object == this || this.decorated().equals(object);
        }
        
        @Override
        public int hashCode() {
            return this.decorated().hashCode();
        }
        
        @Override
        public boolean removeAll(final Collection<?> coll) {
            if (this.parent.isEmpty() || coll.isEmpty()) {
                return false;
            }
            boolean modified = false;
            final Iterator<?> it = coll.iterator();
            while (it.hasNext()) {
                modified |= this.remove(it.next());
            }
            return modified;
        }
        
        @Override
        public boolean retainAll(final Collection<?> coll) {
            if (this.parent.isEmpty()) {
                return false;
            }
            if (coll.isEmpty()) {
                this.parent.clear();
                return true;
            }
            boolean modified = false;
            final Iterator<E> it = this.iterator();
            while (it.hasNext()) {
                if (!coll.contains(it.next())) {
                    it.remove();
                    modified = true;
                }
            }
            return modified;
        }
        
        @Override
        public void clear() {
            this.parent.clear();
        }
    }
    
    protected static class KeySet<K> extends View<K, Object, K> implements Set<K>
    {
        private static final long serialVersionUID = -7107935777385040694L;
        
        protected KeySet(final AbstractDualBidiMap<K, ?> parent) {
            super(parent.normalMap.keySet(), parent);
        }
        
        @Override
        public Iterator<K> iterator() {
            return (Iterator<K>)this.parent.createKeySetIterator((Iterator<K>)super.iterator());
        }
        
        @Override
        public boolean contains(final Object key) {
            return this.parent.normalMap.containsKey(key);
        }
        
        @Override
        public boolean remove(final Object key) {
            if (this.parent.normalMap.containsKey(key)) {
                final Object value = this.parent.normalMap.remove(key);
                this.parent.reverseMap.remove(value);
                return true;
            }
            return false;
        }
    }
    
    protected static class KeySetIterator<K> extends AbstractIteratorDecorator<K>
    {
        protected final AbstractDualBidiMap<K, ?> parent;
        protected K lastKey;
        protected boolean canRemove;
        
        protected KeySetIterator(final Iterator<K> iterator, final AbstractDualBidiMap<K, ?> parent) {
            super(iterator);
            this.lastKey = null;
            this.canRemove = false;
            this.parent = parent;
        }
        
        @Override
        public K next() {
            this.lastKey = super.next();
            this.canRemove = true;
            return this.lastKey;
        }
        
        @Override
        public void remove() {
            if (!this.canRemove) {
                throw new IllegalStateException("Iterator remove() can only be called once after next()");
            }
            final Object value = this.parent.normalMap.get(this.lastKey);
            super.remove();
            this.parent.reverseMap.remove(value);
            this.lastKey = null;
            this.canRemove = false;
        }
    }
    
    protected static class Values<V> extends View<Object, V, V> implements Set<V>
    {
        private static final long serialVersionUID = 4023777119829639864L;
        
        protected Values(final AbstractDualBidiMap<?, V> parent) {
            super(parent.normalMap.values(), parent);
        }
        
        @Override
        public Iterator<V> iterator() {
            return (Iterator<V>)this.parent.createValuesIterator((Iterator<V>)super.iterator());
        }
        
        @Override
        public boolean contains(final Object value) {
            return this.parent.reverseMap.containsKey(value);
        }
        
        @Override
        public boolean remove(final Object value) {
            if (this.parent.reverseMap.containsKey(value)) {
                final Object key = this.parent.reverseMap.remove(value);
                this.parent.normalMap.remove(key);
                return true;
            }
            return false;
        }
    }
    
    protected static class ValuesIterator<V> extends AbstractIteratorDecorator<V>
    {
        protected final AbstractDualBidiMap<Object, V> parent;
        protected V lastValue;
        protected boolean canRemove;
        
        protected ValuesIterator(final Iterator<V> iterator, final AbstractDualBidiMap<?, V> parent) {
            super(iterator);
            this.lastValue = null;
            this.canRemove = false;
            this.parent = (AbstractDualBidiMap<Object, V>)parent;
        }
        
        @Override
        public V next() {
            this.lastValue = super.next();
            this.canRemove = true;
            return this.lastValue;
        }
        
        @Override
        public void remove() {
            if (!this.canRemove) {
                throw new IllegalStateException("Iterator remove() can only be called once after next()");
            }
            super.remove();
            this.parent.reverseMap.remove(this.lastValue);
            this.lastValue = null;
            this.canRemove = false;
        }
    }
    
    protected static class EntrySet<K, V> extends View<K, V, Map.Entry<K, V>> implements Set<Map.Entry<K, V>>
    {
        private static final long serialVersionUID = 4040410962603292348L;
        
        protected EntrySet(final AbstractDualBidiMap<K, V> parent) {
            super(parent.normalMap.entrySet(), parent);
        }
        
        @Override
        public Iterator<Map.Entry<K, V>> iterator() {
            return (Iterator<Map.Entry<K, V>>)this.parent.createEntrySetIterator(super.iterator());
        }
        
        @Override
        public boolean remove(final Object obj) {
            if (!(obj instanceof Map.Entry)) {
                return false;
            }
            final Map.Entry<?, ?> entry = (Map.Entry<?, ?>)obj;
            final Object key = entry.getKey();
            if (this.parent.containsKey(key)) {
                final V value = (V)this.parent.normalMap.get(key);
                if (value == null) {
                    if (entry.getValue() != null) {
                        return false;
                    }
                }
                else if (!value.equals(entry.getValue())) {
                    return false;
                }
                this.parent.normalMap.remove(key);
                this.parent.reverseMap.remove(value);
                return true;
            }
            return false;
        }
    }
    
    protected static class EntrySetIterator<K, V> extends AbstractIteratorDecorator<Map.Entry<K, V>>
    {
        protected final AbstractDualBidiMap<K, V> parent;
        protected Map.Entry<K, V> last;
        protected boolean canRemove;
        
        protected EntrySetIterator(final Iterator<Map.Entry<K, V>> iterator, final AbstractDualBidiMap<K, V> parent) {
            super(iterator);
            this.last = null;
            this.canRemove = false;
            this.parent = parent;
        }
        
        @Override
        public Map.Entry<K, V> next() {
            this.last = new MapEntry<K, V>(super.next(), this.parent);
            this.canRemove = true;
            return this.last;
        }
        
        @Override
        public void remove() {
            if (!this.canRemove) {
                throw new IllegalStateException("Iterator remove() can only be called once after next()");
            }
            final Object value = this.last.getValue();
            super.remove();
            this.parent.reverseMap.remove(value);
            this.last = null;
            this.canRemove = false;
        }
    }
    
    protected static class MapEntry<K, V> extends AbstractMapEntryDecorator<K, V>
    {
        protected final AbstractDualBidiMap<K, V> parent;
        
        protected MapEntry(final Map.Entry<K, V> entry, final AbstractDualBidiMap<K, V> parent) {
            super(entry);
            this.parent = parent;
        }
        
        @Override
        public V setValue(final V value) {
            final K key = this.getKey();
            if (this.parent.reverseMap.containsKey(value) && this.parent.reverseMap.get(value) != key) {
                throw new IllegalArgumentException("Cannot use setValue() when the object being set is already in the map");
            }
            this.parent.put(key, value);
            return super.setValue(value);
        }
    }
    
    protected static class BidiMapIterator<K, V> implements MapIterator<K, V>, ResettableIterator<K>
    {
        protected final AbstractDualBidiMap<K, V> parent;
        protected Iterator<Map.Entry<K, V>> iterator;
        protected Map.Entry<K, V> last;
        protected boolean canRemove;
        
        protected BidiMapIterator(final AbstractDualBidiMap<K, V> parent) {
            this.last = null;
            this.canRemove = false;
            this.parent = parent;
            this.iterator = parent.normalMap.entrySet().iterator();
        }
        
        @Override
        public boolean hasNext() {
            return this.iterator.hasNext();
        }
        
        @Override
        public K next() {
            this.last = this.iterator.next();
            this.canRemove = true;
            return this.last.getKey();
        }
        
        @Override
        public void remove() {
            if (!this.canRemove) {
                throw new IllegalStateException("Iterator remove() can only be called once after next()");
            }
            final V value = this.last.getValue();
            this.iterator.remove();
            this.parent.reverseMap.remove(value);
            this.last = null;
            this.canRemove = false;
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
            return this.parent.put(this.last.getKey(), value);
        }
        
        @Override
        public void reset() {
            this.iterator = this.parent.normalMap.entrySet().iterator();
            this.last = null;
            this.canRemove = false;
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
