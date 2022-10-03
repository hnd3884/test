package javax.jms;

import java.util.Enumeration;

public interface Message
{
    public static final int DEFAULT_DELIVERY_MODE = 2;
    public static final int DEFAULT_PRIORITY = 4;
    public static final long DEFAULT_TIME_TO_LIVE = 0L;
    
    String getJMSMessageID() throws JMSException;
    
    void setJMSMessageID(final String p0) throws JMSException;
    
    long getJMSTimestamp() throws JMSException;
    
    void setJMSTimestamp(final long p0) throws JMSException;
    
    byte[] getJMSCorrelationIDAsBytes() throws JMSException;
    
    void setJMSCorrelationIDAsBytes(final byte[] p0) throws JMSException;
    
    void setJMSCorrelationID(final String p0) throws JMSException;
    
    String getJMSCorrelationID() throws JMSException;
    
    Destination getJMSReplyTo() throws JMSException;
    
    void setJMSReplyTo(final Destination p0) throws JMSException;
    
    Destination getJMSDestination() throws JMSException;
    
    void setJMSDestination(final Destination p0) throws JMSException;
    
    int getJMSDeliveryMode() throws JMSException;
    
    void setJMSDeliveryMode(final int p0) throws JMSException;
    
    boolean getJMSRedelivered() throws JMSException;
    
    void setJMSRedelivered(final boolean p0) throws JMSException;
    
    String getJMSType() throws JMSException;
    
    void setJMSType(final String p0) throws JMSException;
    
    long getJMSExpiration() throws JMSException;
    
    void setJMSExpiration(final long p0) throws JMSException;
    
    int getJMSPriority() throws JMSException;
    
    void setJMSPriority(final int p0) throws JMSException;
    
    void clearProperties() throws JMSException;
    
    boolean propertyExists(final String p0) throws JMSException;
    
    boolean getBooleanProperty(final String p0) throws JMSException;
    
    byte getByteProperty(final String p0) throws JMSException;
    
    short getShortProperty(final String p0) throws JMSException;
    
    int getIntProperty(final String p0) throws JMSException;
    
    long getLongProperty(final String p0) throws JMSException;
    
    float getFloatProperty(final String p0) throws JMSException;
    
    double getDoubleProperty(final String p0) throws JMSException;
    
    String getStringProperty(final String p0) throws JMSException;
    
    Object getObjectProperty(final String p0) throws JMSException;
    
    Enumeration getPropertyNames() throws JMSException;
    
    void setBooleanProperty(final String p0, final boolean p1) throws JMSException;
    
    void setByteProperty(final String p0, final byte p1) throws JMSException;
    
    void setShortProperty(final String p0, final short p1) throws JMSException;
    
    void setIntProperty(final String p0, final int p1) throws JMSException;
    
    void setLongProperty(final String p0, final long p1) throws JMSException;
    
    void setFloatProperty(final String p0, final float p1) throws JMSException;
    
    void setDoubleProperty(final String p0, final double p1) throws JMSException;
    
    void setStringProperty(final String p0, final String p1) throws JMSException;
    
    void setObjectProperty(final String p0, final Object p1) throws JMSException;
    
    void acknowledge() throws JMSException;
    
    void clearBody() throws JMSException;
}
