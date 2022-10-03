package org.msgpack.type;

import java.io.IOException;
import org.msgpack.packer.Packer;
import java.math.BigInteger;
import org.msgpack.MessageTypeException;

class LongValueImpl extends IntegerValue
{
    private long value;
    private static long BYTE_MAX;
    private static long SHORT_MAX;
    private static long INT_MAX;
    private static long BYTE_MIN;
    private static long SHORT_MIN;
    private static long INT_MIN;
    
    LongValueImpl(final long value) {
        this.value = value;
    }
    
    @Override
    public byte getByte() {
        if (this.value > LongValueImpl.BYTE_MAX || this.value < LongValueImpl.BYTE_MIN) {
            throw new MessageTypeException();
        }
        return (byte)this.value;
    }
    
    @Override
    public short getShort() {
        if (this.value > LongValueImpl.SHORT_MAX || this.value < LongValueImpl.SHORT_MIN) {
            throw new MessageTypeException();
        }
        return (short)this.value;
    }
    
    @Override
    public int getInt() {
        if (this.value > LongValueImpl.INT_MAX || this.value < LongValueImpl.INT_MIN) {
            throw new MessageTypeException();
        }
        return (int)this.value;
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
        return (int)this.value;
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
        return (double)this.value;
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
            return this.value == v.asIntegerValue().getLong();
        }
        catch (final MessageTypeException ex) {
            return false;
        }
    }
    
    @Override
    public int hashCode() {
        if (LongValueImpl.INT_MIN <= this.value && this.value <= LongValueImpl.INT_MAX) {
            return (int)this.value;
        }
        return (int)(this.value ^ this.value >>> 32);
    }
    
    @Override
    public String toString() {
        return Long.toString(this.value);
    }
    
    @Override
    public StringBuilder toString(final StringBuilder sb) {
        return sb.append(Long.toString(this.value));
    }
    
    static {
        LongValueImpl.BYTE_MAX = 127L;
        LongValueImpl.SHORT_MAX = 32767L;
        LongValueImpl.INT_MAX = 2147483647L;
        LongValueImpl.BYTE_MIN = -128L;
        LongValueImpl.SHORT_MIN = -32768L;
        LongValueImpl.INT_MIN = -2147483648L;
    }
}
