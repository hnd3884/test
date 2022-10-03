package javax.validation;

public class GroupDefinitionException extends ValidationException
{
    public GroupDefinitionException(final String message) {
        super(message);
    }
    
    public GroupDefinitionException() {
    }
    
    public GroupDefinitionException(final String message, final Throwable cause) {
        super(message, cause);
    }
    
    public GroupDefinitionException(final Throwable cause) {
        super(cause);
    }
}
