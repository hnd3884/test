package org.apache.commons.collections4.map;

import java.util.NoSuchElementException;
import java.util.ConcurrentModificationException;
import org.apache.commons.collections4.KeyValue;
import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Collection;
import org.apache.commons.collections4.iterators.EmptyIterator;
import java.util.Set;
import org.apache.commons.collections4.iterators.EmptyMapIterator;
import org.apache.commons.collections4.MapIterator;
import java.util.Iterator;
import java.util.Map;
import org.apache.commons.collections4.IterableMap;
import java.util.AbstractMap;

public class AbstractHashedMap<K, V> extends AbstractMap<K, V> implements IterableMap<K, V>
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
    transient float loadFactor;
    transient int size;
    transient HashEntry<K, V>[] data;
    transient int threshold;
    transient int modCount;
    transient EntrySet<K, V> entrySet;
    transient KeySet<K> keySet;
    transient Values<V> values;
    
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
        if (initialCapacity < 0) {
            throw new IllegalArgumentException("Initial capacity must be a non negative number");
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
    
    protected AbstractHashedMap(final Map<? extends K, ? extends V> map) {
        this(Math.max(2 * map.size(), 16), 0.75f);
        this._putAll(map);
    }
    
    protected void init() {
    }
    
    @Override
    public V get(Object key) {
        key = this.convertKey(key);
        final int hashCode = this.hash(key);
        for (HashEntry<K, V> entry = this.data[this.hashIndex(hashCode, this.data.length)]; entry != null; entry = entry.next) {
            if (entry.hashCode == hashCode && this.isEqualKey(key, entry.key)) {
                return entry.getValue();
            }
        }
        return null;
    }
    
    @Override
    public int size() {
        return this.size;
    }
    
    @Override
    public boolean isEmpty() {
        return this.size == 0;
    }
    
    @Override
    public boolean containsKey(Object key) {
        key = this.convertKey(key);
        final int hashCode = this.hash(key);
        for (HashEntry<K, V> entry = this.data[this.hashIndex(hashCode, this.data.length)]; entry != null; entry = entry.next) {
            if (entry.hashCode == hashCode && this.isEqualKey(key, entry.key)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public boolean containsValue(final Object value) {
        if (value == null) {
            for (HashEntry<K, V> entry : this.data) {
                final HashEntry<K, V> element = entry;
                while (entry != null) {
                    if (entry.getValue() == null) {
                        return true;
                    }
                    entry = entry.next;
                }
            }
        }
        else {
            for (HashEntry<K, V> entry : this.data) {
                final HashEntry<K, V> element = entry;
                while (entry != null) {
                    if (this.isEqualValue(value, entry.getValue())) {
                        return true;
                    }
                    entry = entry.next;
                }
            }
        }
        return false;
    }
    
    @Override
    public V put(final K key, final V value) {
        final Object convertedKey = this.convertKey(key);
        final int hashCode = this.hash(convertedKey);
        final int index = this.hashIndex(hashCode, this.data.length);
        for (HashEntry<K, V> entry = this.data[index]; entry != null; entry = entry.next) {
            if (entry.hashCode == hashCode && this.isEqualKey(convertedKey, entry.key)) {
                final V oldValue = entry.getValue();
                this.updateEntry(entry, value);
                return oldValue;
            }
        }
        this.addMapping(index, hashCode, key, value);
        return null;
    }
    
    @Override
    public void putAll(final Map<? extends K, ? extends V> map) {
        this._putAll(map);
    }
    
    private void _putAll(final Map<? extends K, ? extends V> map) {
        final int mapSize = map.size();
        if (mapSize == 0) {
            return;
        }
        final int newSize = (int)((this.size + mapSize) / this.loadFactor + 1.0f);
        this.ensureCapacity(this.calculateNewCapacity(newSize));
        for (final Map.Entry<? extends K, ? extends V> entry : map.entrySet()) {
            this.put(entry.getKey(), entry.getValue());
        }
    }
    
    @Override
    public V remove(Object key) {
        key = this.convertKey(key);
        final int hashCode = this.hash(key);
        final int index = this.hashIndex(hashCode, this.data.length);
        HashEntry<K, V> entry = this.data[index];
        HashEntry<K, V> previous = null;
        while (entry != null) {
            if (entry.hashCode == hashCode && this.isEqualKey(key, entry.key)) {
                final V oldValue = entry.getValue();
                this.removeMapping(entry, index, previous);
                return oldValue;
            }
            previous = entry;
            entry = entry.next;
        }
        return null;
    }
    
    @Override
    public void clear() {
        ++this.modCount;
        final HashEntry<K, V>[] data = this.data;
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
    
    protected HashEntry<K, V> getEntry(Object key) {
        key = this.convertKey(key);
        final int hashCode = this.hash(key);
        for (HashEntry<K, V> entry = this.data[this.hashIndex(hashCode, this.data.length)]; entry != null; entry = entry.next) {
            if (entry.hashCode == hashCode && this.isEqualKey(key, entry.key)) {
                return entry;
            }
        }
        return null;
    }
    
    protected void updateEntry(final HashEntry<K, V> entry, final V newValue) {
        entry.setValue(newValue);
    }
    
    protected void reuseEntry(final HashEntry<K, V> entry, final int hashIndex, final int hashCode, final K key, final V value) {
        entry.next = this.data[hashIndex];
        entry.hashCode = hashCode;
        entry.key = key;
        entry.value = value;
    }
    
    protected void addMapping(final int hashIndex, final int hashCode, final K key, final V value) {
        ++this.modCount;
        final HashEntry<K, V> entry = this.createEntry(this.data[hashIndex], hashCode, key, value);
        this.addEntry(entry, hashIndex);
        ++this.size;
        this.checkCapacity();
    }
    
    protected HashEntry<K, V> createEntry(final HashEntry<K, V> next, final int hashCode, final K key, final V value) {
        return new HashEntry<K, V>(next, hashCode, this.convertKey(key), value);
    }
    
    protected void addEntry(final HashEntry<K, V> entry, final int hashIndex) {
        this.data[hashIndex] = entry;
    }
    
    protected void removeMapping(final HashEntry<K, V> entry, final int hashIndex, final HashEntry<K, V> previous) {
        ++this.modCount;
        this.removeEntry(entry, hashIndex, previous);
        --this.size;
        this.destroyEntry(entry);
    }
    
    protected void removeEntry(final HashEntry<K, V> entry, final int hashIndex, final HashEntry<K, V> previous) {
        if (previous == null) {
            this.data[hashIndex] = entry.next;
        }
        else {
            previous.next = entry.next;
        }
    }
    
    protected void destroyEntry(final HashEntry<K, V> entry) {
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
            final HashEntry<K, V>[] oldEntries = this.data;
            final HashEntry<K, V>[] newEntries = new HashEntry[newCapacity];
            ++this.modCount;
            for (int i = oldCapacity - 1; i >= 0; --i) {
                HashEntry<K, V> entry = oldEntries[i];
                if (entry != null) {
                    oldEntries[i] = null;
                    do {
                        final HashEntry<K, V> next = entry.next;
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
    
    protected HashEntry<K, V> entryNext(final HashEntry<K, V> entry) {
        return entry.next;
    }
    
    protected int entryHashCode(final HashEntry<K, V> entry) {
        return entry.hashCode;
    }
    
    protected K entryKey(final HashEntry<K, V> entry) {
        return entry.getKey();
    }
    
    protected V entryValue(final HashEntry<K, V> entry) {
        return entry.getValue();
    }
    
    @Override
    public MapIterator<K, V> mapIterator() {
        if (this.size == 0) {
            return EmptyMapIterator.emptyMapIterator();
        }
        return new HashMapIterator<K, V>(this);
    }
    
    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        if (this.entrySet == null) {
            this.entrySet = new EntrySet<K, V>(this);
        }
        return (Set<Map.Entry<K, V>>)this.entrySet;
    }
    
    protected Iterator<Map.Entry<K, V>> createEntrySetIterator() {
        if (this.size() == 0) {
            return EmptyIterator.emptyIterator();
        }
        return new EntrySetIterator<K, V>(this);
    }
    
    @Override
    public Set<K> keySet() {
        if (this.keySet == null) {
            this.keySet = new KeySet<K>(this);
        }
        return this.keySet;
    }
    
    protected Iterator<K> createKeySetIterator() {
        if (this.size() == 0) {
            return EmptyIterator.emptyIterator();
        }
        return new KeySetIterator<K>(this);
    }
    
    @Override
    public Collection<V> values() {
        if (this.values == null) {
            this.values = new Values<V>(this);
        }
        return this.values;
    }
    
    protected Iterator<V> createValuesIterator() {
        if (this.size() == 0) {
            return EmptyIterator.emptyIterator();
        }
        return new ValuesIterator<V>(this);
    }
    
    protected void doWriteObject(final ObjectOutputStream out) throws IOException {
        out.writeFloat(this.loadFactor);
        out.writeInt(this.data.length);
        out.writeInt(this.size);
        final MapIterator<K, V> it = this.mapIterator();
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
            final K key = (K)in.readObject();
            final V value = (V)in.readObject();
            this.put(key, value);
        }
    }
    
    @Override
    protected AbstractHashedMap<K, V> clone() {
        try {
            final AbstractHashedMap<K, V> cloned = (AbstractHashedMap<K, V>)super.clone();
            cloned.data = new HashEntry[this.data.length];
            cloned.entrySet = null;
            cloned.keySet = null;
            cloned.values = null;
            cloned.modCount = 0;
            cloned.size = 0;
            cloned.init();
            cloned.putAll((Map<? extends K, ? extends V>)this);
            return cloned;
        }
        catch (final CloneNotSupportedException ex) {
            throw new InternalError();
        }
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Map)) {
            return false;
        }
        final Map<?, ?> map = (Map<?, ?>)obj;
        if (map.size() != this.size()) {
            return false;
        }
        final MapIterator<?, ?> it = this.mapIterator();
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
    
    @Override
    public int hashCode() {
        int total = 0;
        final Iterator<Map.Entry<K, V>> it = this.createEntrySetIterator();
        while (it.hasNext()) {
            total += it.next().hashCode();
        }
        return total;
    }
    
    @Override
    public String toString() {
        if (this.size() == 0) {
            return "{}";
        }
        final StringBuilder buf = new StringBuilder(32 * this.size());
        buf.append('{');
        final MapIterator<K, V> it = this.mapIterator();
        boolean hasNext = it.hasNext();
        while (hasNext) {
            final K key = it.next();
            final V value = it.getValue();
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
    
    protected static class HashMapIterator<K, V> extends HashIterator<K, V> implements MapIterator<K, V>
    {
        protected HashMapIterator(final AbstractHashedMap<K, V> parent) {
            super(parent);
        }
        
        @Override
        public K next() {
            return super.nextEntry().getKey();
        }
        
        @Override
        public K getKey() {
            final HashEntry<K, V> current = this.currentEntry();
            if (current == null) {
                throw new IllegalStateException("getKey() can only be called after next() and before remove()");
            }
            return current.getKey();
        }
        
        @Override
        public V getValue() {
            final HashEntry<K, V> current = this.currentEntry();
            if (current == null) {
                throw new IllegalStateException("getValue() can only be called after next() and before remove()");
            }
            return current.getValue();
        }
        
        @Override
        public V setValue(final V value) {
            final HashEntry<K, V> current = this.currentEntry();
            if (current == null) {
                throw new IllegalStateException("setValue() can only be called after next() and before remove()");
            }
            return current.setValue(value);
        }
    }
    
    protected static class EntrySet<K, V> extends AbstractSet<Map.Entry<K, V>>
    {
        private final AbstractHashedMap<K, V> parent;
        
        protected EntrySet(final AbstractHashedMap<K, V> parent) {
            this.parent = parent;
        }
        
        @Override
        public int size() {
            return this.parent.size();
        }
        
        @Override
        public void clear() {
            this.parent.clear();
        }
        
        @Override
        public boolean contains(final Object entry) {
            if (entry instanceof Map.Entry) {
                final Map.Entry<?, ?> e = (Map.Entry<?, ?>)entry;
                final Map.Entry<K, V> match = this.parent.getEntry(e.getKey());
                return match != null && match.equals(e);
            }
            return false;
        }
        
        @Override
        public boolean remove(final Object obj) {
            if (!(obj instanceof Map.Entry)) {
                return false;
            }
            if (!this.contains(obj)) {
                return false;
            }
            final Map.Entry<?, ?> entry = (Map.Entry<?, ?>)obj;
            this.parent.remove(entry.getKey());
            return true;
        }
        
        @Override
        public Iterator<Map.Entry<K, V>> iterator() {
            return this.parent.createEntrySetIterator();
        }
    }
    
    protected static class EntrySetIterator<K, V> extends HashIterator<K, V> implements Iterator<Map.Entry<K, V>>
    {
        protected EntrySetIterator(final AbstractHashedMap<K, V> parent) {
            super(parent);
        }
        
        @Override
        public Map.Entry<K, V> next() {
            return super.nextEntry();
        }
    }
    
    protected static class KeySet<K> extends AbstractSet<K>
    {
        private final AbstractHashedMap<K, ?> parent;
        
        protected KeySet(final AbstractHashedMap<K, ?> parent) {
            this.parent = parent;
        }
        
        @Override
        public int size() {
            return this.parent.size();
        }
        
        @Override
        public void clear() {
            this.parent.clear();
        }
        
        @Override
        public boolean contains(final Object key) {
            return this.parent.containsKey(key);
        }
        
        @Override
        public boolean remove(final Object key) {
            final boolean result = this.parent.containsKey(key);
            this.parent.remove(key);
            return result;
        }
        
        @Override
        public Iterator<K> iterator() {
            return this.parent.createKeySetIterator();
        }
    }
    
    protected static class KeySetIterator<K> extends HashIterator<K, Object> implements Iterator<K>
    {
        protected KeySetIterator(final AbstractHashedMap<K, ?> parent) {
            super(parent);
        }
        
        @Override
        public K next() {
            return super.nextEntry().getKey();
        }
    }
    
    protected static class Values<V> extends AbstractCollection<V>
    {
        private final AbstractHashedMap<?, V> parent;
        
        protected Values(final AbstractHashedMap<?, V> parent) {
            this.parent = parent;
        }
        
        @Override
        public int size() {
            return this.parent.size();
        }
        
        @Override
        public void clear() {
            this.parent.clear();
        }
        
        @Override
        public boolean contains(final Object value) {
            return this.parent.containsValue(value);
        }
        
        @Override
        public Iterator<V> iterator() {
            return this.parent.createValuesIterator();
        }
    }
    
    protected static class ValuesIterator<V> extends HashIterator<Object, V> implements Iterator<V>
    {
        protected ValuesIterator(final AbstractHashedMap<?, V> parent) {
            super(parent);
        }
        
        @Override
        public V next() {
            return super.nextEntry().getValue();
        }
    }
    
    protected static class HashEntry<K, V> implements Map.Entry<K, V>, KeyValue<K, V>
    {
        protected HashEntry<K, V> next;
        protected int hashCode;
        protected Object key;
        protected Object value;
        
        protected HashEntry(final HashEntry<K, V> next, final int hashCode, final Object key, final V value) {
            this.next = next;
            this.hashCode = hashCode;
            this.key = key;
            this.value = value;
        }
        
        @Override
        public K getKey() {
            if (this.key == AbstractHashedMap.NULL) {
                return null;
            }
            return (K)this.key;
        }
        
        @Override
        public V getValue() {
            return (V)this.value;
        }
        
        @Override
        public V setValue(final V value) {
            final Object old = this.value;
            this.value = value;
            return (V)old;
        }
        
        @Override
        public boolean equals(final Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof Map.Entry)) {
                return false;
            }
            final Map.Entry<?, ?> other = (Map.Entry<?, ?>)obj;
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
        
        @Override
        public int hashCode() {
            return ((this.getKey() == null) ? 0 : this.getKey().hashCode()) ^ ((this.getValue() == null) ? 0 : this.getValue().hashCode());
        }
        
        @Override
        public String toString() {
            return new StringBuilder().append(this.getKey()).append('=').append(this.getValue()).toString();
        }
    }
    
    protected abstract static class HashIterator<K, V>
    {
        private final AbstractHashedMap<K, V> parent;
        private int hashIndex;
        private HashEntry<K, V> last;
        private HashEntry<K, V> next;
        private int expectedModCount;
        
        protected HashIterator(final AbstractHashedMap<K, V> parent) {
            this.parent = parent;
            HashEntry<K, V>[] data;
            int i;
            HashEntry<K, V> next;
            for (data = parent.data, i = data.length, next = null; i > 0 && next == null; next = data[--i]) {}
            this.next = next;
            this.hashIndex = i;
            this.expectedModCount = parent.modCount;
        }
        
        public boolean hasNext() {
            return this.next != null;
        }
        
        protected HashEntry<K, V> nextEntry() {
            if (this.parent.modCount != this.expectedModCount) {
                throw new ConcurrentModificationException();
            }
            final HashEntry<K, V> newCurrent = this.next;
            if (newCurrent == null) {
                throw new NoSuchElementException("No next() entry in the iteration");
            }
            HashEntry<K, V>[] data;
            int i;
            HashEntry<K, V> n;
            for (data = this.parent.data, i = this.hashIndex, n = newCurrent.next; n == null && i > 0; n = data[--i]) {}
            this.next = n;
            this.hashIndex = i;
            return this.last = newCurrent;
        }
        
        protected HashEntry<K, V> currentEntry() {
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
        
        @Override
        public String toString() {
            if (this.last != null) {
                return "Iterator[" + this.last.getKey() + "=" + this.last.getValue() + "]";
            }
            return "Iterator[]";
        }
    }
}
