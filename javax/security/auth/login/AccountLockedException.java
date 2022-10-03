package javax.security.auth.login;

public class AccountLockedException extends AccountException
{
    private static final long serialVersionUID = 8280345554014066334L;
    
    public AccountLockedException() {
    }
    
    public AccountLockedException(final String s) {
        super(s);
    }
}
