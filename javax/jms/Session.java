package javax.jms;

import java.io.Serializable;

public interface Session extends Runnable
{
    public static final int AUTO_ACKNOWLEDGE = 1;
    public static final int CLIENT_ACKNOWLEDGE = 2;
    public static final int DUPS_OK_ACKNOWLEDGE = 3;
    
    BytesMessage createBytesMessage() throws JMSException;
    
    MapMessage createMapMessage() throws JMSException;
    
    Message createMessage() throws JMSException;
    
    ObjectMessage createObjectMessage() throws JMSException;
    
    ObjectMessage createObjectMessage(final Serializable p0) throws JMSException;
    
    StreamMessage createStreamMessage() throws JMSException;
    
    TextMessage createTextMessage() throws JMSException;
    
    TextMessage createTextMessage(final String p0) throws JMSException;
    
    boolean getTransacted() throws JMSException;
    
    void commit() throws JMSException;
    
    void rollback() throws JMSException;
    
    void close() throws JMSException;
    
    void recover() throws JMSException;
    
    MessageListener getMessageListener() throws JMSException;
    
    void setMessageListener(final MessageListener p0) throws JMSException;
}
