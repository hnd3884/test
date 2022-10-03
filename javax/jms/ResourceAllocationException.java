package javax.jms;

public class ResourceAllocationException extends JMSException
{
    public ResourceAllocationException(final String reason, final String errorCode) {
        super(reason, errorCode);
    }
    
    public ResourceAllocationException(final String reason) {
        super(reason, (String)null);
    }
}
