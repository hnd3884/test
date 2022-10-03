package javax.jms;

public interface QueueReceiver extends MessageConsumer
{
    Queue getQueue() throws JMSException;
}
