package cryptix.jce.provider.cipher;

import java.security.InvalidKeyException;
import java.security.Key;

public final class Twofish extends BlockCipher
{
    private static final int BLOCK_SIZE = 16;
    private static final int ROUNDS = 16;
    private static final int TOTAL_SUBKEYS = 40;
    private static final int SK_BUMP = 16843009;
    private static final int SK_ROTL = 9;
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
    private static final int[][] MDS;
    private static final int RS_GF_FDBK = 333;
    private boolean decrypt;
    private final int[] sBox;
    private final int[] subKeys;
    
    private static final int LFSR1(final int x) {
        return x >> 1 ^ (((x & 0x1) != 0x0) ? 180 : 0);
    }
    
    private static final int LFSR2(final int x) {
        return x >> 2 ^ (((x & 0x2) != 0x0) ? 180 : 0) ^ (((x & 0x1) != 0x0) ? 90 : 0);
    }
    
    private static final int Mx_1(final int x) {
        return x;
    }
    
    private static final int Mx_X(final int x) {
        return x ^ LFSR2(x);
    }
    
    private static final int Mx_Y(final int x) {
        return x ^ LFSR1(x) ^ LFSR2(x);
    }
    
    protected void coreInit(final Key key, final boolean decrypt) throws InvalidKeyException {
        if (key == null) {
            throw new InvalidKeyException("key: key is null");
        }
        if (!key.getFormat().equalsIgnoreCase("RAW")) {
            throw new InvalidKeyException("key: wrong format, RAW needed");
        }
        final byte[] userkey = key.getEncoded();
        if (userkey == null) {
            throw new InvalidKeyException("RAW bytes missing");
        }
        final int len = userkey.length;
        if (len != 16 && len != 24 && len != 32) {
            throw new InvalidKeyException("Invalid user key length");
        }
        this.decrypt = decrypt;
        this.makeSubKeys(userkey);
    }
    
    protected void coreCrypt(final byte[] in, final int inOffset, final byte[] out, final int outOffset) {
        this.blockCrypt(in, inOffset, out, outOffset);
    }
    
