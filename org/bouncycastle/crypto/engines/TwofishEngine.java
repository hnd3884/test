package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.BlockCipher;

public final class TwofishEngine implements BlockCipher
{
    private static final byte[][] P;
    private static final int P_00 = 1;
    private static final int P_01 = 0;
    private static final int P_02 = 0;
    private static final int P_03 = 1;
    private static final int P_04 = 1;
    private static final int P_10 = 0;
    private static final int P_11 = 0;
    private static final int P_12 = 1;
    private static final int P_13 = 1;
    private static final int P_14 = 0;
    private static final int P_20 = 1;
    private static final int P_21 = 1;
    private static final int P_22 = 0;
    private static final int P_23 = 0;
    private static final int P_24 = 0;
    private static final int P_30 = 0;
    private static final int P_31 = 1;
    private static final int P_32 = 1;
    private static final int P_33 = 0;
    private static final int P_34 = 1;
    private static final int GF256_FDBK = 361;
    private static final int GF256_FDBK_2 = 180;
    private static final int GF256_FDBK_4 = 90;
    private static final int RS_GF_FDBK = 333;
    private static final int ROUNDS = 16;
    private static final int MAX_ROUNDS = 16;
    private static final int BLOCK_SIZE = 16;
    private static final int MAX_KEY_BITS = 256;
    private static final int INPUT_WHITEN = 0;
    private static final int OUTPUT_WHITEN = 4;
    private static final int ROUND_SUBKEYS = 8;
    private static final int TOTAL_SUBKEYS = 40;
    private static final int SK_STEP = 33686018;
    private static final int SK_BUMP = 16843009;
    private static final int SK_ROTL = 9;
    private boolean encrypting;
    private int[] gMDS0;
    private int[] gMDS1;
    private int[] gMDS2;
    private int[] gMDS3;
    private int[] gSubKeys;
    private int[] gSBox;
    private int k64Cnt;
    private byte[] workingKey;
    
    public TwofishEngine() {
        this.encrypting = false;
        this.gMDS0 = new int[256];
        this.gMDS1 = new int[256];
        this.gMDS2 = new int[256];
        this.gMDS3 = new int[256];
        this.k64Cnt = 0;
        this.workingKey = null;
        final int[] array = new int[2];
        final int[] array2 = new int[2];
        final int[] array3 = new int[2];
        for (int i = 0; i < 256; ++i) {
            final int n = TwofishEngine.P[0][i] & 0xFF;
            array[0] = n;
            array2[0] = (this.Mx_X(n) & 0xFF);
            array3[0] = (this.Mx_Y(n) & 0xFF);
            final int n2 = TwofishEngine.P[1][i] & 0xFF;
            array[1] = n2;
            array2[1] = (this.Mx_X(n2) & 0xFF);
            array3[1] = (this.Mx_Y(n2) & 0xFF);
            this.gMDS0[i] = (array[1] | array2[1] << 8 | array3[1] << 16 | array3[1] << 24);
            this.gMDS1[i] = (array3[0] | array3[0] << 8 | array2[0] << 16 | array[0] << 24);
            this.gMDS2[i] = (array2[1] | array3[1] << 8 | array[1] << 16 | array3[1] << 24);
            this.gMDS3[i] = (array2[0] | array[0] << 8 | array3[0] << 16 | array2[0] << 24);
        }
    }
    
    public void init(final boolean encrypting, final CipherParameters cipherParameters) {
        if (cipherParameters instanceof KeyParameter) {
            this.encrypting = encrypting;
            this.workingKey = ((KeyParameter)cipherParameters).getKey();
            this.k64Cnt = this.workingKey.length / 8;
            this.setKey(this.workingKey);
            return;
        }
        throw new IllegalArgumentException("invalid parameter passed to Twofish init - " + cipherParameters.getClass().getName());
    }
    
    public String getAlgorithmName() {
        return "Twofish";
    }
    
    public int processBlock(final byte[] array, final int n, final byte[] array2, final int n2) {
        if (this.workingKey == null) {
            throw new IllegalStateException("Twofish not initialised");
        }
        if (n + 16 > array.length) {
            throw new DataLengthException("input buffer too short");
        }
        if (n2 + 16 > array2.length) {
            throw new OutputLengthException("output buffer too short");
        }
        if (this.encrypting) {
            this.encryptBlock(array, n, array2, n2);
        }
        else {
            this.decryptBlock(array, n, array2, n2);
        }
        return 16;
    }
    
