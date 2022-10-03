package org.apache.commons.collections4.map;

import org.apache.commons.collections4.FunctorException;
import org.apache.commons.collections4.iterators.IteratorChain;
import java.util.AbstractCollection;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.iterators.TransformIterator;
import org.apache.commons.collections4.Transformer;
import org.apache.commons.collections4.iterators.LazyIteratorChain;
import org.apache.commons.collections4.iterators.EmptyIterator;
import java.util.Iterator;
import java.util.Set;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;
import java.util.Collection;
import org.apache.commons.collections4.Factory;
import java.io.Serializable;
import org.apache.commons.collections4.MultiMap;

@Deprecated
public class MultiValueMap<K, V> extends AbstractMapDecorator<K, Object> implements MultiMap<K, V>, Serializable
{
    private static final long serialVersionUID = -2214159910087182007L;
    private final Factory<? extends Collection<V>> collectionFactory;
    private transient Collection<V> valuesView;
    
    public static <K, V> MultiValueMap<K, V> multiValueMap(final Map<K, ? super Collection<V>> map) {
        return multiValueMap(map, ArrayList.class);
    }
    
    public static <K, V, C extends Collection<V>> MultiValueMap<K, V> multiValueMap(final Map<K, ? super C> map, final Class<C> collectionClass) {
        return new MultiValueMap<K, V>((Map<K, ? super C>)map, (Factory<C>)new ReflectionFactory<Collection<V>>((Class<C>)collectionClass));
    }
    
    public static <K, V, C extends Collection<V>> MultiValueMap<K, V> multiValueMap(final Map<K, ? super C> map, final Factory<C> collectionFactory) {
        return new MultiValueMap<K, V>((Map<K, ? super C>)map, (Factory<C>)collectionFactory);
    }
    
    public MultiValueMap() {
        this((Map)new HashMap(), (Factory)new ReflectionFactory(ArrayList.class));
    }
    
