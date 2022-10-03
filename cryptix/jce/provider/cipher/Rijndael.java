package cryptix.jce.provider.cipher;

import java.security.InvalidKeyException;
import java.security.Key;

public final class Rijndael extends BlockCipher
{
    private static final int BLOCK_SIZE = 16;
    private static final String SS = "\u637c\u777b\uf26b\u6fc5\u3001\u672b\ufed7\uab76\uca82\uc97d\ufa59\u47f0\uadd4\ua2af\u9ca4\u72c0\ub7fd\u9326\u363f\uf7cc\u34a5\ue5f1\u71d8\u3115\u04c7\u23c3\u1896\u059a\u0712\u80e2\ueb27\ub275\u0983\u2c1a\u1b6e\u5aa0\u523b\ud6b3\u29e3\u2f84\u53d1\u00ed\u20fc\ub15b\u6acb\ube39\u4a4c\u58cf\ud0ef\uaafb\u434d\u3385\u45f9\u027f\u503c\u9fa8\u51a3\u408f\u929d\u38f5\ubcb6\uda21\u10ff\uf3d2\ucd0c\u13ec\u5f97\u4417\uc4a7\u7e3d\u645d\u1973\u6081\u4fdc\u222a\u9088\u46ee\ub814\ude5e\u0bdb\ue032\u3a0a\u4906\u245c\uc2d3\uac62\u9195\ue479\ue7c8\u376d\u8dd5\u4ea9\u6c56\uf4ea\u657a\uae08\uba78\u252e\u1ca6\ub4c6\ue8dd\u741f\u4bbd\u8b8a\u703e\ub566\u4803\uf60e\u6135\u57b9\u86c1\u1d9e\ue1f8\u9811\u69d9\u8e94\u9b1e\u87e9\uce55\u28df\u8ca1\u890d\ubfe6\u4268\u4199\u2d0f\ub054\ubb16";
    private static final byte[] S;
    private static final byte[] Si;
    private static final int[] T1;
    private static final int[] T2;
    private static final int[] T3;
    private static final int[] T4;
    private static final int[] T5;
    private static final int[] T6;
    private static final int[] T7;
    private static final int[] T8;
    private static final int[] U1;
    private static final int[] U2;
    private static final int[] U3;
    private static final int[] U4;
    private static final byte[] rcon;
    private boolean ROUNDS_12;
    private boolean ROUNDS_14;
    private boolean decrypt;
    private int[] K;
    private int limit;
    
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
        this.decrypt = decrypt;
        this.K = makeKey(userkey, decrypt);
        if (decrypt) {
            invertKey(this.K);
        }
        this.ROUNDS_12 = (len >= 24);
        this.ROUNDS_14 = (len == 32);
        this.limit = getRounds(len) * 4;
    }
    
    protected void coreCrypt(final byte[] in, final int inOffset, final byte[] out, final int outOffset) {
        if (this.decrypt) {
            this.blockDecrypt(in, inOffset, out, outOffset);
        }
        else {
            this.blockEncrypt(in, inOffset, out, outOffset);
        }
    }
    
    private void blockEncrypt(final byte[] in, int inOffset, final byte[] out, int outOffset) {
        int keyOffset;
        int t0;
        int t2;
        int t3;
        int t4;
        int a0;
        int a2;
        int a3;
        for (keyOffset = 0, t0 = ((in[inOffset++] << 24 | (in[inOffset++] & 0xFF) << 16 | (in[inOffset++] & 0xFF) << 8 | (in[inOffset++] & 0xFF)) ^ this.K[keyOffset++]), t2 = ((in[inOffset++] << 24 | (in[inOffset++] & 0xFF) << 16 | (in[inOffset++] & 0xFF) << 8 | (in[inOffset++] & 0xFF)) ^ this.K[keyOffset++]), t3 = ((in[inOffset++] << 24 | (in[inOffset++] & 0xFF) << 16 | (in[inOffset++] & 0xFF) << 8 | (in[inOffset++] & 0xFF)) ^ this.K[keyOffset++]), t4 = ((in[inOffset++] << 24 | (in[inOffset++] & 0xFF) << 16 | (in[inOffset++] & 0xFF) << 8 | (in[inOffset++] & 0xFF)) ^ this.K[keyOffset++]); keyOffset < this.limit; a0 = (Rijndael.T1[t0 >>> 24] ^ Rijndael.T2[t2 >>> 16 & 0xFF] ^ Rijndael.T3[t3 >>> 8 & 0xFF] ^ Rijndael.T4[t4 & 0xFF] ^ this.K[keyOffset++]), a2 = (Rijndael.T1[t2 >>> 24] ^ Rijndael.T2[t3 >>> 16 & 0xFF] ^ Rijndael.T3[t4 >>> 8 & 0xFF] ^ Rijndael.T4[t0 & 0xFF] ^ this.K[keyOffset++]), a3 = (Rijndael.T1[t3 >>> 24] ^ Rijndael.T2[t4 >>> 16 & 0xFF] ^ Rijndael.T3[t0 >>> 8 & 0xFF] ^ Rijndael.T4[t2 & 0xFF] ^ this.K[keyOffset++]), t4 = (Rijndael.T1[t4 >>> 24] ^ Rijndael.T2[t0 >>> 16 & 0xFF] ^ Rijndael.T3[t2 >>> 8 & 0xFF] ^ Rijndael.T4[t3 & 0xFF] ^ this.K[keyOffset++]), t0 = a0, t2 = a2, t3 = a3) {}
        int tt = this.K[keyOffset++];
        out[outOffset++] = (byte)(Rijndael.S[t0 >>> 24] ^ tt >>> 24);
        out[outOffset++] = (byte)(Rijndael.S[t2 >>> 16 & 0xFF] ^ tt >>> 16);
        out[outOffset++] = (byte)(Rijndael.S[t3 >>> 8 & 0xFF] ^ tt >>> 8);
        out[outOffset++] = (byte)(Rijndael.S[t4 & 0xFF] ^ tt);
        tt = this.K[keyOffset++];
        out[outOffset++] = (byte)(Rijndael.S[t2 >>> 24] ^ tt >>> 24);
        out[outOffset++] = (byte)(Rijndael.S[t3 >>> 16 & 0xFF] ^ tt >>> 16);
        out[outOffset++] = (byte)(Rijndael.S[t4 >>> 8 & 0xFF] ^ tt >>> 8);
        out[outOffset++] = (byte)(Rijndael.S[t0 & 0xFF] ^ tt);
        tt = this.K[keyOffset++];
        out[outOffset++] = (byte)(Rijndael.S[t3 >>> 24] ^ tt >>> 24);
        out[outOffset++] = (byte)(Rijndael.S[t4 >>> 16 & 0xFF] ^ tt >>> 16);
        out[outOffset++] = (byte)(Rijndael.S[t0 >>> 8 & 0xFF] ^ tt >>> 8);
        out[outOffset++] = (byte)(Rijndael.S[t2 & 0xFF] ^ tt);
        tt = this.K[keyOffset++];
        out[outOffset++] = (byte)(Rijndael.S[t4 >>> 24] ^ tt >>> 24);
        out[outOffset++] = (byte)(Rijndael.S[t0 >>> 16 & 0xFF] ^ tt >>> 16);
        out[outOffset++] = (byte)(Rijndael.S[t2 >>> 8 & 0xFF] ^ tt >>> 8);
        out[outOffset] = (byte)(Rijndael.S[t3 & 0xFF] ^ tt);
    }
    
    private void blockDecrypt(final byte[] in, int inOffset, final byte[] out, int outOffset) {
        int keyOffset = 8;
        int t0 = (in[inOffset++] << 24 | (in[inOffset++] & 0xFF) << 16 | (in[inOffset++] & 0xFF) << 8 | (in[inOffset++] & 0xFF)) ^ this.K[4];
        int t2 = (in[inOffset++] << 24 | (in[inOffset++] & 0xFF) << 16 | (in[inOffset++] & 0xFF) << 8 | (in[inOffset++] & 0xFF)) ^ this.K[5];
        int t3 = (in[inOffset++] << 24 | (in[inOffset++] & 0xFF) << 16 | (in[inOffset++] & 0xFF) << 8 | (in[inOffset++] & 0xFF)) ^ this.K[6];
        int t4 = (in[inOffset++] << 24 | (in[inOffset++] & 0xFF) << 16 | (in[inOffset++] & 0xFF) << 8 | (in[inOffset] & 0xFF)) ^ this.K[7];
        if (this.ROUNDS_12) {
            int a0 = Rijndael.T5[t0 >>> 24] ^ Rijndael.T6[t4 >>> 16 & 0xFF] ^ Rijndael.T7[t3 >>> 8 & 0xFF] ^ Rijndael.T8[t2 & 0xFF] ^ this.K[keyOffset++];
            int a2 = Rijndael.T5[t2 >>> 24] ^ Rijndael.T6[t0 >>> 16 & 0xFF] ^ Rijndael.T7[t4 >>> 8 & 0xFF] ^ Rijndael.T8[t3 & 0xFF] ^ this.K[keyOffset++];
            int a3 = Rijndael.T5[t3 >>> 24] ^ Rijndael.T6[t2 >>> 16 & 0xFF] ^ Rijndael.T7[t0 >>> 8 & 0xFF] ^ Rijndael.T8[t4 & 0xFF] ^ this.K[keyOffset++];
            t4 = (Rijndael.T5[t4 >>> 24] ^ Rijndael.T6[t3 >>> 16 & 0xFF] ^ Rijndael.T7[t2 >>> 8 & 0xFF] ^ Rijndael.T8[t0 & 0xFF] ^ this.K[keyOffset++]);
            t0 = (Rijndael.T5[a0 >>> 24] ^ Rijndael.T6[t4 >>> 16 & 0xFF] ^ Rijndael.T7[a3 >>> 8 & 0xFF] ^ Rijndael.T8[a2 & 0xFF] ^ this.K[keyOffset++]);
            t2 = (Rijndael.T5[a2 >>> 24] ^ Rijndael.T6[a0 >>> 16 & 0xFF] ^ Rijndael.T7[t4 >>> 8 & 0xFF] ^ Rijndael.T8[a3 & 0xFF] ^ this.K[keyOffset++]);
            t3 = (Rijndael.T5[a3 >>> 24] ^ Rijndael.T6[a2 >>> 16 & 0xFF] ^ Rijndael.T7[a0 >>> 8 & 0xFF] ^ Rijndael.T8[t4 & 0xFF] ^ this.K[keyOffset++]);
            t4 = (Rijndael.T5[t4 >>> 24] ^ Rijndael.T6[a3 >>> 16 & 0xFF] ^ Rijndael.T7[a2 >>> 8 & 0xFF] ^ Rijndael.T8[a0 & 0xFF] ^ this.K[keyOffset++]);
            if (this.ROUNDS_14) {
                a0 = (Rijndael.T5[t0 >>> 24] ^ Rijndael.T6[t4 >>> 16 & 0xFF] ^ Rijndael.T7[t3 >>> 8 & 0xFF] ^ Rijndael.T8[t2 & 0xFF] ^ this.K[keyOffset++]);
                a2 = (Rijndael.T5[t2 >>> 24] ^ Rijndael.T6[t0 >>> 16 & 0xFF] ^ Rijndael.T7[t4 >>> 8 & 0xFF] ^ Rijndael.T8[t3 & 0xFF] ^ this.K[keyOffset++]);
                a3 = (Rijndael.T5[t3 >>> 24] ^ Rijndael.T6[t2 >>> 16 & 0xFF] ^ Rijndael.T7[t0 >>> 8 & 0xFF] ^ Rijndael.T8[t4 & 0xFF] ^ this.K[keyOffset++]);
                t4 = (Rijndael.T5[t4 >>> 24] ^ Rijndael.T6[t3 >>> 16 & 0xFF] ^ Rijndael.T7[t2 >>> 8 & 0xFF] ^ Rijndael.T8[t0 & 0xFF] ^ this.K[keyOffset++]);
                t0 = (Rijndael.T5[a0 >>> 24] ^ Rijndael.T6[t4 >>> 16 & 0xFF] ^ Rijndael.T7[a3 >>> 8 & 0xFF] ^ Rijndael.T8[a2 & 0xFF] ^ this.K[keyOffset++]);
                t2 = (Rijndael.T5[a2 >>> 24] ^ Rijndael.T6[a0 >>> 16 & 0xFF] ^ Rijndael.T7[t4 >>> 8 & 0xFF] ^ Rijndael.T8[a3 & 0xFF] ^ this.K[keyOffset++]);
                t3 = (Rijndael.T5[a3 >>> 24] ^ Rijndael.T6[a2 >>> 16 & 0xFF] ^ Rijndael.T7[a0 >>> 8 & 0xFF] ^ Rijndael.T8[t4 & 0xFF] ^ this.K[keyOffset++]);
                t4 = (Rijndael.T5[t4 >>> 24] ^ Rijndael.T6[a3 >>> 16 & 0xFF] ^ Rijndael.T7[a2 >>> 8 & 0xFF] ^ Rijndael.T8[a0 & 0xFF] ^ this.K[keyOffset++]);
            }
        }
        int a0 = Rijndael.T5[t0 >>> 24] ^ Rijndael.T6[t4 >>> 16 & 0xFF] ^ Rijndael.T7[t3 >>> 8 & 0xFF] ^ Rijndael.T8[t2 & 0xFF] ^ this.K[keyOffset++];
        int a2 = Rijndael.T5[t2 >>> 24] ^ Rijndael.T6[t0 >>> 16 & 0xFF] ^ Rijndael.T7[t4 >>> 8 & 0xFF] ^ Rijndael.T8[t3 & 0xFF] ^ this.K[keyOffset++];
        int a3 = Rijndael.T5[t3 >>> 24] ^ Rijndael.T6[t2 >>> 16 & 0xFF] ^ Rijndael.T7[t0 >>> 8 & 0xFF] ^ Rijndael.T8[t4 & 0xFF] ^ this.K[keyOffset++];
        t4 = (Rijndael.T5[t4 >>> 24] ^ Rijndael.T6[t3 >>> 16 & 0xFF] ^ Rijndael.T7[t2 >>> 8 & 0xFF] ^ Rijndael.T8[t0 & 0xFF] ^ this.K[keyOffset++]);
        t0 = (Rijndael.T5[a0 >>> 24] ^ Rijndael.T6[t4 >>> 16 & 0xFF] ^ Rijndael.T7[a3 >>> 8 & 0xFF] ^ Rijndael.T8[a2 & 0xFF] ^ this.K[keyOffset++]);
        t2 = (Rijndael.T5[a2 >>> 24] ^ Rijndael.T6[a0 >>> 16 & 0xFF] ^ Rijndael.T7[t4 >>> 8 & 0xFF] ^ Rijndael.T8[a3 & 0xFF] ^ this.K[keyOffset++]);
        t3 = (Rijndael.T5[a3 >>> 24] ^ Rijndael.T6[a2 >>> 16 & 0xFF] ^ Rijndael.T7[a0 >>> 8 & 0xFF] ^ Rijndael.T8[t4 & 0xFF] ^ this.K[keyOffset++]);
        t4 = (Rijndael.T5[t4 >>> 24] ^ Rijndael.T6[a3 >>> 16 & 0xFF] ^ Rijndael.T7[a2 >>> 8 & 0xFF] ^ Rijndael.T8[a0 & 0xFF] ^ this.K[keyOffset++]);
        a0 = (Rijndael.T5[t0 >>> 24] ^ Rijndael.T6[t4 >>> 16 & 0xFF] ^ Rijndael.T7[t3 >>> 8 & 0xFF] ^ Rijndael.T8[t2 & 0xFF] ^ this.K[keyOffset++]);
        a2 = (Rijndael.T5[t2 >>> 24] ^ Rijndael.T6[t0 >>> 16 & 0xFF] ^ Rijndael.T7[t4 >>> 8 & 0xFF] ^ Rijndael.T8[t3 & 0xFF] ^ this.K[keyOffset++]);
        a3 = (Rijndael.T5[t3 >>> 24] ^ Rijndael.T6[t2 >>> 16 & 0xFF] ^ Rijndael.T7[t0 >>> 8 & 0xFF] ^ Rijndael.T8[t4 & 0xFF] ^ this.K[keyOffset++]);
        t4 = (Rijndael.T5[t4 >>> 24] ^ Rijndael.T6[t3 >>> 16 & 0xFF] ^ Rijndael.T7[t2 >>> 8 & 0xFF] ^ Rijndael.T8[t0 & 0xFF] ^ this.K[keyOffset++]);
        t0 = (Rijndael.T5[a0 >>> 24] ^ Rijndael.T6[t4 >>> 16 & 0xFF] ^ Rijndael.T7[a3 >>> 8 & 0xFF] ^ Rijndael.T8[a2 & 0xFF] ^ this.K[keyOffset++]);
        t2 = (Rijndael.T5[a2 >>> 24] ^ Rijndael.T6[a0 >>> 16 & 0xFF] ^ Rijndael.T7[t4 >>> 8 & 0xFF] ^ Rijndael.T8[a3 & 0xFF] ^ this.K[keyOffset++]);
        t3 = (Rijndael.T5[a3 >>> 24] ^ Rijndael.T6[a2 >>> 16 & 0xFF] ^ Rijndael.T7[a0 >>> 8 & 0xFF] ^ Rijndael.T8[t4 & 0xFF] ^ this.K[keyOffset++]);
        t4 = (Rijndael.T5[t4 >>> 24] ^ Rijndael.T6[a3 >>> 16 & 0xFF] ^ Rijndael.T7[a2 >>> 8 & 0xFF] ^ Rijndael.T8[a0 & 0xFF] ^ this.K[keyOffset++]);
        a0 = (Rijndael.T5[t0 >>> 24] ^ Rijndael.T6[t4 >>> 16 & 0xFF] ^ Rijndael.T7[t3 >>> 8 & 0xFF] ^ Rijndael.T8[t2 & 0xFF] ^ this.K[keyOffset++]);
        a2 = (Rijndael.T5[t2 >>> 24] ^ Rijndael.T6[t0 >>> 16 & 0xFF] ^ Rijndael.T7[t4 >>> 8 & 0xFF] ^ Rijndael.T8[t3 & 0xFF] ^ this.K[keyOffset++]);
        a3 = (Rijndael.T5[t3 >>> 24] ^ Rijndael.T6[t2 >>> 16 & 0xFF] ^ Rijndael.T7[t0 >>> 8 & 0xFF] ^ Rijndael.T8[t4 & 0xFF] ^ this.K[keyOffset++]);
        t4 = (Rijndael.T5[t4 >>> 24] ^ Rijndael.T6[t3 >>> 16 & 0xFF] ^ Rijndael.T7[t2 >>> 8 & 0xFF] ^ Rijndael.T8[t0 & 0xFF] ^ this.K[keyOffset++]);
        t0 = (Rijndael.T5[a0 >>> 24] ^ Rijndael.T6[t4 >>> 16 & 0xFF] ^ Rijndael.T7[a3 >>> 8 & 0xFF] ^ Rijndael.T8[a2 & 0xFF] ^ this.K[keyOffset++]);
        t2 = (Rijndael.T5[a2 >>> 24] ^ Rijndael.T6[a0 >>> 16 & 0xFF] ^ Rijndael.T7[t4 >>> 8 & 0xFF] ^ Rijndael.T8[a3 & 0xFF] ^ this.K[keyOffset++]);
        t3 = (Rijndael.T5[a3 >>> 24] ^ Rijndael.T6[a2 >>> 16 & 0xFF] ^ Rijndael.T7[a0 >>> 8 & 0xFF] ^ Rijndael.T8[t4 & 0xFF] ^ this.K[keyOffset++]);
        t4 = (Rijndael.T5[t4 >>> 24] ^ Rijndael.T6[a3 >>> 16 & 0xFF] ^ Rijndael.T7[a2 >>> 8 & 0xFF] ^ Rijndael.T8[a0 & 0xFF] ^ this.K[keyOffset++]);
        a0 = (Rijndael.T5[t0 >>> 24] ^ Rijndael.T6[t4 >>> 16 & 0xFF] ^ Rijndael.T7[t3 >>> 8 & 0xFF] ^ Rijndael.T8[t2 & 0xFF] ^ this.K[keyOffset++]);
        a2 = (Rijndael.T5[t2 >>> 24] ^ Rijndael.T6[t0 >>> 16 & 0xFF] ^ Rijndael.T7[t4 >>> 8 & 0xFF] ^ Rijndael.T8[t3 & 0xFF] ^ this.K[keyOffset++]);
        a3 = (Rijndael.T5[t3 >>> 24] ^ Rijndael.T6[t2 >>> 16 & 0xFF] ^ Rijndael.T7[t0 >>> 8 & 0xFF] ^ Rijndael.T8[t4 & 0xFF] ^ this.K[keyOffset++]);
        t4 = (Rijndael.T5[t4 >>> 24] ^ Rijndael.T6[t3 >>> 16 & 0xFF] ^ Rijndael.T7[t2 >>> 8 & 0xFF] ^ Rijndael.T8[t0 & 0xFF] ^ this.K[keyOffset++]);
        t0 = (Rijndael.T5[a0 >>> 24] ^ Rijndael.T6[t4 >>> 16 & 0xFF] ^ Rijndael.T7[a3 >>> 8 & 0xFF] ^ Rijndael.T8[a2 & 0xFF] ^ this.K[keyOffset++]);
        t2 = (Rijndael.T5[a2 >>> 24] ^ Rijndael.T6[a0 >>> 16 & 0xFF] ^ Rijndael.T7[t4 >>> 8 & 0xFF] ^ Rijndael.T8[a3 & 0xFF] ^ this.K[keyOffset++]);
        t3 = (Rijndael.T5[a3 >>> 24] ^ Rijndael.T6[a2 >>> 16 & 0xFF] ^ Rijndael.T7[a0 >>> 8 & 0xFF] ^ Rijndael.T8[t4 & 0xFF] ^ this.K[keyOffset++]);
        t4 = (Rijndael.T5[t4 >>> 24] ^ Rijndael.T6[a3 >>> 16 & 0xFF] ^ Rijndael.T7[a2 >>> 8 & 0xFF] ^ Rijndael.T8[a0 & 0xFF] ^ this.K[keyOffset++]);
        a0 = (Rijndael.T5[t0 >>> 24] ^ Rijndael.T6[t4 >>> 16 & 0xFF] ^ Rijndael.T7[t3 >>> 8 & 0xFF] ^ Rijndael.T8[t2 & 0xFF] ^ this.K[keyOffset++]);
        a2 = (Rijndael.T5[t2 >>> 24] ^ Rijndael.T6[t0 >>> 16 & 0xFF] ^ Rijndael.T7[t4 >>> 8 & 0xFF] ^ Rijndael.T8[t3 & 0xFF] ^ this.K[keyOffset++]);
        a3 = (Rijndael.T5[t3 >>> 24] ^ Rijndael.T6[t2 >>> 16 & 0xFF] ^ Rijndael.T7[t0 >>> 8 & 0xFF] ^ Rijndael.T8[t4 & 0xFF] ^ this.K[keyOffset++]);
        t4 = (Rijndael.T5[t4 >>> 24] ^ Rijndael.T6[t3 >>> 16 & 0xFF] ^ Rijndael.T7[t2 >>> 8 & 0xFF] ^ Rijndael.T8[t0 & 0xFF] ^ this.K[keyOffset++]);
        t2 = this.K[0];
        out[outOffset++] = (byte)(Rijndael.Si[a0 >>> 24] ^ t2 >>> 24);
        out[outOffset++] = (byte)(Rijndael.Si[t4 >>> 16 & 0xFF] ^ t2 >>> 16);
        out[outOffset++] = (byte)(Rijndael.Si[a3 >>> 8 & 0xFF] ^ t2 >>> 8);
        out[outOffset++] = (byte)(Rijndael.Si[a2 & 0xFF] ^ t2);
        t2 = this.K[1];
        out[outOffset++] = (byte)(Rijndael.Si[a2 >>> 24] ^ t2 >>> 24);
        out[outOffset++] = (byte)(Rijndael.Si[a0 >>> 16 & 0xFF] ^ t2 >>> 16);
        out[outOffset++] = (byte)(Rijndael.Si[t4 >>> 8 & 0xFF] ^ t2 >>> 8);
        out[outOffset++] = (byte)(Rijndael.Si[a3 & 0xFF] ^ t2);
        t2 = this.K[2];
        out[outOffset++] = (byte)(Rijndael.Si[a3 >>> 24] ^ t2 >>> 24);
        out[outOffset++] = (byte)(Rijndael.Si[a2 >>> 16 & 0xFF] ^ t2 >>> 16);
        out[outOffset++] = (byte)(Rijndael.Si[a0 >>> 8 & 0xFF] ^ t2 >>> 8);
        out[outOffset++] = (byte)(Rijndael.Si[t4 & 0xFF] ^ t2);
        t2 = this.K[3];
        out[outOffset++] = (byte)(Rijndael.Si[t4 >>> 24] ^ t2 >>> 24);
        out[outOffset++] = (byte)(Rijndael.Si[a3 >>> 16 & 0xFF] ^ t2 >>> 16);
        out[outOffset++] = (byte)(Rijndael.Si[a2 >>> 8 & 0xFF] ^ t2 >>> 8);
        out[outOffset] = (byte)(Rijndael.Si[a0 & 0xFF] ^ t2);
    }
    
    private static int[] makeKey(final byte[] keyBytes, final boolean decrypt) throws InvalidKeyException {
        final int ROUNDS = getRounds(keyBytes.length);
        final int ROUND_KEY_COUNT = (ROUNDS + 1) * 4;
        final int[] K = new int[ROUND_KEY_COUNT];
        final int KC = keyBytes.length / 4;
        final int[] tk = new int[KC];
        for (int k = 0, n = 0; k < KC; tk[k++] = (keyBytes[n++] << 24 | (keyBytes[n++] & 0xFF) << 16 | (keyBytes[n++] & 0xFF) << 8 | (keyBytes[n++] & 0xFF))) {}
        int t;
        for (t = 0; t < KC; ++t) {
            K[t] = tk[t];
        }
        int rconpointer = 0;
        while (t < ROUND_KEY_COUNT) {
            int tt = tk[KC - 1];
            final int[] array = tk;
            final int n2 = 0;
            array[n2] ^= (Rijndael.S[tt >>> 16 & 0xFF] << 24 ^ (Rijndael.S[tt >>> 8 & 0xFF] & 0xFF) << 16 ^ (Rijndael.S[tt & 0xFF] & 0xFF) << 8 ^ (Rijndael.S[tt >>> 24] & 0xFF) ^ Rijndael.rcon[rconpointer++] << 24);
            if (KC != 8) {
                int[] array2;
                int n3;
                for (int i = 1, j = 0; i < KC; n3 = i++, array2[n3] ^= tk[j++]) {
                    array2 = tk;
                }
            }
            else {
                int[] array3;
                int n4;
                for (int i = 1, j = 0; i < KC / 2; n4 = i++, array3[n4] ^= tk[j++]) {
                    array3 = tk;
                }
                tt = tk[KC / 2 - 1];
                final int[] array4 = tk;
                final int n5 = KC / 2;
                array4[n5] ^= ((Rijndael.S[tt & 0xFF] & 0xFF) ^ (Rijndael.S[tt >>> 8 & 0xFF] & 0xFF) << 8 ^ (Rijndael.S[tt >>> 16 & 0xFF] & 0xFF) << 16 ^ Rijndael.S[tt >>> 24] << 24);
                int[] array5;
                int n6;
                for (int j = KC / 2, i = j + 1; i < KC; n6 = i++, array5[n6] ^= tk[j++]) {
                    array5 = tk;
                }
            }
            for (int j = 0; j < KC && t < ROUND_KEY_COUNT; ++j, ++t) {
                K[t] = tk[j];
            }
        }
        return K;
    }
    
    private static void invertKey(final int[] K) {
        for (int i = 0; i < K.length / 2 - 4; i += 4) {
            final int jj0 = K[i];
            final int jj2 = K[i + 1];
            final int jj3 = K[i + 2];
            final int jj4 = K[i + 3];
            K[i] = K[K.length - i - 4];
            K[i + 1] = K[K.length - i - 4 + 1];
            K[i + 2] = K[K.length - i - 4 + 2];
            K[i + 3] = K[K.length - i - 4 + 3];
            K[K.length - i - 4] = jj0;
            K[K.length - i - 4 + 1] = jj2;
            K[K.length - i - 4 + 2] = jj3;
            K[K.length - i - 4 + 3] = jj4;
        }
        for (int r = 4; r < K.length - 4; ++r) {
            final int tt = K[r];
            K[r] = (Rijndael.U1[tt >>> 24 & 0xFF] ^ Rijndael.U2[tt >>> 16 & 0xFF] ^ Rijndael.U3[tt >>> 8 & 0xFF] ^ Rijndael.U4[tt & 0xFF]);
        }
        final int j0 = K[K.length - 4];
        final int j2 = K[K.length - 3];
        final int j3 = K[K.length - 2];
        final int j4 = K[K.length - 1];
        for (int k = K.length - 1; k > 3; --k) {
            K[k] = K[k - 4];
        }
        K[0] = j0;
        K[1] = j2;
        K[2] = j3;
        K[3] = j4;
    }
    
    private static int getRounds(final int keySize) {
        return (keySize >> 2) + 6;
    }
    
    public Rijndael() {
        super(16);
    }
    
    static {
        S = new byte[256];
        Si = new byte[256];
        T1 = new int[256];
        T2 = new int[256];
        T3 = new int[256];
        T4 = new int[256];
        T5 = new int[256];
        T6 = new int[256];
        T7 = new int[256];
        T8 = new int[256];
        U1 = new int[256];
        U2 = new int[256];
        U3 = new int[256];
        U4 = new int[256];
        rcon = new byte[30];
        final int ROOT = 283;
        final int j = 0;
        for (int l = 0; l < 256; ++l) {
            final char c = "\u637c\u777b\uf26b\u6fc5\u3001\u672b\ufed7\uab76\uca82\uc97d\ufa59\u47f0\uadd4\ua2af\u9ca4\u72c0\ub7fd\u9326\u363f\uf7cc\u34a5\ue5f1\u71d8\u3115\u04c7\u23c3\u1896\u059a\u0712\u80e2\ueb27\ub275\u0983\u2c1a\u1b6e\u5aa0\u523b\ud6b3\u29e3\u2f84\u53d1\u00ed\u20fc\ub15b\u6acb\ube39\u4a4c\u58cf\ud0ef\uaafb\u434d\u3385\u45f9\u027f\u503c\u9fa8\u51a3\u408f\u929d\u38f5\ubcb6\uda21\u10ff\uf3d2\ucd0c\u13ec\u5f97\u4417\uc4a7\u7e3d\u645d\u1973\u6081\u4fdc\u222a\u9088\u46ee\ub814\ude5e\u0bdb\ue032\u3a0a\u4906\u245c\uc2d3\uac62\u9195\ue479\ue7c8\u376d\u8dd5\u4ea9\u6c56\uf4ea\u657a\uae08\uba78\u252e\u1ca6\ub4c6\ue8dd\u741f\u4bbd\u8b8a\u703e\ub566\u4803\uf60e\u6135\u57b9\u86c1\u1d9e\ue1f8\u9811\u69d9\u8e94\u9b1e\u87e9\uce55\u28df\u8ca1\u890d\ubfe6\u4268\u4199\u2d0f\ub054\ubb16".charAt(l >>> 1);
            Rijndael.S[l] = (byte)(((l & 0x1) == 0x0) ? (c >>> 8) : (c & '\u00ff'));
            final int s = Rijndael.S[l] & 0xFF;
            Rijndael.Si[s] = (byte)l;
            int s2 = s << 1;
            if (s2 >= 256) {
                s2 ^= ROOT;
            }
            final int s3 = s2 ^ s;
            int i2 = l << 1;
            if (i2 >= 256) {
                i2 ^= ROOT;
            }
            int i3 = i2 << 1;
            if (i3 >= 256) {
                i3 ^= ROOT;
            }
            int i4 = i3 << 1;
            if (i4 >= 256) {
                i4 ^= ROOT;
            }
            final int i5 = i4 ^ l;
            final int ib = i5 ^ i2;
            final int id = i5 ^ i3;
            final int ie = i4 ^ i3 ^ i2;
            int t = Rijndael.T1[l] = (s2 << 24 | s << 16 | s << 8 | s3);
            Rijndael.T2[l] = (t >>> 8 | t << 24);
            Rijndael.T3[l] = (t >>> 16 | t << 16);
            Rijndael.T4[l] = (t >>> 24 | t << 8);
            Rijndael.T5[s] = (Rijndael.U1[l] = (t = (ie << 24 | i5 << 16 | id << 8 | ib)));
            Rijndael.T6[s] = (Rijndael.U2[l] = (t >>> 8 | t << 24));
            Rijndael.T7[s] = (Rijndael.U3[l] = (t >>> 16 | t << 16));
            Rijndael.T8[s] = (Rijndael.U4[l] = (t >>> 24 | t << 8));
        }
        int r = 1;
        Rijndael.rcon[0] = 1;
        for (int k = 1; k < 30; ++k) {
            r <<= 1;
            if (r >= 256) {
                r ^= ROOT;
            }
            Rijndael.rcon[k] = (byte)r;
        }
    }
}
