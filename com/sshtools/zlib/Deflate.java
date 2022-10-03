package com.sshtools.zlib;

public final class Deflate
{
    private static _b[] f;
    private static final String[] t;
    ZStream fb;
    int lb;
    byte[] bc;
    int gc;
    int l;
    int q;
    int hc;
    byte ib;
    byte ec;
    int y;
    int kb;
    int db;
    int u;
    byte[] yb;
    int x;
    short[] jb;
    short[] v;
    int b;
    int g;
    int dc;
    int tb;
    int wb;
    int h;
    int i;
    int o;
    int ac;
    int nb;
    int qb;
    int fc;
    int gb;
    int e;
    int eb;
    int j;
    int mb;
    int zb;
    int ub;
    short[] p;
    short[] cb;
    short[] z;
    d hb;
    d bb;
    d vb;
    short[] xb;
    int[] k;
    int n;
    int s;
    byte[] m;
    int cc;
    int sb;
    int w;
    int d;
    int rb;
    int ob;
    int c;
    int ab;
    short r;
    int pb;
    
    Deflate() {
        this.hb = new d();
        this.bb = new d();
        this.vb = new d();
        this.xb = new short[16];
        this.k = new int[573];
        this.m = new byte[573];
        this.p = new short[1146];
        this.cb = new short[122];
        this.z = new short[78];
    }
    
    void g() {
        this.x = 2 * this.kb;
        this.v[this.g - 1] = 0;
        for (int i = 0; i < this.g - 1; ++i) {
            this.v[i] = 0;
        }
        this.eb = Deflate.f[this.j].f;
        this.zb = Deflate.f[this.j].b;
        this.ub = Deflate.f[this.j].c;
        this.e = Deflate.f[this.j].e;
        this.nb = 0;
        this.h = 0;
        this.fc = 0;
        final int n = 2;
        this.gb = n;
        this.i = n;
        this.ac = 0;
        this.b = 0;
    }
    
    void c() {
        this.hb.b = this.p;
        this.hb.g = com.sshtools.zlib.f.f;
        this.bb.b = this.cb;
        this.bb.g = com.sshtools.zlib.f.d;
        this.vb.b = this.z;
        this.vb.g = com.sshtools.zlib.f.e;
        this.r = 0;
        this.pb = 0;
        this.ab = 8;
        this.j();
    }
    
    void j() {
        for (int i = 0; i < 286; ++i) {
            this.p[i * 2] = 0;
        }
        for (int j = 0; j < 30; ++j) {
            this.cb[j * 2] = 0;
        }
        for (int k = 0; k < 19; ++k) {
            this.z[k * 2] = 0;
        }
        this.p[512] = 1;
        final int n = 0;
        this.ob = n;
        this.rb = n;
        final int n2 = 0;
        this.c = n2;
        this.w = n2;
    }
    
    void c(final short[] array, int n) {
        final int n2 = this.k[n];
        for (int i = n << 1; i <= this.n; i <<= 1) {
            if (i < this.n && b(array, this.k[i + 1], this.k[i], this.m)) {
                ++i;
            }
            if (b(array, n2, this.k[i], this.m)) {
                break;
            }
            this.k[n] = this.k[i];
            n = i;
        }
        this.k[n] = n2;
    }
    
    static boolean b(final short[] array, final int n, final int n2, final byte[] array2) {
        return array[n * 2] < array[n2 * 2] || (array[n * 2] == array[n2 * 2] && array2[n] <= array2[n2]);
    }
    
    void b(final short[] array, final int n) {
        short n2 = -1;
        short n3 = array[1];
        short n4 = 0;
        short n5 = 7;
        short n6 = 4;
        if (n3 == 0) {
            n5 = 138;
            n6 = 3;
        }
        array[(n + 1) * 2 + 1] = -1;
        for (int i = 0; i <= n; ++i) {
            final short n7 = n3;
            n3 = array[(i + 1) * 2 + 1];
            if (++n4 >= n5 || n7 != n3) {
                if (n4 < n6) {
                    final short[] z = this.z;
                    final int n8 = n7 * 2;
                    z[n8] += n4;
                }
                else if (n7 != 0) {
                    if (n7 != n2) {
                        final short[] z2 = this.z;
                        final int n9 = n7 * 2;
                        ++z2[n9];
                    }
                    final short[] z3 = this.z;
                    final int n10 = 32;
                    ++z3[n10];
                }
                else if (n4 <= 10) {
                    final short[] z4 = this.z;
                    final int n11 = 34;
                    ++z4[n11];
                }
                else {
                    final short[] z5 = this.z;
                    final int n12 = 36;
                    ++z5[n12];
                }
                n4 = 0;
                n2 = n7;
                if (n3 == 0) {
                    n5 = 138;
                    n6 = 3;
                }
                else if (n7 == n3) {
                    n5 = 6;
                    n6 = 3;
                }
                else {
                    n5 = 7;
                    n6 = 4;
                }
            }
        }
    }
    
