package com.maverick.crypto.engines;

import java.io.IOException;

public interface CipherEngine
{
    void init(final boolean p0, final byte[] p1);
    
    int getBlockSize();
    
    int processBlock(final byte[] p0, final int p1, final byte[] p2, final int p3) throws IOException;
}
