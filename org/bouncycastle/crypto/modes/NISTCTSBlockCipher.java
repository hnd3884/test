package org.bouncycastle.crypto.modes;

import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.BufferedBlockCipher;

public class NISTCTSBlockCipher extends BufferedBlockCipher
{
    public static final int CS1 = 1;
    public static final int CS2 = 2;
    public static final int CS3 = 3;
    private final int type;
    private final int blockSize;
    
    public NISTCTSBlockCipher(final int type, final BlockCipher blockCipher) {
        this.type = type;
        this.cipher = new CBCBlockCipher(blockCipher);
        this.blockSize = blockCipher.getBlockSize();
        this.buf = new byte[this.blockSize * 2];
        this.bufOff = 0;
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
    public int getOutputSize(final int n) {
        return n + this.bufOff;
    }
    
    @Override
    public int processByte(final byte b, final byte[] array, final int n) throws DataLengthException, IllegalStateException {
        int processBlock = 0;
        if (this.bufOff == this.buf.length) {
            processBlock = this.cipher.processBlock(this.buf, 0, array, n);
            System.arraycopy(this.buf, this.blockSize, this.buf, 0, this.blockSize);
            this.bufOff = this.blockSize;
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
            System.arraycopy(this.buf, blockSize, this.buf, 0, blockSize);
            this.bufOff = blockSize;
            for (i -= n4, n += n4; i > blockSize; i -= blockSize, n += blockSize) {
                System.arraycopy(array, n, this.buf, this.bufOff, blockSize);
                n3 += this.cipher.processBlock(this.buf, 0, array2, n2 + n3);
                System.arraycopy(this.buf, blockSize, this.buf, 0, blockSize);
            }
        }
        System.arraycopy(array, n, this.buf, this.bufOff, i);
        this.bufOff += i;
        return n3;
    }
    
    @Override
    public int doFinal(final byte[] array, final int n) throws DataLengthException, IllegalStateException, InvalidCipherTextException {
        if (this.bufOff + n > array.length) {
            throw new OutputLengthException("output buffer to small in doFinal");
        }
        final int blockSize = this.cipher.getBlockSize();
        final int n2 = this.bufOff - blockSize;
        final byte[] array2 = new byte[blockSize];
        if (this.forEncryption) {
            if (this.bufOff < blockSize) {
                throw new DataLengthException("need at least one block of input for NISTCTS");
            }
            if (this.bufOff > blockSize) {
                final byte[] array3 = new byte[blockSize];
                if (this.type == 2 || this.type == 3) {
                    this.cipher.processBlock(this.buf, 0, array2, 0);
                    System.arraycopy(this.buf, blockSize, array3, 0, n2);
                    this.cipher.processBlock(array3, 0, array3, 0);
                    if (this.type == 2 && n2 == blockSize) {
                        System.arraycopy(array2, 0, array, n, blockSize);
                        System.arraycopy(array3, 0, array, n + blockSize, n2);
                    }
                    else {
                        System.arraycopy(array3, 0, array, n, blockSize);
                        System.arraycopy(array2, 0, array, n + blockSize, n2);
                    }
                }
                else {
                    System.arraycopy(this.buf, 0, array2, 0, blockSize);
                    this.cipher.processBlock(array2, 0, array2, 0);
                    System.arraycopy(array2, 0, array, n, n2);
                    System.arraycopy(this.buf, this.bufOff - n2, array3, 0, n2);
                    this.cipher.processBlock(array3, 0, array3, 0);
                    System.arraycopy(array3, 0, array, n + n2, blockSize);
                }
            }
            else {
                this.cipher.processBlock(this.buf, 0, array2, 0);
                System.arraycopy(array2, 0, array, n, blockSize);
            }
        }
        else {
            if (this.bufOff < blockSize) {
                throw new DataLengthException("need at least one block of input for CTS");
            }
            final byte[] array4 = new byte[blockSize];
            if (this.bufOff > blockSize) {
                if (this.type == 3 || (this.type == 2 && (this.buf.length - this.bufOff) % blockSize != 0)) {
                    if (this.cipher instanceof CBCBlockCipher) {
                        ((CBCBlockCipher)this.cipher).getUnderlyingCipher().processBlock(this.buf, 0, array2, 0);
                    }
                    else {
                        this.cipher.processBlock(this.buf, 0, array2, 0);
                    }
                    for (int i = blockSize; i != this.bufOff; ++i) {
                        array4[i - blockSize] = (byte)(array2[i - blockSize] ^ this.buf[i]);
                    }
                    System.arraycopy(this.buf, blockSize, array2, 0, n2);
                    this.cipher.processBlock(array2, 0, array, n);
                    System.arraycopy(array4, 0, array, n + blockSize, n2);
                }
                else {
                    ((CBCBlockCipher)this.cipher).getUnderlyingCipher().processBlock(this.buf, this.bufOff - blockSize, array4, 0);
                    System.arraycopy(this.buf, 0, array2, 0, blockSize);
                    if (n2 != blockSize) {
                        System.arraycopy(array4, n2, array2, n2, blockSize - n2);
                    }
                    this.cipher.processBlock(array2, 0, array2, 0);
                    System.arraycopy(array2, 0, array, n, blockSize);
                    for (int j = 0; j != n2; ++j) {
                        final byte[] array5 = array4;
                        final int n3 = j;
                        array5[n3] ^= this.buf[j];
                    }
                    System.arraycopy(array4, 0, array, n + blockSize, n2);
                }
            }
            else {
                this.cipher.processBlock(this.buf, 0, array2, 0);
                System.arraycopy(array2, 0, array, n, blockSize);
            }
        }
        final int bufOff = this.bufOff;
        this.reset();
        return bufOff;
    }
}
