package org.bouncycastle.crypto.digests;

import org.bouncycastle.util.Memoable;

public class GOST3411_2012_512Digest extends GOST3411_2012Digest
{
    private static final byte[] IV;
    
    public GOST3411_2012_512Digest() {
        super(GOST3411_2012_512Digest.IV);
    }
    
    public GOST3411_2012_512Digest(final GOST3411_2012_512Digest gost3411_2012_512Digest) {
        super(GOST3411_2012_512Digest.IV);
        this.reset(gost3411_2012_512Digest);
    }
    
    @Override
    public String getAlgorithmName() {
        return "GOST3411-2012-512";
    }
    
    @Override
    public int getDigestSize() {
        return 64;
    }
    
    @Override
    public Memoable copy() {
        return new GOST3411_2012_512Digest(this);
    }
    
    static {
        IV = new byte[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
    }
}
