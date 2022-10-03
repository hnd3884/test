package org.bouncycastle.crypto;

public class BufferedBlockCipher
{
    protected byte[] buf;
    protected int bufOff;
    protected boolean forEncryption;
    protected BlockCipher cipher;
    protected boolean partialBlockOkay;
    protected boolean pgpCFB;
    
    protected BufferedBlockCipher() {
    }
    
    public BufferedBlockCipher(final BlockCipher cipher) {
        this.cipher = cipher;
        this.buf = new byte[cipher.getBlockSize()];
        this.bufOff = 0;
        final String algorithmName = cipher.getAlgorithmName();
        final int n = algorithmName.indexOf(47) + 1;
        this.pgpCFB = (n > 0 && algorithmName.startsWith("PGP", n));
        if (this.pgpCFB || cipher instanceof StreamCipher) {
            this.partialBlockOkay = true;
        }
        else {
            this.partialBlockOkay = (n > 0 && algorithmName.startsWith("OpenPGP", n));
        }
    }
    
    public BlockCipher getUnderlyingCipher() {
        return this.cipher;
    }
    
    public void init(final boolean forEncryption, final CipherParameters cipherParameters) throws IllegalArgumentException {
        this.forEncryption = forEncryption;
        this.reset();
        this.cipher.init(forEncryption, cipherParameters);
    }
    
    public int getBlockSize() {
        return this.cipher.getBlockSize();
    }
    
    public int getUpdateOutputSize(final int n) {
        final int n2 = n + this.bufOff;
        int n3;
        if (this.pgpCFB) {
            if (this.forEncryption) {
                n3 = n2 % this.buf.length - (this.cipher.getBlockSize() + 2);
            }
            else {
                n3 = n2 % this.buf.length;
            }
        }
        else {
            n3 = n2 % this.buf.length;
        }
        return n2 - n3;
    }
    
    public int getOutputSize(final int n) {
        return n + this.bufOff;
    }
    
    public int processByte(final byte b, final byte[] array, final int n) throws DataLengthException, IllegalStateException {
        int processBlock = 0;
        this.buf[this.bufOff++] = b;
        if (this.bufOff == this.buf.length) {
            processBlock = this.cipher.processBlock(this.buf, 0, array, n);
            this.bufOff = 0;
        }
        return processBlock;
    }
    
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
        if (this.bufOff == this.buf.length) {
            n3 += this.cipher.processBlock(this.buf, 0, array2, n2 + n3);
            this.bufOff = 0;
        }
        return n3;
    }
    
    public int doFinal(final byte[] array, final int n) throws DataLengthException, IllegalStateException, InvalidCipherTextException {
        try {
            int bufOff = 0;
            if (n + this.bufOff > array.length) {
                throw new OutputLengthException("output buffer too short for doFinal()");
            }
            if (this.bufOff != 0) {
                if (!this.partialBlockOkay) {
                    throw new DataLengthException("data not block size aligned");
                }
                this.cipher.processBlock(this.buf, 0, this.buf, 0);
                bufOff = this.bufOff;
                this.bufOff = 0;
                System.arraycopy(this.buf, 0, array, n, bufOff);
            }
            return bufOff;
        }
        finally {
            this.reset();
        }
    }
    
    public void reset() {
        for (int i = 0; i < this.buf.length; ++i) {
            this.buf[i] = 0;
        }
        this.bufOff = 0;
        this.cipher.reset();
    }
}
