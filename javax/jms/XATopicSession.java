package javax.jms;

public interface XATopicSession extends XASession
{
    TopicSession getTopicSession() throws JMSException;
}
