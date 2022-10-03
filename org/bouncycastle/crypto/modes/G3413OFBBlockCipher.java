package org.bouncycastle.crypto.modes;

import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.StreamBlockCipher;

public class G3413OFBBlockCipher extends StreamBlockCipher
{
    private int m;
    private int blockSize;
    private byte[] R;
    private byte[] R_init;
    private byte[] Y;
    private BlockCipher cipher;
    private int byteCount;
    private boolean initialized;
    
    public G3413OFBBlockCipher(final BlockCipher cipher) {
        super(cipher);
        this.initialized = false;
        this.blockSize = cipher.getBlockSize();
        this.cipher = cipher;
        this.Y = new byte[this.blockSize];
    }
    
    public void init(final boolean b, final CipherParameters cipherParameters) throws IllegalArgumentException {
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
        return this.cipher.getAlgorithmName() + "/OFB";
    }
    
    public int getBlockSize() {
        return this.blockSize;
    }
    
    public int processBlock(final byte[] array, final int n, final byte[] array2, final int n2) throws DataLengthException, IllegalStateException {
        this.processBytes(array, n, this.blockSize, array2, n2);
        return this.blockSize;
    }
    
    @Override
    protected byte calculateByte(final byte b) {
        if (this.byteCount == 0) {
            this.generateY();
        }
        final byte b2 = (byte)(this.Y[this.byteCount] ^ b);
        ++this.byteCount;
        if (this.byteCount == this.getBlockSize()) {
            this.byteCount = 0;
            this.generateR();
        }
        return b2;
    }
    
    private void generateY() {
        this.cipher.processBlock(GOST3413CipherUtil.MSB(this.R, this.blockSize), 0, this.Y, 0);
    }
    
    private void generateR() {
        final byte[] lsb = GOST3413CipherUtil.LSB(this.R, this.m - this.blockSize);
        System.arraycopy(lsb, 0, this.R, 0, lsb.length);
        System.arraycopy(this.Y, 0, this.R, lsb.length, this.m - lsb.length);
    }
    
    public void reset() {
        if (this.initialized) {
            System.arraycopy(this.R_init, 0, this.R, 0, this.R_init.length);
            Arrays.clear(this.Y);
            this.byteCount = 0;
            this.cipher.reset();
        }
    }
}
