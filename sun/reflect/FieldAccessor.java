package sun.reflect;

public interface FieldAccessor
{
    Object get(final Object p0) throws IllegalArgumentException;
    
    boolean getBoolean(final Object p0) throws IllegalArgumentException;
    
    byte getByte(final Object p0) throws IllegalArgumentException;
    
    char getChar(final Object p0) throws IllegalArgumentException;
    
    short getShort(final Object p0) throws IllegalArgumentException;
    
    int getInt(final Object p0) throws IllegalArgumentException;
    
    long getLong(final Object p0) throws IllegalArgumentException;
    
    float getFloat(final Object p0) throws IllegalArgumentException;
    
    double getDouble(final Object p0) throws IllegalArgumentException;
    
    void set(final Object p0, final Object p1) throws IllegalArgumentException, IllegalAccessException;
    
    void setBoolean(final Object p0, final boolean p1) throws IllegalArgumentException, IllegalAccessException;
    
    void setByte(final Object p0, final byte p1) throws IllegalArgumentException, IllegalAccessException;
    
    void setChar(final Object p0, final char p1) throws IllegalArgumentException, IllegalAccessException;
    
    void setShort(final Object p0, final short p1) throws IllegalArgumentException, IllegalAccessException;
    
    void setInt(final Object p0, final int p1) throws IllegalArgumentException, IllegalAccessException;
    
    void setLong(final Object p0, final long p1) throws IllegalArgumentException, IllegalAccessException;
    
    void setFloat(final Object p0, final float p1) throws IllegalArgumentException, IllegalAccessException;
    
    void setDouble(final Object p0, final double p1) throws IllegalArgumentException, IllegalAccessException;
}
