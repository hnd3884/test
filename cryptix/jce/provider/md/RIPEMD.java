package cryptix.jce.provider.md;

public final class RIPEMD extends PaddingMD
{
    private static final int HASH_SIZE = 16;
    private int[] context;
    private int[] savedContext;
    private int[] X;
    
    public Object clone() {
        return new RIPEMD(this);
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
        int A = this.context[0];
        int B = this.context[1];
        int C = this.context[2];
        int D = this.context[3];
        A = this.FF(A, B, C, D, this.X[0], 11);
        D = this.FF(D, A, B, C, this.X[1], 14);
        C = this.FF(C, D, A, B, this.X[2], 15);
        B = this.FF(B, C, D, A, this.X[3], 12);
        A = this.FF(A, B, C, D, this.X[4], 5);
        D = this.FF(D, A, B, C, this.X[5], 8);
        C = this.FF(C, D, A, B, this.X[6], 7);
        B = this.FF(B, C, D, A, this.X[7], 9);
        A = this.FF(A, B, C, D, this.X[8], 11);
        D = this.FF(D, A, B, C, this.X[9], 13);
        C = this.FF(C, D, A, B, this.X[10], 14);
        B = this.FF(B, C, D, A, this.X[11], 15);
        A = this.FF(A, B, C, D, this.X[12], 6);
        D = this.FF(D, A, B, C, this.X[13], 7);
        C = this.FF(C, D, A, B, this.X[14], 9);
        B = this.FF(B, C, D, A, this.X[15], 8);
        A = this.GG(A, B, C, D, this.X[7], 7);
        D = this.GG(D, A, B, C, this.X[4], 6);
        C = this.GG(C, D, A, B, this.X[13], 8);
        B = this.GG(B, C, D, A, this.X[1], 13);
        A = this.GG(A, B, C, D, this.X[10], 11);
        D = this.GG(D, A, B, C, this.X[6], 9);
        C = this.GG(C, D, A, B, this.X[15], 7);
        B = this.GG(B, C, D, A, this.X[3], 15);
        A = this.GG(A, B, C, D, this.X[12], 7);
        D = this.GG(D, A, B, C, this.X[0], 12);
        C = this.GG(C, D, A, B, this.X[9], 15);
        B = this.GG(B, C, D, A, this.X[5], 9);
        A = this.GG(A, B, C, D, this.X[14], 7);
        D = this.GG(D, A, B, C, this.X[2], 11);
        C = this.GG(C, D, A, B, this.X[11], 13);
        B = this.GG(B, C, D, A, this.X[8], 12);
        A = this.HH(A, B, C, D, this.X[3], 11);
        D = this.HH(D, A, B, C, this.X[10], 13);
        C = this.HH(C, D, A, B, this.X[2], 14);
        B = this.HH(B, C, D, A, this.X[4], 7);
        A = this.HH(A, B, C, D, this.X[9], 14);
        D = this.HH(D, A, B, C, this.X[15], 9);
        C = this.HH(C, D, A, B, this.X[8], 13);
        B = this.HH(B, C, D, A, this.X[1], 15);
        A = this.HH(A, B, C, D, this.X[14], 6);
        D = this.HH(D, A, B, C, this.X[7], 8);
        C = this.HH(C, D, A, B, this.X[0], 13);
        B = this.HH(B, C, D, A, this.X[6], 6);
        A = this.HH(A, B, C, D, this.X[11], 12);
        D = this.HH(D, A, B, C, this.X[13], 5);
        C = this.HH(C, D, A, B, this.X[5], 7);
        B = this.HH(B, C, D, A, this.X[12], 5);
        this.savedContext[0] = A;
        this.savedContext[1] = B;
        this.savedContext[2] = C;
        this.savedContext[3] = D;
        A = this.context[0];
        B = this.context[1];
        C = this.context[2];
        D = this.context[3];
        A = this.FFP(A, B, C, D, this.X[0], 11);
        D = this.FFP(D, A, B, C, this.X[1], 14);
        C = this.FFP(C, D, A, B, this.X[2], 15);
        B = this.FFP(B, C, D, A, this.X[3], 12);
        A = this.FFP(A, B, C, D, this.X[4], 5);
        D = this.FFP(D, A, B, C, this.X[5], 8);
        C = this.FFP(C, D, A, B, this.X[6], 7);
        B = this.FFP(B, C, D, A, this.X[7], 9);
        A = this.FFP(A, B, C, D, this.X[8], 11);
        D = this.FFP(D, A, B, C, this.X[9], 13);
        C = this.FFP(C, D, A, B, this.X[10], 14);
        B = this.FFP(B, C, D, A, this.X[11], 15);
        A = this.FFP(A, B, C, D, this.X[12], 6);
        D = this.FFP(D, A, B, C, this.X[13], 7);
        C = this.FFP(C, D, A, B, this.X[14], 9);
        B = this.FFP(B, C, D, A, this.X[15], 8);
        A = this.GGP(A, B, C, D, this.X[7], 7);
        D = this.GGP(D, A, B, C, this.X[4], 6);
        C = this.GGP(C, D, A, B, this.X[13], 8);
        B = this.GGP(B, C, D, A, this.X[1], 13);
        A = this.GGP(A, B, C, D, this.X[10], 11);
        D = this.GGP(D, A, B, C, this.X[6], 9);
        C = this.GGP(C, D, A, B, this.X[15], 7);
        B = this.GGP(B, C, D, A, this.X[3], 15);
        A = this.GGP(A, B, C, D, this.X[12], 7);
        D = this.GGP(D, A, B, C, this.X[0], 12);
        C = this.GGP(C, D, A, B, this.X[9], 15);
        B = this.GGP(B, C, D, A, this.X[5], 9);
        A = this.GGP(A, B, C, D, this.X[14], 7);
        D = this.GGP(D, A, B, C, this.X[2], 11);
        C = this.GGP(C, D, A, B, this.X[11], 13);
        B = this.GGP(B, C, D, A, this.X[8], 12);
        A = this.HHP(A, B, C, D, this.X[3], 11);
        D = this.HHP(D, A, B, C, this.X[10], 13);
        C = this.HHP(C, D, A, B, this.X[2], 14);
        B = this.HHP(B, C, D, A, this.X[4], 7);
        A = this.HHP(A, B, C, D, this.X[9], 14);
        D = this.HHP(D, A, B, C, this.X[15], 9);
        C = this.HHP(C, D, A, B, this.X[8], 13);
        B = this.HHP(B, C, D, A, this.X[1], 15);
        A = this.HHP(A, B, C, D, this.X[14], 6);
        D = this.HHP(D, A, B, C, this.X[7], 8);
        C = this.HHP(C, D, A, B, this.X[0], 13);
        B = this.HHP(B, C, D, A, this.X[6], 6);
        A = this.HHP(A, B, C, D, this.X[11], 12);
        D = this.HHP(D, A, B, C, this.X[13], 5);
        C = this.HHP(C, D, A, B, this.X[5], 7);
        B = this.HHP(B, C, D, A, this.X[12], 5);
        A += this.savedContext[3];
        B += this.savedContext[0];
        C += this.savedContext[1];
        D += this.savedContext[2];
        A += this.context[2];
        B += this.context[3];
        C += this.context[0];
        D += this.context[1];
        this.context[1] = A;
        this.context[2] = B;
        this.context[3] = C;
        this.context[0] = D;
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
    
    private int FFP(final int a, final int b, final int c, final int d, final int x, final int s) {
        final int t = a + ((b & c) | (~b & d)) + x + 1352829926;
        return t << s | t >>> 32 - s;
    }
    
    private int GGP(final int a, final int b, final int c, final int d, final int x, final int s) {
        final int t = a + ((b & (c | d)) | (c & d)) + x;
        return t << s | t >>> 32 - s;
    }
    
    private int HHP(final int a, final int b, final int c, final int d, final int x, final int s) {
        final int t = a + (b ^ c ^ d) + x + 1548603684;
        return t << s | t >>> 32 - s;
    }
    
    public RIPEMD() {
        super(16, 0);
        this.context = new int[4];
        this.savedContext = new int[4];
        this.X = new int[16];
        this.coreReset();
    }
    
    private RIPEMD(final RIPEMD src) {
        super(src);
        this.context = new int[4];
        this.savedContext = new int[4];
        this.X = new int[16];
        this.context = src.context.clone();
        this.savedContext = src.savedContext.clone();
        this.X = src.X.clone();
    }
}
