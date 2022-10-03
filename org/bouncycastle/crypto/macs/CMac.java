package org.bouncycastle.crypto.macs;

import org.bouncycastle.crypto.paddings.ISO7816d4Padding;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.util.Pack;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.Mac;

public class CMac implements Mac
{
    private byte[] poly;
    private byte[] ZEROES;
    private byte[] mac;
    private byte[] buf;
    private int bufOff;
    private BlockCipher cipher;
    private int macSize;
    private byte[] Lu;
    private byte[] Lu2;
    
    public CMac(final BlockCipher blockCipher) {
        this(blockCipher, blockCipher.getBlockSize() * 8);
    }
    
    public CMac(final BlockCipher blockCipher, final int n) {
        if (n % 8 != 0) {
            throw new IllegalArgumentException("MAC size must be multiple of 8");
        }
        if (n > blockCipher.getBlockSize() * 8) {
            throw new IllegalArgumentException("MAC size must be less or equal to " + blockCipher.getBlockSize() * 8);
        }
        this.cipher = new CBCBlockCipher(blockCipher);
        this.macSize = n / 8;
        this.poly = lookupPoly(blockCipher.getBlockSize());
        this.mac = new byte[blockCipher.getBlockSize()];
        this.buf = new byte[blockCipher.getBlockSize()];
        this.ZEROES = new byte[blockCipher.getBlockSize()];
        this.bufOff = 0;
    }
    
    public String getAlgorithmName() {
        return this.cipher.getAlgorithmName();
    }
    
    private static int shiftLeft(final byte[] array, final byte[] array2) {
        int length = array.length;
        int n = 0;
        while (--length >= 0) {
            final int n2 = array[length] & 0xFF;
            array2[length] = (byte)(n2 << 1 | n);
            n = (n2 >>> 7 & 0x1);
        }
        return n;
    }
    
    private byte[] doubleLu(final byte[] array) {
        final byte[] array2 = new byte[array.length];
        final int n = -shiftLeft(array, array2) & 0xFF;
        final byte[] array3 = array2;
        final int n2 = array.length - 3;
        array3[n2] ^= (byte)(this.poly[1] & n);
        final byte[] array4 = array2;
        final int n3 = array.length - 2;
        array4[n3] ^= (byte)(this.poly[2] & n);
        final byte[] array5 = array2;
        final int n4 = array.length - 1;
        array5[n4] ^= (byte)(this.poly[3] & n);
        return array2;
    }
    
    private static byte[] lookupPoly(final int n) {
        int n2 = 0;
        switch (n * 8) {
            case 64: {
                n2 = 27;
                break;
            }
            case 128: {
                n2 = 135;
                break;
            }
            case 160: {
                n2 = 45;
                break;
            }
            case 192: {
                n2 = 135;
                break;
            }
            case 224: {
                n2 = 777;
                break;
            }
            case 256: {
                n2 = 1061;
                break;
            }
            case 320: {
                n2 = 27;
                break;
            }
            case 384: {
                n2 = 4109;
                break;
            }
            case 448: {
                n2 = 2129;
                break;
            }
            case 512: {
                n2 = 293;
                break;
            }
            case 768: {
                n2 = 655377;
                break;
            }
            case 1024: {
                n2 = 524355;
                break;
            }
            case 2048: {
                n2 = 548865;
                break;
            }
            default: {
                throw new IllegalArgumentException("Unknown block size for CMAC: " + n * 8);
            }
        }
        return Pack.intToBigEndian(n2);
    }
    
    public void init(final CipherParameters cipherParameters) {
        this.validate(cipherParameters);
        this.cipher.init(true, cipherParameters);
        final byte[] array = new byte[this.ZEROES.length];
        this.cipher.processBlock(this.ZEROES, 0, array, 0);
        this.Lu = this.doubleLu(array);
        this.Lu2 = this.doubleLu(this.Lu);
        this.reset();
    }
    
    void validate(final CipherParameters cipherParameters) {
        if (cipherParameters != null && !(cipherParameters instanceof KeyParameter)) {
            throw new IllegalArgumentException("CMac mode only permits key to be set.");
        }
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
        final int n2 = blockSize - this.bufOff;
        if (i > n2) {
            System.arraycopy(array, n, this.buf, this.bufOff, n2);
            this.cipher.processBlock(this.buf, 0, this.mac, 0);
            this.bufOff = 0;
            for (i -= n2, n += n2; i > blockSize; i -= blockSize, n += blockSize) {
                this.cipher.processBlock(array, n, this.mac, 0);
            }
        }
        System.arraycopy(array, n, this.buf, this.bufOff, i);
        this.bufOff += i;
    }
    
    public int doFinal(final byte[] array, final int n) {
        byte[] array2;
        if (this.bufOff == this.cipher.getBlockSize()) {
            array2 = this.Lu;
        }
        else {
            new ISO7816d4Padding().addPadding(this.buf, this.bufOff);
            array2 = this.Lu2;
        }
        for (int i = 0; i < this.mac.length; ++i) {
            final byte[] buf = this.buf;
            final int n2 = i;
            buf[n2] ^= array2[i];
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
