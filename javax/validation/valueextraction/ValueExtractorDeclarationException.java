package javax.validation.valueextraction;

import javax.validation.ValidationException;

public class ValueExtractorDeclarationException extends ValidationException
{
    public ValueExtractorDeclarationException() {
    }
    
    public ValueExtractorDeclarationException(final String message) {
        super(message);
    }
    
    public ValueExtractorDeclarationException(final Throwable cause) {
        super(cause);
    }
    
    public ValueExtractorDeclarationException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
