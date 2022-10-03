package javax.validation;

public class ValidationException extends RuntimeException
{
    public ValidationException(final String message) {
        super(message);
    }
    
    public ValidationException() {
    }
    
    public ValidationException(final String message, final Throwable cause) {
        super(message, cause);
    }
    
    public ValidationException(final Throwable cause) {
        super(cause);
    }
}
