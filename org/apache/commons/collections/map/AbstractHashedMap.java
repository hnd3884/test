package org.apache.commons.collections.map;

import java.util.NoSuchElementException;
import java.util.ConcurrentModificationException;
import org.apache.commons.collections.KeyValue;
import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Collection;
import org.apache.commons.collections.iterators.EmptyIterator;
import java.util.Set;
import org.apache.commons.collections.iterators.EmptyMapIterator;
import org.apache.commons.collections.MapIterator;
import java.util.Iterator;
import java.util.Map;
import org.apache.commons.collections.IterableMap;
import java.util.AbstractMap;

public class AbstractHashedMap extends AbstractMap implements IterableMap
{
    protected static final String NO_NEXT_ENTRY = "No next() entry in the iteration";
    protected static final String NO_PREVIOUS_ENTRY = "No previous() entry in the iteration";
    protected static final String REMOVE_INVALID = "remove() can only be called once after next()";
    protected static final String GETKEY_INVALID = "getKey() can only be called after next() and before remove()";
    protected static final String GETVALUE_INVALID = "getValue() can only be called after next() and before remove()";
    protected static final String SETVALUE_INVALID = "setValue() can only be called after next() and before remove()";
    protected static final int DEFAULT_CAPACITY = 16;
    protected static final int DEFAULT_THRESHOLD = 12;
    protected static final float DEFAULT_LOAD_FACTOR = 0.75f;
    protected static final int MAXIMUM_CAPACITY = 1073741824;
    protected static final Object NULL;
    protected transient float loadFactor;
    protected transient int size;
    protected transient HashEntry[] data;
    protected transient int threshold;
    protected transient int modCount;
    protected transient EntrySet entrySet;
    protected transient KeySet keySet;
    protected transient Values values;
    
    protected AbstractHashedMap() {
    }
    
    protected AbstractHashedMap(final int initialCapacity, final float loadFactor, final int threshold) {
        this.loadFactor = loadFactor;
        this.data = new HashEntry[initialCapacity];
        this.threshold = threshold;
        this.init();
    }
    
    protected AbstractHashedMap(final int initialCapacity) {
        this(initialCapacity, 0.75f);
    }
    
    protected AbstractHashedMap(int initialCapacity, final float loadFactor) {
        if (initialCapacity < 1) {
            throw new IllegalArgumentException("Initial capacity must be greater than 0");
        }
        if (loadFactor <= 0.0f || Float.isNaN(loadFactor)) {
            throw new IllegalArgumentException("Load factor must be greater than 0");
        }
        this.loadFactor = loadFactor;
        initialCapacity = this.calculateNewCapacity(initialCapacity);
        this.threshold = this.calculateThreshold(initialCapacity, loadFactor);
        this.data = new HashEntry[initialCapacity];
        this.init();
    }
    
    protected AbstractHashedMap(final Map map) {
        this(Math.max(2 * map.size(), 16), 0.75f);
        this.putAll(map);
    }
    
    protected void init() {
    }
    
    public Object get(Object key) {
        key = this.convertKey(key);
        final int hashCode = this.hash(key);
        for (HashEntry entry = this.data[this.hashIndex(hashCode, this.data.length)]; entry != null; entry = entry.next) {
            if (entry.hashCode == hashCode && this.isEqualKey(key, entry.key)) {
                return entry.getValue();
            }
        }
        return null;
    }
    
    public int size() {
        return this.size;
    }
    
    public boolean isEmpty() {
        return this.size == 0;
    }
    