    int h() {
        this.b(this.p, this.hb.k);
        this.b(this.cb, this.bb.k);
        this.vb.b(this);
        int n;
        for (n = 18; n >= 3 && this.z[com.sshtools.zlib.d.e[n] * 2 + 1] == 0; --n) {}
        this.rb += 3 * (n + 1) + 5 + 5 + 4;
        return n;
    }
    
    void b(final int n, final int n2, final int n3) {
        this.b(n - 257, 5);
        this.b(n2 - 1, 5);
        this.b(n3 - 4, 4);
        for (int i = 0; i < n3; ++i) {
            this.b(this.z[com.sshtools.zlib.d.e[i] * 2 + 1], 3);
        }
        this.d(this.p, n - 1);
        this.d(this.cb, n2 - 1);
    }
    
    void d(final short[] array, final int n) {
        short n2 = -1;
        short n3 = array[1];
        int n4 = 0;
        int n5 = 7;
        int n6 = 4;
        if (n3 == 0) {
            n5 = 138;
            n6 = 3;
        }
        for (int i = 0; i <= n; ++i) {
            final short n7 = n3;
            n3 = array[(i + 1) * 2 + 1];
            if (++n4 >= n5 || n7 != n3) {
                if (n4 < n6) {
                    do {
                        this.b(n7, this.z);
                    } while (--n4 != 0);
                }
                else if (n7 != 0) {
                    if (n7 != n2) {
                        this.b(n7, this.z);
                        --n4;
                    }
                    this.b(16, this.z);
                    this.b(n4 - 3, 2);
                }
                else if (n4 <= 10) {
                    this.b(17, this.z);
                    this.b(n4 - 3, 3);
                }
                else {
                    this.b(18, this.z);
                    this.b(n4 - 11, 7);
                }
                n4 = 0;
                n2 = n7;
                if (n3 == 0) {
                    n5 = 138;
                    n6 = 3;
                }
                else if (n7 == n3) {
                    n5 = 6;
                    n6 = 3;
                }
                else {
                    n5 = 7;
                    n6 = 4;
                }
            }
        }
    }
    
    final void b(final byte[] array, final int n, final int n2) {
        System.arraycopy(array, n, this.bc, this.q, n2);
        this.q += n2;
    }
    
    final void b(final byte b) {
        this.bc[this.q++] = b;
    }
    
    final void c(final int n) {
        this.b((byte)n);
        this.b((byte)(n >>> 8));
    }
    
    final void f(final int n) {
        this.b((byte)(n >> 8));
        this.b((byte)n);
    }
    
    final void b(final int n, final short[] array) {
        this.b(array[n * 2] & 0xFFFF, array[n * 2 + 1] & 0xFFFF);
    }
    
    void b(final int n, final int n2) {
        if (this.pb > 16 - n2) {
            this.c(this.r |= (short)(n << this.pb & 0xFFFF));
            this.r = (short)(n >>> 16 - this.pb);
            this.pb += n2 - 16;
        }
        else {
            this.r |= (short)(n << this.pb & 0xFFFF);
            this.pb += n2;
        }
    }
    
    void d() {
        this.b(2, 3);
        this.b(256, com.sshtools.zlib.f.k);
        this.b();
        if (1 + this.ab + 10 - this.pb < 9) {
            this.b(2, 3);
            this.b(256, com.sshtools.zlib.f.k);
            this.b();
        }
        this.ab = 7;
    }
    
