package cryptix.jce.provider.cipher;

import java.security.InvalidKeyException;
import java.security.Key;

public final class DES extends BlockCipher
{
    private static final int ROUNDS = 16;
    private static final int BLOCK_SIZE = 8;
    private static final int KEY_LENGTH = 8;
    private static final int ALT_KEY_LENGTH = 7;
    private static final int INTERNAL_KEY_LENGTH = 32;
    private static final int[] SKB;
    private static final int[] SP_TRANS;
    private int[] sKey;
    
    protected void coreInit(final Key key, final boolean decrypt) throws InvalidKeyException {
        byte[] userkey = key.getEncoded();
        if (userkey == null) {
            throw new InvalidKeyException("Null user key");
        }
        if (userkey.length == 7) {
            final byte[] temp = userkey = new byte[] { userkey[0], (byte)(userkey[0] << 7 | (userkey[1] >>> 1 & 0x7F)), (byte)(userkey[1] << 6 | (userkey[2] >>> 2 & 0x3F)), (byte)(userkey[2] << 5 | (userkey[3] >>> 3 & 0x1F)), (byte)(userkey[3] << 4 | (userkey[4] >>> 4 & 0xF)), (byte)(userkey[4] << 3 | (userkey[5] >>> 5 & 0x7)), (byte)(userkey[5] << 2 | (userkey[6] >>> 6 & 0x3)), (byte)(userkey[6] << 1) };
        }
        if (userkey.length != 8) {
            throw new InvalidKeyException("Invalid user key length");
        }
        int i = 0;
        int c = (userkey[i++] & 0xFF) | (userkey[i++] & 0xFF) << 8 | (userkey[i++] & 0xFF) << 16 | userkey[i++] << 24;
        int d = (userkey[i++] & 0xFF) | (userkey[i++] & 0xFF) << 8 | (userkey[i++] & 0xFF) << 16 | userkey[i] << 24;
        int t = (d >>> 4 ^ c) & 0xF0F0F0F;
        c ^= t;
        d ^= t << 4;
        t = ((c << 18 ^ c) & 0xCCCC0000);
        c ^= (t ^ t >>> 18);
        t = ((d << 18 ^ d) & 0xCCCC0000);
        d ^= (t ^ t >>> 18);
        t = ((d >>> 1 ^ c) & 0x55555555);
        c ^= t;
        d ^= t << 1;
        t = ((c >>> 8 ^ d) & 0xFF00FF);
        d ^= t;
        c ^= t << 8;
        t = ((d >>> 1 ^ c) & 0x55555555);
        c ^= t;
        d ^= t << 1;
        d = ((d & 0xFF) << 16 | (d & 0xFF00) | (d & 0xFF0000) >>> 16 | (c & 0xF0000000) >>> 4);
        c &= 0xFFFFFFF;
        int j = 0;
        for (i = 0; i < 16; ++i) {
            if ((32508 >> i & 0x1) == 0x1) {
                c = ((c >>> 2 | c << 26) & 0xFFFFFFF);
                d = ((d >>> 2 | d << 26) & 0xFFFFFFF);
            }
            else {
                c = ((c >>> 1 | c << 27) & 0xFFFFFFF);
                d = ((d >>> 1 | d << 27) & 0xFFFFFFF);
            }
            int s = DES.SKB[c & 0x3F] | DES.SKB[0x40 | ((c >>> 6 & 0x3) | (c >>> 7 & 0x3C))] | DES.SKB[0x80 | ((c >>> 13 & 0xF) | (c >>> 14 & 0x30))] | DES.SKB[0xC0 | ((c >>> 20 & 0x1) | (c >>> 21 & 0x6) | (c >>> 22 & 0x38))];
            t = (DES.SKB[0x100 | (d & 0x3F)] | DES.SKB[0x140 | ((d >>> 7 & 0x3) | (d >>> 8 & 0x3C))] | DES.SKB[0x180 | (d >>> 15 & 0x3F)] | DES.SKB[0x1C0 | ((d >>> 21 & 0xF) | (d >>> 22 & 0x30))]);
            this.sKey[j++] = (t << 16 | (s & 0xFFFF));
            s = (s >>> 16 | (t & 0xFFFF0000));
            this.sKey[j++] = (s << 4 | s >>> 28);
        }
        if (decrypt) {
            for (i = 0; i < 16; ++i) {
                j = 30 - i + i % 2 * 2;
                t = this.sKey[i];
                this.sKey[i] = this.sKey[j];
                this.sKey[j] = t;
            }
        }
    }
    
