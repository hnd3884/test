package javax.naming;

public class AuthenticationException extends NamingSecurityException
{
    private static final long serialVersionUID = 3678497619904568096L;
    
    public AuthenticationException(final String s) {
        super(s);
    }
    
    public AuthenticationException() {
    }
}