    boolean c(int n, final int n2) {
        this.bc[this.d + this.w * 2] = (byte)(n >>> 8);
        this.bc[this.d + this.w * 2 + 1] = (byte)n;
        this.bc[this.cc + this.w] = (byte)n2;
        ++this.w;
        if (n == 0) {
            final short[] p2 = this.p;
            final int n3 = n2 * 2;
            ++p2[n3];
        }
        else {
            ++this.c;
            --n;
            final short[] p3 = this.p;
            final int n4 = (com.sshtools.zlib.d.c[n2] + 256 + 1) * 2;
            ++p3[n4];
            final short[] cb = this.cb;
            final int n5 = com.sshtools.zlib.d.b(n) * 2;
            ++cb[n5];
        }
        if ((this.w & 0x1FFF) == 0x0 && this.j > 2) {
            int n6 = this.w * 8;
            final int n7 = this.nb - this.h;
            for (int i = 0; i < 30; ++i) {
                n6 += (int)(this.cb[i * 2] * (5L + com.sshtools.zlib.d.f[i]));
            }
            final int n8 = n6 >>> 3;
            if (this.c < this.w / 2 && n8 < n7 / 2) {
                return true;
            }
        }
        return this.w == this.sb - 1;
    }
    
    void b(final short[] array, final short[] array2) {
        int i = 0;
        if (this.w != 0) {
            do {
                int n = (this.bc[this.d + i * 2] << 8 & 0xFF00) | (this.bc[this.d + i * 2 + 1] & 0xFF);
                final int n2 = this.bc[this.cc + i] & 0xFF;
                ++i;
                if (n == 0) {
                    this.b(n2, array);
                }
                else {
                    final byte b = com.sshtools.zlib.d.c[n2];
                    this.b(b + 256 + 1, array);
                    final int n3 = com.sshtools.zlib.d.j[b];
                    if (n3 != 0) {
                        this.b(n2 - com.sshtools.zlib.d.h[b], n3);
                    }
                    final int b2 = com.sshtools.zlib.d.b(--n);
                    this.b(b2, array2);
                    final int n4 = com.sshtools.zlib.d.f[b2];
                    if (n4 == 0) {
                        continue;
                    }
                    this.b(n - com.sshtools.zlib.d.l[b2], n4);
                }
            } while (i < this.w);
        }
        this.b(256, array);
        this.ab = array[513];
    }
    
    void e() {
        int i = 0;
        int n = 0;
        int n2 = 0;
        while (i < 7) {
            n2 += this.p[i * 2];
            ++i;
        }
        while (i < 128) {
            n += this.p[i * 2];
            ++i;
        }
        while (i < 256) {
            n2 += this.p[i * 2];
            ++i;
        }
        this.ib = (byte)((n2 <= n >>> 2) ? 1 : 0);
    }
    
    void b() {
        if (this.pb == 16) {
            this.c(this.r);
            this.r = 0;
            this.pb = 0;
        }
        else if (this.pb >= 8) {
            this.b((byte)this.r);
            this.r >>>= 8;
            this.pb -= 8;
        }
    }
    
    void f() {
        if (this.pb > 8) {
            this.c(this.r);
        }
        else if (this.pb > 0) {
            this.b((byte)this.r);
        }
        this.r = 0;
        this.pb = 0;
    }
    
    void b(final int n, final int n2, final boolean b) {
        this.f();
        this.ab = 8;
        if (b) {
            this.c((short)n2);
            this.c((short)~n2);
        }
        this.b(this.yb, n, n2);
    }
    
    void b(final boolean b) {
        this.d((this.h >= 0) ? this.h : -1, this.nb - this.h, b);
        this.h = this.nb;
        this.fb.b();
    }
    
    int e(final int n) {
        int n2 = 65535;
        if (n2 > this.gc - 5) {
            n2 = this.gc - 5;
        }
        while (true) {
            if (this.fc <= 1) {
                this.i();
                if (this.fc == 0 && n == 0) {
                    return 0;
                }
                if (this.fc == 0) {
                    this.b(n == 4);
                    if (this.fb.avail_out == 0) {
                        return (n == 4) ? 2 : 0;
                    }
                    return (n == 4) ? 3 : 1;
                }
            }
            this.nb += this.fc;
            this.fc = 0;
            final int nb = this.h + n2;
            if (this.nb == 0 || this.nb >= nb) {
                this.fc = this.nb - nb;
                this.nb = nb;
                this.b(false);
                if (this.fb.avail_out == 0) {
                    return 0;
                }
            }
            if (this.nb - this.h >= this.kb - 262) {
                this.b(false);
                if (this.fb.avail_out == 0) {
                    return 0;
                }
                continue;
            }
        }
    }
    
