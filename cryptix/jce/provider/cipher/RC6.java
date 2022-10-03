package cryptix.jce.provider.cipher;

import java.security.InvalidKeyException;
import java.security.Key;

public final class RC6 extends BlockCipher
{
    private static final int ROUNDS = 20;
    private static final int BLOCK_SIZE = 16;
    private static final int P = -1209970333;
    private static final int Q = -1640531527;
    private int[] S;
    private boolean decrypt;
    
    protected void coreInit(final Key key, final boolean decrypt) throws InvalidKeyException {
        if (key == null) {
            throw new InvalidKeyException("Key missing");
        }
        if (!key.getFormat().equalsIgnoreCase("RAW")) {
            throw new InvalidKeyException("Wrong format: RAW bytes needed");
        }
        final byte[] userkey = key.getEncoded();
        if (userkey == null) {
            throw new InvalidKeyException("RAW bytes missing");
        }
        final int len = userkey.length;
        if (len != 16 && len != 24 && len != 32) {
            throw new InvalidKeyException("Invalid user key length");
        }
        this.generateSubKeys(userkey);
        this.decrypt = decrypt;
    }
    
    protected final void coreCrypt(final byte[] in, int inOffset, final byte[] out, int outOffset) {
        int A = (in[inOffset++] & 0xFF) | (in[inOffset++] & 0xFF) << 8 | (in[inOffset++] & 0xFF) << 16 | in[inOffset++] << 24;
        int B = (in[inOffset++] & 0xFF) | (in[inOffset++] & 0xFF) << 8 | (in[inOffset++] & 0xFF) << 16 | in[inOffset++] << 24;
        int C = (in[inOffset++] & 0xFF) | (in[inOffset++] & 0xFF) << 8 | (in[inOffset++] & 0xFF) << 16 | in[inOffset++] << 24;
        int D = (in[inOffset++] & 0xFF) | (in[inOffset++] & 0xFF) << 8 | (in[inOffset++] & 0xFF) << 16 | in[inOffset] << 24;
        if (this.decrypt) {
            C -= this.S[43];
            A -= this.S[42];
            int t;
            int u;
            for (int i = 42; i > 2; C = (rotr(C - this.S[--i], t) ^ u), A = (rotr(A - this.S[--i], u) ^ t)) {
                t = D;
                D = C;
                C = B;
                B = A;
                A = t;
                u = rotl(D * (2 * D + 1), 5);
                t = rotl(B * (2 * B + 1), 5);
            }
            D -= this.S[1];
            B -= this.S[0];
        }
        else {
            B += this.S[0];
            D += this.S[1];
            int t;
            int u;
            for (int i = 1; i <= 40; A = rotl(A ^ t, u) + this.S[++i], C = rotl(C ^ u, t) + this.S[++i], t = A, A = B, B = C, C = D, D = t) {
                t = rotl(B * (2 * B + 1), 5);
                u = rotl(D * (2 * D + 1), 5);
            }
            A += this.S[42];
            C += this.S[43];
        }
        out[outOffset++] = (byte)A;
        out[outOffset++] = (byte)(A >>> 8);
        out[outOffset++] = (byte)(A >>> 16);
        out[outOffset++] = (byte)(A >>> 24);
        out[outOffset++] = (byte)B;
        out[outOffset++] = (byte)(B >>> 8);
        out[outOffset++] = (byte)(B >>> 16);
        out[outOffset++] = (byte)(B >>> 24);
        out[outOffset++] = (byte)C;
        out[outOffset++] = (byte)(C >>> 8);
        out[outOffset++] = (byte)(C >>> 16);
        out[outOffset++] = (byte)(C >>> 24);
        out[outOffset++] = (byte)D;
        out[outOffset++] = (byte)(D >>> 8);
        out[outOffset++] = (byte)(D >>> 16);
        out[outOffset] = (byte)(D >>> 24);
    }
    
    private final void generateSubKeys(final byte[] key) {
        final int len = key.length;
        final int c = len / 4;
        final int[] L = new int[c];
        int off = 0;
        for (int i = 0; i < c; ++i) {
            L[i] = ((key[off++] & 0xFF) | (key[off++] & 0xFF) << 8 | (key[off++] & 0xFF) << 16 | (key[off++] & 0xFF) << 24);
        }
        this.S[0] = -1209970333;
        for (int j = 1; j <= 43; ++j) {
            this.S[j] = this.S[j - 1] - 1640531527;
        }
        int A = 0;
        int B = 0;
        int k = 0;
        int l = 0;
        for (int v = 132, s = 1; s <= v; ++s) {
            final int[] s2 = this.S;
            final int n = k;
            final int rotl = rotl(this.S[k] + A + B, 3);
            s2[n] = rotl;
            A = rotl;
            final int[] array = L;
            final int n2 = l;
            final int rotl2 = rotl(L[l] + A + B, A + B);
            array[n2] = rotl2;
            B = rotl2;
            k = (k + 1) % 44;
            l = (l + 1) % c;
        }
    }
    
    private static int rotl(final int val, final int amount) {
        return val << amount | val >>> 32 - amount;
    }
    
    private static int rotr(final int val, final int amount) {
        return val >>> amount | val << 32 - amount;
    }
    
    public RC6() {
        super(16);
        this.S = new int[44];
    }
}
