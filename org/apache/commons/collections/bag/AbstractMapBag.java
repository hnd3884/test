package org.apache.commons.collections.bag;

import java.util.ConcurrentModificationException;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import org.apache.commons.collections.set.UnmodifiableSet;
import java.lang.reflect.Array;
import java.util.Iterator;
import java.util.Collection;
import java.util.Set;
import java.util.Map;
import org.apache.commons.collections.Bag;

public abstract class AbstractMapBag implements Bag
{
    private transient Map map;
    private int size;
    private transient int modCount;
    private transient Set uniqueSet;
    
    protected AbstractMapBag() {
    }
    
    protected AbstractMapBag(final Map map) {
        this.map = map;
    }
    
    protected Map getMap() {
        return this.map;
    }
    
    public int size() {
        return this.size;
    }
    
    public boolean isEmpty() {
        return this.map.isEmpty();
    }
    
    public int getCount(final Object object) {
        final MutableInteger count = this.map.get(object);
        if (count != null) {
            return count.value;
        }
        return 0;
    }
    
    public boolean contains(final Object object) {
        return this.map.containsKey(object);
    }
    
    public boolean containsAll(final Collection coll) {
        if (coll instanceof Bag) {
            return this.containsAll((Bag)coll);
        }
        return this.containsAll(new HashBag(coll));
    }
    
    boolean containsAll(final Bag other) {
        boolean result = true;
        final Iterator it = other.uniqueSet().iterator();
        while (it.hasNext()) {
            final Object current = it.next();
            final boolean contains = this.getCount(current) >= other.getCount(current);
            result = (result && contains);
        }
        return result;
    }
    
    public Iterator iterator() {
        return new BagIterator(this);
    }
    
    public boolean add(final Object object) {
        return this.add(object, 1);
    }
    
    public boolean add(final Object object, final int nCopies) {
        ++this.modCount;
        if (nCopies <= 0) {
            return false;
        }
        final MutableInteger mut = this.map.get(object);
        this.size += nCopies;
        if (mut == null) {
            this.map.put(object, new MutableInteger(nCopies));
            return true;
        }
        final MutableInteger mutableInteger = mut;
        mutableInteger.value += nCopies;
        return false;
    }
    
    public boolean addAll(final Collection coll) {
        boolean changed = false;
        final Iterator i = coll.iterator();
        while (i.hasNext()) {
            final boolean added = this.add(i.next());
            changed = (changed || added);
        }
        return changed;
    }
    
    public void clear() {
        ++this.modCount;
        this.map.clear();
        this.size = 0;
    }
    
    public boolean remove(final Object object) {
        final MutableInteger mut = this.map.get(object);
        if (mut == null) {
            return false;
        }
        ++this.modCount;
        this.map.remove(object);
        this.size -= mut.value;
        return true;
    }
    
    public boolean remove(final Object object, final int nCopies) {
        final MutableInteger mut = this.map.get(object);
        if (mut == null) {
            return false;
        }
        if (nCopies <= 0) {
            return false;
        }
        ++this.modCount;
        if (nCopies < mut.value) {
            final MutableInteger mutableInteger = mut;
            mutableInteger.value -= nCopies;
            this.size -= nCopies;
        }
        else {
            this.map.remove(object);
            this.size -= mut.value;
        }
        return true;
    }
    
    public boolean removeAll(final Collection coll) {
        boolean result = false;
        if (coll != null) {
            final Iterator i = coll.iterator();
            while (i.hasNext()) {
                final boolean changed = this.remove(i.next(), 1);
                result = (result || changed);
            }
        }
        return result;
    }
    
    public boolean retainAll(final Collection coll) {
        if (coll instanceof Bag) {
            return this.retainAll((Bag)coll);
        }
        return this.retainAll(new HashBag(coll));
    }
    
    boolean retainAll(final Bag other) {
        boolean result = false;
        final Bag excess = new HashBag();
        final Iterator i = this.uniqueSet().iterator();
        while (i.hasNext()) {
            final Object current = i.next();
            final int myCount = this.getCount(current);
            final int otherCount = other.getCount(current);
            if (1 <= otherCount && otherCount <= myCount) {
                excess.add(current, myCount - otherCount);
            }
            else {
                excess.add(current, myCount);
            }
        }
        if (!excess.isEmpty()) {
            result = this.removeAll(excess);
        }
        return result;
    }
    
    public Object[] toArray() {
        final Object[] result = new Object[this.size()];
        int i = 0;
        final Iterator it = this.map.keySet().iterator();
        while (it.hasNext()) {
            final Object current = it.next();
            for (int index = this.getCount(current); index > 0; --index) {
                result[i++] = current;
            }
        }
        return result;
    }
    
