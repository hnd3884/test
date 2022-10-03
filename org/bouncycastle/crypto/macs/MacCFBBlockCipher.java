package org.bouncycastle.crypto.macs;

import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.BlockCipher;

class MacCFBBlockCipher
{
    private byte[] IV;
    private byte[] cfbV;
    private byte[] cfbOutV;
    private int blockSize;
    private BlockCipher cipher;
    
    public MacCFBBlockCipher(final BlockCipher cipher, final int n) {
        this.cipher = null;
        this.cipher = cipher;
        this.blockSize = n / 8;
        this.IV = new byte[cipher.getBlockSize()];
        this.cfbV = new byte[cipher.getBlockSize()];
        this.cfbOutV = new byte[cipher.getBlockSize()];
    }
    
    public void init(final CipherParameters cipherParameters) throws IllegalArgumentException {
        if (cipherParameters instanceof ParametersWithIV) {
            final ParametersWithIV parametersWithIV = (ParametersWithIV)cipherParameters;
            final byte[] iv = parametersWithIV.getIV();
            if (iv.length < this.IV.length) {
                System.arraycopy(iv, 0, this.IV, this.IV.length - iv.length, iv.length);
            }
            else {
                System.arraycopy(iv, 0, this.IV, 0, this.IV.length);
            }
            this.reset();
            this.cipher.init(true, parametersWithIV.getParameters());
        }
        else {
            this.reset();
            this.cipher.init(true, cipherParameters);
        }
    }
    
    public String getAlgorithmName() {
        return this.cipher.getAlgorithmName() + "/CFB" + this.blockSize * 8;
    }
    
    public int getBlockSize() {
        return this.blockSize;
    }
    
    public int processBlock(final byte[] array, final int n, final byte[] array2, final int n2) throws DataLengthException, IllegalStateException {
        if (n + this.blockSize > array.length) {
            throw new DataLengthException("input buffer too short");
        }
        if (n2 + this.blockSize > array2.length) {
            throw new OutputLengthException("output buffer too short");
        }
        this.cipher.processBlock(this.cfbV, 0, this.cfbOutV, 0);
        for (int i = 0; i < this.blockSize; ++i) {
            array2[n2 + i] = (byte)(this.cfbOutV[i] ^ array[n + i]);
        }
        System.arraycopy(this.cfbV, this.blockSize, this.cfbV, 0, this.cfbV.length - this.blockSize);
        System.arraycopy(array2, n2, this.cfbV, this.cfbV.length - this.blockSize, this.blockSize);
        return this.blockSize;
    }
    
    public void reset() {
        System.arraycopy(this.IV, 0, this.cfbV, 0, this.IV.length);
        this.cipher.reset();
    }
    
    void getMacBlock(final byte[] array) {
        this.cipher.processBlock(this.cfbV, 0, array, 0);
    }
}
