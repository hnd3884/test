package org.bouncycastle.crypto.tls;

import org.bouncycastle.crypto.Signer;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;

public abstract class AbstractTlsSigner implements TlsSigner
{
    protected TlsContext context;
    
    public void init(final TlsContext context) {
        this.context = context;
    }
    
    public byte[] generateRawSignature(final AsymmetricKeyParameter asymmetricKeyParameter, final byte[] array) throws CryptoException {
        return this.generateRawSignature(null, asymmetricKeyParameter, array);
    }
    
    public boolean verifyRawSignature(final byte[] array, final AsymmetricKeyParameter asymmetricKeyParameter, final byte[] array2) throws CryptoException {
        return this.verifyRawSignature(null, array, asymmetricKeyParameter, array2);
    }
    
    public Signer createSigner(final AsymmetricKeyParameter asymmetricKeyParameter) {
        return this.createSigner(null, asymmetricKeyParameter);
    }
    
    public Signer createVerifyer(final AsymmetricKeyParameter asymmetricKeyParameter) {
        return this.createVerifyer(null, asymmetricKeyParameter);
    }
}
