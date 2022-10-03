package com.sun.security.auth.module;

import java.io.UnsupportedEncodingException;

class Crypt
{
    private static final byte[] IP;
    private static final byte[] FP;
    private static final byte[] PC1_C;
    private static final byte[] PC1_D;
    private static final byte[] shifts;
    private static final byte[] PC2_C;
    private static final byte[] PC2_D;
    private byte[] C;
    private byte[] D;
    private byte[] KS;
    private byte[] E;
    private static final byte[] e2;
    private static final byte[][] S;
    private static final byte[] P;
    private byte[] L;
    private byte[] tempL;
    private byte[] f;
    private byte[] preS;
    
    private void setkey(final byte[] array) {
        if (this.KS == null) {
            this.KS = new byte[768];
        }
        for (int i = 0; i < 28; ++i) {
            this.C[i] = array[Crypt.PC1_C[i] - 1];
            this.D[i] = array[Crypt.PC1_D[i] - 1];
        }
        for (int j = 0; j < 16; ++j) {
            for (byte b = 0; b < Crypt.shifts[j]; ++b) {
                final byte b2 = this.C[0];
                for (int k = 0; k < 27; ++k) {
                    this.C[k] = this.C[k + 1];
                }
                this.C[27] = b2;
                final byte b3 = this.D[0];
                for (int l = 0; l < 27; ++l) {
                    this.D[l] = this.D[l + 1];
                }
                this.D[27] = b3;
            }
            for (int n = 0; n < 24; ++n) {
                final int n2 = j * 48;
                this.KS[n2 + n] = this.C[Crypt.PC2_C[n] - 1];
                this.KS[n2 + n + 24] = this.D[Crypt.PC2_D[n] - 28 - 1];
            }
        }
        for (int n3 = 0; n3 < 48; ++n3) {
            this.E[n3] = Crypt.e2[n3];
        }
    }
    
    private void encrypt(final byte[] array, final int n) {
        final byte b = 32;
        if (this.KS == null) {
            this.KS = new byte[768];
        }
        for (int i = 0; i < 64; ++i) {
            this.L[i] = array[Crypt.IP[i] - 1];
        }
        for (int j = 0; j < 16; ++j) {
            final int n2 = j * 48;
            for (byte b2 = 0; b2 < 32; ++b2) {
                this.tempL[b2] = this.L[b + b2];
            }
            for (int k = 0; k < 48; ++k) {
                this.preS[k] = (byte)(this.L[b + this.E[k] - 1] ^ this.KS[n2 + k]);
            }
            for (int l = 0; l < 8; ++l) {
                final int n3 = 6 * l;
                final byte b3 = Crypt.S[l][(this.preS[n3 + 0] << 5) + (this.preS[n3 + 1] << 3) + (this.preS[n3 + 2] << 2) + (this.preS[n3 + 3] << 1) + (this.preS[n3 + 4] << 0) + (this.preS[n3 + 5] << 4)];
                final int n4 = 4 * l;
                this.f[n4 + 0] = (byte)(b3 >> 3 & 0x1);
                this.f[n4 + 1] = (byte)(b3 >> 2 & 0x1);
                this.f[n4 + 2] = (byte)(b3 >> 1 & 0x1);
                this.f[n4 + 3] = (byte)(b3 >> 0 & 0x1);
            }
            for (byte b4 = 0; b4 < 32; ++b4) {
                this.L[b + b4] = (byte)(this.L[b4] ^ this.f[Crypt.P[b4] - 1]);
            }
            for (int n5 = 0; n5 < 32; ++n5) {
                this.L[n5] = this.tempL[n5];
            }
        }
        for (byte b5 = 0; b5 < 32; ++b5) {
            final byte b6 = this.L[b5];
            this.L[b5] = this.L[b + b5];
            this.L[b + b5] = b6;
        }
        for (int n6 = 0; n6 < 64; ++n6) {
            array[n6] = this.L[Crypt.FP[n6] - 1];
        }
    }
    
    public Crypt() {
        this.C = new byte[28];
        this.D = new byte[28];
        this.E = new byte[48];
        this.L = new byte[64];
        this.tempL = new byte[32];
        this.f = new byte[32];
        this.preS = new byte[48];
    }
    
