package javax.jms;

public interface TopicConnectionFactory extends ConnectionFactory
{
    TopicConnection createTopicConnection() throws JMSException;
    
    TopicConnection createTopicConnection(final String p0, final String p1) throws JMSException;
}
