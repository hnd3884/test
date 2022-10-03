package org.bouncycastle.crypto.modes.kgcm;

public class Tables16kKGCMMultiplier_512 implements KGCMMultiplier
{
    private long[][] T;
    
    public void init(final long[] array) {
        if (this.T == null) {
            this.T = new long[256][8];
        }
        else if (KGCMUtil_512.equal(array, this.T[1])) {
            return;
        }
        KGCMUtil_512.copy(array, this.T[1]);
        for (int i = 2; i < 256; i += 2) {
            KGCMUtil_512.multiplyX(this.T[i >> 1], this.T[i]);
            KGCMUtil_512.add(this.T[i], this.T[1], this.T[i + 1]);
        }
    }
    
    public void multiplyH(final long[] array) {
        final long[] array2 = new long[8];
        KGCMUtil_512.copy(this.T[(int)(array[7] >>> 56) & 0xFF], array2);
        for (int i = 62; i >= 0; --i) {
            KGCMUtil_512.multiplyX8(array2, array2);
            KGCMUtil_512.add(this.T[(int)(array[i >>> 3] >>> ((i & 0x7) << 3)) & 0xFF], array2, array2);
        }
        KGCMUtil_512.copy(array2, array);
    }
}