    void c(final int n, final int n2, final boolean b) {
        this.b(0 + (b ? 1 : 0), 3);
        this.b(n, n2, true);
    }
    
    void d(final int n, final int n2, final boolean b) {
        int h = 0;
        int n3;
        int n4;
        if (this.j > 0) {
            if (this.ib == 2) {
                this.e();
            }
            this.hb.b(this);
            this.bb.b(this);
            h = this.h();
            n3 = this.rb + 3 + 7 >>> 3;
            n4 = this.ob + 3 + 7 >>> 3;
            if (n4 <= n3) {
                n3 = n4;
            }
        }
        else {
            n4 = (n3 = n2 + 5);
        }
        if (n2 + 4 <= n3 && n != -1) {
            this.c(n, n2, b);
        }
        else if (n4 == n3) {
            this.b(2 + (b ? 1 : 0), 3);
            this.b(com.sshtools.zlib.f.k, com.sshtools.zlib.f.g);
        }
        else {
            this.b(4 + (b ? 1 : 0), 3);
            this.b(this.hb.k + 1, this.bb.k + 1, h + 1);
            this.b(this.p, this.cb);
        }
        this.j();
        if (b) {
            this.f();
        }
    }
    
    void i() {
        do {
            int kb = this.x - this.fc - this.nb;
            if (kb == 0 && this.nb == 0 && this.fc == 0) {
                kb = this.kb;
            }
            else if (kb == -1) {
                --kb;
            }
            else if (this.nb >= this.kb + this.kb - 262) {
                System.arraycopy(this.yb, this.kb, this.yb, 0, this.kb);
                this.qb -= this.kb;
                this.nb -= this.kb;
                this.h -= this.kb;
                int g;
                int n = g = this.g;
                do {
                    final int n2 = this.v[--g] & 0xFFFF;
                    this.v[g] = (short)((n2 >= this.kb) ? ((short)(n2 - this.kb)) : 0);
                } while (--n != 0);
                int kb2;
                int n3 = kb2 = this.kb;
                do {
                    final int n4 = this.jb[--kb2] & 0xFFFF;
                    this.jb[kb2] = (short)((n4 >= this.kb) ? ((short)(n4 - this.kb)) : 0);
                } while (--n3 != 0);
                kb += this.kb;
            }
            if (this.fb.avail_in == 0) {
                return;
            }
            this.fc += this.fb.b(this.yb, this.nb + this.fc, kb);
            if (this.fc < 3) {
                continue;
            }
            this.b = (this.yb[this.nb] & 0xFF);
            this.b = ((this.b << this.wb ^ (this.yb[this.nb + 1] & 0xFF)) & this.tb);
        } while (this.fc < 262 && this.fb.avail_in != 0);
    }
    
    int g(final int n) {
        int n2 = 0;
        while (true) {
            if (this.fc < 262) {
                this.i();
                if (this.fc < 262 && n == 0) {
                    return 0;
                }
                if (this.fc == 0) {
                    this.b(n == 4);
                    if (this.fb.avail_out != 0) {
                        return (n == 4) ? 3 : 1;
                    }
                    if (n == 4) {
                        return 2;
                    }
                    return 0;
                }
            }
            if (this.fc >= 3) {
                this.b = ((this.b << this.wb ^ (this.yb[this.nb + 2] & 0xFF)) & this.tb);
                n2 = (this.v[this.b] & 0xFFFF);
                this.jb[this.nb & this.u] = this.v[this.b];
                this.v[this.b] = (short)this.nb;
            }
            if (n2 != 0L && (this.nb - n2 & 0xFFFF) <= this.kb - 262 && this.mb != 2) {
                this.i = this.b(n2);
            }
            boolean b;
            if (this.i >= 3) {
                b = this.c(this.nb - this.qb, this.i - 3);
                this.fc -= this.i;
                if (this.i <= this.eb && this.fc >= 3) {
                    --this.i;
                    do {
                        ++this.nb;
                        this.b = ((this.b << this.wb ^ (this.yb[this.nb + 2] & 0xFF)) & this.tb);
                        n2 = (this.v[this.b] & 0xFFFF);
                        this.jb[this.nb & this.u] = this.v[this.b];
                        this.v[this.b] = (short)this.nb;
                    } while (--this.i != 0);
                    ++this.nb;
                }
                else {
                    this.nb += this.i;
                    this.i = 0;
                    this.b = (this.yb[this.nb] & 0xFF);
                    this.b = ((this.b << this.wb ^ (this.yb[this.nb + 1] & 0xFF)) & this.tb);
                }
            }
            else {
                b = this.c(0, this.yb[this.nb] & 0xFF);
                --this.fc;
                ++this.nb;
            }
            if (b) {
                this.b(false);
                if (this.fb.avail_out == 0) {
                    return 0;
                }
                continue;
            }
        }
    }
    
