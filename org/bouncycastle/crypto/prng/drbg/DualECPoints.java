package org.bouncycastle.crypto.prng.drbg;

import org.bouncycastle.math.ec.ECPoint;

public class DualECPoints
{
    private final ECPoint p;
    private final ECPoint q;
    private final int securityStrength;
    private final int cofactor;
    
    public DualECPoints(final int securityStrength, final ECPoint p4, final ECPoint q, final int cofactor) {
        if (!p4.getCurve().equals(q.getCurve())) {
            throw new IllegalArgumentException("points need to be on the same curve");
        }
        this.securityStrength = securityStrength;
        this.p = p4;
        this.q = q;
        this.cofactor = cofactor;
    }
    
    public int getSeedLen() {
        return this.p.getCurve().getFieldSize();
    }
    
    public int getMaxOutlen() {
        return (this.p.getCurve().getFieldSize() - (13 + log2(this.cofactor))) / 8 * 8;
    }
    
    public ECPoint getP() {
        return this.p;
    }
    
    public ECPoint getQ() {
        return this.q;
    }
    
    public int getSecurityStrength() {
        return this.securityStrength;
    }
    
    public int getCofactor() {
        return this.cofactor;
    }
    
    private static int log2(int n) {
        int n2 = 0;
        while ((n >>= 1) != 0) {
            ++n2;
        }
        return n2;
    }
}
