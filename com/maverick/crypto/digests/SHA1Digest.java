package com.maverick.crypto.digests;

public class SHA1Digest extends GeneralDigest
{
    private int o;
    private int n;
    private int m;
    private int l;
    private int k;
    private int[] q;
    private int p;
    
    public SHA1Digest() {
        this.q = new int[80];
        this.reset();
    }
    
    public SHA1Digest(final SHA1Digest sha1Digest) {
        super(sha1Digest);
        this.q = new int[80];
        this.o = sha1Digest.o;
        this.n = sha1Digest.n;
        this.m = sha1Digest.m;
        this.l = sha1Digest.l;
        this.k = sha1Digest.k;
        System.arraycopy(sha1Digest.q, 0, this.q, 0, sha1Digest.q.length);
        this.p = sha1Digest.p;
    }
    
    public String getAlgorithmName() {
        return "SHA-1";
    }
    
    public int getDigestSize() {
        return 20;
    }
    
    protected void processWord(final byte[] array, final int n) {
        this.q[this.p++] = ((array[n] & 0xFF) << 24 | (array[n + 1] & 0xFF) << 16 | (array[n + 2] & 0xFF) << 8 | (array[n + 3] & 0xFF));
        if (this.p == 16) {
            this.processBlock();
        }
    }
    
    private void c(final int n, final byte[] array, final int n2) {
        array[n2] = (byte)(n >>> 24);
        array[n2 + 1] = (byte)(n >>> 16);
        array[n2 + 2] = (byte)(n >>> 8);
        array[n2 + 3] = (byte)n;
    }
    
    protected void processLength(final long n) {
        if (this.p > 14) {
            this.processBlock();
        }
        this.q[14] = (int)(n >>> 32);
        this.q[15] = (int)(n & -1L);
    }
    
    public int doFinal(final byte[] array, final int n) {
        this.finish();
        this.c(this.o, array, n);
        this.c(this.n, array, n + 4);
        this.c(this.m, array, n + 8);
        this.c(this.l, array, n + 12);
        this.c(this.k, array, n + 16);
        this.reset();
        return 20;
    }
    
    public void reset() {
        super.reset();
        this.o = 1732584193;
        this.n = -271733879;
        this.m = -1732584194;
        this.l = 271733878;
        this.k = -1009589776;
        this.p = 0;
        for (int i = 0; i != this.q.length; ++i) {
            this.q[i] = 0;
        }
    }
    
    private int g(final int n, final int n2, final int n3) {
        return (n & n2) | (~n & n3);
    }
    
    private int h(final int n, final int n2, final int n3) {
        return n ^ n2 ^ n3;
    }
    
    private int f(final int n, final int n2, final int n3) {
        return (n & n2) | (n & n3) | (n2 & n3);
    }
    
    private int c(final int n, final int n2) {
        return n << n2 | n >>> 32 - n2;
    }
    
    protected void processBlock() {
        for (int i = 16; i <= 79; ++i) {
            this.q[i] = this.c(this.q[i - 3] ^ this.q[i - 8] ^ this.q[i - 14] ^ this.q[i - 16], 1);
        }
        int o = this.o;
        int n = this.n;
        int n2 = this.m;
        int l = this.l;
        int k = this.k;
        for (int j = 0; j <= 19; ++j) {
            final int n3 = this.c(o, 5) + this.g(n, n2, l) + k + this.q[j] + 1518500249;
            k = l;
            l = n2;
            n2 = this.c(n, 30);
            n = o;
            o = n3;
        }
        for (int n4 = 20; n4 <= 39; ++n4) {
            final int n5 = this.c(o, 5) + this.h(n, n2, l) + k + this.q[n4] + 1859775393;
            k = l;
            l = n2;
            n2 = this.c(n, 30);
            n = o;
            o = n5;
        }
        for (int n6 = 40; n6 <= 59; ++n6) {
            final int n7 = this.c(o, 5) + this.f(n, n2, l) + k + this.q[n6] - 1894007588;
            k = l;
            l = n2;
            n2 = this.c(n, 30);
            n = o;
            o = n7;
        }
        for (int n8 = 60; n8 <= 79; ++n8) {
            final int n9 = this.c(o, 5) + this.h(n, n2, l) + k + this.q[n8] - 899497514;
            k = l;
            l = n2;
            n2 = this.c(n, 30);
            n = o;
            o = n9;
        }
        this.o += o;
        this.n += n;
        this.m += n2;
        this.l += l;
        this.k += k;
        this.p = 0;
        for (int n10 = 0; n10 != this.q.length; ++n10) {
            this.q[n10] = 0;
        }
    }
}
