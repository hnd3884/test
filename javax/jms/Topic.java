package javax.jms;

public interface Topic extends Destination
{
    String getTopicName() throws JMSException;
}
