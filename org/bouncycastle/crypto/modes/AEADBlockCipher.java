package org.bouncycastle.crypto.modes;

import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;

public interface AEADBlockCipher
{
    void init(final boolean p0, final CipherParameters p1) throws IllegalArgumentException;
    
    String getAlgorithmName();
    
    BlockCipher getUnderlyingCipher();
    
    void processAADByte(final byte p0);
    
    void processAADBytes(final byte[] p0, final int p1, final int p2);
    
    int processByte(final byte p0, final byte[] p1, final int p2) throws DataLengthException;
    
    int processBytes(final byte[] p0, final int p1, final int p2, final byte[] p3, final int p4) throws DataLengthException;
    
    int doFinal(final byte[] p0, final int p1) throws IllegalStateException, InvalidCipherTextException;
    
    byte[] getMac();
    
    int getUpdateOutputSize(final int p0);
    
    int getOutputSize(final int p0);
    
    void reset();
}
