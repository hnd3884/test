package org.bouncycastle.crypto;

public interface Wrapper
{
    void init(final boolean p0, final CipherParameters p1);
    
    String getAlgorithmName();
    
    byte[] wrap(final byte[] p0, final int p1, final int p2);
    
    byte[] unwrap(final byte[] p0, final int p1, final int p2) throws InvalidCipherTextException;
}
