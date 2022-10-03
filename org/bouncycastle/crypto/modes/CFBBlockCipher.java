package org.bouncycastle.crypto.modes;

import org.bouncycastle.util.Arrays;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.StreamBlockCipher;

public class CFBBlockCipher extends StreamBlockCipher
{
    private byte[] IV;
    private byte[] cfbV;
    private byte[] cfbOutV;
    private byte[] inBuf;
    private int blockSize;
    private BlockCipher cipher;
    private boolean encrypting;
    private int byteCount;
    
    public CFBBlockCipher(final BlockCipher cipher, final int n) {
        super(cipher);
        this.cipher = null;
        this.cipher = cipher;
        this.blockSize = n / 8;
        this.IV = new byte[cipher.getBlockSize()];
        this.cfbV = new byte[cipher.getBlockSize()];
        this.cfbOutV = new byte[cipher.getBlockSize()];
        this.inBuf = new byte[this.blockSize];
    }
    
    public void init(final boolean encrypting, final CipherParameters cipherParameters) throws IllegalArgumentException {
        this.encrypting = encrypting;
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
        return this.cipher.getAlgorithmName() + "/CFB" + this.blockSize * 8;
    }
    
    @Override
    protected byte calculateByte(final byte b) throws DataLengthException, IllegalStateException {
        return this.encrypting ? this.encryptByte(b) : this.decryptByte(b);
    }
    
    private byte encryptByte(final byte b) {
        if (this.byteCount == 0) {
            this.cipher.processBlock(this.cfbV, 0, this.cfbOutV, 0);
        }
        final byte b2 = (byte)(this.cfbOutV[this.byteCount] ^ b);
        this.inBuf[this.byteCount++] = b2;
        if (this.byteCount == this.blockSize) {
            this.byteCount = 0;
            System.arraycopy(this.cfbV, this.blockSize, this.cfbV, 0, this.cfbV.length - this.blockSize);
            System.arraycopy(this.inBuf, 0, this.cfbV, this.cfbV.length - this.blockSize, this.blockSize);
        }
        return b2;
    }
    
    private byte decryptByte(final byte b) {
        if (this.byteCount == 0) {
            this.cipher.processBlock(this.cfbV, 0, this.cfbOutV, 0);
        }
        this.inBuf[this.byteCount] = b;
        final byte b2 = (byte)(this.cfbOutV[this.byteCount++] ^ b);
        if (this.byteCount == this.blockSize) {
            this.byteCount = 0;
            System.arraycopy(this.cfbV, this.blockSize, this.cfbV, 0, this.cfbV.length - this.blockSize);
            System.arraycopy(this.inBuf, 0, this.cfbV, this.cfbV.length - this.blockSize, this.blockSize);
        }
        return b2;
    }
    
    public int getBlockSize() {
        return this.blockSize;
    }
    
    public int processBlock(final byte[] array, final int n, final byte[] array2, final int n2) throws DataLengthException, IllegalStateException {
        this.processBytes(array, n, this.blockSize, array2, n2);
        return this.blockSize;
    }
    
    public int encryptBlock(final byte[] array, final int n, final byte[] array2, final int n2) throws DataLengthException, IllegalStateException {
        this.processBytes(array, n, this.blockSize, array2, n2);
        return this.blockSize;
    }
    
    public int decryptBlock(final byte[] array, final int n, final byte[] array2, final int n2) throws DataLengthException, IllegalStateException {
        this.processBytes(array, n, this.blockSize, array2, n2);
        return this.blockSize;
    }
    
    public byte[] getCurrentIV() {
        return Arrays.clone(this.cfbV);
    }
    
    public void reset() {
        System.arraycopy(this.IV, 0, this.cfbV, 0, this.IV.length);
        Arrays.fill(this.inBuf, (byte)0);
        this.byteCount = 0;
        this.cipher.reset();
    }
}
