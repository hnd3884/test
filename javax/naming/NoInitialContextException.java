package javax.naming;

public class NoInitialContextException extends NamingException
{
    private static final long serialVersionUID = -3413733186901258623L;
    
    public NoInitialContextException() {
    }
    
    public NoInitialContextException(final String s) {
        super(s);
    }
}
