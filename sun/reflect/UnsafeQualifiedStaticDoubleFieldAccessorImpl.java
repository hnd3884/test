package sun.reflect;

import java.lang.reflect.Field;

class UnsafeQualifiedStaticDoubleFieldAccessorImpl extends UnsafeQualifiedStaticFieldAccessorImpl
{
    UnsafeQualifiedStaticDoubleFieldAccessorImpl(final Field field, final boolean b) {
        super(field, b);
    }
    
    @Override
    public Object get(final Object o) throws IllegalArgumentException {
        return new Double(this.getDouble(o));
    }
    
    @Override
    public boolean getBoolean(final Object o) throws IllegalArgumentException {
        throw this.newGetBooleanIllegalArgumentException();
    }
    
    @Override
    public byte getByte(final Object o) throws IllegalArgumentException {
        throw this.newGetByteIllegalArgumentException();
    }
    
    @Override
    public char getChar(final Object o) throws IllegalArgumentException {
        throw this.newGetCharIllegalArgumentException();
    }
    
    @Override
    public short getShort(final Object o) throws IllegalArgumentException {
        throw this.newGetShortIllegalArgumentException();
    }
    
    @Override
    public int getInt(final Object o) throws IllegalArgumentException {
        throw this.newGetIntIllegalArgumentException();
    }
    
    @Override
    public long getLong(final Object o) throws IllegalArgumentException {
        throw this.newGetLongIllegalArgumentException();
    }
    
    @Override
    public float getFloat(final Object o) throws IllegalArgumentException {
        throw this.newGetFloatIllegalArgumentException();
    }
    
    @Override
    public double getDouble(final Object o) throws IllegalArgumentException {
        return UnsafeQualifiedStaticDoubleFieldAccessorImpl.unsafe.getDoubleVolatile(this.base, this.fieldOffset);
    }
    
    @Override
    public void set(final Object o, final Object o2) throws IllegalArgumentException, IllegalAccessException {
        if (this.isReadOnly) {
            this.throwFinalFieldIllegalAccessException(o2);
        }
        if (o2 == null) {
            this.throwSetIllegalArgumentException(o2);
        }
        if (o2 instanceof Byte) {
            UnsafeQualifiedStaticDoubleFieldAccessorImpl.unsafe.putDoubleVolatile(this.base, this.fieldOffset, (byte)o2);
            return;
        }
        if (o2 instanceof Short) {
            UnsafeQualifiedStaticDoubleFieldAccessorImpl.unsafe.putDoubleVolatile(this.base, this.fieldOffset, (short)o2);
            return;
        }
        if (o2 instanceof Character) {
            UnsafeQualifiedStaticDoubleFieldAccessorImpl.unsafe.putDoubleVolatile(this.base, this.fieldOffset, (char)o2);
            return;
        }
        if (o2 instanceof Integer) {
            UnsafeQualifiedStaticDoubleFieldAccessorImpl.unsafe.putDoubleVolatile(this.base, this.fieldOffset, (int)o2);
            return;
        }
        if (o2 instanceof Long) {
            UnsafeQualifiedStaticDoubleFieldAccessorImpl.unsafe.putDoubleVolatile(this.base, this.fieldOffset, (double)(long)o2);
            return;
        }
        if (o2 instanceof Float) {
            UnsafeQualifiedStaticDoubleFieldAccessorImpl.unsafe.putDoubleVolatile(this.base, this.fieldOffset, (float)o2);
            return;
        }
        if (o2 instanceof Double) {
            UnsafeQualifiedStaticDoubleFieldAccessorImpl.unsafe.putDoubleVolatile(this.base, this.fieldOffset, (double)o2);
            return;
        }
        this.throwSetIllegalArgumentException(o2);
    }
    
    @Override
    public void setBoolean(final Object o, final boolean b) throws IllegalArgumentException, IllegalAccessException {
        this.throwSetIllegalArgumentException(b);
    }
    
    @Override
    public void setByte(final Object o, final byte b) throws IllegalArgumentException, IllegalAccessException {
        this.setDouble(o, b);
    }
    
    @Override
    public void setChar(final Object o, final char c) throws IllegalArgumentException, IllegalAccessException {
        this.setDouble(o, c);
    }
    
    @Override
    public void setShort(final Object o, final short n) throws IllegalArgumentException, IllegalAccessException {
        this.setDouble(o, n);
    }
    
    @Override
    public void setInt(final Object o, final int n) throws IllegalArgumentException, IllegalAccessException {
        this.setDouble(o, n);
    }
    
    @Override
    public void setLong(final Object o, final long n) throws IllegalArgumentException, IllegalAccessException {
        this.setDouble(o, (double)n);
    }
    
    @Override
    public void setFloat(final Object o, final float n) throws IllegalArgumentException, IllegalAccessException {
        this.setDouble(o, n);
    }
    
    @Override
    public void setDouble(final Object o, final double n) throws IllegalArgumentException, IllegalAccessException {
        if (this.isReadOnly) {
            this.throwFinalFieldIllegalAccessException(n);
        }
        UnsafeQualifiedStaticDoubleFieldAccessorImpl.unsafe.putDoubleVolatile(this.base, this.fieldOffset, n);
    }
}
