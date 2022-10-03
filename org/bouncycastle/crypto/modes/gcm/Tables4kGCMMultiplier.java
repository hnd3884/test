package org.bouncycastle.crypto.modes.gcm;

import org.bouncycastle.util.Pack;
import org.bouncycastle.util.Arrays;

public class Tables4kGCMMultiplier implements GCMMultiplier
{
    private byte[] H;
    private long[][] T;
    
    public void init(final byte[] array) {
        if (this.T == null) {
            this.T = new long[256][2];
        }
        else if (Arrays.areEqual(this.H, array)) {
            return;
        }
        GCMUtil.asLongs(this.H = Arrays.clone(array), this.T[1]);
        GCMUtil.multiplyP7(this.T[1], this.T[1]);
        for (int i = 2; i < 256; i += 2) {
            GCMUtil.divideP(this.T[i >> 1], this.T[i]);
            GCMUtil.xor(this.T[i], this.T[1], this.T[i + 1]);
        }
    }
    
    public void multiplyH(final byte[] array) {
        final long[] array2 = this.T[array[15] & 0xFF];
        long n = array2[0];
        long n2 = array2[1];
        for (int i = 14; i >= 0; --i) {
            final long[] array3 = this.T[array[i] & 0xFF];
            final long n3 = n2 << 56;
            n2 = (array3[1] ^ (n2 >>> 8 | n << 56));
            n = (array3[0] ^ n >>> 8 ^ n3 ^ n3 >>> 1 ^ n3 >>> 2 ^ n3 >>> 7);
        }
        Pack.longToBigEndian(n, array, 0);
        Pack.longToBigEndian(n2, array, 8);
    }
}
