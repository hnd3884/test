package javax.jms;

public interface TopicPublisher extends MessageProducer
{
    Topic getTopic() throws JMSException;
    
    void publish(final Message p0) throws JMSException;
    
    void publish(final Message p0, final int p1, final int p2, final long p3) throws JMSException;
    
    void publish(final Topic p0, final Message p1) throws JMSException;
    
    void publish(final Topic p0, final Message p1, final int p2, final int p3, final long p4) throws JMSException;
}
