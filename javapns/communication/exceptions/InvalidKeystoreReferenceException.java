package javapns.communication.exceptions;

public class InvalidKeystoreReferenceException extends KeystoreException
{
    private static final long serialVersionUID = 3144387163593035745L;
    
    public InvalidKeystoreReferenceException() {
        super("Invalid keystore parameter.  Must be InputStream, File, String (as a file path), or byte[].");
    }
    
    public InvalidKeystoreReferenceException(final Object keystore) {
        super("Invalid keystore parameter (" + keystore + ").  Must be InputStream, File, String (as a file path), or byte[].");
    }
    
    public InvalidKeystoreReferenceException(final String message) {
        super(message);
    }
}
