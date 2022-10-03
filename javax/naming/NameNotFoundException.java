package javax.naming;

public class NameNotFoundException extends NamingException
{
    private static final long serialVersionUID = -8007156725367842053L;
    
    public NameNotFoundException(final String s) {
        super(s);
    }
    
    public NameNotFoundException() {
    }
}
