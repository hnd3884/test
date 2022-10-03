package org.bouncycastle.crypto.modes;

import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.BlockCipher;

public class PGPCFBBlockCipher implements BlockCipher
{
    private byte[] IV;
    private byte[] FR;
    private byte[] FRE;
    private byte[] tmp;
    private BlockCipher cipher;
    private int count;
    private int blockSize;
    private boolean forEncryption;
    private boolean inlineIv;
    
    public PGPCFBBlockCipher(final BlockCipher cipher, final boolean inlineIv) {
        this.cipher = cipher;
        this.inlineIv = inlineIv;
        this.blockSize = cipher.getBlockSize();
        this.IV = new byte[this.blockSize];
        this.FR = new byte[this.blockSize];
        this.FRE = new byte[this.blockSize];
        this.tmp = new byte[this.blockSize];
    }
    
    public BlockCipher getUnderlyingCipher() {
        return this.cipher;
    }
    
    public String getAlgorithmName() {
        if (this.inlineIv) {
            return this.cipher.getAlgorithmName() + "/PGPCFBwithIV";
        }
        return this.cipher.getAlgorithmName() + "/PGPCFB";
    }
    
    public int getBlockSize() {
        return this.cipher.getBlockSize();
    }
    
    public int processBlock(final byte[] array, final int n, final byte[] array2, final int n2) throws DataLengthException, IllegalStateException {
        if (this.inlineIv) {
            return this.forEncryption ? this.encryptBlockWithIV(array, n, array2, n2) : this.decryptBlockWithIV(array, n, array2, n2);
        }
        return this.forEncryption ? this.encryptBlock(array, n, array2, n2) : this.decryptBlock(array, n, array2, n2);
    }
    
    public void reset() {
        this.count = 0;
        for (int i = 0; i != this.FR.length; ++i) {
            if (this.inlineIv) {
                this.FR[i] = 0;
            }
            else {
                this.FR[i] = this.IV[i];
            }
        }
        this.cipher.reset();
    }
    
    public void init(final boolean forEncryption, final CipherParameters cipherParameters) throws IllegalArgumentException {
        this.forEncryption = forEncryption;
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
            this.cipher.init(true, parametersWithIV.getParameters());
        }
        else {
            this.reset();
            this.cipher.init(true, cipherParameters);
        }
    }
    
    private byte encryptByte(final byte b, final int n) {
        return (byte)(this.FRE[n] ^ b);
    }
    
    private int encryptBlockWithIV(final byte[] array, final int n, final byte[] array2, final int n2) throws DataLengthException, IllegalStateException {
        if (n + this.blockSize > array.length) {
            throw new DataLengthException("input buffer too short");
        }
        if (this.count != 0) {
            if (this.count >= this.blockSize + 2) {
                if (n2 + this.blockSize > array2.length) {
                    throw new OutputLengthException("output buffer too short");
                }
                this.cipher.processBlock(this.FR, 0, this.FRE, 0);
                for (int i = 0; i < this.blockSize; ++i) {
                    array2[n2 + i] = this.encryptByte(array[n + i], i);
                }
                System.arraycopy(array2, n2, this.FR, 0, this.blockSize);
            }
            return this.blockSize;
        }
        if (n2 + 2 * this.blockSize + 2 > array2.length) {
            throw new OutputLengthException("output buffer too short");
        }
        this.cipher.processBlock(this.FR, 0, this.FRE, 0);
        for (int j = 0; j < this.blockSize; ++j) {
            array2[n2 + j] = this.encryptByte(this.IV[j], j);
        }
        System.arraycopy(array2, n2, this.FR, 0, this.blockSize);
        this.cipher.processBlock(this.FR, 0, this.FRE, 0);
        array2[n2 + this.blockSize] = this.encryptByte(this.IV[this.blockSize - 2], 0);
        array2[n2 + this.blockSize + 1] = this.encryptByte(this.IV[this.blockSize - 1], 1);
        System.arraycopy(array2, n2 + 2, this.FR, 0, this.blockSize);
        this.cipher.processBlock(this.FR, 0, this.FRE, 0);
        for (int k = 0; k < this.blockSize; ++k) {
            array2[n2 + this.blockSize + 2 + k] = this.encryptByte(array[n + k], k);
        }
        System.arraycopy(array2, n2 + this.blockSize + 2, this.FR, 0, this.blockSize);
        this.count += 2 * this.blockSize + 2;
        return 2 * this.blockSize + 2;
    }
    
    private int decryptBlockWithIV(final byte[] array, final int n, final byte[] array2, final int n2) throws DataLengthException, IllegalStateException {
        if (n + this.blockSize > array.length) {
            throw new DataLengthException("input buffer too short");
        }
        if (n2 + this.blockSize > array2.length) {
            throw new OutputLengthException("output buffer too short");
        }
        if (this.count == 0) {
            for (int i = 0; i < this.blockSize; ++i) {
                this.FR[i] = array[n + i];
            }
            this.cipher.processBlock(this.FR, 0, this.FRE, 0);
            this.count += this.blockSize;
            return 0;
        }
        if (this.count == this.blockSize) {
            System.arraycopy(array, n, this.tmp, 0, this.blockSize);
            System.arraycopy(this.FR, 2, this.FR, 0, this.blockSize - 2);
            this.FR[this.blockSize - 2] = this.tmp[0];
            this.FR[this.blockSize - 1] = this.tmp[1];
            this.cipher.processBlock(this.FR, 0, this.FRE, 0);
            for (int j = 0; j < this.blockSize - 2; ++j) {
                array2[n2 + j] = this.encryptByte(this.tmp[j + 2], j);
            }
            System.arraycopy(this.tmp, 2, this.FR, 0, this.blockSize - 2);
            this.count += 2;
            return this.blockSize - 2;
        }
        if (this.count >= this.blockSize + 2) {
            System.arraycopy(array, n, this.tmp, 0, this.blockSize);
            array2[n2 + 0] = this.encryptByte(this.tmp[0], this.blockSize - 2);
            array2[n2 + 1] = this.encryptByte(this.tmp[1], this.blockSize - 1);
            System.arraycopy(this.tmp, 0, this.FR, this.blockSize - 2, 2);
            this.cipher.processBlock(this.FR, 0, this.FRE, 0);
            for (int k = 0; k < this.blockSize - 2; ++k) {
                array2[n2 + k + 2] = this.encryptByte(this.tmp[k + 2], k);
            }
            System.arraycopy(this.tmp, 2, this.FR, 0, this.blockSize - 2);
        }
        return this.blockSize;
    }
    
    private int encryptBlock(final byte[] array, final int n, final byte[] array2, final int n2) throws DataLengthException, IllegalStateException {
        if (n + this.blockSize > array.length) {
            throw new DataLengthException("input buffer too short");
        }
        if (n2 + this.blockSize > array2.length) {
            throw new OutputLengthException("output buffer too short");
        }
        this.cipher.processBlock(this.FR, 0, this.FRE, 0);
        for (int i = 0; i < this.blockSize; ++i) {
            array2[n2 + i] = this.encryptByte(array[n + i], i);
        }
        for (int j = 0; j < this.blockSize; ++j) {
            this.FR[j] = array2[n2 + j];
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
        this.cipher.processBlock(this.FR, 0, this.FRE, 0);
        for (int i = 0; i < this.blockSize; ++i) {
            array2[n2 + i] = this.encryptByte(array[n + i], i);
        }
        for (int j = 0; j < this.blockSize; ++j) {
            this.FR[j] = array[n + j];
        }
        return this.blockSize;
    }
}
