package javax.security.auth.login;

public class AccountExpiredException extends AccountException
{
    private static final long serialVersionUID = -6064064890162661560L;
    
    public AccountExpiredException() {
    }
    
    public AccountExpiredException(final String s) {
        super(s);
    }
}
