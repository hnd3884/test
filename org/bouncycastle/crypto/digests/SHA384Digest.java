package org.bouncycastle.crypto.digests;

import org.bouncycastle.util.Memoable;
import org.bouncycastle.util.Pack;

public class SHA384Digest extends LongDigest
{
    private static final int DIGEST_LENGTH = 48;
    
    public SHA384Digest() {
    }
    
    public SHA384Digest(final SHA384Digest sha384Digest) {
        super(sha384Digest);
    }
    
    public SHA384Digest(final byte[] array) {
        this.restoreState(array);
    }
    
    public String getAlgorithmName() {
        return "SHA-384";
    }
    
    public int getDigestSize() {
        return 48;
    }
    
    public int doFinal(final byte[] array, final int n) {
        this.finish();
        Pack.longToBigEndian(this.H1, array, n);
        Pack.longToBigEndian(this.H2, array, n + 8);
        Pack.longToBigEndian(this.H3, array, n + 16);
        Pack.longToBigEndian(this.H4, array, n + 24);
        Pack.longToBigEndian(this.H5, array, n + 32);
        Pack.longToBigEndian(this.H6, array, n + 40);
        this.reset();
        return 48;
    }
    
    @Override
    public void reset() {
        super.reset();
        this.H1 = -3766243637369397544L;
        this.H2 = 7105036623409894663L;
        this.H3 = -7973340178411365097L;
        this.H4 = 1526699215303891257L;
        this.H5 = 7436329637833083697L;
        this.H6 = -8163818279084223215L;
        this.H7 = -2662702644619276377L;
        this.H8 = 5167115440072839076L;
    }
    
    public Memoable copy() {
        return new SHA384Digest(this);
    }
    
    public void reset(final Memoable memoable) {
        super.copyIn((LongDigest)memoable);
    }
    
    public byte[] getEncodedState() {
        final byte[] array = new byte[this.getEncodedStateSize()];
        super.populateState(array);
        return array;
    }
}
