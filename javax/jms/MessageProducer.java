package javax.jms;

public interface MessageProducer
{
    void setDisableMessageID(final boolean p0) throws JMSException;
    
    boolean getDisableMessageID() throws JMSException;
    
    void setDisableMessageTimestamp(final boolean p0) throws JMSException;
    
    boolean getDisableMessageTimestamp() throws JMSException;
    
    void setDeliveryMode(final int p0) throws JMSException;
    
    int getDeliveryMode() throws JMSException;
    
    void setPriority(final int p0) throws JMSException;
    
    int getPriority() throws JMSException;
    
    void setTimeToLive(final long p0) throws JMSException;
    
    long getTimeToLive() throws JMSException;
    
    void close() throws JMSException;
}
