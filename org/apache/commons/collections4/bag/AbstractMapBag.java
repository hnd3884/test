package org.apache.commons.collections4.bag;

import java.util.ConcurrentModificationException;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import org.apache.commons.collections4.set.UnmodifiableSet;
import java.lang.reflect.Array;
import java.util.Iterator;
import java.util.Collection;
import java.util.Set;
import java.util.Map;
import org.apache.commons.collections4.Bag;

public abstract class AbstractMapBag<E> implements Bag<E>
{
    private transient Map<E, MutableInteger> map;
    private int size;
    private transient int modCount;
    private transient Set<E> uniqueSet;
    
    protected AbstractMapBag() {
    }
    
    protected AbstractMapBag(final Map<E, MutableInteger> map) {
        this.map = map;
    }
    
    protected Map<E, MutableInteger> getMap() {
        return this.map;
    }
    
    @Override
    public int size() {
        return this.size;
    }
    
    @Override
    public boolean isEmpty() {
        return this.map.isEmpty();
    }
    
    @Override
    public int getCount(final Object object) {
        final MutableInteger count = this.map.get(object);
        if (count != null) {
            return count.value;
        }
        return 0;
    }
    
    @Override
    public boolean contains(final Object object) {
        return this.map.containsKey(object);
    }
    
    @Override
    public boolean containsAll(final Collection<?> coll) {
        if (coll instanceof Bag) {
            return this.containsAll((Bag)coll);
        }
        return this.containsAll(new HashBag<Object>(coll));
    }
    
    boolean containsAll(final Bag<?> other) {
        for (final Object current : other.uniqueSet()) {
            if (this.getCount(current) < other.getCount(current)) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public Iterator<E> iterator() {
        return new BagIterator<E>(this);
    }
    
    @Override
    public boolean add(final E object) {
        return this.add(object, 1);
    }
    
    @Override
    public boolean add(final E object, final int nCopies) {
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
    
    @Override
    public boolean addAll(final Collection<? extends E> coll) {
        boolean changed = false;
        final Iterator<? extends E> i = coll.iterator();
        while (i.hasNext()) {
            final boolean added = this.add(i.next());
            changed = (changed || added);
        }
        return changed;
    }
    
    @Override
    public void clear() {
        ++this.modCount;
        this.map.clear();
        this.size = 0;
    }
    
    @Override
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
    
    @Override
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
    
    @Override
    public boolean removeAll(final Collection<?> coll) {
        boolean result = false;
        if (coll != null) {
            final Iterator<?> i = coll.iterator();
            while (i.hasNext()) {
                final boolean changed = this.remove(i.next(), 1);
                result = (result || changed);
            }
        }
        return result;
    }
    
    @Override
    public boolean retainAll(final Collection<?> coll) {
        if (coll instanceof Bag) {
            return this.retainAll((Bag)coll);
        }
        return this.retainAll(new HashBag<Object>(coll));
    }
    
    boolean retainAll(final Bag<?> other) {
        boolean result = false;
        final Bag<E> excess = new HashBag<E>();
        for (final E current : this.uniqueSet()) {
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
    
    @Override
    public Object[] toArray() {
        final Object[] result = new Object[this.size()];
        int i = 0;
        for (final E current : this.map.keySet()) {
            for (int index = this.getCount(current); index > 0; --index) {
                result[i++] = current;
            }
        }
        return result;
    }
    
    @Override
    public <T> T[] toArray(T[] array) {
        final int size = this.size();
        if (array.length < size) {
            final T[] unchecked = array = (T[])Array.newInstance(array.getClass().getComponentType(), size);
        }
        int i = 0;
        for (final E current : this.map.keySet()) {
            for (int index = this.getCount(current); index > 0; --index) {
                final T unchecked2 = (T)current;
                array[i++] = unchecked2;
            }
        }
        while (i < array.length) {
            array[i++] = null;
        }
        return array;
    }
    
    @Override
    public Set<E> uniqueSet() {
        if (this.uniqueSet == null) {
            this.uniqueSet = UnmodifiableSet.unmodifiableSet((Set<? extends E>)this.map.keySet());
        }
        return this.uniqueSet;
    }
    
    protected void doWriteObject(final ObjectOutputStream out) throws IOException {
        out.writeInt(this.map.size());
        for (final Map.Entry<E, MutableInteger> entry : this.map.entrySet()) {
            out.writeObject(entry.getKey());
            out.writeInt(entry.getValue().value);
        }
    }
    
    protected void doReadObject(final Map<E, MutableInteger> map, final ObjectInputStream in) throws IOException, ClassNotFoundException {
        this.map = map;
        for (int entrySize = in.readInt(), i = 0; i < entrySize; ++i) {
            final E obj = (E)in.readObject();
            final int count = in.readInt();
            map.put(obj, new MutableInteger(count));
            this.size += count;
        }
    }
    
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (!(object instanceof Bag)) {
            return false;
        }
        final Bag<?> other = (Bag<?>)object;
        if (other.size() != this.size()) {
            return false;
        }
        for (final E element : this.map.keySet()) {
            if (other.getCount(element) != this.getCount(element)) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        int total = 0;
        for (final Map.Entry<E, MutableInteger> entry : this.map.entrySet()) {
            final E element = entry.getKey();
            final MutableInteger count = entry.getValue();
            total += (((element == null) ? 0 : element.hashCode()) ^ count.value);
        }
        return total;
    }
    
    @Override
    public String toString() {
        if (this.size() == 0) {
            return "[]";
        }
        final StringBuilder buf = new StringBuilder();
        buf.append('[');
        final Iterator<E> it = this.uniqueSet().iterator();
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
    
    static class BagIterator<E> implements Iterator<E>
    {
        private final AbstractMapBag<E> parent;
        private final Iterator<Map.Entry<E, MutableInteger>> entryIterator;
        private Map.Entry<E, MutableInteger> current;
        private int itemCount;
        private final int mods;
        private boolean canRemove;
        
        public BagIterator(final AbstractMapBag<E> parent) {
            this.parent = parent;
            this.entryIterator = ((AbstractMapBag<Object>)parent).map.entrySet().iterator();
            this.current = null;
            this.mods = ((AbstractMapBag<Object>)parent).modCount;
            this.canRemove = false;
        }
        
        @Override
        public boolean hasNext() {
            return this.itemCount > 0 || this.entryIterator.hasNext();
        }
        
        @Override
        public E next() {
            if (((AbstractMapBag<Object>)this.parent).modCount != this.mods) {
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
        
        @Override
        public void remove() {
            if (((AbstractMapBag<Object>)this.parent).modCount != this.mods) {
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
            ((AbstractMapBag<Object>)this.parent).size--;
            this.canRemove = false;
        }
    }
    
    protected static class MutableInteger
    {
        protected int value;
        
        MutableInteger(final int value) {
            this.value = value;
        }
        
        @Override
        public boolean equals(final Object obj) {
            return obj instanceof MutableInteger && ((MutableInteger)obj).value == this.value;
        }
        
        @Override
        public int hashCode() {
            return this.value;
        }
    }
}
