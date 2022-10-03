package javax.management;

public class JMRuntimeException extends RuntimeException
{
    private static final long serialVersionUID = 6573344628407841861L;
    
    public JMRuntimeException() {
    }
    
    public JMRuntimeException(final String s) {
        super(s);
    }
    
    JMRuntimeException(final String s, final Throwable t) {
        super(s, t);
    }
}
