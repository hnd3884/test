package org.bouncycastle.crypto;

public interface StreamCipher
{
    void init(final boolean p0, final CipherParameters p1) throws IllegalArgumentException;
    
    String getAlgorithmName();
    
    byte returnByte(final byte p0);
    
    int processBytes(final byte[] p0, final int p1, final int p2, final byte[] p3, final int p4) throws DataLengthException;
    
    void reset();
}