    public synchronized byte[] crypt(final byte[] array, final byte[] array2) {
        final byte[] array3 = new byte[66];
        final byte[] array4 = new byte[13];
        for (int n = 0, n2 = 0; n < array.length && n2 < 64; ++n2, ++n) {
            final byte b = array[n];
            for (int i = 0; i < 7; ++i, ++n2) {
                array3[n2] = (byte)(b >> 6 - i & 0x1);
            }
        }
        this.setkey(array3);
        for (int j = 0; j < 66; ++j) {
            array3[j] = 0;
        }
        for (int k = 0; k < 2; ++k) {
            int n3 = array2[k];
            array4[k] = (byte)n3;
            if (n3 > 90) {
                n3 -= 6;
            }
            if (n3 > 57) {
                n3 -= 7;
            }
            n3 -= 46;
            for (int l = 0; l < 6; ++l) {
                if ((n3 >> l & 0x1) != 0x0) {
                    final byte b2 = this.E[6 * k + l];
                    this.E[6 * k + l] = this.E[6 * k + l + 24];
                    this.E[6 * k + l + 24] = b2;
                }
            }
        }
        for (int n4 = 0; n4 < 25; ++n4) {
            this.encrypt(array3, 0);
        }
        for (int n5 = 0; n5 < 11; ++n5) {
            int n6 = 0;
            for (int n7 = 0; n7 < 6; ++n7) {
                n6 = (n6 << 1 | array3[6 * n5 + n7]);
            }
            n6 += 46;
            if (n6 > 57) {
                n6 += 7;
            }
            if (n6 > 90) {
                n6 += 6;
            }
            array4[n5 + 2] = (byte)n6;
        }
        if (array4[1] == 0) {
            array4[1] = array4[0];
        }
        return array4;
    }
    
    public static void main(final String[] array) {
        if (array.length != 2) {
            System.err.println("usage: Crypt password salt");
            System.exit(1);
        }
        final Crypt crypt = new Crypt();
        try {
            final byte[] crypt2 = crypt.crypt(array[0].getBytes("ISO-8859-1"), array[1].getBytes("ISO-8859-1"));
            for (int i = 0; i < crypt2.length; ++i) {
                System.out.println(" " + i + " " + (char)crypt2[i]);
            }
        }
        catch (final UnsupportedEncodingException ex) {}
    }
    
