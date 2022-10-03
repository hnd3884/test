package org.apache.commons.collections4.map;

import java.util.ConcurrentModificationException;
import org.apache.commons.collections4.OrderedIterator;
import org.apache.commons.collections4.ResettableIterator;
import org.apache.commons.collections4.MapIterator;
import org.apache.commons.collections4.iterators.EmptyOrderedIterator;
import java.util.Iterator;
import org.apache.commons.collections4.iterators.EmptyOrderedMapIterator;
import org.apache.commons.collections4.OrderedMapIterator;
import java.util.NoSuchElementException;
import java.util.Map;
import org.apache.commons.collections4.OrderedMap;

public abstract class AbstractLinkedMap<K, V> extends AbstractHashedMap<K, V> implements OrderedMap<K, V>
{
    transient LinkEntry<K, V> header;
    
    protected AbstractLinkedMap() {
    }
    
    protected AbstractLinkedMap(final int initialCapacity, final float loadFactor, final int threshold) {
        super(initialCapacity, loadFactor, threshold);
    }
    
    protected AbstractLinkedMap(final int initialCapacity) {
        super(initialCapacity);
    }
    
    protected AbstractLinkedMap(final int initialCapacity, final float loadFactor) {
        super(initialCapacity, loadFactor);
    }
    
    protected AbstractLinkedMap(final Map<? extends K, ? extends V> map) {
        super(map);
    }
    
    @Override
    protected void init() {
        this.header = this.createEntry(null, -1, null, null);
        final LinkEntry<K, V> header = this.header;
        final LinkEntry<K, V> header2 = this.header;
        final LinkEntry<K, V> header3 = this.header;
        header2.after = header3;
        header.before = header3;
    }
    
    @Override
    public boolean containsValue(final Object value) {
        if (value == null) {
            for (LinkEntry<K, V> entry = this.header.after; entry != this.header; entry = entry.after) {
                if (entry.getValue() == null) {
                    return true;
                }
            }
        }
        else {
            for (LinkEntry<K, V> entry = this.header.after; entry != this.header; entry = entry.after) {
                if (this.isEqualValue(value, entry.getValue())) {
                    return true;
                }
            }
        }
        return false;
    }
    
    @Override
    public void clear() {
        super.clear();
        final LinkEntry<K, V> header = this.header;
        final LinkEntry<K, V> header2 = this.header;
        final LinkEntry<K, V> header3 = this.header;
        header2.after = header3;
        header.before = header3;
    }
    
    @Override
    public K firstKey() {
        if (this.size == 0) {
            throw new NoSuchElementException("Map is empty");
        }
        return this.header.after.getKey();
    }
    
    @Override
    public K lastKey() {
        if (this.size == 0) {
            throw new NoSuchElementException("Map is empty");
        }
        return this.header.before.getKey();
    }
    
    @Override
    public K nextKey(final Object key) {
        final LinkEntry<K, V> entry = this.getEntry(key);
        return (entry == null || entry.after == this.header) ? null : entry.after.getKey();
    }
    
    @Override
    protected LinkEntry<K, V> getEntry(final Object key) {
        return (LinkEntry)super.getEntry(key);
    }
    
    @Override
    public K previousKey(final Object key) {
        final LinkEntry<K, V> entry = this.getEntry(key);
        return (entry == null || entry.before == this.header) ? null : entry.before.getKey();
    }
    
    protected LinkEntry<K, V> getEntry(final int index) {
        if (index < 0) {
            throw new IndexOutOfBoundsException("Index " + index + " is less than zero");
        }
        if (index >= this.size) {
            throw new IndexOutOfBoundsException("Index " + index + " is invalid for size " + this.size);
        }
        LinkEntry<K, V> entry;
        if (index < this.size / 2) {
            entry = this.header.after;
            for (int currentIndex = 0; currentIndex < index; ++currentIndex) {
                entry = entry.after;
            }
        }
        else {
            entry = this.header;
            for (int currentIndex = this.size; currentIndex > index; --currentIndex) {
                entry = entry.before;
            }
        }
        return entry;
    }
    
    @Override
    protected void addEntry(final HashEntry<K, V> entry, final int hashIndex) {
        final LinkEntry<K, V> link = (LinkEntry)entry;
        link.after = this.header;
        link.before = this.header.before;
        this.header.before.after = link;
        this.header.before = link;
        this.data[hashIndex] = link;
    }
    
    @Override
    protected LinkEntry<K, V> createEntry(final HashEntry<K, V> next, final int hashCode, final K key, final V value) {
        return new LinkEntry<K, V>(next, hashCode, this.convertKey(key), value);
    }
    
    @Override
    protected void removeEntry(final HashEntry<K, V> entry, final int hashIndex, final HashEntry<K, V> previous) {
        final LinkEntry<K, V> link = (LinkEntry)entry;
        link.before.after = link.after;
        link.after.before = link.before;
        link.after = null;
        link.before = null;
        super.removeEntry(entry, hashIndex, previous);
    }
    
    protected LinkEntry<K, V> entryBefore(final LinkEntry<K, V> entry) {
        return entry.before;
    }
    
    protected LinkEntry<K, V> entryAfter(final LinkEntry<K, V> entry) {
        return entry.after;
    }
    
    @Override
    public OrderedMapIterator<K, V> mapIterator() {
        if (this.size == 0) {
            return EmptyOrderedMapIterator.emptyOrderedMapIterator();
        }
        return new LinkMapIterator<K, V>(this);
    }
    
