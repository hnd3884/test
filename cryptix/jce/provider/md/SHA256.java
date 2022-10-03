package cryptix.jce.provider.md;

public class SHA256 extends PaddingMD implements Cloneable
{
    private static final int HASH_SIZE = 32;
    private static final int[] K;
    private final int[] context;
    private final int[] buffer;
    
    public Object clone() {
        return new SHA256(this);
    }
    
    protected void coreDigest(final byte[] buf, final int off) {
        for (int i = 0; i < this.context.length; ++i) {
            for (int j = 0; j < 4; ++j) {
                buf[off + (i * 4 + (3 - j))] = (byte)(this.context[i] >>> 8 * j);
            }
        }
    }
    
    protected void coreReset() {
        this.context[0] = 1779033703;
        this.context[1] = -1150833019;
        this.context[2] = 1013904242;
        this.context[3] = -1521486534;
        this.context[4] = 1359893119;
        this.context[5] = -1694144372;
        this.context[6] = 528734635;
        this.context[7] = 1541459225;
    }
    
    protected void coreUpdate(final byte[] block, int offset) {
        final int[] W = this.buffer;
        for (int i = 0; i < 16; ++i) {
            W[i] = (block[offset++] << 24 | (block[offset++] & 0xFF) << 16 | (block[offset++] & 0xFF) << 8 | (block[offset++] & 0xFF));
        }
        for (int i = 16; i < 64; ++i) {
            W[i] = this.sig1(W[i - 2]) + W[i - 7] + this.sig0(W[i - 15]) + W[i - 16];
        }
        int a = this.context[0];
        int b = this.context[1];
        int c = this.context[2];
        int d = this.context[3];
        int e = this.context[4];
        int f = this.context[5];
        int g = this.context[6];
        int h = this.context[7];
        for (int j = 0; j < 64; ++j) {
            final int T1 = h + this.Sig1(e) + this.Ch(e, f, g) + SHA256.K[j] + W[j];
            final int T2 = this.Sig0(a) + this.Maj(a, b, c);
            h = g;
            g = f;
            f = e;
            e = d + T1;
            d = c;
            c = b;
            b = a;
            a = T1 + T2;
        }
        final int[] context = this.context;
        final int n = 0;
        context[n] += a;
        final int[] context2 = this.context;
        final int n2 = 1;
        context2[n2] += b;
        final int[] context3 = this.context;
        final int n3 = 2;
        context3[n3] += c;
        final int[] context4 = this.context;
        final int n4 = 3;
        context4[n4] += d;
        final int[] context5 = this.context;
        final int n5 = 4;
        context5[n5] += e;
        final int[] context6 = this.context;
        final int n6 = 5;
        context6[n6] += f;
        final int[] context7 = this.context;
        final int n7 = 6;
        context7[n7] += g;
        final int[] context8 = this.context;
        final int n8 = 7;
        context8[n8] += h;
    }
    
    private final int Ch(final int x, final int y, final int z) {
        return (x & y) ^ (~x & z);
    }
    
    private final int Maj(final int x, final int y, final int z) {
        return (x & y) ^ (x & z) ^ (y & z);
    }
    
    private final int Sig0(final int x) {
        return this.S(2, x) ^ this.S(13, x) ^ this.S(22, x);
    }
    
    private final int Sig1(final int x) {
        return this.S(6, x) ^ this.S(11, x) ^ this.S(25, x);
    }
    
    private final int sig0(final int x) {
        return this.S(7, x) ^ this.S(18, x) ^ this.R(3, x);
    }
    
    private final int sig1(final int x) {
        return this.S(17, x) ^ this.S(19, x) ^ this.R(10, x);
    }
    
    private final int R(final int off, final int x) {
        return x >>> off;
    }
    
    private final int S(final int off, final int x) {
        return x >>> off | x << 32 - off;
    }
    
    public SHA256() {
        super(32, 1);
        this.context = new int[8];
        this.buffer = new int[64];
        this.coreReset();
    }
    
    private SHA256(final SHA256 src) {
        super(src);
        this.context = src.context.clone();
        this.buffer = src.buffer.clone();
    }
    
    static {
        K = new int[] { 1116352408, 1899447441, -1245643825, -373957723, 961987163, 1508970993, -1841331548, -1424204075, -670586216, 310598401, 607225278, 1426881987, 1925078388, -2132889090, -1680079193, -1046744716, -459576895, -272742522, 264347078, 604807628, 770255983, 1249150122, 1555081692, 1996064986, -1740746414, -1473132947, -1341970488, -1084653625, -958395405, -710438585, 113926993, 338241895, 666307205, 773529912, 1294757372, 1396182291, 1695183700, 1986661051, -2117940946, -1838011259, -1564481375, -1474664885, -1035236496, -949202525, -778901479, -694614492, -200395387, 275423344, 430227734, 506948616, 659060556, 883997877, 958139571, 1322822218, 1537002063, 1747873779, 1955562222, 2024104815, -2067236844, -1933114872, -1866530822, -1538233109, -1090935817, -965641998 };
    }
}
