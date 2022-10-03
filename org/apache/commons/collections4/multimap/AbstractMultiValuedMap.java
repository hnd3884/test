package org.apache.commons.collections4.multimap;

import org.apache.commons.collections4.keyvalue.UnmodifiableMapEntry;
import org.apache.commons.collections4.iterators.AbstractIteratorDecorator;
import java.util.AbstractSet;
import java.util.AbstractMap;
import org.apache.commons.collections4.iterators.IteratorChain;
import org.apache.commons.collections4.keyvalue.AbstractMapEntry;
import org.apache.commons.collections4.iterators.TransformIterator;
import java.util.ArrayList;
import org.apache.commons.collections4.iterators.LazyIteratorChain;
import java.util.AbstractCollection;
import org.apache.commons.collections4.Transformer;
import org.apache.commons.collections4.multiset.AbstractMultiSet;
import org.apache.commons.collections4.IteratorUtils;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import org.apache.commons.collections4.iterators.EmptyMapIterator;
import org.apache.commons.collections4.MapIterator;
import org.apache.commons.collections4.multiset.UnmodifiableMultiSet;
import java.util.Iterator;
import java.util.Set;
import org.apache.commons.collections4.CollectionUtils;
import java.util.Map;
import org.apache.commons.collections4.MultiSet;
import java.util.Collection;
import org.apache.commons.collections4.MultiValuedMap;

public abstract class AbstractMultiValuedMap<K, V> implements MultiValuedMap<K, V>
{
    private transient Collection<V> valuesView;
    private transient EntryValues entryValuesView;
    private transient MultiSet<K> keysMultiSetView;
    private transient AsMap asMapView;
    private transient Map<K, Collection<V>> map;
    
    protected AbstractMultiValuedMap() {
    }
    
    protected AbstractMultiValuedMap(final Map<K, ? extends Collection<V>> map) {
        if (map == null) {
            throw new NullPointerException("Map must not be null.");
        }
        this.map = (Map<K, Collection<V>>)map;
    }
    
    protected Map<K, ? extends Collection<V>> getMap() {
        return this.map;
    }
    
    protected void setMap(final Map<K, ? extends Collection<V>> map) {
        this.map = (Map<K, Collection<V>>)map;
    }
    
    protected abstract Collection<V> createCollection();
    
    @Override
    public boolean containsKey(final Object key) {
        return this.getMap().containsKey(key);
    }
    
    @Override
    public boolean containsValue(final Object value) {
        return this.values().contains(value);
    }
    
    @Override
    public boolean containsMapping(final Object key, final Object value) {
        final Collection<V> coll = (Collection<V>)this.getMap().get(key);
        return coll != null && coll.contains(value);
    }
    
    @Override
    public Collection<Map.Entry<K, V>> entries() {
        return (this.entryValuesView != null) ? this.entryValuesView : (this.entryValuesView = new EntryValues());
    }
    
    @Override
    public Collection<V> get(final K key) {
        return this.wrappedCollection(key);
    }
    
    Collection<V> wrappedCollection(final K key) {
        return new WrappedCollection(key);
    }
    
    @Override
    public Collection<V> remove(final Object key) {
        return CollectionUtils.emptyIfNull((Collection<V>)this.getMap().remove(key));
    }
    
    @Override
    public boolean removeMapping(final Object key, final Object value) {
        final Collection<V> coll = (Collection<V>)this.getMap().get(key);
        if (coll == null) {
            return false;
        }
        final boolean changed = coll.remove(value);
        if (coll.isEmpty()) {
            this.getMap().remove(key);
        }
        return changed;
    }
    
    @Override
    public boolean isEmpty() {
        return this.getMap().isEmpty();
    }
    
    @Override
    public Set<K> keySet() {
        return this.getMap().keySet();
    }
    
    @Override
    public int size() {
        int size = 0;
        for (final Collection<V> col : this.getMap().values()) {
            size += col.size();
        }
        return size;
    }
    
