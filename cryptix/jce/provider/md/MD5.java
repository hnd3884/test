package cryptix.jce.provider.md;

public final class MD5 extends PaddingMD implements Cloneable
{
    private static final int HASH_SIZE = 16;
    private int[] context;
    private int[] X;
    
    public Object clone() {
        return new MD5(this);
    }
    
    protected void coreDigest(final byte[] buf, final int off) {
        for (int i = 0; i < 4; ++i) {
            for (int j = 0; j < 4; ++j) {
                buf[off + (i * 4 + j)] = (byte)(this.context[i] >>> 8 * j);
            }
        }
    }
    
    protected void coreReset() {
        this.context[0] = 1732584193;
        this.context[1] = -271733879;
        this.context[2] = -1732584194;
        this.context[3] = 271733878;
    }
    
    protected void coreUpdate(final byte[] block, int offset) {
        for (int i = 0; i < 16; ++i) {
            this.X[i] = ((block[offset++] & 0xFF) | (block[offset++] & 0xFF) << 8 | (block[offset++] & 0xFF) << 16 | (block[offset++] & 0xFF) << 24);
        }
        int a = this.context[0];
        int b = this.context[1];
        int c = this.context[2];
        int d = this.context[3];
        a = FF(a, b, c, d, this.X[0], 7, -680876936);
        d = FF(d, a, b, c, this.X[1], 12, -389564586);
        c = FF(c, d, a, b, this.X[2], 17, 606105819);
        b = FF(b, c, d, a, this.X[3], 22, -1044525330);
        a = FF(a, b, c, d, this.X[4], 7, -176418897);
        d = FF(d, a, b, c, this.X[5], 12, 1200080426);
        c = FF(c, d, a, b, this.X[6], 17, -1473231341);
        b = FF(b, c, d, a, this.X[7], 22, -45705983);
        a = FF(a, b, c, d, this.X[8], 7, 1770035416);
        d = FF(d, a, b, c, this.X[9], 12, -1958414417);
        c = FF(c, d, a, b, this.X[10], 17, -42063);
        b = FF(b, c, d, a, this.X[11], 22, -1990404162);
        a = FF(a, b, c, d, this.X[12], 7, 1804603682);
        d = FF(d, a, b, c, this.X[13], 12, -40341101);
        c = FF(c, d, a, b, this.X[14], 17, -1502002290);
        b = FF(b, c, d, a, this.X[15], 22, 1236535329);
        a = GG(a, b, c, d, this.X[1], 5, -165796510);
        d = GG(d, a, b, c, this.X[6], 9, -1069501632);
        c = GG(c, d, a, b, this.X[11], 14, 643717713);
        b = GG(b, c, d, a, this.X[0], 20, -373897302);
        a = GG(a, b, c, d, this.X[5], 5, -701558691);
        d = GG(d, a, b, c, this.X[10], 9, 38016083);
        c = GG(c, d, a, b, this.X[15], 14, -660478335);
        b = GG(b, c, d, a, this.X[4], 20, -405537848);
        a = GG(a, b, c, d, this.X[9], 5, 568446438);
        d = GG(d, a, b, c, this.X[14], 9, -1019803690);
        c = GG(c, d, a, b, this.X[3], 14, -187363961);
        b = GG(b, c, d, a, this.X[8], 20, 1163531501);
        a = GG(a, b, c, d, this.X[13], 5, -1444681467);
        d = GG(d, a, b, c, this.X[2], 9, -51403784);
        c = GG(c, d, a, b, this.X[7], 14, 1735328473);
        b = GG(b, c, d, a, this.X[12], 20, -1926607734);
        a = HH(a, b, c, d, this.X[5], 4, -378558);
        d = HH(d, a, b, c, this.X[8], 11, -2022574463);
        c = HH(c, d, a, b, this.X[11], 16, 1839030562);
        b = HH(b, c, d, a, this.X[14], 23, -35309556);
        a = HH(a, b, c, d, this.X[1], 4, -1530992060);
        d = HH(d, a, b, c, this.X[4], 11, 1272893353);
        c = HH(c, d, a, b, this.X[7], 16, -155497632);
        b = HH(b, c, d, a, this.X[10], 23, -1094730640);
        a = HH(a, b, c, d, this.X[13], 4, 681279174);
        d = HH(d, a, b, c, this.X[0], 11, -358537222);
        c = HH(c, d, a, b, this.X[3], 16, -722521979);
        b = HH(b, c, d, a, this.X[6], 23, 76029189);
        a = HH(a, b, c, d, this.X[9], 4, -640364487);
        d = HH(d, a, b, c, this.X[12], 11, -421815835);
        c = HH(c, d, a, b, this.X[15], 16, 530742520);
        b = HH(b, c, d, a, this.X[2], 23, -995338651);
        a = this.II(a, b, c, d, this.X[0], 6, -198630844);
        d = this.II(d, a, b, c, this.X[7], 10, 1126891415);
        c = this.II(c, d, a, b, this.X[14], 15, -1416354905);
        b = this.II(b, c, d, a, this.X[5], 21, -57434055);
        a = this.II(a, b, c, d, this.X[12], 6, 1700485571);
        d = this.II(d, a, b, c, this.X[3], 10, -1894986606);
        c = this.II(c, d, a, b, this.X[10], 15, -1051523);
        b = this.II(b, c, d, a, this.X[1], 21, -2054922799);
        a = this.II(a, b, c, d, this.X[8], 6, 1873313359);
        d = this.II(d, a, b, c, this.X[15], 10, -30611744);
        c = this.II(c, d, a, b, this.X[6], 15, -1560198380);
        b = this.II(b, c, d, a, this.X[13], 21, 1309151649);
        a = this.II(a, b, c, d, this.X[4], 6, -145523070);
        d = this.II(d, a, b, c, this.X[11], 10, -1120210379);
        c = this.II(c, d, a, b, this.X[2], 15, 718787259);
        b = this.II(b, c, d, a, this.X[9], 21, -343485551);
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
    }
    
    private static int F(final int x, final int y, final int z) {
        return z ^ (x & (y ^ z));
    }
    
    private static int G(final int x, final int y, final int z) {
        return y ^ (z & (x ^ y));
    }
    
    private static int H(final int x, final int y, final int z) {
        return x ^ y ^ z;
    }
    
    private static int I(final int x, final int y, final int z) {
        return y ^ (x | ~z);
    }
    
    private static int FF(int a, final int b, final int c, final int d, final int k, final int s, final int t) {
        a += k + t + F(b, c, d);
        a = (a << s | a >>> -s);
        return a + b;
    }
    
    private static int GG(int a, final int b, final int c, final int d, final int k, final int s, final int t) {
        a += k + t + G(b, c, d);
        a = (a << s | a >>> -s);
        return a + b;
    }
    
    private static int HH(int a, final int b, final int c, final int d, final int k, final int s, final int t) {
        a += k + t + H(b, c, d);
        a = (a << s | a >>> -s);
        return a + b;
    }
    
    private int II(int a, final int b, final int c, final int d, final int k, final int s, final int t) {
        a += k + t + I(b, c, d);
        a = (a << s | a >>> -s);
        return a + b;
    }
    
    public MD5() {
        super(16, 0);
        this.context = new int[4];
        this.X = new int[16];
        this.coreReset();
    }
    
    private MD5(final MD5 src) {
        super(src);
        this.context = new int[4];
        this.X = new int[16];
        this.context = src.context.clone();
        this.X = src.X.clone();
    }
}
