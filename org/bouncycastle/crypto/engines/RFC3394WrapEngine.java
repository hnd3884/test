package org.bouncycastle.crypto.engines;

import org.bouncycastle.util.Arrays;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.Wrapper;

public class RFC3394WrapEngine implements Wrapper
{
    private BlockCipher engine;
    private boolean wrapCipherMode;
    private KeyParameter param;
    private boolean forWrapping;
    private byte[] iv;
    
    public RFC3394WrapEngine(final BlockCipher blockCipher) {
        this(blockCipher, false);
    }
    
    public RFC3394WrapEngine(final BlockCipher engine, final boolean b) {
        this.iv = new byte[] { -90, -90, -90, -90, -90, -90, -90, -90 };
        this.engine = engine;
        this.wrapCipherMode = !b;
    }
    
    public void init(final boolean forWrapping, CipherParameters parameters) {
        this.forWrapping = forWrapping;
        if (parameters instanceof ParametersWithRandom) {
            parameters = ((ParametersWithRandom)parameters).getParameters();
        }
        if (parameters instanceof KeyParameter) {
            this.param = (KeyParameter)parameters;
        }
        else if (parameters instanceof ParametersWithIV) {
            this.iv = ((ParametersWithIV)parameters).getIV();
            this.param = (KeyParameter)((ParametersWithIV)parameters).getParameters();
            if (this.iv.length != 8) {
                throw new IllegalArgumentException("IV not equal to 8");
            }
        }
    }
    
    public String getAlgorithmName() {
        return this.engine.getAlgorithmName();
    }
    
    public byte[] wrap(final byte[] array, final int n, final int n2) {
        if (!this.forWrapping) {
            throw new IllegalStateException("not set for wrapping");
        }
        final int n3 = n2 / 8;
        if (n3 * 8 != n2) {
            throw new DataLengthException("wrap data must be a multiple of 8 bytes");
        }
        final byte[] array2 = new byte[n2 + this.iv.length];
        final byte[] array3 = new byte[8 + this.iv.length];
        System.arraycopy(this.iv, 0, array2, 0, this.iv.length);
        System.arraycopy(array, n, array2, this.iv.length, n2);
        this.engine.init(this.wrapCipherMode, this.param);
        for (int i = 0; i != 6; ++i) {
            for (int j = 1; j <= n3; ++j) {
                System.arraycopy(array2, 0, array3, 0, this.iv.length);
                System.arraycopy(array2, 8 * j, array3, this.iv.length, 8);
                this.engine.processBlock(array3, 0, array3, 0);
                for (int k = n3 * i + j, n4 = 1; k != 0; k >>>= 8, ++n4) {
                    final byte b = (byte)k;
                    final byte[] array4 = array3;
                    final int n5 = this.iv.length - n4;
                    array4[n5] ^= b;
                }
                System.arraycopy(array3, 0, array2, 0, 8);
                System.arraycopy(array3, 8, array2, 8 * j, 8);
            }
        }
        return array2;
    }
    
    public byte[] unwrap(final byte[] array, final int n, final int n2) throws InvalidCipherTextException {
        if (this.forWrapping) {
            throw new IllegalStateException("not set for unwrapping");
        }
        final int n3 = n2 / 8;
        if (n3 * 8 != n2) {
            throw new InvalidCipherTextException("unwrap data must be a multiple of 8 bytes");
        }
        final byte[] array2 = new byte[n2 - this.iv.length];
        final byte[] array3 = new byte[this.iv.length];
        final byte[] array4 = new byte[8 + this.iv.length];
        System.arraycopy(array, n, array3, 0, this.iv.length);
        System.arraycopy(array, n + this.iv.length, array2, 0, n2 - this.iv.length);
        this.engine.init(!this.wrapCipherMode, this.param);
        final int n4 = n3 - 1;
        for (int i = 5; i >= 0; --i) {
            for (int j = n4; j >= 1; --j) {
                System.arraycopy(array3, 0, array4, 0, this.iv.length);
                System.arraycopy(array2, 8 * (j - 1), array4, this.iv.length, 8);
                for (int k = n4 * i + j, n5 = 1; k != 0; k >>>= 8, ++n5) {
                    final byte b = (byte)k;
                    final byte[] array5 = array4;
                    final int n6 = this.iv.length - n5;
                    array5[n6] ^= b;
                }
                this.engine.processBlock(array4, 0, array4, 0);
                System.arraycopy(array4, 0, array3, 0, 8);
                System.arraycopy(array4, 8, array2, 8 * (j - 1), 8);
            }
        }
        if (!Arrays.constantTimeAreEqual(array3, this.iv)) {
            throw new InvalidCipherTextException("checksum failed");
        }
        return array2;
    }
}
