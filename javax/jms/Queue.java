package javax.jms;

public interface Queue extends Destination
{
    String getQueueName() throws JMSException;
}
