package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.BlockCipher;

public class GOST3412_2015Engine implements BlockCipher
{
    private static final byte[] PI;
    private static final byte[] inversePI;
    private final byte[] lFactors;
    protected static final int BLOCK_SIZE = 16;
    private int KEY_LENGTH;
    private int SUB_LENGTH;
    private byte[][] subKeys;
    private boolean forEncryption;
    private byte[][] _gf_mul;
    
    public GOST3412_2015Engine() {
        this.lFactors = new byte[] { -108, 32, -123, 16, -62, -64, 1, -5, 1, -64, -62, 16, -123, 32, -108, 1 };
        this.KEY_LENGTH = 32;
        this.SUB_LENGTH = this.KEY_LENGTH / 2;
        this.subKeys = null;
        this._gf_mul = init_gf256_mul_table();
    }
    
    private static byte[][] init_gf256_mul_table() {
        final byte[][] array = new byte[256][];
        for (int i = 0; i < 256; ++i) {
            array[i] = new byte[256];
            for (int j = 0; j < 256; ++j) {
                array[i][j] = kuz_mul_gf256_slow((byte)i, (byte)j);
            }
        }
        return array;
    }
    
    private static byte kuz_mul_gf256_slow(byte b, byte b2) {
        byte b3 = 0;
        for (int n = 0; n < 8 && b != 0 && b2 != 0; b2 >>= 1, n = (byte)(n + 1)) {
            if ((b2 & 0x1) != 0x0) {
                b3 ^= b;
            }
            final byte b4 = (byte)(b & 0x80);
            b <<= 1;
            if (b4 != 0) {
                b ^= (byte)195;
            }
        }
        return b3;
    }
    
    public String getAlgorithmName() {
        return "GOST3412_2015";
    }
    
    public int getBlockSize() {
        return 16;
    }
    
    public void init(final boolean forEncryption, final CipherParameters cipherParameters) throws IllegalArgumentException {
        if (cipherParameters instanceof KeyParameter) {
            this.forEncryption = forEncryption;
            this.generateSubKeys(((KeyParameter)cipherParameters).getKey());
        }
        else if (cipherParameters != null) {
            throw new IllegalArgumentException("invalid parameter passed to GOST3412_2015 init - " + cipherParameters.getClass().getName());
        }
    }
    
    private void generateSubKeys(final byte[] array) {
        if (array.length != this.KEY_LENGTH) {
            throw new IllegalArgumentException("Key length invalid. Key needs to be 32 byte - 256 bit!!!");
        }
        this.subKeys = new byte[10][];
        for (int i = 0; i < 10; ++i) {
            this.subKeys[i] = new byte[this.SUB_LENGTH];
        }
        final byte[] array2 = new byte[this.SUB_LENGTH];
        final byte[] array3 = new byte[this.SUB_LENGTH];
        for (int j = 0; j < this.SUB_LENGTH; ++j) {
            this.subKeys[0][j] = (array2[j] = array[j]);
            this.subKeys[1][j] = (array3[j] = array[j + this.SUB_LENGTH]);
        }
        final byte[] array4 = new byte[this.SUB_LENGTH];
        for (int k = 1; k < 5; ++k) {
            for (int l = 1; l <= 8; ++l) {
                this.C(array4, 8 * (k - 1) + l);
                this.F(array4, array2, array3);
            }
            System.arraycopy(array2, 0, this.subKeys[2 * k], 0, this.SUB_LENGTH);
            System.arraycopy(array3, 0, this.subKeys[2 * k + 1], 0, this.SUB_LENGTH);
        }
    }
    
    private void C(final byte[] array, final int n) {
        Arrays.clear(array);
        array[15] = (byte)n;
        this.L(array);
    }
    
    private void F(final byte[] array, final byte[] array2, final byte[] array3) {
        final byte[] lsx = this.LSX(array, array2);
        this.X(lsx, array3);
        System.arraycopy(array2, 0, array3, 0, this.SUB_LENGTH);
        System.arraycopy(lsx, 0, array2, 0, this.SUB_LENGTH);
    }
    
    public int processBlock(final byte[] array, final int n, final byte[] array2, final int n2) throws DataLengthException, IllegalStateException {
        if (this.subKeys == null) {
            throw new IllegalStateException("GOST3412_2015 engine not initialised");
        }
        if (n + 16 > array.length) {
            throw new DataLengthException("input buffer too short");
        }
        if (n2 + 16 > array2.length) {
            throw new OutputLengthException("output buffer too short");
        }
        this.GOST3412_2015Func(array, n, array2, n2);
        return 16;
    }
    
    private void GOST3412_2015Func(final byte[] array, final int n, final byte[] array2, final int n2) {
        byte[] array3 = new byte[16];
        System.arraycopy(array, n, array3, 0, 16);
        if (this.forEncryption) {
            for (int i = 0; i < 9; ++i) {
                array3 = Arrays.copyOf(this.LSX(this.subKeys[i], array3), 16);
            }
            this.X(array3, this.subKeys[9]);
        }
        else {
            for (int j = 9; j > 0; --j) {
                array3 = Arrays.copyOf(this.XSL(this.subKeys[j], array3), 16);
            }
            this.X(array3, this.subKeys[0]);
        }
        System.arraycopy(array3, 0, array2, n2, 16);
    }
    
    private byte[] LSX(final byte[] array, final byte[] array2) {
        final byte[] copy = Arrays.copyOf(array, array.length);
        this.X(copy, array2);
        this.S(copy);
        this.L(copy);
        return copy;
    }
    
    private byte[] XSL(final byte[] array, final byte[] array2) {
        final byte[] copy = Arrays.copyOf(array, array.length);
        this.X(copy, array2);
        this.inverseL(copy);
        this.inverseS(copy);
        return copy;
    }
    
