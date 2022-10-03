package org.bouncycastle.crypto.digests;

import org.bouncycastle.crypto.ExtendedDigest;

public class NonMemoableDigest implements ExtendedDigest
{
    private ExtendedDigest baseDigest;
    
    public NonMemoableDigest(final ExtendedDigest baseDigest) {
        if (baseDigest == null) {
            throw new IllegalArgumentException("baseDigest must not be null");
        }
        this.baseDigest = baseDigest;
    }
    
    public String getAlgorithmName() {
        return this.baseDigest.getAlgorithmName();
    }
    
    public int getDigestSize() {
        return this.baseDigest.getDigestSize();
    }
    
    public void update(final byte b) {
        this.baseDigest.update(b);
    }
    
    public void update(final byte[] array, final int n, final int n2) {
        this.baseDigest.update(array, n, n2);
    }
    
    public int doFinal(final byte[] array, final int n) {
        return this.baseDigest.doFinal(array, n);
    }
    
    public void reset() {
        this.baseDigest.reset();
    }
    
    public int getByteLength() {
        return this.baseDigest.getByteLength();
    }
}
