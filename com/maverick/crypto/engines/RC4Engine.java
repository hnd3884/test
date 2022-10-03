package com.maverick.crypto.engines;

public class RC4Engine
{
    private byte[] d;
    private int c;
    private int e;
    private byte[] b;
    
    public RC4Engine() {
        this.d = null;
        this.c = 0;
        this.e = 0;
        this.b = null;
    }
    
    public void init(final boolean b, final byte[] b2) {
        this.b(this.b = b2);
    }
    
    public String getAlgorithmName() {
        return "RC4";
    }
    
    public byte returnByte(final byte b) {
        this.c = (this.c + 1 & 0xFF);
        this.e = (this.d[this.c] + this.e & 0xFF);
        final byte b2 = this.d[this.c];
        this.d[this.c] = this.d[this.e];
        this.d[this.e] = b2;
        return (byte)(b ^ this.d[this.d[this.c] + this.d[this.e] & 0xFF]);
    }
    
    public void processBytes(final byte[] array, final int n, final int n2, final byte[] array2, final int n3) {
        if (n + n2 > array.length) {
            throw new RuntimeException("input buffer too short");
        }
        if (n3 + n2 > array2.length) {
            throw new RuntimeException("output buffer too short");
        }
        for (int i = 0; i < n2; ++i) {
            this.c = (this.c + 1 & 0xFF);
            this.e = (this.d[this.c] + this.e & 0xFF);
            final byte b = this.d[this.c];
            this.d[this.c] = this.d[this.e];
            this.d[this.e] = b;
            array2[i + n3] = (byte)(array[i + n] ^ this.d[this.d[this.c] + this.d[this.e] & 0xFF]);
        }
    }
    
    public void reset() {
        this.b(this.b);
    }
    
    private void b(final byte[] b) {
        this.b = b;
        this.c = 0;
        this.e = 0;
        if (this.d == null) {
            this.d = new byte[256];
        }
        for (int i = 0; i < 256; ++i) {
            this.d[i] = (byte)i;
        }
        int n = 0;
        int n2 = 0;
        for (int j = 0; j < 256; ++j) {
            n2 = ((b[n] & 0xFF) + this.d[j] + n2 & 0xFF);
            final byte b2 = this.d[j];
            this.d[j] = this.d[n2];
            this.d[n2] = b2;
            n = (n + 1) % b.length;
        }
    }
}
