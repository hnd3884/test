package com.sshtools.zlib;

final class c
{
    private static final int[] u;
    static final int[] n;
    int m;
    int e;
    int q;
    int g;
    int[] j;
    int[] f;
    int[] i;
    g s;
    int k;
    int t;
    int c;
    int[] b;
    byte[] h;
    int d;
    int o;
    int p;
    Object l;
    long r;
    
    c(final ZStream zStream, final Object l, final int d) {
        this.f = new int[1];
        this.i = new int[1];
        this.b = new int[4320];
        this.h = new byte[d];
        this.d = d;
        this.l = l;
        this.m = 0;
        this.b(zStream, null);
    }
    
    void b(final ZStream zStream, final long[] array) {
        if (array != null) {
            array[0] = this.r;
        }
        if (this.m == 4 || this.m == 5) {
            this.j = null;
        }
        if (this.m == 6) {
            this.s.b(zStream);
        }
        this.m = 0;
        this.t = 0;
        this.c = 0;
        final int n = 0;
        this.p = n;
        this.o = n;
        if (this.l != null) {
            final long b = zStream.b.b(0L, null, 0, 0);
            this.r = b;
            zStream.adler = b;
        }
    }
    
    int c(final ZStream zStream, int n) {
        int next_in_index = zStream.next_in_index;
        int avail_in = zStream.avail_in;
        int n2 = this.c;
        int i = this.t;
        int p2 = this.p;
        int n3 = (p2 < this.o) ? (this.o - p2 - 1) : (this.d - p2);
        Label_2718: {
        Label_2598:
            while (true) {
                switch (this.m) {
                    case 0: {
                        while (i < 3) {
                            if (avail_in == 0) {
                                this.c = n2;
                                this.t = i;
                                zStream.avail_in = avail_in;
                                zStream.total_in += next_in_index - zStream.next_in_index;
                                zStream.next_in_index = next_in_index;
                                this.p = p2;
                                return this.b(zStream, n);
                            }
                            n = 0;
                            --avail_in;
                            n2 |= (zStream.next_in[next_in_index++] & 0xFF) << i;
                            i += 8;
                        }
                        final int n4 = n2 & 0x7;
                        this.k = (n4 & 0x1);
                        switch (n4 >>> 1) {
                            case 0: {
                                final int n5 = n2 >>> 3;
                                i -= 3;
                                final int n6 = i & 0x7;
                                n2 = n5 >>> n6;
                                i -= n6;
                                this.m = 1;
                                continue;
                            }
                            case 1: {
                                final int[] array = { 0 };
                                final int[] array2 = { 0 };
                                final int[][] array3 = { null };
                                final int[][] array4 = { null };
                                com.sshtools.zlib.h.b(array, array2, array3, array4, zStream);
                                this.s = new g(array[0], array2[0], array3[0], array4[0], zStream);
                                n2 >>>= 3;
                                i -= 3;
                                this.m = 6;
                                continue;
                            }
                            case 2: {
                                n2 >>>= 3;
                                i -= 3;
                                this.m = 3;
                                continue;
                            }
                            case 3: {
                                final int c = n2 >>> 3;
                                i -= 3;
                                this.m = 9;
                                zStream.msg = "invalid block type";
                                n = -3;
                                this.c = c;
                                this.t = i;
                                zStream.avail_in = avail_in;
                                zStream.total_in += next_in_index - zStream.next_in_index;
                                zStream.next_in_index = next_in_index;
                                this.p = p2;
                                return this.b(zStream, n);
                            }
                        }
                        continue;
                    }
                    case 1: {
                        while (i < 32) {
                            if (avail_in == 0) {
                                this.c = n2;
                                this.t = i;
                                zStream.avail_in = avail_in;
                                zStream.total_in += next_in_index - zStream.next_in_index;
                                zStream.next_in_index = next_in_index;
                                this.p = p2;
                                return this.b(zStream, n);
                            }
                            n = 0;
                            --avail_in;
                            n2 |= (zStream.next_in[next_in_index++] & 0xFF) << i;
                            i += 8;
                        }
                        if ((~n2 >>> 16 & 0xFFFF) != (n2 & 0xFFFF)) {
                            this.m = 9;
                            zStream.msg = "invalid stored block lengths";
                            n = -3;
                            this.c = n2;
                            this.t = i;
                            zStream.avail_in = avail_in;
                            zStream.total_in += next_in_index - zStream.next_in_index;
                            zStream.next_in_index = next_in_index;
                            this.p = p2;
                            return this.b(zStream, n);
                        }
                        this.e = (n2 & 0xFFFF);
                        i = (n2 = 0);
                        this.m = ((this.e != 0) ? 2 : ((this.k != 0) ? 7 : 0));
                        continue;
                    }
                    case 2: {
                        if (avail_in == 0) {
                            this.c = n2;
                            this.t = i;
                            zStream.avail_in = avail_in;
                            zStream.total_in += next_in_index - zStream.next_in_index;
                            zStream.next_in_index = next_in_index;
                            this.p = p2;
                            return this.b(zStream, n);
                        }
                        if (n3 == 0) {
                            if (p2 == this.d && this.o != 0) {
                                p2 = 0;
                                n3 = ((p2 < this.o) ? (this.o - p2 - 1) : (this.d - p2));
                            }
                            if (n3 == 0) {
                                this.p = p2;
                                n = this.b(zStream, n);
                                p2 = this.p;
                                n3 = ((p2 < this.o) ? (this.o - p2 - 1) : (this.d - p2));
                                if (p2 == this.d && this.o != 0) {
                                    p2 = 0;
                                    n3 = ((p2 < this.o) ? (this.o - p2 - 1) : (this.d - p2));
                                }
                                if (n3 == 0) {
                                    this.c = n2;
                                    this.t = i;
                                    zStream.avail_in = avail_in;
                                    zStream.total_in += next_in_index - zStream.next_in_index;
                                    zStream.next_in_index = next_in_index;
                                    this.p = p2;
                                    return this.b(zStream, n);
                                }
                            }
                        }
                        n = 0;
                        int e = this.e;
                        if (e > avail_in) {
                            e = avail_in;
                        }
                        if (e > n3) {
                            e = n3;
                        }
                        System.arraycopy(zStream.next_in, next_in_index, this.h, p2, e);
                        next_in_index += e;
                        avail_in -= e;
                        p2 += e;
                        n3 -= e;
                        if ((this.e -= e) != 0) {
                            continue;
                        }
                        this.m = ((this.k != 0) ? 7 : 0);
                        continue;
                    }
                    case 3: {
                        while (i < 14) {
                            if (avail_in == 0) {
                                this.c = n2;
                                this.t = i;
                                zStream.avail_in = avail_in;
                                zStream.total_in += next_in_index - zStream.next_in_index;
                                zStream.next_in_index = next_in_index;
                                this.p = p2;
                                return this.b(zStream, n);
                            }
                            n = 0;
                            --avail_in;
                            n2 |= (zStream.next_in[next_in_index++] & 0xFF) << i;
                            i += 8;
                        }
                        final int n7 = this.q = (n2 & 0x3FFF);
                        if ((n7 & 0x1F) > 29 || (n7 >> 5 & 0x1F) > 29) {
                            this.m = 9;
                            zStream.msg = "too many length or distance symbols";
                            n = -3;
                            this.c = n2;
                            this.t = i;
                            zStream.avail_in = avail_in;
                            zStream.total_in += next_in_index - zStream.next_in_index;
                            zStream.next_in_index = next_in_index;
                            this.p = p2;
                            return this.b(zStream, n);
                        }
                        this.j = new int[258 + (n7 & 0x1F) + (n7 >> 5 & 0x1F)];
                        n2 >>>= 14;
                        i -= 14;
                        this.g = 0;
                        this.m = 4;
                    }
                    case 4: {
                        while (this.g < 4 + (this.q >>> 10)) {
                            while (i < 3) {
                                if (avail_in == 0) {
                                    this.c = n2;
                                    this.t = i;
                                    zStream.avail_in = avail_in;
                                    zStream.total_in += next_in_index - zStream.next_in_index;
                                    zStream.next_in_index = next_in_index;
                                    this.p = p2;
                                    return this.b(zStream, n);
                                }
                                n = 0;
                                --avail_in;
                                n2 |= (zStream.next_in[next_in_index++] & 0xFF) << i;
                                i += 8;
                            }
                            this.j[com.sshtools.zlib.c.n[this.g++]] = (n2 & 0x7);
                            n2 >>>= 3;
                            i -= 3;
                        }
                        while (this.g < 19) {
                            this.j[com.sshtools.zlib.c.n[this.g++]] = 0;
                        }
                        this.f[0] = 7;
                        final int b = com.sshtools.zlib.h.b(this.j, this.f, this.i, this.b, zStream);
                        if (b != 0) {
                            n = b;
                            if (n == -3) {
                                this.j = null;
                                this.m = 9;
                            }
                            this.c = n2;
                            this.t = i;
                            zStream.avail_in = avail_in;
                            zStream.total_in += next_in_index - zStream.next_in_index;
                            zStream.next_in_index = next_in_index;
                            this.p = p2;
                            return this.b(zStream, n);
                        }
                        this.g = 0;
                        this.m = 5;
                    }
                    case 5: {
                        while (true) {
                            final int q = this.q;
                            if (this.g >= 258 + (q & 0x1F) + (q >> 5 & 0x1F)) {
                                this.i[0] = -1;
                                final int[] array5 = { 0 };
                                final int[] array6 = { 0 };
                                final int[] array7 = { 0 };
                                final int[] array8 = { 0 };
                                array5[0] = 9;
                                array6[0] = 6;
                                final int q2 = this.q;
                                final int b2 = com.sshtools.zlib.h.b(257 + (q2 & 0x1F), 1 + (q2 >> 5 & 0x1F), this.j, array5, array6, array7, array8, this.b, zStream);
                                if (b2 != 0) {
                                    if (b2 == -3) {
                                        this.j = null;
                                        this.m = 9;
                                    }
                                    n = b2;
                                    this.c = n2;
                                    this.t = i;
                                    zStream.avail_in = avail_in;
                                    zStream.total_in += next_in_index - zStream.next_in_index;
                                    zStream.next_in_index = next_in_index;
                                    this.p = p2;
                                    return this.b(zStream, n);
                                }
                                this.s = new g(array5[0], array6[0], this.b, array7[0], this.b, array8[0], zStream);
                                this.j = null;
                                this.m = 6;
                            }
                            else {
                                int n8;
                                for (n8 = this.f[0]; i < n8; i += 8) {
                                    if (avail_in == 0) {
                                        this.c = n2;
                                        this.t = i;
                                        zStream.avail_in = avail_in;
                                        zStream.total_in += next_in_index - zStream.next_in_index;
                                        zStream.next_in_index = next_in_index;
                                        this.p = p2;
                                        return this.b(zStream, n);
                                    }
                                    n = 0;
                                    --avail_in;
                                    n2 |= (zStream.next_in[next_in_index++] & 0xFF) << i;
                                }
                                if (this.i[0] == -1) {}
                                final int n9 = this.b[(this.i[0] + (n2 & com.sshtools.zlib.c.u[n8])) * 3 + 1];
                                final int n10 = this.b[(this.i[0] + (n2 & com.sshtools.zlib.c.u[n9])) * 3 + 2];
                                if (n10 < 16) {
                                    n2 >>>= n9;
                                    i -= n9;
                                    this.j[this.g++] = n10;
                                }
                                else {
                                    final int n11 = (n10 == 18) ? 7 : (n10 - 14);
                                    final int n12 = (n10 == 18) ? 11 : 3;
                                    while (i < n9 + n11) {
                                        if (avail_in == 0) {
                                            this.c = n2;
                                            this.t = i;
                                            zStream.avail_in = avail_in;
                                            zStream.total_in += next_in_index - zStream.next_in_index;
                                            zStream.next_in_index = next_in_index;
                                            this.p = p2;
                                            return this.b(zStream, n);
                                        }
                                        n = 0;
                                        --avail_in;
                                        n2 |= (zStream.next_in[next_in_index++] & 0xFF) << i;
                                        i += 8;
                                    }
                                    final int n13 = n2 >>> n9;
                                    final int n14 = i - n9;
                                    int n15 = n12 + (n13 & com.sshtools.zlib.c.u[n11]);
                                    n2 = n13 >>> n11;
                                    i = n14 - n11;
                                    int g = this.g;
                                    final int q3 = this.q;
                                    if (g + n15 > 258 + (q3 & 0x1F) + (q3 >> 5 & 0x1F) || (n10 == 16 && g < 1)) {
                                        this.j = null;
                                        this.m = 9;
                                        zStream.msg = "invalid bit length repeat";
                                        n = -3;
                                        this.c = n2;
                                        this.t = i;
                                        zStream.avail_in = avail_in;
                                        zStream.total_in += next_in_index - zStream.next_in_index;
                                        zStream.next_in_index = next_in_index;
                                        this.p = p2;
                                        return this.b(zStream, n);
                                    }
                                    final int n16 = (n10 == 16) ? this.j[g - 1] : 0;
                                    do {
                                        this.j[g++] = n16;
                                    } while (--n15 != 0);
                                    this.g = g;
                                }
                            }
                        }
                        break;
                    }
                    case 6: {
                        this.c = n2;
                        this.t = i;
                        zStream.avail_in = avail_in;
                        zStream.total_in += next_in_index - zStream.next_in_index;
                        zStream.next_in_index = next_in_index;
                        this.p = p2;
                        if ((n = this.s.b(this, zStream, n)) != 1) {
                            return this.b(zStream, n);
                        }
                        n = 0;
                        this.s.b(zStream);
                        next_in_index = zStream.next_in_index;
                        avail_in = zStream.avail_in;
                        n2 = this.c;
                        i = this.t;
                        p2 = this.p;
                        n3 = ((p2 < this.o) ? (this.o - p2 - 1) : (this.d - p2));
                        if (this.k == 0) {
                            this.m = 0;
                            continue;
                        }
                        this.m = 7;
                        break Label_2598;
                    }
                    case 7: {
                        break Label_2598;
                    }
                    case 8: {
                        break Label_2718;
                    }
                    case 9: {
                        n = -3;
                        this.c = n2;
                        this.t = i;
                        zStream.avail_in = avail_in;
                        zStream.total_in += next_in_index - zStream.next_in_index;
                        zStream.next_in_index = next_in_index;
                        this.p = p2;
                        return this.b(zStream, n);
                    }
                    default: {
                        n = -2;
                        this.c = n2;
                        this.t = i;
                        zStream.avail_in = avail_in;
                        zStream.total_in += next_in_index - zStream.next_in_index;
                        zStream.next_in_index = next_in_index;
                        this.p = p2;
                        return this.b(zStream, n);
                    }
                }
            }
            this.p = p2;
            n = this.b(zStream, n);
            p2 = this.p;
            final int n17 = (p2 < this.o) ? (this.o - p2 - 1) : (this.d - p2);
            if (this.o != this.p) {
                this.c = n2;
                this.t = i;
                zStream.avail_in = avail_in;
                zStream.total_in += next_in_index - zStream.next_in_index;
                zStream.next_in_index = next_in_index;
                this.p = p2;
                return this.b(zStream, n);
            }
            this.m = 8;
        }
        n = 1;
        this.c = n2;
        this.t = i;
        zStream.avail_in = avail_in;
        zStream.total_in += next_in_index - zStream.next_in_index;
        zStream.next_in_index = next_in_index;
        this.p = p2;
        return this.b(zStream, n);
    }
    