    @Override
    protected Iterator<Map.Entry<K, V>> createEntrySetIterator() {
        if (this.size() == 0) {
            return (Iterator<Map.Entry<K, V>>)EmptyOrderedIterator.emptyOrderedIterator();
        }
        return new EntrySetIterator<K, V>(this);
    }
    
    @Override
    protected Iterator<K> createKeySetIterator() {
        if (this.size() == 0) {
            return (Iterator<K>)EmptyOrderedIterator.emptyOrderedIterator();
        }
        return new KeySetIterator<K>(this);
    }
    
    @Override
    protected Iterator<V> createValuesIterator() {
        if (this.size() == 0) {
            return (Iterator<V>)EmptyOrderedIterator.emptyOrderedIterator();
        }
        return new ValuesIterator<V>(this);
    }
    
    protected static class LinkMapIterator<K, V> extends LinkIterator<K, V> implements OrderedMapIterator<K, V>, ResettableIterator<K>
    {
        protected LinkMapIterator(final AbstractLinkedMap<K, V> parent) {
            super(parent);
        }
        
        @Override
        public K next() {
            return super.nextEntry().getKey();
        }
        
        @Override
        public K previous() {
            return super.previousEntry().getKey();
        }
        
        @Override
        public K getKey() {
            final LinkEntry<K, V> current = this.currentEntry();
            if (current == null) {
                throw new IllegalStateException("getKey() can only be called after next() and before remove()");
            }
            return current.getKey();
        }
        
        @Override
        public V getValue() {
            final LinkEntry<K, V> current = this.currentEntry();
            if (current == null) {
                throw new IllegalStateException("getValue() can only be called after next() and before remove()");
            }
            return current.getValue();
        }
        
        @Override
        public V setValue(final V value) {
            final LinkEntry<K, V> current = this.currentEntry();
            if (current == null) {
                throw new IllegalStateException("setValue() can only be called after next() and before remove()");
            }
            return current.setValue(value);
        }
    }
    
    protected static class EntrySetIterator<K, V> extends LinkIterator<K, V> implements OrderedIterator<Map.Entry<K, V>>, ResettableIterator<Map.Entry<K, V>>
    {
        protected EntrySetIterator(final AbstractLinkedMap<K, V> parent) {
            super(parent);
        }
        
        @Override
        public Map.Entry<K, V> next() {
            return super.nextEntry();
        }
        
        @Override
        public Map.Entry<K, V> previous() {
            return super.previousEntry();
        }
    }
    
    protected static class KeySetIterator<K> extends LinkIterator<K, Object> implements OrderedIterator<K>, ResettableIterator<K>
    {
        protected KeySetIterator(final AbstractLinkedMap<K, ?> parent) {
            super(parent);
        }
        
        @Override
        public K next() {
            return super.nextEntry().getKey();
        }
        
        @Override
        public K previous() {
            return super.previousEntry().getKey();
        }
    }
    
    protected static class ValuesIterator<V> extends LinkIterator<Object, V> implements OrderedIterator<V>, ResettableIterator<V>
    {
        protected ValuesIterator(final AbstractLinkedMap<?, V> parent) {
            super(parent);
        }
        
        @Override
        public V next() {
            return super.nextEntry().getValue();
        }
        
        @Override
        public V previous() {
            return super.previousEntry().getValue();
        }
    }
    
    protected static class LinkEntry<K, V> extends HashEntry<K, V>
    {
        protected LinkEntry<K, V> before;
        protected LinkEntry<K, V> after;
        
        protected LinkEntry(final HashEntry<K, V> next, final int hashCode, final Object key, final V value) {
            super(next, hashCode, key, value);
        }
    }
    
    protected abstract static class LinkIterator<K, V>
    {
        protected final AbstractLinkedMap<K, V> parent;
        protected LinkEntry<K, V> last;
        protected LinkEntry<K, V> next;
        protected int expectedModCount;
        
        protected LinkIterator(final AbstractLinkedMap<K, V> parent) {
            this.parent = parent;
            this.next = parent.header.after;
            this.expectedModCount = parent.modCount;
        }
        
        public boolean hasNext() {
            return this.next != this.parent.header;
        }
        
        public boolean hasPrevious() {
            return this.next.before != this.parent.header;
        }
        
        protected LinkEntry<K, V> nextEntry() {
            if (this.parent.modCount != this.expectedModCount) {
                throw new ConcurrentModificationException();
            }
            if (this.next == this.parent.header) {
                throw new NoSuchElementException("No next() entry in the iteration");
            }
            this.last = this.next;
            this.next = this.next.after;
            return this.last;
        }
        
        protected LinkEntry<K, V> previousEntry() {
            if (this.parent.modCount != this.expectedModCount) {
                throw new ConcurrentModificationException();
            }
            final LinkEntry<K, V> previous = this.next.before;
            if (previous == this.parent.header) {
                throw new NoSuchElementException("No previous() entry in the iteration");
            }
            this.next = previous;
            return this.last = previous;
        }
        
        protected LinkEntry<K, V> currentEntry() {
            return this.last;
        }
        
        public void remove() {
            if (this.last == null) {
                throw new IllegalStateException("remove() can only be called once after next()");
            }
            if (this.parent.modCount != this.expectedModCount) {
                throw new ConcurrentModificationException();
            }
            this.parent.remove(this.last.getKey());
            this.last = null;
            this.expectedModCount = this.parent.modCount;
        }
        
        public void reset() {
            this.last = null;
            this.next = this.parent.header.after;
        }
        
        @Override
        public String toString() {
            if (this.last != null) {
                return "Iterator[" + this.last.getKey() + "=" + this.last.getValue() + "]";
            }
            return "Iterator[]";
        }
    }
}
