package org.bouncycastle.crypto.modes.kgcm;

public class BasicKGCMMultiplier_128 implements KGCMMultiplier
{
    private final long[] H;
    
    public BasicKGCMMultiplier_128() {
        this.H = new long[2];
    }
    
    public void init(final long[] array) {
        KGCMUtil_128.copy(array, this.H);
    }
    
    public void multiplyH(final long[] array) {
        KGCMUtil_128.multiply(array, this.H, array);
    }
}
