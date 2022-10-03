package org.apache.commons.collections4.multiset;

import org.apache.commons.collections4.iterators.AbstractIteratorDecorator;
import java.util.ConcurrentModificationException;
import java.lang.reflect.Array;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import org.apache.commons.collections4.MultiSet;
import java.util.Iterator;
import java.util.Map;

public abstract class AbstractMapMultiSet<E> extends AbstractMultiSet<E>
{
    private transient Map<E, MutableInteger> map;
    private transient int size;
    private transient int modCount;
    
    protected AbstractMapMultiSet() {
    }
    
    protected AbstractMapMultiSet(final Map<E, MutableInteger> map) {
        this.map = map;
    }
    
    protected Map<E, MutableInteger> getMap() {
        return this.map;
    }
    
    protected void setMap(final Map<E, MutableInteger> map) {
        this.map = map;
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
    public Iterator<E> iterator() {
        return new MapBasedMultiSetIterator<E>(this);
    }
    
    @Override
    public int add(final E object, final int occurrences) {
        if (occurrences < 0) {
            throw new IllegalArgumentException("Occurrences must not be negative.");
        }
        final MutableInteger mut = this.map.get(object);
        final int oldCount = (mut != null) ? mut.value : 0;
        if (occurrences > 0) {
            ++this.modCount;
            this.size += occurrences;
            if (mut == null) {
                this.map.put(object, new MutableInteger(occurrences));
            }
            else {
                final MutableInteger mutableInteger = mut;
                mutableInteger.value += occurrences;
            }
        }
        return oldCount;
    }
    
    @Override
    public void clear() {
        ++this.modCount;
        this.map.clear();
        this.size = 0;
    }
    
    @Override
    public int remove(final Object object, final int occurrences) {
        if (occurrences < 0) {
            throw new IllegalArgumentException("Occurrences must not be negative.");
        }
        final MutableInteger mut = this.map.get(object);
        if (mut == null) {
            return 0;
        }
        final int oldCount = mut.value;
        if (occurrences > 0) {
            ++this.modCount;
            if (occurrences < mut.value) {
                final MutableInteger mutableInteger = mut;
                mutableInteger.value -= occurrences;
                this.size -= occurrences;
            }
            else {
                this.map.remove(object);
                this.size -= mut.value;
            }
        }
        return oldCount;
    }
    
    @Override
    protected Iterator<E> createUniqueSetIterator() {
        return (Iterator<E>)new UniqueSetIterator((Iterator<Object>)this.getMap().keySet().iterator(), (AbstractMapMultiSet<Object>)this);
    }
    
    @Override
    protected int uniqueElements() {
        return this.map.size();
    }
    
    @Override
    protected Iterator<MultiSet.Entry<E>> createEntrySetIterator() {
        return new EntrySetIterator<E>(this.map.entrySet().iterator(), this);
    }
    
    @Override
    protected void doWriteObject(final ObjectOutputStream out) throws IOException {
        out.writeInt(this.map.size());
        for (final Map.Entry<E, MutableInteger> entry : this.map.entrySet()) {
            out.writeObject(entry.getKey());
            out.writeInt(entry.getValue().value);
        }
    }
    
    @Override
    protected void doReadObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        for (int entrySize = in.readInt(), i = 0; i < entrySize; ++i) {
            final E obj = (E)in.readObject();
            final int count = in.readInt();
            this.map.put(obj, new MutableInteger(count));
            this.size += count;
        }
    }
    
