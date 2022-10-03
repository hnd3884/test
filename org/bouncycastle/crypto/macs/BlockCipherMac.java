package org.bouncycastle.crypto.macs;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.Mac;

public class BlockCipherMac implements Mac
{
    private byte[] mac;
    private byte[] buf;
    private int bufOff;
    private BlockCipher cipher;
    private int macSize;
    
    @Deprecated
    public BlockCipherMac(final BlockCipher blockCipher) {
        this(blockCipher, blockCipher.getBlockSize() * 8 / 2);
    }
    
    @Deprecated
    public BlockCipherMac(final BlockCipher blockCipher, final int n) {
        if (n % 8 != 0) {
            throw new IllegalArgumentException("MAC size must be multiple of 8");
        }
        this.cipher = new CBCBlockCipher(blockCipher);
        this.macSize = n / 8;
        this.mac = new byte[blockCipher.getBlockSize()];
        this.buf = new byte[blockCipher.getBlockSize()];
        this.bufOff = 0;
    }
    
    public String getAlgorithmName() {
        return this.cipher.getAlgorithmName();
    }
    
    public void init(final CipherParameters cipherParameters) {
        this.reset();
        this.cipher.init(true, cipherParameters);
    }
    
    public int getMacSize() {
        return this.macSize;
    }
    
    public void update(final byte b) {
        if (this.bufOff == this.buf.length) {
            this.cipher.processBlock(this.buf, 0, this.mac, 0);
            this.bufOff = 0;
        }
        this.buf[this.bufOff++] = b;
    }
    
    public void update(final byte[] array, int n, int i) {
        if (i < 0) {
            throw new IllegalArgumentException("Can't have a negative input length!");
        }
        final int blockSize = this.cipher.getBlockSize();
        final int n2 = 0;
        final int n3 = blockSize - this.bufOff;
        if (i > n3) {
            System.arraycopy(array, n, this.buf, this.bufOff, n3);
            int n4 = n2 + this.cipher.processBlock(this.buf, 0, this.mac, 0);
            this.bufOff = 0;
            for (i -= n3, n += n3; i > blockSize; i -= blockSize, n += blockSize) {
                n4 += this.cipher.processBlock(array, n, this.mac, 0);
            }
        }
        System.arraycopy(array, n, this.buf, this.bufOff, i);
        this.bufOff += i;
    }
    
    public int doFinal(final byte[] array, final int n) {
        while (this.bufOff < this.cipher.getBlockSize()) {
            this.buf[this.bufOff] = 0;
            ++this.bufOff;
        }
        this.cipher.processBlock(this.buf, 0, this.mac, 0);
        System.arraycopy(this.mac, 0, array, n, this.macSize);
        this.reset();
        return this.macSize;
    }
    
    public void reset() {
        for (int i = 0; i < this.buf.length; ++i) {
            this.buf[i] = 0;
        }
        this.bufOff = 0;
        this.cipher.reset();
    }
}
