package javapns.communication.exceptions;

public class InvalidKeystorePasswordException extends KeystoreException
{
    private static final long serialVersionUID = 5973743951334025887L;
    
    public InvalidKeystorePasswordException() {
        super("Invalid keystore password!  Verify settings for connecting to Apple...");
    }
    
    public InvalidKeystorePasswordException(final String message) {
        super(message);
    }
}
