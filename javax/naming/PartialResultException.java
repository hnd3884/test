package javax.naming;

public class PartialResultException extends NamingException
{
    private static final long serialVersionUID = 2572144970049426786L;
    
    public PartialResultException(final String s) {
        super(s);
    }
    
    public PartialResultException() {
    }
}
