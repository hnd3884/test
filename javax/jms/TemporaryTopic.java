package javax.jms;

public interface TemporaryTopic extends Topic
{
    void delete() throws JMSException;
}
