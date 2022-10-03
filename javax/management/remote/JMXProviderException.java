package javax.management.remote;

import java.io.IOException;

public class JMXProviderException extends IOException
{
    private static final long serialVersionUID = -3166703627550447198L;
    private Throwable cause;
    
    public JMXProviderException() {
        this.cause = null;
    }
    
    public JMXProviderException(final String s) {
        super(s);
        this.cause = null;
    }
    
    public JMXProviderException(final String s, final Throwable cause) {
        super(s);
        this.cause = null;
        this.cause = cause;
    }
    
    @Override
    public Throwable getCause() {
        return this.cause;
    }
}
