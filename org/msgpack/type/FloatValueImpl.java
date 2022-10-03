package org.msgpack.type;

import java.io.IOException;
import org.msgpack.packer.Packer;
import java.math.BigDecimal;
import java.math.BigInteger;

class FloatValueImpl extends FloatValue
{
    private float value;
    
    FloatValueImpl(final float value) {
        this.value = value;
    }
    
    @Override
    public float getFloat() {
        return this.value;
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
        return this.value;
    }
    
    @Override
    public double doubleValue() {
        return this.value;
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
    public void writeTo(final Packer pk) throws IOException {
        pk.write(this.value);
    }
    
    @Override
    public int hashCode() {
        return Float.floatToIntBits(this.value);
    }
    
    @Override
    public String toString() {
        return Float.toString(this.value);
    }
    
    @Override
    public StringBuilder toString(final StringBuilder sb) {
        return sb.append(Float.toString(this.value));
    }
}
