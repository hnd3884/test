package com.maverick.crypto.security;

import com.maverick.crypto.digests.SHA1Digest;
import java.util.Random;

public class SecureRandom extends Random
{
    private static SecureRandom e;
    private long c;
    private SHA1Digest g;
    private byte[] f;
    private byte[] b;
    private byte[] d;
    
    public SecureRandom() {
        super(0L);
        this.c = 1L;
        this.g = new SHA1Digest();
        this.f = new byte[this.g.getDigestSize()];
        this.b = new byte[4];
        this.d = new byte[8];
        this.setSeed(System.currentTimeMillis());
    }
    
    public SecureRandom(final byte[] seed) {
        this.c = 1L;
        this.g = new SHA1Digest();
        this.f = new byte[this.g.getDigestSize()];
        this.b = new byte[4];
        this.d = new byte[8];
        this.setSeed(seed);
    }
    
    public static SecureRandom getInstance(final String s) {
        return new SecureRandom();
    }
    
    public static SecureRandom getInstance() {
        synchronized (SecureRandom.e) {
            SecureRandom.e.setSeed(System.currentTimeMillis());
            return SecureRandom.e;
        }
    }
    
    public static byte[] getSeed(final int n) {
        final byte[] array = new byte[n];
        synchronized (SecureRandom.e) {
            SecureRandom.e.setSeed(System.currentTimeMillis());
            SecureRandom.e.nextBytes(array);
        }
        return array;
    }
    
    public byte[] generateSeed(final int n) {
        final byte[] array = new byte[n];
        this.nextBytes(array);
        return array;
    }
    
    public synchronized void setSeed(final byte[] array) {
        this.g.update(array, 0, array.length);
    }
    
    public synchronized void nextBytes(final byte[] array) {
        this.nextBytes(array, 0, array.length);
    }
    
    public synchronized void nextBytes(final byte[] array, final int n, final int n2) {
        int n3 = 0;
        this.g.doFinal(this.f, 0);
        for (int i = 0; i != n2; ++i) {
            if (n3 == this.f.length) {
                final byte[] b = this.b(this.c++);
                this.g.update(b, 0, b.length);
                this.g.update(this.f, 0, this.f.length);
                this.g.doFinal(this.f, 0);
                n3 = 0;
            }
            array[i + n] = this.f[n3++];
        }
        final byte[] b2 = this.b(this.c++);
        this.g.update(b2, 0, b2.length);
        this.g.update(this.f, 0, this.f.length);
    }
    
    public synchronized void setSeed(final long n) {
        if (n != 0L) {
            this.setSeed(this.b(n));
        }
    }
    
    public synchronized int nextInt() {
        this.nextBytes(this.b);
        int n = 0;
        for (int i = 0; i < 4; ++i) {
            n = (n << 8) + (this.b[i] & 0xFF);
        }
        return n;
    }
    
    protected final synchronized int next(final int n) {
        final int n2 = (n + 7) / 8;
        final byte[] array = new byte[n2];
        this.nextBytes(array);
        int n3 = 0;
        for (int i = 0; i < n2; ++i) {
            n3 = (n3 << 8) + (array[i] & 0xFF);
        }
        return n3 & (1 << n) - 1;
    }
    
    private synchronized byte[] b(long n) {
        for (int i = 0; i != 8; ++i) {
            this.d[i] = (byte)n;
            n >>>= 8;
        }
        return this.d;
    }
    
    static {
        SecureRandom.e = new SecureRandom();
    }
}
