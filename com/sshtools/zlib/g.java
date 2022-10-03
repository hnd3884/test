package com.sshtools.zlib;

final class g
{
    private static final int[] c;
    int j;
    int k;
    int[] p;
    int h;
    int n;
    int o;
    int f;
    int m;
    byte i;
    byte e;
    int[] l;
    int d;
    int[] g;
    int b;
    
    g(final int n, final int n2, final int[] l, final int d, final int[] g, final int b, final ZStream zStream) {
        this.h = 0;
        this.j = 0;
        this.i = (byte)n;
        this.e = (byte)n2;
        this.l = l;
        this.d = d;
        this.g = g;
        this.b = b;
    }
    
    g(final int n, final int n2, final int[] l, final int[] g, final ZStream zStream) {
        this.h = 0;
        this.j = 0;
        this.i = (byte)n;
        this.e = (byte)n2;
        this.l = l;
        this.d = 0;
        this.g = g;
        this.b = 0;
    }
    
    int b(final c c, final ZStream zStream, int n) {
        int next_in_index = zStream.next_in_index;
        int avail_in = zStream.avail_in;
        int c2 = c.c;
        int i = c.t;
        int n2 = c.p;
        int n3 = (n2 < c.o) ? (c.o - n2 - 1) : (c.d - n2);
        Label_2099: {
            Label_2042: {
            Label_1986:
                while (true) {
                    switch (this.j) {
                        case 0: {
                            if (n3 >= 258 && avail_in >= 10) {
                                c.c = c2;
                                c.t = i;
                                zStream.avail_in = avail_in;
                                zStream.total_in += next_in_index - zStream.next_in_index;
                                zStream.next_in_index = next_in_index;
                                c.p = n2;
                                n = this.b(this.i, this.e, this.l, this.d, this.g, this.b, c, zStream);
                                next_in_index = zStream.next_in_index;
                                avail_in = zStream.avail_in;
                                c2 = c.c;
                                i = c.t;
                                n2 = c.p;
                                n3 = ((n2 < c.o) ? (c.o - n2 - 1) : (c.d - n2));
                                if (n != 0) {
                                    this.j = ((n == 1) ? 7 : 9);
                                    continue;
                                }
                            }
                            this.n = this.i;
                            this.p = this.l;
                            this.h = this.d;
                            this.j = 1;
                        }
                        case 1: {
                            int n4;
                            for (n4 = this.n; i < n4; i += 8) {
                                if (avail_in == 0) {
                                    c.c = c2;
                                    c.t = i;
                                    zStream.avail_in = avail_in;
                                    zStream.total_in += next_in_index - zStream.next_in_index;
                                    zStream.next_in_index = next_in_index;
                                    c.p = n2;
                                    return c.b(zStream, n);
                                }
                                n = 0;
                                --avail_in;
                                c2 |= (zStream.next_in[next_in_index++] & 0xFF) << i;
                            }
                            final int n5 = (this.h + (c2 & com.sshtools.zlib.g.c[n4])) * 3;
                            c2 >>>= this.p[n5 + 1];
                            i -= this.p[n5 + 1];
                            final int n6 = this.p[n5];
                            if (n6 == 0) {
                                this.o = this.p[n5 + 2];
                                this.j = 6;
                                continue;
                            }
                            if ((n6 & 0x10) != 0x0) {
                                this.f = (n6 & 0xF);
                                this.k = this.p[n5 + 2];
                                this.j = 2;
                                continue;
                            }
                            if ((n6 & 0x40) == 0x0) {
                                this.n = n6;
                                this.h = n5 / 3 + this.p[n5 + 2];
                                continue;
                            }
                            if ((n6 & 0x20) != 0x0) {
                                this.j = 7;
                                continue;
                            }
                            this.j = 9;
                            zStream.msg = "invalid literal/length code";
                            n = -3;
                            c.c = c2;
                            c.t = i;
                            zStream.avail_in = avail_in;
                            zStream.total_in += next_in_index - zStream.next_in_index;
                            zStream.next_in_index = next_in_index;
                            c.p = n2;
                            return c.b(zStream, n);
                        }
                        case 2: {
                            int f;
                            for (f = this.f; i < f; i += 8) {
                                if (avail_in == 0) {
                                    c.c = c2;
                                    c.t = i;
                                    zStream.avail_in = avail_in;
                                    zStream.total_in += next_in_index - zStream.next_in_index;
                                    zStream.next_in_index = next_in_index;
                                    c.p = n2;
                                    return c.b(zStream, n);
                                }
                                n = 0;
                                --avail_in;
                                c2 |= (zStream.next_in[next_in_index++] & 0xFF) << i;
                            }
                            this.k += (c2 & com.sshtools.zlib.g.c[f]);
                            c2 >>= f;
                            i -= f;
                            this.n = this.e;
                            this.p = this.g;
                            this.h = this.b;
                            this.j = 3;
                        }
                        case 3: {
                            int n7;
                            for (n7 = this.n; i < n7; i += 8) {
                                if (avail_in == 0) {
                                    c.c = c2;
                                    c.t = i;
                                    zStream.avail_in = avail_in;
                                    zStream.total_in += next_in_index - zStream.next_in_index;
                                    zStream.next_in_index = next_in_index;
                                    c.p = n2;
                                    return c.b(zStream, n);
                                }
                                n = 0;
                                --avail_in;
                                c2 |= (zStream.next_in[next_in_index++] & 0xFF) << i;
                            }
                            final int n8 = (this.h + (c2 & com.sshtools.zlib.g.c[n7])) * 3;
                            c2 >>= this.p[n8 + 1];
                            i -= this.p[n8 + 1];
                            final int n9 = this.p[n8];
                            if ((n9 & 0x10) != 0x0) {
                                this.f = (n9 & 0xF);
                                this.m = this.p[n8 + 2];
                                this.j = 4;
                                continue;
                            }
                            if ((n9 & 0x40) == 0x0) {
                                this.n = n9;
                                this.h = n8 / 3 + this.p[n8 + 2];
                                continue;
                            }
                            this.j = 9;
                            zStream.msg = "invalid distance code";
                            n = -3;
                            c.c = c2;
                            c.t = i;
                            zStream.avail_in = avail_in;
                            zStream.total_in += next_in_index - zStream.next_in_index;
                            zStream.next_in_index = next_in_index;
                            c.p = n2;
                            return c.b(zStream, n);
                        }
                        case 4: {
                            int f2;
                            for (f2 = this.f; i < f2; i += 8) {
                                if (avail_in == 0) {
                                    c.c = c2;
                                    c.t = i;
                                    zStream.avail_in = avail_in;
                                    zStream.total_in += next_in_index - zStream.next_in_index;
                                    zStream.next_in_index = next_in_index;
                                    c.p = n2;
                                    return c.b(zStream, n);
                                }
                                n = 0;
                                --avail_in;
                                c2 |= (zStream.next_in[next_in_index++] & 0xFF) << i;
                            }
                            this.m += (c2 & com.sshtools.zlib.g.c[f2]);
                            c2 >>= f2;
                            i -= f2;
                            this.j = 5;
                        }
                        case 5: {
                            int j;
                            for (j = n2 - this.m; j < 0; j += c.d) {}
                            while (this.k != 0) {
                                if (n3 == 0) {
                                    if (n2 == c.d && c.o != 0) {
                                        n2 = 0;
                                        n3 = ((n2 < c.o) ? (c.o - n2 - 1) : (c.d - n2));
                                    }
                                    if (n3 == 0) {
                                        c.p = n2;
                                        n = c.b(zStream, n);
                                        n2 = c.p;
                                        n3 = ((n2 < c.o) ? (c.o - n2 - 1) : (c.d - n2));
                                        if (n2 == c.d && c.o != 0) {
                                            n2 = 0;
                                            n3 = ((n2 < c.o) ? (c.o - n2 - 1) : (c.d - n2));
                                        }
                                        if (n3 == 0) {
                                            c.c = c2;
                                            c.t = i;
                                            zStream.avail_in = avail_in;
                                            zStream.total_in += next_in_index - zStream.next_in_index;
                                            zStream.next_in_index = next_in_index;
                                            c.p = n2;
                                            return c.b(zStream, n);
                                        }
                                    }
                                }
                                c.h[n2++] = c.h[j++];
                                --n3;
                                if (j == c.d) {
                                    j = 0;
                                }
                                --this.k;
                            }
                            this.j = 0;
                            continue;
                        }
                        case 6: {
                            if (n3 == 0) {
                                if (n2 == c.d && c.o != 0) {
                                    n2 = 0;
                                    n3 = ((n2 < c.o) ? (c.o - n2 - 1) : (c.d - n2));
                                }
                                if (n3 == 0) {
                                    c.p = n2;
                                    n = c.b(zStream, n);
                                    n2 = c.p;
                                    n3 = ((n2 < c.o) ? (c.o - n2 - 1) : (c.d - n2));
                                    if (n2 == c.d && c.o != 0) {
                                        n2 = 0;
                                        n3 = ((n2 < c.o) ? (c.o - n2 - 1) : (c.d - n2));
                                    }
                                    if (n3 == 0) {
                                        c.c = c2;
                                        c.t = i;
                                        zStream.avail_in = avail_in;
                                        zStream.total_in += next_in_index - zStream.next_in_index;
                                        zStream.next_in_index = next_in_index;
                                        c.p = n2;
                                        return c.b(zStream, n);
                                    }
                                }
                            }
                            n = 0;
                            c.h[n2++] = (byte)this.o;
                            --n3;
                            this.j = 0;
                            continue;
                        }
                        case 7: {
                            if (i > 7) {
                                i -= 8;
                                ++avail_in;
                                --next_in_index;
                            }
                            c.p = n2;
                            n = c.b(zStream, n);
                            n2 = c.p;
                            final int n10 = (n2 < c.o) ? (c.o - n2 - 1) : (c.d - n2);
                            if (c.o != c.p) {
                                c.c = c2;
                                c.t = i;
                                zStream.avail_in = avail_in;
                                zStream.total_in += next_in_index - zStream.next_in_index;
                                zStream.next_in_index = next_in_index;
                                c.p = n2;
                                return c.b(zStream, n);
                            }
                            this.j = 8;
                            break Label_1986;
                        }
                        case 8: {
                            break Label_1986;
                        }
                        case 9: {
                            break Label_2042;
                        }
                        default: {
                            break Label_2099;
                        }
                    }
                }
                n = 1;
                c.c = c2;
                c.t = i;
                zStream.avail_in = avail_in;
                zStream.total_in += next_in_index - zStream.next_in_index;
                zStream.next_in_index = next_in_index;
                c.p = n2;
                return c.b(zStream, n);
            }
            n = -3;
            c.c = c2;
            c.t = i;
            zStream.avail_in = avail_in;
            zStream.total_in += next_in_index - zStream.next_in_index;
            zStream.next_in_index = next_in_index;
            c.p = n2;
            return c.b(zStream, n);
        }
        n = -2;
        c.c = c2;
        c.t = i;
        zStream.avail_in = avail_in;
        zStream.total_in += next_in_index - zStream.next_in_index;
        zStream.next_in_index = next_in_index;
        c.p = n2;
        return c.b(zStream, n);
    }
    
