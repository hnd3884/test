package org.bouncycastle.crypto.modes;

import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.BlockCipher;

public class G3413CBCBlockCipher implements BlockCipher
{
    private int m;
    private int blockSize;
    private byte[] R;
    private byte[] R_init;
    private BlockCipher cipher;
    private boolean initialized;
    private boolean forEncryption;
    
    public G3413CBCBlockCipher(final BlockCipher cipher) {
        this.initialized = false;
        this.blockSize = cipher.getBlockSize();
        this.cipher = cipher;
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
                this.cipher.init(forEncryption, parametersWithIV.getParameters());
            }
        }
        else {
            this.setupDefaultParams();
            this.initArrays();
            System.arraycopy(this.R_init, 0, this.R, 0, this.R_init.length);
            if (cipherParameters != null) {
                this.cipher.init(forEncryption, cipherParameters);
            }
        }
        this.initialized = true;
    }
    
    private void initArrays() {
        this.R = new byte[this.m];
        this.R_init = new byte[this.m];
    }
    
    private void setupDefaultParams() {
        this.m = this.blockSize;
    }
    
    public String getAlgorithmName() {
        return this.cipher.getAlgorithmName() + "/CBC";
    }
    
    public int getBlockSize() {
        return this.blockSize;
    }
    
    public int processBlock(final byte[] array, final int n, final byte[] array2, final int n2) throws DataLengthException, IllegalStateException {
        return this.forEncryption ? this.encrypt(array, n, array2, n2) : this.decrypt(array, n, array2, n2);
    }
    
    private int encrypt(final byte[] array, final int n, final byte[] array2, final int n2) {
        final byte[] sum = GOST3413CipherUtil.sum(GOST3413CipherUtil.copyFromInput(array, this.blockSize, n), GOST3413CipherUtil.MSB(this.R, this.blockSize));
        final byte[] array3 = new byte[sum.length];
        this.cipher.processBlock(sum, 0, array3, 0);
        System.arraycopy(array3, 0, array2, n2, array3.length);
        if (array2.length > n2 + sum.length) {
            this.generateR(array3);
        }
        return array3.length;
    }
    
    private int decrypt(final byte[] array, final int n, final byte[] array2, final int n2) {
        final byte[] msb = GOST3413CipherUtil.MSB(this.R, this.blockSize);
        final byte[] copyFromInput = GOST3413CipherUtil.copyFromInput(array, this.blockSize, n);
        final byte[] array3 = new byte[copyFromInput.length];
        this.cipher.processBlock(copyFromInput, 0, array3, 0);
        final byte[] sum = GOST3413CipherUtil.sum(array3, msb);
        System.arraycopy(sum, 0, array2, n2, sum.length);
        if (array2.length > n2 + sum.length) {
            this.generateR(copyFromInput);
        }
        return sum.length;
    }
    
    private void generateR(final byte[] array) {
        final byte[] lsb = GOST3413CipherUtil.LSB(this.R, this.m - this.blockSize);
        System.arraycopy(lsb, 0, this.R, 0, lsb.length);
        System.arraycopy(array, 0, this.R, lsb.length, this.m - lsb.length);
    }
    
    public void reset() {
        if (this.initialized) {
            System.arraycopy(this.R_init, 0, this.R, 0, this.R_init.length);
            this.cipher.reset();
        }
    }
}
