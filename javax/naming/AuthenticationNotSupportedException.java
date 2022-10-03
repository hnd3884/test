package javax.naming;

public class AuthenticationNotSupportedException extends NamingSecurityException
{
    private static final long serialVersionUID = -7149033933259492300L;
    
    public AuthenticationNotSupportedException(final String s) {
        super(s);
    }
    
    public AuthenticationNotSupportedException() {
    }
}