    void b(final ZStream zStream) {
    }
    
    int b(final int n, final int n2, final int[] array, final int n3, final int[] array2, final int n4, final c c, final ZStream zStream) {
        int next_in_index = zStream.next_in_index;
        int avail_in = zStream.avail_in;
        int c2 = c.c;
        int t = c.t;
        int p8 = c.p;
        int n5 = (p8 < c.o) ? (c.o - p8 - 1) : (c.d - p8);
        final int n6 = com.sshtools.zlib.g.c[n];
        final int n7 = com.sshtools.zlib.g.c[n2];
        while (true) {
            if (t < 20) {
                --avail_in;
                c2 |= (zStream.next_in[next_in_index++] & 0xFF) << t;
                t += 8;
            }
            else {
                int n8 = c2 & n6;
                Label_1365: {
                    int n9;
                    if ((n9 = array[(n3 + n8) * 3]) == 0) {
                        c2 >>= array[(n3 + n8) * 3 + 1];
                        t -= array[(n3 + n8) * 3 + 1];
                        c.h[p8++] = (byte)array[(n3 + n8) * 3 + 2];
                        --n5;
                    }
                    else {
                        do {
                            c2 >>= array[(n3 + n8) * 3 + 1];
                            t -= array[(n3 + n8) * 3 + 1];
                            if ((n9 & 0x10) != 0x0) {
                                final int n10 = n9 & 0xF;
                                int n11 = array[(n3 + n8) * 3 + 2] + (c2 & com.sshtools.zlib.g.c[n10]);
                                int c3 = c2 >> n10;
                                int i;
                                for (i = t - n10; i < 15; i += 8) {
                                    --avail_in;
                                    c3 |= (zStream.next_in[next_in_index++] & 0xFF) << i;
                                }
                                int n12 = c3 & n7;
                                int n13 = array2[(n4 + n12) * 3];
                                while (true) {
                                    c3 >>= array2[(n4 + n12) * 3 + 1];
                                    i -= array2[(n4 + n12) * 3 + 1];
                                    if ((n13 & 0x10) != 0x0) {
                                        int n14;
                                        for (n14 = (n13 & 0xF); i < n14; i += 8) {
                                            --avail_in;
                                            c3 |= (zStream.next_in[next_in_index++] & 0xFF) << i;
                                        }
                                        final int n15 = array2[(n4 + n12) * 3 + 2] + (c3 & com.sshtools.zlib.g.c[n14]);
                                        c2 = c3 >> n14;
                                        t = i - n14;
                                        n5 -= n11;
                                        int j;
                                        if (p8 >= n15) {
                                            j = p8 - n15;
                                            if (p8 - j > 0 && 2 > p8 - j) {
                                                c.h[p8++] = c.h[j++];
                                                --n11;
                                                c.h[p8++] = c.h[j++];
                                                --n11;
                                            }
                                            else {
                                                System.arraycopy(c.h, j, c.h, p8, 2);
                                                p8 += 2;
                                                j += 2;
                                                n11 -= 2;
                                            }
                                        }
                                        else {
                                            j = p8 - n15;
                                            do {
                                                j += c.d;
                                            } while (j < 0);
                                            int n16 = c.d - j;
                                            if (n11 > n16) {
                                                n11 -= n16;
                                                if (p8 - j > 0 && n16 > p8 - j) {
                                                    do {
                                                        c.h[p8++] = c.h[j++];
                                                    } while (--n16 != 0);
                                                }
                                                else {
                                                    System.arraycopy(c.h, j, c.h, p8, n16);
                                                    p8 += n16;
                                                }
                                                j = 0;
                                            }
                                        }
                                        if (p8 - j > 0 && n11 > p8 - j) {
                                            do {
                                                c.h[p8++] = c.h[j++];
                                            } while (--n11 != 0);
                                            break Label_1365;
                                        }
                                        System.arraycopy(c.h, j, c.h, p8, n11);
                                        p8 += n11;
                                        break Label_1365;
                                    }
                                    else {
                                        if ((n13 & 0x40) != 0x0) {
                                            zStream.msg = "invalid distance code";
                                            final int n17 = zStream.avail_in - avail_in;
                                            final int n18 = (i >> 3 < n17) ? (i >> 3) : n17;
                                            final int avail_in2 = avail_in + n18;
                                            final int next_in_index2 = next_in_index - n18;
                                            final int t2 = i - (n18 << 3);
                                            c.c = c3;
                                            c.t = t2;
                                            zStream.avail_in = avail_in2;
                                            zStream.total_in += next_in_index2 - zStream.next_in_index;
                                            zStream.next_in_index = next_in_index2;
                                            c.p = p8;
                                            return -3;
                                        }
                                        n12 = n12 + array2[(n4 + n12) * 3 + 2] + (c3 & com.sshtools.zlib.g.c[n13]);
                                        n13 = array2[(n4 + n12) * 3];
                                    }
                                }
                            }
                            else if ((n9 & 0x40) == 0x0) {
                                n8 = n8 + array[(n3 + n8) * 3 + 2] + (c2 & com.sshtools.zlib.g.c[n9]);
                            }
                            else {
                                if ((n9 & 0x20) != 0x0) {
                                    final int n19 = zStream.avail_in - avail_in;
                                    final int n20 = (t >> 3 < n19) ? (t >> 3) : n19;
                                    final int avail_in3 = avail_in + n20;
                                    final int next_in_index3 = next_in_index - n20;
                                    final int t3 = t - (n20 << 3);
                                    c.c = c2;
                                    c.t = t3;
                                    zStream.avail_in = avail_in3;
                                    zStream.total_in += next_in_index3 - zStream.next_in_index;
                                    zStream.next_in_index = next_in_index3;
                                    c.p = p8;
                                    return 1;
                                }
                                zStream.msg = "invalid literal/length code";
                                final int n21 = zStream.avail_in - avail_in;
                                final int n22 = (t >> 3 < n21) ? (t >> 3) : n21;
                                final int avail_in4 = avail_in + n22;
                                final int next_in_index4 = next_in_index - n22;
                                final int t4 = t - (n22 << 3);
                                c.c = c2;
                                c.t = t4;
                                zStream.avail_in = avail_in4;
                                zStream.total_in += next_in_index4 - zStream.next_in_index;
                                zStream.next_in_index = next_in_index4;
                                c.p = p8;
                                return -3;
                            }
                        } while ((n9 = array[(n3 + n8) * 3]) != 0);
                        c2 >>= array[(n3 + n8) * 3 + 1];
                        t -= array[(n3 + n8) * 3 + 1];
                        c.h[p8++] = (byte)array[(n3 + n8) * 3 + 2];
                        --n5;
                    }
                }
                if (n5 < 258 || avail_in < 10) {
                    final int n23 = zStream.avail_in - avail_in;
                    final int n24 = (t >> 3 < n23) ? (t >> 3) : n23;
                    final int avail_in5 = avail_in + n24;
                    final int next_in_index5 = next_in_index - n24;
                    final int t5 = t - (n24 << 3);
                    c.c = c2;
                    c.t = t5;
                    zStream.avail_in = avail_in5;
                    zStream.total_in += next_in_index5 - zStream.next_in_index;
                    zStream.next_in_index = next_in_index5;
                    c.p = p8;
                    return 0;
                }
                continue;
            }
        }
    }
    
    static {
        c = new int[] { 0, 1, 3, 7, 15, 31, 63, 127, 255, 511, 1023, 2047, 4095, 8191, 16383, 32767, 65535 };
    }
}
