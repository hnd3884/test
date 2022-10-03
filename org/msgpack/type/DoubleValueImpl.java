package org.msgpack.type;

import java.io.IOException;
import org.msgpack.packer.Packer;
import java.math.BigDecimal;
import java.math.BigInteger;

class DoubleValueImpl extends FloatValue
{
    private double value;
    
    DoubleValueImpl(final double value) {
        this.value = value;
    }
    
    @Override
    public float getFloat() {
        return (float)this.value;
    }
    
    @Override
    public double getDouble() {
        return this.value;
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
        return (long)this.value;
    }
    
    @Override
    public BigInteger bigIntegerValue() {
        return new BigDecimal(this.value).toBigInteger();
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
        return v.isFloatValue() && this.value == v.asFloatValue().getDouble();
    }
    
    @Override
    public int hashCode() {
        final long v = Double.doubleToLongBits(this.value);
        return (int)(v ^ v >>> 32);
    }
    
    @Override
    public String toString() {
        return Double.toString(this.value);
    }
    
    @Override
    public StringBuilder toString(final StringBuilder sb) {
        return sb.append(Double.toString(this.value));
    }
}
