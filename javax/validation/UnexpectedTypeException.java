package javax.validation;

public class UnexpectedTypeException extends ConstraintDeclarationException
{
    public UnexpectedTypeException(final String message) {
        super(message);
    }
    
    public UnexpectedTypeException() {
    }
    
    public UnexpectedTypeException(final String message, final Throwable cause) {
        super(message, cause);
    }
    
    public UnexpectedTypeException(final Throwable cause) {
        super(cause);
    }
}
