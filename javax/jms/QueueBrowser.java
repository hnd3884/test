package javax.jms;

import java.util.Enumeration;

public interface QueueBrowser
{
    Queue getQueue() throws JMSException;
    
    String getMessageSelector() throws JMSException;
    
    Enumeration getEnumeration() throws JMSException;
    
    void close() throws JMSException;
}
