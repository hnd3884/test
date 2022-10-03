package org.bouncycastle.crypto.paddings;

import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.DataLengthException;
import java.security.SecureRandom;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.BufferedBlockCipher;

public class PaddedBufferedBlockCipher extends BufferedBlockCipher
{
    BlockCipherPadding padding;
    
    public PaddedBufferedBlockCipher(final BlockCipher cipher, final BlockCipherPadding padding) {
        this.cipher = cipher;
        this.padding = padding;
        this.buf = new byte[cipher.getBlockSize()];
        this.bufOff = 0;
    }
    
    public PaddedBufferedBlockCipher(final BlockCipher blockCipher) {
        this(blockCipher, new PKCS7Padding());
    }
    
    @Override
    public void init(final boolean forEncryption, final CipherParameters cipherParameters) throws IllegalArgumentException {
        this.forEncryption = forEncryption;
        this.reset();
        if (cipherParameters instanceof ParametersWithRandom) {
            final ParametersWithRandom parametersWithRandom = (ParametersWithRandom)cipherParameters;
            this.padding.init(parametersWithRandom.getRandom());
            this.cipher.init(forEncryption, parametersWithRandom.getParameters());
        }
        else {
            this.padding.init(null);
            this.cipher.init(forEncryption, cipherParameters);
        }
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
            return Math.max(0, n2 - this.buf.length);
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
        int processBlock2;
        if (this.forEncryption) {
            if (this.bufOff == blockSize) {
                if (n + 2 * blockSize > array.length) {
                    this.reset();
                    throw new OutputLengthException("output buffer too short");
                }
                processBlock = this.cipher.processBlock(this.buf, 0, array, n);
                this.bufOff = 0;
            }
            this.padding.addPadding(this.buf, this.bufOff);
            processBlock2 = processBlock + this.cipher.processBlock(this.buf, 0, array, n + processBlock);
            this.reset();
        }
        else {
            if (this.bufOff != blockSize) {
                this.reset();
                throw new DataLengthException("last block incomplete in decryption");
            }
            processBlock2 = this.cipher.processBlock(this.buf, 0, this.buf, 0);
            this.bufOff = 0;
            try {
                processBlock2 -= this.padding.padCount(this.buf);
                System.arraycopy(this.buf, 0, array, n, processBlock2);
            }
            finally {
                this.reset();
            }
        }
        return processBlock2;
    }
}