    static {
        IP = new byte[] { 58, 50, 42, 34, 26, 18, 10, 2, 60, 52, 44, 36, 28, 20, 12, 4, 62, 54, 46, 38, 30, 22, 14, 6, 64, 56, 48, 40, 32, 24, 16, 8, 57, 49, 41, 33, 25, 17, 9, 1, 59, 51, 43, 35, 27, 19, 11, 3, 61, 53, 45, 37, 29, 21, 13, 5, 63, 55, 47, 39, 31, 23, 15, 7 };
        FP = new byte[] { 40, 8, 48, 16, 56, 24, 64, 32, 39, 7, 47, 15, 55, 23, 63, 31, 38, 6, 46, 14, 54, 22, 62, 30, 37, 5, 45, 13, 53, 21, 61, 29, 36, 4, 44, 12, 52, 20, 60, 28, 35, 3, 43, 11, 51, 19, 59, 27, 34, 2, 42, 10, 50, 18, 58, 26, 33, 1, 41, 9, 49, 17, 57, 25 };
        PC1_C = new byte[] { 57, 49, 41, 33, 25, 17, 9, 1, 58, 50, 42, 34, 26, 18, 10, 2, 59, 51, 43, 35, 27, 19, 11, 3, 60, 52, 44, 36 };
        PC1_D = new byte[] { 63, 55, 47, 39, 31, 23, 15, 7, 62, 54, 46, 38, 30, 22, 14, 6, 61, 53, 45, 37, 29, 21, 13, 5, 28, 20, 12, 4 };
        shifts = new byte[] { 1, 1, 2, 2, 2, 2, 2, 2, 1, 2, 2, 2, 2, 2, 2, 1 };
        PC2_C = new byte[] { 14, 17, 11, 24, 1, 5, 3, 28, 15, 6, 21, 10, 23, 19, 12, 4, 26, 8, 16, 7, 27, 20, 13, 2 };
        PC2_D = new byte[] { 41, 52, 31, 37, 47, 55, 30, 40, 51, 45, 33, 48, 44, 49, 39, 56, 34, 53, 46, 42, 50, 36, 29, 32 };
        e2 = new byte[] { 32, 1, 2, 3, 4, 5, 4, 5, 6, 7, 8, 9, 8, 9, 10, 11, 12, 13, 12, 13, 14, 15, 16, 17, 16, 17, 18, 19, 20, 21, 20, 21, 22, 23, 24, 25, 24, 25, 26, 27, 28, 29, 28, 29, 30, 31, 32, 1 };
        S = new byte[][] { { 14, 4, 13, 1, 2, 15, 11, 8, 3, 10, 6, 12, 5, 9, 0, 7, 0, 15, 7, 4, 14, 2, 13, 1, 10, 6, 12, 11, 9, 5, 3, 8, 4, 1, 14, 8, 13, 6, 2, 11, 15, 12, 9, 7, 3, 10, 5, 0, 15, 12, 8, 2, 4, 9, 1, 7, 5, 11, 3, 14, 10, 0, 6, 13 }, { 15, 1, 8, 14, 6, 11, 3, 4, 9, 7, 2, 13, 12, 0, 5, 10, 3, 13, 4, 7, 15, 2, 8, 14, 12, 0, 1, 10, 6, 9, 11, 5, 0, 14, 7, 11, 10, 4, 13, 1, 5, 8, 12, 6, 9, 3, 2, 15, 13, 8, 10, 1, 3, 15, 4, 2, 11, 6, 7, 12, 0, 5, 14, 9 }, { 10, 0, 9, 14, 6, 3, 15, 5, 1, 13, 12, 7, 11, 4, 2, 8, 13, 7, 0, 9, 3, 4, 6, 10, 2, 8, 5, 14, 12, 11, 15, 1, 13, 6, 4, 9, 8, 15, 3, 0, 11, 1, 2, 12, 5, 10, 14, 7, 1, 10, 13, 0, 6, 9, 8, 7, 4, 15, 14, 3, 11, 5, 2, 12 }, { 7, 13, 14, 3, 0, 6, 9, 10, 1, 2, 8, 5, 11, 12, 4, 15, 13, 8, 11, 5, 6, 15, 0, 3, 4, 7, 2, 12, 1, 10, 14, 9, 10, 6, 9, 0, 12, 11, 7, 13, 15, 1, 3, 14, 5, 2, 8, 4, 3, 15, 0, 6, 10, 1, 13, 8, 9, 4, 5, 11, 12, 7, 2, 14 }, { 2, 12, 4, 1, 7, 10, 11, 6, 8, 5, 3, 15, 13, 0, 14, 9, 14, 11, 2, 12, 4, 7, 13, 1, 5, 0, 15, 10, 3, 9, 8, 6, 4, 2, 1, 11, 10, 13, 7, 8, 15, 9, 12, 5, 6, 3, 0, 14, 11, 8, 12, 7, 1, 14, 2, 13, 6, 15, 0, 9, 10, 4, 5, 3 }, { 12, 1, 10, 15, 9, 2, 6, 8, 0, 13, 3, 4, 14, 7, 5, 11, 10, 15, 4, 2, 7, 12, 9, 5, 6, 1, 13, 14, 0, 11, 3, 8, 9, 14, 15, 5, 2, 8, 12, 3, 7, 0, 4, 10, 1, 13, 11, 6, 4, 3, 2, 12, 9, 5, 15, 10, 11, 14, 1, 7, 6, 0, 8, 13 }, { 4, 11, 2, 14, 15, 0, 8, 13, 3, 12, 9, 7, 5, 10, 6, 1, 13, 0, 11, 7, 4, 9, 1, 10, 14, 3, 5, 12, 2, 15, 8, 6, 1, 4, 11, 13, 12, 3, 7, 14, 10, 15, 6, 8, 0, 5, 9, 2, 6, 11, 13, 8, 1, 4, 10, 7, 9, 5, 0, 15, 14, 2, 3, 12 }, { 13, 2, 8, 4, 6, 15, 11, 1, 10, 9, 3, 14, 5, 0, 12, 7, 1, 15, 13, 8, 10, 3, 7, 4, 12, 5, 6, 11, 0, 14, 9, 2, 7, 11, 4, 1, 9, 12, 14, 2, 0, 6, 10, 13, 15, 3, 5, 8, 2, 1, 14, 7, 4, 10, 8, 13, 15, 12, 9, 0, 3, 5, 6, 11 } };
        P = new byte[] { 16, 7, 20, 21, 29, 12, 28, 17, 1, 15, 23, 26, 5, 18, 31, 10, 2, 8, 24, 14, 32, 27, 3, 9, 19, 13, 30, 6, 22, 11, 4, 25 };
    }
}
