package com.maverick.ssh.components.standalone;

import java.io.IOException;
import com.maverick.crypto.engines.CipherEngine;

final class c implements CipherEngine
{
    private static final byte[][] yb;
    private boolean xb;
    private int[] cc;
    private int[] bc;
    private int[] ac;
    private int[] zb;
    private int[] wb;
    private int[] ec;
    private int dc;
    private byte[] vb;
    
    public c() {
        this.xb = false;
        this.cc = new int[256];
        this.bc = new int[256];
        this.ac = new int[256];
        this.zb = new int[256];
        this.dc = 0;
        this.vb = null;
        final int[] array = new int[2];
        final int[] array2 = new int[2];
        final int[] array3 = new int[2];
        for (int i = 0; i < 256; ++i) {
            final int n = c.yb[0][i] & 0xFF;
            array[0] = n;
            array2[0] = (this.o(n) & 0xFF);
            array3[0] = (this.l(n) & 0xFF);
            final int n2 = c.yb[1][i] & 0xFF;
            array[1] = n2;
            array2[1] = (this.o(n2) & 0xFF);
            array3[1] = (this.l(n2) & 0xFF);
            this.cc[i] = (array[1] | array2[1] << 8 | array3[1] << 16 | array3[1] << 24);
            this.bc[i] = (array3[0] | array3[0] << 8 | array2[0] << 16 | array[0] << 24);
            this.ac[i] = (array2[1] | array3[1] << 8 | array[1] << 16 | array3[1] << 24);
            this.zb[i] = (array2[0] | array[0] << 8 | array3[0] << 16 | array2[0] << 24);
        }
    }
    
    public void init(final boolean xb, final byte[] vb) {
        this.xb = xb;
        this.vb = vb;
        this.dc = this.vb.length / 8;
        this.c(this.vb);
    }
    
    public final int processBlock(final byte[] array, final int n, final byte[] array2, final int n2) throws IOException {
        if (this.vb == null) {
            throw new IOException("Twofish not initialised");
        }
        if (n + 16 > array.length) {
            throw new IOException("input buffer too short");
        }
        if (n2 + 16 > array2.length) {
            throw new IOException("output buffer too short");
        }
        if (this.xb) {
            this.e(array, n, array2, n2);
        }
        else {
            this.d(array, n, array2, n2);
        }
        return 16;
    }
    
    public int getBlockSize() {
        return 16;
    }
    
    private void c(final byte[] array) {
        final int[] array2 = new int[4];
        final int[] array3 = new int[4];
        final int[] array4 = new int[4];
        this.wb = new int[40];
        if (this.dc < 1 || this.dc > 4) {
            throw new IllegalArgumentException("Key size larger than 256 bits");
        }
        for (int i = 0; i < this.dc; ++i) {
            final int n = i * 8;
            array2[i] = this.e(array, n);
            array3[i] = this.e(array, n + 4);
            array4[this.dc - 1 - i] = this.c(array2[i], array3[i]);
        }
        for (int j = 0; j < 20; ++j) {
            final int n2 = j * 33686018;
            final int b = this.b(n2, array2);
            final int b2 = this.b(n2 + 16843009, array3);
            final int n3 = b2 << 8 | b2 >>> 24;
            final int n4 = b + n3;
            this.wb[j * 2] = n4;
            final int n5 = n4 + n3;
            this.wb[j * 2 + 1] = (n5 << 9 | n5 >>> 23);
        }
        final int n6 = array4[0];
        final int n7 = array4[1];
        final int n8 = array4[2];
        final int n9 = array4[3];
        this.ec = new int[1024];
        for (int k = 0; k < 256; ++k) {
            int n13;
            int n12;
            int n11;
            int n10 = n11 = (n12 = (n13 = k));
            switch (this.dc & 0x3) {
                case 1: {
                    this.ec[k * 2] = this.cc[(c.yb[0][n11] & 0xFF) ^ this.i(n6)];
                    this.ec[k * 2 + 1] = this.bc[(c.yb[0][n10] & 0xFF) ^ this.p(n6)];
                    this.ec[k * 2 + 512] = this.ac[(c.yb[1][n12] & 0xFF) ^ this.m(n6)];
                    this.ec[k * 2 + 513] = this.zb[(c.yb[1][n13] & 0xFF) ^ this.f(n6)];
                    break;
                }
                case 0: {
                    n11 = ((c.yb[1][n11] & 0xFF) ^ this.i(n9));
                    n10 = ((c.yb[0][n10] & 0xFF) ^ this.p(n9));
                    n12 = ((c.yb[0][n12] & 0xFF) ^ this.m(n9));
                    n13 = ((c.yb[1][n13] & 0xFF) ^ this.f(n9));
                }
                case 3: {
                    n11 = ((c.yb[1][n11] & 0xFF) ^ this.i(n8));
                    n10 = ((c.yb[1][n10] & 0xFF) ^ this.p(n8));
                    n12 = ((c.yb[0][n12] & 0xFF) ^ this.m(n8));
                    n13 = ((c.yb[0][n13] & 0xFF) ^ this.f(n8));
                }
                case 2: {
                    this.ec[k * 2] = this.cc[(c.yb[0][(c.yb[0][n11] & 0xFF) ^ this.i(n7)] & 0xFF) ^ this.i(n6)];
                    this.ec[k * 2 + 1] = this.bc[(c.yb[0][(c.yb[1][n10] & 0xFF) ^ this.p(n7)] & 0xFF) ^ this.p(n6)];
                    this.ec[k * 2 + 512] = this.ac[(c.yb[1][(c.yb[0][n12] & 0xFF) ^ this.m(n7)] & 0xFF) ^ this.m(n6)];
                    this.ec[k * 2 + 513] = this.zb[(c.yb[1][(c.yb[1][n13] & 0xFF) ^ this.f(n7)] & 0xFF) ^ this.f(n6)];
                    break;
                }
            }
        }
    }
    
