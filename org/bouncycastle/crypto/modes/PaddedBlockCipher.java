package org.bouncycastle.crypto.modes;

import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.BufferedBlockCipher;

public class PaddedBlockCipher extends BufferedBlockCipher
{
    public PaddedBlockCipher(final BlockCipher cipher) {
        this.cipher = cipher;
        this.buf = new byte[cipher.getBlockSize()];
        this.bufOff = 0;
    }
    
    @Override
    public int getOutputSize(final int n) {
        final int n2 = n + this.bufOff;
        final int n3 = n2 % this.buf.length;
        if (n3 != 0) {
            return n2 - n3 + this.buf.length;
        }
        if (this.forEncryption) {
            return n2 + this.buf.length;
        }
        return n2;
    }
    
    @Override
    public int getUpdateOutputSize(final int n) {
        final int n2 = n + this.bufOff;
        final int n3 = n2 % this.buf.length;
        if (n3 == 0) {
            return n2 - this.buf.length;
        }
        return n2 - n3;
    }
    
    @Override
    public int processByte(final byte b, final byte[] array, final int n) throws DataLengthException, IllegalStateException {
        int processBlock = 0;
        if (this.bufOff == this.buf.length) {
            processBlock = this.cipher.processBlock(this.buf, 0, array, n);
            this.bufOff = 0;
        }
        this.buf[this.bufOff++] = b;
        return processBlock;
    }
    
    @Override
    public int processBytes(final byte[] array, int n, int i, final byte[] array2, final int n2) throws DataLengthException, IllegalStateException {
        if (i < 0) {
            throw new IllegalArgumentException("Can't have a negative input length!");
        }
        final int blockSize = this.getBlockSize();
        final int updateOutputSize = this.getUpdateOutputSize(i);
        if (updateOutputSize > 0 && n2 + updateOutputSize > array2.length) {
            throw new OutputLengthException("output buffer too short");
        }
        int n3 = 0;
        final int n4 = this.buf.length - this.bufOff;
        if (i > n4) {
            System.arraycopy(array, n, this.buf, this.bufOff, n4);
            n3 += this.cipher.processBlock(this.buf, 0, array2, n2);
            this.bufOff = 0;
            for (i -= n4, n += n4; i > this.buf.length; i -= blockSize, n += blockSize) {
                n3 += this.cipher.processBlock(array, n, array2, n2 + n3);
            }
        }
        System.arraycopy(array, n, this.buf, this.bufOff, i);
        this.bufOff += i;
        return n3;
    }
    
    @Override
    public int doFinal(final byte[] array, final int n) throws DataLengthException, IllegalStateException, InvalidCipherTextException {
        final int blockSize = this.cipher.getBlockSize();
        int processBlock = 0;
        int n2;
        if (this.forEncryption) {
            if (this.bufOff == blockSize) {
                if (n + 2 * blockSize > array.length) {
                    throw new OutputLengthException("output buffer too short");
                }
                processBlock = this.cipher.processBlock(this.buf, 0, array, n);
                this.bufOff = 0;
            }
            final byte b = (byte)(blockSize - this.bufOff);
            while (this.bufOff < blockSize) {
                this.buf[this.bufOff] = b;
                ++this.bufOff;
            }
            n2 = processBlock + this.cipher.processBlock(this.buf, 0, array, n + processBlock);
        }
        else {
            if (this.bufOff != blockSize) {
                throw new DataLengthException("last block incomplete in decryption");
            }
            final int processBlock2 = this.cipher.processBlock(this.buf, 0, this.buf, 0);
            this.bufOff = 0;
            final int n3 = this.buf[blockSize - 1] & 0xFF;
            if (n3 < 0 || n3 > blockSize) {
                throw new InvalidCipherTextException("pad block corrupted");
            }
            n2 = processBlock2 - n3;
            System.arraycopy(this.buf, 0, array, n, n2);
        }
        this.reset();
        return n2;
    }
}
