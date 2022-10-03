package org.bouncycastle.crypto.modes;

import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.util.Pack;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.BufferedBlockCipher;

public class KXTSBlockCipher extends BufferedBlockCipher
{
    private static final long RED_POLY_128 = 135L;
    private static final long RED_POLY_256 = 1061L;
    private static final long RED_POLY_512 = 293L;
    private final int blockSize;
    private final long reductionPolynomial;
    private final long[] tw_init;
    private final long[] tw_current;
    private int counter;
    
    protected static long getReductionPolynomial(final int n) {
        switch (n) {
            case 16: {
                return 135L;
            }
            case 32: {
                return 1061L;
            }
            case 64: {
                return 293L;
            }
            default: {
                throw new IllegalArgumentException("Only 128, 256, and 512 -bit block sizes supported");
            }
        }
    }
    
    public KXTSBlockCipher(final BlockCipher cipher) {
        this.cipher = cipher;
        this.blockSize = cipher.getBlockSize();
        this.reductionPolynomial = getReductionPolynomial(this.blockSize);
        this.tw_init = new long[this.blockSize >>> 3];
        this.tw_current = new long[this.blockSize >>> 3];
        this.counter = -1;
    }
    
    @Override
    public int getOutputSize(final int n) {
        return n;
    }
    
    @Override
    public int getUpdateOutputSize(final int n) {
        return n;
    }
    
    @Override
    public void init(final boolean b, CipherParameters parameters) {
        if (!(parameters instanceof ParametersWithIV)) {
            throw new IllegalArgumentException("Invalid parameters passed");
        }
        final ParametersWithIV parametersWithIV = (ParametersWithIV)parameters;
        parameters = parametersWithIV.getParameters();
        final byte[] iv = parametersWithIV.getIV();
        if (iv.length != this.blockSize) {
            throw new IllegalArgumentException("Currently only support IVs of exactly one block");
        }
        final byte[] array = new byte[this.blockSize];
        System.arraycopy(iv, 0, array, 0, this.blockSize);
        this.cipher.init(true, parameters);
        this.cipher.processBlock(array, 0, array, 0);
        this.cipher.init(b, parameters);
        Pack.littleEndianToLong(array, 0, this.tw_init);
        System.arraycopy(this.tw_init, 0, this.tw_current, 0, this.tw_init.length);
        this.counter = 0;
    }
    
    @Override
    public int processByte(final byte b, final byte[] array, final int n) {
        throw new IllegalStateException("unsupported operation");
    }
    
    @Override
    public int processBytes(final byte[] array, final int n, final int n2, final byte[] array2, final int n3) {
        if (array.length - n < n2) {
            throw new DataLengthException("Input buffer too short");
        }
        if (array2.length - n < n2) {
            throw new OutputLengthException("Output buffer too short");
        }
        if (n2 % this.blockSize != 0) {
            throw new IllegalArgumentException("Partial blocks not supported");
        }
        for (int i = 0; i < n2; i += this.blockSize) {
            this.processBlock(array, n + i, array2, n3 + i);
        }
        return n2;
    }
    
    private void processBlock(final byte[] array, final int n, final byte[] array2, final int n2) {
        if (this.counter == -1) {
            throw new IllegalStateException("Attempt to process too many blocks");
        }
        ++this.counter;
        GF_double(this.reductionPolynomial, this.tw_current);
        final byte[] array3 = new byte[this.blockSize];
        Pack.longToLittleEndian(this.tw_current, array3, 0);
        final byte[] array4 = new byte[this.blockSize];
        System.arraycopy(array3, 0, array4, 0, this.blockSize);
        for (int i = 0; i < this.blockSize; ++i) {
            final byte[] array5 = array4;
            final int n3 = i;
            array5[n3] ^= array[n + i];
        }
        this.cipher.processBlock(array4, 0, array4, 0);
        for (int j = 0; j < this.blockSize; ++j) {
            array2[n2 + j] = (byte)(array4[j] ^ array3[j]);
        }
    }
    
    @Override
    public int doFinal(final byte[] array, final int n) {
        this.reset();
        return 0;
    }
    
    @Override
    public void reset() {
        this.cipher.reset();
        System.arraycopy(this.tw_init, 0, this.tw_current, 0, this.tw_init.length);
        this.counter = 0;
    }
    
    private static void GF_double(final long n, final long[] array) {
        long n2 = 0L;
        for (int i = 0; i < array.length; ++i) {
            final long n3 = array[i];
            final long n4 = n3 >>> 63;
            array[i] = (n3 << 1 ^ n2);
            n2 = n4;
        }
        final int n5 = 0;
        array[n5] ^= (n & -n2);
    }
}
