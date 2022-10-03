package com.sshtools.zlib;

final class d
{
    static final int[] j;
    static final int[] f;
    static final int[] i;
    static final byte[] e;
    static final byte[] d;
    static final byte[] c;
    static final int[] h;
    static final int[] l;
    short[] b;
    int k;
    f g;
    
    static int b(final int n) {
        return (n < 256) ? com.sshtools.zlib.d.d[n] : com.sshtools.zlib.d.d[256 + (n >>> 7)];
    }
    
    void c(final Deflate deflate) {
        final short[] b = this.b;
        final short[] h = this.g.h;
        final int[] i = this.g.i;
        final int c = this.g.c;
        final int j = this.g.j;
        int k = 0;
        for (int l = 0; l <= 15; ++l) {
            deflate.xb[l] = 0;
        }
        b[deflate.k[deflate.s] * 2 + 1] = 0;
        int n;
        for (n = deflate.s + 1; n < 573; ++n) {
            final int n2 = deflate.k[n];
            int n3 = b[b[n2 * 2 + 1] * 2 + 1] + 1;
            if (n3 > j) {
                n3 = j;
                ++k;
            }
            b[n2 * 2 + 1] = (short)n3;
            if (n2 <= this.k) {
                final short[] xb = deflate.xb;
                final short n4 = (short)n3;
                ++xb[n4];
                int n5 = 0;
                if (n2 >= c) {
                    n5 = i[n2 - c];
                }
                final short n6 = b[n2 * 2];
                deflate.rb += n6 * (n3 + n5);
                if (h != null) {
                    deflate.ob += n6 * (h[n2 * 2 + 1] + n5);
                }
            }
        }
        if (k == 0) {
            return;
        }
        do {
            int n7;
            for (n7 = j - 1; deflate.xb[n7] == 0; --n7) {}
            final short[] xb2 = deflate.xb;
            final int n8 = n7;
            --xb2[n8];
            final short[] xb3 = deflate.xb;
            final int n9 = n7 + 1;
            xb3[n9] += 2;
            final short[] xb4 = deflate.xb;
            final short n10 = (short)j;
            --xb4[n10];
            k -= 2;
        } while (k > 0);
        for (short n11 = (short)j; n11 != 0; --n11) {
            int n12 = deflate.xb[n11];
            while (n12 != 0) {
                final int n13 = deflate.k[--n];
                if (n13 > this.k) {
                    continue;
                }
                if (b[n13 * 2 + 1] != n11) {
                    deflate.rb += (int)((n11 - (long)b[n13 * 2 + 1]) * b[n13 * 2]);
                    b[n13 * 2 + 1] = n11;
                }
                --n12;
            }
        }
    }
    
    void b(final Deflate deflate) {
        final short[] b = this.b;
        final short[] h = this.g.h;
        final int b2 = this.g.b;
        int k = -1;
        deflate.n = 0;
        deflate.s = 573;
        for (int i = 0; i < b2; ++i) {
            if (b[i * 2] != 0) {
                k = (deflate.k[++deflate.n] = i);
                deflate.m[i] = 0;
            }
            else {
                b[i * 2 + 1] = 0;
            }
        }
        while (deflate.n < 2) {
            final int[] j = deflate.k;
            final int n = ++deflate.n;
            final int n2 = (k < 2) ? (++k) : 0;
            j[n] = n2;
            final int n3 = n2;
            b[n3 * 2] = 1;
            deflate.m[n3] = 0;
            --deflate.rb;
            if (h != null) {
                deflate.ob -= h[n3 * 2 + 1];
            }
        }
        this.k = k;
        for (int l = deflate.n / 2; l >= 1; --l) {
            deflate.c(b, l);
        }
        int n4 = b2;
        do {
            final int n5 = deflate.k[1];
            deflate.k[1] = deflate.k[deflate.n--];
            deflate.c(b, 1);
            final int n6 = deflate.k[1];
            deflate.k[--deflate.s] = n5;
            deflate.k[--deflate.s] = n6;
            b[n4 * 2] = (short)(b[n5 * 2] + b[n6 * 2]);
            deflate.m[n4] = (byte)(Math.max(deflate.m[n5], deflate.m[n6]) + 1);
            b[n5 * 2 + 1] = (b[n6 * 2 + 1] = (short)n4);
            deflate.k[1] = n4++;
            deflate.c(b, 1);
        } while (deflate.n >= 2);
        deflate.k[--deflate.s] = deflate.k[1];
        this.c(deflate);
        b(b, k, deflate.xb);
    }
    
