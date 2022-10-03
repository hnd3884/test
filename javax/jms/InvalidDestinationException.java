package javax.jms;

public class InvalidDestinationException extends JMSException
{
    public InvalidDestinationException(final String reason, final String errorCode) {
        super(reason, errorCode);
    }
    
    public InvalidDestinationException(final String reason) {
        super(reason, (String)null);
    }
}
