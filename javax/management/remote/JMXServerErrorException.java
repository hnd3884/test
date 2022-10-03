package javax.management.remote;

import java.io.IOException;

public class JMXServerErrorException extends IOException
{
    private static final long serialVersionUID = 3996732239558744666L;
    private final Error cause;
    
    public JMXServerErrorException(final String s, final Error cause) {
        super(s);
        this.cause = cause;
    }
    
    @Override
    public Throwable getCause() {
        return this.cause;
    }
}
