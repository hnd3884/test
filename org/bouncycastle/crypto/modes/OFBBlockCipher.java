package org.bouncycastle.crypto.modes;

import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.StreamBlockCipher;

public class OFBBlockCipher extends StreamBlockCipher
{
    private int byteCount;
    private byte[] IV;
    private byte[] ofbV;
    private byte[] ofbOutV;
    private final int blockSize;
    private final BlockCipher cipher;
    
    public OFBBlockCipher(final BlockCipher cipher, final int n) {
        super(cipher);
        this.cipher = cipher;
        this.blockSize = n / 8;
        this.IV = new byte[cipher.getBlockSize()];
        this.ofbV = new byte[cipher.getBlockSize()];
        this.ofbOutV = new byte[cipher.getBlockSize()];
    }
    
    public void init(final boolean b, final CipherParameters cipherParameters) throws IllegalArgumentException {
        if (cipherParameters instanceof ParametersWithIV) {
            final ParametersWithIV parametersWithIV = (ParametersWithIV)cipherParameters;
            final byte[] iv = parametersWithIV.getIV();
            if (iv.length < this.IV.length) {
                System.arraycopy(iv, 0, this.IV, this.IV.length - iv.length, iv.length);
                for (int i = 0; i < this.IV.length - iv.length; ++i) {
                    this.IV[i] = 0;
                }
            }
            else {
                System.arraycopy(iv, 0, this.IV, 0, this.IV.length);
            }
            this.reset();
            if (parametersWithIV.getParameters() != null) {
                this.cipher.init(true, parametersWithIV.getParameters());
            }
        }
        else {
            this.reset();
            if (cipherParameters != null) {
                this.cipher.init(true, cipherParameters);
            }
        }
    }
    
    public String getAlgorithmName() {
        return this.cipher.getAlgorithmName() + "/OFB" + this.blockSize * 8;
    }
    
    public int getBlockSize() {
        return this.blockSize;
    }
    
    public int processBlock(final byte[] array, final int n, final byte[] array2, final int n2) throws DataLengthException, IllegalStateException {
        this.processBytes(array, n, this.blockSize, array2, n2);
        return this.blockSize;
    }
    
    public void reset() {
        System.arraycopy(this.IV, 0, this.ofbV, 0, this.IV.length);
        this.byteCount = 0;
        this.cipher.reset();
    }
    
    @Override
    protected byte calculateByte(final byte b) throws DataLengthException, IllegalStateException {
        if (this.byteCount == 0) {
            this.cipher.processBlock(this.ofbV, 0, this.ofbOutV, 0);
        }
        final byte b2 = (byte)(this.ofbOutV[this.byteCount++] ^ b);
        if (this.byteCount == this.blockSize) {
            this.byteCount = 0;
            System.arraycopy(this.ofbV, this.blockSize, this.ofbV, 0, this.ofbV.length - this.blockSize);
            System.arraycopy(this.ofbOutV, 0, this.ofbV, this.ofbV.length - this.blockSize, this.blockSize);
        }
        return b2;
    }
}
