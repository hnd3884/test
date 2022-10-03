package javax.naming;

public class OperationNotSupportedException extends NamingException
{
    private static final long serialVersionUID = 5493232822427682064L;
    
    public OperationNotSupportedException() {
    }
    
    public OperationNotSupportedException(final String s) {
        super(s);
    }
}
