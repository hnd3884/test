package javax.jms;

public interface QueueSender extends MessageProducer
{
    Queue getQueue() throws JMSException;
    
    void send(final Message p0) throws JMSException;
    
    void send(final Message p0, final int p1, final int p2, final long p3) throws JMSException;
    
    void send(final Queue p0, final Message p1) throws JMSException;
    
    void send(final Queue p0, final Message p1, final int p2, final int p3, final long p4) throws JMSException;
}
