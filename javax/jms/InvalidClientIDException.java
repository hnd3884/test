package javax.jms;

public class InvalidClientIDException extends JMSException
{
    public InvalidClientIDException(final String reason, final String errorCode) {
        super(reason, errorCode);
    }
    
    public InvalidClientIDException(final String reason) {
        super(reason, (String)null);
    }
}