    public void reset() {
        if (this.workingKey != null) {
            this.setKey(this.workingKey);
        }
    }
    
    public int getBlockSize() {
        return 16;
    }
    
    private void setKey(final byte[] array) {
        final int[] array2 = new int[4];
        final int[] array3 = new int[4];
        final int[] array4 = new int[4];
        this.gSubKeys = new int[40];
        if (this.k64Cnt < 1) {
            throw new IllegalArgumentException("Key size less than 64 bits");
        }
        if (this.k64Cnt > 4) {
            throw new IllegalArgumentException("Key size larger than 256 bits");
        }
        for (int i = 0; i < this.k64Cnt; ++i) {
            final int n = i * 8;
            array2[i] = this.BytesTo32Bits(array, n);
            array3[i] = this.BytesTo32Bits(array, n + 4);
            array4[this.k64Cnt - 1 - i] = this.RS_MDS_Encode(array2[i], array3[i]);
        }
        for (int j = 0; j < 20; ++j) {
            final int n2 = j * 33686018;
            final int f32 = this.F32(n2, array2);
            final int f33 = this.F32(n2 + 16843009, array3);
            final int n3 = f33 << 8 | f33 >>> 24;
            final int n4 = f32 + n3;
            this.gSubKeys[j * 2] = n4;
            final int n5 = n4 + n3;
            this.gSubKeys[j * 2 + 1] = (n5 << 9 | n5 >>> 23);
        }
        final int n6 = array4[0];
        final int n7 = array4[1];
        final int n8 = array4[2];
        final int n9 = array4[3];
        this.gSBox = new int[1024];
        for (int k = 0; k < 256; ++k) {
            int n13;
            int n12;
            int n11;
            int n10 = n11 = (n12 = (n13 = k));
            switch (this.k64Cnt & 0x3) {
                case 1: {
                    this.gSBox[k * 2] = this.gMDS0[(TwofishEngine.P[0][n11] & 0xFF) ^ this.b0(n6)];
                    this.gSBox[k * 2 + 1] = this.gMDS1[(TwofishEngine.P[0][n10] & 0xFF) ^ this.b1(n6)];
                    this.gSBox[k * 2 + 512] = this.gMDS2[(TwofishEngine.P[1][n12] & 0xFF) ^ this.b2(n6)];
                    this.gSBox[k * 2 + 513] = this.gMDS3[(TwofishEngine.P[1][n13] & 0xFF) ^ this.b3(n6)];
                    break;
                }
                case 0: {
                    n11 = ((TwofishEngine.P[1][n11] & 0xFF) ^ this.b0(n9));
                    n10 = ((TwofishEngine.P[0][n10] & 0xFF) ^ this.b1(n9));
                    n12 = ((TwofishEngine.P[0][n12] & 0xFF) ^ this.b2(n9));
                    n13 = ((TwofishEngine.P[1][n13] & 0xFF) ^ this.b3(n9));
                }
                case 3: {
                    n11 = ((TwofishEngine.P[1][n11] & 0xFF) ^ this.b0(n8));
                    n10 = ((TwofishEngine.P[1][n10] & 0xFF) ^ this.b1(n8));
                    n12 = ((TwofishEngine.P[0][n12] & 0xFF) ^ this.b2(n8));
                    n13 = ((TwofishEngine.P[0][n13] & 0xFF) ^ this.b3(n8));
                }
                case 2: {
                    this.gSBox[k * 2] = this.gMDS0[(TwofishEngine.P[0][(TwofishEngine.P[0][n11] & 0xFF) ^ this.b0(n7)] & 0xFF) ^ this.b0(n6)];
                    this.gSBox[k * 2 + 1] = this.gMDS1[(TwofishEngine.P[0][(TwofishEngine.P[1][n10] & 0xFF) ^ this.b1(n7)] & 0xFF) ^ this.b1(n6)];
                    this.gSBox[k * 2 + 512] = this.gMDS2[(TwofishEngine.P[1][(TwofishEngine.P[0][n12] & 0xFF) ^ this.b2(n7)] & 0xFF) ^ this.b2(n6)];
                    this.gSBox[k * 2 + 513] = this.gMDS3[(TwofishEngine.P[1][(TwofishEngine.P[1][n13] & 0xFF) ^ this.b3(n7)] & 0xFF) ^ this.b3(n6)];
                    break;
                }
            }
        }
    }
    