    int d(final int n) {
        int n2 = 0;
        while (true) {
            if (this.fc < 262) {
                this.i();
                if (this.fc < 262 && n == 0) {
                    return 0;
                }
                if (this.fc == 0) {
                    if (this.ac != 0) {
                        this.c(0, this.yb[this.nb - 1] & 0xFF);
                        this.ac = 0;
                    }
                    this.b(n == 4);
                    if (this.fb.avail_out != 0) {
                        return (n == 4) ? 3 : 1;
                    }
                    if (n == 4) {
                        return 2;
                    }
                    return 0;
                }
            }
            if (this.fc >= 3) {
                this.b = ((this.b << this.wb ^ (this.yb[this.nb + 2] & 0xFF)) & this.tb);
                n2 = (this.v[this.b] & 0xFFFF);
                this.jb[this.nb & this.u] = this.v[this.b];
                this.v[this.b] = (short)this.nb;
            }
            this.gb = this.i;
            this.o = this.qb;
            this.i = 2;
            if (n2 != 0 && this.gb < this.eb && (this.nb - n2 & 0xFFFF) <= this.kb - 262) {
                if (this.mb != 2) {
                    this.i = this.b(n2);
                }
                if (this.i <= 5 && (this.mb == 1 || (this.i == 3 && this.nb - this.qb > 4096))) {
                    this.i = 2;
                }
            }
            if (this.gb >= 3 && this.i <= this.gb) {
                final int n3 = this.nb + this.fc - 3;
                final boolean c = this.c(this.nb - 1 - this.o, this.gb - 3);
                this.fc -= this.gb - 1;
                this.gb -= 2;
                do {
                    if (++this.nb <= n3) {
                        this.b = ((this.b << this.wb ^ (this.yb[this.nb + 2] & 0xFF)) & this.tb);
                        n2 = (this.v[this.b] & 0xFFFF);
                        this.jb[this.nb & this.u] = this.v[this.b];
                        this.v[this.b] = (short)this.nb;
                    }
                } while (--this.gb != 0);
                this.ac = 0;
                this.i = 2;
                ++this.nb;
                if (!c) {
                    continue;
                }
                this.b(false);
                if (this.fb.avail_out == 0) {
                    return 0;
                }
                continue;
            }
            else if (this.ac != 0) {
                if (this.c(0, this.yb[this.nb - 1] & 0xFF)) {
                    this.b(false);
                }
                ++this.nb;
                --this.fc;
                if (this.fb.avail_out == 0) {
                    return 0;
                }
                continue;
            }
            else {
                this.ac = 1;
                ++this.nb;
                --this.fc;
            }
        }
    }
    
    int b(int qb) {
        int e = this.e;
        int nb = this.nb;
        int gb = this.gb;
        final int n = (this.nb > this.kb - 262) ? (this.nb - (this.kb - 262)) : 0;
        int n2 = this.ub;
        final int u = this.u;
        final int n3 = this.nb + 258;
        byte b = this.yb[nb + gb - 1];
        byte b2 = this.yb[nb + gb];
        if (this.gb >= this.zb) {
            e >>= 2;
        }
        if (n2 > this.fc) {
            n2 = this.fc;
        }
        do {
            int n4 = qb;
            if (this.yb[n4 + gb] == b2 && this.yb[n4 + gb - 1] == b && this.yb[n4] == this.yb[nb]) {
                if (this.yb[++n4] != this.yb[nb + 1]) {
                    continue;
                }
                nb += 2;
                ++n4;
                while (this.yb[++nb] == this.yb[++n4] && this.yb[++nb] == this.yb[++n4] && this.yb[++nb] == this.yb[++n4] && this.yb[++nb] == this.yb[++n4] && this.yb[++nb] == this.yb[++n4] && this.yb[++nb] == this.yb[++n4] && this.yb[++nb] == this.yb[++n4] && this.yb[++nb] == this.yb[++n4] && nb < n3) {}
                final int n5 = 258 - (n3 - nb);
                nb = n3 - 258;
                if (n5 <= gb) {
                    continue;
                }
                this.qb = qb;
                if ((gb = n5) >= n2) {
                    break;
                }
                b = this.yb[nb + gb - 1];
                b2 = this.yb[nb + gb];
            }
        } while ((qb = (this.jb[qb & u] & 0xFFFF)) > n && --e != 0);
        if (gb <= this.fc) {
            return gb;
        }
        return this.fc;
    }
    
