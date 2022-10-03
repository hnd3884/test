package sun.misc;

public class ServiceConfigurationError extends Error
{
    static final long serialVersionUID = 8769866263384244465L;
    
    public ServiceConfigurationError(final String s) {
        super(s);
    }
    
    public ServiceConfigurationError(final Throwable t) {
        super(t);
    }
}
