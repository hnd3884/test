package cryptix.jce.provider.cipher;

import java.security.InvalidKeyException;
import java.security.Key;

public final class IDEA extends BlockCipher
{
    private static final int ROUNDS = 8;
    private static final int BLOCK_SIZE = 8;
    private static final int KEY_LENGTH = 16;
    private static final int INTERNAL_KEY_LENGTH = 52;
    private short[] ks;
    
    protected void coreInit(final Key key, final boolean decrypt) throws InvalidKeyException {
        this.makeKey(key);
        if (decrypt) {
            this.invertKey();
        }
    }
    
    protected void coreCrypt(final byte[] in, final int inOffset, final byte[] out, final int outOffset) {
        this.blockEncrypt(in, inOffset, out, outOffset);
    }
    
    private void blockEncrypt(final byte[] in, int inOffset, final byte[] out, int outOffset) {
        short x1 = (short)((in[inOffset++] & 0xFF) << 8 | (in[inOffset++] & 0xFF));
        short x2 = (short)((in[inOffset++] & 0xFF) << 8 | (in[inOffset++] & 0xFF));
        short x3 = (short)((in[inOffset++] & 0xFF) << 8 | (in[inOffset++] & 0xFF));
        short x4 = (short)((in[inOffset++] & 0xFF) << 8 | (in[inOffset] & 0xFF));
        int i = 0;
        int round = 8;
        while (round-- > 0) {
            x1 = mul(x1, this.ks[i++]);
            x2 += this.ks[i++];
            x3 += this.ks[i++];
            x4 = mul(x4, this.ks[i++]);
            final short s3 = x3;
            x3 = mul(x1 ^ x3, this.ks[i++]);
            final short s4 = x2;
            x2 = mul(x3 + (x2 ^ x4), this.ks[i++]);
            x3 += x2;
            x1 ^= x2;
            x4 ^= x3;
            x2 ^= s3;
            x3 ^= s4;
        }
        short s4 = mul(x1, this.ks[i++]);
        out[outOffset++] = (byte)(s4 >>> 8);
        out[outOffset++] = (byte)s4;
        s4 = (short)(x3 + this.ks[i++]);
        out[outOffset++] = (byte)(s4 >>> 8);
        out[outOffset++] = (byte)s4;
        s4 = (short)(x2 + this.ks[i++]);
        out[outOffset++] = (byte)(s4 >>> 8);
        out[outOffset++] = (byte)s4;
        s4 = mul(x4, this.ks[i]);
        out[outOffset++] = (byte)(s4 >>> 8);
        out[outOffset] = (byte)s4;
    }
    
    private void makeKey(final Key key) throws InvalidKeyException {
        final byte[] userkey = key.getEncoded();
        if (userkey == null) {
            throw new InvalidKeyException("Null user key");
        }
        if (userkey.length != 16) {
            throw new InvalidKeyException("Invalid user key length");
        }
        this.ks[0] = (short)((userkey[0] & 0xFF) << 8 | (userkey[1] & 0xFF));
        this.ks[1] = (short)((userkey[2] & 0xFF) << 8 | (userkey[3] & 0xFF));
        this.ks[2] = (short)((userkey[4] & 0xFF) << 8 | (userkey[5] & 0xFF));
        this.ks[3] = (short)((userkey[6] & 0xFF) << 8 | (userkey[7] & 0xFF));
        this.ks[4] = (short)((userkey[8] & 0xFF) << 8 | (userkey[9] & 0xFF));
        this.ks[5] = (short)((userkey[10] & 0xFF) << 8 | (userkey[11] & 0xFF));
        this.ks[6] = (short)((userkey[12] & 0xFF) << 8 | (userkey[13] & 0xFF));
        this.ks[7] = (short)((userkey[14] & 0xFF) << 8 | (userkey[15] & 0xFF));
        int i = 0;
        int zoff = 0;
        for (int j = 8; j < 52; ++j) {
            ++i;
            this.ks[i + 7 + zoff] = (short)(this.ks[(i & 0x7) + zoff] << 9 | (this.ks[(i + 1 & 0x7) + zoff] >>> 7 & 0x1FF));
            zoff += (i & 0x8);
            i &= 0x7;
        }
    }
    
    private void invertKey() {
        int j = 4;
        int k = 51;
        final short[] temp = new short[52];
        temp[k--] = inv(this.ks[3]);
        temp[k--] = (short)(-this.ks[2]);
        temp[k--] = (short)(-this.ks[1]);
        temp[k--] = inv(this.ks[0]);
        for (int i = 1; i < 8; ++i, j += 6) {
            temp[k--] = this.ks[j + 1];
            temp[k--] = this.ks[j];
            temp[k--] = inv(this.ks[j + 5]);
            temp[k--] = (short)(-this.ks[j + 3]);
            temp[k--] = (short)(-this.ks[j + 4]);
            temp[k--] = inv(this.ks[j + 2]);
        }
        temp[k--] = this.ks[j + 1];
        temp[k--] = this.ks[j];
        temp[k--] = inv(this.ks[j + 5]);
        temp[k--] = (short)(-this.ks[j + 4]);
        temp[k--] = (short)(-this.ks[j + 3]);
        temp[k--] = inv(this.ks[j + 2]);
        System.arraycopy(temp, 0, this.ks, 0, 52);
    }
    
    private static short inv(final short xx) {
        int x = xx & 0xFFFF;
        if (x <= 1) {
            return (short)x;
        }
        int t1 = 65537 / x;
        int y = 65537 % x;
        if (y == 1) {
            return (short)(1 - t1);
        }
        int t2 = 1;
        do {
            int q = x / y;
            x %= y;
            t2 += q * t1;
            if (x == 1) {
                return (short)t2;
            }
            q = y / x;
            y %= x;
            t1 += q * t2;
        } while (y != 1);
        return (short)(1 - t1);
    }
    
    private static short mul(int a, int b) {
        a &= 0xFFFF;
        b &= 0xFFFF;
        if (a == 0) {
            return (short)(1 - b);
        }
        if (b != 0) {
            final int p = a * b;
            b = (p & 0xFFFF);
            a = p >>> 16;
            return (short)(b - a + ((b < a) ? 1 : 0));
        }
        return (short)(1 - a);
    }
    
    public IDEA() {
        super(8);
        this.ks = new short[52];
    }
}