    private final void makeSubKeys(final byte[] k) throws InvalidKeyException {
        final int length = k.length;
        final int k64Cnt = length / 8;
        final int[] k32e = new int[4];
        final int[] k32o = new int[4];
        final int[] sBoxKey = new int[4];
        for (int offset = 0, n = 0, n2 = k64Cnt - 1; n < 4 && offset < length; k32e[n] = ((k[offset++] & 0xFF) | (k[offset++] & 0xFF) << 8 | (k[offset++] & 0xFF) << 16 | (k[offset++] & 0xFF) << 24), k32o[n] = ((k[offset++] & 0xFF) | (k[offset++] & 0xFF) << 8 | (k[offset++] & 0xFF) << 16 | (k[offset++] & 0xFF) << 24), sBoxKey[n2] = RS_MDS_Encode(k32e[n], k32o[n]), ++n, --n2) {}
        int q = 0;
        int A;
        int B;
        for (int i = 0; i < 40; this.subKeys[i++] = A, A += B, this.subKeys[i++] = (A << 9 | A >>> 23)) {
            A = F32(k64Cnt, q, k32e);
            q += 16843009;
            B = F32(k64Cnt, q, k32o);
            q += 16843009;
            B = (B << 8 | B >>> 24);
            A += B;
        }
        final int k2 = sBoxKey[0];
        final int k3 = sBoxKey[1];
        final int k4 = sBoxKey[2];
        final int k5 = sBoxKey[3];
        for (int i = 0; i < 256; ++i) {
            int b4;
            int b3;
            int b2;
            int b1 = b2 = (b3 = (b4 = i));
            switch (k64Cnt & 0x3) {
                case 1: {
                    this.sBox[2 * i] = Twofish.MDS[0][(Twofish.P[0][b2] & 0xFF) ^ b0(k2)];
                    this.sBox[2 * i + 1] = Twofish.MDS[1][(Twofish.P[0][b1] & 0xFF) ^ b1(k2)];
                    this.sBox[512 + 2 * i] = Twofish.MDS[2][(Twofish.P[1][b3] & 0xFF) ^ b2(k2)];
                    this.sBox[512 + 2 * i + 1] = Twofish.MDS[3][(Twofish.P[1][b4] & 0xFF) ^ b3(k2)];
                    break;
                }
                case 0: {
                    b2 = ((Twofish.P[1][b2] & 0xFF) ^ b0(k5));
                    b1 = ((Twofish.P[0][b1] & 0xFF) ^ b1(k5));
                    b3 = ((Twofish.P[0][b3] & 0xFF) ^ b2(k5));
                    b4 = ((Twofish.P[1][b4] & 0xFF) ^ b3(k5));
                }
                case 3: {
                    b2 = ((Twofish.P[1][b2] & 0xFF) ^ b0(k4));
                    b1 = ((Twofish.P[1][b1] & 0xFF) ^ b1(k4));
                    b3 = ((Twofish.P[0][b3] & 0xFF) ^ b2(k4));
                    b4 = ((Twofish.P[0][b4] & 0xFF) ^ b3(k4));
                }
                case 2: {
                    this.sBox[2 * i] = Twofish.MDS[0][(Twofish.P[0][(Twofish.P[0][b2] & 0xFF) ^ b0(k3)] & 0xFF) ^ b0(k2)];
                    this.sBox[2 * i + 1] = Twofish.MDS[1][(Twofish.P[0][(Twofish.P[1][b1] & 0xFF) ^ b1(k3)] & 0xFF) ^ b1(k2)];
                    this.sBox[512 + 2 * i] = Twofish.MDS[2][(Twofish.P[1][(Twofish.P[0][b3] & 0xFF) ^ b2(k3)] & 0xFF) ^ b2(k2)];
                    this.sBox[512 + 2 * i + 1] = Twofish.MDS[3][(Twofish.P[1][(Twofish.P[1][b4] & 0xFF) ^ b3(k3)] & 0xFF) ^ b3(k2)];
                    break;
                }
            }
        }
        if (this.decrypt) {
            for (int i = 0; i < 4; ++i) {
                final int t = this.subKeys[i];
                this.subKeys[i] = this.subKeys[i + 4];
                this.subKeys[i + 4] = t;
            }
        }
    }
    
