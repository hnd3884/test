package org.apache.commons.collections4.map;

import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.lang.ref.Reference;
import java.io.Serializable;

public class ReferenceIdentityMap<K, V> extends AbstractReferenceMap<K, V> implements Serializable
{
    private static final long serialVersionUID = -1266190134568365852L;
    
    public ReferenceIdentityMap() {
        super(ReferenceStrength.HARD, ReferenceStrength.SOFT, 16, 0.75f, false);
    }
    
    public ReferenceIdentityMap(final ReferenceStrength keyType, final ReferenceStrength valueType) {
        super(keyType, valueType, 16, 0.75f, false);
    }
    
    public ReferenceIdentityMap(final ReferenceStrength keyType, final ReferenceStrength valueType, final boolean purgeValues) {
        super(keyType, valueType, 16, 0.75f, purgeValues);
    }
    
    public ReferenceIdentityMap(final ReferenceStrength keyType, final ReferenceStrength valueType, final int capacity, final float loadFactor) {
        super(keyType, valueType, capacity, loadFactor, false);
    }
    
    public ReferenceIdentityMap(final ReferenceStrength keyType, final ReferenceStrength valueType, final int capacity, final float loadFactor, final boolean purgeValues) {
        super(keyType, valueType, capacity, loadFactor, purgeValues);
    }
    
    @Override
    protected int hash(final Object key) {
        return System.identityHashCode(key);
    }
    
    @Override
    protected int hashEntry(final Object key, final Object value) {
        return System.identityHashCode(key) ^ System.identityHashCode(value);
    }
    
    @Override
    protected boolean isEqualKey(final Object key1, Object key2) {
        key2 = (this.isKeyType(ReferenceStrength.HARD) ? key2 : ((Reference)key2).get());
        return key1 == key2;
    }
    
    @Override
    protected boolean isEqualValue(final Object value1, final Object value2) {
        return value1 == value2;
    }
    
    private void writeObject(final ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        this.doWriteObject(out);
    }
    
    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.doReadObject(in);
    }
}
