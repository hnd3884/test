package sun.security.provider;

import java.util.Objects;
import java.util.Arrays;

abstract class SHA2 extends DigestBase
{
    private static final int ITERATION = 64;
    private static final int[] ROUND_CONSTS;
    private int[] W;
    private int[] state;
    private final int[] initialHashes;
    
    SHA2(final String s, final int n, final int[] initialHashes) {
        super(s, n, 64);
        this.initialHashes = initialHashes;
        this.state = new int[8];
        this.W = new int[64];
        this.resetHashes();
    }
    
    @Override
    void implReset() {
        this.resetHashes();
        Arrays.fill(this.W, 0);
    }
    
    private void resetHashes() {
        System.arraycopy(this.initialHashes, 0, this.state, 0, this.state.length);
    }
    
    @Override
    void implDigest(final byte[] array, final int n) {
        final long n2 = this.bytesProcessed << 3;
        final int n3 = (int)this.bytesProcessed & 0x3F;
        this.engineUpdate(SHA2.padding, 0, (n3 < 56) ? (56 - n3) : (120 - n3));
        ByteArrayAccess.i2bBig4((int)(n2 >>> 32), this.buffer, 56);
        ByteArrayAccess.i2bBig4((int)n2, this.buffer, 60);
        this.implCompress(this.buffer, 0);
        ByteArrayAccess.i2bBig(this.state, 0, array, n, this.engineGetDigestLength());
    }
    
    private static int lf_ch(final int n, final int n2, final int n3) {
        return (n & n2) ^ (~n & n3);
    }
    
    private static int lf_maj(final int n, final int n2, final int n3) {
        return (n & n2) ^ (n & n3) ^ (n2 & n3);
    }
    
    private static int lf_R(final int n, final int n2) {
        return n >>> n2;
    }
    
    private static int lf_S(final int n, final int n2) {
        return n >>> n2 | n << 32 - n2;
    }
    
    private static int lf_sigma0(final int n) {
        return lf_S(n, 2) ^ lf_S(n, 13) ^ lf_S(n, 22);
    }
    
    private static int lf_sigma1(final int n) {
        return lf_S(n, 6) ^ lf_S(n, 11) ^ lf_S(n, 25);
    }
    
    private static int lf_delta0(final int n) {
        return lf_S(n, 7) ^ lf_S(n, 18) ^ lf_R(n, 3);
    }
    
    private static int lf_delta1(final int n) {
        return lf_S(n, 17) ^ lf_S(n, 19) ^ lf_R(n, 10);
    }
    
    @Override
    void implCompress(final byte[] array, final int n) {
        this.implCompressCheck(array, n);
        this.implCompress0(array, n);
    }
    
    private void implCompressCheck(final byte[] array, final int n) {
        Objects.requireNonNull(array);
        ByteArrayAccess.b2iBig64(array, n, this.W);
    }
    
    private void implCompress0(final byte[] array, final int n) {
        for (int i = 16; i < 64; ++i) {
            this.W[i] = lf_delta1(this.W[i - 2]) + this.W[i - 7] + lf_delta0(this.W[i - 15]) + this.W[i - 16];
        }
        int n2 = this.state[0];
        int n3 = this.state[1];
        int n4 = this.state[2];
        int n5 = this.state[3];
        int n6 = this.state[4];
        int n7 = this.state[5];
        int n8 = this.state[6];
        int n9 = this.state[7];
        for (int j = 0; j < 64; ++j) {
            final int n10 = n9 + lf_sigma1(n6) + lf_ch(n6, n7, n8) + SHA2.ROUND_CONSTS[j] + this.W[j];
            final int n11 = lf_sigma0(n2) + lf_maj(n2, n3, n4);
            n9 = n8;
            n8 = n7;
            n7 = n6;
            n6 = n5 + n10;
            n5 = n4;
            n4 = n3;
            n3 = n2;
            n2 = n10 + n11;
        }
        final int[] state = this.state;
        final int n12 = 0;
        state[n12] += n2;
        final int[] state2 = this.state;
        final int n13 = 1;
        state2[n13] += n3;
        final int[] state3 = this.state;
        final int n14 = 2;
        state3[n14] += n4;
        final int[] state4 = this.state;
        final int n15 = 3;
        state4[n15] += n5;
        final int[] state5 = this.state;
        final int n16 = 4;
        state5[n16] += n6;
        final int[] state6 = this.state;
        final int n17 = 5;
        state6[n17] += n7;
        final int[] state7 = this.state;
        final int n18 = 6;
        state7[n18] += n8;
        final int[] state8 = this.state;
        final int n19 = 7;
        state8[n19] += n9;
    }
    
    @Override
    public Object clone() throws CloneNotSupportedException {
        final SHA2 sha2 = (SHA2)super.clone();
        sha2.state = sha2.state.clone();
        sha2.W = new int[64];
        return sha2;
    }
    
    static {
        ROUND_CONSTS = new int[] { 1116352408, 1899447441, -1245643825, -373957723, 961987163, 1508970993, -1841331548, -1424204075, -670586216, 310598401, 607225278, 1426881987, 1925078388, -2132889090, -1680079193, -1046744716, -459576895, -272742522, 264347078, 604807628, 770255983, 1249150122, 1555081692, 1996064986, -1740746414, -1473132947, -1341970488, -1084653625, -958395405, -710438585, 113926993, 338241895, 666307205, 773529912, 1294757372, 1396182291, 1695183700, 1986661051, -2117940946, -1838011259, -1564481375, -1474664885, -1035236496, -949202525, -778901479, -694614492, -200395387, 275423344, 430227734, 506948616, 659060556, 883997877, 958139571, 1322822218, 1537002063, 1747873779, 1955562222, 2024104815, -2067236844, -1933114872, -1866530822, -1538233109, -1090935817, -965641998 };
    }
    
    public static final class SHA224 extends SHA2
    {
        private static final int[] INITIAL_HASHES;
        
        public SHA224() {
            super("SHA-224", 28, SHA224.INITIAL_HASHES);
        }
        
        static {
            INITIAL_HASHES = new int[] { -1056596264, 914150663, 812702999, -150054599, -4191439, 1750603025, 1694076839, -1090891868 };
        }
    }
    
    public static final class SHA256 extends SHA2
    {
        private static final int[] INITIAL_HASHES;
        
        public SHA256() {
            super("SHA-256", 32, SHA256.INITIAL_HASHES);
        }
        
        static {
            INITIAL_HASHES = new int[] { 1779033703, -1150833019, 1013904242, -1521486534, 1359893119, -1694144372, 528734635, 1541459225 };
        }
    }
}
