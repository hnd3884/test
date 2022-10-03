package sun.reflect;

import java.lang.reflect.Field;

class UnsafeCharacterFieldAccessorImpl extends UnsafeFieldAccessorImpl
{
    UnsafeCharacterFieldAccessorImpl(final Field field) {
        super(field);
    }
    
    @Override
    public Object get(final Object o) throws IllegalArgumentException {
        return new Character(this.getChar(o));
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
        this.ensureObj(o);
        return UnsafeCharacterFieldAccessorImpl.unsafe.getChar(o, this.fieldOffset);
    }
    
    @Override
    public short getShort(final Object o) throws IllegalArgumentException {
        throw this.newGetShortIllegalArgumentException();
    }
    
    @Override
    public int getInt(final Object o) throws IllegalArgumentException {
        return this.getChar(o);
    }
    
    @Override
    public long getLong(final Object o) throws IllegalArgumentException {
        return this.getChar(o);
    }
    
    @Override
    public float getFloat(final Object o) throws IllegalArgumentException {
        return this.getChar(o);
    }
    
    @Override
    public double getDouble(final Object o) throws IllegalArgumentException {
        return this.getChar(o);
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
        if (o2 instanceof Character) {
            UnsafeCharacterFieldAccessorImpl.unsafe.putChar(o, this.fieldOffset, (char)o2);
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
        this.throwSetIllegalArgumentException(b);
    }
    
    @Override
    public void setChar(final Object o, final char c) throws IllegalArgumentException, IllegalAccessException {
        this.ensureObj(o);
        if (this.isFinal) {
            this.throwFinalFieldIllegalAccessException(c);
        }
        UnsafeCharacterFieldAccessorImpl.unsafe.putChar(o, this.fieldOffset, c);
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
