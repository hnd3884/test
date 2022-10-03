package javax.resource;

public class NotSupportedException extends ResourceException
{
    public NotSupportedException(final String reason) {
        super(reason);
    }
    
    public NotSupportedException(final String reason, final String errorCode) {
        super(reason, errorCode);
    }
}
