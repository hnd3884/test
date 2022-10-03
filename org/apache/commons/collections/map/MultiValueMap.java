package org.apache.commons.collections.map;

import java.util.ArrayList;
import org.apache.commons.collections.FunctorException;
import org.apache.commons.collections.iterators.IteratorChain;
import java.util.AbstractCollection;
import org.apache.commons.collections.iterators.EmptyIterator;
import java.util.Iterator;
import java.util.Set;
import java.util.HashMap;
import java.util.Map;
import java.util.Collection;
import org.apache.commons.collections.Factory;
import org.apache.commons.collections.MultiMap;

public class MultiValueMap extends AbstractMapDecorator implements MultiMap
{
    private final Factory collectionFactory;
    private transient Collection values;
    
    public static MultiValueMap decorate(final Map map) {
        return new MultiValueMap(map, new ReflectionFactory(ArrayList.class));
    }
    
    public static MultiValueMap decorate(final Map map, final Class collectionClass) {
        return new MultiValueMap(map, new ReflectionFactory(collectionClass));
    }
    
    public static MultiValueMap decorate(final Map map, final Factory collectionFactory) {
        return new MultiValueMap(map, collectionFactory);
    }
    
    public MultiValueMap() {
        this(new HashMap(), new ReflectionFactory(ArrayList.class));
    }
    
    protected MultiValueMap(final Map map, final Factory collectionFactory) {
        super(map);
        if (collectionFactory == null) {
            throw new IllegalArgumentException("The factory must not be null");
        }
        this.collectionFactory = collectionFactory;
    }
    
    public void clear() {
        this.getMap().clear();
    }
    
    public Object remove(final Object key, final Object value) {
        final Collection valuesForKey = this.getCollection(key);
        if (valuesForKey == null) {
            return null;
        }
        final boolean removed = valuesForKey.remove(value);
        if (!removed) {
            return null;
        }
        if (valuesForKey.isEmpty()) {
            this.remove(key);
        }
        return value;
    }
    
    public boolean containsValue(final Object value) {
        final Set pairs = this.getMap().entrySet();
        if (pairs == null) {
            return false;
        }
        final Iterator pairsIterator = pairs.iterator();
        while (pairsIterator.hasNext()) {
            final Map.Entry keyValuePair = pairsIterator.next();
            final Collection coll = keyValuePair.getValue();
            if (coll.contains(value)) {
                return true;
            }
        }
        return false;
    }
    
    public Object put(final Object key, final Object value) {
        boolean result = false;
        Collection coll = this.getCollection(key);
        if (coll == null) {
            coll = this.createCollection(1);
            result = coll.add(value);
            if (coll.size() > 0) {
                this.getMap().put(key, coll);
                result = true;
            }
        }
        else {
            result = coll.add(value);
        }
        return result ? value : null;
    }
    
    public void putAll(final Map map) {
        if (map instanceof MultiMap) {
            final Iterator it = map.entrySet().iterator();
            while (it.hasNext()) {
                final Map.Entry entry = it.next();
                final Collection coll = entry.getValue();
                this.putAll(entry.getKey(), coll);
            }
        }
        else {
            final Iterator it = map.entrySet().iterator();
            while (it.hasNext()) {
                final Map.Entry entry = it.next();
                this.put(entry.getKey(), entry.getValue());
            }
        }
    }
    
    public Collection values() {
        final Collection vs = this.values;
        return (vs != null) ? vs : (this.values = new Values());
    }
    
    public boolean containsValue(final Object key, final Object value) {
        final Collection coll = this.getCollection(key);
        return coll != null && coll.contains(value);
    }
    
    public Collection getCollection(final Object key) {
        return this.getMap().get(key);
    }
    
    public int size(final Object key) {
        final Collection coll = this.getCollection(key);
        if (coll == null) {
            return 0;
        }
        return coll.size();
    }
    
    public boolean putAll(final Object key, final Collection values) {
        if (values == null || values.size() == 0) {
            return false;
        }
        boolean result = false;
        Collection coll = this.getCollection(key);
        if (coll == null) {
            coll = this.createCollection(values.size());
            coll.addAll(values);
            if (coll.size() > 0) {
                this.getMap().put(key, coll);
                result = true;
            }
        }
        else {
            result = coll.addAll(values);
        }
        return result;
    }
    
    public Iterator iterator(final Object key) {
        if (!this.containsKey(key)) {
            return EmptyIterator.INSTANCE;
        }
        return new ValuesIterator(key);
    }
    
    public int totalSize() {
        int total = 0;
        final Collection values = this.getMap().values();
        final Iterator it = values.iterator();
        while (it.hasNext()) {
            final Collection coll = it.next();
            total += coll.size();
        }
        return total;
    }
    
    protected Collection createCollection(final int size) {
        return (Collection)this.collectionFactory.create();
    }
    
    private class Values extends AbstractCollection
    {
        public Iterator iterator() {
            final IteratorChain chain = new IteratorChain();
            final Iterator it = MultiValueMap.this.keySet().iterator();
            while (it.hasNext()) {
                chain.addIterator(new ValuesIterator(it.next()));
            }
            return chain;
        }
        
        public int size() {
            return MultiValueMap.this.totalSize();
        }
        
        public void clear() {
            MultiValueMap.this.clear();
        }
    }
    
    private class ValuesIterator implements Iterator
    {
        private final Object key;
        private final Collection values;
        private final Iterator iterator;
        
        public ValuesIterator(final Object key) {
            this.key = key;
            this.values = MultiValueMap.this.getCollection(key);
            this.iterator = this.values.iterator();
        }
        
        public void remove() {
            this.iterator.remove();
            if (this.values.isEmpty()) {
                MultiValueMap.this.remove(this.key);
            }
        }
        
        public boolean hasNext() {
            return this.iterator.hasNext();
        }
        
        public Object next() {
            return this.iterator.next();
        }
    }
    
    private static class ReflectionFactory implements Factory
    {
        private final Class clazz;
        
        public ReflectionFactory(final Class clazz) {
            this.clazz = clazz;
        }
        
        public Object create() {
            try {
                return this.clazz.newInstance();
            }
            catch (final Exception ex) {
                throw new FunctorException("Cannot instantiate class: " + this.clazz, ex);
            }
        }
    }
}
