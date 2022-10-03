package javax.security.auth.login;

public class CredentialNotFoundException extends CredentialException
{
    private static final long serialVersionUID = -7779934467214319475L;
    
    public CredentialNotFoundException() {
    }
    
    public CredentialNotFoundException(final String s) {
        super(s);
    }
}
