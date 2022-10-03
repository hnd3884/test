package org.eclipse.jdt.internal.compiler.impl;

public class ByteConstant extends Constant
{
    private byte value;
    
    public static Constant fromValue(final byte value) {
        return new ByteConstant(value);
    }
    
    private ByteConstant(final byte value) {
        this.value = value;
    }
    
    @Override
    public byte byteValue() {
        return this.value;
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
        return this.value;
    }
    
    @Override
    public long longValue() {
        return this.value;
    }
    
    @Override
    public short shortValue() {
        return this.value;
    }
    
    @Override
    public String stringValue() {
        return String.valueOf(this.value);
    }
    
    @Override
    public String toString() {
        return "(byte)" + this.value;
    }
    
    @Override
    public int typeID() {
        return 3;
    }
    
    @Override
    public int hashCode() {
        return this.value;
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
        final ByteConstant other = (ByteConstant)obj;
        return this.value == other.value;
    }
}
