package com.maverick.util;

public class UnsignedInteger32
{
    public static final long MAX_VALUE = 4294967295L;
    public static final long MIN_VALUE = 0L;
    private Long b;
    
    public UnsignedInteger32(final long n) {
        if (n < 0L || n > 4294967295L) {
            throw new NumberFormatException();
        }
        this.b = new Long(n);
    }
    
    public UnsignedInteger32(final String s) throws NumberFormatException {
        final long long1 = Long.parseLong(s);
        if (long1 < 0L || long1 > 4294967295L) {
            throw new NumberFormatException();
        }
        this.b = new Long(long1);
    }
    
    public int intValue() {
        return (int)(long)this.b;
    }
    
    public long longValue() {
        return this.b;
    }
    
    public String toString() {
        return this.b.toString();
    }
    
    public int hashCode() {
        return this.b.hashCode();
    }
    
    public boolean equals(final Object o) {
        return o instanceof UnsignedInteger32 && ((UnsignedInteger32)o).b.equals(this.b);
    }
    
    public static UnsignedInteger32 add(final UnsignedInteger32 unsignedInteger32, final UnsignedInteger32 unsignedInteger33) {
        return new UnsignedInteger32(unsignedInteger32.longValue() + unsignedInteger33.longValue());
    }
    
    public static UnsignedInteger32 add(final UnsignedInteger32 unsignedInteger32, final int n) {
        return new UnsignedInteger32(unsignedInteger32.longValue() + n);
    }
}
