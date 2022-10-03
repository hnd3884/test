package javax.naming;

public class InvalidNameException extends NamingException
{
    private static final long serialVersionUID = -8370672380823801105L;
    
    public InvalidNameException(final String s) {
        super(s);
    }
    
    public InvalidNameException() {
    }
}
