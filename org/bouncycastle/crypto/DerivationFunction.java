package org.bouncycastle.crypto;

public interface DerivationFunction
{
    void init(final DerivationParameters p0);
    
    int generateBytes(final byte[] p0, final int p1, final int p2) throws DataLengthException, IllegalArgumentException;
}
