package org.bouncycastle.crypto.modes.gcm;

import org.bouncycastle.util.Pack;
import org.bouncycastle.util.Arrays;

public class Tables64kGCMMultiplier implements GCMMultiplier
{
    private byte[] H;
    private long[][][] T;
    
    public void init(final byte[] array) {
        if (this.T == null) {
            this.T = new long[16][256][2];
        }
        else if (Arrays.areEqual(this.H, array)) {
            return;
        }
        this.H = Arrays.clone(array);
        for (int i = 0; i < 16; ++i) {
            final long[][] array2 = this.T[i];
            if (i == 0) {
                GCMUtil.asLongs(this.H, array2[1]);
                GCMUtil.multiplyP7(array2[1], array2[1]);
            }
            else {
                GCMUtil.multiplyP8(this.T[i - 1][1], array2[1]);
            }
            for (int j = 2; j < 256; j += 2) {
                GCMUtil.divideP(array2[j >> 1], array2[j]);
                GCMUtil.xor(array2[j], array2[1], array2[j + 1]);
            }
        }
    }
    
    public void multiplyH(final byte[] array) {
        final long[] array2 = this.T[15][array[15] & 0xFF];
        long n = array2[0];
        long n2 = array2[1];
        for (int i = 14; i >= 0; --i) {
            final long[] array3 = this.T[i][array[i] & 0xFF];
            n ^= array3[0];
            n2 ^= array3[1];
        }
        Pack.longToBigEndian(n, array, 0);
        Pack.longToBigEndian(n2, array, 8);
    }
}
