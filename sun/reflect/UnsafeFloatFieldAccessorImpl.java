package sun.reflect;

import java.lang.reflect.Field;

class UnsafeFloatFieldAccessorImpl extends UnsafeFieldAccessorImpl
{
    UnsafeFloatFieldAccessorImpl(final Field field) {
        super(field);
    }
    
    @Override
    public Object get(final Object o) throws IllegalArgumentException {
        return new Float(this.getFloat(o));
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
        this.ensureObj(o);
        return UnsafeFloatFieldAccessorImpl.unsafe.getFloat(o, this.fieldOffset);
    }
    
    @Override
    public double getDouble(final Object o) throws IllegalArgumentException {
        return this.getFloat(o);
    }
    
    @Override
    public void set(final Object o, final Object o2) throws IllegalArgumentException, IllegalAccessException {
        this.ensureObj(o);
        if (this.isFinal) {
            this.throwFinalFieldIllegalAccessException(o2);
        }
        if (o2 == null) {
            this.throwSetIllegalArgumentException(o2);
        }
        if (o2 instanceof Byte) {
            UnsafeFloatFieldAccessorImpl.unsafe.putFloat(o, this.fieldOffset, (byte)o2);
            return;
        }
        if (o2 instanceof Short) {
            UnsafeFloatFieldAccessorImpl.unsafe.putFloat(o, this.fieldOffset, (short)o2);
            return;
        }
        if (o2 instanceof Character) {
            UnsafeFloatFieldAccessorImpl.unsafe.putFloat(o, this.fieldOffset, (char)o2);
            return;
        }
        if (o2 instanceof Integer) {
            UnsafeFloatFieldAccessorImpl.unsafe.putFloat(o, this.fieldOffset, (float)(int)o2);
            return;
        }
        if (o2 instanceof Long) {
            UnsafeFloatFieldAccessorImpl.unsafe.putFloat(o, this.fieldOffset, (float)(long)o2);
            return;
        }
        if (o2 instanceof Float) {
            UnsafeFloatFieldAccessorImpl.unsafe.putFloat(o, this.fieldOffset, (float)o2);
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
        this.setFloat(o, b);
    }
    
    @Override
    public void setChar(final Object o, final char c) throws IllegalArgumentException, IllegalAccessException {
        this.setFloat(o, c);
    }
    
    @Override
    public void setShort(final Object o, final short n) throws IllegalArgumentException, IllegalAccessException {
        this.setFloat(o, n);
    }
    
    @Override
    public void setInt(final Object o, final int n) throws IllegalArgumentException, IllegalAccessException {
        this.setFloat(o, (float)n);
    }
    
    @Override
    public void setLong(final Object o, final long n) throws IllegalArgumentException, IllegalAccessException {
        this.setFloat(o, (float)n);
    }
    
    @Override
    public void setFloat(final Object o, final float n) throws IllegalArgumentException, IllegalAccessException {
        this.ensureObj(o);
        if (this.isFinal) {
            this.throwFinalFieldIllegalAccessException(n);
        }
        UnsafeFloatFieldAccessorImpl.unsafe.putFloat(o, this.fieldOffset, n);
    }
    
    @Override
    public void setDouble(final Object o, final double n) throws IllegalArgumentException, IllegalAccessException {
        this.throwSetIllegalArgumentException(n);
    }
}