    private final void blockCrypt(final byte[] in, int inOffset, final byte[] out, int outOffset) {
        final int[] sBox = this.sBox;
        final int[] sKey = this.subKeys;
        int x0 = (in[inOffset++] & 0xFF) | (in[inOffset++] & 0xFF) << 8 | (in[inOffset++] & 0xFF) << 16 | (in[inOffset++] & 0xFF) << 24;
        int x2 = (in[inOffset++] & 0xFF) | (in[inOffset++] & 0xFF) << 8 | (in[inOffset++] & 0xFF) << 16 | (in[inOffset++] & 0xFF) << 24;
        int x3 = (in[inOffset++] & 0xFF) | (in[inOffset++] & 0xFF) << 8 | (in[inOffset++] & 0xFF) << 16 | (in[inOffset++] & 0xFF) << 24;
        int x4 = (in[inOffset++] & 0xFF) | (in[inOffset++] & 0xFF) << 8 | (in[inOffset++] & 0xFF) << 16 | (in[inOffset] & 0xFF) << 24;
        x0 ^= sKey[0];
        x2 ^= sKey[1];
        x3 ^= sKey[2];
        x4 ^= sKey[3];
        if (this.decrypt) {
            int k = 39;
            for (int R = 0; R < 16; R += 2) {
                int t0 = Fe32(sBox, x0, 0);
                int t2 = Fe32(sBox, x2, 3);
                x4 ^= t0 + 2 * t2 + sKey[k--];
                x4 = (x4 >>> 1 | x4 << 31);
                x3 = (x3 << 1 | x3 >>> 31);
                x3 ^= t0 + t2 + sKey[k--];
                t0 = Fe32(sBox, x3, 0);
                t2 = Fe32(sBox, x4, 3);
                x2 ^= t0 + 2 * t2 + sKey[k--];
                x2 = (x2 >>> 1 | x2 << 31);
                x0 = (x0 << 1 | x0 >>> 31);
                x0 ^= t0 + t2 + sKey[k--];
            }
        }
        else {
            int n = 8;
            for (int R = 0; R < 16; R += 2) {
                int t0 = Fe32(sBox, x0, 0);
                int t2 = Fe32(sBox, x2, 3);
                x3 ^= t0 + t2 + sKey[n++];
                x3 = (x3 >>> 1 | x3 << 31);
                x4 = (x4 << 1 | x4 >>> 31);
                x4 ^= t0 + 2 * t2 + sKey[n++];
                t0 = Fe32(sBox, x3, 0);
                t2 = Fe32(sBox, x4, 3);
                x0 ^= t0 + t2 + sKey[n++];
                x0 = (x0 >>> 1 | x0 << 31);
                x2 = (x2 << 1 | x2 >>> 31);
                x2 ^= t0 + 2 * t2 + sKey[n++];
            }
        }
        x3 ^= sKey[4];
        x4 ^= sKey[5];
        x0 ^= sKey[6];
        x2 ^= sKey[7];
        out[outOffset++] = (byte)x3;
        out[outOffset++] = (byte)(x3 >>> 8);
        out[outOffset++] = (byte)(x3 >>> 16);
        out[outOffset++] = (byte)(x3 >>> 24);
        out[outOffset++] = (byte)x4;
        out[outOffset++] = (byte)(x4 >>> 8);
        out[outOffset++] = (byte)(x4 >>> 16);
        out[outOffset++] = (byte)(x4 >>> 24);
        out[outOffset++] = (byte)x0;
        out[outOffset++] = (byte)(x0 >>> 8);
        out[outOffset++] = (byte)(x0 >>> 16);
        out[outOffset++] = (byte)(x0 >>> 24);
        out[outOffset++] = (byte)x2;
        out[outOffset++] = (byte)(x2 >>> 8);
        out[outOffset++] = (byte)(x2 >>> 16);
        out[outOffset] = (byte)(x2 >>> 24);
    }
    
    private static final int b0(final int x) {
        return x & 0xFF;
    }
    
    private static final int b1(final int x) {
        return x >>> 8 & 0xFF;
    }
    
    private static final int b2(final int x) {
        return x >>> 16 & 0xFF;
    }
    
    private static final int b3(final int x) {
        return x >>> 24 & 0xFF;
    }
    
    private static final int RS_MDS_Encode(final int k0, final int k1) {
        int r = k1;
        for (int i = 0; i < 4; ++i) {
            r = RS_rem(r);
        }
        r ^= k0;
        for (int i = 0; i < 4; ++i) {
            r = RS_rem(r);
        }
        return r;
    }
    
    private static final int RS_rem(final int x) {
        final int b = x >>> 24 & 0xFF;
        final int g2 = (b << 1 ^ (((b & 0x80) != 0x0) ? 333 : 0)) & 0xFF;
        final int g3 = b >>> 1 ^ (((b & 0x1) != 0x0) ? 166 : 0) ^ g2;
        final int result = x << 8 ^ g3 << 24 ^ g2 << 16 ^ g3 << 8 ^ b;
        return result;
    }
    
