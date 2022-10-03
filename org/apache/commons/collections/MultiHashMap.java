package org.apache.commons.collections;

import java.util.NoSuchElementException;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Set;
import org.apache.commons.collections.iterators.EmptyIterator;
import java.io.IOException;
import java.util.Iterator;
import java.io.ObjectInputStream;
import java.util.Map;
import java.util.Collection;
import java.util.HashMap;

public class MultiHashMap extends HashMap implements MultiMap
{
    private transient Collection values;
    private static final long serialVersionUID = 1943563828307035349L;
    
    public MultiHashMap() {
        this.values = null;
    }
    
    public MultiHashMap(final int initialCapacity) {
        super(initialCapacity);
        this.values = null;
    }
    
    public MultiHashMap(final int initialCapacity, final float loadFactor) {
        super(initialCapacity, loadFactor);
        this.values = null;
    }
    
    public MultiHashMap(final Map mapToCopy) {
        super((int)(mapToCopy.size() * 1.4f));
        this.values = null;
        this.putAll(mapToCopy);
    }
    
    private void readObject(final ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        String version = "1.2";
        try {
            version = System.getProperty("java.version");
        }
        catch (final SecurityException ex) {}
        if (version.startsWith("1.2") || version.startsWith("1.3")) {
            final Iterator iterator = this.entrySet().iterator();
            while (iterator.hasNext()) {
                final Map.Entry entry = iterator.next();
                super.put(entry.getKey(), entry.getValue().iterator().next());
            }
        }
    }
    
    public int totalSize() {
        int total = 0;
        final Collection values = super.values();
        final Iterator it = values.iterator();
        while (it.hasNext()) {
            final Collection coll = it.next();
            total += coll.size();
        }
        return total;
    }
    
    public Collection getCollection(final Object key) {
        return this.get(key);
    }
    
    public int size(final Object key) {
        final Collection coll = this.getCollection(key);
        if (coll == null) {
            return 0;
        }
        return coll.size();
    }
    
    public Iterator iterator(final Object key) {
        final Collection coll = this.getCollection(key);
        if (coll == null) {
            return EmptyIterator.INSTANCE;
        }
        return coll.iterator();
    }
    
    public Object put(final Object key, final Object value) {
        Collection coll = this.getCollection(key);
        if (coll == null) {
            coll = this.createCollection(null);
            super.put(key, coll);
        }
        final boolean results = coll.add(value);
        return results ? value : null;
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
    
    public boolean putAll(final Object key, final Collection values) {
        if (values == null || values.size() == 0) {
            return false;
        }
        Collection coll = this.getCollection(key);
        if (coll != null) {
            return coll.addAll(values);
        }
        coll = this.createCollection(values);
        if (coll.size() == 0) {
            return false;
        }
        super.put(key, coll);
        return true;
    }
    
    public boolean containsValue(final Object value) {
        final Set pairs = super.entrySet();
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
    
    public boolean containsValue(final Object key, final Object value) {
        final Collection coll = this.getCollection(key);
        return coll != null && coll.contains(value);
    }
    
    public Object remove(final Object key, final Object item) {
        final Collection valuesForKey = this.getCollection(key);
        if (valuesForKey == null) {
            return null;
        }
        final boolean removed = valuesForKey.remove(item);
        if (!removed) {
            return null;
        }
        if (valuesForKey.isEmpty()) {
            this.remove(key);
        }
        return item;
    }
    
    public void clear() {
        final Set pairs = super.entrySet();
        final Iterator pairsIterator = pairs.iterator();
        while (pairsIterator.hasNext()) {
            final Map.Entry keyValuePair = pairsIterator.next();
            final Collection coll = keyValuePair.getValue();
            coll.clear();
        }
        super.clear();
    }
    
    public Collection values() {
        final Collection vs = this.values;
        return (vs != null) ? vs : (this.values = new Values());
    }
    
    Iterator superValuesIterator() {
        return super.values().iterator();
    }
    
    public Object clone() {
        final MultiHashMap cloned = (MultiHashMap)super.clone();
        final Iterator it = cloned.entrySet().iterator();
        while (it.hasNext()) {
            final Map.Entry entry = it.next();
            final Collection coll = entry.getValue();
            final Collection newColl = this.createCollection(coll);
            entry.setValue(newColl);
        }
        return cloned;
    }
    
    protected Collection createCollection(final Collection coll) {
        if (coll == null) {
            return new ArrayList();
        }
        return new ArrayList(coll);
    }
    
    private class Values extends AbstractCollection
    {
        public Iterator iterator() {
            return new ValueIterator();
        }
        
        public int size() {
            int compt = 0;
            final Iterator it = this.iterator();
            while (it.hasNext()) {
                it.next();
                ++compt;
            }
            return compt;
        }
        
        public void clear() {
            MultiHashMap.this.clear();
        }
    }
    
    private class ValueIterator implements Iterator
    {
        private Iterator backedIterator;
        private Iterator tempIterator;
        
        private ValueIterator() {
            this.backedIterator = MultiHashMap.this.superValuesIterator();
        }
        
        private boolean searchNextIterator() {
            while (this.tempIterator == null || !this.tempIterator.hasNext()) {
                if (!this.backedIterator.hasNext()) {
                    return false;
                }
                this.tempIterator = this.backedIterator.next().iterator();
            }
            return true;
        }
        
        public boolean hasNext() {
            return this.searchNextIterator();
        }
        
        public Object next() {
            if (!this.searchNextIterator()) {
                throw new NoSuchElementException();
            }
            return this.tempIterator.next();
        }
        
        public void remove() {
            if (this.tempIterator == null) {
                throw new IllegalStateException();
            }
            this.tempIterator.remove();
        }
    }
}
