package org.bouncycastle.openssl;

public class EncryptionException extends PEMException
{
    private Throwable cause;
    
    public EncryptionException(final String s) {
        super(s);
    }
    
    public EncryptionException(final String s, final Throwable cause) {
        super(s);
        this.cause = cause;
    }
    
    @Override
    public Throwable getCause() {
        return this.cause;
    }
}
