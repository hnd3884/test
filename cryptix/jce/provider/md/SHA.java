package cryptix.jce.provider.md;

abstract class SHA extends PaddingMD
{
    private static final int HASH_SIZE = 20;
    private final int[] context;
    private final int[] buffer;
    
    protected void coreDigest(final byte[] buf, final int off) {
        for (int i = 0; i < this.context.length; ++i) {
            for (int j = 0; j < 4; ++j) {
                buf[off + (i * 4 + (3 - j))] = (byte)(this.context[i] >>> 8 * j);
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
        final int[] W = this.buffer;
        for (int i = 0; i < 16; ++i) {
            W[i] = (block[offset++] << 24 | (block[offset++] & 0xFF) << 16 | (block[offset++] & 0xFF) << 8 | (block[offset++] & 0xFF));
        }
        this.expand(W);
        int A = this.context[0];
        int B = this.context[1];
        int C = this.context[2];
        int D = this.context[3];
        int E = this.context[4];
        E += (A << 5 | A >>> -5) + f1(B, C, D) + W[0];
        B = (B << 30 | B >>> -30);
        D += (E << 5 | E >>> -5) + f1(A, B, C) + W[1];
        A = (A << 30 | A >>> -30);
        C += (D << 5 | D >>> -5) + f1(E, A, B) + W[2];
        E = (E << 30 | E >>> -30);
        B += (C << 5 | C >>> -5) + f1(D, E, A) + W[3];
        D = (D << 30 | D >>> -30);
        A += (B << 5 | B >>> -5) + f1(C, D, E) + W[4];
        C = (C << 30 | C >>> -30);
        E += (A << 5 | A >>> -5) + f1(B, C, D) + W[5];
        B = (B << 30 | B >>> -30);
        D += (E << 5 | E >>> -5) + f1(A, B, C) + W[6];
        A = (A << 30 | A >>> -30);
        C += (D << 5 | D >>> -5) + f1(E, A, B) + W[7];
        E = (E << 30 | E >>> -30);
        B += (C << 5 | C >>> -5) + f1(D, E, A) + W[8];
        D = (D << 30 | D >>> -30);
        A += (B << 5 | B >>> -5) + f1(C, D, E) + W[9];
        C = (C << 30 | C >>> -30);
        E += (A << 5 | A >>> -5) + f1(B, C, D) + W[10];
        B = (B << 30 | B >>> -30);
        D += (E << 5 | E >>> -5) + f1(A, B, C) + W[11];
        A = (A << 30 | A >>> -30);
        C += (D << 5 | D >>> -5) + f1(E, A, B) + W[12];
        E = (E << 30 | E >>> -30);
        B += (C << 5 | C >>> -5) + f1(D, E, A) + W[13];
        D = (D << 30 | D >>> -30);
        A += (B << 5 | B >>> -5) + f1(C, D, E) + W[14];
        C = (C << 30 | C >>> -30);
        E += (A << 5 | A >>> -5) + f1(B, C, D) + W[15];
        B = (B << 30 | B >>> -30);
        D += (E << 5 | E >>> -5) + f1(A, B, C) + W[16];
        A = (A << 30 | A >>> -30);
        C += (D << 5 | D >>> -5) + f1(E, A, B) + W[17];
        E = (E << 30 | E >>> -30);
        B += (C << 5 | C >>> -5) + f1(D, E, A) + W[18];
        D = (D << 30 | D >>> -30);
        A += (B << 5 | B >>> -5) + f1(C, D, E) + W[19];
        C = (C << 30 | C >>> -30);
        E += (A << 5 | A >>> -5) + f2(B, C, D) + W[20];
        B = (B << 30 | B >>> -30);
        D += (E << 5 | E >>> -5) + f2(A, B, C) + W[21];
        A = (A << 30 | A >>> -30);
        C += (D << 5 | D >>> -5) + f2(E, A, B) + W[22];
        E = (E << 30 | E >>> -30);
        B += (C << 5 | C >>> -5) + f2(D, E, A) + W[23];
        D = (D << 30 | D >>> -30);
        A += (B << 5 | B >>> -5) + f2(C, D, E) + W[24];
        C = (C << 30 | C >>> -30);
        E += (A << 5 | A >>> -5) + f2(B, C, D) + W[25];
        B = (B << 30 | B >>> -30);
        D += (E << 5 | E >>> -5) + f2(A, B, C) + W[26];
        A = (A << 30 | A >>> -30);
        C += (D << 5 | D >>> -5) + f2(E, A, B) + W[27];
        E = (E << 30 | E >>> -30);
        B += (C << 5 | C >>> -5) + f2(D, E, A) + W[28];
        D = (D << 30 | D >>> -30);
        A += (B << 5 | B >>> -5) + f2(C, D, E) + W[29];
        C = (C << 30 | C >>> -30);
        E += (A << 5 | A >>> -5) + f2(B, C, D) + W[30];
        B = (B << 30 | B >>> -30);
        D += (E << 5 | E >>> -5) + f2(A, B, C) + W[31];
        A = (A << 30 | A >>> -30);
        C += (D << 5 | D >>> -5) + f2(E, A, B) + W[32];
        E = (E << 30 | E >>> -30);
        B += (C << 5 | C >>> -5) + f2(D, E, A) + W[33];
        D = (D << 30 | D >>> -30);
        A += (B << 5 | B >>> -5) + f2(C, D, E) + W[34];
        C = (C << 30 | C >>> -30);
        E += (A << 5 | A >>> -5) + f2(B, C, D) + W[35];
        B = (B << 30 | B >>> -30);
        D += (E << 5 | E >>> -5) + f2(A, B, C) + W[36];
        A = (A << 30 | A >>> -30);
        C += (D << 5 | D >>> -5) + f2(E, A, B) + W[37];
        E = (E << 30 | E >>> -30);
        B += (C << 5 | C >>> -5) + f2(D, E, A) + W[38];
        D = (D << 30 | D >>> -30);
        A += (B << 5 | B >>> -5) + f2(C, D, E) + W[39];
        C = (C << 30 | C >>> -30);
        E += (A << 5 | A >>> -5) + f3(B, C, D) + W[40];
        B = (B << 30 | B >>> -30);
        D += (E << 5 | E >>> -5) + f3(A, B, C) + W[41];
        A = (A << 30 | A >>> -30);
        C += (D << 5 | D >>> -5) + f3(E, A, B) + W[42];
        E = (E << 30 | E >>> -30);
        B += (C << 5 | C >>> -5) + f3(D, E, A) + W[43];
        D = (D << 30 | D >>> -30);
        A += (B << 5 | B >>> -5) + f3(C, D, E) + W[44];
        C = (C << 30 | C >>> -30);
        E += (A << 5 | A >>> -5) + f3(B, C, D) + W[45];
        B = (B << 30 | B >>> -30);
        D += (E << 5 | E >>> -5) + f3(A, B, C) + W[46];
        A = (A << 30 | A >>> -30);
        C += (D << 5 | D >>> -5) + f3(E, A, B) + W[47];
        E = (E << 30 | E >>> -30);
        B += (C << 5 | C >>> -5) + f3(D, E, A) + W[48];
        D = (D << 30 | D >>> -30);
        A += (B << 5 | B >>> -5) + f3(C, D, E) + W[49];
        C = (C << 30 | C >>> -30);
        E += (A << 5 | A >>> -5) + f3(B, C, D) + W[50];
        B = (B << 30 | B >>> -30);
        D += (E << 5 | E >>> -5) + f3(A, B, C) + W[51];
        A = (A << 30 | A >>> -30);
        C += (D << 5 | D >>> -5) + f3(E, A, B) + W[52];
        E = (E << 30 | E >>> -30);
        B += (C << 5 | C >>> -5) + f3(D, E, A) + W[53];
        D = (D << 30 | D >>> -30);
        A += (B << 5 | B >>> -5) + f3(C, D, E) + W[54];
        C = (C << 30 | C >>> -30);
        E += (A << 5 | A >>> -5) + f3(B, C, D) + W[55];
        B = (B << 30 | B >>> -30);
        D += (E << 5 | E >>> -5) + f3(A, B, C) + W[56];
        A = (A << 30 | A >>> -30);
        C += (D << 5 | D >>> -5) + f3(E, A, B) + W[57];
        E = (E << 30 | E >>> -30);
        B += (C << 5 | C >>> -5) + f3(D, E, A) + W[58];
        D = (D << 30 | D >>> -30);
        A += (B << 5 | B >>> -5) + f3(C, D, E) + W[59];
        C = (C << 30 | C >>> -30);
        E += (A << 5 | A >>> -5) + f4(B, C, D) + W[60];
        B = (B << 30 | B >>> -30);
        D += (E << 5 | E >>> -5) + f4(A, B, C) + W[61];
        A = (A << 30 | A >>> -30);
        C += (D << 5 | D >>> -5) + f4(E, A, B) + W[62];
        E = (E << 30 | E >>> -30);
        B += (C << 5 | C >>> -5) + f4(D, E, A) + W[63];
        D = (D << 30 | D >>> -30);
        A += (B << 5 | B >>> -5) + f4(C, D, E) + W[64];
        C = (C << 30 | C >>> -30);
        E += (A << 5 | A >>> -5) + f4(B, C, D) + W[65];
        B = (B << 30 | B >>> -30);
        D += (E << 5 | E >>> -5) + f4(A, B, C) + W[66];
        A = (A << 30 | A >>> -30);
        C += (D << 5 | D >>> -5) + f4(E, A, B) + W[67];
        E = (E << 30 | E >>> -30);
        B += (C << 5 | C >>> -5) + f4(D, E, A) + W[68];
        D = (D << 30 | D >>> -30);
        A += (B << 5 | B >>> -5) + f4(C, D, E) + W[69];
        C = (C << 30 | C >>> -30);
        E += (A << 5 | A >>> -5) + f4(B, C, D) + W[70];
        B = (B << 30 | B >>> -30);
        D += (E << 5 | E >>> -5) + f4(A, B, C) + W[71];
        A = (A << 30 | A >>> -30);
        C += (D << 5 | D >>> -5) + f4(E, A, B) + W[72];
        E = (E << 30 | E >>> -30);
        B += (C << 5 | C >>> -5) + f4(D, E, A) + W[73];
        D = (D << 30 | D >>> -30);
        A += (B << 5 | B >>> -5) + f4(C, D, E) + W[74];
        C = (C << 30 | C >>> -30);
        E += (A << 5 | A >>> -5) + f4(B, C, D) + W[75];
        B = (B << 30 | B >>> -30);
        D += (E << 5 | E >>> -5) + f4(A, B, C) + W[76];
        A = (A << 30 | A >>> -30);
        C += (D << 5 | D >>> -5) + f4(E, A, B) + W[77];
        E = (E << 30 | E >>> -30);
        B += (C << 5 | C >>> -5) + f4(D, E, A) + W[78];
        D = (D << 30 | D >>> -30);
        A += (B << 5 | B >>> -5) + f4(C, D, E) + W[79];
        C = (C << 30 | C >>> -30);
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
        final int[] context5 = this.context;
        final int n5 = 4;
        context5[n5] += E;
    }
    
    private static int f1(final int a, final int b, final int c) {
        return (c ^ (a & (b ^ c))) + 1518500249;
    }
    
    private static int f2(final int a, final int b, final int c) {
        return (a ^ b ^ c) + 1859775393;
    }
    
    private static int f3(final int a, final int b, final int c) {
        return ((a & b) | (c & (a | b))) - 1894007588;
    }
    
    private static int f4(final int a, final int b, final int c) {
        return (a ^ b ^ c) - 899497514;
    }
    
    protected abstract void expand(final int[] p0);
    
    public SHA() {
        super(20, 1);
        this.context = new int[5];
        this.buffer = new int[80];
        this.coreReset();
    }
    
    protected SHA(final SHA src) {
        super(src);
        this.context = src.context.clone();
        this.buffer = src.buffer.clone();
    }
}
