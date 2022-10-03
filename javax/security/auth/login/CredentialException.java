package javax.security.auth.login;

public class CredentialException extends LoginException
{
    private static final long serialVersionUID = -4772893876810601859L;
    
    public CredentialException() {
    }
    
    public CredentialException(final String s) {
        super(s);
    }
}
