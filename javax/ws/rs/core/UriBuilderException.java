package javax.ws.rs.core;

public class UriBuilderException extends RuntimeException
{
    private static final long serialVersionUID = 956255913370721193L;
    
    public UriBuilderException() {
    }
    
    public UriBuilderException(final String msg) {
        super(msg);
    }
    
    public UriBuilderException(final String msg, final Throwable cause) {
        super(msg, cause);
    }
    
    public UriBuilderException(final Throwable cause) {
        super(cause);
    }
}
