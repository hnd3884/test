package org.bouncycastle.crypto.prng.drbg;

public interface SP80090DRBG
{
    int getBlockSize();
    
    int generate(final byte[] p0, final byte[] p1, final boolean p2);
    
    void reseed(final byte[] p0);
}
