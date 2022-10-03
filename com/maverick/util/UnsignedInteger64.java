package com.maverick.util;

import java.math.BigInteger;

public class UnsignedInteger64
{
    public static final BigInteger MAX_VALUE;
    public static final BigInteger MIN_VALUE;
    private BigInteger b;
    
    public UnsignedInteger64(final String s) throws NumberFormatException {
        this.b = new BigInteger(s);
        if (this.b.compareTo(UnsignedInteger64.MIN_VALUE) < 0 || this.b.compareTo(UnsignedInteger64.MAX_VALUE) > 0) {
            throw new NumberFormatException();
        }
    }
    
    public UnsignedInteger64(final byte[] array) throws NumberFormatException {
        this.b = new BigInteger(array);
        if (this.b.compareTo(UnsignedInteger64.MIN_VALUE) < 0 || this.b.compareTo(UnsignedInteger64.MAX_VALUE) > 0) {
            throw new NumberFormatException();
        }
    }
    
    public UnsignedInteger64(final long n) {
        this.b = BigInteger.valueOf(n);
    }
    
    public UnsignedInteger64(final BigInteger bigInteger) {
        this.b = new BigInteger(bigInteger.toString());
        if (this.b.compareTo(UnsignedInteger64.MIN_VALUE) < 0 || this.b.compareTo(UnsignedInteger64.MAX_VALUE) > 0) {
            throw new NumberFormatException();
        }
    }
    
    public boolean equals(final Object o) {
        if (o == null) {
            return false;
        }
        try {
            return ((UnsignedInteger64)o).b.equals(this.b);
        }
        catch (final ClassCastException ex) {
            return false;
        }
    }
    
    public BigInteger bigIntValue() {
        return this.b;
    }
    
    public long longValue() {
        return this.b.longValue();
    }
    
    public String toString() {
        return this.b.toString(10);
    }
    
    public int hashCode() {
        return this.b.hashCode();
    }
    
    public static UnsignedInteger64 add(final UnsignedInteger64 unsignedInteger64, final UnsignedInteger64 unsignedInteger65) {
        return new UnsignedInteger64(unsignedInteger64.b.add(unsignedInteger65.b));
    }
    
    public static UnsignedInteger64 add(final UnsignedInteger64 unsignedInteger64, final int n) {
        return new UnsignedInteger64(unsignedInteger64.b.add(BigInteger.valueOf(n)));
    }
    
    public byte[] toByteArray() {
        final byte[] array = new byte[8];
        final byte[] byteArray = this.bigIntValue().toByteArray();
        System.arraycopy(byteArray, 0, array, array.length - byteArray.length, byteArray.length);
        return array;
    }
    
    static {
        MAX_VALUE = new BigInteger("18446744073709551615");
        MIN_VALUE = new BigInteger("0");
    }
}
