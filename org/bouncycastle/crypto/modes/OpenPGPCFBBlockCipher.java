package org.bouncycastle.crypto.modes;

import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.BlockCipher;

public class OpenPGPCFBBlockCipher implements BlockCipher
{
    private byte[] IV;
    private byte[] FR;
    private byte[] FRE;
    private BlockCipher cipher;
    private int count;
    private int blockSize;
    private boolean forEncryption;
    
    public OpenPGPCFBBlockCipher(final BlockCipher cipher) {
        this.cipher = cipher;
        this.blockSize = cipher.getBlockSize();
        this.IV = new byte[this.blockSize];
        this.FR = new byte[this.blockSize];
        this.FRE = new byte[this.blockSize];
    }
    
    public BlockCipher getUnderlyingCipher() {
        return this.cipher;
    }
    
    public String getAlgorithmName() {
        return this.cipher.getAlgorithmName() + "/OpenPGPCFB";
    }
    
    public int getBlockSize() {
        return this.cipher.getBlockSize();
    }
    
    public int processBlock(final byte[] array, final int n, final byte[] array2, final int n2) throws DataLengthException, IllegalStateException {
        return this.forEncryption ? this.encryptBlock(array, n, array2, n2) : this.decryptBlock(array, n, array2, n2);
    }
    
    public void reset() {
        this.count = 0;
        System.arraycopy(this.IV, 0, this.FR, 0, this.FR.length);
        this.cipher.reset();
    }
    
    public void init(final boolean forEncryption, final CipherParameters cipherParameters) throws IllegalArgumentException {
        this.forEncryption = forEncryption;
        this.reset();
        this.cipher.init(true, cipherParameters);
    }
    
    private byte encryptByte(final byte b, final int n) {
        return (byte)(this.FRE[n] ^ b);
    }
    
    private int encryptBlock(final byte[] array, final int n, final byte[] array2, final int n2) throws DataLengthException, IllegalStateException {
        if (n + this.blockSize > array.length) {
            throw new DataLengthException("input buffer too short");
        }
        if (n2 + this.blockSize > array2.length) {
            throw new OutputLengthException("output buffer too short");
        }
        if (this.count > this.blockSize) {
            this.FR[this.blockSize - 2] = (array2[n2] = this.encryptByte(array[n], this.blockSize - 2));
            this.FR[this.blockSize - 1] = (array2[n2 + 1] = this.encryptByte(array[n + 1], this.blockSize - 1));
            this.cipher.processBlock(this.FR, 0, this.FRE, 0);
            for (int i = 2; i < this.blockSize; ++i) {
                this.FR[i - 2] = (array2[n2 + i] = this.encryptByte(array[n + i], i - 2));
            }
        }
        else if (this.count == 0) {
            this.cipher.processBlock(this.FR, 0, this.FRE, 0);
            for (int j = 0; j < this.blockSize; ++j) {
                this.FR[j] = (array2[n2 + j] = this.encryptByte(array[n + j], j));
            }
            this.count += this.blockSize;
        }
        else if (this.count == this.blockSize) {
            this.cipher.processBlock(this.FR, 0, this.FRE, 0);
            array2[n2] = this.encryptByte(array[n], 0);
            array2[n2 + 1] = this.encryptByte(array[n + 1], 1);
            System.arraycopy(this.FR, 2, this.FR, 0, this.blockSize - 2);
            System.arraycopy(array2, n2, this.FR, this.blockSize - 2, 2);
            this.cipher.processBlock(this.FR, 0, this.FRE, 0);
            for (int k = 2; k < this.blockSize; ++k) {
                this.FR[k - 2] = (array2[n2 + k] = this.encryptByte(array[n + k], k - 2));
            }
            this.count += this.blockSize;
        }
        return this.blockSize;
    }
    
    private int decryptBlock(final byte[] array, final int n, final byte[] array2, final int n2) throws DataLengthException, IllegalStateException {
        if (n + this.blockSize > array.length) {
            throw new DataLengthException("input buffer too short");
        }
        if (n2 + this.blockSize > array2.length) {
            throw new OutputLengthException("output buffer too short");
        }
        if (this.count > this.blockSize) {
            final byte b = array[n];
            this.FR[this.blockSize - 2] = b;
            array2[n2] = this.encryptByte(b, this.blockSize - 2);
            final byte b2 = array[n + 1];
            this.FR[this.blockSize - 1] = b2;
            array2[n2 + 1] = this.encryptByte(b2, this.blockSize - 1);
            this.cipher.processBlock(this.FR, 0, this.FRE, 0);
            for (int i = 2; i < this.blockSize; ++i) {
                final byte b3 = array[n + i];
                this.FR[i - 2] = b3;
                array2[n2 + i] = this.encryptByte(b3, i - 2);
            }
        }
        else if (this.count == 0) {
            this.cipher.processBlock(this.FR, 0, this.FRE, 0);
            for (int j = 0; j < this.blockSize; ++j) {
                this.FR[j] = array[n + j];
                array2[j] = this.encryptByte(array[n + j], j);
            }
            this.count += this.blockSize;
        }
        else if (this.count == this.blockSize) {
            this.cipher.processBlock(this.FR, 0, this.FRE, 0);
            final byte b4 = array[n];
            final byte b5 = array[n + 1];
            array2[n2] = this.encryptByte(b4, 0);
            array2[n2 + 1] = this.encryptByte(b5, 1);
            System.arraycopy(this.FR, 2, this.FR, 0, this.blockSize - 2);
            this.FR[this.blockSize - 2] = b4;
            this.FR[this.blockSize - 1] = b5;
            this.cipher.processBlock(this.FR, 0, this.FRE, 0);
            for (int k = 2; k < this.blockSize; ++k) {
                final byte b6 = array[n + k];
                this.FR[k - 2] = b6;
                array2[n2 + k] = this.encryptByte(b6, k - 2);
            }
            this.count += this.blockSize;
        }
        return this.blockSize;
    }
}
