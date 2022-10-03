package org.apache.commons.collections4.map;

import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Iterator;
import org.apache.commons.collections4.MapIterator;
import java.util.Map;
import java.io.Serializable;
import org.apache.commons.collections4.keyvalue.MultiKey;

public class MultiKeyMap<K, V> extends AbstractMapDecorator<MultiKey<? extends K>, V> implements Serializable, Cloneable
{
    private static final long serialVersionUID = -1788199231038721040L;
    
    public static <K, V> MultiKeyMap<K, V> multiKeyMap(final AbstractHashedMap<MultiKey<? extends K>, V> map) {
        if (map == null) {
            throw new NullPointerException("Map must not be null");
        }
        if (map.size() > 0) {
            throw new IllegalArgumentException("Map must be empty");
        }
        return new MultiKeyMap<K, V>(map);
    }
    
    public MultiKeyMap() {
        this((AbstractHashedMap)new HashedMap());
    }
    
    protected MultiKeyMap(final AbstractHashedMap<MultiKey<? extends K>, V> map) {
        super(map);
        this.map = (Map<K, V>)map;
    }
    
    public V get(final Object key1, final Object key2) {
        final int hashCode = this.hash(key1, key2);
        for (AbstractHashedMap.HashEntry<MultiKey<? extends K>, V> entry = this.decorated().data[this.decorated().hashIndex(hashCode, this.decorated().data.length)]; entry != null; entry = entry.next) {
            if (entry.hashCode == hashCode && this.isEqualKey(entry, key1, key2)) {
                return entry.getValue();
            }
        }
        return null;
    }
    
    public boolean containsKey(final Object key1, final Object key2) {
        final int hashCode = this.hash(key1, key2);
        for (AbstractHashedMap.HashEntry<MultiKey<? extends K>, V> entry = this.decorated().data[this.decorated().hashIndex(hashCode, this.decorated().data.length)]; entry != null; entry = entry.next) {
            if (entry.hashCode == hashCode && this.isEqualKey(entry, key1, key2)) {
                return true;
            }
        }
        return false;
    }
    
    public V put(final K key1, final K key2, final V value) {
        final int hashCode = this.hash(key1, key2);
        final int index = this.decorated().hashIndex(hashCode, this.decorated().data.length);
        for (AbstractHashedMap.HashEntry<MultiKey<? extends K>, V> entry = this.decorated().data[index]; entry != null; entry = entry.next) {
            if (entry.hashCode == hashCode && this.isEqualKey(entry, key1, key2)) {
                final V oldValue = entry.getValue();
                this.decorated().updateEntry(entry, value);
                return oldValue;
            }
        }
        this.decorated().addMapping(index, hashCode, (MultiKey<? extends K>)new MultiKey<K>((K)key1, (K)key2), value);
        return null;
    }
    
    public V removeMultiKey(final Object key1, final Object key2) {
        final int hashCode = this.hash(key1, key2);
        final int index = this.decorated().hashIndex(hashCode, this.decorated().data.length);
        AbstractHashedMap.HashEntry<MultiKey<? extends K>, V> entry = this.decorated().data[index];
        AbstractHashedMap.HashEntry<MultiKey<? extends K>, V> previous = null;
        while (entry != null) {
            if (entry.hashCode == hashCode && this.isEqualKey(entry, key1, key2)) {
                final V oldValue = entry.getValue();
                this.decorated().removeMapping(entry, index, previous);
                return oldValue;
            }
            previous = entry;
            entry = entry.next;
        }
        return null;
    }
    
    protected int hash(final Object key1, final Object key2) {
        int h = 0;
        if (key1 != null) {
            h ^= key1.hashCode();
        }
        if (key2 != null) {
            h ^= key2.hashCode();
        }
        h += ~(h << 9);
        h ^= h >>> 14;
        h += h << 4;
        h ^= h >>> 10;
        return h;
    }
    
    protected boolean isEqualKey(final AbstractHashedMap.HashEntry<MultiKey<? extends K>, V> entry, final Object key1, final Object key2) {
        final MultiKey<? extends K> multi = entry.getKey();
        return multi.size() == 2 && (key1 == multi.getKey(0) || (key1 != null && key1.equals(multi.getKey(0)))) && (key2 == multi.getKey(1) || (key2 != null && key2.equals(multi.getKey(1))));
    }
    
