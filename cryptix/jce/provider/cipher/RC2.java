package cryptix.jce.provider.cipher;

import java.security.InvalidKeyException;
import java.security.Key;

public final class RC2 extends BlockCipher
{
    public static final int BLOCK_SIZE = 8;
    private static final int[] S_BOX;
    private int[] sKey;
    private boolean decrypt;
    
    protected void coreInit(final Key key, final boolean decrypt) throws InvalidKeyException {
        this.makeKey(key);
        this.decrypt = decrypt;
    }
    
    protected void coreCrypt(final byte[] in, final int inOffset, final byte[] out, final int outOffset) {
        if (this.decrypt) {
            this.blockDecrypt(in, inOffset, out, outOffset);
        }
        else {
            this.blockEncrypt(in, inOffset, out, outOffset);
        }
    }
    
    private void makeKey(final Key key) throws InvalidKeyException {
        final byte[] userkey = key.getEncoded();
        if (userkey == null) {
            throw new InvalidKeyException("Null RC2 user key");
        }
        final int len = userkey.length;
        if (len > 128) {
            throw new InvalidKeyException("Invalid RC2 user key size");
        }
        final int[] sk = new int[128];
        for (int i = 0; i < len; ++i) {
            sk[i] = (userkey[i] & 0xFF);
        }
        for (int i = len; i < 128; ++i) {
            sk[i] = RC2.S_BOX[sk[i - len] + sk[i - 1] & 0xFF];
        }
        sk[128 - len] = RC2.S_BOX[sk[128 - len] & 0xFF];
        for (int i = 127 - len; i >= 0; --i) {
            sk[i] = RC2.S_BOX[sk[i + len] ^ sk[i + 1]];
        }
        for (int i = 63; i >= 0; --i) {
            this.sKey[i] = ((sk[i * 2 + 1] << 8 | sk[i * 2]) & 0xFFFF);
        }
    }
    
    private void blockEncrypt(final byte[] in, int inOff, final byte[] out, int outOff) {
        int w0 = (in[inOff++] & 0xFF) | (in[inOff++] & 0xFF) << 8;
        int w2 = (in[inOff++] & 0xFF) | (in[inOff++] & 0xFF) << 8;
        int w3 = (in[inOff++] & 0xFF) | (in[inOff++] & 0xFF) << 8;
        int w4 = (in[inOff++] & 0xFF) | (in[inOff] & 0xFF) << 8;
        int j = 0;
        for (int i = 0; i < 16; ++i) {
            w0 = (w0 + (w2 & ~w4) + (w3 & w4) + this.sKey[j++] & 0xFFFF);
            w0 = (w0 << 1 | w0 >>> 15);
            w2 = (w2 + (w3 & ~w0) + (w4 & w0) + this.sKey[j++] & 0xFFFF);
            w2 = (w2 << 2 | w2 >>> 14);
            w3 = (w3 + (w4 & ~w2) + (w0 & w2) + this.sKey[j++] & 0xFFFF);
            w3 = (w3 << 3 | w3 >>> 13);
            w4 = (w4 + (w0 & ~w3) + (w2 & w3) + this.sKey[j++] & 0xFFFF);
            w4 = (w4 << 5 | w4 >>> 11);
            if (i == 4 || i == 10) {
                w0 += this.sKey[w4 & 0x3F];
                w2 += this.sKey[w0 & 0x3F];
                w3 += this.sKey[w2 & 0x3F];
                w4 += this.sKey[w3 & 0x3F];
            }
        }
        out[outOff++] = (byte)w0;
        out[outOff++] = (byte)(w0 >>> 8);
        out[outOff++] = (byte)w2;
        out[outOff++] = (byte)(w2 >>> 8);
        out[outOff++] = (byte)w3;
        out[outOff++] = (byte)(w3 >>> 8);
        out[outOff++] = (byte)w4;
        out[outOff] = (byte)(w4 >>> 8);
    }
    
