package javax.ws.rs.core;

import java.io.IOException;

public class NoContentException extends IOException
{
    private static final long serialVersionUID = -3082577759787473245L;
    
    public NoContentException(final String message) {
        super(message);
    }
    
    public NoContentException(final String message, final Throwable cause) {
        super(message, cause);
    }
    
    public NoContentException(final Throwable cause) {
        super(cause);
    }
}
