package javax.jms;

public interface TopicSubscriber extends MessageConsumer
{
    Topic getTopic() throws JMSException;
    
    boolean getNoLocal() throws JMSException;
}
