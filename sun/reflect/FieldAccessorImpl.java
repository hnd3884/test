package sun.reflect;

abstract class FieldAccessorImpl extends MagicAccessorImpl implements FieldAccessor
{
    @Override
    public abstract Object get(final Object p0) throws IllegalArgumentException;
    
    @Override
    public abstract boolean getBoolean(final Object p0) throws IllegalArgumentException;
    
    @Override
    public abstract byte getByte(final Object p0) throws IllegalArgumentException;
    
    @Override
    public abstract char getChar(final Object p0) throws IllegalArgumentException;
    
    @Override
    public abstract short getShort(final Object p0) throws IllegalArgumentException;
    
    @Override
    public abstract int getInt(final Object p0) throws IllegalArgumentException;
    
    @Override
    public abstract long getLong(final Object p0) throws IllegalArgumentException;
    
    @Override
    public abstract float getFloat(final Object p0) throws IllegalArgumentException;
    
    @Override
    public abstract double getDouble(final Object p0) throws IllegalArgumentException;
    
    @Override
    public abstract void set(final Object p0, final Object p1) throws IllegalArgumentException, IllegalAccessException;
    
    @Override
    public abstract void setBoolean(final Object p0, final boolean p1) throws IllegalArgumentException, IllegalAccessException;
    
    @Override
    public abstract void setByte(final Object p0, final byte p1) throws IllegalArgumentException, IllegalAccessException;
    
    @Override
    public abstract void setChar(final Object p0, final char p1) throws IllegalArgumentException, IllegalAccessException;
    
    @Override
    public abstract void setShort(final Object p0, final short p1) throws IllegalArgumentException, IllegalAccessException;
    
    @Override
    public abstract void setInt(final Object p0, final int p1) throws IllegalArgumentException, IllegalAccessException;
    
    @Override
    public abstract void setLong(final Object p0, final long p1) throws IllegalArgumentException, IllegalAccessException;
    
    @Override
    public abstract void setFloat(final Object p0, final float p1) throws IllegalArgumentException, IllegalAccessException;
    
    @Override
    public abstract void setDouble(final Object p0, final double p1) throws IllegalArgumentException, IllegalAccessException;
}
