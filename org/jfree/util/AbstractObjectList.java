package org.jfree.util;

import java.io.ObjectOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Arrays;
import java.io.Serializable;

public class AbstractObjectList implements Cloneable, Serializable
{
    private static final long serialVersionUID = 7789833772597351595L;
    public static final int DEFAULT_INITIAL_CAPACITY = 8;
    private transient Object[] objects;
    private int size;
    private int increment;
    
    protected AbstractObjectList() {
        this(8);
    }
    
    protected AbstractObjectList(final int initialCapacity) {
        this(initialCapacity, initialCapacity);
    }
    
    protected AbstractObjectList(final int initialCapacity, final int increment) {
        this.size = 0;
        this.increment = 8;
        this.objects = new Object[initialCapacity];
        this.increment = increment;
    }
    
    public void clear() {
        Arrays.fill(this.objects, null);
        this.size = 0;
    }
    
    public Object clone() throws CloneNotSupportedException {
        final AbstractObjectList clone = (AbstractObjectList)super.clone();
        if (this.objects != null) {
            clone.objects = new Object[this.objects.length];
            System.arraycopy(this.objects, 0, clone.objects, 0, this.objects.length);
        }
        return clone;
    }
    
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof AbstractObjectList)) {
            return false;
        }
        final AbstractObjectList other = (AbstractObjectList)obj;
        for (int listSize = this.size(), i = 0; i < listSize; ++i) {
            if (!ObjectUtilities.equal(this.get(i), other.get(i))) {
                return false;
            }
        }
        return true;
    }
    
    protected Object get(final int index) {
        Object result = null;
        if (index >= 0 && index < this.size) {
            result = this.objects[index];
        }
        return result;
    }
    
    public int hashCode() {
        return super.hashCode();
    }
    
    protected int indexOf(final Object object) {
        for (int index = 0; index < this.size; ++index) {
            if (this.objects[index] == object) {
                return index;
            }
        }
        return -1;
    }
    
    private void readObject(final ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.objects = new Object[this.size];
        for (int count = stream.readInt(), i = 0; i < count; ++i) {
            final int index = stream.readInt();
            if (index != -1) {
                this.set(index, stream.readObject());
            }
        }
    }
    
    protected void set(final int index, final Object object) {
        if (index < 0) {
            throw new IllegalArgumentException("Requires index >= 0.");
        }
        if (index >= this.objects.length) {
            final Object[] enlarged = new Object[index + this.increment];
            System.arraycopy(this.objects, 0, enlarged, 0, this.objects.length);
            this.objects = enlarged;
        }
        this.objects[index] = object;
        this.size = Math.max(this.size, index + 1);
    }
    
    public int size() {
        return this.size;
    }
    
    private void writeObject(final ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        final int count = this.size();
        stream.writeInt(count);
        for (int i = 0; i < count; ++i) {
            final Object object = this.get(i);
            if (object != null && object instanceof Serializable) {
                stream.writeInt(i);
                stream.writeObject(object);
            }
            else {
                stream.writeInt(-1);
            }
        }
    }
}
