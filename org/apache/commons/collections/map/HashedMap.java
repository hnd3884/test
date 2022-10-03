package org.apache.commons.collections.map;

import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Map;
import java.io.Serializable;

public class HashedMap extends AbstractHashedMap implements Serializable, Cloneable
{
    private static final long serialVersionUID = -1788199231038721040L;
    
    public HashedMap() {
        super(16, 0.75f, 12);
    }
    
    public HashedMap(final int initialCapacity) {
        super(initialCapacity);
    }
    
    public HashedMap(final int initialCapacity, final float loadFactor) {
        super(initialCapacity, loadFactor);
    }
    
    public HashedMap(final Map map) {
        super(map);
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
}
