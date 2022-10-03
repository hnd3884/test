package cryptix.jce.provider.cipher;

import java.security.InvalidKeyException;
import java.security.Key;

public final class SKIPJACK extends BlockCipher
{
    private static final int BLOCK_SIZE = 8;
    private static final int KEY_LENGTH = 10;
    private static final int[] F;
    private final int[] K;
    private boolean decrypt;
    
    protected void coreInit(final Key key, final boolean decrypt) throws InvalidKeyException {
        final byte[] userkey = key.getEncoded();
        if (userkey == null) {
            throw new InvalidKeyException("Null user key");
        }
        if (userkey.length != 10) {
            throw new InvalidKeyException("Invalid user key length");
        }
        for (int i = 0; i < this.K.length; ++i) {
            this.K[i] = (userkey[i % 10] & 0xFF);
        }
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
    
    private final void blockEncrypt(final byte[] in, int inOffset, final byte[] out, int outOffset) {
        int w1 = (in[inOffset++] & 0xFF) << 8 | (in[inOffset++] & 0xFF);
        int w2 = (in[inOffset++] & 0xFF) << 8 | (in[inOffset++] & 0xFF);
        int w3 = (in[inOffset++] & 0xFF) << 8 | (in[inOffset++] & 0xFF);
        int w4 = (in[inOffset++] & 0xFF) << 8 | (in[inOffset] & 0xFF);
        w1 = this.G(w1, 0);
        w4 ^= (w1 ^ 0x1);
        w4 = this.G(w4, 4);
        w3 ^= (w4 ^ 0x2);
        w3 = this.G(w3, 8);
        w2 ^= (w3 ^ 0x3);
        w2 = this.G(w2, 2);
        w1 ^= (w2 ^ 0x4);
        w1 = this.G(w1, 6);
        w4 ^= (w1 ^ 0x5);
        w4 = this.G(w4, 0);
        w3 ^= (w4 ^ 0x6);
        w3 = this.G(w3, 4);
        w2 ^= (w3 ^ 0x7);
        w2 = this.G(w2, 8);
        w1 ^= (w2 ^ 0x8);
        w2 ^= (w1 ^ 0x9);
        w1 = this.G(w1, 2);
        w1 ^= (w4 ^ 0xA);
        w4 = this.G(w4, 6);
        w4 ^= (w3 ^ 0xB);
        w3 = this.G(w3, 0);
        w3 ^= (w2 ^ 0xC);
        w2 = this.G(w2, 4);
        w2 ^= (w1 ^ 0xD);
        w1 = this.G(w1, 8);
        w1 ^= (w4 ^ 0xE);
        w4 = this.G(w4, 2);
        w4 ^= (w3 ^ 0xF);
        w3 = this.G(w3, 6);
        w3 ^= (w2 ^ 0x10);
        w2 = this.G(w2, 0);
        w1 = this.G(w1, 4);
        w4 ^= (w1 ^ 0x11);
        w4 = this.G(w4, 8);
        w3 ^= (w4 ^ 0x12);
        w3 = this.G(w3, 2);
        w2 ^= (w3 ^ 0x13);
        w2 = this.G(w2, 6);
        w1 ^= (w2 ^ 0x14);
        w1 = this.G(w1, 0);
        w4 ^= (w1 ^ 0x15);
        w4 = this.G(w4, 4);
        w3 ^= (w4 ^ 0x16);
        w3 = this.G(w3, 8);
        w2 ^= (w3 ^ 0x17);
        w2 = this.G(w2, 2);
        w1 ^= (w2 ^ 0x18);
        w2 ^= (w1 ^ 0x19);
        w1 = this.G(w1, 6);
        w1 ^= (w4 ^ 0x1A);
        w4 = this.G(w4, 0);
        w4 ^= (w3 ^ 0x1B);
        w3 = this.G(w3, 4);
        w3 ^= (w2 ^ 0x1C);
        w2 = this.G(w2, 8);
        w2 ^= (w1 ^ 0x1D);
        w1 = this.G(w1, 2);
        w1 ^= (w4 ^ 0x1E);
        w4 = this.G(w4, 6);
        w4 ^= (w3 ^ 0x1F);
        w3 = this.G(w3, 0);
        w3 ^= (w2 ^ 0x20);
        w2 = this.G(w2, 4);
        out[outOffset++] = (byte)(w1 >>> 8);
        out[outOffset++] = (byte)w1;
        out[outOffset++] = (byte)(w2 >>> 8);
        out[outOffset++] = (byte)w2;
        out[outOffset++] = (byte)(w3 >>> 8);
        out[outOffset++] = (byte)w3;
        out[outOffset++] = (byte)(w4 >>> 8);
        out[outOffset] = (byte)w4;
    }
    
    private final int G(final int in, final int counter) {
        int low = in & 0xFF;
        int high = in >>> 8;
        high ^= SKIPJACK.F[low ^ this.K[counter]];
        low ^= SKIPJACK.F[high ^ this.K[counter + 1]];
        high ^= SKIPJACK.F[low ^ this.K[counter + 2]];
        low ^= SKIPJACK.F[high ^ this.K[counter + 3]];
        return high << 8 | low;
    }
    
    private final void blockDecrypt(final byte[] in, int inOffset, final byte[] out, int outOffset) {
        int w1 = (in[inOffset++] & 0xFF) << 8 | (in[inOffset++] & 0xFF);
        int w2 = (in[inOffset++] & 0xFF) << 8 | (in[inOffset++] & 0xFF);
        int w3 = (in[inOffset++] & 0xFF) << 8 | (in[inOffset++] & 0xFF);
        int w4 = (in[inOffset++] & 0xFF) << 8 | (in[inOffset] & 0xFF);
        w2 = this.GINV(w2, 7);
        w3 ^= (w2 ^ 0x20);
        w3 = this.GINV(w3, 3);
        w4 ^= (w3 ^ 0x1F);
        w4 = this.GINV(w4, 9);
        w1 ^= (w4 ^ 0x1E);
        w1 = this.GINV(w1, 5);
        w2 ^= (w1 ^ 0x1D);
        w2 = this.GINV(w2, 11);
        w3 ^= (w2 ^ 0x1C);
        w3 = this.GINV(w3, 7);
        w4 ^= (w3 ^ 0x1B);
        w4 = this.GINV(w4, 3);
        w1 ^= (w4 ^ 0x1A);
        w1 = this.GINV(w1, 9);
        w2 ^= (w1 ^ 0x19);
        w1 ^= (w2 ^ 0x18);
        w2 = this.GINV(w2, 5);
        w2 ^= (w3 ^ 0x17);
        w3 = this.GINV(w3, 11);
        w3 ^= (w4 ^ 0x16);
        w4 = this.GINV(w4, 7);
        w4 ^= (w1 ^ 0x15);
        w1 = this.GINV(w1, 3);
        w1 ^= (w2 ^ 0x14);
        w2 = this.GINV(w2, 9);
        w2 ^= (w3 ^ 0x13);
        w3 = this.GINV(w3, 5);
        w3 ^= (w4 ^ 0x12);
        w4 = this.GINV(w4, 11);
        w4 ^= (w1 ^ 0x11);
        w1 = this.GINV(w1, 7);
        w2 = this.GINV(w2, 3);
        w3 ^= (w2 ^ 0x10);
        w3 = this.GINV(w3, 9);
        w4 ^= (w3 ^ 0xF);
        w4 = this.GINV(w4, 5);
        w1 ^= (w4 ^ 0xE);
        w1 = this.GINV(w1, 11);
        w2 ^= (w1 ^ 0xD);
        w2 = this.GINV(w2, 7);
        w3 ^= (w2 ^ 0xC);
        w3 = this.GINV(w3, 3);
        w4 ^= (w3 ^ 0xB);
        w4 = this.GINV(w4, 9);
        w1 ^= (w4 ^ 0xA);
        w1 = this.GINV(w1, 5);
        w2 ^= (w1 ^ 0x9);
        w1 ^= (w2 ^ 0x8);
        w2 = this.GINV(w2, 11);
        w2 ^= (w3 ^ 0x7);
        w3 = this.GINV(w3, 7);
        w3 ^= (w4 ^ 0x6);
        w4 = this.GINV(w4, 3);
        w4 ^= (w1 ^ 0x5);
        w1 = this.GINV(w1, 9);
        w1 ^= (w2 ^ 0x4);
        w2 = this.GINV(w2, 5);
        w2 ^= (w3 ^ 0x3);
        w3 = this.GINV(w3, 11);
        w3 ^= (w4 ^ 0x2);
        w4 = this.GINV(w4, 7);
        w4 ^= (w1 ^ 0x1);
        w1 = this.GINV(w1, 3);
        out[outOffset++] = (byte)(w1 >>> 8);
        out[outOffset++] = (byte)w1;
        out[outOffset++] = (byte)(w2 >>> 8);
        out[outOffset++] = (byte)w2;
        out[outOffset++] = (byte)(w3 >>> 8);
        out[outOffset++] = (byte)w3;
        out[outOffset++] = (byte)(w4 >>> 8);
        out[outOffset] = (byte)w4;
    }
    
    private final int GINV(final int in, final int counter) {
        int low = in & 0xFF;
        int high = in >>> 8;
        low ^= SKIPJACK.F[high ^ this.K[counter]];
        high ^= SKIPJACK.F[low ^ this.K[counter - 1]];
        low ^= SKIPJACK.F[high ^ this.K[counter - 2]];
        high ^= SKIPJACK.F[low ^ this.K[counter - 3]];
        return high << 8 | low;
    }
    
    public SKIPJACK() {
        super(8);
        this.K = new int[12];
    }
    
    static {
        F = new int[] { 163, 215, 9, 131, 248, 72, 246, 244, 179, 33, 21, 120, 153, 177, 175, 249, 231, 45, 77, 138, 206, 76, 202, 46, 82, 149, 217, 30, 78, 56, 68, 40, 10, 223, 2, 160, 23, 241, 96, 104, 18, 183, 122, 195, 233, 250, 61, 83, 150, 132, 107, 186, 242, 99, 154, 25, 124, 174, 229, 245, 247, 22, 106, 162, 57, 182, 123, 15, 193, 147, 129, 27, 238, 180, 26, 234, 208, 145, 47, 184, 85, 185, 218, 133, 63, 65, 191, 224, 90, 88, 128, 95, 102, 11, 216, 144, 53, 213, 192, 167, 51, 6, 101, 105, 69, 0, 148, 86, 109, 152, 155, 118, 151, 252, 178, 194, 176, 254, 219, 32, 225, 235, 214, 228, 221, 71, 74, 29, 66, 237, 158, 110, 73, 60, 205, 67, 39, 210, 7, 212, 222, 199, 103, 24, 137, 203, 48, 31, 141, 198, 143, 170, 200, 116, 220, 201, 93, 92, 49, 164, 112, 136, 97, 44, 159, 13, 43, 135, 80, 130, 84, 100, 38, 125, 3, 64, 52, 75, 28, 115, 209, 196, 253, 59, 204, 251, 127, 171, 230, 62, 91, 165, 173, 4, 35, 156, 20, 81, 34, 240, 41, 121, 113, 126, 255, 140, 14, 226, 12, 239, 188, 114, 117, 111, 55, 161, 236, 211, 142, 98, 139, 134, 16, 232, 8, 119, 17, 190, 146, 79, 36, 197, 50, 54, 157, 207, 243, 166, 187, 172, 94, 108, 169, 19, 87, 37, 181, 227, 189, 168, 58, 1, 5, 89, 42, 70 };
    }
}