    private void encryptBlock(final byte[] array, final int n, final byte[] array2, final int n2) {
        int n3 = this.BytesTo32Bits(array, n) ^ this.gSubKeys[0];
        int n4 = this.BytesTo32Bits(array, n + 4) ^ this.gSubKeys[1];
        int n5 = this.BytesTo32Bits(array, n + 8) ^ this.gSubKeys[2];
        int n6 = this.BytesTo32Bits(array, n + 12) ^ this.gSubKeys[3];
        int n7 = 8;
        for (int i = 0; i < 16; i += 2) {
            final int fe32_0 = this.Fe32_0(n3);
            final int fe32_2 = this.Fe32_3(n4);
            final int n8 = n5 ^ fe32_0 + fe32_2 + this.gSubKeys[n7++];
            n5 = (n8 >>> 1 | n8 << 31);
            n6 = ((n6 << 1 | n6 >>> 31) ^ fe32_0 + 2 * fe32_2 + this.gSubKeys[n7++]);
            final int fe32_3 = this.Fe32_0(n5);
            final int fe32_4 = this.Fe32_3(n6);
            final int n9 = n3 ^ fe32_3 + fe32_4 + this.gSubKeys[n7++];
            n3 = (n9 >>> 1 | n9 << 31);
            n4 = ((n4 << 1 | n4 >>> 31) ^ fe32_3 + 2 * fe32_4 + this.gSubKeys[n7++]);
        }
        this.Bits32ToBytes(n5 ^ this.gSubKeys[4], array2, n2);
        this.Bits32ToBytes(n6 ^ this.gSubKeys[5], array2, n2 + 4);
        this.Bits32ToBytes(n3 ^ this.gSubKeys[6], array2, n2 + 8);
        this.Bits32ToBytes(n4 ^ this.gSubKeys[7], array2, n2 + 12);
    }
    
    private void decryptBlock(final byte[] array, final int n, final byte[] array2, final int n2) {
        int n3 = this.BytesTo32Bits(array, n) ^ this.gSubKeys[4];
        int n4 = this.BytesTo32Bits(array, n + 4) ^ this.gSubKeys[5];
        int n5 = this.BytesTo32Bits(array, n + 8) ^ this.gSubKeys[6];
        int n6 = this.BytesTo32Bits(array, n + 12) ^ this.gSubKeys[7];
        int n7 = 39;
        for (int i = 0; i < 16; i += 2) {
            final int fe32_0 = this.Fe32_0(n3);
            final int fe32_2 = this.Fe32_3(n4);
            final int n8 = n6 ^ fe32_0 + 2 * fe32_2 + this.gSubKeys[n7--];
            n5 = ((n5 << 1 | n5 >>> 31) ^ fe32_0 + fe32_2 + this.gSubKeys[n7--]);
            n6 = (n8 >>> 1 | n8 << 31);
            final int fe32_3 = this.Fe32_0(n5);
            final int fe32_4 = this.Fe32_3(n6);
            final int n9 = n4 ^ fe32_3 + 2 * fe32_4 + this.gSubKeys[n7--];
            n3 = ((n3 << 1 | n3 >>> 31) ^ fe32_3 + fe32_4 + this.gSubKeys[n7--]);
            n4 = (n9 >>> 1 | n9 << 31);
        }
        this.Bits32ToBytes(n5 ^ this.gSubKeys[0], array2, n2);
        this.Bits32ToBytes(n6 ^ this.gSubKeys[1], array2, n2 + 4);
        this.Bits32ToBytes(n3 ^ this.gSubKeys[2], array2, n2 + 8);
        this.Bits32ToBytes(n4 ^ this.gSubKeys[3], array2, n2 + 12);
    }
    
