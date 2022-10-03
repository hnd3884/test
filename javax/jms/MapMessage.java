package javax.jms;

import java.util.Enumeration;

public interface MapMessage extends Message
{
    boolean getBoolean(final String p0) throws JMSException;
    
    byte getByte(final String p0) throws JMSException;
    
    short getShort(final String p0) throws JMSException;
    
    char getChar(final String p0) throws JMSException;
    
    int getInt(final String p0) throws JMSException;
    
    long getLong(final String p0) throws JMSException;
    
    float getFloat(final String p0) throws JMSException;
    
    double getDouble(final String p0) throws JMSException;
    
    String getString(final String p0) throws JMSException;
    
    byte[] getBytes(final String p0) throws JMSException;
    
    Object getObject(final String p0) throws JMSException;
    
    Enumeration getMapNames() throws JMSException;
    
    void setBoolean(final String p0, final boolean p1) throws JMSException;
    
    void setByte(final String p0, final byte p1) throws JMSException;
    
    void setShort(final String p0, final short p1) throws JMSException;
    
    void setChar(final String p0, final char p1) throws JMSException;
    
    void setInt(final String p0, final int p1) throws JMSException;
    
    void setLong(final String p0, final long p1) throws JMSException;
    
    void setFloat(final String p0, final float p1) throws JMSException;
    
    void setDouble(final String p0, final double p1) throws JMSException;
    
    void setString(final String p0, final String p1) throws JMSException;
    
    void setBytes(final String p0, final byte[] p1) throws JMSException;
    
    void setBytes(final String p0, final byte[] p1, final int p2, final int p3) throws JMSException;
    
    void setObject(final String p0, final Object p1) throws JMSException;
    
    boolean itemExists(final String p0) throws JMSException;
}
