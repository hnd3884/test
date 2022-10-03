package org.bouncycastle.crypto.tls;

import org.bouncycastle.crypto.Signer;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;

public interface TlsSigner
{
    void init(final TlsContext p0);
    
    byte[] generateRawSignature(final AsymmetricKeyParameter p0, final byte[] p1) throws CryptoException;
    
    byte[] generateRawSignature(final SignatureAndHashAlgorithm p0, final AsymmetricKeyParameter p1, final byte[] p2) throws CryptoException;
    
    boolean verifyRawSignature(final byte[] p0, final AsymmetricKeyParameter p1, final byte[] p2) throws CryptoException;
    
    boolean verifyRawSignature(final SignatureAndHashAlgorithm p0, final byte[] p1, final AsymmetricKeyParameter p2, final byte[] p3) throws CryptoException;
    
    Signer createSigner(final AsymmetricKeyParameter p0);
    
    Signer createSigner(final SignatureAndHashAlgorithm p0, final AsymmetricKeyParameter p1);
    
    Signer createVerifyer(final AsymmetricKeyParameter p0);
    
    Signer createVerifyer(final SignatureAndHashAlgorithm p0, final AsymmetricKeyParameter p1);
    
    boolean isValidPublicKey(final AsymmetricKeyParameter p0);
}