    static void b(final short[] array, final int n, final short[] array2) {
        final short[] array3 = new short[16];
        short n2 = 0;
        for (int i = 1; i <= 15; ++i) {
            n2 = (array3[i] = (short)(n2 + array2[i - 1] << 1));
        }
        for (int j = 0; j <= n; ++j) {
            final short n3 = array[j * 2 + 1];
            if (n3 != 0) {
                final int n4 = j * 2;
                final short[] array4 = array3;
                final short n5 = n3;
                final short n6 = array4[n5];
                array4[n5] = (short)(n6 + 1);
                array[n4] = (short)b(n6, n3);
            }
        }
    }
    
    static int b(int n, int n2) {
        int n3 = 0;
        do {
            final int n4 = n3 | (n & 0x1);
            n >>>= 1;
            n3 = n4 << 1;
        } while (--n2 > 0);
        return n3 >>> 1;
    }
    
    static {
        j = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 2, 2, 2, 2, 3, 3, 3, 3, 4, 4, 4, 4, 5, 5, 5, 5, 0 };
        f = new int[] { 0, 0, 0, 0, 1, 1, 2, 2, 3, 3, 4, 4, 5, 5, 6, 6, 7, 7, 8, 8, 9, 9, 10, 10, 11, 11, 12, 12, 13, 13 };
        i = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 3, 7 };
        e = new byte[] { 16, 17, 18, 0, 8, 7, 9, 6, 10, 5, 11, 4, 12, 3, 13, 2, 14, 1, 15 };
        d = new byte[] { 0, 1, 2, 3, 4, 4, 5, 5, 6, 6, 6, 6, 7, 7, 7, 7, 8, 8, 8, 8, 8, 8, 8, 8, 9, 9, 9, 9, 9, 9, 9, 9, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 0, 0, 16, 17, 18, 18, 19, 19, 20, 20, 20, 20, 21, 21, 21, 21, 22, 22, 22, 22, 22, 22, 22, 22, 23, 23, 23, 23, 23, 23, 23, 23, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 27, 27, 27, 27, 27, 27, 27, 27, 27, 27, 27, 27, 27, 27, 27, 27, 27, 27, 27, 27, 27, 27, 27, 27, 27, 27, 27, 27, 27, 27, 27, 27, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 29, 29, 29, 29, 29, 29, 29, 29, 29, 29, 29, 29, 29, 29, 29, 29, 29, 29, 29, 29, 29, 29, 29, 29, 29, 29, 29, 29, 29, 29, 29, 29, 29, 29, 29, 29, 29, 29, 29, 29, 29, 29, 29, 29, 29, 29, 29, 29, 29, 29, 29, 29, 29, 29, 29, 29, 29, 29, 29, 29, 29, 29, 29, 29 };
        c = new byte[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 8, 9, 9, 10, 10, 11, 11, 12, 12, 12, 12, 13, 13, 13, 13, 14, 14, 14, 14, 15, 15, 15, 15, 16, 16, 16, 16, 16, 16, 16, 16, 17, 17, 17, 17, 17, 17, 17, 17, 18, 18, 18, 18, 18, 18, 18, 18, 19, 19, 19, 19, 19, 19, 19, 19, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 27, 27, 27, 27, 27, 27, 27, 27, 27, 27, 27, 27, 27, 27, 27, 27, 27, 27, 27, 27, 27, 27, 27, 27, 27, 27, 27, 27, 27, 27, 27, 28 };
        h = new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 10, 12, 14, 16, 20, 24, 28, 32, 40, 48, 56, 64, 80, 96, 112, 128, 160, 192, 224, 0 };
        l = new int[] { 0, 1, 2, 3, 4, 6, 8, 12, 16, 24, 32, 48, 64, 96, 128, 192, 256, 384, 512, 768, 1024, 1536, 2048, 3072, 4096, 6144, 8192, 12288, 16384, 24576 };
    }
}