    public V get(final Object key1, final Object key2, final Object key3) {
        final int hashCode = this.hash(key1, key2, key3);
        for (AbstractHashedMap.HashEntry<MultiKey<? extends K>, V> entry = this.decorated().data[this.decorated().hashIndex(hashCode, this.decorated().data.length)]; entry != null; entry = entry.next) {
            if (entry.hashCode == hashCode && this.isEqualKey(entry, key1, key2, key3)) {
                return entry.getValue();
            }
        }
        return null;
    }
    
    public boolean containsKey(final Object key1, final Object key2, final Object key3) {
        final int hashCode = this.hash(key1, key2, key3);
        for (AbstractHashedMap.HashEntry<MultiKey<? extends K>, V> entry = this.decorated().data[this.decorated().hashIndex(hashCode, this.decorated().data.length)]; entry != null; entry = entry.next) {
            if (entry.hashCode == hashCode && this.isEqualKey(entry, key1, key2, key3)) {
                return true;
            }
        }
        return false;
    }
    
    public V put(final K key1, final K key2, final K key3, final V value) {
        final int hashCode = this.hash(key1, key2, key3);
        final int index = this.decorated().hashIndex(hashCode, this.decorated().data.length);
        for (AbstractHashedMap.HashEntry<MultiKey<? extends K>, V> entry = this.decorated().data[index]; entry != null; entry = entry.next) {
            if (entry.hashCode == hashCode && this.isEqualKey(entry, key1, key2, key3)) {
                final V oldValue = entry.getValue();
                this.decorated().updateEntry(entry, value);
                return oldValue;
            }
        }
        this.decorated().addMapping(index, hashCode, (MultiKey<? extends K>)new MultiKey<K>((K)key1, (K)key2, (K)key3), value);
        return null;
    }
    
    public V removeMultiKey(final Object key1, final Object key2, final Object key3) {
        final int hashCode = this.hash(key1, key2, key3);
        final int index = this.decorated().hashIndex(hashCode, this.decorated().data.length);
        AbstractHashedMap.HashEntry<MultiKey<? extends K>, V> entry = this.decorated().data[index];
        AbstractHashedMap.HashEntry<MultiKey<? extends K>, V> previous = null;
        while (entry != null) {
            if (entry.hashCode == hashCode && this.isEqualKey(entry, key1, key2, key3)) {
                final V oldValue = entry.getValue();
                this.decorated().removeMapping(entry, index, previous);
                return oldValue;
            }
            previous = entry;
            entry = entry.next;
        }
        return null;
    }
    
    protected int hash(final Object key1, final Object key2, final Object key3) {
        int h = 0;
        if (key1 != null) {
            h ^= key1.hashCode();
        }
        if (key2 != null) {
            h ^= key2.hashCode();
        }
        if (key3 != null) {
            h ^= key3.hashCode();
        }
        h += ~(h << 9);
        h ^= h >>> 14;
        h += h << 4;
        h ^= h >>> 10;
        return h;
    }
    
    protected boolean isEqualKey(final AbstractHashedMap.HashEntry<MultiKey<? extends K>, V> entry, final Object key1, final Object key2, final Object key3) {
        final MultiKey<? extends K> multi = entry.getKey();
        return multi.size() == 3 && (key1 == multi.getKey(0) || (key1 != null && key1.equals(multi.getKey(0)))) && (key2 == multi.getKey(1) || (key2 != null && key2.equals(multi.getKey(1)))) && (key3 == multi.getKey(2) || (key3 != null && key3.equals(multi.getKey(2))));
    }
    
    public V get(final Object key1, final Object key2, final Object key3, final Object key4) {
        final int hashCode = this.hash(key1, key2, key3, key4);
        for (AbstractHashedMap.HashEntry<MultiKey<? extends K>, V> entry = this.decorated().data[this.decorated().hashIndex(hashCode, this.decorated().data.length)]; entry != null; entry = entry.next) {
            if (entry.hashCode == hashCode && this.isEqualKey(entry, key1, key2, key3, key4)) {
                return entry.getValue();
            }
        }
        return null;
    }
    