    void b(final ZStream zStream) {
        this.b(zStream, null);
        this.h = null;
        this.b = null;
    }
    
    void b(final byte[] array, final int n, final int n2) {
        System.arraycopy(array, n, this.h, 0, n2);
        this.p = n2;
        this.o = n2;
    }
    
    int b(final ZStream zStream, int n) {
        final int next_out_index = zStream.next_out_index;
        final int o = this.o;
        int avail_out = ((o <= this.p) ? this.p : this.d) - o;
        if (avail_out > zStream.avail_out) {
            avail_out = zStream.avail_out;
        }
        if (avail_out != 0 && n == -5) {
            n = 0;
        }
        zStream.avail_out -= avail_out;
        zStream.total_out += avail_out;
        if (this.l != null) {
            final long b = zStream.b.b(this.r, this.h, o, avail_out);
            this.r = b;
            zStream.adler = b;
        }
        System.arraycopy(this.h, o, zStream.next_out, next_out_index, avail_out);
        int next_out_index2 = next_out_index + avail_out;
        int o2 = o + avail_out;
        if (o2 == this.d) {
            final int n2 = 0;
            if (this.p == this.d) {
                this.p = 0;
            }
            int avail_out2 = this.p - n2;
            if (avail_out2 > zStream.avail_out) {
                avail_out2 = zStream.avail_out;
            }
            if (avail_out2 != 0 && n == -5) {
                n = 0;
            }
            zStream.avail_out -= avail_out2;
            zStream.total_out += avail_out2;
            if (this.l != null) {
                final long b2 = zStream.b.b(this.r, this.h, n2, avail_out2);
                this.r = b2;
                zStream.adler = b2;
            }
            System.arraycopy(this.h, n2, zStream.next_out, next_out_index2, avail_out2);
            next_out_index2 += avail_out2;
            o2 = n2 + avail_out2;
        }
        zStream.next_out_index = next_out_index2;
        this.o = o2;
        return n;
    }
    
    static {
        u = new int[] { 0, 1, 3, 7, 15, 31, 63, 127, 255, 511, 1023, 2047, 4095, 8191, 16383, 32767, 65535 };
        n = new int[] { 16, 17, 18, 0, 8, 7, 9, 6, 10, 5, 11, 4, 12, 3, 13, 2, 14, 1, 15 };
    }
}
