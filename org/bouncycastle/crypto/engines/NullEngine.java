package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.BlockCipher;

public class NullEngine implements BlockCipher
{
    private boolean initialised;
    protected static final int DEFAULT_BLOCK_SIZE = 1;
    private final int blockSize;
    
    public NullEngine() {
        this(1);
    }
    
    public NullEngine(final int blockSize) {
        this.blockSize = blockSize;
    }
    
    public void init(final boolean b, final CipherParameters cipherParameters) throws IllegalArgumentException {
        this.initialised = true;
    }
    
    public String getAlgorithmName() {
        return "Null";
    }
    
    public int getBlockSize() {
        return this.blockSize;
    }
    
    public int processBlock(final byte[] array, final int n, final byte[] array2, final int n2) throws DataLengthException, IllegalStateException {
        if (!this.initialised) {
            throw new IllegalStateException("Null engine not initialised");
        }
        if (n + this.blockSize > array.length) {
            throw new DataLengthException("input buffer too short");
        }
        if (n2 + this.blockSize > array2.length) {
            throw new OutputLengthException("output buffer too short");
        }
        for (int i = 0; i < this.blockSize; ++i) {
            array2[n2 + i] = array[n + i];
        }
        return this.blockSize;
    }
    
    public void reset() {
    }
}