    private void blockDecrypt(final byte[] in, final int inOff, final byte[] out, int outOff) {
        int w0 = (in[inOff] & 0xFF) | (in[inOff + 1] & 0xFF) << 8;
        int w2 = (in[inOff + 2] & 0xFF) | (in[inOff + 3] & 0xFF) << 8;
        int w3 = (in[inOff + 4] & 0xFF) | (in[inOff + 5] & 0xFF) << 8;
        int w4 = (in[inOff + 6] & 0xFF) | (in[inOff + 7] & 0xFF) << 8;
        int j = 63;
        for (int i = 15; i >= 0; --i) {
            w4 = ((w4 >>> 5 | w4 << 11) & 0xFFFF);
            w4 = (w4 - (w0 & ~w3) - (w2 & w3) - this.sKey[j--] & 0xFFFF);
            w3 = ((w3 >>> 3 | w3 << 13) & 0xFFFF);
            w3 = (w3 - (w4 & ~w2) - (w0 & w2) - this.sKey[j--] & 0xFFFF);
            w2 = ((w2 >>> 2 | w2 << 14) & 0xFFFF);
            w2 = (w2 - (w3 & ~w0) - (w4 & w0) - this.sKey[j--] & 0xFFFF);
            w0 = ((w0 >>> 1 | w0 << 15) & 0xFFFF);
            w0 = (w0 - (w2 & ~w4) - (w3 & w4) - this.sKey[j--] & 0xFFFF);
            if (i == 11 || i == 5) {
                w4 = (w4 - this.sKey[w3 & 0x3F] & 0xFFFF);
                w3 = (w3 - this.sKey[w2 & 0x3F] & 0xFFFF);
                w2 = (w2 - this.sKey[w0 & 0x3F] & 0xFFFF);
                w0 = (w0 - this.sKey[w4 & 0x3F] & 0xFFFF);
            }
        }
        out[outOff++] = (byte)w0;
        out[outOff++] = (byte)(w0 >>> 8);
        out[outOff++] = (byte)w2;
        out[outOff++] = (byte)(w2 >>> 8);
        out[outOff++] = (byte)w3;
        out[outOff++] = (byte)(w3 >>> 8);
        out[outOff++] = (byte)w4;
        out[outOff] = (byte)(w4 >>> 8);
    }
    
    public RC2() {
        super(8);
        this.sKey = new int[64];
    }
    
    static {
        S_BOX = new int[] { 217, 120, 249, 196, 25, 221, 181, 237, 40, 233, 253, 121, 74, 160, 216, 157, 198, 126, 55, 131, 43, 118, 83, 142, 98, 76, 100, 136, 68, 139, 251, 162, 23, 154, 89, 245, 135, 179, 79, 19, 97, 69, 109, 141, 9, 129, 125, 50, 189, 143, 64, 235, 134, 183, 123, 11, 240, 149, 33, 34, 92, 107, 78, 130, 84, 214, 101, 147, 206, 96, 178, 28, 115, 86, 192, 20, 167, 140, 241, 220, 18, 117, 202, 31, 59, 190, 228, 209, 66, 61, 212, 48, 163, 60, 182, 38, 111, 191, 14, 218, 70, 105, 7, 87, 39, 242, 29, 155, 188, 148, 67, 3, 248, 17, 199, 246, 144, 239, 62, 231, 6, 195, 213, 47, 200, 102, 30, 215, 8, 232, 234, 222, 128, 82, 238, 247, 132, 170, 114, 172, 53, 77, 106, 42, 150, 26, 210, 113, 90, 21, 73, 116, 75, 159, 208, 94, 4, 24, 164, 236, 194, 224, 65, 110, 15, 81, 203, 204, 36, 145, 175, 80, 161, 244, 112, 57, 153, 124, 58, 133, 35, 184, 180, 122, 252, 2, 54, 91, 37, 85, 151, 49, 45, 93, 250, 152, 227, 138, 146, 174, 5, 223, 41, 16, 103, 108, 186, 201, 211, 0, 230, 207, 225, 158, 168, 44, 99, 22, 1, 63, 88, 226, 137, 169, 13, 56, 52, 27, 171, 51, 255, 176, 187, 72, 12, 95, 185, 177, 205, 46, 197, 243, 219, 71, 229, 165, 156, 119, 10, 166, 32, 104, 254, 127, 193, 173 };
    }
}
