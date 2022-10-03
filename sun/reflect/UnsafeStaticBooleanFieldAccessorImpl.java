package sun.reflect;

import java.lang.reflect.Field;

class UnsafeStaticBooleanFieldAccessorImpl extends UnsafeStaticFieldAccessorImpl
{
    UnsafeStaticBooleanFieldAccessorImpl(final Field field) {
        super(field);
    }
    
    @Override
    public Object get(final Object o) throws IllegalArgumentException {
        return new Boolean(this.getBoolean(o));
    }
    
    @Override
    public boolean getBoolean(final Object o) throws IllegalArgumentException {
        return UnsafeStaticBooleanFieldAccessorImpl.unsafe.getBoolean(this.base, this.fieldOffset);
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
        throw this.newGetDoubleIllegalArgumentException();
    }
    
    @Override
    public void set(final Object o, final Object o2) throws IllegalArgumentException, IllegalAccessException {
        if (this.isFinal) {
            this.throwFinalFieldIllegalAccessException(o2);
        }
        if (o2 == null) {
            this.throwSetIllegalArgumentException(o2);
        }
        if (o2 instanceof Boolean) {
            UnsafeStaticBooleanFieldAccessorImpl.unsafe.putBoolean(this.base, this.fieldOffset, (boolean)o2);
            return;
        }
        this.throwSetIllegalArgumentException(o2);
    }
    
    @Override
    public void setBoolean(final Object o, final boolean b) throws IllegalArgumentException, IllegalAccessException {
        if (this.isFinal) {
            this.throwFinalFieldIllegalAccessException(b);
        }
        UnsafeStaticBooleanFieldAccessorImpl.unsafe.putBoolean(this.base, this.fieldOffset, b);
    }
    
    @Override
    public void setByte(final Object o, final byte b) throws IllegalArgumentException, IllegalAccessException {
        this.throwSetIllegalArgumentException(b);
    }
    
    @Override
    public void setChar(final Object o, final char c) throws IllegalArgumentException, IllegalAccessException {
        this.throwSetIllegalArgumentException(c);
    }
    
    @Override
    public void setShort(final Object o, final short n) throws IllegalArgumentException, IllegalAccessException {
        this.throwSetIllegalArgumentException(n);
    }
    
    @Override
    public void setInt(final Object o, final int n) throws IllegalArgumentException, IllegalAccessException {
        this.throwSetIllegalArgumentException(n);
    }
    
    @Override
    public void setLong(final Object o, final long n) throws IllegalArgumentException, IllegalAccessException {
        this.throwSetIllegalArgumentException(n);
    }
    
    @Override
    public void setFloat(final Object o, final float n) throws IllegalArgumentException, IllegalAccessException {
        this.throwSetIllegalArgumentException(n);
    }
    
    @Override
    public void setDouble(final Object o, final double n) throws IllegalArgumentException, IllegalAccessException {
        this.throwSetIllegalArgumentException(n);
    }
}
