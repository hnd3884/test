package javax.jms;

public interface QueueSession extends Session
{
    Queue createQueue(final String p0) throws JMSException;
    
    QueueReceiver createReceiver(final Queue p0) throws JMSException;
    
    QueueReceiver createReceiver(final Queue p0, final String p1) throws JMSException;
    
    QueueSender createSender(final Queue p0) throws JMSException;
    
    QueueBrowser createBrowser(final Queue p0) throws JMSException;
    
    QueueBrowser createBrowser(final Queue p0, final String p1) throws JMSException;
    
    TemporaryQueue createTemporaryQueue() throws JMSException;
}
