package org.apache.commons.collections4.map;

import java.lang.ref.WeakReference;
import java.lang.ref.SoftReference;
import java.util.NoSuchElementException;
import java.util.ConcurrentModificationException;
import java.util.List;
import org.apache.commons.collections4.keyvalue.DefaultMapEntry;
import java.util.ArrayList;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Iterator;
import java.lang.ref.Reference;
import java.util.Collection;
import java.util.Set;
import org.apache.commons.collections4.MapIterator;
import java.util.Map;
import java.lang.ref.ReferenceQueue;

public abstract class AbstractReferenceMap<K, V> extends AbstractHashedMap<K, V>
{
    private ReferenceStrength keyType;
    private ReferenceStrength valueType;
    private boolean purgeValues;
    private transient ReferenceQueue<Object> queue;
    
    protected AbstractReferenceMap() {
    }
    
    protected AbstractReferenceMap(final ReferenceStrength keyType, final ReferenceStrength valueType, final int capacity, final float loadFactor, final boolean purgeValues) {
        super(capacity, loadFactor);
        this.keyType = keyType;
        this.valueType = valueType;
        this.purgeValues = purgeValues;
    }
    
    @Override
    protected void init() {
        this.queue = new ReferenceQueue<Object>();
    }
    
    @Override
    public int size() {
        this.purgeBeforeRead();
        return super.size();
    }
    
    @Override
    public boolean isEmpty() {
        this.purgeBeforeRead();
        return super.isEmpty();
    }
    
    @Override
    public boolean containsKey(final Object key) {
        this.purgeBeforeRead();
        final Map.Entry<K, V> entry = this.getEntry(key);
        return entry != null && entry.getValue() != null;
    }
    
    @Override
    public boolean containsValue(final Object value) {
        this.purgeBeforeRead();
        return value != null && super.containsValue(value);
    }
    
    @Override
    public V get(final Object key) {
        this.purgeBeforeRead();
        final Map.Entry<K, V> entry = this.getEntry(key);
        if (entry == null) {
            return null;
        }
        return entry.getValue();
    }
    
    @Override
    public V put(final K key, final V value) {
        if (key == null) {
            throw new NullPointerException("null keys not allowed");
        }
        if (value == null) {
            throw new NullPointerException("null values not allowed");
        }
        this.purgeBeforeWrite();
        return super.put(key, value);
    }
    
    @Override
    public V remove(final Object key) {
        if (key == null) {
            return null;
        }
        this.purgeBeforeWrite();
        return super.remove(key);
    }
    
    @Override
    public void clear() {
        super.clear();
        while (this.queue.poll() != null) {}
    }
    