    int c(final ZStream zStream, final int n, final int n2) {
        return this.b(zStream, n, 8, n2, 8, 0);
    }
    
    int b(final ZStream zStream, int j, final int n, int db, final int n2, final int mb) {
        int hc = 0;
        zStream.msg = null;
        if (j == -1) {
            j = 6;
        }
        if (db < 0) {
            hc = 1;
            db = -db;
        }
        if (n2 < 1 || n2 > 9 || n != 8 || db < 9 || db > 15 || j < 0 || j > 9 || mb < 0 || mb > 2) {
            return -2;
        }
        zStream.d = this;
        this.hc = hc;
        this.db = db;
        this.kb = 1 << this.db;
        this.u = this.kb - 1;
        this.dc = n2 + 7;
        this.g = 1 << this.dc;
        this.tb = this.g - 1;
        this.wb = (this.dc + 3 - 1) / 3;
        this.yb = new byte[this.kb * 2];
        this.jb = new short[this.kb];
        this.v = new short[this.g];
        this.sb = 1 << n2 + 6;
        this.bc = new byte[this.sb * 4];
        this.gc = this.sb * 4;
        this.d = this.sb / 2;
        this.cc = 3 * this.sb;
        this.j = j;
        this.mb = mb;
        this.ec = (byte)n;
        return this.b(zStream);
    }
    
    int b(final ZStream zStream) {
        final long n = 0L;
        zStream.total_out = n;
        zStream.total_in = n;
        zStream.msg = null;
        zStream.e = 2;
        this.q = 0;
        this.l = 0;
        if (this.hc < 0) {
            this.hc = 0;
        }
        this.lb = ((this.hc != 0) ? 113 : 42);
        zStream.adler = zStream.b.b(0L, null, 0, 0);
        this.y = 0;
        this.c();
        this.g();
        return 0;
    }
    
    int k() {
        if (this.lb != 42 && this.lb != 113 && this.lb != 666) {
            return -2;
        }
        this.bc = null;
        this.v = null;
        this.jb = null;
        this.yb = null;
        return (this.lb == 113) ? -3 : 0;
    }
    
    int b(final ZStream zStream, int j, final int mb) {
        int deflate = 0;
        if (j == -1) {
            j = 6;
        }
        if (j < 0 || j > 9 || mb < 0 || mb > 2) {
            return -2;
        }
        if (Deflate.f[this.j].d != Deflate.f[j].d && zStream.total_in != 0L) {
            deflate = zStream.deflate(1);
        }
        if (this.j != j) {
            this.j = j;
            this.eb = Deflate.f[this.j].f;
            this.zb = Deflate.f[this.j].b;
            this.ub = Deflate.f[this.j].c;
            this.e = Deflate.f[this.j].e;
        }
        this.mb = mb;
        return deflate;
    }
    
    int b(final ZStream zStream, final byte[] array, final int n) {
        int n2 = n;
        int n3 = 0;
        if (array == null || this.lb != 42) {
            return -2;
        }
        zStream.adler = zStream.b.b(zStream.adler, array, 0, n);
        if (n2 < 3) {
            return 0;
        }
        if (n2 > this.kb - 262) {
            n2 = this.kb - 262;
            n3 = n - n2;
        }
        System.arraycopy(array, n3, this.yb, 0, n2);
        this.nb = n2;
        this.h = n2;
        this.b = (this.yb[0] & 0xFF);
        this.b = ((this.b << this.wb ^ (this.yb[1] & 0xFF)) & this.tb);
        for (int i = 0; i <= n2 - 3; ++i) {
            this.b = ((this.b << this.wb ^ (this.yb[i + 2] & 0xFF)) & this.tb);
            this.jb[i & this.u] = this.v[this.b];
            this.v[this.b] = (short)i;
        }
        return 0;
    }
    
