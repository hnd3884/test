package sun.security.util;

public class PendingException extends RuntimeException
{
    private static final long serialVersionUID = -5201837247928788640L;
    
    public PendingException() {
    }
    
    public PendingException(final String s) {
        super(s);
    }
}
