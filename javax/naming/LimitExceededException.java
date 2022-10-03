package javax.naming;

public class LimitExceededException extends NamingException
{
    private static final long serialVersionUID = -776898738660207856L;
    
    public LimitExceededException() {
    }
    
    public LimitExceededException(final String s) {
        super(s);
    }
}
