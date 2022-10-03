package org.apache.commons.collections4.keyvalue;

import java.util.Arrays;
import java.io.Serializable;

public class MultiKey<K> implements Serializable
{
    private static final long serialVersionUID = 4465448607415788805L;
    private final K[] keys;
    private transient int hashCode;
    
    public MultiKey(final K key1, final K key2) {
        this(new Object[] { key1, key2 }, false);
    }
    
    public MultiKey(final K key1, final K key2, final K key3) {
        this(new Object[] { key1, key2, key3 }, false);
    }
    
    public MultiKey(final K key1, final K key2, final K key3, final K key4) {
        this(new Object[] { key1, key2, key3, key4 }, false);
    }
    
    public MultiKey(final K key1, final K key2, final K key3, final K key4, final K key5) {
        this(new Object[] { key1, key2, key3, key4, key5 }, false);
    }
    
    public MultiKey(final K[] keys) {
        this(keys, true);
    }
    
    public MultiKey(final K[] keys, final boolean makeClone) {
        if (keys == null) {
            throw new IllegalArgumentException("The array of keys must not be null");
        }
        if (makeClone) {
            this.keys = keys.clone();
        }
        else {
            this.keys = keys;
        }
        this.calculateHashCode(keys);
    }
    
    public K[] getKeys() {
        return this.keys.clone();
    }
    
    public K getKey(final int index) {
        return this.keys[index];
    }
    
    public int size() {
        return this.keys.length;
    }
    
    @Override
    public boolean equals(final Object other) {
        if (other == this) {
            return true;
        }
        if (other instanceof MultiKey) {
            final MultiKey<?> otherMulti = (MultiKey<?>)other;
            return Arrays.equals(this.keys, (Object[])otherMulti.keys);
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return this.hashCode;
    }
    
    @Override
    public String toString() {
        return "MultiKey" + Arrays.toString(this.keys);
    }
    
    private void calculateHashCode(final Object[] keys) {
        int total = 0;
        for (final Object key : keys) {
            if (key != null) {
                total ^= key.hashCode();
            }
        }
        this.hashCode = total;
    }
    
    protected Object readResolve() {
        this.calculateHashCode(this.keys);
        return this;
    }
}
