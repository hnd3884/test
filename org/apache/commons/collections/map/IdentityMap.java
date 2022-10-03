package org.apache.commons.collections.map;

import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Map;
import java.io.Serializable;

public class IdentityMap extends AbstractHashedMap implements Serializable, Cloneable
{
    private static final long serialVersionUID = 2028493495224302329L;
    
    public IdentityMap() {
        super(16, 0.75f, 12);
    }
    
    public IdentityMap(final int initialCapacity) {
        super(initialCapacity);
    }
    
    public IdentityMap(final int initialCapacity, final float loadFactor) {
        super(initialCapacity, loadFactor);
    }
    
    public IdentityMap(final Map map) {
        super(map);
    }
    
    protected int hash(final Object key) {
        return System.identityHashCode(key);
    }
    
    protected boolean isEqualKey(final Object key1, final Object key2) {
        return key1 == key2;
    }
    
    protected boolean isEqualValue(final Object value1, final Object value2) {
        return value1 == value2;
    }
    
    protected HashEntry createEntry(final HashEntry next, final int hashCode, final Object key, final Object value) {
        return new IdentityEntry(next, hashCode, key, value);
    }
    
    public Object clone() {
        return super.clone();
    }
    
    private void writeObject(final ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        this.doWriteObject(out);
    }
    
    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.doReadObject(in);
    }
    
    protected static class IdentityEntry extends HashEntry
    {
        protected IdentityEntry(final HashEntry next, final int hashCode, final Object key, final Object value) {
            super(next, hashCode, key, value);
        }
        
        public boolean equals(final Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof Map.Entry)) {
                return false;
            }
            final Map.Entry other = (Map.Entry)obj;
            return this.getKey() == other.getKey() && this.getValue() == other.getValue();
        }
        
        public int hashCode() {
            return System.identityHashCode(this.getKey()) ^ System.identityHashCode(this.getValue());
        }
    }
}
