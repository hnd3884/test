package javax.security.cert;

public class CertificateNotYetValidException extends CertificateException
{
    private static final long serialVersionUID = -8976172474266822818L;
    
    public CertificateNotYetValidException() {
    }
    
    public CertificateNotYetValidException(final String s) {
        super(s);
    }
}
