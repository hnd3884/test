package org.bouncycastle.crypto.modes.kgcm;

public class BasicKGCMMultiplier_256 implements KGCMMultiplier
{
    private final long[] H;
    
    public BasicKGCMMultiplier_256() {
        this.H = new long[4];
    }
    
    public void init(final long[] array) {
        KGCMUtil_256.copy(array, this.H);
    }
    
    public void multiplyH(final long[] array) {
        KGCMUtil_256.multiply(array, this.H, array);
    }
}
