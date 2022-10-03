package sun.security.provider;

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
    
    SHA3(final String s, final int n) {
        super(s, n, 200 - 2 * n);
        this.state = new byte[200];
        this.lanes = new long[25];
    }
    
    @Override
    void implCompress(final byte[] array, int n) {
        for (int i = 0; i < this.buffer.length; ++i) {
            final byte[] state = this.state;
            final int n2 = i;
            state[n2] ^= array[n++];
        }
        this.keccak();
    }
    
    @Override
    void implDigest(final byte[] array, final int n) {
        final int setPaddingBytes = setPaddingBytes(this.buffer, (int)(this.bytesProcessed % this.buffer.length));
        if (setPaddingBytes < 1) {
            throw new ProviderException("Incorrect pad size: " + setPaddingBytes);
        }
        for (int i = 0; i < this.buffer.length; ++i) {
            final byte[] state = this.state;
            final int n2 = i;
            state[n2] ^= this.buffer[i];
        }
        this.keccak();
        System.arraycopy(this.state, 0, array, n, this.engineGetDigestLength());
    }
    
    @Override
    void implReset() {
        Arrays.fill(this.state, (byte)0);
        Arrays.fill(this.lanes, 0L);
    }
    
    private static int setPaddingBytes(final byte[] array, final int n) {
        if (n != array.length) {
            Arrays.fill(array, n, array.length, (byte)0);
            array[n] |= 0x6;
            final int n2 = array.length - 1;
            array[n2] |= 0xFFFFFF80;
        }
        return array.length - n;
    }
    
    private static void bytes2Lanes(final byte[] array, final long[] array2) {
        for (int n = 0, i = 0; i < 5; ++i, n += 40) {
            ByteArrayAccess.b2lLittle(array, n, array2, 5 * i, 40);
        }
    }
    
    private static void lanes2Bytes(final long[] array, final byte[] array2) {
        for (int n = 0, i = 0; i < 5; ++i, n += 40) {
            ByteArrayAccess.l2bLittle(array, 5 * i, array2, n, 40);
        }
    }
    
    private static long[] smTheta(final long[] array) {
        final long n = array[0] ^ array[5] ^ array[10] ^ array[15] ^ array[20];
        final long n2 = array[1] ^ array[6] ^ array[11] ^ array[16] ^ array[21];
        final long n3 = array[2] ^ array[7] ^ array[12] ^ array[17] ^ array[22];
        final long n4 = array[3] ^ array[8] ^ array[13] ^ array[18] ^ array[23];
        final long n5 = array[4] ^ array[9] ^ array[14] ^ array[19] ^ array[24];
        final long n6 = n5 ^ Long.rotateLeft(n2, 1);
        final long n7 = n ^ Long.rotateLeft(n3, 1);
        final long n8 = n2 ^ Long.rotateLeft(n4, 1);
        final long n9 = n3 ^ Long.rotateLeft(n5, 1);
        final long n10 = n4 ^ Long.rotateLeft(n, 1);
        for (int i = 0; i < array.length; i += 5) {
            final int n11 = i;
            array[n11] ^= n6;
            final int n12 = i + 1;
            array[n12] ^= n7;
            final int n13 = i + 2;
            array[n13] ^= n8;
            final int n14 = i + 3;
            array[n14] ^= n9;
            final int n15 = i + 4;
            array[n15] ^= n10;
        }
        return array;
    }
    
    private static long[] smPiRho(final long[] array) {
        final long rotateLeft = Long.rotateLeft(array[10], 3);
        array[10] = Long.rotateLeft(array[1], 1);
        array[1] = Long.rotateLeft(array[6], 44);
        array[6] = Long.rotateLeft(array[9], 20);
        array[9] = Long.rotateLeft(array[22], 61);
        array[22] = Long.rotateLeft(array[14], 39);
        array[14] = Long.rotateLeft(array[20], 18);
        array[20] = Long.rotateLeft(array[2], 62);
        array[2] = Long.rotateLeft(array[12], 43);
        array[12] = Long.rotateLeft(array[13], 25);
        array[13] = Long.rotateLeft(array[19], 8);
        array[19] = Long.rotateLeft(array[23], 56);
        array[23] = Long.rotateLeft(array[15], 41);
        array[15] = Long.rotateLeft(array[4], 27);
        array[4] = Long.rotateLeft(array[24], 14);
        array[24] = Long.rotateLeft(array[21], 2);
        array[21] = Long.rotateLeft(array[8], 55);
        array[8] = Long.rotateLeft(array[16], 45);
        array[16] = Long.rotateLeft(array[5], 36);
        array[5] = Long.rotateLeft(array[3], 28);
        array[3] = Long.rotateLeft(array[18], 21);
        array[18] = Long.rotateLeft(array[17], 15);
        array[17] = Long.rotateLeft(array[11], 10);
        array[11] = Long.rotateLeft(array[7], 6);
        array[7] = rotateLeft;
        return array;
    }
    
    private static long[] smChi(final long[] array) {
        for (int i = 0; i < array.length; i += 5) {
            final long n = array[i];
            final long n2 = array[i + 1];
            final long n3 = array[i + 2];
            final long n4 = array[i + 3];
            final long n5 = array[i + 4];
            array[i] = (n ^ (~n2 & n3));
            array[i + 1] = (n2 ^ (~n3 & n4));
            array[i + 2] = (n3 ^ (~n4 & n5));
            array[i + 3] = (n4 ^ (~n5 & n));
            array[i + 4] = (n5 ^ (~n & n2));
        }
        return array;
    }
    
    private static long[] smIota(final long[] array, final int n) {
        final int n2 = 0;
        array[n2] ^= SHA3.RC_CONSTANTS[n];
        return array;
    }
    
    private void keccak() {
        bytes2Lanes(this.state, this.lanes);
        for (int i = 0; i < 24; ++i) {
            smIota(smChi(smPiRho(smTheta(this.lanes))), i);
        }
        lanes2Bytes(this.lanes, this.state);
    }
    
    @Override
    public Object clone() throws CloneNotSupportedException {
        final SHA3 sha3 = (SHA3)super.clone();
        sha3.state = sha3.state.clone();
        return sha3;
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