    protected <C extends Collection<V>> MultiValueMap(final Map<K, ? super C> map, final Factory<C> collectionFactory) {
        super((Map<K, Object>)map);
        if (collectionFactory == null) {
            throw new IllegalArgumentException("The factory must not be null");
        }
        this.collectionFactory = collectionFactory;
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
    public void clear() {
        this.decorated().clear();
    }
    
    @Override
    public boolean removeMapping(final Object key, final Object value) {
        final Collection<V> valuesForKey = this.getCollection(key);
        if (valuesForKey == null) {
            return false;
        }
        final boolean removed = valuesForKey.remove(value);
        if (!removed) {
            return false;
        }
        if (valuesForKey.isEmpty()) {
            this.remove(key);
        }
        return true;
    }
    
    @Override
    public boolean containsValue(final Object value) {
        final Set<Map.Entry<K, Object>> pairs = this.decorated().entrySet();
        if (pairs != null) {
            for (final Map.Entry<K, Object> entry : pairs) {
                if (entry.getValue().contains(value)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    @Override
    public Object put(final K key, final Object value) {
        boolean result = false;
        Collection<V> coll = this.getCollection(key);
        if (coll == null) {
            coll = this.createCollection(1);
            coll.add((V)value);
            if (coll.size() > 0) {
                this.decorated().put(key, coll);
                result = true;
            }
        }
        else {
            result = coll.add((V)value);
        }
        return result ? value : null;
    }
    
    @Override
    public void putAll(final Map<? extends K, ?> map) {
        if (map instanceof MultiMap) {
            for (final Map.Entry<? extends K, Object> entry : map.entrySet()) {
                this.putAll(entry.getKey(), entry.getValue());
            }
        }
        else {
            for (final Map.Entry<? extends K, ?> entry2 : map.entrySet()) {
                this.put(entry2.getKey(), entry2.getValue());
            }
        }
    }
    
    @Override
    public Set<Map.Entry<K, Object>> entrySet() {
        return super.entrySet();
    }
    
    @Override
    public Collection<Object> values() {
        final Collection<V> vs = this.valuesView;
        return (Collection<Object>)((vs != null) ? vs : (this.valuesView = new Values()));
    }
    
    public boolean containsValue(final Object key, final Object value) {
        final Collection<V> coll = this.getCollection(key);
        return coll != null && coll.contains(value);
    }
    
    public Collection<V> getCollection(final Object key) {
        return this.decorated().get(key);
    }
    
    public int size(final Object key) {
        final Collection<V> coll = this.getCollection(key);
        if (coll == null) {
            return 0;
        }
        return coll.size();
    }
    
    public boolean putAll(final K key, final Collection<V> values) {
        if (values == null || values.size() == 0) {
            return false;
        }
        boolean result = false;
        Collection<V> coll = this.getCollection(key);
        if (coll == null) {
            coll = this.createCollection(values.size());
            coll.addAll((Collection<? extends V>)values);
            if (coll.size() > 0) {
                this.decorated().put(key, coll);
                result = true;
            }
        }
        else {
            result = coll.addAll((Collection<? extends V>)values);
        }
        return result;
    }
    
    public Iterator<V> iterator(final Object key) {
        if (!this.containsKey(key)) {
            return EmptyIterator.emptyIterator();
        }
        return new ValuesIterator(key);
    }
    
    public Iterator<Map.Entry<K, V>> iterator() {
        final Collection<K> allKeys = new ArrayList<K>((Collection<? extends K>)this.keySet());
        final Iterator<K> keyIterator = allKeys.iterator();
        return new LazyIteratorChain<Map.Entry<K, V>>() {
            @Override
            protected Iterator<? extends Map.Entry<K, V>> nextIterator(final int count) {
                if (!keyIterator.hasNext()) {
                    return null;
                }
                final K key = keyIterator.next();
                final Transformer<V, Map.Entry<K, V>> transformer = new Transformer<V, Map.Entry<K, V>>() {
                    @Override
                    public Map.Entry<K, V> transform(final V input) {
                        return new Map.Entry<K, V>() {
                            @Override
                            public K getKey() {
                                return key;
                            }
                            
                            @Override
                            public V getValue() {
                                return input;
                            }
                            
                            @Override
                            public V setValue(final V value) {
                                throw new UnsupportedOperationException();
                            }
                        };
                    }
                };
                return new TransformIterator<Object, Map.Entry<K, V>>(new ValuesIterator(key), transformer);
            }
        };
    }
    
    public int totalSize() {
        int total = 0;
        for (final Object v : this.decorated().values()) {
            total += CollectionUtils.size(v);
        }
        return total;
    }
    
    protected Collection<V> createCollection(final int size) {
        return (Collection)this.collectionFactory.create();
    }
    
    private class Values extends AbstractCollection<V>
    {
        @Override
        public Iterator<V> iterator() {
            final IteratorChain<V> chain = new IteratorChain<V>();
            for (final K k : MultiValueMap.this.keySet()) {
                chain.addIterator((Iterator<? extends V>)new ValuesIterator(k));
            }
            return chain;
        }
        
        @Override
        public int size() {
            return MultiValueMap.this.totalSize();
        }
        
        @Override
        public void clear() {
            MultiValueMap.this.clear();
        }
    }
    
    private class ValuesIterator implements Iterator<V>
    {
        private final Object key;
        private final Collection<V> values;
        private final Iterator<V> iterator;
        
        public ValuesIterator(final Object key) {
            this.key = key;
            this.values = MultiValueMap.this.getCollection(key);
            this.iterator = this.values.iterator();
        }
        
        @Override
        public void remove() {
            this.iterator.remove();
            if (this.values.isEmpty()) {
                MultiValueMap.this.remove(this.key);
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
    
    private static class ReflectionFactory<T extends Collection<?>> implements Factory<T>, Serializable
    {
        private static final long serialVersionUID = 2986114157496788874L;
        private final Class<T> clazz;
        
        public ReflectionFactory(final Class<T> clazz) {
            this.clazz = clazz;
        }
        
        @Override
        public T create() {
            try {
                return this.clazz.newInstance();
            }
            catch (final Exception ex) {
                throw new FunctorException("Cannot instantiate class: " + this.clazz, ex);
            }
        }
        
        private void readObject(final ObjectInputStream is) throws IOException, ClassNotFoundException {
            is.defaultReadObject();
            if (this.clazz != null && !Collection.class.isAssignableFrom(this.clazz)) {
                throw new UnsupportedOperationException();
            }
        }
    }
}
