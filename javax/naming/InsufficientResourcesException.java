package javax.naming;

public class InsufficientResourcesException extends NamingException
{
    private static final long serialVersionUID = 6227672693037844532L;
    
    public InsufficientResourcesException(final String s) {
        super(s);
    }
    
    public InsufficientResourcesException() {
    }
}
