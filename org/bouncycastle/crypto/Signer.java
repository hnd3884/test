package org.bouncycastle.crypto;

public interface Signer
{
    void init(final boolean p0, final CipherParameters p1);
    
    void update(final byte p0);
    
    void update(final byte[] p0, final int p1, final int p2);
    
    byte[] generateSignature() throws CryptoException, DataLengthException;
    
    boolean verifySignature(final byte[] p0);
    
    void reset();
}
