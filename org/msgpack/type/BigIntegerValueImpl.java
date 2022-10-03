package org.msgpack.type;

import java.io.IOException;
import org.msgpack.packer.Packer;
import org.msgpack.MessageTypeException;
import java.math.BigInteger;

class BigIntegerValueImpl extends IntegerValue
{
    private BigInteger value;
    private static BigInteger BYTE_MAX;
    private static BigInteger SHORT_MAX;
    private static BigInteger INT_MAX;
    private static BigInteger LONG_MAX;
    private static BigInteger BYTE_MIN;
    private static BigInteger SHORT_MIN;
    private static BigInteger INT_MIN;
    private static BigInteger LONG_MIN;
    
    BigIntegerValueImpl(final BigInteger value) {
        this.value = value;
    }
    
    @Override
    public byte getByte() {
        if (this.value.compareTo(BigIntegerValueImpl.BYTE_MAX) > 0 || this.value.compareTo(BigIntegerValueImpl.BYTE_MIN) < 0) {
            throw new MessageTypeException();
        }
        return this.value.byteValue();
    }
    
    @Override
    public short getShort() {
        if (this.value.compareTo(BigIntegerValueImpl.SHORT_MAX) > 0 || this.value.compareTo(BigIntegerValueImpl.SHORT_MIN) < 0) {
            throw new MessageTypeException();
        }
        return this.value.shortValue();
    }
    
    @Override
    public int getInt() {
        if (this.value.compareTo(BigIntegerValueImpl.INT_MAX) > 0 || this.value.compareTo(BigIntegerValueImpl.INT_MIN) < 0) {
            throw new MessageTypeException();
        }
        return this.value.intValue();
    }
    
    @Override
    public long getLong() {
        if (this.value.compareTo(BigIntegerValueImpl.LONG_MAX) > 0 || this.value.compareTo(BigIntegerValueImpl.LONG_MIN) < 0) {
            throw new MessageTypeException();
        }
        return this.value.longValue();
    }
    
    @Override
    public BigInteger getBigInteger() {
        return this.value;
    }
    
    @Override
    public byte byteValue() {
        return this.value.byteValue();
    }
    
    @Override
    public short shortValue() {
        return this.value.shortValue();
    }
    
    @Override
    public int intValue() {
        return this.value.intValue();
    }
    
    @Override
    public long longValue() {
        return this.value.longValue();
    }
    
    @Override
    public BigInteger bigIntegerValue() {
        return this.value;
    }
    
    @Override
    public float floatValue() {
        return this.value.floatValue();
    }
    
    @Override
    public double doubleValue() {
        return this.value.doubleValue();
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
        return v.isIntegerValue() && this.value.equals(v.asIntegerValue().bigIntegerValue());
    }
    
    @Override
    public int hashCode() {
        if (BigIntegerValueImpl.INT_MIN.compareTo(this.value) <= 0 && this.value.compareTo(BigIntegerValueImpl.INT_MAX) <= 0) {
            return (int)this.value.longValue();
        }
        if (BigIntegerValueImpl.LONG_MIN.compareTo(this.value) <= 0 && this.value.compareTo(BigIntegerValueImpl.LONG_MAX) <= 0) {
            final long v = this.value.longValue();
            return (int)(v ^ v >>> 32);
        }
        return this.value.hashCode();
    }
    
    @Override
    public String toString() {
        return this.value.toString();
    }
    
    @Override
    public StringBuilder toString(final StringBuilder sb) {
        return sb.append(this.value.toString());
    }
    
    static {
        BigIntegerValueImpl.BYTE_MAX = BigInteger.valueOf(127L);
        BigIntegerValueImpl.SHORT_MAX = BigInteger.valueOf(32767L);
        BigIntegerValueImpl.INT_MAX = BigInteger.valueOf(2147483647L);
        BigIntegerValueImpl.LONG_MAX = BigInteger.valueOf(Long.MAX_VALUE);
        BigIntegerValueImpl.BYTE_MIN = BigInteger.valueOf(-128L);
        BigIntegerValueImpl.SHORT_MIN = BigInteger.valueOf(-32768L);
        BigIntegerValueImpl.INT_MIN = BigInteger.valueOf(-2147483648L);
        BigIntegerValueImpl.LONG_MIN = BigInteger.valueOf(Long.MIN_VALUE);
    }
}
