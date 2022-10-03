package jcifs.util;

import java.security.MessageDigest;

public class MD4 extends MessageDigest implements Cloneable
{
    private static final int BLOCK_LENGTH = 64;
    private int[] context;
    private long count;
    private byte[] buffer;
    private int[] X;
    
    public MD4() {
        super("MD4");
        this.context = new int[4];
        this.buffer = new byte[64];
        this.X = new int[16];
        this.engineReset();
    }
    
    private MD4(final MD4 md) {
        this();
        this.context = md.context.clone();
        this.buffer = md.buffer.clone();
        this.count = md.count;
    }
    
    public Object clone() {
        return new MD4(this);
    }
    
    public void engineReset() {
        this.context[0] = 1732584193;
        this.context[1] = -271733879;
        this.context[2] = -1732584194;
        this.context[3] = 271733878;
        this.count = 0L;
        for (int i = 0; i < 64; ++i) {
            this.buffer[i] = 0;
        }
    }
    
    public void engineUpdate(final byte b) {
        final int i = (int)(this.count % 64L);
        ++this.count;
        this.buffer[i] = b;
        if (i == 63) {
            this.transform(this.buffer, 0);
        }
    }
    
    public void engineUpdate(final byte[] input, final int offset, final int len) {
        if (offset < 0 || len < 0 || offset + (long)len > input.length) {
            throw new ArrayIndexOutOfBoundsException();
        }
        int bufferNdx = (int)(this.count % 64L);
        this.count += len;
        final int partLen = 64 - bufferNdx;
        int i = 0;
        if (len >= partLen) {
            System.arraycopy(input, offset, this.buffer, bufferNdx, partLen);
            this.transform(this.buffer, 0);
            for (i = partLen; i + 64 - 1 < len; i += 64) {
                this.transform(input, offset + i);
            }
            bufferNdx = 0;
        }
        if (i < len) {
            System.arraycopy(input, offset + i, this.buffer, bufferNdx, len - i);
        }
    }
    
    public byte[] engineDigest() {
        final int bufferNdx = (int)(this.count % 64L);
        final int padLen = (bufferNdx < 56) ? (56 - bufferNdx) : (120 - bufferNdx);
        final byte[] tail = new byte[padLen + 8];
        tail[0] = -128;
        for (int i = 0; i < 8; ++i) {
            tail[padLen + i] = (byte)(this.count * 8L >>> 8 * i);
        }
        this.engineUpdate(tail, 0, tail.length);
        final byte[] result = new byte[16];
        for (int j = 0; j < 4; ++j) {
            for (int k = 0; k < 4; ++k) {
                result[j * 4 + k] = (byte)(this.context[j] >>> 8 * k);
            }
        }
        this.engineReset();
        return result;
    }
    
    private void transform(final byte[] block, int offset) {
        for (int i = 0; i < 16; ++i) {
            this.X[i] = ((block[offset++] & 0xFF) | (block[offset++] & 0xFF) << 8 | (block[offset++] & 0xFF) << 16 | (block[offset++] & 0xFF) << 24);
        }
        int A = this.context[0];
        int B = this.context[1];
        int C = this.context[2];
        int D = this.context[3];
        A = this.FF(A, B, C, D, this.X[0], 3);
        D = this.FF(D, A, B, C, this.X[1], 7);
        C = this.FF(C, D, A, B, this.X[2], 11);
        B = this.FF(B, C, D, A, this.X[3], 19);
        A = this.FF(A, B, C, D, this.X[4], 3);
        D = this.FF(D, A, B, C, this.X[5], 7);
        C = this.FF(C, D, A, B, this.X[6], 11);
        B = this.FF(B, C, D, A, this.X[7], 19);
        A = this.FF(A, B, C, D, this.X[8], 3);
        D = this.FF(D, A, B, C, this.X[9], 7);
        C = this.FF(C, D, A, B, this.X[10], 11);
        B = this.FF(B, C, D, A, this.X[11], 19);
        A = this.FF(A, B, C, D, this.X[12], 3);
        D = this.FF(D, A, B, C, this.X[13], 7);
        C = this.FF(C, D, A, B, this.X[14], 11);
        B = this.FF(B, C, D, A, this.X[15], 19);
        A = this.GG(A, B, C, D, this.X[0], 3);
        D = this.GG(D, A, B, C, this.X[4], 5);
        C = this.GG(C, D, A, B, this.X[8], 9);
        B = this.GG(B, C, D, A, this.X[12], 13);
        A = this.GG(A, B, C, D, this.X[1], 3);
        D = this.GG(D, A, B, C, this.X[5], 5);
        C = this.GG(C, D, A, B, this.X[9], 9);
        B = this.GG(B, C, D, A, this.X[13], 13);
        A = this.GG(A, B, C, D, this.X[2], 3);
        D = this.GG(D, A, B, C, this.X[6], 5);
        C = this.GG(C, D, A, B, this.X[10], 9);
        B = this.GG(B, C, D, A, this.X[14], 13);
        A = this.GG(A, B, C, D, this.X[3], 3);
        D = this.GG(D, A, B, C, this.X[7], 5);
        C = this.GG(C, D, A, B, this.X[11], 9);
        B = this.GG(B, C, D, A, this.X[15], 13);
        A = this.HH(A, B, C, D, this.X[0], 3);
        D = this.HH(D, A, B, C, this.X[8], 9);
        C = this.HH(C, D, A, B, this.X[4], 11);
        B = this.HH(B, C, D, A, this.X[12], 15);
        A = this.HH(A, B, C, D, this.X[2], 3);
        D = this.HH(D, A, B, C, this.X[10], 9);
        C = this.HH(C, D, A, B, this.X[6], 11);
        B = this.HH(B, C, D, A, this.X[14], 15);
        A = this.HH(A, B, C, D, this.X[1], 3);
        D = this.HH(D, A, B, C, this.X[9], 9);
        C = this.HH(C, D, A, B, this.X[5], 11);
        B = this.HH(B, C, D, A, this.X[13], 15);
        A = this.HH(A, B, C, D, this.X[3], 3);
        D = this.HH(D, A, B, C, this.X[11], 9);
        C = this.HH(C, D, A, B, this.X[7], 11);
        B = this.HH(B, C, D, A, this.X[15], 15);
        final int[] context = this.context;
        final int n = 0;
        context[n] += A;
        final int[] context2 = this.context;
        final int n2 = 1;
        context2[n2] += B;
        final int[] context3 = this.context;
        final int n3 = 2;
        context3[n3] += C;
        final int[] context4 = this.context;
        final int n4 = 3;
        context4[n4] += D;
    }
    
    private int FF(final int a, final int b, final int c, final int d, final int x, final int s) {
        final int t = a + ((b & c) | (~b & d)) + x;
        return t << s | t >>> 32 - s;
    }
    
    private int GG(final int a, final int b, final int c, final int d, final int x, final int s) {
        final int t = a + ((b & (c | d)) | (c & d)) + x + 1518500249;
        return t << s | t >>> 32 - s;
    }
    
    private int HH(final int a, final int b, final int c, final int d, final int x, final int s) {
        final int t = a + (b ^ c ^ d) + x + 1859775393;
        return t << s | t >>> 32 - s;
    }
}