    protected void coreCrypt(final byte[] in, int inOffset, final byte[] out, int outOffset) {
        int L = (in[inOffset++] & 0xFF) | (in[inOffset++] & 0xFF) << 8 | (in[inOffset++] & 0xFF) << 16 | in[inOffset++] << 24;
        int R = (in[inOffset++] & 0xFF) | (in[inOffset++] & 0xFF) << 8 | (in[inOffset++] & 0xFF) << 16 | in[inOffset] << 24;
        int t = (R >>> 4 ^ L) & 0xF0F0F0F;
        L ^= t;
        R ^= t << 4;
        t = ((L >>> 16 ^ R) & 0xFFFF);
        R ^= t;
        L ^= t << 16;
        t = ((R >>> 2 ^ L) & 0x33333333);
        L ^= t;
        R ^= t << 2;
        t = ((L >>> 8 ^ R) & 0xFF00FF);
        R ^= t;
        L ^= t << 8;
        t = ((R >>> 1 ^ L) & 0x55555555);
        L ^= t;
        R ^= t << 1;
        int u = R << 1 | R >>> 31;
        R = (L << 1 | L >>> 31);
        L = u;
        for (int i = 0; i < 32; u = (R ^ this.sKey[i++]), t = (R ^ this.sKey[i++]), t = (t >>> 4 | t << 28), L ^= (DES.SP_TRANS[0x40 | (t & 0x3F)] | DES.SP_TRANS[0xC0 | (t >>> 8 & 0x3F)] | DES.SP_TRANS[0x140 | (t >>> 16 & 0x3F)] | DES.SP_TRANS[0x1C0 | (t >>> 24 & 0x3F)] | DES.SP_TRANS[u & 0x3F] | DES.SP_TRANS[0x80 | (u >>> 8 & 0x3F)] | DES.SP_TRANS[0x100 | (u >>> 16 & 0x3F)] | DES.SP_TRANS[0x180 | (u >>> 24 & 0x3F)]), u = (L ^ this.sKey[i++]), t = (L ^ this.sKey[i++]), t = (t >>> 4 | t << 28), R ^= (DES.SP_TRANS[0x40 | (t & 0x3F)] | DES.SP_TRANS[0xC0 | (t >>> 8 & 0x3F)] | DES.SP_TRANS[0x140 | (t >>> 16 & 0x3F)] | DES.SP_TRANS[0x1C0 | (t >>> 24 & 0x3F)] | DES.SP_TRANS[u & 0x3F] | DES.SP_TRANS[0x80 | (u >>> 8 & 0x3F)] | DES.SP_TRANS[0x100 | (u >>> 16 & 0x3F)] | DES.SP_TRANS[0x180 | (u >>> 24 & 0x3F)])) {}
        R = (R >>> 1 | R << 31);
        L = (L >>> 1 | L << 31);
        t = ((R >>> 1 ^ L) & 0x55555555);
        L ^= t;
        R ^= t << 1;
        t = ((L >>> 8 ^ R) & 0xFF00FF);
        R ^= t;
        L ^= t << 8;
        t = ((R >>> 2 ^ L) & 0x33333333);
        L ^= t;
        R ^= t << 2;
        t = ((L >>> 16 ^ R) & 0xFFFF);
        R ^= t;
        L ^= t << 16;
        t = ((R >>> 4 ^ L) & 0xF0F0F0F);
        L ^= t;
        R ^= t << 4;
        out[outOffset++] = (byte)L;
        out[outOffset++] = (byte)(L >> 8);
        out[outOffset++] = (byte)(L >> 16);
        out[outOffset++] = (byte)(L >> 24);
        out[outOffset++] = (byte)R;
        out[outOffset++] = (byte)(R >> 8);
        out[outOffset++] = (byte)(R >> 16);
        out[outOffset] = (byte)(R >> 24);
    }
    
    public DES() {
        super("DES", 8);
        this.sKey = new int[32];
    }
    
    static {
        SKB = new int[512];
        SP_TRANS = new int[512];
        final String cd = "D]PKESYM`UBJ\\@RXA`I[T`HC`LZQ\\PB]TL`[C`JQ@Y`HSXDUIZRAM`EK";
        int count = 0;
        int offset = 0;
        for (int i = 0; i < cd.length(); ++i) {
            final int s = cd.charAt(i) - '@';
            if (s != 32) {
                final int bit = 1 << count++;
                for (int n = 0; n < 64; ++n) {
                    if ((bit & n) != 0x0) {
                        final int[] skb = DES.SKB;
                        final int n2 = offset + n;
                        skb[n2] |= 1 << s;
                    }
                }
                if (count == 6) {
                    offset += 64;
                    count = 0;
                }
            }
        }
        final String spt = "g3H821:80:H03BA0@N1290BAA88::3112aIH8:8282@0@AH0:1W3A8P810@22;22A18^@9H9@129:<8@822`?:@0@8PH2H81A19:G1@03403A0B1;:0@1g192:@919AA0A109:W21492H@0051919811:215011139883942N8::3112A2:31981jM118::A101@I88:1aN0<@030128:X;811`920:;H0310D1033@W980:8A4@804A3803o1A2021B2:@1AH023GA:8:@81@@12092B:098042P@:0:A0HA9>1;289:@1804:40Ph=1:H0I0HP0408024bC9P8@I808A;@0@0PnH0::8:19J@818:@iF0398:8A9H0<13@001@11<8;@82B01P0a2989B:0AY0912889bD0A1@B1A0A0AB033O91182440A9P8@I80n@1I03@1J828212A`A8:12B1@19A9@9@8^B:0@H00<82AB030bB840821Q:8310A302102::A1::20A1;8";
        offset = 0;
        for (int j = 0; j < 32; ++j) {
            int k = -1;
            final int bit = 1 << j;
            for (int l = 0; l < 32; ++l) {
                final int c = spt.charAt(offset >> 1) - '0' >> (offset & 0x1) * 3 & 0x7;
                ++offset;
                if (c < 5) {
                    k += c + 1;
                    final int[] sp_TRANS = DES.SP_TRANS;
                    final int n3 = k;
                    sp_TRANS[n3] |= bit;
                }
                else {
                    final int param = spt.charAt(offset >> 1) - '0' >> (offset & 0x1) * 3 & 0x7;
                    ++offset;
                    if (c == 5) {
                        k += param + 6;
                        final int[] sp_TRANS2 = DES.SP_TRANS;
                        final int n4 = k;
                        sp_TRANS2[n4] |= bit;
                    }
                    else if (c == 6) {
                        k += (param << 6) + 1;
                        final int[] sp_TRANS3 = DES.SP_TRANS;
                        final int n5 = k;
                        sp_TRANS3[n5] |= bit;
                    }
                    else {
                        k += param << 6;
                        --l;
                    }
                }
            }
        }
    }
}
