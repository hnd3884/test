package org.bouncycastle.crypto.modes.gcm;

public interface GCMExponentiator
{
    void init(final byte[] p0);
    
    void exponentiateX(final long p0, final byte[] p1);
}
