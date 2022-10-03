package org.apache.commons.collections4.map;

import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class ReferenceMap<K, V> extends AbstractReferenceMap<K, V> implements Serializable
{
    private static final long serialVersionUID = 1555089888138299607L;
    
    public ReferenceMap() {
        super(ReferenceStrength.HARD, ReferenceStrength.SOFT, 16, 0.75f, false);
    }
    
    public ReferenceMap(final ReferenceStrength keyType, final ReferenceStrength valueType) {
        super(keyType, valueType, 16, 0.75f, false);
    }
    
    public ReferenceMap(final ReferenceStrength keyType, final ReferenceStrength valueType, final boolean purgeValues) {
        super(keyType, valueType, 16, 0.75f, purgeValues);
    }
    
    public ReferenceMap(final ReferenceStrength keyType, final ReferenceStrength valueType, final int capacity, final float loadFactor) {
        super(keyType, valueType, capacity, loadFactor, false);
    }
    
    public ReferenceMap(final ReferenceStrength keyType, final ReferenceStrength valueType, final int capacity, final float loadFactor, final boolean purgeValues) {
        super(keyType, valueType, capacity, loadFactor, purgeValues);
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
