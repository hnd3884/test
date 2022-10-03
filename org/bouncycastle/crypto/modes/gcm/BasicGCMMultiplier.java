package org.bouncycastle.crypto.modes.gcm;

public class BasicGCMMultiplier implements GCMMultiplier
{
    private long[] H;
    
    public void init(final byte[] array) {
        this.H = GCMUtil.asLongs(array);
    }
    
    public void multiplyH(final byte[] array) {
        final long[] longs = GCMUtil.asLongs(array);
        GCMUtil.multiply(longs, this.H);
        GCMUtil.asBytes(longs, array);
    }
}