    private void e(final byte[] array, final int n, final byte[] array2, final int n2) {
        int n3 = this.e(array, n) ^ this.wb[0];
        int n4 = this.e(array, n + 4) ^ this.wb[1];
        int n5 = this.e(array, n + 8) ^ this.wb[2];
        int n6 = this.e(array, n + 12) ^ this.wb[3];
        int n7 = 8;
        for (int i = 0; i < 16; i += 2) {
            final int k = this.k(n3);
            final int h = this.h(n4);
            final int n8 = n5 ^ k + h + this.wb[n7++];
            n5 = (n8 >>> 1 | n8 << 31);
            n6 = ((n6 << 1 | n6 >>> 31) ^ k + 2 * h + this.wb[n7++]);
            final int j = this.k(n5);
            final int h2 = this.h(n6);
            final int n9 = n3 ^ j + h2 + this.wb[n7++];
            n3 = (n9 >>> 1 | n9 << 31);
            n4 = ((n4 << 1 | n4 >>> 31) ^ j + 2 * h2 + this.wb[n7++]);
        }
        this.c(n5 ^ this.wb[4], array2, n2);
        this.c(n6 ^ this.wb[5], array2, n2 + 4);
        this.c(n3 ^ this.wb[6], array2, n2 + 8);
        this.c(n4 ^ this.wb[7], array2, n2 + 12);
    }
    
    private void d(final byte[] array, final int n, final byte[] array2, final int n2) {
        int n3 = this.e(array, n) ^ this.wb[4];
        int n4 = this.e(array, n + 4) ^ this.wb[5];
        int n5 = this.e(array, n + 8) ^ this.wb[6];
        int n6 = this.e(array, n + 12) ^ this.wb[7];
        int n7 = 39;
        for (int i = 0; i < 16; i += 2) {
            final int k = this.k(n3);
            final int h = this.h(n4);
            final int n8 = n6 ^ k + 2 * h + this.wb[n7--];
            n5 = ((n5 << 1 | n5 >>> 31) ^ k + h + this.wb[n7--]);
            n6 = (n8 >>> 1 | n8 << 31);
            final int j = this.k(n5);
            final int h2 = this.h(n6);
            final int n9 = n4 ^ j + 2 * h2 + this.wb[n7--];
            n3 = ((n3 << 1 | n3 >>> 31) ^ j + h2 + this.wb[n7--]);
            n4 = (n9 >>> 1 | n9 << 31);
        }
        this.c(n5 ^ this.wb[0], array2, n2);
        this.c(n6 ^ this.wb[1], array2, n2 + 4);
        this.c(n3 ^ this.wb[2], array2, n2 + 8);
        this.c(n4 ^ this.wb[3], array2, n2 + 12);
    }
    
