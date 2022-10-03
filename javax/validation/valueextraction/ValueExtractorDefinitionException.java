package javax.validation.valueextraction;

import javax.validation.ValidationException;

public class ValueExtractorDefinitionException extends ValidationException
{
    public ValueExtractorDefinitionException() {
    }
    
    public ValueExtractorDefinitionException(final String message) {
        super(message);
    }
    
    public ValueExtractorDefinitionException(final Throwable cause) {
        super(cause);
    }
    
    public ValueExtractorDefinitionException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
