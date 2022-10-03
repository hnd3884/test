package javax.jms;

public interface MessageConsumer
{
    String getMessageSelector() throws JMSException;
    
    MessageListener getMessageListener() throws JMSException;
    
    void setMessageListener(final MessageListener p0) throws JMSException;
    
    Message receive() throws JMSException;
    
    Message receive(final long p0) throws JMSException;
    
    Message receiveNoWait() throws JMSException;
    
    void close() throws JMSException;
}
