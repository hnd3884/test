package javax.jms;

public interface TemporaryQueue extends Queue
{
    void delete() throws JMSException;
}
