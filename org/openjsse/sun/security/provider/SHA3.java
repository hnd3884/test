package org.openjsse.sun.security.provider;

import java.util.Arrays;
import java.security.ProviderException;

abstract class SHA3 extends DigestBase
{
    private static final int WIDTH = 200;
    private static final int DM = 5;
    private static final int NR = 24;
    private static final long[] RC_CONSTANTS;
    private byte[] state;
    private final long[] lanes;
    
    SHA3(final String name, final int digestLength) {
        super(name, digestLength, 200 - 2 * digestLength);
        this.state = new byte[200];
        this.lanes = new long[25];
    }
    
    @Override
    void implCompress(final byte[] b, int ofs) {
        for (int i = 0; i < this.buffer.length; ++i) {
            final byte[] state = this.state;
            final int n = i;
            state[n] ^= b[ofs++];
        }
        this.keccak();
    }
    
    @Override
    void implDigest(final byte[] out, final int ofs) {
        final int numOfPadding = setPaddingBytes(this.buffer, (int)(this.bytesProcessed % this.buffer.length));
        if (numOfPadding < 1) {
            throw new ProviderException("Incorrect pad size: " + numOfPadding);
        }
        for (int i = 0; i < this.buffer.length; ++i) {
            final byte[] state = this.state;
            final int n = i;
            state[n] ^= this.buffer[i];
        }
        this.keccak();
        System.arraycopy(this.state, 0, out, ofs, this.engineGetDigestLength());
    }
    
    @Override
    void implReset() {
        Arrays.fill(this.state, (byte)0);
        Arrays.fill(this.lanes, 0L);
    }
    
    private static int setPaddingBytes(final byte[] in, final int len) {
        if (len != in.length) {
            Arrays.fill(in, len, in.length, (byte)0);
            in[len] |= 0x6;
            final int n = in.length - 1;
            in[n] |= 0xFFFFFF80;
        }
        return in.length - len;
    }
    
    private static void bytes2Lanes(final byte[] s, final long[] m) {
        for (int sOfs = 0, y = 0; y < 5; ++y, sOfs += 40) {
            ByteArrayAccess.b2lLittle(s, sOfs, m, 5 * y, 40);
        }
    }
    
    private static void lanes2Bytes(final long[] m, final byte[] s) {
        for (int sOfs = 0, y = 0; y < 5; ++y, sOfs += 40) {
            ByteArrayAccess.l2bLittle(m, 5 * y, s, sOfs, 40);
        }
    }
    
    private static long[] smTheta(final long[] a) {
        final long c0 = a[0] ^ a[5] ^ a[10] ^ a[15] ^ a[20];
        final long c2 = a[1] ^ a[6] ^ a[11] ^ a[16] ^ a[21];
        final long c3 = a[2] ^ a[7] ^ a[12] ^ a[17] ^ a[22];
        final long c4 = a[3] ^ a[8] ^ a[13] ^ a[18] ^ a[23];
        final long c5 = a[4] ^ a[9] ^ a[14] ^ a[19] ^ a[24];
        final long d0 = c5 ^ Long.rotateLeft(c2, 1);
        final long d2 = c0 ^ Long.rotateLeft(c3, 1);
        final long d3 = c2 ^ Long.rotateLeft(c4, 1);
        final long d4 = c3 ^ Long.rotateLeft(c5, 1);
        final long d5 = c4 ^ Long.rotateLeft(c0, 1);
        for (int y = 0; y < a.length; y += 5) {
            final int n = y;
            a[n] ^= d0;
            final int n2 = y + 1;
            a[n2] ^= d2;
            final int n3 = y + 2;
            a[n3] ^= d3;
            final int n4 = y + 3;
            a[n4] ^= d4;
            final int n5 = y + 4;
            a[n5] ^= d5;
        }
        return a;
    }
    
