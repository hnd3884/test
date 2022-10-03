package org.bouncycastle.crypto.prng;

public interface RandomGenerator
{
    void addSeedMaterial(final byte[] p0);
    
    void addSeedMaterial(final long p0);
    
    void nextBytes(final byte[] p0);
    
    void nextBytes(final byte[] p0, final int p1, final int p2);
}
