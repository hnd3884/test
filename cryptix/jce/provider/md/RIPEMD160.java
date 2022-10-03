package cryptix.jce.provider.md;

public final class RIPEMD160 extends PaddingMD implements Cloneable
{
    private static final int[] R;
    private static final int[] Rp;
    private static final int[] S;
    private static final int[] Sp;
    private static final int HASH_SIZE = 20;
    private int[] context;
    private int[] X;
    
    public Object clone() {
        return new RIPEMD160(this);
    }
    
    protected void coreDigest(final byte[] buf, final int off) {
        for (int i = 0; i < 5; ++i) {
            for (int j = 0; j < 4; ++j) {
                buf[off + (i * 4 + j)] = (byte)(this.context[i] >>> 8 * j & 0xFF);
            }
        }
    }
    
    protected void coreReset() {
        this.context[0] = 1732584193;
        this.context[1] = -271733879;
        this.context[2] = -1732584194;
        this.context[3] = 271733878;
        this.context[4] = -1009589776;
    }
    
    protected void coreUpdate(final byte[] block, int offset) {
        for (int j = 0; j < 16; ++j) {
            this.X[j] = ((block[offset++] & 0xFF) | (block[offset++] & 0xFF) << 8 | (block[offset++] & 0xFF) << 16 | (block[offset++] & 0xFF) << 24);
        }
        int A;
        int Ap = A = this.context[0];
        int B;
        int Bp = B = this.context[1];
        int C;
        int Cp = C = this.context[2];
        int D;
        int Dp = D = this.context[3];
        int E;
        int Ep = E = this.context[4];
        for (int i = 0; i < 16; ++i) {
            int s = RIPEMD160.S[i];
            int T = A + (B ^ C ^ D) + this.X[i];
            A = E;
            E = D;
            D = (C << 10 | C >>> 22);
            C = B;
            B = (T << s | T >>> 32 - s) + A;
            s = RIPEMD160.Sp[i];
            T = Ap + (Bp ^ (Cp | ~Dp)) + this.X[RIPEMD160.Rp[i]] + 1352829926;
            Ap = Ep;
            Ep = Dp;
            Dp = (Cp << 10 | Cp >>> 22);
            Cp = Bp;
            Bp = (T << s | T >>> 32 - s) + Ap;
        }
        for (int i = 16; i < 32; ++i) {
            int s = RIPEMD160.S[i];
            int T = A + ((B & C) | (~B & D)) + this.X[RIPEMD160.R[i]] + 1518500249;
            A = E;
            E = D;
            D = (C << 10 | C >>> 22);
            C = B;
            B = (T << s | T >>> 32 - s) + A;
            s = RIPEMD160.Sp[i];
            T = Ap + ((Bp & Dp) | (Cp & ~Dp)) + this.X[RIPEMD160.Rp[i]] + 1548603684;
            Ap = Ep;
            Ep = Dp;
            Dp = (Cp << 10 | Cp >>> 22);
            Cp = Bp;
            Bp = (T << s | T >>> 32 - s) + Ap;
        }
        for (int i = 32; i < 48; ++i) {
            int s = RIPEMD160.S[i];
            int T = A + ((B | ~C) ^ D) + this.X[RIPEMD160.R[i]] + 1859775393;
            A = E;
            E = D;
            D = (C << 10 | C >>> 22);
            C = B;
            B = (T << s | T >>> 32 - s) + A;
            s = RIPEMD160.Sp[i];
            T = Ap + ((Bp | ~Cp) ^ Dp) + this.X[RIPEMD160.Rp[i]] + 1836072691;
            Ap = Ep;
            Ep = Dp;
            Dp = (Cp << 10 | Cp >>> 22);
            Cp = Bp;
            Bp = (T << s | T >>> 32 - s) + Ap;
        }
        for (int i = 48; i < 64; ++i) {
            int s = RIPEMD160.S[i];
            int T = A + ((B & D) | (C & ~D)) + this.X[RIPEMD160.R[i]] - 1894007588;
            A = E;
            E = D;
            D = (C << 10 | C >>> 22);
            C = B;
            B = (T << s | T >>> 32 - s) + A;
            s = RIPEMD160.Sp[i];
            T = Ap + ((Bp & Cp) | (~Bp & Dp)) + this.X[RIPEMD160.Rp[i]] + 2053994217;
            Ap = Ep;
            Ep = Dp;
            Dp = (Cp << 10 | Cp >>> 22);
            Cp = Bp;
            Bp = (T << s | T >>> 32 - s) + Ap;
        }
        for (int i = 64; i < 80; ++i) {
            int s = RIPEMD160.S[i];
            int T = A + (B ^ (C | ~D)) + this.X[RIPEMD160.R[i]] - 1454113458;
            A = E;
            E = D;
            D = (C << 10 | C >>> 22);
            C = B;
            B = (T << s | T >>> 32 - s) + A;
            s = RIPEMD160.Sp[i];
            T = Ap + (Bp ^ Cp ^ Dp) + this.X[RIPEMD160.Rp[i]];
            Ap = Ep;
            Ep = Dp;
            Dp = (Cp << 10 | Cp >>> 22);
            Cp = Bp;
            Bp = (T << s | T >>> 32 - s) + Ap;
        }
        int T = this.context[1] + C + Dp;
        this.context[1] = this.context[2] + D + Ep;
        this.context[2] = this.context[3] + E + Ap;
        this.context[3] = this.context[4] + A + Bp;
        this.context[4] = this.context[0] + B + Cp;
        this.context[0] = T;
    }
    