    @Override
    public Object[] toArray() {
        final Object[] result = new Object[this.size()];
        int i = 0;
        for (final Map.Entry<E, MutableInteger> entry : this.map.entrySet()) {
            final E current = entry.getKey();
            final MutableInteger count = entry.getValue();
            for (int index = count.value; index > 0; --index) {
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
        for (final Map.Entry<E, MutableInteger> entry : this.map.entrySet()) {
            final E current = entry.getKey();
            final MutableInteger count = entry.getValue();
            for (int index = count.value; index > 0; --index) {
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
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (!(object instanceof MultiSet)) {
            return false;
        }
        final MultiSet<?> other = (MultiSet<?>)object;
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
    
    private static class MapBasedMultiSetIterator<E> implements Iterator<E>
    {
        private final AbstractMapMultiSet<E> parent;
        private final Iterator<Map.Entry<E, MutableInteger>> entryIterator;
        private Map.Entry<E, MutableInteger> current;
        private int itemCount;
        private final int mods;
        private boolean canRemove;
        
        public MapBasedMultiSetIterator(final AbstractMapMultiSet<E> parent) {
            this.parent = parent;
            this.entryIterator = ((AbstractMapMultiSet<Object>)parent).map.entrySet().iterator();
            this.current = null;
            this.mods = ((AbstractMapMultiSet<Object>)parent).modCount;
            this.canRemove = false;
        }
        
        @Override
        public boolean hasNext() {
            return this.itemCount > 0 || this.entryIterator.hasNext();
        }
        
        @Override
        public E next() {
            if (((AbstractMapMultiSet<Object>)this.parent).modCount != this.mods) {
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
            if (((AbstractMapMultiSet<Object>)this.parent).modCount != this.mods) {
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
            ((AbstractMapMultiSet<Object>)this.parent).size--;
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
    
    protected static class UniqueSetIterator<E> extends AbstractIteratorDecorator<E>
    {
        protected final AbstractMapMultiSet<E> parent;
        protected E lastElement;
        protected boolean canRemove;
        
        protected UniqueSetIterator(final Iterator<E> iterator, final AbstractMapMultiSet<E> parent) {
            super(iterator);
            this.lastElement = null;
            this.canRemove = false;
            this.parent = parent;
        }
        
        @Override
        public E next() {
            this.lastElement = super.next();
            this.canRemove = true;
            return this.lastElement;
        }
        
        @Override
        public void remove() {
            if (!this.canRemove) {
                throw new IllegalStateException("Iterator remove() can only be called once after next()");
            }
            final int count = this.parent.getCount(this.lastElement);
            super.remove();
            this.parent.remove(this.lastElement, count);
            this.lastElement = null;
            this.canRemove = false;
        }
    }
    
    protected static class EntrySetIterator<E> implements Iterator<MultiSet.Entry<E>>
    {
        protected final AbstractMapMultiSet<E> parent;
        protected final Iterator<Map.Entry<E, MutableInteger>> decorated;
        protected MultiSet.Entry<E> last;
        protected boolean canRemove;
        
        protected EntrySetIterator(final Iterator<Map.Entry<E, MutableInteger>> iterator, final AbstractMapMultiSet<E> parent) {
            this.last = null;
            this.canRemove = false;
            this.decorated = iterator;
            this.parent = parent;
        }
        
        @Override
        public boolean hasNext() {
            return this.decorated.hasNext();
        }
        
        @Override
        public MultiSet.Entry<E> next() {
            this.last = new MultiSetEntry<E>(this.decorated.next());
            this.canRemove = true;
            return this.last;
        }
        
        @Override
        public void remove() {
            if (!this.canRemove) {
                throw new IllegalStateException("Iterator remove() can only be called once after next()");
            }
            this.decorated.remove();
            this.last = null;
            this.canRemove = false;
        }
    }
    
    protected static class MultiSetEntry<E> extends AbstractEntry<E>
    {
        protected final Map.Entry<E, MutableInteger> parentEntry;
        
        protected MultiSetEntry(final Map.Entry<E, MutableInteger> parentEntry) {
            this.parentEntry = parentEntry;
        }
        
        @Override
        public E getElement() {
            return this.parentEntry.getKey();
        }
        
        @Override
        public int getCount() {
            return this.parentEntry.getValue().value;
        }
    }
}