    private final int b(final int n, final int[] array) {
        int i = this.i(n);
        int p2 = this.p(n);
        int m = this.m(n);
        int f = this.f(n);
        final int n2 = array[0];
        final int n3 = array[1];
        final int n4 = array[2];
        final int n5 = array[3];
        int n6 = 0;
        switch (this.dc & 0x3) {
            case 1: {
                n6 = (this.cc[(c.yb[0][i] & 0xFF) ^ this.i(n2)] ^ this.bc[(c.yb[0][p2] & 0xFF) ^ this.p(n2)] ^ this.ac[(c.yb[1][m] & 0xFF) ^ this.m(n2)] ^ this.zb[(c.yb[1][f] & 0xFF) ^ this.f(n2)]);
                break;
            }
            case 0: {
                i = ((c.yb[1][i] & 0xFF) ^ this.i(n5));
                p2 = ((c.yb[0][p2] & 0xFF) ^ this.p(n5));
                m = ((c.yb[0][m] & 0xFF) ^ this.m(n5));
                f = ((c.yb[1][f] & 0xFF) ^ this.f(n5));
            }
            case 3: {
                i = ((c.yb[1][i] & 0xFF) ^ this.i(n4));
                p2 = ((c.yb[1][p2] & 0xFF) ^ this.p(n4));
                m = ((c.yb[0][m] & 0xFF) ^ this.m(n4));
                f = ((c.yb[0][f] & 0xFF) ^ this.f(n4));
            }
            case 2: {
                n6 = (this.cc[(c.yb[0][(c.yb[0][i] & 0xFF) ^ this.i(n3)] & 0xFF) ^ this.i(n2)] ^ this.bc[(c.yb[0][(c.yb[1][p2] & 0xFF) ^ this.p(n3)] & 0xFF) ^ this.p(n2)] ^ this.ac[(c.yb[1][(c.yb[0][m] & 0xFF) ^ this.m(n3)] & 0xFF) ^ this.m(n2)] ^ this.zb[(c.yb[1][(c.yb[1][f] & 0xFF) ^ this.f(n3)] & 0xFF) ^ this.f(n2)]);
                break;
            }
        }
        return n6;
    }
    
    private final int c(final int n, final int n2) {
        int j = n2;
        for (int i = 0; i < 4; ++i) {
            j = this.j(j);
        }
        int k = j ^ n;
        for (int l = 0; l < 4; ++l) {
            k = this.j(k);
        }
        return k;
    }
    
    private final int j(final int n) {
        final int n2 = n >>> 24 & 0xFF;
        final int n3 = (n2 << 1 ^ (((n2 & 0x80) != 0x0) ? 333 : 0)) & 0xFF;
        final int n4 = n2 >>> 1 ^ (((n2 & 0x1) != 0x0) ? 166 : 0) ^ n3;
        return n << 8 ^ n4 << 24 ^ n3 << 16 ^ n4 << 8 ^ n2;
    }
    
    private final int n(final int n) {
        return n >> 1 ^ (((n & 0x1) != 0x0) ? 180 : 0);
    }
    
    private final int g(final int n) {
        return n >> 2 ^ (((n & 0x2) != 0x0) ? 180 : 0) ^ (((n & 0x1) != 0x0) ? 90 : 0);
    }
    
    private final int o(final int n) {
        return n ^ this.g(n);
    }
    
    private final int l(final int n) {
        return n ^ this.n(n) ^ this.g(n);
    }
    
    private final int i(final int n) {
        return n & 0xFF;
    }
    
    private final int p(final int n) {
        return n >>> 8 & 0xFF;
    }
    
    private final int m(final int n) {
        return n >>> 16 & 0xFF;
    }
    
    private final int f(final int n) {
        return n >>> 24 & 0xFF;
    }
    
    private final int k(final int n) {
        return this.ec[0 + 2 * (n & 0xFF)] ^ this.ec[1 + 2 * (n >>> 8 & 0xFF)] ^ this.ec[512 + 2 * (n >>> 16 & 0xFF)] ^ this.ec[513 + 2 * (n >>> 24 & 0xFF)];
    }
    
    private final int h(final int n) {
        return this.ec[0 + 2 * (n >>> 24 & 0xFF)] ^ this.ec[1 + 2 * (n & 0xFF)] ^ this.ec[512 + 2 * (n >>> 8 & 0xFF)] ^ this.ec[513 + 2 * (n >>> 16 & 0xFF)];
    }
    
