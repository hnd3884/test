package cryptix.jce.provider.cipher;

import java.security.InvalidKeyException;
import java.security.Key;

public final class Square extends BlockCipher
{
    private static final int BLOCK_SIZE = 16;
    private static final int KEY_LENGTH = 10;
    private static final int R = 8;
    private static final byte[] SE;
    private static final byte[] SD;
    private static final int[] TE;
    private static final int[] TD;
    private static final int ROOT = 501;
    private static final int[] OFFSET;
    private int[][] sKey;
    private boolean decrypt;
    
    protected void coreInit(final Key key, final boolean decrypt) throws InvalidKeyException {
        this.makeKey(key, !decrypt);
        this.decrypt = decrypt;
    }
    
    protected void coreCrypt(final byte[] in, final int inOffset, final byte[] out, final int outOffset) {
        if (this.decrypt) {
            this.square(in, inOffset, out, outOffset, Square.TD, Square.SD);
        }
        else {
            this.square(in, inOffset, out, outOffset, Square.TE, Square.SE);
        }
    }
    
    private void makeKey(final Key key, final boolean doEncrypt) throws InvalidKeyException {
        final byte[] userkey = key.getEncoded();
        if (userkey == null) {
            throw new InvalidKeyException("Null user key");
        }
        if (userkey.length != 16) {
            throw new InvalidKeyException("Invalid user key length");
        }
        int j = 0;
        if (doEncrypt) {
            for (int k = 0; k < 4; ++k) {
                this.sKey[0][k] = ((userkey[j++] & 0xFF) << 24 | (userkey[j++] & 0xFF) << 16 | (userkey[j++] & 0xFF) << 8 | (userkey[j++] & 0xFF));
            }
            for (int i = 1; i < 9; ++i) {
                j = i - 1;
                this.sKey[i][0] = (this.sKey[j][0] ^ rot32L(this.sKey[j][3], 8) ^ Square.OFFSET[j]);
                this.sKey[i][1] = (this.sKey[j][1] ^ this.sKey[i][0]);
                this.sKey[i][2] = (this.sKey[j][2] ^ this.sKey[i][1]);
                this.sKey[i][3] = (this.sKey[j][3] ^ this.sKey[i][2]);
                transform(this.sKey[j], this.sKey[j]);
            }
        }
        else {
            final int[][] tKey = new int[9][4];
            for (int l = 0; l < 4; ++l) {
                tKey[0][l] = ((userkey[j++] & 0xFF) << 24 | (userkey[j++] & 0xFF) << 16 | (userkey[j++] & 0xFF) << 8 | (userkey[j++] & 0xFF));
            }
            for (int i = 1; i < 9; ++i) {
                j = i - 1;
                tKey[i][0] = (tKey[j][0] ^ rot32L(tKey[j][3], 8) ^ Square.OFFSET[j]);
                tKey[i][1] = (tKey[j][1] ^ tKey[i][0]);
                tKey[i][2] = (tKey[j][2] ^ tKey[i][1]);
                tKey[i][3] = (tKey[j][3] ^ tKey[i][2]);
            }
            for (int i = 0; i < 8; ++i) {
                System.arraycopy(tKey[8 - i], 0, this.sKey[i], 0, 4);
            }
            transform(tKey[0], this.sKey[8]);
        }
    }
    
    private static void transform(final int[] in, final int[] out) {
        for (int i = 0; i < 4; ++i) {
            final int l3 = in[i];
            final int l4 = l3 >>> 8;
            final int l5 = l3 >>> 16;
            final int l6 = l3 >>> 24;
            int m = ((mul(l6, 2) ^ mul(l5, 3) ^ l4 ^ l3) & 0xFF) << 24;
            m ^= ((l6 ^ mul(l5, 2) ^ mul(l4, 3) ^ l3) & 0xFF) << 16;
            m ^= ((l6 ^ l5 ^ mul(l4, 2) ^ mul(l3, 3)) & 0xFF) << 8;
            m ^= ((mul(l6, 3) ^ l5 ^ l4 ^ mul(l3, 2)) & 0xFF);
            out[i] = m;
        }
    }
    
    private static int rot32L(final int x, final int s) {
        return x << s | x >>> 32 - s;
    }
    
    private static int rot32R(final int x, final int s) {
        return x >>> s | x << 32 - s;
    }
    
    private static final int mul(int a, int b) {
        if (a == 0) {
            return 0;
        }
        a &= 0xFF;
        b &= 0xFF;
        int p = 0;
        while (b != 0) {
            if ((b & 0x1) != 0x0) {
                p ^= a;
            }
            a <<= 1;
            if (a > 255) {
                a ^= 0x1F5;
            }
            b >>>= 1;
        }
        return p & 0xFF;
    }
    