    private static final int F32(final int k64Cnt, final int x, final int[] k32) {
        int b0 = b0(x);
        int b2 = b1(x);
        int b3 = b2(x);
        int b4 = b3(x);
        final int k33 = k32[0];
        final int k34 = k32[1];
        final int k35 = k32[2];
        final int k36 = k32[3];
        int result = 0;
        switch (k64Cnt & 0x3) {
            case 1: {
                result = (Twofish.MDS[0][(Twofish.P[0][b0] & 0xFF) ^ b0(k33)] ^ Twofish.MDS[1][(Twofish.P[0][b2] & 0xFF) ^ b1(k33)] ^ Twofish.MDS[2][(Twofish.P[1][b3] & 0xFF) ^ b2(k33)] ^ Twofish.MDS[3][(Twofish.P[1][b4] & 0xFF) ^ b3(k33)]);
                break;
            }
            case 0: {
                b0 = ((Twofish.P[1][b0] & 0xFF) ^ b0(k36));
                b2 = ((Twofish.P[0][b2] & 0xFF) ^ b1(k36));
                b3 = ((Twofish.P[0][b3] & 0xFF) ^ b2(k36));
                b4 = ((Twofish.P[1][b4] & 0xFF) ^ b3(k36));
            }
            case 3: {
                b0 = ((Twofish.P[1][b0] & 0xFF) ^ b0(k35));
                b2 = ((Twofish.P[1][b2] & 0xFF) ^ b1(k35));
                b3 = ((Twofish.P[0][b3] & 0xFF) ^ b2(k35));
                b4 = ((Twofish.P[0][b4] & 0xFF) ^ b3(k35));
            }
            case 2: {
                result = (Twofish.MDS[0][(Twofish.P[0][(Twofish.P[0][b0] & 0xFF) ^ b0(k34)] & 0xFF) ^ b0(k33)] ^ Twofish.MDS[1][(Twofish.P[0][(Twofish.P[1][b2] & 0xFF) ^ b1(k34)] & 0xFF) ^ b1(k33)] ^ Twofish.MDS[2][(Twofish.P[1][(Twofish.P[0][b3] & 0xFF) ^ b2(k34)] & 0xFF) ^ b2(k33)] ^ Twofish.MDS[3][(Twofish.P[1][(Twofish.P[1][b4] & 0xFF) ^ b3(k34)] & 0xFF) ^ b3(k33)]);
                break;
            }
        }
        return result;
    }
    
    private static final int Fe32(final int[] sBox, final int x, final int R) {
        return sBox[2 * _b(x, R)] ^ sBox[2 * _b(x, R + 1) + 1] ^ sBox[512 + 2 * _b(x, R + 2)] ^ sBox[512 + 2 * _b(x, R + 3) + 1];
    }
    
    private static final int _b(final int x, final int N) {
        int result = 0;
        switch (N % 4) {
            case 0: {
                result = b0(x);
                break;
            }
            case 1: {
                result = b1(x);
                break;
            }
            case 2: {
                result = b2(x);
                break;
            }
            case 3: {
                result = b3(x);
                break;
            }
        }
        return result;
    }
    
    public Twofish() {
        super(16);
        this.sBox = new int[1024];
        this.subKeys = new int[40];
    }
    
