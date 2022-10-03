package org.bouncycastle.crypto.engines;

import org.bouncycastle.util.Arrays;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.util.Pack;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.Wrapper;

public class RFC5649WrapEngine implements Wrapper
{
    private BlockCipher engine;
    private KeyParameter param;
    private boolean forWrapping;
    private byte[] highOrderIV;
    private byte[] preIV;
    private byte[] extractedAIV;
    
    public RFC5649WrapEngine(final BlockCipher engine) {
        this.highOrderIV = new byte[] { -90, 89, 89, -90 };
        this.preIV = this.highOrderIV;
        this.extractedAIV = null;
        this.engine = engine;
    }
    
    public void init(final boolean forWrapping, CipherParameters parameters) {
        this.forWrapping = forWrapping;
        if (parameters instanceof ParametersWithRandom) {
            parameters = ((ParametersWithRandom)parameters).getParameters();
        }
        if (parameters instanceof KeyParameter) {
            this.param = (KeyParameter)parameters;
            this.preIV = this.highOrderIV;
        }
        else if (parameters instanceof ParametersWithIV) {
            this.preIV = ((ParametersWithIV)parameters).getIV();
            this.param = (KeyParameter)((ParametersWithIV)parameters).getParameters();
            if (this.preIV.length != 4) {
                throw new IllegalArgumentException("IV length not equal to 4");
            }
        }
    }
    
    public String getAlgorithmName() {
        return this.engine.getAlgorithmName();
    }
    
    private byte[] padPlaintext(final byte[] array) {
        final int length = array.length;
        final int n = (8 - length % 8) % 8;
        final byte[] array2 = new byte[length + n];
        System.arraycopy(array, 0, array2, 0, length);
        if (n != 0) {
            System.arraycopy(new byte[n], 0, array2, length, n);
        }
        return array2;
    }
    
    public byte[] wrap(final byte[] array, final int n, final int n2) {
        if (!this.forWrapping) {
            throw new IllegalStateException("not set for wrapping");
        }
        final byte[] array2 = new byte[8];
        final byte[] intToBigEndian = Pack.intToBigEndian(n2);
        System.arraycopy(this.preIV, 0, array2, 0, this.preIV.length);
        System.arraycopy(intToBigEndian, 0, array2, this.preIV.length, intToBigEndian.length);
        final byte[] array3 = new byte[n2];
        System.arraycopy(array, n, array3, 0, n2);
        final byte[] padPlaintext = this.padPlaintext(array3);
        if (padPlaintext.length == 8) {
            final byte[] array4 = new byte[padPlaintext.length + array2.length];
            System.arraycopy(array2, 0, array4, 0, array2.length);
            System.arraycopy(padPlaintext, 0, array4, array2.length, padPlaintext.length);
            this.engine.init(true, this.param);
            for (int i = 0; i < array4.length; i += this.engine.getBlockSize()) {
                this.engine.processBlock(array4, i, array4, i);
            }
            return array4;
        }
        final RFC3394WrapEngine rfc3394WrapEngine = new RFC3394WrapEngine(this.engine);
        rfc3394WrapEngine.init(true, new ParametersWithIV(this.param, array2));
        return rfc3394WrapEngine.wrap(padPlaintext, 0, padPlaintext.length);
    }
    
    public byte[] unwrap(final byte[] array, final int n, final int n2) throws InvalidCipherTextException {
        if (this.forWrapping) {
            throw new IllegalStateException("not set for unwrapping");
        }
        final int n3 = n2 / 8;
        if (n3 * 8 != n2) {
            throw new InvalidCipherTextException("unwrap data must be a multiple of 8 bytes");
        }
        if (n3 == 1) {
            throw new InvalidCipherTextException("unwrap data must be at least 16 bytes");
        }
        final byte[] array2 = new byte[n2];
        System.arraycopy(array, n, array2, 0, n2);
        final byte[] array3 = new byte[n2];
        byte[] rfc3394UnwrapNoIvCheck;
        if (n3 == 2) {
            this.engine.init(false, this.param);
            for (int i = 0; i < array2.length; i += this.engine.getBlockSize()) {
                this.engine.processBlock(array2, i, array3, i);
            }
            System.arraycopy(array3, 0, this.extractedAIV = new byte[8], 0, this.extractedAIV.length);
            rfc3394UnwrapNoIvCheck = new byte[array3.length - this.extractedAIV.length];
            System.arraycopy(array3, this.extractedAIV.length, rfc3394UnwrapNoIvCheck, 0, rfc3394UnwrapNoIvCheck.length);
        }
        else {
            rfc3394UnwrapNoIvCheck = this.rfc3394UnwrapNoIvCheck(array, n, n2);
        }
        final byte[] array4 = new byte[4];
        final byte[] array5 = new byte[4];
        System.arraycopy(this.extractedAIV, 0, array4, 0, array4.length);
        System.arraycopy(this.extractedAIV, array4.length, array5, 0, array5.length);
        final int bigEndianToInt = Pack.bigEndianToInt(array5, 0);
        boolean b = true;
        if (!Arrays.constantTimeAreEqual(array4, this.preIV)) {
            b = false;
        }
        final int length = rfc3394UnwrapNoIvCheck.length;
        if (bigEndianToInt <= length - 8) {
            b = false;
        }
        if (bigEndianToInt > length) {
            b = false;
        }
        int length2 = length - bigEndianToInt;
        if (length2 >= rfc3394UnwrapNoIvCheck.length) {
            b = false;
            length2 = rfc3394UnwrapNoIvCheck.length;
        }
        final byte[] array6 = new byte[length2];
        final byte[] array7 = new byte[length2];
        System.arraycopy(rfc3394UnwrapNoIvCheck, rfc3394UnwrapNoIvCheck.length - length2, array7, 0, length2);
        if (!Arrays.constantTimeAreEqual(array7, array6)) {
            b = false;
        }
        if (!b) {
            throw new InvalidCipherTextException("checksum failed");
        }
        final byte[] array8 = new byte[bigEndianToInt];
        System.arraycopy(rfc3394UnwrapNoIvCheck, 0, array8, 0, array8.length);
        return array8;
    }
    
    private byte[] rfc3394UnwrapNoIvCheck(final byte[] array, final int n, final int n2) {
        final byte[] array2 = new byte[8];
        final byte[] array3 = new byte[n2 - array2.length];
        final byte[] extractedAIV = new byte[array2.length];
        final byte[] array4 = new byte[8 + array2.length];
        System.arraycopy(array, n, extractedAIV, 0, array2.length);
        System.arraycopy(array, n + array2.length, array3, 0, n2 - array2.length);
        this.engine.init(false, this.param);
        final int n3 = n2 / 8 - 1;
        for (int i = 5; i >= 0; --i) {
            for (int j = n3; j >= 1; --j) {
                System.arraycopy(extractedAIV, 0, array4, 0, array2.length);
                System.arraycopy(array3, 8 * (j - 1), array4, array2.length, 8);
                for (int k = n3 * i + j, n4 = 1; k != 0; k >>>= 8, ++n4) {
                    final byte b = (byte)k;
                    final byte[] array5 = array4;
                    final int n5 = array2.length - n4;
                    array5[n5] ^= b;
                }
                this.engine.processBlock(array4, 0, array4, 0);
                System.arraycopy(array4, 0, extractedAIV, 0, 8);
                System.arraycopy(array4, 8, array3, 8 * (j - 1), 8);
            }
        }
        this.extractedAIV = extractedAIV;
        return array3;
    }
}
