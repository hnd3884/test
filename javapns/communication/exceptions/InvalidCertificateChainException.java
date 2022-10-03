package javapns.communication.exceptions;

public class InvalidCertificateChainException extends KeystoreException
{
    private static final long serialVersionUID = -1978821654637371922L;
    
    public InvalidCertificateChainException() {
        super("Invalid certificate chain!  Verify that the keystore you provided was produced according to specs...");
    }
    
    public InvalidCertificateChainException(final String message) {
        super("Invalid certificate chain (" + message + ")!  Verify that the keystore you provided was produced according to specs...");
    }
}
