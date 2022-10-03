package org.apache.commons.collections;

import java.util.Iterator;
import java.io.ObjectOutput;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.Externalizable;

public class LRUMap extends SequencedHashMap implements Externalizable
{
    private int maximumSize;
    private static final long serialVersionUID = 2197433140769957051L;
    
    public LRUMap() {
        this(100);
    }
    
    public LRUMap(final int i) {
        super(i);
        this.maximumSize = 0;
        this.maximumSize = i;
    }
    
    public Object get(final Object key) {
        if (!this.containsKey(key)) {
            return null;
        }
        final Object value = this.remove(key);
        super.put(key, value);
        return value;
    }
    
    public Object put(final Object key, final Object value) {
        final int mapSize = this.size();
        Object retval = null;
        if (mapSize >= this.maximumSize && !this.containsKey(key)) {
            this.removeLRU();
        }
        retval = super.put(key, value);
        return retval;
    }
    
    protected void removeLRU() {
        final Object key = this.getFirstKey();
        final Object value = super.get(key);
        this.remove(key);
        this.processRemovedLRU(key, value);
    }
    
    protected void processRemovedLRU(final Object key, final Object value) {
    }
    
    public void readExternal(final ObjectInput in) throws IOException, ClassNotFoundException {
        this.maximumSize = in.readInt();
        for (int size = in.readInt(), i = 0; i < size; ++i) {
            final Object key = in.readObject();
            final Object value = in.readObject();
            this.put(key, value);
        }
    }
    
    public void writeExternal(final ObjectOutput out) throws IOException {
        out.writeInt(this.maximumSize);
        out.writeInt(this.size());
        final Iterator iterator = this.keySet().iterator();
        while (iterator.hasNext()) {
            final Object key = iterator.next();
            out.writeObject(key);
            final Object value = super.get(key);
            out.writeObject(value);
        }
    }
    
    public int getMaximumSize() {
        return this.maximumSize;
    }
    
    public void setMaximumSize(final int maximumSize) {
        this.maximumSize = maximumSize;
        while (this.size() > maximumSize) {
            this.removeLRU();
        }
    }
}
