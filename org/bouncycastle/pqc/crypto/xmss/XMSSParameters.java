package org.bouncycastle.pqc.crypto.xmss;

import org.bouncycastle.crypto.Digest;

public final class XMSSParameters
{
    private final XMSSOid oid;
    private final WOTSPlus wotsPlus;
    private final int height;
    private final int k;
    
    public XMSSParameters(final int height, final Digest digest) {
        if (height < 2) {
            throw new IllegalArgumentException("height must be >= 2");
        }
        if (digest == null) {
            throw new NullPointerException("digest == null");
        }
        this.wotsPlus = new WOTSPlus(new WOTSPlusParameters(digest));
        this.height = height;
        this.k = this.determineMinK();
        this.oid = DefaultXMSSOid.lookup(this.getDigest().getAlgorithmName(), this.getDigestSize(), this.getWinternitzParameter(), this.wotsPlus.getParams().getLen(), height);
    }
    
    private int determineMinK() {
        for (int i = 2; i <= this.height; ++i) {
            if ((this.height - i) % 2 == 0) {
                return i;
            }
        }
        throw new IllegalStateException("should never happen...");
    }
    
    protected Digest getDigest() {
        return this.wotsPlus.getParams().getDigest();
    }
    
    public int getDigestSize() {
        return this.wotsPlus.getParams().getDigestSize();
    }
    
    public int getWinternitzParameter() {
        return this.wotsPlus.getParams().getWinternitzParameter();
    }
    
    public int getHeight() {
        return this.height;
    }
    
    WOTSPlus getWOTSPlus() {
        return this.wotsPlus;
    }
    
    int getK() {
        return this.k;
    }
}
