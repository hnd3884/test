package org.bouncycastle.crypto;

public interface Mac
{
    void init(final CipherParameters p0) throws IllegalArgumentException;
    
    String getAlgorithmName();
    
    int getMacSize();
    
    void update(final byte p0) throws IllegalStateException;
    
    void update(final byte[] p0, final int p1, final int p2) throws DataLengthException, IllegalStateException;
    
    int doFinal(final byte[] p0, final int p1) throws DataLengthException, IllegalStateException;
    
    void reset();
}
