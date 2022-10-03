package org.bouncycastle.crypto.modes.kgcm;

public class BasicKGCMMultiplier_512 implements KGCMMultiplier
{
    private final long[] H;
    
    public BasicKGCMMultiplier_512() {
        this.H = new long[8];
    }
    
    public void init(final long[] array) {
        KGCMUtil_512.copy(array, this.H);
    }
    
    public void multiplyH(final long[] array) {
        KGCMUtil_512.multiply(array, this.H, array);
    }
}
