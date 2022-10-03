package org.bouncycastle.crypto.digests;

public class SHA3Digest extends KeccakDigest
{
    private static int checkBitLength(final int n) {
        switch (n) {
            case 224:
            case 256:
            case 384:
            case 512: {
                return n;
            }
            default: {
                throw new IllegalArgumentException("'bitLength' " + n + " not supported for SHA-3");
            }
        }
    }
    
    public SHA3Digest() {
        this(256);
    }
    
    public SHA3Digest(final int n) {
        super(checkBitLength(n));
    }
    
    public SHA3Digest(final SHA3Digest sha3Digest) {
        super(sha3Digest);
    }
    
    @Override
    public String getAlgorithmName() {
        return "SHA3-" + this.fixedOutputLength;
    }
    
    @Override
    public int doFinal(final byte[] array, final int n) {
        this.absorbBits(2, 2);
        return super.doFinal(array, n);
    }
    
    @Override
    protected int doFinal(final byte[] array, final int n, final byte b, final int n2) {
        if (n2 < 0 || n2 > 7) {
            throw new IllegalArgumentException("'partialBits' must be in the range [0,7]");
        }
        int n3 = (b & (1 << n2) - 1) | 2 << n2;
        int n4 = n2 + 2;
        if (n4 >= 8) {
            this.absorb(new byte[] { (byte)n3 }, 0, 1);
            n4 -= 8;
            n3 >>>= 8;
        }
        return super.doFinal(array, n, (byte)n3, n4);
    }
}
