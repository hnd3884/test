package org.bouncycastle.crypto.modes.kgcm;

public interface KGCMMultiplier
{
    void init(final long[] p0);
    
    void multiplyH(final long[] p0);
}
