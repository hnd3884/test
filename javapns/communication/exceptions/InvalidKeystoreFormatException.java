package javapns.communication.exceptions;

public class InvalidKeystoreFormatException extends KeystoreException
{
    private static final long serialVersionUID = 8822634206752412121L;
    
    public InvalidKeystoreFormatException() {
        super("Invalid keystore format!  Make sure it is PKCS12...");
    }
    
    public InvalidKeystoreFormatException(final String message) {
        super(message);
    }
}
