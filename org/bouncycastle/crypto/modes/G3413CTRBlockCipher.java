package org.bouncycastle.crypto.modes;

import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.StreamBlockCipher;

public class G3413CTRBlockCipher extends StreamBlockCipher
{
    private final int s;
    private byte[] CTR;
    private byte[] IV;
    private byte[] buf;
    private final int blockSize;
    private final BlockCipher cipher;
    private int byteCount;
    private boolean initialized;
    
    public G3413CTRBlockCipher(final BlockCipher blockCipher) {
        this(blockCipher, blockCipher.getBlockSize() * 8);
    }
    
    public G3413CTRBlockCipher(final BlockCipher cipher, final int n) {
        super(cipher);
        this.byteCount = 0;
        if (n < 0 || n > cipher.getBlockSize() * 8) {
            throw new IllegalArgumentException("Parameter bitBlockSize must be in range 0 < bitBlockSize <= " + cipher.getBlockSize() * 8);
        }
        this.cipher = cipher;
        this.blockSize = cipher.getBlockSize();
        this.s = n / 8;
        this.CTR = new byte[this.blockSize];
    }
    
    public void init(final boolean b, final CipherParameters cipherParameters) throws IllegalArgumentException {
        if (cipherParameters instanceof ParametersWithIV) {
            final ParametersWithIV parametersWithIV = (ParametersWithIV)cipherParameters;
            this.initArrays();
            this.IV = Arrays.clone(parametersWithIV.getIV());
            if (this.IV.length != this.blockSize / 2) {
                throw new IllegalArgumentException("Parameter IV length must be == blockSize/2");
            }
            System.arraycopy(this.IV, 0, this.CTR, 0, this.IV.length);
            for (int i = this.IV.length; i < this.blockSize; ++i) {
                this.CTR[i] = 0;
            }
            if (parametersWithIV.getParameters() != null) {
                this.cipher.init(true, parametersWithIV.getParameters());
            }
        }
        else {
            this.initArrays();
            if (cipherParameters != null) {
                this.cipher.init(true, cipherParameters);
            }
        }
        this.initialized = true;
    }
    
    private void initArrays() {
        this.IV = new byte[this.blockSize / 2];
        this.CTR = new byte[this.blockSize];
        this.buf = new byte[this.s];
    }
    
    public String getAlgorithmName() {
        return this.cipher.getAlgorithmName() + "/GCTR";
    }
    
    public int getBlockSize() {
        return this.s;
    }
    
    public int processBlock(final byte[] array, final int n, final byte[] array2, final int n2) throws DataLengthException, IllegalStateException {
        this.processBytes(array, n, this.s, array2, n2);
        return this.s;
    }
    
    @Override
    protected byte calculateByte(final byte b) {
        if (this.byteCount == 0) {
            this.buf = this.generateBuf();
        }
        final byte b2 = (byte)(this.buf[this.byteCount] ^ b);
        ++this.byteCount;
        if (this.byteCount == this.s) {
            this.byteCount = 0;
            this.generateCRT();
        }
        return b2;
    }
    
    private void generateCRT() {
        final byte[] ctr = this.CTR;
        final int n = this.CTR.length - 1;
        ++ctr[n];
    }
    
    private byte[] generateBuf() {
        final byte[] array = new byte[this.CTR.length];
        this.cipher.processBlock(this.CTR, 0, array, 0);
        return GOST3413CipherUtil.MSB(array, this.s);
    }
    
    public void reset() {
        if (this.initialized) {
            System.arraycopy(this.IV, 0, this.CTR, 0, this.IV.length);
            for (int i = this.IV.length; i < this.blockSize; ++i) {
                this.CTR[i] = 0;
            }
            this.byteCount = 0;
            this.cipher.reset();
        }
    }
}
