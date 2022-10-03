package org.bouncycastle.crypto;

public interface KeyEncapsulation
{
    void init(final CipherParameters p0);
    
    CipherParameters encrypt(final byte[] p0, final int p1, final int p2);
    
    CipherParameters decrypt(final byte[] p0, final int p1, final int p2, final int p3);
}