    private final int e(final byte[] array, final int n) {
        return (array[n] & 0xFF) | (array[n + 1] & 0xFF) << 8 | (array[n + 2] & 0xFF) << 16 | (array[n + 3] & 0xFF) << 24;
    }
    
    private final void c(final int n, final byte[] array, final int n2) {
        array[n2] = (byte)n;
        array[n2 + 1] = (byte)(n >> 8);
        array[n2 + 2] = (byte)(n >> 16);
        array[n2 + 3] = (byte)(n >> 24);
    }
    
    static {
        yb = new byte[][] { { -87, 103, -77, -24, 4, -3, -93, 118, -102, -110, -128, 120, -28, -35, -47, 56, 13, -58, 53, -104, 24, -9, -20, 108, 67, 117, 55, 38, -6, 19, -108, 72, -14, -48, -117, 48, -124, 84, -33, 35, 25, 91, 61, 89, -13, -82, -94, -126, 99, 1, -125, 46, -39, 81, -101, 124, -90, -21, -91, -66, 22, 12, -29, 97, -64, -116, 58, -11, 115, 44, 37, 11, -69, 78, -119, 107, 83, 106, -76, -15, -31, -26, -67, 69, -30, -12, -74, 102, -52, -107, 3, 86, -44, 28, 30, -41, -5, -61, -114, -75, -23, -49, -65, -70, -22, 119, 57, -81, 51, -55, 98, 113, -127, 121, 9, -83, 36, -51, -7, -40, -27, -59, -71, 77, 68, 8, -122, -25, -95, 29, -86, -19, 6, 112, -78, -46, 65, 123, -96, 17, 49, -62, 39, -112, 32, -10, 96, -1, -106, 92, -79, -85, -98, -100, 82, 27, 95, -109, 10, -17, -111, -123, 73, -18, 45, 79, -113, 59, 71, -121, 109, 70, -42, 62, 105, 100, 42, -50, -53, 47, -4, -105, 5, 122, -84, 127, -43, 26, 75, 14, -89, 90, 40, 20, 63, 41, -120, 60, 76, 2, -72, -38, -80, 23, 85, 31, -118, 125, 87, -57, -115, 116, -73, -60, -97, 114, 126, 21, 34, 18, 88, 7, -103, 52, 110, 80, -34, 104, 101, -68, -37, -8, -56, -88, 43, 64, -36, -2, 50, -92, -54, 16, 33, -16, -45, 93, 15, 0, 111, -99, 54, 66, 74, 94, -63, -32 }, { 117, -13, -58, -12, -37, 123, -5, -56, 74, -45, -26, 107, 69, 125, -24, 75, -42, 50, -40, -3, 55, 113, -15, -31, 48, 15, -8, 27, -121, -6, 6, 63, 94, -70, -82, 91, -118, 0, -68, -99, 109, -63, -79, 14, -128, 93, -46, -43, -96, -124, 7, 20, -75, -112, 44, -93, -78, 115, 76, 84, -110, 116, 54, 81, 56, -80, -67, 90, -4, 96, 98, -106, 108, 66, -9, 16, 124, 40, 39, -116, 19, -107, -100, -57, 36, 70, 59, 112, -54, -29, -123, -53, 17, -48, -109, -72, -90, -125, 32, -1, -97, 119, -61, -52, 3, 111, 8, -65, 64, -25, 43, -30, 121, 12, -86, -126, 65, 58, -22, -71, -28, -102, -92, -105, 126, -38, 122, 23, 102, -108, -95, 29, 61, -16, -34, -77, 11, 114, -89, 28, -17, -47, 83, 62, -113, 51, 38, 95, -20, 118, 42, 73, -127, -120, -18, 33, -60, 26, -21, -39, -59, 57, -103, -51, -83, 49, -117, 1, 24, 35, -35, 31, 78, 45, -7, 72, 79, -14, 101, -114, 120, 92, 88, 25, -115, -27, -104, 87, 103, 127, 5, 100, -81, 99, -74, -2, -11, -73, 60, -91, -50, -23, 104, 68, -32, 77, 67, 105, 41, 46, -84, 21, 89, -88, 10, -98, 110, 71, -33, 52, 53, 106, -49, -36, 34, -55, -64, -101, -119, -44, -19, -85, 18, -94, 13, 82, -69, 2, 47, -87, -41, 97, 30, -76, 80, 4, -10, -62, 22, 37, -122, 86, 85, 9, -66, -111 } };
    }
}
