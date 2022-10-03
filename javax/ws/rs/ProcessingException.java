package javax.ws.rs;

public class ProcessingException extends RuntimeException
{
    private static final long serialVersionUID = -4232431597816056514L;
    
    public ProcessingException(final Throwable cause) {
        super(cause);
    }
    
    public ProcessingException(final String message, final Throwable cause) {
        super(message, cause);
    }
    
    public ProcessingException(final String message) {
        super(message);
    }
}
