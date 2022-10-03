package javax.xml.validation;

public final class SchemaFactoryConfigurationError extends Error
{
    static final long serialVersionUID = 3531438703147750126L;
    
    public SchemaFactoryConfigurationError() {
    }
    
    public SchemaFactoryConfigurationError(final String message) {
        super(message);
    }
    
    public SchemaFactoryConfigurationError(final Throwable cause) {
        super(cause);
    }
    
    public SchemaFactoryConfigurationError(final String message, final Throwable cause) {
        super(message, cause);
    }
}