    public boolean containsKey(final Object key1, final Object key2, final Object key3, final Object key4) {
        final int hashCode = this.hash(key1, key2, key3, key4);
        for (AbstractHashedMap.HashEntry<MultiKey<? extends K>, V> entry = this.decorated().data[this.decorated().hashIndex(hashCode, this.decorated().data.length)]; entry != null; entry = entry.next) {
            if (entry.hashCode == hashCode && this.isEqualKey(entry, key1, key2, key3, key4)) {
                return true;
            }
        }
        return false;
    }
    
    public V put(final K key1, final K key2, final K key3, final K key4, final V value) {
        final int hashCode = this.hash(key1, key2, key3, key4);
        final int index = this.decorated().hashIndex(hashCode, this.decorated().data.length);
        for (AbstractHashedMap.HashEntry<MultiKey<? extends K>, V> entry = this.decorated().data[index]; entry != null; entry = entry.next) {
            if (entry.hashCode == hashCode && this.isEqualKey(entry, key1, key2, key3, key4)) {
                final V oldValue = entry.getValue();
                this.decorated().updateEntry(entry, value);
                return oldValue;
            }
        }
        this.decorated().addMapping(index, hashCode, (MultiKey<? extends K>)new MultiKey<K>((K)key1, (K)key2, (K)key3, (K)key4), value);
        return null;
    }
    
    public V removeMultiKey(final Object key1, final Object key2, final Object key3, final Object key4) {
        final int hashCode = this.hash(key1, key2, key3, key4);
        final int index = this.decorated().hashIndex(hashCode, this.decorated().data.length);
        AbstractHashedMap.HashEntry<MultiKey<? extends K>, V> entry = this.decorated().data[index];
        AbstractHashedMap.HashEntry<MultiKey<? extends K>, V> previous = null;
        while (entry != null) {
            if (entry.hashCode == hashCode && this.isEqualKey(entry, key1, key2, key3, key4)) {
                final V oldValue = entry.getValue();
                this.decorated().removeMapping(entry, index, previous);
                return oldValue;
            }
            previous = entry;
            entry = entry.next;
        }
        return null;
    }
    
    protected int hash(final Object key1, final Object key2, final Object key3, final Object key4) {
        int h = 0;
        if (key1 != null) {
            h ^= key1.hashCode();
        }
        if (key2 != null) {
            h ^= key2.hashCode();
        }
        if (key3 != null) {
            h ^= key3.hashCode();
        }
        if (key4 != null) {
            h ^= key4.hashCode();
        }
        h += ~(h << 9);
        h ^= h >>> 14;
        h += h << 4;
        h ^= h >>> 10;
        return h;
    }
    
    protected boolean isEqualKey(final AbstractHashedMap.HashEntry<MultiKey<? extends K>, V> entry, final Object key1, final Object key2, final Object key3, final Object key4) {
        final MultiKey<? extends K> multi = entry.getKey();
        return multi.size() == 4 && (key1 == multi.getKey(0) || (key1 != null && key1.equals(multi.getKey(0)))) && (key2 == multi.getKey(1) || (key2 != null && key2.equals(multi.getKey(1)))) && (key3 == multi.getKey(2) || (key3 != null && key3.equals(multi.getKey(2)))) && (key4 == multi.getKey(3) || (key4 != null && key4.equals(multi.getKey(3))));
    }
    
    public V get(final Object key1, final Object key2, final Object key3, final Object key4, final Object key5) {
        final int hashCode = this.hash(key1, key2, key3, key4, key5);
        for (AbstractHashedMap.HashEntry<MultiKey<? extends K>, V> entry = this.decorated().data[this.decorated().hashIndex(hashCode, this.decorated().data.length)]; entry != null; entry = entry.next) {
            if (entry.hashCode == hashCode && this.isEqualKey(entry, key1, key2, key3, key4, key5)) {
                return entry.getValue();
            }
        }
        return null;
    }
    
