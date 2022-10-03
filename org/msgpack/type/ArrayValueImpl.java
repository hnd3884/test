package org.msgpack.type;

import java.util.ListIterator;
import java.io.IOException;
import org.msgpack.packer.Packer;

class ArrayValueImpl extends AbstractArrayValue
{
    private static ArrayValueImpl emptyInstance;
    private Value[] array;
    
    public static ArrayValue getEmptyInstance() {
        return ArrayValueImpl.emptyInstance;
    }
    
    @Override
    public Value[] getElementArray() {
        return this.array;
    }
    
    ArrayValueImpl(final Value[] array, final boolean gift) {
        if (gift) {
            this.array = array;
        }
        else {
            System.arraycopy(array, 0, this.array = new Value[array.length], 0, array.length);
        }
    }
    
    @Override
    public int size() {
        return this.array.length;
    }
    
    @Override
    public boolean isEmpty() {
        return this.array.length == 0;
    }
    
    @Override
    public Value get(final int index) {
        if (index < 0 || this.array.length <= index) {
            throw new IndexOutOfBoundsException();
        }
        return this.array[index];
    }
    
    @Override
    public int indexOf(final Object o) {
        if (o == null) {
            return -1;
        }
        for (int i = 0; i < this.array.length; ++i) {
            if (this.array[i].equals(o)) {
                return i;
            }
        }
        return -1;
    }
    
    @Override
    public int lastIndexOf(final Object o) {
        if (o == null) {
            return -1;
        }
        for (int i = this.array.length - 1; i >= 0; --i) {
            if (this.array[i].equals(o)) {
                return i;
            }
        }
        return -1;
    }
    
    @Override
    public void writeTo(final Packer pk) throws IOException {
        pk.writeArrayBegin(this.array.length);
        for (int i = 0; i < this.array.length; ++i) {
            this.array[i].writeTo(pk);
        }
        pk.writeArrayEnd();
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Value)) {
            return false;
        }
        final Value v = (Value)o;
        if (!v.isArrayValue()) {
            return false;
        }
        if (v.getClass() == ArrayValueImpl.class) {
            return this.equals((ArrayValueImpl)v);
        }
        final ListIterator<Value> oi = v.asArrayValue().listIterator();
        final int i = 0;
        while (i < this.array.length) {
            if (!oi.hasNext() || !this.array[i].equals(oi.next())) {
                return false;
            }
        }
        return !oi.hasNext();
    }
    
    private boolean equals(final ArrayValueImpl o) {
        if (this.array.length != o.array.length) {
            return false;
        }
        for (int i = 0; i < this.array.length; ++i) {
            if (!this.array[i].equals(o.array[i])) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        int h = 1;
        for (int i = 0; i < this.array.length; ++i) {
            final Value obj = this.array[i];
            h = 31 * h + obj.hashCode();
        }
        return h;
    }
    
    @Override
    public String toString() {
        return this.toString(new StringBuilder()).toString();
    }
    
    @Override
    public StringBuilder toString(final StringBuilder sb) {
        if (this.array.length == 0) {
            return sb.append("[]");
        }
        sb.append("[");
        sb.append(this.array[0]);
        for (int i = 1; i < this.array.length; ++i) {
            sb.append(",");
            this.array[i].toString(sb);
        }
        sb.append("]");
        return sb;
    }
    
    static {
        ArrayValueImpl.emptyInstance = new ArrayValueImpl(new Value[0], true);
    }
}
