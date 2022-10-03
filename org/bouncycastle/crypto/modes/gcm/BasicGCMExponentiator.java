package org.bouncycastle.crypto.modes.gcm;

import org.bouncycastle.util.Arrays;

public class BasicGCMExponentiator implements GCMExponentiator
{
    private long[] x;
    
    public void init(final byte[] array) {
        this.x = GCMUtil.asLongs(array);
    }
    
    public void exponentiateX(long n, final byte[] array) {
        final long[] oneAsLongs = GCMUtil.oneAsLongs();
        if (n > 0L) {
            final long[] clone = Arrays.clone(this.x);
            do {
                if ((n & 0x1L) != 0x0L) {
                    GCMUtil.multiply(oneAsLongs, clone);
                }
                GCMUtil.square(clone, clone);
                n >>>= 1;
            } while (n > 0L);
        }
        GCMUtil.asBytes(oneAsLongs, array);
    }
}
