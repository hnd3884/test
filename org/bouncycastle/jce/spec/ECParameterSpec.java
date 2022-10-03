package org.bouncycastle.jce.spec;

import java.math.BigInteger;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.ec.ECCurve;
import java.security.spec.AlgorithmParameterSpec;

public class ECParameterSpec implements AlgorithmParameterSpec
{
    private ECCurve curve;
    private byte[] seed;
    private ECPoint G;
    private BigInteger n;
    private BigInteger h;
    
    public ECParameterSpec(final ECCurve curve, final ECPoint ecPoint, final BigInteger n) {
        this.curve = curve;
        this.G = ecPoint.normalize();
        this.n = n;
        this.h = BigInteger.valueOf(1L);
        this.seed = null;
    }
    
    public ECParameterSpec(final ECCurve curve, final ECPoint ecPoint, final BigInteger n, final BigInteger h) {
        this.curve = curve;
        this.G = ecPoint.normalize();
        this.n = n;
        this.h = h;
        this.seed = null;
    }
    
    public ECParameterSpec(final ECCurve curve, final ECPoint ecPoint, final BigInteger n, final BigInteger h, final byte[] seed) {
        this.curve = curve;
        this.G = ecPoint.normalize();
        this.n = n;
        this.h = h;
        this.seed = seed;
    }
    
    public ECCurve getCurve() {
        return this.curve;
    }
    
    public ECPoint getG() {
        return this.G;
    }
    
    public BigInteger getN() {
        return this.n;
    }
    
    public BigInteger getH() {
        return this.h;
    }
    
    public byte[] getSeed() {
        return this.seed;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof ECParameterSpec)) {
            return false;
        }
        final ECParameterSpec ecParameterSpec = (ECParameterSpec)o;
        return this.getCurve().equals(ecParameterSpec.getCurve()) && this.getG().equals(ecParameterSpec.getG());
    }
    
    @Override
    public int hashCode() {
        return this.getCurve().hashCode() ^ this.getG().hashCode();
    }
}
