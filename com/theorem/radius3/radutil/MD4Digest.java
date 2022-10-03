package com.theorem.radius3.radutil;

import java.util.Arrays;

public class MD4Digest
{
    private MD4Context a;
    
    public MD4Digest() {
        this.a = new MD4Context();
    }
    
    public final void reset() {
        this.a.a();
    }
    
    public final void update(final byte[] array) {
        this.update(array, array.length);
    }
    
    public final void update(final byte[] array, final int n) {
        int n2 = 0;
        final int abs = Math.abs((this.a.d + n) % 64);
        for (int n3 = (this.a.d + n) / 64, i = 0; i < n3; ++i) {
            final int n4 = 64 - this.a.d;
            System.arraycopy(array, n2, this.a.b, this.a.d, n4);
            n2 += n4;
            final MD4Context a = this.a;
            a.d += n4;
            if (this.a.d == 64) {
                this.b();
                this.a.d = 0;
                final MD4Context a2 = this.a;
                ++a2.e;
            }
        }
        if (abs > 0) {
            System.arraycopy(array, n2, this.a.b, this.a.d, abs);
            final MD4Context a3 = this.a;
            a3.d += abs;
        }
    }
    
    public final byte[] digest() {
        this.a();
        final byte[] array = new byte[16];
        int n = 0;
        for (int i = 0; i < 4; ++i) {
            array[n++] = (byte)this.a.a[i];
            array[n++] = (byte)(this.a.a[i] >>> 8);
            array[n++] = (byte)(this.a.a[i] >>> 16);
            array[n++] = (byte)(this.a.a[i] >>> 24);
        }
        this.a.c();
        this.a.f = true;
        return array;
    }
    
    private final void a() {
        final int n = this.a.e * 64 + this.a.d << 3;
        final byte[] array = new byte[128];
        for (int i = 0; i < 64; ++i) {
            array[i] = (byte)((i < this.a.d) ? this.a.b[i] : 0);
        }
        array[this.a.d] = -128;
        if (this.a.d <= 55) {
            a(array, 56, n);
            System.arraycopy(array, 0, this.a.b, 0, 64);
            this.b();
        }
        else {
            a(array, 120, n);
            System.arraycopy(array, 0, this.a.b, 0, 64);
            this.b();
            System.arraycopy(array, 64, this.a.b, 0, 64);
            this.b();
        }
        Arrays.fill(array, (byte)0);
    }
    
    private static void a(final byte[] array, final int n, final int n2) {
        array[n] = (byte)(n2 & 0xFF);
        array[1 + n] = (byte)(n2 >> 8 & 0xFF);
        array[2 + n] = (byte)(n2 >> 16 & 0xFF);
        array[3 + n] = (byte)(n2 >> 24 & 0xFF);
    }
    
    private static int a(final int n, final int n2, final int n3) {
        return (n & n2) | (~n & n3);
    }
    
    private static int b(final int n, final int n2, final int n3) {
        return (n & n2) | (n & n3) | (n2 & n3);
    }
    
    private static int c(final int n, final int n2, final int n3) {
        return n ^ n2 ^ n3;
    }
    
    private static int a(final int n, final int n2) {
        if (n2 == 0) {
            return n;
        }
        return (n << n2 & -1) | (n >> 32 - n2 & Integer.MAX_VALUE >> 31 - n2);
    }
    
    private static int a(final int[] array, final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
        return a(n + a(n2, n3, n4) + array[n5], n6);
    }
    
    private static int b(final int[] array, final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
        return a(n + b(n2, n3, n4) + array[n5] + 1518500249, n6);
    }
    
    private static int c(final int[] array, final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
        return a(n + c(n2, n3, n4) + array[n5] + 1859775393, n6);
    }
    
