package javax.jms;

public interface ServerSessionPool
{
    ServerSession getServerSession() throws JMSException;
}
