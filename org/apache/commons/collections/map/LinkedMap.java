package org.apache.commons.collections.map;

import org.apache.commons.collections.list.UnmodifiableList;
import org.apache.commons.collections.iterators.UnmodifiableListIterator;
import java.util.ListIterator;
import org.apache.commons.collections.iterators.UnmodifiableIterator;
import java.util.Iterator;
import java.util.Collection;
import java.util.AbstractList;
import java.util.List;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Map;
import java.io.Serializable;

public class LinkedMap extends AbstractLinkedMap implements Serializable, Cloneable
{
    private static final long serialVersionUID = 9077234323521161066L;
    
    public LinkedMap() {
        super(16, 0.75f, 12);
    }
    
    public LinkedMap(final int initialCapacity) {
        super(initialCapacity);
    }
    
    public LinkedMap(final int initialCapacity, final float loadFactor) {
        super(initialCapacity, loadFactor);
    }
    
    public LinkedMap(final Map map) {
        super(map);
    }
    
    public Object clone() {
        return super.clone();
    }
    
    private void writeObject(final ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        this.doWriteObject(out);
    }
    
    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.doReadObject(in);
    }
    
    public Object get(final int index) {
        return this.getEntry(index).getKey();
    }
    
    public Object getValue(final int index) {
        return this.getEntry(index).getValue();
    }
    
    public int indexOf(Object key) {
        key = this.convertKey(key);
        int i = 0;
        for (LinkEntry entry = this.header.after; entry != this.header; entry = entry.after, ++i) {
            if (this.isEqualKey(key, entry.key)) {
                return i;
            }
        }
        return -1;
    }
    
    public Object remove(final int index) {
        return this.remove(this.get(index));
    }
    
    public List asList() {
        return new LinkedMapList(this);
    }
    
    static class LinkedMapList extends AbstractList
    {
        final LinkedMap parent;
        
        LinkedMapList(final LinkedMap parent) {
            this.parent = parent;
        }
        
        public int size() {
            return this.parent.size();
        }
        
        public Object get(final int index) {
            return this.parent.get(index);
        }
        
        public boolean contains(final Object obj) {
            return this.parent.containsKey(obj);
        }
        
        public int indexOf(final Object obj) {
            return this.parent.indexOf(obj);
        }
        
        public int lastIndexOf(final Object obj) {
            return this.parent.indexOf(obj);
        }
        
        public boolean containsAll(final Collection coll) {
            return this.parent.keySet().containsAll(coll);
        }
        
        public Object remove(final int index) {
            throw new UnsupportedOperationException();
        }
        
        public boolean remove(final Object obj) {
            throw new UnsupportedOperationException();
        }
        
        public boolean removeAll(final Collection coll) {
            throw new UnsupportedOperationException();
        }
        
        public boolean retainAll(final Collection coll) {
            throw new UnsupportedOperationException();
        }
        
        public void clear() {
            throw new UnsupportedOperationException();
        }
        
        public Object[] toArray() {
            return this.parent.keySet().toArray();
        }
        
        public Object[] toArray(final Object[] array) {
            return this.parent.keySet().toArray(array);
        }
        
        public Iterator iterator() {
            return UnmodifiableIterator.decorate(this.parent.keySet().iterator());
        }
        
        public ListIterator listIterator() {
            return UnmodifiableListIterator.decorate(super.listIterator());
        }
        
        public ListIterator listIterator(final int fromIndex) {
            return UnmodifiableListIterator.decorate(super.listIterator(fromIndex));
        }
        
        public List subList(final int fromIndexInclusive, final int toIndexExclusive) {
            return UnmodifiableList.decorate(super.subList(fromIndexInclusive, toIndexExclusive));
        }
    }
}
