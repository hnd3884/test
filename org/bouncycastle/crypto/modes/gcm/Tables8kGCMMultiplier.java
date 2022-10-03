package org.bouncycastle.crypto.modes.gcm;

import org.bouncycastle.util.Pack;
import org.bouncycastle.util.Arrays;

public class Tables8kGCMMultiplier implements GCMMultiplier
{
    private byte[] H;
    private long[][][] T;
    
    public void init(final byte[] array) {
        if (this.T == null) {
            this.T = new long[32][16][2];
        }
        else if (Arrays.areEqual(this.H, array)) {
            return;
        }
        this.H = Arrays.clone(array);
        for (int i = 0; i < 32; ++i) {
            final long[][] array2 = this.T[i];
            if (i == 0) {
                GCMUtil.asLongs(this.H, array2[1]);
                GCMUtil.multiplyP3(array2[1], array2[1]);
            }
            else {
                GCMUtil.multiplyP4(this.T[i - 1][1], array2[1]);
            }
            for (int j = 2; j < 16; j += 2) {
                GCMUtil.divideP(array2[j >> 1], array2[j]);
                GCMUtil.xor(array2[j], array2[1], array2[j + 1]);
            }
        }
    }
    
    public void multiplyH(final byte[] array) {
        long n = 0L;
        long n2 = 0L;
        for (int i = 15; i >= 0; --i) {
            final long[] array2 = this.T[i + i + 1][array[i] & 0xF];
            final long[] array3 = this.T[i + i][(array[i] & 0xF0) >>> 4];
            n ^= (array2[0] ^ array3[0]);
            n2 ^= (array2[1] ^ array3[1]);
        }
        Pack.longToBigEndian(n, array, 0);
        Pack.longToBigEndian(n2, array, 8);
    }
}
