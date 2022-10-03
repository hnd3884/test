package com.maverick.crypto.digests;

public class GeneralHMac implements HMac
{
    private Digest f;
    private int c;
    private int e;
    private byte[] b;
    private byte[] d;
    
    public GeneralHMac(final Digest f) {
        this.b = new byte[64];
        this.d = new byte[64];
        this.f = f;
        this.c = f.getDigestSize();
        this.e = this.c;
    }
    
    public GeneralHMac(final Digest f, final int e) {
        this.b = new byte[64];
        this.d = new byte[64];
        this.f = f;
        this.c = f.getDigestSize();
        this.e = e;
    }
    
    public String getAlgorithmName() {
        return this.f.getAlgorithmName() + "/HMAC";
    }
    
    public int getOutputSize() {
        return this.e;
    }
    
    public Digest getUnderlyingDigest() {
        return this.f;
    }
    
    public void init(final byte[] array) {
        this.f.reset();
        if (array.length > 64) {
            this.f.update(array, 0, array.length);
            this.f.doFinal(this.b, 0);
            for (int i = this.c; i < this.b.length; ++i) {
                this.b[i] = 0;
            }
        }
        else {
            System.arraycopy(array, 0, this.b, 0, array.length);
            for (int j = array.length; j < this.b.length; ++j) {
                this.b[j] = 0;
            }
        }
        this.d = new byte[this.b.length];
        System.arraycopy(this.b, 0, this.d, 0, this.b.length);
        for (int k = 0; k < this.b.length; ++k) {
            final byte[] b = this.b;
            final int n = k;
            b[n] ^= 0x36;
        }
        for (int l = 0; l < this.d.length; ++l) {
            final byte[] d = this.d;
            final int n2 = l;
            d[n2] ^= 0x5C;
        }
        this.f.update(this.b, 0, this.b.length);
    }
    
    public int getMacSize() {
        return this.c;
    }
    
    public void update(final byte b) {
        this.f.update(b);
    }
    
    public void update(final byte[] array, final int n, final int n2) {
        this.f.update(array, n, n2);
    }
    
    public int doFinal(final byte[] array, final int n) {
        final byte[] array2 = new byte[this.c];
        this.f.doFinal(array2, 0);
        this.f.update(this.d, 0, this.d.length);
        this.f.update(array2, 0, array2.length);
        final int doFinal = this.f.doFinal(array, n);
        this.reset();
        return doFinal;
    }
    
    public void reset() {
        this.f.reset();
        this.f.update(this.b, 0, this.b.length);
    }
}
