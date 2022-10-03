package org.bouncycastle.crypto;

public class BufferedAsymmetricBlockCipher
{
    protected byte[] buf;
    protected int bufOff;
    private final AsymmetricBlockCipher cipher;
    
    public BufferedAsymmetricBlockCipher(final AsymmetricBlockCipher cipher) {
        this.cipher = cipher;
    }
    
    public AsymmetricBlockCipher getUnderlyingCipher() {
        return this.cipher;
    }
    
    public int getBufferPosition() {
        return this.bufOff;
    }
    
    public void init(final boolean b, final CipherParameters cipherParameters) {
        this.reset();
        this.cipher.init(b, cipherParameters);
        this.buf = new byte[this.cipher.getInputBlockSize() + (b ? 1 : 0)];
        this.bufOff = 0;
    }
    
    public int getInputBlockSize() {
        return this.cipher.getInputBlockSize();
    }
    
    public int getOutputBlockSize() {
        return this.cipher.getOutputBlockSize();
    }
    
    public void processByte(final byte b) {
        if (this.bufOff >= this.buf.length) {
            throw new DataLengthException("attempt to process message too long for cipher");
        }
        this.buf[this.bufOff++] = b;
    }
    
    public void processBytes(final byte[] array, final int n, final int n2) {
        if (n2 == 0) {
            return;
        }
        if (n2 < 0) {
            throw new IllegalArgumentException("Can't have a negative input length!");
        }
        if (this.bufOff + n2 > this.buf.length) {
            throw new DataLengthException("attempt to process message too long for cipher");
        }
        System.arraycopy(array, n, this.buf, this.bufOff, n2);
        this.bufOff += n2;
    }
    
    public byte[] doFinal() throws InvalidCipherTextException {
        final byte[] processBlock = this.cipher.processBlock(this.buf, 0, this.bufOff);
        this.reset();
        return processBlock;
    }
    
    public void reset() {
        if (this.buf != null) {
            for (int i = 0; i < this.buf.length; ++i) {
                this.buf[i] = 0;
            }
        }
        this.bufOff = 0;
    }
}