    private void square(final byte[] in, int off, final byte[] out, int outOff, final int[] T, final byte[] S) {
        int a = (in[off++] & 0xFF) << 24 | (in[off++] & 0xFF) << 16 | (in[off++] & 0xFF) << 8 | (in[off++] & 0xFF);
        int b = (in[off++] & 0xFF) << 24 | (in[off++] & 0xFF) << 16 | (in[off++] & 0xFF) << 8 | (in[off++] & 0xFF);
        int c = (in[off++] & 0xFF) << 24 | (in[off++] & 0xFF) << 16 | (in[off++] & 0xFF) << 8 | (in[off++] & 0xFF);
        int d = (in[off++] & 0xFF) << 24 | (in[off++] & 0xFF) << 16 | (in[off++] & 0xFF) << 8 | (in[off++] & 0xFF);
        a ^= this.sKey[0][0];
        b ^= this.sKey[0][1];
        c ^= this.sKey[0][2];
        d ^= this.sKey[0][3];
        for (int l = 1; l < 8; ++l) {
            final int aa = T[a >>> 24 & 0xFF] ^ rot32R(T[b >>> 24 & 0xFF], 8) ^ rot32R(T[c >>> 24 & 0xFF], 16) ^ rot32R(T[d >>> 24 & 0xFF], 24) ^ this.sKey[l][0];
            final int bb = T[a >>> 16 & 0xFF] ^ rot32R(T[b >>> 16 & 0xFF], 8) ^ rot32R(T[c >>> 16 & 0xFF], 16) ^ rot32R(T[d >>> 16 & 0xFF], 24) ^ this.sKey[l][1];
            final int cc = T[a >>> 8 & 0xFF] ^ rot32R(T[b >>> 8 & 0xFF], 8) ^ rot32R(T[c >>> 8 & 0xFF], 16) ^ rot32R(T[d >>> 8 & 0xFF], 24) ^ this.sKey[l][2];
            final int dd = T[a & 0xFF] ^ rot32R(T[b & 0xFF], 8) ^ rot32R(T[c & 0xFF], 16) ^ rot32R(T[d & 0xFF], 24) ^ this.sKey[l][3];
            a = aa;
            b = bb;
            c = cc;
            d = dd;
        }
        for (int i = 0, j = 24; i < 4; ++i, j -= 8) {
            int k = (S[a >>> j & 0xFF] & 0xFF) << 24 | (S[b >>> j & 0xFF] & 0xFF) << 16 | (S[c >>> j & 0xFF] & 0xFF) << 8 | (S[d >>> j & 0xFF] & 0xFF);
            k ^= this.sKey[8][i];
            out[outOff++] = (byte)(k >>> 24 & 0xFF);
            out[outOff++] = (byte)(k >>> 16 & 0xFF);
            out[outOff++] = (byte)(k >>> 8 & 0xFF);
            out[outOff++] = (byte)(k & 0xFF);
        }
    }
    
    public Square() {
        super(16);
        this.sKey = new int[9][4];
    }
    
    static {
        SE = new byte[256];
        SD = new byte[256];
        TE = new int[256];
        TD = new int[256];
        OFFSET = new int[8];
        final byte[] exp = new byte[256];
        final byte[] log = new byte[256];
        exp[0] = 1;
        for (int k = 1; k < 256; ++k) {
            int j = exp[k - 1] << 1;
            if ((j & 0x100) != 0x0) {
                j ^= 0x1F5;
            }
            exp[k] = (byte)j;
            log[j & 0xFF] = (byte)k;
        }
        Square.SE[0] = 0;
        Square.SE[1] = 1;
        for (int i = 2; i < 256; ++i) {
            Square.SE[i] = exp[255 - log[i] & 0xFF];
        }
        final int[] trans = { 1, 3, 5, 15, 31, 61, 123, 214 };
        for (int i = 0; i < 256; ++i) {
            int v = 177;
            for (int l = 0; l < 8; ++l) {
                int u = Square.SE[i] & trans[l] & 0xFF;
                u ^= u >>> 4;
                u ^= u >>> 2;
                u ^= u >>> 1;
                u &= 0x1;
                v ^= u << l;
            }
            Square.SE[i] = (byte)v;
            Square.SD[v] = (byte)i;
        }
        Square.OFFSET[0] = 1;
        for (int i = 1; i < 8; ++i) {
            Square.OFFSET[i] = mul(Square.OFFSET[i - 1], 2);
            final int[] offset = Square.OFFSET;
            final int n = i - 1;
            offset[n] <<= 24;
        }
        final int[] offset2 = Square.OFFSET;
        final int n2 = 7;
        offset2[n2] <<= 24;
        for (int i = 0; i < 256; ++i) {
            final int se = Square.SE[i] & 0xFF;
            final int sd = Square.SD[i] & 0xFF;
            Square.TE[i] = ((Square.SE[i & 0x3] == 0) ? 0 : (mul(se, 2) << 24 | se << 16 | se << 8 | mul(se, 3)));
            Square.TD[i] = ((Square.SD[i & 0x3] == 0) ? 0 : (mul(sd, 14) << 24 | mul(sd, 9) << 16 | mul(sd, 13) << 8 | mul(sd, 11)));
        }
    }
}
