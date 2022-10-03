package org.bouncycastle.crypto;

public interface AsymmetricBlockCipher
{
    void init(final boolean p0, final CipherParameters p1);
    
    int getInputBlockSize();
    
    int getOutputBlockSize();
    
    byte[] processBlock(final byte[] p0, final int p1, final int p2) throws InvalidCipherTextException;
}