    @Override
    public Collection<V> values() {
        final Collection<V> vs = this.valuesView;
        return (vs != null) ? vs : (this.valuesView = new Values());
    }
    
    @Override
    public void clear() {
        this.getMap().clear();
    }
    
    @Override
    public boolean put(final K key, final V value) {
        Collection<V> coll = (Collection<V>)this.getMap().get(key);
        if (coll != null) {
            return coll.add(value);
        }
        coll = this.createCollection();
        if (coll.add(value)) {
            this.map.put(key, coll);
            return true;
        }
        return false;
    }
    
    @Override
    public boolean putAll(final Map<? extends K, ? extends V> map) {
        if (map == null) {
            throw new NullPointerException("Map must not be null.");
        }
        boolean changed = false;
        for (final Map.Entry<? extends K, ? extends V> entry : map.entrySet()) {
            changed |= this.put(entry.getKey(), entry.getValue());
        }
        return changed;
    }
    
    @Override
    public boolean putAll(final MultiValuedMap<? extends K, ? extends V> map) {
        if (map == null) {
            throw new NullPointerException("Map must not be null.");
        }
        boolean changed = false;
        for (final Map.Entry<? extends K, ? extends V> entry : map.entries()) {
            changed |= this.put(entry.getKey(), entry.getValue());
        }
        return changed;
    }
    
    @Override
    public MultiSet<K> keys() {
        if (this.keysMultiSetView == null) {
            this.keysMultiSetView = UnmodifiableMultiSet.unmodifiableMultiSet((MultiSet<? extends K>)new KeysMultiSet());
        }
        return this.keysMultiSetView;
    }
    
    @Override
    public Map<K, Collection<V>> asMap() {
        return (this.asMapView != null) ? this.asMapView : (this.asMapView = new AsMap(this.map));
    }
    
    @Override
    public boolean putAll(final K key, final Iterable<? extends V> values) {
        if (values == null) {
            throw new NullPointerException("Values must not be null.");
        }
        if (values instanceof Collection) {
            final Collection<? extends V> valueCollection = (Collection)values;
            return !valueCollection.isEmpty() && this.get(key).addAll(valueCollection);
        }
        final Iterator<? extends V> it = values.iterator();
        return it.hasNext() && CollectionUtils.addAll(this.get(key), it);
    }
    
    @Override
    public MapIterator<K, V> mapIterator() {
        if (this.size() == 0) {
            return EmptyMapIterator.emptyMapIterator();
        }
        return new MultiValuedMapIterator();
    }
    
    @Override
    public boolean equals(final Object obj) {
        return this == obj || (obj instanceof MultiValuedMap && this.asMap().equals(((MultiValuedMap)obj).asMap()));
    }
    
    @Override
    public int hashCode() {
        return this.getMap().hashCode();
    }
    
    @Override
    public String toString() {
        return this.getMap().toString();
    }
    
    protected void doWriteObject(final ObjectOutputStream out) throws IOException {
        out.writeInt(this.map.size());
        for (final Map.Entry<K, Collection<V>> entry : this.map.entrySet()) {
            out.writeObject(entry.getKey());
            out.writeInt(entry.getValue().size());
            for (final V value : entry.getValue()) {
                out.writeObject(value);
            }
        }
    }
    
