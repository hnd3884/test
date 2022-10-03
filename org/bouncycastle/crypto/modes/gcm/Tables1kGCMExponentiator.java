package org.bouncycastle.crypto.modes.gcm;

import org.bouncycastle.util.Arrays;
import java.util.Vector;

public class Tables1kGCMExponentiator implements GCMExponentiator
{
    private Vector lookupPowX2;
    
    public void init(final byte[] array) {
        final long[] longs = GCMUtil.asLongs(array);
        if (this.lookupPowX2 != null && Arrays.areEqual(longs, this.lookupPowX2.elementAt(0))) {
            return;
        }
        (this.lookupPowX2 = new Vector(8)).addElement(longs);
    }
    
    public void exponentiateX(long n, final byte[] array) {
        final long[] oneAsLongs = GCMUtil.oneAsLongs();
        int n2 = 0;
        while (n > 0L) {
            if ((n & 0x1L) != 0x0L) {
                this.ensureAvailable(n2);
                GCMUtil.multiply(oneAsLongs, (long[])this.lookupPowX2.elementAt(n2));
            }
            ++n2;
            n >>>= 1;
        }
        GCMUtil.asBytes(oneAsLongs, array);
    }
    
    private void ensureAvailable(final int n) {
        int size = this.lookupPowX2.size();
        if (size <= n) {
            long[] clone = this.lookupPowX2.elementAt(size - 1);
            do {
                clone = Arrays.clone(clone);
                GCMUtil.square(clone, clone);
                this.lookupPowX2.addElement(clone);
            } while (++size <= n);
        }
    }
}