    private final void b() {
        this.a.b();
        final int[] c = this.a.c;
        final int n = this.a.a[0];
        final int n2 = this.a.a[1];
        final int n3 = this.a.a[2];
        final int n4 = this.a.a[3];
        final int n5 = n & -1;
        final int n6 = n2 & -1;
        final int n7 = n3 & -1;
        final int n8 = n4 & -1;
        final int a = a(c, n5, n6, n7, n8, 0, 3);
        final int a2 = a(c, n8, a, n6, n7, 1, 7);
        final int a3 = a(c, n7, a2, a, n6, 2, 11);
        final int a4 = a(c, n6, a3, a2, a, 3, 19);
        final int a5 = a(c, a, a4, a3, a2, 4, 3);
        final int a6 = a(c, a2, a5, a4, a3, 5, 7);
        final int a7 = a(c, a3, a6, a5, a4, 6, 11);
        final int a8 = a(c, a4, a7, a6, a5, 7, 19);
        final int a9 = a(c, a5, a8, a7, a6, 8, 3);
        final int a10 = a(c, a6, a9, a8, a7, 9, 7);
        final int a11 = a(c, a7, a10, a9, a8, 10, 11);
        final int a12 = a(c, a8, a11, a10, a9, 11, 19);
        final int a13 = a(c, a9, a12, a11, a10, 12, 3);
        final int a14 = a(c, a10, a13, a12, a11, 13, 7);
        final int a15 = a(c, a11, a14, a13, a12, 14, 11);
        final int a16 = a(c, a12, a15, a14, a13, 15, 19);
        final int b = b(c, a13, a16, a15, a14, 0, 3);
        final int b2 = b(c, a14, b, a16, a15, 4, 5);
        final int b3 = b(c, a15, b2, b, a16, 8, 9);
        final int b4 = b(c, a16, b3, b2, b, 12, 13);
        final int b5 = b(c, b, b4, b3, b2, 1, 3);
        final int b6 = b(c, b2, b5, b4, b3, 5, 5);
        final int b7 = b(c, b3, b6, b5, b4, 9, 9);
        final int b8 = b(c, b4, b7, b6, b5, 13, 13);
        final int b9 = b(c, b5, b8, b7, b6, 2, 3);
        final int b10 = b(c, b6, b9, b8, b7, 6, 5);
        final int b11 = b(c, b7, b10, b9, b8, 10, 9);
        final int b12 = b(c, b8, b11, b10, b9, 14, 13);
        final int b13 = b(c, b9, b12, b11, b10, 3, 3);
        final int b14 = b(c, b10, b13, b12, b11, 7, 5);
        final int b15 = b(c, b11, b14, b13, b12, 11, 9);
        final int b16 = b(c, b12, b15, b14, b13, 15, 13);
        final int c2 = c(c, b13, b16, b15, b14, 0, 3);
        final int c3 = c(c, b14, c2, b16, b15, 8, 9);
        final int c4 = c(c, b15, c3, c2, b16, 4, 11);
        final int c5 = c(c, b16, c4, c3, c2, 12, 15);
        final int c6 = c(c, c2, c5, c4, c3, 2, 3);
        final int c7 = c(c, c3, c6, c5, c4, 10, 9);
        final int c8 = c(c, c4, c7, c6, c5, 6, 11);
        final int c9 = c(c, c5, c8, c7, c6, 14, 15);
        final int c10 = c(c, c6, c9, c8, c7, 1, 3);
        final int c11 = c(c, c7, c10, c9, c8, 9, 9);
        final int c12 = c(c, c8, c11, c10, c9, 5, 11);
        final int c13 = c(c, c9, c12, c11, c10, 13, 15);
        final int c14 = c(c, c10, c13, c12, c11, 3, 3);
        final int c15 = c(c, c11, c14, c13, c12, 11, 9);
        final int c16 = c(c, c12, c15, c14, c13, 7, 11);
        final int c17 = c(c, c13, c16, c15, c14, 15, 15);
        final int[] a17 = this.a.a;
        final int n9 = 0;
        a17[n9] += c14;
        final int[] a18 = this.a.a;
        final int n10 = 1;
        a18[n10] += c17;
        final int[] a19 = this.a.a;
        final int n11 = 2;
        a19[n11] += c16;
        final int[] a20 = this.a.a;
        final int n12 = 3;
        a20[n12] += c15;
    }
    
    private class MD4Context
    {
        int[] a;
        byte[] b;
        int[] c;
        int d;
        int e;
        boolean f;
        
        MD4Context() {
            this.a();
        }
        
        final void a() {
            if (this.b == null) {
                this.b = new byte[64];
            }
            else {
                Arrays.fill(this.b, (byte)0);
            }
            if (this.c == null) {
                this.c = new int[16];
            }
            else {
                Arrays.fill(this.c, 0);
            }
            if (this.a == null) {
                this.a = new int[4];
            }
            this.a[0] = 1732584193;
            this.a[1] = -271733879;
            this.a[2] = -1732584194;
            this.a[3] = 271733878;
            this.d = 0;
            this.e = 0;
            this.f = false;
        }
        
        final void b() {
            int n = 0;
            for (int i = 0; i < 16; ++i) {
                this.c[i] = ((this.b[n + 3] & 0xFF) << 24 | (this.b[n + 2] & 0xFF) << 16 | (this.b[n + 1] & 0xFF) << 8 | (this.b[n + 0] & 0xFF));
                n += 4;
            }
        }
        
        final void c() {
            Arrays.fill(this.b, (byte)0);
            Arrays.fill(this.a, 0);
            Arrays.fill(this.c, 0);
            this.d = 0;
        }
        
        public final String toString() {
            String s = "Context:" + "\n DigestBuffer  ";
            for (int i = 0; i < this.a.length; ++i) {
                s = s + this.a[i] + " ";
            }
            String s2 = s + "\n IntBuffer  ";
            for (int j = 0; j < this.c.length; ++j) {
                s2 = s2 + this.c[j] + " ";
            }
            return s2 + "\n Count " + this.d + " " + "\n Done " + this.f;
        }
    }
}