    @Override
    public MapIterator<K, V> mapIterator() {
        return new ReferenceMapIterator<K, V>(this);
    }
    
    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        if (this.entrySet == null) {
            this.entrySet = (EntrySet<K, V>)new ReferenceEntrySet<Object, Object>((AbstractHashedMap<K, V>)this);
        }
        return (Set<Map.Entry<K, V>>)this.entrySet;
    }
    
    @Override
    public Set<K> keySet() {
        if (this.keySet == null) {
            this.keySet = (KeySet<K>)new ReferenceKeySet<Object>((AbstractHashedMap<K, ?>)this);
        }
        return this.keySet;
    }
    
    @Override
    public Collection<V> values() {
        if (this.values == null) {
            this.values = (Values<V>)new ReferenceValues<Object>((AbstractHashedMap<?, V>)this);
        }
        return this.values;
    }
    
    protected void purgeBeforeRead() {
        this.purge();
    }
    
    protected void purgeBeforeWrite() {
        this.purge();
    }
    
    protected void purge() {
        for (Reference<?> ref = this.queue.poll(); ref != null; ref = this.queue.poll()) {
            this.purge(ref);
        }
    }
    
    protected void purge(final Reference<?> ref) {
        final int hash = ref.hashCode();
        final int index = this.hashIndex(hash, this.data.length);
        HashEntry<K, V> previous = null;
        for (HashEntry<K, V> entry = this.data[index]; entry != null; entry = entry.next) {
            if (((ReferenceEntry)entry).purge(ref)) {
                if (previous == null) {
                    this.data[index] = entry.next;
                }
                else {
                    previous.next = entry.next;
                }
                --this.size;
                return;
            }
            previous = entry;
        }
    }
    
    @Override
    protected HashEntry<K, V> getEntry(final Object key) {
        if (key == null) {
            return null;
        }
        return super.getEntry(key);
    }
    
    protected int hashEntry(final Object key, final Object value) {
        return ((key == null) ? 0 : key.hashCode()) ^ ((value == null) ? 0 : value.hashCode());
    }
    
    @Override
    protected boolean isEqualKey(final Object key1, Object key2) {
        key2 = ((this.keyType == ReferenceStrength.HARD) ? key2 : ((Reference)key2).get());
        return key1 == key2 || key1.equals(key2);
    }
    
    @Override
    protected ReferenceEntry<K, V> createEntry(final HashEntry<K, V> next, final int hashCode, final K key, final V value) {
        return new ReferenceEntry<K, V>(this, next, hashCode, key, value);
    }
    
    @Override
    protected Iterator<Map.Entry<K, V>> createEntrySetIterator() {
        return new ReferenceEntrySetIterator<K, V>(this);
    }
    
    @Override
    protected Iterator<K> createKeySetIterator() {
        return new ReferenceKeySetIterator<K>(this);
    }
    
    @Override
    protected Iterator<V> createValuesIterator() {
        return new ReferenceValuesIterator<V>(this);
    }
    
    @Override
    protected void doWriteObject(final ObjectOutputStream out) throws IOException {
        out.writeInt(this.keyType.value);
        out.writeInt(this.valueType.value);
        out.writeBoolean(this.purgeValues);
        out.writeFloat(this.loadFactor);
        out.writeInt(this.data.length);
        final MapIterator<K, V> it = this.mapIterator();
        while (it.hasNext()) {
            out.writeObject(it.next());
            out.writeObject(it.getValue());
        }
        out.writeObject(null);
    }
    
    @Override
    protected void doReadObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        this.keyType = ReferenceStrength.resolve(in.readInt());
        this.valueType = ReferenceStrength.resolve(in.readInt());
        this.purgeValues = in.readBoolean();
        this.loadFactor = in.readFloat();
        final int capacity = in.readInt();
        this.init();
        this.data = new HashEntry[capacity];
        while (true) {
            final K key = (K)in.readObject();
            if (key == null) {
                break;
            }
            final V value = (V)in.readObject();
            this.put(key, value);
        }
        this.threshold = this.calculateThreshold(this.data.length, this.loadFactor);
    }
    
    protected boolean isKeyType(final ReferenceStrength type) {
        return this.keyType == type;
    }
    
    public enum ReferenceStrength
    {
        HARD(0), 
        SOFT(1), 
        WEAK(2);
        
        public final int value;
        
        public static ReferenceStrength resolve(final int value) {
            switch (value) {
                case 0: {
                    return ReferenceStrength.HARD;
                }
                case 1: {
                    return ReferenceStrength.SOFT;
                }
                case 2: {
                    return ReferenceStrength.WEAK;
                }
                default: {
                    throw new IllegalArgumentException();
                }
            }
        }
        
        private ReferenceStrength(final int value) {
            this.value = value;
        }
    }
    
    static class ReferenceEntrySet<K, V> extends EntrySet<K, V>
    {
        protected ReferenceEntrySet(final AbstractHashedMap<K, V> parent) {
            super(parent);
        }
        
        @Override
        public Object[] toArray() {
            return this.toArray(new Object[this.size()]);
        }
        
        @Override
        public <T> T[] toArray(final T[] arr) {
            final ArrayList<Map.Entry<K, V>> list = new ArrayList<Map.Entry<K, V>>(this.size());
            for (final Map.Entry<K, V> entry : this) {
                list.add(new DefaultMapEntry<K, V>((Map.Entry<? extends K, ? extends V>)entry));
            }
            return list.toArray(arr);
        }
    }
    
    static class ReferenceKeySet<K> extends KeySet<K>
    {
        protected ReferenceKeySet(final AbstractHashedMap<K, ?> parent) {
            super(parent);
        }
        
        @Override
        public Object[] toArray() {
            return this.toArray(new Object[this.size()]);
        }
        
        @Override
        public <T> T[] toArray(final T[] arr) {
            final List<K> list = new ArrayList<K>(this.size());
            for (final K key : this) {
                list.add(key);
            }
            return list.toArray(arr);
        }
    }
    
    static class ReferenceValues<V> extends Values<V>
    {
        protected ReferenceValues(final AbstractHashedMap<?, V> parent) {
            super(parent);
        }
        
        @Override
        public Object[] toArray() {
            return this.toArray(new Object[this.size()]);
        }
        
        @Override
        public <T> T[] toArray(final T[] arr) {
            final List<V> list = new ArrayList<V>(this.size());
            for (final V value : this) {
                list.add(value);
            }
            return list.toArray(arr);
        }
    }
    
    protected static class ReferenceEntry<K, V> extends HashEntry<K, V>
    {
        private final AbstractReferenceMap<K, V> parent;
        
        public ReferenceEntry(final AbstractReferenceMap<K, V> parent, final HashEntry<K, V> next, final int hashCode, final K key, final V value) {
            super((HashEntry<K, Object>)next, hashCode, null, null);
            this.parent = parent;
            this.key = this.toReference(((AbstractReferenceMap<Object, Object>)parent).keyType, key, hashCode);
            this.value = this.toReference(((AbstractReferenceMap<Object, Object>)parent).valueType, value, hashCode);
        }
        
        @Override
        public K getKey() {
            return (K)((((AbstractReferenceMap<Object, Object>)this.parent).keyType == ReferenceStrength.HARD) ? this.key : ((Reference)this.key).get());
        }
        
        @Override
        public V getValue() {
            return (V)((((AbstractReferenceMap<Object, Object>)this.parent).valueType == ReferenceStrength.HARD) ? this.value : ((Reference)this.value).get());
        }
        
        @Override
        public V setValue(final V obj) {
            final V old = this.getValue();
            if (((AbstractReferenceMap<Object, Object>)this.parent).valueType != ReferenceStrength.HARD) {
                ((Reference)this.value).clear();
            }
            this.value = this.toReference(((AbstractReferenceMap<Object, Object>)this.parent).valueType, obj, this.hashCode);
            return old;
        }
        
        @Override
        public boolean equals(final Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof Map.Entry)) {
                return false;
            }
            final Map.Entry<?, ?> entry = (Map.Entry<?, ?>)obj;
            final Object entryKey = entry.getKey();
            final Object entryValue = entry.getValue();
            return entryKey != null && entryValue != null && this.parent.isEqualKey(entryKey, this.key) && this.parent.isEqualValue(entryValue, this.getValue());
        }
        
        @Override
        public int hashCode() {
            return this.parent.hashEntry(this.getKey(), this.getValue());
        }
        
        protected <T> Object toReference(final ReferenceStrength type, final T referent, final int hash) {
            if (type == ReferenceStrength.HARD) {
                return referent;
            }
            if (type == ReferenceStrength.SOFT) {
                return new SoftRef(hash, referent, ((AbstractReferenceMap<Object, Object>)this.parent).queue);
            }
            if (type == ReferenceStrength.WEAK) {
                return new WeakRef(hash, referent, ((AbstractReferenceMap<Object, Object>)this.parent).queue);
            }
            throw new Error();
        }
        
        boolean purge(final Reference<?> ref) {
            boolean r = ((AbstractReferenceMap<Object, Object>)this.parent).keyType != ReferenceStrength.HARD && this.key == ref;
            r = (r || (((AbstractReferenceMap<Object, Object>)this.parent).valueType != ReferenceStrength.HARD && this.value == ref));
            if (r) {
                if (((AbstractReferenceMap<Object, Object>)this.parent).keyType != ReferenceStrength.HARD) {
                    ((Reference)this.key).clear();
                }
                if (((AbstractReferenceMap<Object, Object>)this.parent).valueType != ReferenceStrength.HARD) {
                    ((Reference)this.value).clear();
                }
                else if (((AbstractReferenceMap<Object, Object>)this.parent).purgeValues) {
                    this.value = null;
                }
            }
            return r;
        }
        
        protected ReferenceEntry<K, V> next() {
            return (ReferenceEntry)this.next;
        }
    }
    
    static class ReferenceBaseIterator<K, V>
    {
        final AbstractReferenceMap<K, V> parent;
        int index;
        ReferenceEntry<K, V> entry;
        ReferenceEntry<K, V> previous;
        K currentKey;
        K nextKey;
        V currentValue;
        V nextValue;
        int expectedModCount;
        
        public ReferenceBaseIterator(final AbstractReferenceMap<K, V> parent) {
            this.parent = parent;
            this.index = ((parent.size() != 0) ? parent.data.length : 0);
            this.expectedModCount = parent.modCount;
        }
        
        public boolean hasNext() {
            this.checkMod();
            while (this.nextNull()) {
                ReferenceEntry<K, V> e;
                int i;
                for (e = this.entry, i = this.index; e == null && i > 0; --i, e = (ReferenceEntry)this.parent.data[i]) {}
                this.entry = e;
                this.index = i;
                if (e == null) {
                    this.currentKey = null;
                    this.currentValue = null;
                    return false;
                }
                this.nextKey = e.getKey();
                this.nextValue = e.getValue();
                if (!this.nextNull()) {
                    continue;
                }
                this.entry = this.entry.next();
            }
            return true;
        }
        
        private void checkMod() {
            if (this.parent.modCount != this.expectedModCount) {
                throw new ConcurrentModificationException();
            }
        }
        
        private boolean nextNull() {
            return this.nextKey == null || this.nextValue == null;
        }
        
        protected ReferenceEntry<K, V> nextEntry() {
            this.checkMod();
            if (this.nextNull() && !this.hasNext()) {
                throw new NoSuchElementException();
            }
            this.previous = this.entry;
            this.entry = this.entry.next();
            this.currentKey = this.nextKey;
            this.currentValue = this.nextValue;
            this.nextKey = null;
            this.nextValue = null;
            return this.previous;
        }
        
        protected ReferenceEntry<K, V> currentEntry() {
            this.checkMod();
            return this.previous;
        }
        
        public void remove() {
            this.checkMod();
            if (this.previous == null) {
                throw new IllegalStateException();
            }
            this.parent.remove(this.currentKey);
            this.previous = null;
            this.currentKey = null;
            this.currentValue = null;
            this.expectedModCount = this.parent.modCount;
        }
    }
    
    static class ReferenceEntrySetIterator<K, V> extends ReferenceBaseIterator<K, V> implements Iterator<Map.Entry<K, V>>
    {
        public ReferenceEntrySetIterator(final AbstractReferenceMap<K, V> parent) {
            super(parent);
        }
        
        @Override
        public Map.Entry<K, V> next() {
            return this.nextEntry();
        }
    }
    
    static class ReferenceKeySetIterator<K> extends ReferenceBaseIterator<K, Object> implements Iterator<K>
    {
        ReferenceKeySetIterator(final AbstractReferenceMap<K, ?> parent) {
            super(parent);
        }
        
        @Override
        public K next() {
            return this.nextEntry().getKey();
        }
    }
    
    static class ReferenceValuesIterator<V> extends ReferenceBaseIterator<Object, V> implements Iterator<V>
    {
        ReferenceValuesIterator(final AbstractReferenceMap<?, V> parent) {
            super(parent);
        }
        
        @Override
        public V next() {
            return this.nextEntry().getValue();
        }
    }
    
    static class ReferenceMapIterator<K, V> extends ReferenceBaseIterator<K, V> implements MapIterator<K, V>
    {
        protected ReferenceMapIterator(final AbstractReferenceMap<K, V> parent) {
            super(parent);
        }
        
        @Override
        public K next() {
            return this.nextEntry().getKey();
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
    
    static class SoftRef<T> extends SoftReference<T>
    {
        private final int hash;
        
        public SoftRef(final int hash, final T r, final ReferenceQueue<? super T> q) {
            super(r, q);
            this.hash = hash;
        }
        
        @Override
        public int hashCode() {
            return this.hash;
        }
    }
    
    static class WeakRef<T> extends WeakReference<T>
    {
        private final int hash;
        
        public WeakRef(final int hash, final T r, final ReferenceQueue<? super T> q) {
            super(r, q);
            this.hash = hash;
        }
        
        @Override
        public int hashCode() {
            return this.hash;
        }
    }
}
