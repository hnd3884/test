package javax.jms;

public class MessageEOFException extends JMSException
{
    public MessageEOFException(final String reason, final String errorCode) {
        super(reason, errorCode);
    }
    
    public MessageEOFException(final String reason) {
        super(reason, (String)null);
    }
}
