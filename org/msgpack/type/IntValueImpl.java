package org.msgpack.type;

import java.io.IOException;
import org.msgpack.packer.Packer;
import java.math.BigInteger;
import org.msgpack.MessageTypeException;

class IntValueImpl extends IntegerValue
{
    private int value;
    private static int BYTE_MAX;
    private static int SHORT_MAX;
    private static int BYTE_MIN;
    private static int SHORT_MIN;
    
    IntValueImpl(final int value) {
        this.value = value;
    }
    
    @Override
    public byte getByte() {
        if (this.value > IntValueImpl.BYTE_MAX || this.value < IntValueImpl.BYTE_MIN) {
            throw new MessageTypeException();
        }
        return (byte)this.value;
    }
    
    @Override
    public short getShort() {
        if (this.value > IntValueImpl.SHORT_MAX || this.value < IntValueImpl.SHORT_MIN) {
            throw new MessageTypeException();
        }
        return (short)this.value;
    }
    
    @Override
    public int getInt() {
        return this.value;
    }
    
    @Override
    public long getLong() {
        return this.value;
    }
    
    @Override
    public BigInteger getBigInteger() {
        return BigInteger.valueOf(this.value);
    }
    
    @Override
    public byte byteValue() {
        return (byte)this.value;
    }
    
    @Override
    public short shortValue() {
        return (short)this.value;
    }
    
    @Override
    public int intValue() {
        return this.value;
    }
    
    @Override
    public long longValue() {
        return this.value;
    }
    
    @Override
    public BigInteger bigIntegerValue() {
        return BigInteger.valueOf(this.value);
    }
    
    @Override
    public float floatValue() {
        return (float)this.value;
    }
    
    @Override
    public double doubleValue() {
        return this.value;
    }
    
    @Override
    public void writeTo(final Packer pk) throws IOException {
        pk.write(this.value);
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
        if (!v.isIntegerValue()) {
            return false;
        }
        try {
            return this.value == v.asIntegerValue().getInt();
        }
        catch (final MessageTypeException ex) {
            return false;
        }
    }
    
    @Override
    public int hashCode() {
        return this.value;
    }
    
    @Override
    public String toString() {
        return Integer.toString(this.value);
    }
    
    @Override
    public StringBuilder toString(final StringBuilder sb) {
        return sb.append(Integer.toString(this.value));
    }
    
    static {
        IntValueImpl.BYTE_MAX = 127;
        IntValueImpl.SHORT_MAX = 32767;
        IntValueImpl.BYTE_MIN = -128;
        IntValueImpl.SHORT_MIN = -32768;
    }
}
