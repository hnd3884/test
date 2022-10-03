package com.maverick.crypto.digests;

import java.io.IOException;

public interface HMac
{
    int doFinal(final byte[] p0, final int p1);
    
    int getMacSize();
    
    int getOutputSize();
    
    void init(final byte[] p0) throws IOException;
    
    void reset();
    
    void update(final byte p0);
    
    void update(final byte[] p0, final int p1, final int p2);
}
