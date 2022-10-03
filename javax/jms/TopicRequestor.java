package javax.jms;

public class TopicRequestor
{
    private TopicSession topicSession;
    private Topic topic;
    private TopicPublisher requestPublisher;
    private TemporaryTopic responseTopic;
    private TopicSubscriber responseSubscriber;
    
    public TopicRequestor(final TopicSession session, final Topic topic) throws JMSException {
        this.topicSession = null;
        this.topic = null;
        this.requestPublisher = null;
        this.responseTopic = null;
        this.responseSubscriber = null;
        this.topicSession = session;
        this.topic = topic;
        this.requestPublisher = this.topicSession.createPublisher(topic);
        this.responseTopic = this.topicSession.createTemporaryTopic();
        this.responseSubscriber = this.topicSession.createSubscriber(this.responseTopic);
    }
    
    public Message request(final Message message) throws JMSException {
        message.setJMSReplyTo(this.responseTopic);
        this.requestPublisher.publish(message);
        return this.responseSubscriber.receive();
    }
    
    public void close() throws JMSException {
        this.responseSubscriber.close();
        this.responseTopic.delete();
        this.topicSession.close();
    }
}
