package javax.security.sasl;

public class AuthenticationException extends SaslException
{
    private static final long serialVersionUID = -3579708765071815007L;
    
    public AuthenticationException() {
    }
    
    public AuthenticationException(final String s) {
        super(s);
    }
    
    public AuthenticationException(final String s, final Throwable t) {
        super(s, t);
    }
}
