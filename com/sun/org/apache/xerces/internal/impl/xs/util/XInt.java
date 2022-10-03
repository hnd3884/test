package com.sun.org.apache.xerces.internal.impl.xs.util;

public final class XInt
{
    private int fValue;
    
    XInt(final int value) {
        this.fValue = value;
    }
    
    public final int intValue() {
        return this.fValue;
    }
    
    public final short shortValue() {
        return (short)this.fValue;
    }
    
    public final boolean equals(final XInt compareVal) {
        return this.fValue == compareVal.fValue;
    }
    
    @Override
    public String toString() {
        return Integer.toString(this.fValue);
    }
}
