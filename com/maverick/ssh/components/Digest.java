package com.maverick.ssh.components;

import java.math.BigInteger;

public interface Digest
{
    void putBigInteger(final BigInteger p0);
    
    void putByte(final byte p0);
    
    void putBytes(final byte[] p0);
    
    void putBytes(final byte[] p0, final int p1, final int p2);
    
    void putInt(final int p0);
    
    void putString(final String p0);
    
    void reset();
    
    byte[] doFinal();
}
