package javax.jms;

public class QueueRequestor
{
    private QueueSession queueSession;
    private Queue queue;
    private QueueSender requestSender;
    private QueueReceiver replyReceiver;
    private TemporaryQueue replyQueue;
    
    public QueueRequestor(final QueueSession session, final Queue queue) throws JMSException {
        this.queueSession = null;
        this.queue = null;
        this.requestSender = null;
        this.replyReceiver = null;
        this.replyQueue = null;
        this.queueSession = session;
        this.queue = queue;
        this.requestSender = this.queueSession.createSender(queue);
        this.replyQueue = this.queueSession.createTemporaryQueue();
        this.replyReceiver = this.queueSession.createReceiver(this.replyQueue);
    }
    
    public Message request(final Message message) throws JMSException {
        message.setJMSReplyTo(this.replyQueue);
        message.setJMSDeliveryMode(1);
        this.requestSender.send(message);
        return this.replyReceiver.receive();
    }
    
    public void close() throws JMSException {
        this.replyReceiver.close();
        this.replyQueue.delete();
        this.queueSession.close();
    }
}
