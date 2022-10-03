package org.bouncycastle.crypto.macs;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.paddings.BlockCipherPadding;
import org.bouncycastle.crypto.Mac;

public class CFBBlockCipherMac implements Mac
{
    private byte[] mac;
    private byte[] buf;
    private int bufOff;
    private MacCFBBlockCipher cipher;
    private BlockCipherPadding padding;
    private int macSize;
    
    public CFBBlockCipherMac(final BlockCipher blockCipher) {
        this(blockCipher, 8, blockCipher.getBlockSize() * 8 / 2, null);
    }
    
    public CFBBlockCipherMac(final BlockCipher blockCipher, final BlockCipherPadding blockCipherPadding) {
        this(blockCipher, 8, blockCipher.getBlockSize() * 8 / 2, blockCipherPadding);
    }
    
    public CFBBlockCipherMac(final BlockCipher blockCipher, final int n, final int n2) {
        this(blockCipher, n, n2, null);
    }
    
    public CFBBlockCipherMac(final BlockCipher blockCipher, final int n, final int n2, final BlockCipherPadding padding) {
        this.padding = null;
        if (n2 % 8 != 0) {
            throw new IllegalArgumentException("MAC size must be multiple of 8");
        }
        this.mac = new byte[blockCipher.getBlockSize()];
        this.cipher = new MacCFBBlockCipher(blockCipher, n);
        this.padding = padding;
        this.macSize = n2 / 8;
        this.buf = new byte[this.cipher.getBlockSize()];
        this.bufOff = 0;
    }
    
    public String getAlgorithmName() {
        return this.cipher.getAlgorithmName();
    }
    
    public void init(final CipherParameters cipherParameters) {
        this.reset();
        this.cipher.init(cipherParameters);
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
        final int blockSize = this.cipher.getBlockSize();
        if (this.padding == null) {
            while (this.bufOff < blockSize) {
                this.buf[this.bufOff] = 0;
                ++this.bufOff;
            }
        }
        else {
            this.padding.addPadding(this.buf, this.bufOff);
        }
        this.cipher.processBlock(this.buf, 0, this.mac, 0);
        this.cipher.getMacBlock(this.mac);
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
