package javax.jms;

public class MessageFormatException extends JMSException
{
    public MessageFormatException(final String reason, final String errorCode) {
        super(reason, errorCode);
    }
    
    public MessageFormatException(final String reason) {
        super(reason, (String)null);
    }
}
