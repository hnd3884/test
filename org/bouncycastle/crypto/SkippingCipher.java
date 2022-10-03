package org.bouncycastle.crypto;

public interface SkippingCipher
{
    long skip(final long p0);
    
    long seekTo(final long p0);
    
    long getPosition();
}
