package javax.jms;

public interface ServerSession
{
    Session getSession() throws JMSException;
    
    void start() throws JMSException;
}
