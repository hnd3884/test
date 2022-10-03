package sun.security.pkcs11.wrapper;

public class PKCS11RuntimeException extends RuntimeException
{
    private static final long serialVersionUID = 7889842162743590564L;
    
    public PKCS11RuntimeException() {
    }
    
    public PKCS11RuntimeException(final String s) {
        super(s);
    }
    
    public PKCS11RuntimeException(final Exception ex) {
        super(ex);
    }
    
    public PKCS11RuntimeException(final String s, final Exception ex) {
        super(s, ex);
    }
}
