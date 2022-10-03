package javax.jms;

public interface XATopicConnection extends XAConnection, TopicConnection
{
    XATopicSession createXATopicSession() throws JMSException;
}
