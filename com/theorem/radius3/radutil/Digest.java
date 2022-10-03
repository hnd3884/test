package com.theorem.radius3.radutil;

public interface Digest
{
    String getAlgorithmName();
    
    int getDigestSize();
    
    void update(final byte p0);
    
    void update(final byte[] p0, final int p1, final int p2);
    
    int doFinal(final byte[] p0, final int p1);
    
    void reset();
}
