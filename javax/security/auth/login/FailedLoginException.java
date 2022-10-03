package javax.security.auth.login;

public class FailedLoginException extends LoginException
{
    private static final long serialVersionUID = 802556922354616286L;
    
    public FailedLoginException() {
    }
    
    public FailedLoginException(final String s) {
        super(s);
    }
}
