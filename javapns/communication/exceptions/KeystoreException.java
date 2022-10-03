package javapns.communication.exceptions;

public class KeystoreException extends Exception
{
    private static final long serialVersionUID = 2549063865160633139L;
    
    public KeystoreException(final String message) {
        super(message);
    }
    
    public KeystoreException(final String message, final Exception cause) {
        super(message, cause);
    }
}
