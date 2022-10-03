package org.owasp.esapi.errors;

public class ConfigurationException extends RuntimeException
{
    protected static final long serialVersionUID = 1L;
    
    public ConfigurationException(final Exception e) {
        super(e);
    }
    
    public ConfigurationException(final String s) {
        super(s);
    }
    
    public ConfigurationException(final String s, final Throwable cause) {
        super(s, cause);
    }
    
    public ConfigurationException(final Throwable cause) {
        super(cause);
    }
}
