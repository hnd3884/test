package org.eclipse.jdt.internal.compiler.impl;

public class FloatConstant extends Constant
{
    float value;
    
    public static Constant fromValue(final float value) {
        return new FloatConstant(value);
    }
    
    private FloatConstant(final float value) {
        this.value = value;
    }
    
    @Override
    public byte byteValue() {
        return (byte)this.value;
    }
    
    @Override
    public char charValue() {
        return (char)this.value;
    }
    
    @Override
    public double doubleValue() {
        return this.value;
    }
    
    @Override
    public float floatValue() {
        return this.value;
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
    public short shortValue() {
        return (short)this.value;
    }
    
    @Override
    public String stringValue() {
        return String.valueOf(this.value);
    }
    
    @Override
    public String toString() {
        return "(float)" + this.value;
    }
    
    @Override
    public int typeID() {
        return 9;
    }
    
    @Override
    public int hashCode() {
        return Float.floatToIntBits(this.value);
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        final FloatConstant other = (FloatConstant)obj;
        return Float.floatToIntBits(this.value) == Float.floatToIntBits(other.value);
    }
}
