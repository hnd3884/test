package sun.security.provider;

import java.util.Objects;
import java.util.Arrays;

public final class SHA extends DigestBase
{
    private int[] W;
    private int[] state;
    private static final int round1_kt = 1518500249;
    private static final int round2_kt = 1859775393;
    private static final int round3_kt = -1894007588;
    private static final int round4_kt = -899497514;
    
    public SHA() {
        super("SHA-1", 20, 64);
        this.state = new int[5];
        this.W = new int[80];
        this.resetHashes();
    }
    
    @Override
    public Object clone() throws CloneNotSupportedException {
        final SHA sha = (SHA)super.clone();
        sha.state = sha.state.clone();
        sha.W = new int[80];
        return sha;
    }
    
    @Override
    void implReset() {
        this.resetHashes();
        Arrays.fill(this.W, 0);
    }
    
    private void resetHashes() {
        this.state[0] = 1732584193;
        this.state[1] = -271733879;
        this.state[2] = -1732584194;
        this.state[3] = 271733878;
        this.state[4] = -1009589776;
    }
    
    @Override
    void implDigest(final byte[] array, final int n) {
        final long n2 = this.bytesProcessed << 3;
        final int n3 = (int)this.bytesProcessed & 0x3F;
        this.engineUpdate(SHA.padding, 0, (n3 < 56) ? (56 - n3) : (120 - n3));
        ByteArrayAccess.i2bBig4((int)(n2 >>> 32), this.buffer, 56);
        ByteArrayAccess.i2bBig4((int)n2, this.buffer, 60);
        this.implCompress(this.buffer, 0);
        ByteArrayAccess.i2bBig(this.state, 0, array, n, 20);
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
        for (int i = 16; i <= 79; ++i) {
            final int n2 = this.W[i - 3] ^ this.W[i - 8] ^ this.W[i - 14] ^ this.W[i - 16];
            this.W[i] = (n2 << 1 | n2 >>> 31);
        }
        int n3 = this.state[0];
        int n4 = this.state[1];
        int n5 = this.state[2];
        int n6 = this.state[3];
        int n7 = this.state[4];
        for (int j = 0; j < 20; ++j) {
            final int n8 = (n3 << 5 | n3 >>> 27) + ((n4 & n5) | (~n4 & n6)) + n7 + this.W[j] + 1518500249;
            n7 = n6;
            n6 = n5;
            n5 = (n4 << 30 | n4 >>> 2);
            n4 = n3;
            n3 = n8;
        }
        for (int k = 20; k < 40; ++k) {
            final int n9 = (n3 << 5 | n3 >>> 27) + (n4 ^ n5 ^ n6) + n7 + this.W[k] + 1859775393;
            n7 = n6;
            n6 = n5;
            n5 = (n4 << 30 | n4 >>> 2);
            n4 = n3;
            n3 = n9;
        }
        for (int l = 40; l < 60; ++l) {
            final int n10 = (n3 << 5 | n3 >>> 27) + ((n4 & n5) | (n4 & n6) | (n5 & n6)) + n7 + this.W[l] - 1894007588;
            n7 = n6;
            n6 = n5;
            n5 = (n4 << 30 | n4 >>> 2);
            n4 = n3;
            n3 = n10;
        }
        for (int n11 = 60; n11 < 80; ++n11) {
            final int n12 = (n3 << 5 | n3 >>> 27) + (n4 ^ n5 ^ n6) + n7 + this.W[n11] - 899497514;
            n7 = n6;
            n6 = n5;
            n5 = (n4 << 30 | n4 >>> 2);
            n4 = n3;
            n3 = n12;
        }
        final int[] state = this.state;
        final int n13 = 0;
        state[n13] += n3;
        final int[] state2 = this.state;
        final int n14 = 1;
        state2[n14] += n4;
        final int[] state3 = this.state;
        final int n15 = 2;
        state3[n15] += n5;
        final int[] state4 = this.state;
        final int n16 = 3;
        state4[n16] += n6;
        final int[] state5 = this.state;
        final int n17 = 4;
        state5[n17] += n7;
    }
}
