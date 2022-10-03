package org.eclipse.jdt.internal.compiler.impl;

public class CharConstant extends Constant
{
    private char value;
    
    public static Constant fromValue(final char value) {
        return new CharConstant(value);
    }
    
    private CharConstant(final char value) {
        this.value = value;
    }
    
    @Override
    public byte byteValue() {
        return (byte)this.value;
    }
    
    @Override
    public char charValue() {
        return this.value;
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
        return (short)this.value;
    }
    
    @Override
    public String stringValue() {
        return String.valueOf(this.value);
    }
    
    @Override
    public String toString() {
        return "(char)" + this.value;
    }
    
    @Override
    public int typeID() {
        return 2;
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
        final CharConstant other = (CharConstant)obj;
        return this.value == other.value;
    }
}