    public RIPEMD160() {
        super(20, 0);
        this.context = new int[5];
        this.X = new int[16];
        this.coreReset();
    }
    
    private RIPEMD160(final RIPEMD160 src) {
        super(src);
        this.context = new int[5];
        this.X = new int[16];
        this.context = src.context.clone();
        this.X = src.X.clone();
    }
    
    static {
        R = new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 7, 4, 13, 1, 10, 6, 15, 3, 12, 0, 9, 5, 2, 14, 11, 8, 3, 10, 14, 4, 9, 15, 8, 1, 2, 7, 0, 6, 13, 11, 5, 12, 1, 9, 11, 10, 0, 8, 12, 4, 13, 3, 7, 15, 14, 5, 6, 2, 4, 0, 5, 9, 7, 12, 2, 10, 14, 1, 3, 8, 11, 6, 15, 13 };
        Rp = new int[] { 5, 14, 7, 0, 9, 2, 11, 4, 13, 6, 15, 8, 1, 10, 3, 12, 6, 11, 3, 7, 0, 13, 5, 10, 14, 15, 8, 12, 4, 9, 1, 2, 15, 5, 1, 3, 7, 14, 6, 9, 11, 8, 12, 2, 10, 0, 4, 13, 8, 6, 4, 1, 3, 11, 15, 0, 5, 12, 2, 13, 9, 7, 10, 14, 12, 15, 10, 4, 1, 5, 8, 7, 6, 2, 13, 14, 0, 3, 9, 11 };
        S = new int[] { 11, 14, 15, 12, 5, 8, 7, 9, 11, 13, 14, 15, 6, 7, 9, 8, 7, 6, 8, 13, 11, 9, 7, 15, 7, 12, 15, 9, 11, 7, 13, 12, 11, 13, 6, 7, 14, 9, 13, 15, 14, 8, 13, 6, 5, 12, 7, 5, 11, 12, 14, 15, 14, 15, 9, 8, 9, 14, 5, 6, 8, 6, 5, 12, 9, 15, 5, 11, 6, 8, 13, 12, 5, 12, 13, 14, 11, 8, 5, 6 };
        Sp = new int[] { 8, 9, 9, 11, 13, 15, 15, 5, 7, 7, 8, 11, 14, 14, 12, 6, 9, 13, 15, 7, 12, 8, 9, 11, 7, 7, 12, 7, 6, 15, 13, 11, 9, 7, 15, 11, 8, 6, 6, 14, 12, 13, 5, 14, 13, 13, 7, 5, 15, 5, 8, 11, 14, 14, 6, 14, 6, 9, 12, 9, 12, 5, 15, 8, 8, 5, 12, 9, 12, 5, 14, 6, 8, 13, 6, 5, 15, 13, 11, 11 };
    }
}