    int b(final ZStream fb, final int y) {
        if (y > 4 || y < 0) {
            return -2;
        }
        if (fb.next_out == null || (fb.next_in == null && fb.avail_in != 0) || (this.lb == 666 && y != 4)) {
            fb.msg = Deflate.t[4];
            return -2;
        }
        if (fb.avail_out == 0) {
            fb.msg = Deflate.t[7];
            return -5;
        }
        this.fb = fb;
        final int y2 = this.y;
        this.y = y;
        if (this.lb == 42) {
            final int n = 8 + (this.db - 8 << 4) << 8;
            int n2 = (this.j - 1 & 0xFF) >> 1;
            if (n2 > 3) {
                n2 = 3;
            }
            int n3 = n | n2 << 6;
            if (this.nb != 0) {
                n3 |= 0x20;
            }
            final int n4 = n3 + (31 - n3 % 31);
            this.lb = 113;
            this.f(n4);
            if (this.nb != 0) {
                this.f((int)(fb.adler >>> 16));
                this.f((int)(fb.adler & 0xFFFFL));
            }
            fb.adler = fb.b.b(0L, null, 0, 0);
        }
        if (this.q != 0) {
            fb.b();
            if (fb.avail_out == 0) {
                this.y = -1;
                return 0;
            }
        }
        else if (fb.avail_in == 0 && y <= y2 && y != 4) {
            fb.msg = Deflate.t[7];
            return -5;
        }
        if (this.lb == 666 && fb.avail_in != 0) {
            fb.msg = Deflate.t[7];
            return -5;
        }
        if (fb.avail_in != 0 || this.fc != 0 || (y != 0 && this.lb != 666)) {
            int n5 = -1;
            switch (Deflate.f[this.j].d) {
                case 0: {
                    n5 = this.e(y);
                    break;
                }
                case 1: {
                    n5 = this.g(y);
                    break;
                }
                case 2: {
                    n5 = this.d(y);
                    break;
                }
            }
            if (n5 == 2 || n5 == 3) {
                this.lb = 666;
            }
            if (n5 == 0 || n5 == 2) {
                if (fb.avail_out == 0) {
                    this.y = -1;
                }
                return 0;
            }
            if (n5 == 1) {
                if (y == 1) {
                    this.d();
                }
                else {
                    this.c(0, 0, false);
                    if (y == 3) {
                        for (int i = 0; i < this.g; ++i) {
                            this.v[i] = 0;
                        }
                    }
                }
                fb.b();
                if (fb.avail_out == 0) {
                    this.y = -1;
                    return 0;
                }
            }
        }
        if (y != 4) {
            return 0;
        }
        if (this.hc != 0) {
            return 1;
        }
        this.f((int)(fb.adler >>> 16));
        this.f((int)(fb.adler & 0xFFFFL));
        fb.b();
        this.hc = -1;
        return (this.q == 0) ? 1 : 0;
    }
    
    static {
        (Deflate.f = new _b[10])[0] = new _b(0, 0, 0, 0, 0);
        Deflate.f[1] = new _b(4, 4, 8, 4, 1);
        Deflate.f[2] = new _b(4, 5, 16, 8, 1);
        Deflate.f[3] = new _b(4, 6, 32, 32, 1);
        Deflate.f[4] = new _b(4, 4, 16, 16, 2);
        Deflate.f[5] = new _b(8, 16, 32, 32, 2);
        Deflate.f[6] = new _b(8, 16, 128, 128, 2);
        Deflate.f[7] = new _b(8, 32, 128, 256, 2);
        Deflate.f[8] = new _b(32, 128, 258, 1024, 2);
        Deflate.f[9] = new _b(32, 258, 258, 4096, 2);
        t = new String[] { "need dictionary", "stream end", "", "file error", "stream error", "data error", "insufficient memory", "buffer error", "incompatible version", "" };
    }
    
    static class _b
    {
        int b;
        int f;
        int c;
        int e;
        int d;
        
        _b(final int b, final int f, final int c, final int e, final int d) {
            this.b = b;
            this.f = f;
            this.c = c;
            this.e = e;
            this.d = d;
        }
    }
}
