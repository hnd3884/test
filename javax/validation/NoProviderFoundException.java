package javax.validation;

public class NoProviderFoundException extends ValidationException
{
    public NoProviderFoundException() {
    }
    
    public NoProviderFoundException(final String message) {
        super(message);
    }
    
    public NoProviderFoundException(final Throwable cause) {
        super(cause);
    }
    
    public NoProviderFoundException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