    public boolean containsKey(final Object key1, final Object key2, final Object key3, final Object key4, final Object key5) {
        final int hashCode = this.hash(key1, key2, key3, key4, key5);
        for (AbstractHashedMap.HashEntry<MultiKey<? extends K>, V> entry = this.decorated().data[this.decorated().hashIndex(hashCode, this.decorated().data.length)]; entry != null; entry = entry.next) {
            if (entry.hashCode == hashCode && this.isEqualKey(entry, key1, key2, key3, key4, key5)) {
                return true;
            }
        }
        return false;
    }
    
    public V put(final K key1, final K key2, final K key3, final K key4, final K key5, final V value) {
        final int hashCode = this.hash(key1, key2, key3, key4, key5);
        final int index = this.decorated().hashIndex(hashCode, this.decorated().data.length);
        for (AbstractHashedMap.HashEntry<MultiKey<? extends K>, V> entry = this.decorated().data[index]; entry != null; entry = entry.next) {
            if (entry.hashCode == hashCode && this.isEqualKey(entry, key1, key2, key3, key4, key5)) {
                final V oldValue = entry.getValue();
                this.decorated().updateEntry(entry, value);
                return oldValue;
            }
        }
        this.decorated().addMapping(index, hashCode, (MultiKey<? extends K>)new MultiKey<K>((K)key1, (K)key2, (K)key3, (K)key4, (K)key5), value);
        return null;
    }
    
    public V removeMultiKey(final Object key1, final Object key2, final Object key3, final Object key4, final Object key5) {
        final int hashCode = this.hash(key1, key2, key3, key4, key5);
        final int index = this.decorated().hashIndex(hashCode, this.decorated().data.length);
        AbstractHashedMap.HashEntry<MultiKey<? extends K>, V> entry = this.decorated().data[index];
        AbstractHashedMap.HashEntry<MultiKey<? extends K>, V> previous = null;
        while (entry != null) {
            if (entry.hashCode == hashCode && this.isEqualKey(entry, key1, key2, key3, key4, key5)) {
                final V oldValue = entry.getValue();
                this.decorated().removeMapping(entry, index, previous);
                return oldValue;
            }
            previous = entry;
            entry = entry.next;
        }
        return null;
    }
    
    protected int hash(final Object key1, final Object key2, final Object key3, final Object key4, final Object key5) {
        int h = 0;
        if (key1 != null) {
            h ^= key1.hashCode();
        }
        if (key2 != null) {
            h ^= key2.hashCode();
        }
        if (key3 != null) {
            h ^= key3.hashCode();
        }
        if (key4 != null) {
            h ^= key4.hashCode();
        }
        if (key5 != null) {
            h ^= key5.hashCode();
        }
        h += ~(h << 9);
        h ^= h >>> 14;
        h += h << 4;
        h ^= h >>> 10;
        return h;
    }
    
    protected boolean isEqualKey(final AbstractHashedMap.HashEntry<MultiKey<? extends K>, V> entry, final Object key1, final Object key2, final Object key3, final Object key4, final Object key5) {
        final MultiKey<? extends K> multi = entry.getKey();
        return multi.size() == 5 && (key1 == multi.getKey(0) || (key1 != null && key1.equals(multi.getKey(0)))) && (key2 == multi.getKey(1) || (key2 != null && key2.equals(multi.getKey(1)))) && (key3 == multi.getKey(2) || (key3 != null && key3.equals(multi.getKey(2)))) && (key4 == multi.getKey(3) || (key4 != null && key4.equals(multi.getKey(3)))) && (key5 == multi.getKey(4) || (key5 != null && key5.equals(multi.getKey(4))));
    }
    
    public boolean removeAll(final Object key1) {
        boolean modified = false;
        final MapIterator<MultiKey<? extends K>, V> it = this.mapIterator();
        while (it.hasNext()) {
            final MultiKey<? extends K> multi = it.next();
            if (multi.size() >= 1) {
                if (key1 == null) {
                    if (multi.getKey(0) != null) {
                        continue;
                    }
                }
                else if (!key1.equals(multi.getKey(0))) {
                    continue;
                }
                it.remove();
                modified = true;
            }
        }
        return modified;
    }
    
