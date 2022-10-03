package org.apache.commons.collections4.map;

import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Map;
import java.io.Serializable;
import org.apache.commons.collections4.BoundedMap;

public class LRUMap<K, V> extends AbstractLinkedMap<K, V> implements BoundedMap<K, V>, Serializable, Cloneable
{
    private static final long serialVersionUID = -612114643488955218L;
    protected static final int DEFAULT_MAX_SIZE = 100;
    private transient int maxSize;
    private boolean scanUntilRemovable;
    
    public LRUMap() {
        this(100, 0.75f, false);
    }
    
    public LRUMap(final int maxSize) {
        this(maxSize, 0.75f);
    }
    
    public LRUMap(final int maxSize, final int initialSize) {
        this(maxSize, initialSize, 0.75f);
    }
    
    public LRUMap(final int maxSize, final boolean scanUntilRemovable) {
        this(maxSize, 0.75f, scanUntilRemovable);
    }
    
    public LRUMap(final int maxSize, final float loadFactor) {
        this(maxSize, loadFactor, false);
    }
    
    public LRUMap(final int maxSize, final int initialSize, final float loadFactor) {
        this(maxSize, initialSize, loadFactor, false);
    }
    
    public LRUMap(final int maxSize, final float loadFactor, final boolean scanUntilRemovable) {
        this(maxSize, maxSize, loadFactor, scanUntilRemovable);
    }
    
    public LRUMap(final int maxSize, final int initialSize, final float loadFactor, final boolean scanUntilRemovable) {
        super(initialSize, loadFactor);
        if (maxSize < 1) {
            throw new IllegalArgumentException("LRUMap max size must be greater than 0");
        }
        if (initialSize > maxSize) {
            throw new IllegalArgumentException("LRUMap initial size must not be greather than max size");
        }
        this.maxSize = maxSize;
        this.scanUntilRemovable = scanUntilRemovable;
    }
    
    public LRUMap(final Map<? extends K, ? extends V> map) {
        this(map, false);
    }
    
    public LRUMap(final Map<? extends K, ? extends V> map, final boolean scanUntilRemovable) {
        this(map.size(), 0.75f, scanUntilRemovable);
        this.putAll(map);
    }
    
    @Override
    public V get(final Object key) {
        return this.get(key, true);
    }
    
    public V get(final Object key, final boolean updateToMRU) {
        final LinkEntry<K, V> entry = this.getEntry(key);
        if (entry == null) {
            return null;
        }
        if (updateToMRU) {
            this.moveToMRU(entry);
        }
        return entry.getValue();
    }
    
    protected void moveToMRU(final LinkEntry<K, V> entry) {
        if (entry.after != this.header) {
            ++this.modCount;
            if (entry.before == null) {
                throw new IllegalStateException("Entry.before is null. Please check that your keys are immutable, and that you have used synchronization properly. If so, then please report this to dev@commons.apache.org as a bug.");
            }
            entry.before.after = entry.after;
            entry.after.before = entry.before;
            entry.after = this.header;
            entry.before = this.header.before;
            this.header.before.after = entry;
            this.header.before = entry;
        }
        else if (entry == this.header) {
            throw new IllegalStateException("Can't move header to MRU (please report this to dev@commons.apache.org)");
        }
    }
    
    @Override
    protected void updateEntry(final HashEntry<K, V> entry, final V newValue) {
        this.moveToMRU((LinkEntry)entry);
        entry.setValue(newValue);
    }
    
    @Override
    protected void addMapping(final int hashIndex, final int hashCode, final K key, final V value) {
        if (this.isFull()) {
            LinkEntry<K, V> reuse = this.header.after;
            boolean removeLRUEntry = false;
            if (this.scanUntilRemovable) {
                while (reuse != this.header && reuse != null) {
                    if (this.removeLRU(reuse)) {
                        removeLRUEntry = true;
                        break;
                    }
                    reuse = reuse.after;
                }
                if (reuse == null) {
                    throw new IllegalStateException("Entry.after=null, header.after" + this.header.after + " header.before" + this.header.before + " key=" + key + " value=" + value + " size=" + this.size + " maxSize=" + this.maxSize + " Please check that your keys are immutable, and that you have used synchronization properly." + " If so, then please report this to dev@commons.apache.org as a bug.");
                }
            }
            else {
                removeLRUEntry = this.removeLRU(reuse);
            }
            if (removeLRUEntry) {
                if (reuse == null) {
                    throw new IllegalStateException("reuse=null, header.after=" + this.header.after + " header.before" + this.header.before + " key=" + key + " value=" + value + " size=" + this.size + " maxSize=" + this.maxSize + " Please check that your keys are immutable, and that you have used synchronization properly." + " If so, then please report this to dev@commons.apache.org as a bug.");
                }
                this.reuseMapping(reuse, hashIndex, hashCode, key, value);
            }
            else {
                super.addMapping(hashIndex, hashCode, key, value);
            }
        }
        else {
            super.addMapping(hashIndex, hashCode, key, value);
        }
    }
    
    protected void reuseMapping(final LinkEntry<K, V> entry, final int hashIndex, final int hashCode, final K key, final V value) {
        try {
            final int removeIndex = this.hashIndex(entry.hashCode, this.data.length);
            final HashEntry<K, V>[] tmp = this.data;
            HashEntry<K, V> loop = tmp[removeIndex];
            HashEntry<K, V> previous = null;
            while (loop != entry && loop != null) {
                previous = loop;
                loop = loop.next;
            }
            if (loop == null) {
                throw new IllegalStateException("Entry.next=null, data[removeIndex]=" + this.data[removeIndex] + " previous=" + previous + " key=" + key + " value=" + value + " size=" + this.size + " maxSize=" + this.maxSize + " Please check that your keys are immutable, and that you have used synchronization properly." + " If so, then please report this to dev@commons.apache.org as a bug.");
            }
            ++this.modCount;
            this.removeEntry(entry, removeIndex, previous);
            this.reuseEntry(entry, hashIndex, hashCode, key, value);
            this.addEntry(entry, hashIndex);
        }
        catch (final NullPointerException ex) {
            throw new IllegalStateException("NPE, entry=" + entry + " entryIsHeader=" + (entry == this.header) + " key=" + key + " value=" + value + " size=" + this.size + " maxSize=" + this.maxSize + " Please check that your keys are immutable, and that you have used synchronization properly." + " If so, then please report this to dev@commons.apache.org as a bug.");
        }
    }
    
    protected boolean removeLRU(final LinkEntry<K, V> entry) {
        return true;
    }
    
    @Override
    public boolean isFull() {
        return this.size >= this.maxSize;
    }
    
    @Override
    public int maxSize() {
        return this.maxSize;
    }
    
    public boolean isScanUntilRemovable() {
        return this.scanUntilRemovable;
    }
    
    public LRUMap<K, V> clone() {
        return (LRUMap)super.clone();
    }
    
    private void writeObject(final ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        this.doWriteObject(out);
    }
    
    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.doReadObject(in);
    }
    
    @Override
    protected void doWriteObject(final ObjectOutputStream out) throws IOException {
        out.writeInt(this.maxSize);
        super.doWriteObject(out);
    }
    
    @Override
    protected void doReadObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        this.maxSize = in.readInt();
        super.doReadObject(in);
    }
}