    private void X(final byte[] array, final byte[] array2) {
        for (int i = 0; i < array.length; ++i) {
            final int n = i;
            array[n] ^= array2[i];
        }
    }
    
    private void S(final byte[] array) {
        for (int i = 0; i < array.length; ++i) {
            array[i] = GOST3412_2015Engine.PI[this.unsignedByte(array[i])];
        }
    }
    
    private void inverseS(final byte[] array) {
        for (int i = 0; i < array.length; ++i) {
            array[i] = GOST3412_2015Engine.inversePI[this.unsignedByte(array[i])];
        }
    }
    
    private int unsignedByte(final byte b) {
        return b & 0xFF;
    }
    
    private void L(final byte[] array) {
        for (int i = 0; i < 16; ++i) {
            this.R(array);
        }
    }
    
    private void inverseL(final byte[] array) {
        for (int i = 0; i < 16; ++i) {
            this.inverseR(array);
        }
    }
    
    private void R(final byte[] array) {
        final byte l = this.l(array);
        System.arraycopy(array, 0, array, 1, 15);
        array[0] = l;
    }
    
    private void inverseR(final byte[] array) {
        final byte[] array2 = new byte[16];
        System.arraycopy(array, 1, array2, 0, 15);
        array2[15] = array[0];
        final byte l = this.l(array2);
        System.arraycopy(array, 1, array, 0, 15);
        array[15] = l;
    }
    
    private byte l(final byte[] array) {
        byte b = array[15];
        for (int i = 14; i >= 0; --i) {
            b ^= this._gf_mul[this.unsignedByte(array[i])][this.unsignedByte(this.lFactors[i])];
        }
        return b;
    }
    
    public void reset() {
    }
    
    static {
        PI = new byte[] { -4, -18, -35, 17, -49, 110, 49, 22, -5, -60, -6, -38, 35, -59, 4, 77, -23, 119, -16, -37, -109, 46, -103, -70, 23, 54, -15, -69, 20, -51, 95, -63, -7, 24, 101, 90, -30, 92, -17, 33, -127, 28, 60, 66, -117, 1, -114, 79, 5, -124, 2, -82, -29, 106, -113, -96, 6, 11, -19, -104, 127, -44, -45, 31, -21, 52, 44, 81, -22, -56, 72, -85, -14, 42, 104, -94, -3, 58, -50, -52, -75, 112, 14, 86, 8, 12, 118, 18, -65, 114, 19, 71, -100, -73, 93, -121, 21, -95, -106, 41, 16, 123, -102, -57, -13, -111, 120, 111, -99, -98, -78, -79, 50, 117, 25, 61, -1, 53, -118, 126, 109, 84, -58, -128, -61, -67, 13, 87, -33, -11, 36, -87, 62, -88, 67, -55, -41, 121, -42, -10, 124, 34, -71, 3, -32, 15, -20, -34, 122, -108, -80, -68, -36, -24, 40, 80, 78, 51, 10, 74, -89, -105, 96, 115, 30, 0, 98, 68, 26, -72, 56, -126, 100, -97, 38, 65, -83, 69, 70, -110, 39, 94, 85, 47, -116, -93, -91, 125, 105, -43, -107, 59, 7, 88, -77, 64, -122, -84, 29, -9, 48, 55, 107, -28, -120, -39, -25, -119, -31, 27, -125, 73, 76, 63, -8, -2, -115, 83, -86, -112, -54, -40, -123, 97, 32, 113, 103, -92, 45, 43, 9, 91, -53, -101, 37, -48, -66, -27, 108, 82, 89, -90, 116, -46, -26, -12, -76, -64, -47, 102, -81, -62, 57, 75, 99, -74 };
        inversePI = new byte[] { -91, 45, 50, -113, 14, 48, 56, -64, 84, -26, -98, 57, 85, 126, 82, -111, 100, 3, 87, 90, 28, 96, 7, 24, 33, 114, -88, -47, 41, -58, -92, 63, -32, 39, -115, 12, -126, -22, -82, -76, -102, 99, 73, -27, 66, -28, 21, -73, -56, 6, 112, -99, 65, 117, 25, -55, -86, -4, 77, -65, 42, 115, -124, -43, -61, -81, 43, -122, -89, -79, -78, 91, 70, -45, -97, -3, -44, 15, -100, 47, -101, 67, -17, -39, 121, -74, 83, 127, -63, -16, 35, -25, 37, 94, -75, 30, -94, -33, -90, -2, -84, 34, -7, -30, 74, -68, 53, -54, -18, 120, 5, 107, 81, -31, 89, -93, -14, 113, 86, 17, 106, -119, -108, 101, -116, -69, 119, 60, 123, 40, -85, -46, 49, -34, -60, 95, -52, -49, 118, 44, -72, -40, 46, 54, -37, 105, -77, 20, -107, -66, 98, -95, 59, 22, 102, -23, 92, 108, 109, -83, 55, 97, 75, -71, -29, -70, -15, -96, -123, -125, -38, 71, -59, -80, 51, -6, -106, 111, 110, -62, -10, 80, -1, 93, -87, -114, 23, 27, -105, 125, -20, 88, -9, 31, -5, 124, 9, 13, 122, 103, 69, -121, -36, -24, 79, 29, 78, 4, -21, -8, -13, 62, 61, -67, -118, -120, -35, -51, 11, 19, -104, 2, -109, -128, -112, -48, 36, 52, -53, -19, -12, -50, -103, 16, 68, 64, -110, 58, 1, 38, 18, 26, 72, 104, -11, -127, -117, -57, -42, 32, 10, 8, 0, 76, -41, 116 };
    }
}
