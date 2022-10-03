package ar.com.fernandospr.wns.exceptions;

public class WnsException extends RuntimeException
{
    private static final long serialVersionUID = -2805144407471327141L;
    
    public WnsException() {
    }
    
    public WnsException(final String message) {
        super(message);
    }
    
    public WnsException(final Throwable cause) {
        super(cause);
    }
    
    public WnsException(final String m, final Throwable c) {
        super(m, c);
    }
}
