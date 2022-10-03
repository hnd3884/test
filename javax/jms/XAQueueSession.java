package javax.jms;

public interface XAQueueSession extends XASession
{
    QueueSession getQueueSession() throws JMSException;
}
