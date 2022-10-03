package org.bouncycastle.math.ec;

public interface ECLookupTable
{
    int getSize();
    
    ECPoint lookup(final int p0);
}