    static {
        final byte[][] p = new byte[2][];
        p[0] = new byte[] { -87, 103, -77, -24, 4, -3, -93, 118, -102, -110, -128, 120, -28, -35, -47, 56, 13, -58, 53, -104, 24, -9, -20, 108, 67, 117, 55, 38, -6, 19, -108, 72, -14, -48, -117, 48, -124, 84, -33, 35, 25, 91, 61, 89, -13, -82, -94, -126, 99, 1, -125, 46, -39, 81, -101, 124, -90, -21, -91, -66, 22, 12, -29, 97, -64, -116, 58, -11, 115, 44, 37, 11, -69, 78, -119, 107, 83, 106, -76, -15, -31, -26, -67, 69, -30, -12, -74, 102, -52, -107, 3, 86, -44, 28, 30, -41, -5, -61, -114, -75, -23, -49, -65, -70, -22, 119, 57, -81, 51, -55, 98, 113, -127, 121, 9, -83, 36, -51, -7, -40, -27, -59, -71, 77, 68, 8, -122, -25, -95, 29, -86, -19, 6, 112, -78, -46, 65, 123, -96, 17, 49, -62, 39, -112, 32, -10, 96, -1, -106, 92, -79, -85, -98, -100, 82, 27, 95, -109, 10, -17, -111, -123, 73, -18, 45, 79, -113, 59, 71, -121, 109, 70, -42, 62, 105, 100, 42, -50, -53, 47, -4, -105, 5, 122, -84, 127, -43, 26, 75, 14, -89, 90, 40, 20, 63, 41, -120, 60, 76, 2, -72, -38, -80, 23, 85, 31, -118, 125, 87, -57, -115, 116, -73, -60, -97, 114, 126, 21, 34, 18, 88, 7, -103, 52, 110, 80, -34, 104, 101, -68, -37, -8, -56, -88, 43, 64, -36, -2, 50, -92, -54, 16, 33, -16, -45, 93, 15, 0, 111, -99, 54, 66, 74, 94, -63, -32 };
        p[1] = new byte[] { 117, -13, -58, -12, -37, 123, -5, -56, 74, -45, -26, 107, 69, 125, -24, 75, -42, 50, -40, -3, 55, 113, -15, -31, 48, 15, -8, 27, -121, -6, 6, 63, 94, -70, -82, 91, -118, 0, -68, -99, 109, -63, -79, 14, -128, 93, -46, -43, -96, -124, 7, 20, -75, -112, 44, -93, -78, 115, 76, 84, -110, 116, 54, 81, 56, -80, -67, 90, -4, 96, 98, -106, 108, 66, -9, 16, 124, 40, 39, -116, 19, -107, -100, -57, 36, 70, 59, 112, -54, -29, -123, -53, 17, -48, -109, -72, -90, -125, 32, -1, -97, 119, -61, -52, 3, 111, 8, -65, 64, -25, 43, -30, 121, 12, -86, -126, 65, 58, -22, -71, -28, -102, -92, -105, 126, -38, 122, 23, 102, -108, -95, 29, 61, -16, -34, -77, 11, 114, -89, 28, -17, -47, 83, 62, -113, 51, 38, 95, -20, 118, 42, 73, -127, -120, -18, 33, -60, 26, -21, -39, -59, 57, -103, -51, -83, 49, -117, 1, 24, 35, -35, 31, 78, 45, -7, 72, 79, -14, 101, -114, 120, 92, 88, 25, -115, -27, -104, 87, 103, 127, 5, 100, -81, 99, -74, -2, -11, -73, 60, -91, -50, -23, 104, 68, -32, 77, 67, 105, 41, 46, -84, 21, 89, -88, 10, -98, 110, 71, -33, 52, 53, 106, -49, -36, 34, -55, -64, -101, -119, -44, -19, -85, 18, -94, 13, 82, -69, 2, 47, -87, -41, 97, 30, -76, 80, 4, -10, -62, 22, 37, -122, 86, 85, 9, -66, -111 };
        P = p;
        MDS = new int[4][256];
        final int[] m1 = new int[2];
        final int[] mX = new int[2];
        final int[] mY = new int[2];
        for (int i = 0; i < 256; ++i) {
            int j = Twofish.P[0][i] & 0xFF;
            m1[0] = j;
            mX[0] = (Mx_X(j) & 0xFF);
            mY[0] = (Mx_Y(j) & 0xFF);
            j = (Twofish.P[1][i] & 0xFF);
            m1[1] = j;
            mX[1] = (Mx_X(j) & 0xFF);
            mY[1] = (Mx_Y(j) & 0xFF);
            Twofish.MDS[0][i] = (m1[1] | mX[1] << 8 | mY[1] << 16 | mY[1] << 24);
            Twofish.MDS[1][i] = (mY[0] | mY[0] << 8 | mX[0] << 16 | m1[0] << 24);
            Twofish.MDS[2][i] = (mX[1] | mY[1] << 8 | m1[1] << 16 | mY[1] << 24);
            Twofish.MDS[3][i] = (mX[0] | m1[0] << 8 | mY[0] << 16 | mX[0] << 24);
        }
    }
}
