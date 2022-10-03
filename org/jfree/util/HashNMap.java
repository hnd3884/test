package org.jfree.util;

import java.util.NoSuchElementException;
import java.util.Set;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Iterator;
import java.io.Serializable;

public class HashNMap implements Serializable, Cloneable
{
    private static final long serialVersionUID = -670924844536074826L;
    private static final Iterator EMPTY_ITERATOR;
    private HashMap table;
    private static final Object[] EMPTY_ARRAY;
    
    static {
        EMPTY_ITERATOR = new EmptyIterator(null);
        EMPTY_ARRAY = new Object[0];
    }
    
    public HashNMap() {
        this.table = new HashMap();
    }
    
    public boolean add(final Object key, final Object val) {
        final List v = this.table.get(key);
        if (v == null) {
            this.put(key, val);
            return true;
        }
        return v.add(val);
    }
    
    public void clear() {
        this.table.clear();
    }
    
    public Object clone() throws CloneNotSupportedException {
        final HashNMap map = (HashNMap)super.clone();
        map.table = new HashMap();
        final Iterator iterator = this.keys();
        while (iterator.hasNext()) {
            final Object key = iterator.next();
            final List list = map.table.get(key);
            if (list != null) {
                map.table.put(key, ObjectUtilities.clone(list));
            }
        }
        return map;
    }
    
    public boolean contains(final Object value) {
        return this.containsKey(value) || this.containsValue(value);
    }
    
    public boolean containsKey(final Object key) {
        return this.table.containsKey(key);
    }
    
    public boolean containsValue(final Object value) {
        Iterator e;
        boolean found;
        List v;
        for (e = this.table.values().iterator(), found = false; e.hasNext() && !found; found = v.contains(value)) {
            v = e.next();
        }
        return found;
    }
    
    public boolean containsValue(final Object key, final Object value) {
        final List v = this.table.get(key);
        return v != null && v.contains(value);
    }
    
    protected List createList() {
        return new ArrayList();
    }
    
    public Object get(final Object key, final int n) {
        final List v = this.table.get(key);
        if (v == null) {
            return null;
        }
        return v.get(n);
    }
    
    public Iterator getAll(final Object key) {
        final List v = this.table.get(key);
        if (v == null) {
            return HashNMap.EMPTY_ITERATOR;
        }
        return v.iterator();
    }
    
    public Object getFirst(final Object key) {
        return this.get(key, 0);
    }
    
    public int getValueCount(final Object key) {
        if (key == null) {
            throw new NullPointerException("Key must not be null.");
        }
        final List list = this.table.get(key);
        if (list != null) {
            return list.size();
        }
        return 0;
    }
    
    public Set keySet() {
        return this.table.keySet();
    }
    
    public Iterator keys() {
        return this.table.keySet().iterator();
    }
    
    public boolean put(final Object key, final Object val) {
        final List v = this.table.get(key);
        if (v == null) {
            final List newList = this.createList();
            newList.add(val);
            this.table.put(key, newList);
            return true;
        }
        v.clear();
        return v.add(val);
    }
    
    public boolean remove(final Object key, final Object value) {
        final List v = this.table.get(key);
        if (v == null) {
            return false;
        }
        if (!v.remove(value)) {
            return false;
        }
        if (v.size() == 0) {
            this.table.remove(key);
        }
        return true;
    }
    
    public void removeAll(final Object key) {
        this.table.remove(key);
    }
    
    public Object[] toArray(final Object key) {
        if (key == null) {
            throw new NullPointerException("Key must not be null.");
        }
        final List list = this.table.get(key);
        if (list != null) {
            return list.toArray();
        }
        return HashNMap.EMPTY_ARRAY;
    }
    
    public Object[] toArray(final Object key, final Object[] data) {
        if (key == null) {
            throw new NullPointerException("Key must not be null.");
        }
        final List list = this.table.get(key);
        if (list != null) {
            return list.toArray(data);
        }
        if (data.length > 0) {
            data[0] = null;
        }
        return data;
    }
    
    private static final class EmptyIterator implements Iterator
    {
        public boolean hasNext() {
            return false;
        }
        
        public Object next() {
            throw new NoSuchElementException("This iterator is empty.");
        }
        
        public void remove() {
            throw new UnsupportedOperationException("This iterator is empty, no remove supported.");
        }
    }
    
    static class 1
    {
    }
}
