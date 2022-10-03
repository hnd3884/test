package javax.validation;

public class ConstraintDefinitionException extends ValidationException
{
    public ConstraintDefinitionException(final String message) {
        super(message);
    }
    
    public ConstraintDefinitionException() {
    }
    
    public ConstraintDefinitionException(final String message, final Throwable cause) {
        super(message, cause);
    }
    
    public ConstraintDefinitionException(final Throwable cause) {
        super(cause);
    }
}
