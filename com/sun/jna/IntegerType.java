package com.sun.jna;

public abstract class IntegerType extends Number implements NativeMapped
{
    private int size;
    private long value;
    private Number number;
    
    public IntegerType(final int size) {
        this(size, 0L);
    }
    
    public IntegerType(final int size, final long value) {
        this.size = size;
        this.setValue(value);
    }
    
    public void setValue(final long value) {
        long truncated = value;
        this.value = value;
        switch (this.size) {
            case 1: {
                truncated = (byte)value;
                this.number = new Byte((byte)value);
                break;
            }
            case 2: {
                truncated = (short)value;
                this.number = new Short((short)value);
                break;
            }
            case 4: {
                truncated = (int)value;
                this.number = new Integer((int)value);
                break;
            }
            case 8: {
                this.number = new Long(value);
                break;
            }
            default: {
                throw new IllegalArgumentException("Unsupported size: " + this.size);
            }
        }
        if (this.size < 8) {
            final long mask = ~((1L << this.size * 8) - 1L);
            if ((value < 0L && truncated != value) || (value >= 0L && (mask & value) != 0x0L)) {
                throw new IllegalArgumentException("Argument value 0x" + Long.toHexString(value) + " exceeds native capacity (" + this.size + " bytes) mask=0x" + Long.toHexString(mask));
            }
        }
    }
    
    public Object toNative() {
        return this.number;
    }
    
    public Object fromNative(final Object nativeValue, final FromNativeContext context) {
        final long value = (nativeValue == null) ? 0L : ((Number)nativeValue).longValue();
        try {
            final IntegerType number = (IntegerType)this.getClass().newInstance();
            number.setValue(value);
            return number;
        }
        catch (final InstantiationException e) {
            throw new IllegalArgumentException("Can't instantiate " + this.getClass());
        }
        catch (final IllegalAccessException e2) {
            throw new IllegalArgumentException("Not allowed to instantiate " + this.getClass());
        }
    }
    
    public Class nativeType() {
        return this.number.getClass();
    }
    
    public int intValue() {
        return this.number.intValue();
    }
    
    public long longValue() {
        return this.number.longValue();
    }
    
    public float floatValue() {
        return this.number.floatValue();
    }
    
    public double doubleValue() {
        return this.number.doubleValue();
    }
    
    public boolean equals(final Object rhs) {
        return rhs instanceof IntegerType && this.number.equals(((IntegerType)rhs).number);
    }
    
    public String toString() {
        return this.number.toString();
    }
    
    public int hashCode() {
        return this.number.hashCode();
    }
}
