package org.apache.commons.collections.keyvalue;

import java.util.Arrays;
import java.io.Serializable;

public class MultiKey implements Serializable
{
    private static final long serialVersionUID = 4465448607415788805L;
    private final Object[] keys;
    private transient int hashCode;
    
    public MultiKey(final Object key1, final Object key2) {
        this(new Object[] { key1, key2 }, false);
    }
    
    public MultiKey(final Object key1, final Object key2, final Object key3) {
        this(new Object[] { key1, key2, key3 }, false);
    }
    
    public MultiKey(final Object key1, final Object key2, final Object key3, final Object key4) {
        this(new Object[] { key1, key2, key3, key4 }, false);
    }
    
    public MultiKey(final Object key1, final Object key2, final Object key3, final Object key4, final Object key5) {
        this(new Object[] { key1, key2, key3, key4, key5 }, false);
    }
    
    public MultiKey(final Object[] keys) {
        this(keys, true);
    }
    
    public MultiKey(final Object[] keys, final boolean makeClone) {
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
    
    public Object[] getKeys() {
        return this.keys.clone();
    }
    
    public Object getKey(final int index) {
        return this.keys[index];
    }
    
    public int size() {
        return this.keys.length;
    }
    
    public boolean equals(final Object other) {
        if (other == this) {
            return true;
        }
        if (other instanceof MultiKey) {
            final MultiKey otherMulti = (MultiKey)other;
            return Arrays.equals(this.keys, otherMulti.keys);
        }
        return false;
    }
    
    public int hashCode() {
        return this.hashCode;
    }
    
    public String toString() {
        return "MultiKey" + Arrays.asList(this.keys).toString();
    }
    
    private void calculateHashCode(final Object[] keys) {
        int total = 0;
        for (int i = 0; i < keys.length; ++i) {
            if (keys[i] != null) {
                total ^= keys[i].hashCode();
            }
        }
        this.hashCode = total;
    }
    
    private Object readResolve() {
        this.calculateHashCode(this.keys);
        return this;
    }
}
