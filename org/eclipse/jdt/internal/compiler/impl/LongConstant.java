package org.eclipse.jdt.internal.compiler.impl;

public class LongConstant extends Constant
{
    private static final LongConstant ZERO;
    private static final LongConstant MIN_VALUE;
    private long value;
    
    static {
        ZERO = new LongConstant(0L);
        MIN_VALUE = new LongConstant(Long.MIN_VALUE);
    }
    
    public static Constant fromValue(final long value) {
        if (value == 0L) {
            return LongConstant.ZERO;
        }
        if (value == Long.MIN_VALUE) {
            return LongConstant.MIN_VALUE;
        }
        return new LongConstant(value);
    }
    
    private LongConstant(final long value) {
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
        return (double)this.value;
    }
    
    @Override
    public float floatValue() {
        return (float)this.value;
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
    public short shortValue() {
        return (short)this.value;
    }
    
    @Override
    public String stringValue() {
        return String.valueOf(this.value);
    }
    
    @Override
    public String toString() {
        return "(long)" + this.value;
    }
    
    @Override
    public int typeID() {
        return 7;
    }
    
    @Override
    public int hashCode() {
        return (int)(this.value ^ this.value >>> 32);
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
        final LongConstant other = (LongConstant)obj;
        return this.value == other.value;
    }
}
