package javax.jms;

public class MessageNotWriteableException extends JMSException
{
    public MessageNotWriteableException(final String reason, final String errorCode) {
        super(reason, errorCode);
    }
    
    public MessageNotWriteableException(final String reason) {
        super(reason, (String)null);
    }
}