    public boolean containsKey(Object key) {
        key = this.convertKey(key);
        final int hashCode = this.hash(key);
        for (HashEntry entry = this.data[this.hashIndex(hashCode, this.data.length)]; entry != null; entry = entry.next) {
            if (entry.hashCode == hashCode && this.isEqualKey(key, entry.key)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean containsValue(final Object value) {
        if (value == null) {
            for (int i = 0, isize = this.data.length; i < isize; ++i) {
                for (HashEntry entry = this.data[i]; entry != null; entry = entry.next) {
                    if (entry.getValue() == null) {
                        return true;
                    }
                }
            }
        }
        else {
            for (int i = 0, isize = this.data.length; i < isize; ++i) {
                for (HashEntry entry = this.data[i]; entry != null; entry = entry.next) {
                    if (this.isEqualValue(value, entry.getValue())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    public Object put(Object key, final Object value) {
        key = this.convertKey(key);
        final int hashCode = this.hash(key);
        final int index = this.hashIndex(hashCode, this.data.length);
        for (HashEntry entry = this.data[index]; entry != null; entry = entry.next) {
            if (entry.hashCode == hashCode && this.isEqualKey(key, entry.key)) {
                final Object oldValue = entry.getValue();
                this.updateEntry(entry, value);
                return oldValue;
            }
        }
        this.addMapping(index, hashCode, key, value);
        return null;
    }
    
    public void putAll(final Map map) {
        final int mapSize = map.size();
        if (mapSize == 0) {
            return;
        }
        final int newSize = (int)((this.size + mapSize) / this.loadFactor + 1.0f);
        this.ensureCapacity(this.calculateNewCapacity(newSize));
        final Iterator it = map.entrySet().iterator();
        while (it.hasNext()) {
            final Map.Entry entry = it.next();
            this.put(entry.getKey(), entry.getValue());
        }
    }
    
    public Object remove(Object key) {
        key = this.convertKey(key);
        final int hashCode = this.hash(key);
        final int index = this.hashIndex(hashCode, this.data.length);
        HashEntry entry = this.data[index];
        HashEntry previous = null;
        while (entry != null) {
            if (entry.hashCode == hashCode && this.isEqualKey(key, entry.key)) {
                final Object oldValue = entry.getValue();
                this.removeMapping(entry, index, previous);
                return oldValue;
            }
            previous = entry;
            entry = entry.next;
        }
        return null;
    }
    
    public void clear() {
        ++this.modCount;
        final HashEntry[] data = this.data;
        for (int i = data.length - 1; i >= 0; --i) {
            data[i] = null;
        }
        this.size = 0;
    }
    
    protected Object convertKey(final Object key) {
        return (key == null) ? AbstractHashedMap.NULL : key;
    }
    
    protected int hash(final Object key) {
        int h = key.hashCode();
        h += ~(h << 9);
        h ^= h >>> 14;
        h += h << 4;
        h ^= h >>> 10;
        return h;
    }
    
    protected boolean isEqualKey(final Object key1, final Object key2) {
        return key1 == key2 || key1.equals(key2);
    }
    
    protected boolean isEqualValue(final Object value1, final Object value2) {
        return value1 == value2 || value1.equals(value2);
    }
    
    protected int hashIndex(final int hashCode, final int dataSize) {
        return hashCode & dataSize - 1;
    }
    
    protected HashEntry getEntry(Object key) {
        key = this.convertKey(key);
        final int hashCode = this.hash(key);
        for (HashEntry entry = this.data[this.hashIndex(hashCode, this.data.length)]; entry != null; entry = entry.next) {
            if (entry.hashCode == hashCode && this.isEqualKey(key, entry.key)) {
                return entry;
            }
        }
        return null;
    }
    
    protected void updateEntry(final HashEntry entry, final Object newValue) {
        entry.setValue(newValue);
    }
    
    protected void reuseEntry(final HashEntry entry, final int hashIndex, final int hashCode, final Object key, final Object value) {
        entry.next = this.data[hashIndex];
        entry.hashCode = hashCode;
        entry.key = key;
        entry.value = value;
    }
    
    protected void addMapping(final int hashIndex, final int hashCode, final Object key, final Object value) {
        ++this.modCount;
        final HashEntry entry = this.createEntry(this.data[hashIndex], hashCode, key, value);
        this.addEntry(entry, hashIndex);
        ++this.size;
        this.checkCapacity();
    }
    
    protected HashEntry createEntry(final HashEntry next, final int hashCode, final Object key, final Object value) {
        return new HashEntry(next, hashCode, key, value);
    }
    
    protected void addEntry(final HashEntry entry, final int hashIndex) {
        this.data[hashIndex] = entry;
    }
    
    protected void removeMapping(final HashEntry entry, final int hashIndex, final HashEntry previous) {
        ++this.modCount;
        this.removeEntry(entry, hashIndex, previous);
        --this.size;
        this.destroyEntry(entry);
    }
    
    protected void removeEntry(final HashEntry entry, final int hashIndex, final HashEntry previous) {
        if (previous == null) {
            this.data[hashIndex] = entry.next;
        }
        else {
            previous.next = entry.next;
        }
    }
    
    protected void destroyEntry(final HashEntry entry) {
        entry.next = null;
        entry.key = null;
        entry.value = null;
    }
    
    protected void checkCapacity() {
        if (this.size >= this.threshold) {
            final int newCapacity = this.data.length * 2;
            if (newCapacity <= 1073741824) {
                this.ensureCapacity(newCapacity);
            }
        }
    }
    
    protected void ensureCapacity(final int newCapacity) {
        final int oldCapacity = this.data.length;
        if (newCapacity <= oldCapacity) {
            return;
        }
        if (this.size == 0) {
            this.threshold = this.calculateThreshold(newCapacity, this.loadFactor);
            this.data = new HashEntry[newCapacity];
        }
        else {
            final HashEntry[] oldEntries = this.data;
            final HashEntry[] newEntries = new HashEntry[newCapacity];
            ++this.modCount;
            for (int i = oldCapacity - 1; i >= 0; --i) {
                HashEntry entry = oldEntries[i];
                if (entry != null) {
                    oldEntries[i] = null;
                    do {
                        final HashEntry next = entry.next;
                        final int index = this.hashIndex(entry.hashCode, newCapacity);
                        entry.next = newEntries[index];
                        newEntries[index] = entry;
                        entry = next;
                    } while (entry != null);
                }
            }
            this.threshold = this.calculateThreshold(newCapacity, this.loadFactor);
            this.data = newEntries;
        }
    }
    
    protected int calculateNewCapacity(final int proposedCapacity) {
        int newCapacity = 1;
        if (proposedCapacity > 1073741824) {
            newCapacity = 1073741824;
        }
        else {
            while (newCapacity < proposedCapacity) {
                newCapacity <<= 1;
            }
            if (newCapacity > 1073741824) {
                newCapacity = 1073741824;
            }
        }
        return newCapacity;
    }
    
    protected int calculateThreshold(final int newCapacity, final float factor) {
        return (int)(newCapacity * factor);
    }
    
    protected HashEntry entryNext(final HashEntry entry) {
        return entry.next;
    }
    
    protected int entryHashCode(final HashEntry entry) {
        return entry.hashCode;
    }
    
    protected Object entryKey(final HashEntry entry) {
        return entry.key;
    }
    
    protected Object entryValue(final HashEntry entry) {
        return entry.value;
    }
    
    public MapIterator mapIterator() {
        if (this.size == 0) {
            return EmptyMapIterator.INSTANCE;
        }
        return new HashMapIterator(this);
    }
    
    public Set entrySet() {
        if (this.entrySet == null) {
            this.entrySet = new EntrySet(this);
        }
        return this.entrySet;
    }
    
    protected Iterator createEntrySetIterator() {
        if (this.size() == 0) {
            return EmptyIterator.INSTANCE;
        }
        return new EntrySetIterator(this);
    }
    
    public Set keySet() {
        if (this.keySet == null) {
            this.keySet = new KeySet(this);
        }
        return this.keySet;
    }
    
    protected Iterator createKeySetIterator() {
        if (this.size() == 0) {
            return EmptyIterator.INSTANCE;
        }
        return new KeySetIterator(this);
    }
    
    public Collection values() {
        if (this.values == null) {
            this.values = new Values(this);
        }
        return this.values;
    }
    
    protected Iterator createValuesIterator() {
        if (this.size() == 0) {
            return EmptyIterator.INSTANCE;
        }
        return new ValuesIterator(this);
    }
    
    protected void doWriteObject(final ObjectOutputStream out) throws IOException {
        out.writeFloat(this.loadFactor);
        out.writeInt(this.data.length);
        out.writeInt(this.size);
        final MapIterator it = this.mapIterator();
        while (it.hasNext()) {
            out.writeObject(it.next());
            out.writeObject(it.getValue());
        }
    }
    
    protected void doReadObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        this.loadFactor = in.readFloat();
        final int capacity = in.readInt();
        final int size = in.readInt();
        this.init();
        this.threshold = this.calculateThreshold(capacity, this.loadFactor);
        this.data = new HashEntry[capacity];
        for (int i = 0; i < size; ++i) {
            final Object key = in.readObject();
            final Object value = in.readObject();
            this.put(key, value);
        }
    }
    
    protected Object clone() {
        try {
            final AbstractHashedMap cloned = (AbstractHashedMap)super.clone();
            cloned.data = new HashEntry[this.data.length];
            cloned.entrySet = null;
            cloned.keySet = null;
            cloned.values = null;
            cloned.modCount = 0;
            cloned.size = 0;
            cloned.init();
            cloned.putAll(this);
            return cloned;
        }
        catch (final CloneNotSupportedException ex) {
            return null;
        }
    }
    
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Map)) {
            return false;
        }
        final Map map = (Map)obj;
        if (map.size() != this.size()) {
            return false;
        }
        final MapIterator it = this.mapIterator();
        try {
            while (it.hasNext()) {
                final Object key = it.next();
                final Object value = it.getValue();
                if (value == null) {
                    if (map.get(key) != null || !map.containsKey(key)) {
                        return false;
                    }
                    continue;
                }
                else {
                    if (!value.equals(map.get(key))) {
                        return false;
                    }
                    continue;
                }
            }
        }
        catch (final ClassCastException ignored) {
            return false;
        }
        catch (final NullPointerException ignored2) {
            return false;
        }
        return true;
    }
    
    public int hashCode() {
        int total = 0;
        final Iterator it = this.createEntrySetIterator();
        while (it.hasNext()) {
            total += it.next().hashCode();
        }
        return total;
    }
    
    public String toString() {
        if (this.size() == 0) {
            return "{}";
        }
        final StringBuffer buf = new StringBuffer(32 * this.size());
        buf.append('{');
        final MapIterator it = this.mapIterator();
        boolean hasNext = it.hasNext();
        while (hasNext) {
            final Object key = it.next();
            final Object value = it.getValue();
            buf.append((key == this) ? "(this Map)" : key).append('=').append((value == this) ? "(this Map)" : value);
            hasNext = it.hasNext();
            if (hasNext) {
                buf.append(',').append(' ');
            }
        }
        buf.append('}');
        return buf.toString();
    }
    
    static {
        NULL = new Object();
    }
    
    protected static class HashMapIterator extends HashIterator implements MapIterator
    {
        protected HashMapIterator(final AbstractHashedMap parent) {
            super(parent);
        }
        
        public Object next() {
            return super.nextEntry().getKey();
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
    
    protected static class EntrySet extends AbstractSet
    {
        protected final AbstractHashedMap parent;
        
        protected EntrySet(final AbstractHashedMap parent) {
            this.parent = parent;
        }
        
        public int size() {
            return this.parent.size();
        }
        
        public void clear() {
            this.parent.clear();
        }
        
        public boolean contains(final Object entry) {
            if (entry instanceof Map.Entry) {
                final Map.Entry e = (Map.Entry)entry;
                final Map.Entry match = this.parent.getEntry(e.getKey());
                return match != null && match.equals(e);
            }
            return false;
        }
        
        public boolean remove(final Object obj) {
            if (!(obj instanceof Map.Entry)) {
                return false;
            }
            if (!this.contains(obj)) {
                return false;
            }
            final Map.Entry entry = (Map.Entry)obj;
            final Object key = entry.getKey();
            this.parent.remove(key);
            return true;
        }
        
        public Iterator iterator() {
            return this.parent.createEntrySetIterator();
        }
    }
    
    protected static class EntrySetIterator extends HashIterator
    {
        protected EntrySetIterator(final AbstractHashedMap parent) {
            super(parent);
        }
        
        public Object next() {
            return super.nextEntry();
        }
    }
    
    protected static class KeySet extends AbstractSet
    {
        protected final AbstractHashedMap parent;
        
        protected KeySet(final AbstractHashedMap parent) {
            this.parent = parent;
        }
        
        public int size() {
            return this.parent.size();
        }
        
        public void clear() {
            this.parent.clear();
        }
        
        public boolean contains(final Object key) {
            return this.parent.containsKey(key);
        }
        
        public boolean remove(final Object key) {
            final boolean result = this.parent.containsKey(key);
            this.parent.remove(key);
            return result;
        }
        
        public Iterator iterator() {
            return this.parent.createKeySetIterator();
        }
    }
    
    protected static class KeySetIterator extends EntrySetIterator
    {
        protected KeySetIterator(final AbstractHashedMap parent) {
            super(parent);
        }
        
        public Object next() {
            return super.nextEntry().getKey();
        }
    }
    
    protected static class Values extends AbstractCollection
    {
        protected final AbstractHashedMap parent;
        
        protected Values(final AbstractHashedMap parent) {
            this.parent = parent;
        }
        
        public int size() {
            return this.parent.size();
        }
        
        public void clear() {
            this.parent.clear();
        }
        
        public boolean contains(final Object value) {
            return this.parent.containsValue(value);
        }
        
        public Iterator iterator() {
            return this.parent.createValuesIterator();
        }
    }
    
    protected static class ValuesIterator extends HashIterator
    {
        protected ValuesIterator(final AbstractHashedMap parent) {
            super(parent);
        }
        
        public Object next() {
            return super.nextEntry().getValue();
        }
    }
    
    protected static class HashEntry implements Map.Entry, KeyValue
    {
        protected HashEntry next;
        protected int hashCode;
        protected Object key;
        protected Object value;
        
        protected HashEntry(final HashEntry next, final int hashCode, final Object key, final Object value) {
            this.next = next;
            this.hashCode = hashCode;
            this.key = key;
            this.value = value;
        }
        
        public Object getKey() {
            return (this.key == AbstractHashedMap.NULL) ? null : this.key;
        }
        
        public Object getValue() {
            return this.value;
        }
        
        public Object setValue(final Object value) {
            final Object old = this.value;
            this.value = value;
            return old;
        }
        
        public boolean equals(final Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof Map.Entry)) {
                return false;
            }
            final Map.Entry other = (Map.Entry)obj;
            if (this.getKey() == null) {
                if (other.getKey() != null) {
                    return false;
                }
            }
            else if (!this.getKey().equals(other.getKey())) {
                return false;
            }
            if ((this.getValue() != null) ? this.getValue().equals(other.getValue()) : (other.getValue() == null)) {
                return true;
            }
            return false;
        }
        
        public int hashCode() {
            return ((this.getKey() == null) ? 0 : this.getKey().hashCode()) ^ ((this.getValue() == null) ? 0 : this.getValue().hashCode());
        }
        
        public String toString() {
            return new StringBuffer().append(this.getKey()).append('=').append(this.getValue()).toString();
        }
    }
    
    protected abstract static class HashIterator implements Iterator
    {
        protected final AbstractHashedMap parent;
        protected int hashIndex;
        protected HashEntry last;
        protected HashEntry next;
        protected int expectedModCount;
        
        protected HashIterator(final AbstractHashedMap parent) {
            this.parent = parent;
            HashEntry[] data;
            int i;
            HashEntry next;
            for (data = parent.data, i = data.length, next = null; i > 0 && next == null; next = data[--i]) {}
            this.next = next;
            this.hashIndex = i;
            this.expectedModCount = parent.modCount;
        }
        
        public boolean hasNext() {
            return this.next != null;
        }
        
        protected HashEntry nextEntry() {
            if (this.parent.modCount != this.expectedModCount) {
                throw new ConcurrentModificationException();
            }
            final HashEntry newCurrent = this.next;
            if (newCurrent == null) {
                throw new NoSuchElementException("No next() entry in the iteration");
            }
            HashEntry[] data;
            int i;
            HashEntry n;
            for (data = this.parent.data, i = this.hashIndex, n = newCurrent.next; n == null && i > 0; n = data[--i]) {}
            this.next = n;
            this.hashIndex = i;
            return this.last = newCurrent;
        }
        
        protected HashEntry currentEntry() {
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
        
        public String toString() {
            if (this.last != null) {
                return "Iterator[" + this.last.getKey() + "=" + this.last.getValue() + "]";
            }
            return "Iterator[]";
        }
    }
}
