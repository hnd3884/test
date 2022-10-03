package javax.jms;

public interface QueueConnection extends Connection
{
    QueueSession createQueueSession(final boolean p0, final int p1) throws JMSException;
    
    ConnectionConsumer createConnectionConsumer(final Queue p0, final String p1, final ServerSessionPool p2, final int p3) throws JMSException;
}