    public boolean removeAll(final Object key1, final Object key2) {
        boolean modified = false;
        final MapIterator<MultiKey<? extends K>, V> it = this.mapIterator();
        while (it.hasNext()) {
            final MultiKey<? extends K> multi = it.next();
            if (multi.size() >= 2) {
                if (key1 == null) {
                    if (multi.getKey(0) != null) {
                        continue;
                    }
                }
                else if (!key1.equals(multi.getKey(0))) {
                    continue;
                }
                if (key2 == null) {
                    if (multi.getKey(1) != null) {
                        continue;
                    }
                }
                else if (!key2.equals(multi.getKey(1))) {
                    continue;
                }
                it.remove();
                modified = true;
            }
        }
        return modified;
    }
    
    public boolean removeAll(final Object key1, final Object key2, final Object key3) {
        boolean modified = false;
        final MapIterator<MultiKey<? extends K>, V> it = this.mapIterator();
        while (it.hasNext()) {
            final MultiKey<? extends K> multi = it.next();
            if (multi.size() >= 3) {
                if (key1 == null) {
                    if (multi.getKey(0) != null) {
                        continue;
                    }
                }
                else if (!key1.equals(multi.getKey(0))) {
                    continue;
                }
                if (key2 == null) {
                    if (multi.getKey(1) != null) {
                        continue;
                    }
                }
                else if (!key2.equals(multi.getKey(1))) {
                    continue;
                }
                if (key3 == null) {
                    if (multi.getKey(2) != null) {
                        continue;
                    }
                }
                else if (!key3.equals(multi.getKey(2))) {
                    continue;
                }
                it.remove();
                modified = true;
            }
        }
        return modified;
    }
    
    public boolean removeAll(final Object key1, final Object key2, final Object key3, final Object key4) {
        boolean modified = false;
        final MapIterator<MultiKey<? extends K>, V> it = this.mapIterator();
        while (it.hasNext()) {
            final MultiKey<? extends K> multi = it.next();
            if (multi.size() >= 4) {
                if (key1 == null) {
                    if (multi.getKey(0) != null) {
                        continue;
                    }
                }
                else if (!key1.equals(multi.getKey(0))) {
                    continue;
                }
                if (key2 == null) {
                    if (multi.getKey(1) != null) {
                        continue;
                    }
                }
                else if (!key2.equals(multi.getKey(1))) {
                    continue;
                }
                if (key3 == null) {
                    if (multi.getKey(2) != null) {
                        continue;
                    }
                }
                else if (!key3.equals(multi.getKey(2))) {
                    continue;
                }
                if (key4 == null) {
                    if (multi.getKey(3) != null) {
                        continue;
                    }
                }
                else if (!key4.equals(multi.getKey(3))) {
                    continue;
                }
                it.remove();
                modified = true;
            }
        }
        return modified;
    }
    
    protected void checkKey(final MultiKey<?> key) {
        if (key == null) {
            throw new NullPointerException("Key must not be null");
        }
    }
    
    public MultiKeyMap<K, V> clone() {
        try {
            return (MultiKeyMap)super.clone();
        }
        catch (final CloneNotSupportedException e) {
            throw new InternalError();
        }
    }
    
    @Override
    public V put(final MultiKey<? extends K> key, final V value) {
        this.checkKey(key);
        return super.put(key, value);
    }
    
    @Override
    public void putAll(final Map<? extends MultiKey<? extends K>, ? extends V> mapToCopy) {
        for (final MultiKey<? extends K> key : mapToCopy.keySet()) {
            this.checkKey(key);
        }
        super.putAll(mapToCopy);
    }
    
    @Override
    public MapIterator<MultiKey<? extends K>, V> mapIterator() {
        return this.decorated().mapIterator();
    }
    
    @Override
    protected AbstractHashedMap<MultiKey<? extends K>, V> decorated() {
        return (AbstractHashedMap)super.decorated();
    }
    
    private void writeObject(final ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeObject(this.map);
    }
    
    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.map = (Map)in.readObject();
    }
}
