package javax.jms;

public class InvalidSelectorException extends JMSException
{
    public InvalidSelectorException(final String reason, final String errorCode) {
        super(reason, errorCode);
    }
    
    public InvalidSelectorException(final String reason) {
        super(reason, (String)null);
    }
}
