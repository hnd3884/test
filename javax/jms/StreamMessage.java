package javax.jms;

public interface StreamMessage extends Message
{
    boolean readBoolean() throws JMSException;
    
    byte readByte() throws JMSException;
    
    short readShort() throws JMSException;
    
    char readChar() throws JMSException;
    
    int readInt() throws JMSException;
    
    long readLong() throws JMSException;
    
    float readFloat() throws JMSException;
    
    double readDouble() throws JMSException;
    
    String readString() throws JMSException;
    
    int readBytes(final byte[] p0) throws JMSException;
    
    Object readObject() throws JMSException;
    
    void writeBoolean(final boolean p0) throws JMSException;
    
    void writeByte(final byte p0) throws JMSException;
    
    void writeShort(final short p0) throws JMSException;
    
    void writeChar(final char p0) throws JMSException;
    
    void writeInt(final int p0) throws JMSException;
    
    void writeLong(final long p0) throws JMSException;
    
    void writeFloat(final float p0) throws JMSException;
    
    void writeDouble(final double p0) throws JMSException;
    
    void writeString(final String p0) throws JMSException;
    
    void writeBytes(final byte[] p0) throws JMSException;
    
    void writeBytes(final byte[] p0, final int p1, final int p2) throws JMSException;
    
    void writeObject(final Object p0) throws JMSException;
    
    void reset() throws JMSException;
}
