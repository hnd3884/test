package javax.jms;

public interface ConnectionConsumer
{
    ServerSessionPool getServerSessionPool() throws JMSException;
    
    void close() throws JMSException;
}
