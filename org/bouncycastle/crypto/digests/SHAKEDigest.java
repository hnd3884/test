package org.bouncycastle.crypto.digests;

import org.bouncycastle.crypto.Xof;

public class SHAKEDigest extends KeccakDigest implements Xof
{
    private static int checkBitLength(final int n) {
        switch (n) {
            case 128:
            case 256: {
                return n;
            }
            default: {
                throw new IllegalArgumentException("'bitLength' " + n + " not supported for SHAKE");
            }
        }
    }
    
    public SHAKEDigest() {
        this(128);
    }
    
    public SHAKEDigest(final int n) {
        super(checkBitLength(n));
    }
    
    public SHAKEDigest(final SHAKEDigest shakeDigest) {
        super(shakeDigest);
    }
    
    @Override
    public String getAlgorithmName() {
        return "SHAKE" + this.fixedOutputLength;
    }
    
    @Override
    public int doFinal(final byte[] array, final int n) {
        return this.doFinal(array, n, this.getDigestSize());
    }
    
    public int doFinal(final byte[] array, final int n, final int n2) {
        final int doOutput = this.doOutput(array, n, n2);
        this.reset();
        return doOutput;
    }
    
    public int doOutput(final byte[] array, final int n, final int n2) {
        if (!this.squeezing) {
            this.absorbBits(15, 4);
        }
        this.squeeze(array, n, n2 * 8L);
        return n2;
    }
    
    @Override
    protected int doFinal(final byte[] array, final int n, final byte b, final int n2) {
        return this.doFinal(array, n, this.getDigestSize(), b, n2);
    }
    
    protected int doFinal(final byte[] array, final int n, final int n2, final byte b, final int n3) {
        if (n3 < 0 || n3 > 7) {
            throw new IllegalArgumentException("'partialBits' must be in the range [0,7]");
        }
        int n4 = (b & (1 << n3) - 1) | 15 << n3;
        int n5 = n3 + 4;
        if (n5 >= 8) {
            this.absorb(new byte[] { (byte)n4 }, 0, 1);
            n5 -= 8;
            n4 >>>= 8;
        }
        if (n5 > 0) {
            this.absorbBits(n4, n5);
        }
        this.squeeze(array, n, n2 * 8L);
        this.reset();
        return n2;
    }
}
