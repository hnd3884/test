package javax.jms;

public class MessageNotReadableException extends JMSException
{
    public MessageNotReadableException(final String reason, final String errorCode) {
        super(reason, errorCode);
    }
    
    public MessageNotReadableException(final String reason) {
        super(reason, (String)null);
    }
}