    public Object[] toArray(Object[] array) {
        final int size = this.size();
        if (array.length < size) {
            array = (Object[])Array.newInstance(array.getClass().getComponentType(), size);
        }
        int i = 0;
        final Iterator it = this.map.keySet().iterator();
        while (it.hasNext()) {
            final Object current = it.next();
            for (int index = this.getCount(current); index > 0; --index) {
                array[i++] = current;
            }
        }
        if (array.length > size) {
            array[size] = null;
        }
        return array;
    }
    
    public Set uniqueSet() {
        if (this.uniqueSet == null) {
            this.uniqueSet = UnmodifiableSet.decorate(this.map.keySet());
        }
        return this.uniqueSet;
    }
    
    protected void doWriteObject(final ObjectOutputStream out) throws IOException {
        out.writeInt(this.map.size());
        final Iterator it = this.map.entrySet().iterator();
        while (it.hasNext()) {
            final Map.Entry entry = it.next();
            out.writeObject(entry.getKey());
            out.writeInt(entry.getValue().value);
        }
    }
    
    protected void doReadObject(final Map map, final ObjectInputStream in) throws IOException, ClassNotFoundException {
        this.map = map;
        for (int entrySize = in.readInt(), i = 0; i < entrySize; ++i) {
            final Object obj = in.readObject();
            final int count = in.readInt();
            map.put(obj, new MutableInteger(count));
            this.size += count;
        }
    }
    
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (!(object instanceof Bag)) {
            return false;
        }
        final Bag other = (Bag)object;
        if (other.size() != this.size()) {
            return false;
        }
        final Iterator it = this.map.keySet().iterator();
        while (it.hasNext()) {
            final Object element = it.next();
            if (other.getCount(element) != this.getCount(element)) {
                return false;
            }
        }
        return true;
    }
    
    public int hashCode() {
        int total = 0;
        final Iterator it = this.map.entrySet().iterator();
        while (it.hasNext()) {
            final Map.Entry entry = it.next();
            final Object element = entry.getKey();
            final MutableInteger count = entry.getValue();
            total += (((element == null) ? 0 : element.hashCode()) ^ count.value);
        }
        return total;
    }
    
    public String toString() {
        if (this.size() == 0) {
            return "[]";
        }
        final StringBuffer buf = new StringBuffer();
        buf.append('[');
        final Iterator it = this.uniqueSet().iterator();
        while (it.hasNext()) {
            final Object current = it.next();
            final int count = this.getCount(current);
            buf.append(count);
            buf.append(':');
            buf.append(current);
            if (it.hasNext()) {
                buf.append(',');
            }
        }
        buf.append(']');
        return buf.toString();
    }
    
    static class BagIterator implements Iterator
    {
        private AbstractMapBag parent;
        private Iterator entryIterator;
        private Map.Entry current;
        private int itemCount;
        private final int mods;
        private boolean canRemove;
        
        public BagIterator(final AbstractMapBag parent) {
            this.parent = parent;
            this.entryIterator = parent.map.entrySet().iterator();
            this.current = null;
            this.mods = parent.modCount;
            this.canRemove = false;
        }
        
        public boolean hasNext() {
            return this.itemCount > 0 || this.entryIterator.hasNext();
        }
        
        public Object next() {
            if (this.parent.modCount != this.mods) {
                throw new ConcurrentModificationException();
            }
            if (this.itemCount == 0) {
                this.current = this.entryIterator.next();
                this.itemCount = this.current.getValue().value;
            }
            this.canRemove = true;
            --this.itemCount;
            return this.current.getKey();
        }
        
        public void remove() {
            if (this.parent.modCount != this.mods) {
                throw new ConcurrentModificationException();
            }
            if (!this.canRemove) {
                throw new IllegalStateException();
            }
            final MutableInteger mut = this.current.getValue();
            if (mut.value > 1) {
                final MutableInteger mutableInteger = mut;
                --mutableInteger.value;
            }
            else {
                this.entryIterator.remove();
            }
            this.parent.size--;
            this.canRemove = false;
        }
    }
    
    protected static class MutableInteger
    {
        protected int value;
        
        MutableInteger(final int value) {
            this.value = value;
        }
        
        public boolean equals(final Object obj) {
            return obj instanceof MutableInteger && ((MutableInteger)obj).value == this.value;
        }
        
        public int hashCode() {
            return this.value;
        }
    }
}
