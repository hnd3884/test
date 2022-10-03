package javax.jms;

public interface TopicConnection extends Connection
{
    TopicSession createTopicSession(final boolean p0, final int p1) throws JMSException;
    
    ConnectionConsumer createConnectionConsumer(final Topic p0, final String p1, final ServerSessionPool p2, final int p3) throws JMSException;
    
    ConnectionConsumer createDurableConnectionConsumer(final Topic p0, final String p1, final String p2, final ServerSessionPool p3, final int p4) throws JMSException;
}
