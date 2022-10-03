package org.bouncycastle.crypto;

public class RuntimeCryptoException extends RuntimeException
{
    public RuntimeCryptoException() {
    }
    
    public RuntimeCryptoException(final String s) {
        super(s);
    }
}
