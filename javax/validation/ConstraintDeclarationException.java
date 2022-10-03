package javax.validation;

public class ConstraintDeclarationException extends ValidationException
{
    public ConstraintDeclarationException(final String message) {
        super(message);
    }
    
    public ConstraintDeclarationException() {
    }
    
    public ConstraintDeclarationException(final String message, final Throwable cause) {
        super(message, cause);
    }
    
    public ConstraintDeclarationException(final Throwable cause) {
        super(cause);
    }
}
