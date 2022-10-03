package org.bouncycastle.crypto.macs;

import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.engines.DESEngine;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.paddings.BlockCipherPadding;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.Mac;

public class ISO9797Alg3Mac implements Mac
{
    private byte[] mac;
    private byte[] buf;
    private int bufOff;
    private BlockCipher cipher;
    private BlockCipherPadding padding;
    private int macSize;
    private KeyParameter lastKey2;
    private KeyParameter lastKey3;
    
    public ISO9797Alg3Mac(final BlockCipher blockCipher) {
        this(blockCipher, blockCipher.getBlockSize() * 8, null);
    }
    
    public ISO9797Alg3Mac(final BlockCipher blockCipher, final BlockCipherPadding blockCipherPadding) {
        this(blockCipher, blockCipher.getBlockSize() * 8, blockCipherPadding);
    }
    
    public ISO9797Alg3Mac(final BlockCipher blockCipher, final int n) {
        this(blockCipher, n, null);
    }
    
    public ISO9797Alg3Mac(final BlockCipher blockCipher, final int n, final BlockCipherPadding padding) {
        if (n % 8 != 0) {
            throw new IllegalArgumentException("MAC size must be multiple of 8");
        }
        if (!(blockCipher instanceof DESEngine)) {
            throw new IllegalArgumentException("cipher must be instance of DESEngine");
        }
        this.cipher = new CBCBlockCipher(blockCipher);
        this.padding = padding;
        this.macSize = n / 8;
        this.mac = new byte[blockCipher.getBlockSize()];
        this.buf = new byte[blockCipher.getBlockSize()];
        this.bufOff = 0;
    }
    
    public String getAlgorithmName() {
        return "ISO9797Alg3";
    }
    
    public void init(final CipherParameters cipherParameters) {
        this.reset();
        if (!(cipherParameters instanceof KeyParameter) && !(cipherParameters instanceof ParametersWithIV)) {
            throw new IllegalArgumentException("params must be an instance of KeyParameter or ParametersWithIV");
        }
        KeyParameter keyParameter;
        if (cipherParameters instanceof KeyParameter) {
            keyParameter = (KeyParameter)cipherParameters;
        }
        else {
            keyParameter = (KeyParameter)((ParametersWithIV)cipherParameters).getParameters();
        }
        final byte[] key = keyParameter.getKey();
        KeyParameter lastKey3;
        if (key.length == 16) {
            lastKey3 = new KeyParameter(key, 0, 8);
            this.lastKey2 = new KeyParameter(key, 8, 8);
            this.lastKey3 = lastKey3;
        }
        else {
            if (key.length != 24) {
                throw new IllegalArgumentException("Key must be either 112 or 168 bit long");
            }
            lastKey3 = new KeyParameter(key, 0, 8);
            this.lastKey2 = new KeyParameter(key, 8, 8);
            this.lastKey3 = new KeyParameter(key, 16, 8);
        }
        if (cipherParameters instanceof ParametersWithIV) {
            this.cipher.init(true, new ParametersWithIV(lastKey3, ((ParametersWithIV)cipherParameters).getIV()));
        }
        else {
            this.cipher.init(true, lastKey3);
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
            if (this.bufOff == blockSize) {
                this.cipher.processBlock(this.buf, 0, this.mac, 0);
                this.bufOff = 0;
            }
            this.padding.addPadding(this.buf, this.bufOff);
        }
        this.cipher.processBlock(this.buf, 0, this.mac, 0);
        final DESEngine desEngine = new DESEngine();
        desEngine.init(false, this.lastKey2);
        desEngine.processBlock(this.mac, 0, this.mac, 0);
        desEngine.init(true, this.lastKey3);
        desEngine.processBlock(this.mac, 0, this.mac, 0);
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
