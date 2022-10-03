package javax.xml.stream;

public class FactoryConfigurationError extends Error
{
    private static final long serialVersionUID = -2994412584589975744L;
    private Exception nested;
    
    public FactoryConfigurationError() {
    }
    
    public FactoryConfigurationError(final Exception nested) {
        this.nested = nested;
    }
    
    public FactoryConfigurationError(final Exception nested, final String s) {
        super(s);
        this.nested = nested;
    }
    
    public FactoryConfigurationError(final String s) {
        super(s);
    }
    
    public FactoryConfigurationError(final String s, final Exception nested) {
        super(s);
        this.nested = nested;
    }
    
    public Exception getException() {
        return this.nested;
    }
    
    public String getMessage() {
        String s = super.getMessage();
        if (s != null) {
            return s;
        }
        if (this.nested != null) {
            s = this.nested.getMessage();
            if (s == null) {
                s = this.nested.getClass().toString();
            }
        }
        return s;
    }
}
