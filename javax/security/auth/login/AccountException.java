package javax.security.auth.login;

public class AccountException extends LoginException
{
    private static final long serialVersionUID = -2112878680072211787L;
    
    public AccountException() {
    }
    
    public AccountException(final String s) {
        super(s);
    }
}