    private int F32(final int n, final int[] array) {
        int b0 = this.b0(n);
        int b2 = this.b1(n);
        int b3 = this.b2(n);
        int b4 = this.b3(n);
        final int n2 = array[0];
        final int n3 = array[1];
        final int n4 = array[2];
        final int n5 = array[3];
        int n6 = 0;
        switch (this.k64Cnt & 0x3) {
            case 1: {
                n6 = (this.gMDS0[(TwofishEngine.P[0][b0] & 0xFF) ^ this.b0(n2)] ^ this.gMDS1[(TwofishEngine.P[0][b2] & 0xFF) ^ this.b1(n2)] ^ this.gMDS2[(TwofishEngine.P[1][b3] & 0xFF) ^ this.b2(n2)] ^ this.gMDS3[(TwofishEngine.P[1][b4] & 0xFF) ^ this.b3(n2)]);
                break;
            }
            case 0: {
                b0 = ((TwofishEngine.P[1][b0] & 0xFF) ^ this.b0(n5));
                b2 = ((TwofishEngine.P[0][b2] & 0xFF) ^ this.b1(n5));
                b3 = ((TwofishEngine.P[0][b3] & 0xFF) ^ this.b2(n5));
                b4 = ((TwofishEngine.P[1][b4] & 0xFF) ^ this.b3(n5));
            }
            case 3: {
                b0 = ((TwofishEngine.P[1][b0] & 0xFF) ^ this.b0(n4));
                b2 = ((TwofishEngine.P[1][b2] & 0xFF) ^ this.b1(n4));
                b3 = ((TwofishEngine.P[0][b3] & 0xFF) ^ this.b2(n4));
                b4 = ((TwofishEngine.P[0][b4] & 0xFF) ^ this.b3(n4));
            }
            case 2: {
                n6 = (this.gMDS0[(TwofishEngine.P[0][(TwofishEngine.P[0][b0] & 0xFF) ^ this.b0(n3)] & 0xFF) ^ this.b0(n2)] ^ this.gMDS1[(TwofishEngine.P[0][(TwofishEngine.P[1][b2] & 0xFF) ^ this.b1(n3)] & 0xFF) ^ this.b1(n2)] ^ this.gMDS2[(TwofishEngine.P[1][(TwofishEngine.P[0][b3] & 0xFF) ^ this.b2(n3)] & 0xFF) ^ this.b2(n2)] ^ this.gMDS3[(TwofishEngine.P[1][(TwofishEngine.P[1][b4] & 0xFF) ^ this.b3(n3)] & 0xFF) ^ this.b3(n2)]);
                break;
            }
        }
        return n6;
    }
    
    private int RS_MDS_Encode(final int n, final int n2) {
        int rs_rem = n2;
        for (int i = 0; i < 4; ++i) {
            rs_rem = this.RS_rem(rs_rem);
        }
        int rs_rem2 = rs_rem ^ n;
        for (int j = 0; j < 4; ++j) {
            rs_rem2 = this.RS_rem(rs_rem2);
        }
        return rs_rem2;
    }
    
    private int RS_rem(final int n) {
        final int n2 = n >>> 24 & 0xFF;
        final int n3 = (n2 << 1 ^ (((n2 & 0x80) != 0x0) ? 333 : 0)) & 0xFF;
        final int n4 = n2 >>> 1 ^ (((n2 & 0x1) != 0x0) ? 166 : 0) ^ n3;
        return n << 8 ^ n4 << 24 ^ n3 << 16 ^ n4 << 8 ^ n2;
    }
    
    private int LFSR1(final int n) {
        return n >> 1 ^ (((n & 0x1) != 0x0) ? 180 : 0);
    }
    
    private int LFSR2(final int n) {
        return n >> 2 ^ (((n & 0x2) != 0x0) ? 180 : 0) ^ (((n & 0x1) != 0x0) ? 90 : 0);
    }
    
    private int Mx_X(final int n) {
        return n ^ this.LFSR2(n);
    }
    
    private int Mx_Y(final int n) {
        return n ^ this.LFSR1(n) ^ this.LFSR2(n);
    }
    
    private int b0(final int n) {
        return n & 0xFF;
    }
    
    private int b1(final int n) {
        return n >>> 8 & 0xFF;
    }
    
    private int b2(final int n) {
        return n >>> 16 & 0xFF;
    }
    
    private int b3(final int n) {
        return n >>> 24 & 0xFF;
    }
    
    private int Fe32_0(final int n) {
        return this.gSBox[0 + 2 * (n & 0xFF)] ^ this.gSBox[1 + 2 * (n >>> 8 & 0xFF)] ^ this.gSBox[512 + 2 * (n >>> 16 & 0xFF)] ^ this.gSBox[513 + 2 * (n >>> 24 & 0xFF)];
    }
    
    private int Fe32_3(final int n) {
        return this.gSBox[0 + 2 * (n >>> 24 & 0xFF)] ^ this.gSBox[1 + 2 * (n & 0xFF)] ^ this.gSBox[512 + 2 * (n >>> 8 & 0xFF)] ^ this.gSBox[513 + 2 * (n >>> 16 & 0xFF)];
    }
    