    private static long[] smPiRho(final long[] a) {
        final long tmp = Long.rotateLeft(a[10], 3);
        a[10] = Long.rotateLeft(a[1], 1);
        a[1] = Long.rotateLeft(a[6], 44);
        a[6] = Long.rotateLeft(a[9], 20);
        a[9] = Long.rotateLeft(a[22], 61);
        a[22] = Long.rotateLeft(a[14], 39);
        a[14] = Long.rotateLeft(a[20], 18);
        a[20] = Long.rotateLeft(a[2], 62);
        a[2] = Long.rotateLeft(a[12], 43);
        a[12] = Long.rotateLeft(a[13], 25);
        a[13] = Long.rotateLeft(a[19], 8);
        a[19] = Long.rotateLeft(a[23], 56);
        a[23] = Long.rotateLeft(a[15], 41);
        a[15] = Long.rotateLeft(a[4], 27);
        a[4] = Long.rotateLeft(a[24], 14);
        a[24] = Long.rotateLeft(a[21], 2);
        a[21] = Long.rotateLeft(a[8], 55);
        a[8] = Long.rotateLeft(a[16], 45);
        a[16] = Long.rotateLeft(a[5], 36);
        a[5] = Long.rotateLeft(a[3], 28);
        a[3] = Long.rotateLeft(a[18], 21);
        a[18] = Long.rotateLeft(a[17], 15);
        a[17] = Long.rotateLeft(a[11], 10);
        a[11] = Long.rotateLeft(a[7], 6);
        a[7] = tmp;
        return a;
    }
    
    private static long[] smChi(final long[] a) {
        for (int y = 0; y < a.length; y += 5) {
            final long ay0 = a[y];
            final long ay2 = a[y + 1];
            final long ay3 = a[y + 2];
            final long ay4 = a[y + 3];
            final long ay5 = a[y + 4];
            a[y] = (ay0 ^ (~ay2 & ay3));
            a[y + 1] = (ay2 ^ (~ay3 & ay4));
            a[y + 2] = (ay3 ^ (~ay4 & ay5));
            a[y + 3] = (ay4 ^ (~ay5 & ay0));
            a[y + 4] = (ay5 ^ (~ay0 & ay2));
        }
        return a;
    }
    
    private static long[] smIota(final long[] a, final int rndIndex) {
        final int n = 0;
        a[n] ^= SHA3.RC_CONSTANTS[rndIndex];
        return a;
    }
    
    private void keccak() {
        bytes2Lanes(this.state, this.lanes);
        for (int ir = 0; ir < 24; ++ir) {
            smIota(smChi(smPiRho(smTheta(this.lanes))), ir);
        }
        lanes2Bytes(this.lanes, this.state);
    }
    
    @Override
    public Object clone() throws CloneNotSupportedException {
        final SHA3 copy = (SHA3)super.clone();
        copy.state = copy.state.clone();
        return copy;
    }
    
    static {
        RC_CONSTANTS = new long[] { 1L, 32898L, -9223372036854742902L, -9223372034707259392L, 32907L, 2147483649L, -9223372034707259263L, -9223372036854743031L, 138L, 136L, 2147516425L, 2147483658L, 2147516555L, -9223372036854775669L, -9223372036854742903L, -9223372036854743037L, -9223372036854743038L, -9223372036854775680L, 32778L, -9223372034707292150L, -9223372034707259263L, -9223372036854742912L, 2147483649L, -9223372034707259384L };
    }
    
    public static final class SHA224 extends SHA3
    {
        public SHA224() {
            super("SHA3-224", 28);
        }
    }
    
    public static final class SHA256 extends SHA3
    {
        public SHA256() {
            super("SHA3-256", 32);
        }
    }
    
    public static final class SHA384 extends SHA3
    {
        public SHA384() {
            super("SHA3-384", 48);
        }
    }
    
    public static final class SHA512 extends SHA3
    {
        public SHA512() {
            super("SHA3-512", 64);
        }
    }
}
