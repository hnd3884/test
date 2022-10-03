package org.bouncycastle.crypto.modes;

import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.StreamBlockCipher;

public class G3413CFBBlockCipher extends StreamBlockCipher
{
    private final int s;
    private int m;
    private int blockSize;
    private byte[] R;
    private byte[] R_init;
    private BlockCipher cipher;
    private boolean forEncryption;
    private boolean initialized;
    private byte[] gamma;
    private byte[] inBuf;
    private int byteCount;
    
    public G3413CFBBlockCipher(final BlockCipher blockCipher) {
        this(blockCipher, blockCipher.getBlockSize() * 8);
    }
    
    public G3413CFBBlockCipher(final BlockCipher cipher, final int n) {
        super(cipher);
        this.initialized = false;
        if (n < 0 || n > cipher.getBlockSize() * 8) {
            throw new IllegalArgumentException("Parameter bitBlockSize must be in range 0 < bitBlockSize <= " + cipher.getBlockSize() * 8);
        }
        this.blockSize = cipher.getBlockSize();
        this.cipher = cipher;
        this.s = n / 8;
        this.inBuf = new byte[this.getBlockSize()];
    }
    
    public void init(final boolean forEncryption, final CipherParameters cipherParameters) throws IllegalArgumentException {
        this.forEncryption = forEncryption;
        if (cipherParameters instanceof ParametersWithIV) {
            final ParametersWithIV parametersWithIV = (ParametersWithIV)cipherParameters;
            final byte[] iv = parametersWithIV.getIV();
            if (iv.length < this.blockSize) {
                throw new IllegalArgumentException("Parameter m must blockSize <= m");
            }
            this.m = iv.length;
            this.initArrays();
            System.arraycopy(this.R_init = Arrays.clone(iv), 0, this.R, 0, this.R_init.length);
            if (parametersWithIV.getParameters() != null) {
                this.cipher.init(true, parametersWithIV.getParameters());
            }
        }
        else {
            this.setupDefaultParams();
            this.initArrays();
            System.arraycopy(this.R_init, 0, this.R, 0, this.R_init.length);
            if (cipherParameters != null) {
                this.cipher.init(true, cipherParameters);
            }
        }
        this.initialized = true;
    }
    
    private void initArrays() {
        this.R = new byte[this.m];
        this.R_init = new byte[this.m];
    }
    
    private void setupDefaultParams() {
        this.m = 2 * this.blockSize;
    }
    
    public String getAlgorithmName() {
        return this.cipher.getAlgorithmName() + "/CFB" + this.blockSize * 8;
    }
    
    public int getBlockSize() {
        return this.s;
    }
    
    public int processBlock(final byte[] array, final int n, final byte[] array2, final int n2) throws DataLengthException, IllegalStateException {
        this.processBytes(array, n, this.getBlockSize(), array2, n2);
        return this.getBlockSize();
    }
    
    @Override
    protected byte calculateByte(final byte b) {
        if (this.byteCount == 0) {
            this.gamma = this.createGamma();
        }
        final byte b2 = (byte)(this.gamma[this.byteCount] ^ b);
        this.inBuf[this.byteCount++] = (this.forEncryption ? b2 : b);
        if (this.byteCount == this.getBlockSize()) {
            this.byteCount = 0;
            this.generateR(this.inBuf);
        }
        return b2;
    }
    
    byte[] createGamma() {
        final byte[] msb = GOST3413CipherUtil.MSB(this.R, this.blockSize);
        final byte[] array = new byte[msb.length];
        this.cipher.processBlock(msb, 0, array, 0);
        return GOST3413CipherUtil.MSB(array, this.s);
    }
    
    void generateR(final byte[] array) {
        final byte[] lsb = GOST3413CipherUtil.LSB(this.R, this.m - this.s);
        System.arraycopy(lsb, 0, this.R, 0, lsb.length);
        System.arraycopy(array, 0, this.R, lsb.length, this.m - lsb.length);
    }
    
    public void reset() {
        this.byteCount = 0;
        Arrays.clear(this.inBuf);
        Arrays.clear(this.gamma);
        if (this.initialized) {
            System.arraycopy(this.R_init, 0, this.R, 0, this.R_init.length);
            this.cipher.reset();
        }
    }
}
