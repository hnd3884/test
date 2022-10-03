package javax.jms;

public interface TopicSession extends Session
{
    Topic createTopic(final String p0) throws JMSException;
    
    TopicSubscriber createSubscriber(final Topic p0) throws JMSException;
    
    TopicSubscriber createSubscriber(final Topic p0, final String p1, final boolean p2) throws JMSException;
    
    TopicSubscriber createDurableSubscriber(final Topic p0, final String p1) throws JMSException;
    
    TopicSubscriber createDurableSubscriber(final Topic p0, final String p1, final String p2, final boolean p3) throws JMSException;
    
    TopicPublisher createPublisher(final Topic p0) throws JMSException;
    
    TemporaryTopic createTemporaryTopic() throws JMSException;
    
    void unsubscribe(final String p0) throws JMSException;
}