    protected void doReadObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        for (int entrySize = in.readInt(), i = 0; i < entrySize; ++i) {
            final K key = (K)in.readObject();
            final Collection<V> values = this.get(key);
            for (int valueSize = in.readInt(), j = 0; j < valueSize; ++j) {
                final V value = (V)in.readObject();
                values.add(value);
            }
        }
    }
    
    class WrappedCollection implements Collection<V>
    {
        protected final K key;
        
        public WrappedCollection(final K key) {
            this.key = key;
        }
        
        protected Collection<V> getMapping() {
            return (Collection)AbstractMultiValuedMap.this.getMap().get(this.key);
        }
        
        @Override
        public boolean add(final V value) {
            Collection<V> coll = this.getMapping();
            if (coll == null) {
                coll = AbstractMultiValuedMap.this.createCollection();
                AbstractMultiValuedMap.this.map.put(this.key, coll);
            }
            return coll.add(value);
        }
        
        @Override
        public boolean addAll(final Collection<? extends V> other) {
            Collection<V> coll = this.getMapping();
            if (coll == null) {
                coll = AbstractMultiValuedMap.this.createCollection();
                AbstractMultiValuedMap.this.map.put(this.key, coll);
            }
            return coll.addAll(other);
        }
        
        @Override
        public void clear() {
            final Collection<V> coll = this.getMapping();
            if (coll != null) {
                coll.clear();
                AbstractMultiValuedMap.this.remove(this.key);
            }
        }
        
        @Override
        public Iterator<V> iterator() {
            final Collection<V> coll = this.getMapping();
            if (coll == null) {
                return IteratorUtils.EMPTY_ITERATOR;
            }
            return new ValuesIterator(this.key);
        }
        
        @Override
        public int size() {
            final Collection<V> coll = this.getMapping();
            return (coll == null) ? 0 : coll.size();
        }
        
        @Override
        public boolean contains(final Object obj) {
            final Collection<V> coll = this.getMapping();
            return coll != null && coll.contains(obj);
        }
        
        @Override
        public boolean containsAll(final Collection<?> other) {
            final Collection<V> coll = this.getMapping();
            return coll != null && coll.containsAll(other);
        }
        
        @Override
        public boolean isEmpty() {
            final Collection<V> coll = this.getMapping();
            return coll == null || coll.isEmpty();
        }
        
        @Override
        public boolean remove(final Object item) {
            final Collection<V> coll = this.getMapping();
            if (coll == null) {
                return false;
            }
            final boolean result = coll.remove(item);
            if (coll.isEmpty()) {
                AbstractMultiValuedMap.this.remove(this.key);
            }
            return result;
        }
        
        @Override
        public boolean removeAll(final Collection<?> c) {
            final Collection<V> coll = this.getMapping();
            if (coll == null) {
                return false;
            }
            final boolean result = coll.removeAll(c);
            if (coll.isEmpty()) {
                AbstractMultiValuedMap.this.remove(this.key);
            }
            return result;
        }
        
        @Override
        public boolean retainAll(final Collection<?> c) {
            final Collection<V> coll = this.getMapping();
            if (coll == null) {
                return false;
            }
            final boolean result = coll.retainAll(c);
            if (coll.isEmpty()) {
                AbstractMultiValuedMap.this.remove(this.key);
            }
            return result;
        }
        
        @Override
        public Object[] toArray() {
            final Collection<V> coll = this.getMapping();
            if (coll == null) {
                return CollectionUtils.EMPTY_COLLECTION.toArray();
            }
            return coll.toArray();
        }
        
        @Override
        public <T> T[] toArray(final T[] a) {
            final Collection<V> coll = this.getMapping();
            if (coll == null) {
                return CollectionUtils.EMPTY_COLLECTION.toArray(a);
            }
            return coll.toArray(a);
        }
        
        @Override
        public String toString() {
            final Collection<V> coll = this.getMapping();
            if (coll == null) {
                return CollectionUtils.EMPTY_COLLECTION.toString();
            }
            return coll.toString();
        }
    }
    
    private class KeysMultiSet extends AbstractMultiSet<K>
    {
        @Override
        public boolean contains(final Object o) {
            return AbstractMultiValuedMap.this.getMap().containsKey(o);
        }
        
        @Override
        public boolean isEmpty() {
            return AbstractMultiValuedMap.this.getMap().isEmpty();
        }
        
        @Override
        public int size() {
            return AbstractMultiValuedMap.this.size();
        }
        
        @Override
        protected int uniqueElements() {
            return AbstractMultiValuedMap.this.getMap().size();
        }
        
        @Override
        public int getCount(final Object object) {
            int count = 0;
            final Collection<V> col = (Collection<V>)AbstractMultiValuedMap.this.getMap().get(object);
            if (col != null) {
                count = col.size();
            }
            return count;
        }
        
        @Override
        protected Iterator<MultiSet.Entry<K>> createEntrySetIterator() {
            final MapEntryTransformer transformer = new MapEntryTransformer();
            return IteratorUtils.transformedIterator((Iterator<?>)AbstractMultiValuedMap.this.map.entrySet().iterator(), (Transformer<? super Object, ? extends MultiSet.Entry<K>>)transformer);
        }
        
        private final class MapEntryTransformer implements Transformer<Map.Entry<K, Collection<V>>, MultiSet.Entry<K>>
        {
            @Override
            public MultiSet.Entry<K> transform(final Map.Entry<K, Collection<V>> mapEntry) {
                return new AbstractEntry<K>() {
                    @Override
                    public K getElement() {
                        return mapEntry.getKey();
                    }
                    
                    @Override
                    public int getCount() {
                        return mapEntry.getValue().size();
                    }
                };
            }
        }
    }
    
    private class EntryValues extends AbstractCollection<Map.Entry<K, V>>
    {
        @Override
        public Iterator<Map.Entry<K, V>> iterator() {
            return new LazyIteratorChain<Map.Entry<K, V>>() {
                final Collection<K> keysCol = new ArrayList<K>((Collection<? extends K>)AbstractMultiValuedMap.this.getMap().keySet());
                final Iterator<K> keyIterator = this.keysCol.iterator();
                
                @Override
                protected Iterator<? extends Map.Entry<K, V>> nextIterator(final int count) {
                    if (!this.keyIterator.hasNext()) {
                        return null;
                    }
                    final K key = this.keyIterator.next();
                    final Transformer<V, Map.Entry<K, V>> entryTransformer = new Transformer<V, Map.Entry<K, V>>() {
                        @Override
                        public Map.Entry<K, V> transform(final V input) {
                            return new MultiValuedMapEntry(key, input);
                        }
                    };
                    return new TransformIterator<Object, Map.Entry<K, V>>(new ValuesIterator(key), entryTransformer);
                }
            };
        }
        
        @Override
        public int size() {
            return AbstractMultiValuedMap.this.size();
        }
    }
    
    private class MultiValuedMapEntry extends AbstractMapEntry<K, V>
    {
        public MultiValuedMapEntry(final K key, final V value) {
            super(key, value);
        }
        
        @Override
        public V setValue(final V value) {
            throw new UnsupportedOperationException();
        }
    }
    
    private class MultiValuedMapIterator implements MapIterator<K, V>
    {
        private final Iterator<Map.Entry<K, V>> it;
        private Map.Entry<K, V> current;
        
        public MultiValuedMapIterator() {
            this.current = null;
            this.it = AbstractMultiValuedMap.this.entries().iterator();
        }
        
        @Override
        public boolean hasNext() {
            return this.it.hasNext();
        }
        
        @Override
        public K next() {
            this.current = this.it.next();
            return this.current.getKey();
        }
        
        @Override
        public K getKey() {
            if (this.current == null) {
                throw new IllegalStateException();
            }
            return this.current.getKey();
        }
        
        @Override
        public V getValue() {
            if (this.current == null) {
                throw new IllegalStateException();
            }
            return this.current.getValue();
        }
        
        @Override
        public void remove() {
            this.it.remove();
        }
        
        @Override
        public V setValue(final V value) {
            if (this.current == null) {
                throw new IllegalStateException();
            }
            return this.current.setValue(value);
        }
    }
    
    private class Values extends AbstractCollection<V>
    {
        @Override
        public Iterator<V> iterator() {
            final IteratorChain<V> chain = new IteratorChain<V>();
            for (final K k : AbstractMultiValuedMap.this.keySet()) {
                chain.addIterator((Iterator<? extends V>)new ValuesIterator(k));
            }
            return chain;
        }
        
        @Override
        public int size() {
            return AbstractMultiValuedMap.this.size();
        }
        
        @Override
        public void clear() {
            AbstractMultiValuedMap.this.clear();
        }
    }
    
    private class ValuesIterator implements Iterator<V>
    {
        private final Object key;
        private final Collection<V> values;
        private final Iterator<V> iterator;
        
        public ValuesIterator(final Object key) {
            this.key = key;
            this.values = (Collection)AbstractMultiValuedMap.this.getMap().get(key);
            this.iterator = this.values.iterator();
        }
        
        @Override
        public void remove() {
            this.iterator.remove();
            if (this.values.isEmpty()) {
                AbstractMultiValuedMap.this.remove(this.key);
            }
        }
        
        @Override
        public boolean hasNext() {
            return this.iterator.hasNext();
        }
        
        @Override
        public V next() {
            return this.iterator.next();
        }
    }
    
    private class AsMap extends AbstractMap<K, Collection<V>>
    {
        final transient Map<K, Collection<V>> decoratedMap;
        
        AsMap(final Map<K, Collection<V>> map) {
            this.decoratedMap = map;
        }
        
        @Override
        public Set<Map.Entry<K, Collection<V>>> entrySet() {
            return new AsMapEntrySet();
        }
        
        @Override
        public boolean containsKey(final Object key) {
            return this.decoratedMap.containsKey(key);
        }
        
        @Override
        public Collection<V> get(final Object key) {
            final Collection<V> collection = this.decoratedMap.get(key);
            if (collection == null) {
                return null;
            }
            final K k = (K)key;
            return AbstractMultiValuedMap.this.wrappedCollection(k);
        }
        
        @Override
        public Set<K> keySet() {
            return AbstractMultiValuedMap.this.keySet();
        }
        
        @Override
        public int size() {
            return this.decoratedMap.size();
        }
        
        @Override
        public Collection<V> remove(final Object key) {
            final Collection<V> collection = this.decoratedMap.remove(key);
            if (collection == null) {
                return null;
            }
            final Collection<V> output = AbstractMultiValuedMap.this.createCollection();
            output.addAll((Collection<? extends V>)collection);
            collection.clear();
            return output;
        }
        
        @Override
        public boolean equals(final Object object) {
            return this == object || this.decoratedMap.equals(object);
        }
        
        @Override
        public int hashCode() {
            return this.decoratedMap.hashCode();
        }
        
        @Override
        public String toString() {
            return this.decoratedMap.toString();
        }
        
        @Override
        public void clear() {
            AbstractMultiValuedMap.this.clear();
        }
        
        class AsMapEntrySet extends AbstractSet<Map.Entry<K, Collection<V>>>
        {
            @Override
            public Iterator<Map.Entry<K, Collection<V>>> iterator() {
                return (Iterator<Map.Entry<K, Collection<V>>>)new AsMapEntrySetIterator(AsMap.this.decoratedMap.entrySet().iterator());
            }
            
            @Override
            public int size() {
                return AsMap.this.size();
            }
            
            @Override
            public void clear() {
                AsMap.this.clear();
            }
            
            @Override
            public boolean contains(final Object o) {
                return AsMap.this.decoratedMap.entrySet().contains(o);
            }
            
            @Override
            public boolean remove(final Object o) {
                if (!this.contains(o)) {
                    return false;
                }
                final Map.Entry<?, ?> entry = (Map.Entry<?, ?>)o;
                AbstractMultiValuedMap.this.remove(entry.getKey());
                return true;
            }
        }
        
        class AsMapEntrySetIterator extends AbstractIteratorDecorator<Map.Entry<K, Collection<V>>>
        {
            AsMapEntrySetIterator(final Iterator<Map.Entry<K, Collection<V>>> iterator) {
                super(iterator);
            }
            
            @Override
            public Map.Entry<K, Collection<V>> next() {
                final Map.Entry<K, Collection<V>> entry = super.next();
                final K key = entry.getKey();
                return new UnmodifiableMapEntry<K, Collection<V>>(key, AbstractMultiValuedMap.this.wrappedCollection(key));
            }
        }
    }
}
