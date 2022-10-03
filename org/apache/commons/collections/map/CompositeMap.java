package org.apache.commons.collections.map;

import java.util.Iterator;
import org.apache.commons.collections.collection.CompositeCollection;
import org.apache.commons.collections.set.CompositeSet;
import java.util.Set;
import java.util.Collection;
import org.apache.commons.collections.CollectionUtils;
import java.util.Map;

public class CompositeMap implements Map
{
    private Map[] composite;
    private MapMutator mutator;
    
    public CompositeMap() {
        this(new Map[0], (MapMutator)null);
    }
    
    public CompositeMap(final Map one, final Map two) {
        this(new Map[] { one, two }, (MapMutator)null);
    }
    
    public CompositeMap(final Map one, final Map two, final MapMutator mutator) {
        this(new Map[] { one, two }, mutator);
    }
    
    public CompositeMap(final Map[] composite) {
        this(composite, (MapMutator)null);
    }
    
    public CompositeMap(final Map[] composite, final MapMutator mutator) {
        this.mutator = mutator;
        this.composite = new Map[0];
        for (int i = composite.length - 1; i >= 0; --i) {
            this.addComposited(composite[i]);
        }
    }
    
    public void setMutator(final MapMutator mutator) {
        this.mutator = mutator;
    }
    
    public synchronized void addComposited(final Map map) throws IllegalArgumentException {
        for (int i = this.composite.length - 1; i >= 0; --i) {
            final Collection intersect = CollectionUtils.intersection(this.composite[i].keySet(), map.keySet());
            if (intersect.size() != 0) {
                if (this.mutator == null) {
                    throw new IllegalArgumentException("Key collision adding Map to CompositeMap");
                }
                this.mutator.resolveCollision(this, this.composite[i], map, intersect);
            }
        }
        final Map[] temp = new Map[this.composite.length + 1];
        System.arraycopy(this.composite, 0, temp, 0, this.composite.length);
        temp[temp.length - 1] = map;
        this.composite = temp;
    }
    
    public synchronized Map removeComposited(final Map map) {
        for (int size = this.composite.length, i = 0; i < size; ++i) {
            if (this.composite[i].equals(map)) {
                final Map[] temp = new Map[size - 1];
                System.arraycopy(this.composite, 0, temp, 0, i);
                System.arraycopy(this.composite, i + 1, temp, i, size - i - 1);
                this.composite = temp;
                return map;
            }
        }
        return null;
    }
    
    public void clear() {
        for (int i = this.composite.length - 1; i >= 0; --i) {
            this.composite[i].clear();
        }
    }
    
    public boolean containsKey(final Object key) {
        for (int i = this.composite.length - 1; i >= 0; --i) {
            if (this.composite[i].containsKey(key)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean containsValue(final Object value) {
        for (int i = this.composite.length - 1; i >= 0; --i) {
            if (this.composite[i].containsValue(value)) {
                return true;
            }
        }
        return false;
    }
    
    public Set entrySet() {
        final CompositeSet entries = new CompositeSet();
        for (int i = this.composite.length - 1; i >= 0; --i) {
            entries.addComposited(this.composite[i].entrySet());
        }
        return entries;
    }
    
    public Object get(final Object key) {
        for (int i = this.composite.length - 1; i >= 0; --i) {
            if (this.composite[i].containsKey(key)) {
                return this.composite[i].get(key);
            }
        }
        return null;
    }
    
    public boolean isEmpty() {
        for (int i = this.composite.length - 1; i >= 0; --i) {
            if (!this.composite[i].isEmpty()) {
                return false;
            }
        }
        return true;
    }
    
    public Set keySet() {
        final CompositeSet keys = new CompositeSet();
        for (int i = this.composite.length - 1; i >= 0; --i) {
            keys.addComposited(this.composite[i].keySet());
        }
        return keys;
    }
    
    public Object put(final Object key, final Object value) {
        if (this.mutator == null) {
            throw new UnsupportedOperationException("No mutator specified");
        }
        return this.mutator.put(this, this.composite, key, value);
    }
    
    public void putAll(final Map map) {
        if (this.mutator == null) {
            throw new UnsupportedOperationException("No mutator specified");
        }
        this.mutator.putAll(this, this.composite, map);
    }
    
    public Object remove(final Object key) {
        for (int i = this.composite.length - 1; i >= 0; --i) {
            if (this.composite[i].containsKey(key)) {
                return this.composite[i].remove(key);
            }
        }
        return null;
    }
    
    public int size() {
        int size = 0;
        for (int i = this.composite.length - 1; i >= 0; --i) {
            size += this.composite[i].size();
        }
        return size;
    }
    
    public Collection values() {
        final CompositeCollection keys = new CompositeCollection();
        for (int i = this.composite.length - 1; i >= 0; --i) {
            keys.addComposited(this.composite[i].values());
        }
        return keys;
    }
    
    public boolean equals(final Object obj) {
        if (obj instanceof Map) {
            final Map map = (Map)obj;
            return this.entrySet().equals(map.entrySet());
        }
        return false;
    }
    
    public int hashCode() {
        int code = 0;
        final Iterator i = this.entrySet().iterator();
        while (i.hasNext()) {
            code += i.next().hashCode();
        }
        return code;
    }
    
    public interface MapMutator
    {
        void resolveCollision(final CompositeMap p0, final Map p1, final Map p2, final Collection p3);
        
        Object put(final CompositeMap p0, final Map[] p1, final Object p2, final Object p3);
        
        void putAll(final CompositeMap p0, final Map[] p1, final Map p2);
    }
}
