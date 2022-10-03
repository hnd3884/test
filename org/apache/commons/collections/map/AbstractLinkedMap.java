package org.apache.commons.collections.map;

import java.util.ConcurrentModificationException;
import org.apache.commons.collections.ResettableIterator;
import org.apache.commons.collections.OrderedIterator;
import org.apache.commons.collections.iterators.EmptyOrderedIterator;
import java.util.Iterator;
import org.apache.commons.collections.OrderedMapIterator;
import org.apache.commons.collections.iterators.EmptyOrderedMapIterator;
import org.apache.commons.collections.MapIterator;
import java.util.NoSuchElementException;
import java.util.Map;
import org.apache.commons.collections.OrderedMap;

public class AbstractLinkedMap extends AbstractHashedMap implements OrderedMap
{
    protected transient LinkEntry header;
    
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
    
    protected AbstractLinkedMap(final Map map) {
        super(map);
    }
    
    protected void init() {
        this.header = (LinkEntry)this.createEntry(null, -1, null, null);
        final LinkEntry header = this.header;
        final LinkEntry header2 = this.header;
        final LinkEntry header3 = this.header;
        header2.after = header3;
        header.before = header3;
    }
    
    public boolean containsValue(final Object value) {
        if (value == null) {
            for (LinkEntry entry = this.header.after; entry != this.header; entry = entry.after) {
                if (entry.getValue() == null) {
                    return true;
                }
            }
        }
        else {
            for (LinkEntry entry = this.header.after; entry != this.header; entry = entry.after) {
                if (this.isEqualValue(value, entry.getValue())) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public void clear() {
        super.clear();
        final LinkEntry header = this.header;
        final LinkEntry header2 = this.header;
        final LinkEntry header3 = this.header;
        header2.after = header3;
        header.before = header3;
    }
    
    public Object firstKey() {
        if (this.size == 0) {
            throw new NoSuchElementException("Map is empty");
        }
        return this.header.after.getKey();
    }
    
    public Object lastKey() {
        if (this.size == 0) {
            throw new NoSuchElementException("Map is empty");
        }
        return this.header.before.getKey();
    }
    
    public Object nextKey(final Object key) {
        final LinkEntry entry = (LinkEntry)this.getEntry(key);
        return (entry == null || entry.after == this.header) ? null : entry.after.getKey();
    }
    
    public Object previousKey(final Object key) {
        final LinkEntry entry = (LinkEntry)this.getEntry(key);
        return (entry == null || entry.before == this.header) ? null : entry.before.getKey();
    }
    
    protected LinkEntry getEntry(final int index) {
        if (index < 0) {
            throw new IndexOutOfBoundsException("Index " + index + " is less than zero");
        }
        if (index >= this.size) {
            throw new IndexOutOfBoundsException("Index " + index + " is invalid for size " + this.size);
        }
        LinkEntry entry;
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
    
    protected void addEntry(final HashEntry entry, final int hashIndex) {
        final LinkEntry link = (LinkEntry)entry;
        link.after = this.header;
        link.before = this.header.before;
        this.header.before.after = link;
        this.header.before = link;
        this.data[hashIndex] = entry;
    }
    
    protected HashEntry createEntry(final HashEntry next, final int hashCode, final Object key, final Object value) {
        return new LinkEntry(next, hashCode, key, value);
    }
    
    protected void removeEntry(final HashEntry entry, final int hashIndex, final HashEntry previous) {
        final LinkEntry link = (LinkEntry)entry;
        link.before.after = link.after;
        link.after.before = link.before;
        link.after = null;
        link.before = null;
        super.removeEntry(entry, hashIndex, previous);
    }
    
    protected LinkEntry entryBefore(final LinkEntry entry) {
        return entry.before;
    }
    
    protected LinkEntry entryAfter(final LinkEntry entry) {
        return entry.after;
    }
    
    public MapIterator mapIterator() {
        if (this.size == 0) {
            return EmptyOrderedMapIterator.INSTANCE;
        }
        return new LinkMapIterator(this);
    }
    
    public OrderedMapIterator orderedMapIterator() {
        if (this.size == 0) {
            return EmptyOrderedMapIterator.INSTANCE;
        }
        return new LinkMapIterator(this);
    }
    
    protected Iterator createEntrySetIterator() {
        if (this.size() == 0) {
            return EmptyOrderedIterator.INSTANCE;
        }
        return new EntrySetIterator(this);
    }
    
    protected Iterator createKeySetIterator() {
        if (this.size() == 0) {
            return EmptyOrderedIterator.INSTANCE;
        }
        return new KeySetIterator(this);
    }
    
    protected Iterator createValuesIterator() {
        if (this.size() == 0) {
            return EmptyOrderedIterator.INSTANCE;
        }
        return new ValuesIterator(this);
    }
    
    protected static class LinkMapIterator extends LinkIterator implements OrderedMapIterator
    {
        protected LinkMapIterator(final AbstractLinkedMap parent) {
            super(parent);
        }
        
        public Object next() {
            return super.nextEntry().getKey();
        }
        
        public Object previous() {
            return super.previousEntry().getKey();
        }
        
        public Object getKey() {
            final HashEntry current = this.currentEntry();
            if (current == null) {
                throw new IllegalStateException("getKey() can only be called after next() and before remove()");
            }
            return current.getKey();
        }
        
        public Object getValue() {
            final HashEntry current = this.currentEntry();
            if (current == null) {
                throw new IllegalStateException("getValue() can only be called after next() and before remove()");
            }
            return current.getValue();
        }
        
        public Object setValue(final Object value) {
            final HashEntry current = this.currentEntry();
            if (current == null) {
                throw new IllegalStateException("setValue() can only be called after next() and before remove()");
            }
            return current.setValue(value);
        }
    }
    
    protected static class EntrySetIterator extends LinkIterator
    {
        protected EntrySetIterator(final AbstractLinkedMap parent) {
            super(parent);
        }
        
        public Object next() {
            return super.nextEntry();
        }
        
        public Object previous() {
            return super.previousEntry();
        }
    }
    
    protected static class KeySetIterator extends EntrySetIterator
    {
        protected KeySetIterator(final AbstractLinkedMap parent) {
            super(parent);
        }
        
        public Object next() {
            return super.nextEntry().getKey();
        }
        
        public Object previous() {
            return super.previousEntry().getKey();
        }
    }
    
    protected static class ValuesIterator extends LinkIterator
    {
        protected ValuesIterator(final AbstractLinkedMap parent) {
            super(parent);
        }
        
        public Object next() {
            return super.nextEntry().getValue();
        }
        
        public Object previous() {
            return super.previousEntry().getValue();
        }
    }
    
    protected static class LinkEntry extends HashEntry
    {
        protected LinkEntry before;
        protected LinkEntry after;
        
        protected LinkEntry(final HashEntry next, final int hashCode, final Object key, final Object value) {
            super(next, hashCode, key, value);
        }
    }
    
    protected abstract static class LinkIterator implements OrderedIterator, ResettableIterator
    {
        protected final AbstractLinkedMap parent;
        protected LinkEntry last;
        protected LinkEntry next;
        protected int expectedModCount;
        
        protected LinkIterator(final AbstractLinkedMap parent) {
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
        
        protected LinkEntry nextEntry() {
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
        
        protected LinkEntry previousEntry() {
            if (this.parent.modCount != this.expectedModCount) {
                throw new ConcurrentModificationException();
            }
            final LinkEntry previous = this.next.before;
            if (previous == this.parent.header) {
                throw new NoSuchElementException("No previous() entry in the iteration");
            }
            this.next = previous;
            return this.last = previous;
        }
        
        protected LinkEntry currentEntry() {
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
        
        public String toString() {
            if (this.last != null) {
                return "Iterator[" + this.last.getKey() + "=" + this.last.getValue() + "]";
            }
            return "Iterator[]";
        }
    }
}
