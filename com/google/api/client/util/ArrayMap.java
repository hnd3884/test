package com.google.api.client.util;

import java.util.NoSuchElementException;
import java.util.Iterator;
import java.util.AbstractSet;
import java.util.Map;
import java.util.Set;
import java.util.AbstractMap;

public class ArrayMap<K, V> extends AbstractMap<K, V> implements Cloneable
{
    int size;
    private Object[] data;
    
    public static <K, V> ArrayMap<K, V> create() {
        return new ArrayMap<K, V>();
    }
    
    public static <K, V> ArrayMap<K, V> create(final int initialCapacity) {
        final ArrayMap<K, V> result = create();
        result.ensureCapacity(initialCapacity);
        return result;
    }
    
    public static <K, V> ArrayMap<K, V> of(final Object... keyValuePairs) {
        final ArrayMap<K, V> result = create(1);
        final int length = keyValuePairs.length;
        if (1 == length % 2) {
            throw new IllegalArgumentException("missing value for last key: " + keyValuePairs[length - 1]);
        }
        result.size = keyValuePairs.length / 2;
        final ArrayMap<K, V> arrayMap = result;
        final Object[] data2 = new Object[length];
        arrayMap.data = data2;
        final Object[] data = data2;
        System.arraycopy(keyValuePairs, 0, data, 0, length);
        return result;
    }
    
    @Override
    public final int size() {
        return this.size;
    }
    
    public final K getKey(final int index) {
        if (index < 0 || index >= this.size) {
            return null;
        }
        final K result = (K)this.data[index << 1];
        return result;
    }
    
    public final V getValue(final int index) {
        if (index < 0 || index >= this.size) {
            return null;
        }
        return this.valueAtDataIndex(1 + (index << 1));
    }
    
    public final V set(final int index, final K key, final V value) {
        if (index < 0) {
            throw new IndexOutOfBoundsException();
        }
        final int minSize = index + 1;
        this.ensureCapacity(minSize);
        final int dataIndex = index << 1;
        final V result = this.valueAtDataIndex(dataIndex + 1);
        this.setData(dataIndex, key, value);
        if (minSize > this.size) {
            this.size = minSize;
        }
        return result;
    }
    
