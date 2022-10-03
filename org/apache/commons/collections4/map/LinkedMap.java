package org.apache.commons.collections4.map;

import org.apache.commons.collections4.list.UnmodifiableList;
import org.apache.commons.collections4.iterators.UnmodifiableListIterator;
import java.util.ListIterator;
import org.apache.commons.collections4.iterators.UnmodifiableIterator;
import java.util.Iterator;
import java.util.Collection;
import java.util.AbstractList;
import java.util.List;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Map;
import java.io.Serializable;

public class LinkedMap<K, V> extends AbstractLinkedMap<K, V> implements Serializable, Cloneable
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
    
    public LinkedMap(final Map<? extends K, ? extends V> map) {
        super(map);
    }
    
    public LinkedMap<K, V> clone() {
        return (LinkedMap)super.clone();
    }
    
    private void writeObject(final ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        this.doWriteObject(out);
    }
    
    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.doReadObject(in);
    }
    
    public K get(final int index) {
        return this.getEntry(index).getKey();
    }
    
    public V getValue(final int index) {
        return this.getEntry(index).getValue();
    }
    
    public int indexOf(Object key) {
        key = this.convertKey(key);
        int i = 0;
        for (LinkEntry<K, V> entry = this.header.after; entry != this.header; entry = entry.after, ++i) {
            if (this.isEqualKey(key, entry.key)) {
                return i;
            }
        }
        return -1;
    }
    
    public V remove(final int index) {
        return this.remove(this.get(index));
    }
    
    public List<K> asList() {
        return new LinkedMapList<K>(this);
    }
    
    static class LinkedMapList<K> extends AbstractList<K>
    {
        private final LinkedMap<K, ?> parent;
        
        LinkedMapList(final LinkedMap<K, ?> parent) {
            this.parent = parent;
        }
        
        @Override
        public int size() {
            return this.parent.size();
        }
        
        @Override
        public K get(final int index) {
            return this.parent.get(index);
        }
        
        @Override
        public boolean contains(final Object obj) {
            return this.parent.containsKey(obj);
        }
        
        @Override
        public int indexOf(final Object obj) {
            return this.parent.indexOf(obj);
        }
        
        @Override
        public int lastIndexOf(final Object obj) {
            return this.parent.indexOf(obj);
        }
        
        @Override
        public boolean containsAll(final Collection<?> coll) {
            return this.parent.keySet().containsAll(coll);
        }
        
        @Override
        public K remove(final int index) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public boolean remove(final Object obj) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public boolean removeAll(final Collection<?> coll) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public boolean retainAll(final Collection<?> coll) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public void clear() {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public Object[] toArray() {
            return this.parent.keySet().toArray();
        }
        
        @Override
        public <T> T[] toArray(final T[] array) {
            return this.parent.keySet().toArray(array);
        }
        
        @Override
        public Iterator<K> iterator() {
            return UnmodifiableIterator.unmodifiableIterator((Iterator<? extends K>)this.parent.keySet().iterator());
        }
        
        @Override
        public ListIterator<K> listIterator() {
            return UnmodifiableListIterator.umodifiableListIterator(super.listIterator());
        }
        
        @Override
        public ListIterator<K> listIterator(final int fromIndex) {
            return UnmodifiableListIterator.umodifiableListIterator(super.listIterator(fromIndex));
        }
        
        @Override
        public List<K> subList(final int fromIndexInclusive, final int toIndexExclusive) {
            return UnmodifiableList.unmodifiableList(super.subList(fromIndexInclusive, toIndexExclusive));
        }
    }
}