    private int BytesTo32Bits(final byte[] array, final int n) {
        return (array[n] & 0xFF) | (array[n + 1] & 0xFF) << 8 | (array[n + 2] & 0xFF) << 16 | (array[n + 3] & 0xFF) << 24;
    }
    
    private void Bits32ToBytes(final int n, final byte[] array, final int n2) {
        array[n2] = (byte)n;
        array[n2 + 1] = (byte)(n >> 8);
        array[n2 + 2] = (byte)(n >> 16);
        array[n2 + 3] = (byte)(n >> 24);
    }
    
    static {
        P = new byte[][] { { -87, 103, -77, -24, 4, -3, -93, 118, -102, -110, -128, 120, -28, -35, -47, 56, 13, -58, 53, -104, 24, -9, -20, 108, 67, 117, 55, 38, -6, 19, -108, 72, -14, -48, -117, 48, -124, 84, -33, 35, 25, 91, 61, 89, -13, -82, -94, -126, 99, 1, -125, 46, -39, 81, -101, 124, -90, -21, -91, -66, 22, 12, -29, 97, -64, -116, 58, -11, 115, 44, 37, 11, -69, 78, -119, 107, 83, 106, -76, -15, -31, -26, -67, 69, -30, -12, -74, 102, -52, -107, 3, 86, -44, 28, 30, -41, -5, -61, -114, -75, -23, -49, -65, -70, -22, 119, 57, -81, 51, -55, 98, 113, -127, 121, 9, -83, 36, -51, -7, -40, -27, -59, -71, 77, 68, 8, -122, -25, -95, 29, -86, -19, 6, 112, -78, -46, 65, 123, -96, 17, 49, -62, 39, -112, 32, -10, 96, -1, -106, 92, -79, -85, -98, -100, 82, 27, 95, -109, 10, -17, -111, -123, 73, -18, 45, 79, -113, 59, 71, -121, 109, 70, -42, 62, 105, 100, 42, -50, -53, 47, -4, -105, 5, 122, -84, 127, -43, 26, 75, 14, -89, 90, 40, 20, 63, 41, -120, 60, 76, 2, -72, -38, -80, 23, 85, 31, -118, 125, 87, -57, -115, 116, -73, -60, -97, 114, 126, 21, 34, 18, 88, 7, -103, 52, 110, 80, -34, 104, 101, -68, -37, -8, -56, -88, 43, 64, -36, -2, 50, -92, -54, 16, 33, -16, -45, 93, 15, 0, 111, -99, 54, 66, 74, 94, -63, -32 }, { 117, -13, -58, -12, -37, 123, -5, -56, 74, -45, -26, 107, 69, 125, -24, 75, -42, 50, -40, -3, 55, 113, -15, -31, 48, 15, -8, 27, -121, -6, 6, 63, 94, -70, -82, 91, -118, 0, -68, -99, 109, -63, -79, 14, -128, 93, -46, -43, -96, -124, 7, 20, -75, -112, 44, -93, -78, 115, 76, 84, -110, 116, 54, 81, 56, -80, -67, 90, -4, 96, 98, -106, 108, 66, -9, 16, 124, 40, 39, -116, 19, -107, -100, -57, 36, 70, 59, 112, -54, -29, -123, -53, 17, -48, -109, -72, -90, -125, 32, -1, -97, 119, -61, -52, 3, 111, 8, -65, 64, -25, 43, -30, 121, 12, -86, -126, 65, 58, -22, -71, -28, -102, -92, -105, 126, -38, 122, 23, 102, -108, -95, 29, 61, -16, -34, -77, 11, 114, -89, 28, -17, -47, 83, 62, -113, 51, 38, 95, -20, 118, 42, 73, -127, -120, -18, 33, -60, 26, -21, -39, -59, 57, -103, -51, -83, 49, -117, 1, 24, 35, -35, 31, 78, 45, -7, 72, 79, -14, 101, -114, 120, 92, 88, 25, -115, -27, -104, 87, 103, 127, 5, 100, -81, 99, -74, -2, -11, -73, 60, -91, -50, -23, 104, 68, -32, 77, 67, 105, 41, 46, -84, 21, 89, -88, 10, -98, 110, 71, -33, 52, 53, 106, -49, -36, 34, -55, -64, -101, -119, -44, -19, -85, 18, -94, 13, 82, -69, 2, 47, -87, -41, 97, 30, -76, 80, 4, -10, -62, 22, 37, -122, 86, 85, 9, -66, -111 } };
    }
}