    public final V set(final int index, final V value) {
        final int size = this.size;
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }
        final int valueDataIndex = 1 + (index << 1);
        final V result = this.valueAtDataIndex(valueDataIndex);
        this.data[valueDataIndex] = value;
        return result;
    }
    
    public final void add(final K key, final V value) {
        this.set(this.size, key, value);
    }
    
    public final V remove(final int index) {
        return this.removeFromDataIndexOfKey(index << 1);
    }
    
    @Override
    public final boolean containsKey(final Object key) {
        return -2 != this.getDataIndexOfKey(key);
    }
    
    public final int getIndexOfKey(final K key) {
        return this.getDataIndexOfKey(key) >> 1;
    }
    
    @Override
    public final V get(final Object key) {
        return this.valueAtDataIndex(this.getDataIndexOfKey(key) + 1);
    }
    
    @Override
    public final V put(final K key, final V value) {
        int index = this.getIndexOfKey(key);
        if (index == -1) {
            index = this.size;
        }
        return this.set(index, key, value);
    }
    
    @Override
    public final V remove(final Object key) {
        return this.removeFromDataIndexOfKey(this.getDataIndexOfKey(key));
    }
    
    public final void trim() {
        this.setDataCapacity(this.size << 1);
    }
    
    public final void ensureCapacity(final int minCapacity) {
        if (minCapacity < 0) {
            throw new IndexOutOfBoundsException();
        }
        final Object[] data = this.data;
        final int minDataCapacity = minCapacity << 1;
        final int oldDataCapacity = (data == null) ? 0 : data.length;
        if (minDataCapacity > oldDataCapacity) {
            int newDataCapacity = oldDataCapacity / 2 * 3 + 1;
            if (newDataCapacity % 2 != 0) {
                ++newDataCapacity;
            }
            if (newDataCapacity < minDataCapacity) {
                newDataCapacity = minDataCapacity;
            }
            this.setDataCapacity(newDataCapacity);
        }
    }
    
    private void setDataCapacity(final int newDataCapacity) {
        if (newDataCapacity == 0) {
            this.data = null;
            return;
        }
        final int size = this.size;
        final Object[] oldData = this.data;
        if (size == 0 || newDataCapacity != oldData.length) {
            final Object[] data = new Object[newDataCapacity];
            this.data = data;
            final Object[] newData = data;
            if (size != 0) {
                System.arraycopy(oldData, 0, newData, 0, size << 1);
            }
        }
    }
    
    private void setData(final int dataIndexOfKey, final K key, final V value) {
        final Object[] data = this.data;
        data[dataIndexOfKey] = key;
        data[dataIndexOfKey + 1] = value;
    }
    
    private V valueAtDataIndex(final int dataIndex) {
        if (dataIndex < 0) {
            return null;
        }
        final V result = (V)this.data[dataIndex];
        return result;
    }
    
    private int getDataIndexOfKey(final Object key) {
        final int dataSize = this.size << 1;
        final Object[] data = this.data;
        for (int i = 0; i < dataSize; i += 2) {
            final Object k = data[i];
            if (key == null) {
                if (k == null) {
                    return i;
                }
            }
            else if (key.equals(k)) {
                return i;
            }
        }
        return -2;
    }
    
    private V removeFromDataIndexOfKey(final int dataIndexOfKey) {
        final int dataSize = this.size << 1;
        if (dataIndexOfKey < 0 || dataIndexOfKey >= dataSize) {
            return null;
        }
        final V result = this.valueAtDataIndex(dataIndexOfKey + 1);
        final Object[] data = this.data;
        final int moved = dataSize - dataIndexOfKey - 2;
        if (moved != 0) {
            System.arraycopy(data, dataIndexOfKey + 2, data, dataIndexOfKey, moved);
        }
        --this.size;
        this.setData(dataSize - 2, null, null);
        return result;
    }
    
    @Override
    public void clear() {
        this.size = 0;
        this.data = null;
    }
    
    @Override
    public final boolean containsValue(final Object value) {
        final int dataSize = this.size << 1;
        final Object[] data = this.data;
        for (int i = 1; i < dataSize; i += 2) {
            final Object v = data[i];
            if (value == null) {
                if (v == null) {
                    return true;
                }
            }
            else if (value.equals(v)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public final Set<Map.Entry<K, V>> entrySet() {
        return new EntrySet();
    }
    
    public ArrayMap<K, V> clone() {
        try {
            final ArrayMap<K, V> result = (ArrayMap<K, V>)super.clone();
            final Object[] data = this.data;
            if (data != null) {
                final int length = data.length;
                final ArrayMap<K, V> arrayMap = result;
                final Object[] data2 = new Object[length];
                arrayMap.data = data2;
                final Object[] resultData = data2;
                System.arraycopy(data, 0, resultData, 0, length);
            }
            return result;
        }
        catch (final CloneNotSupportedException e) {
            return null;
        }
    }
    
    final class EntrySet extends AbstractSet<Map.Entry<K, V>>
    {
        @Override
        public Iterator<Map.Entry<K, V>> iterator() {
            return new EntryIterator();
        }
        
        @Override
        public int size() {
            return ArrayMap.this.size;
        }
    }
    
    final class EntryIterator implements Iterator<Map.Entry<K, V>>
    {
        private boolean removed;
        private int nextIndex;
        
        @Override
        public boolean hasNext() {
            return this.nextIndex < ArrayMap.this.size;
        }
        
        @Override
        public Map.Entry<K, V> next() {
            final int index = this.nextIndex;
            if (index == ArrayMap.this.size) {
                throw new NoSuchElementException();
            }
            ++this.nextIndex;
            this.removed = false;
            return new Entry(index);
        }
        
        @Override
        public void remove() {
            final int index = this.nextIndex - 1;
            if (this.removed || index < 0) {
                throw new IllegalArgumentException();
            }
            ArrayMap.this.remove(index);
            --this.nextIndex;
            this.removed = true;
        }
    }
    
    final class Entry implements Map.Entry<K, V>
    {
        private int index;
        
        Entry(final int index) {
            this.index = index;
        }
        
        @Override
        public K getKey() {
            return ArrayMap.this.getKey(this.index);
        }
        
        @Override
        public V getValue() {
            return ArrayMap.this.getValue(this.index);
        }
        
        @Override
        public V setValue(final V value) {
            return ArrayMap.this.set(this.index, value);
        }
        
        @Override
        public int hashCode() {
            final K key = this.getKey();
            final V value = this.getValue();
            return ((key != null) ? key.hashCode() : 0) ^ ((value != null) ? value.hashCode() : 0);
        }
        
        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof Map.Entry)) {
                return false;
            }
            final Map.Entry<?, ?> other = (Map.Entry<?, ?>)obj;
            return Objects.equal(this.getKey(), other.getKey()) && Objects.equal(this.getValue(), other.getValue());
        }
    }
}
