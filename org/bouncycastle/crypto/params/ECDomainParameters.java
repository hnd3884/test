package org.bouncycastle.crypto.params;

import org.bouncycastle.util.Arrays;
import java.math.BigInteger;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECConstants;

public class ECDomainParameters implements ECConstants
{
    private ECCurve curve;
    private byte[] seed;
    private ECPoint G;
    private BigInteger n;
    private BigInteger h;
    
    public ECDomainParameters(final ECCurve ecCurve, final ECPoint ecPoint, final BigInteger bigInteger) {
        this(ecCurve, ecPoint, bigInteger, ECDomainParameters.ONE, null);
    }
    
    public ECDomainParameters(final ECCurve ecCurve, final ECPoint ecPoint, final BigInteger bigInteger, final BigInteger bigInteger2) {
        this(ecCurve, ecPoint, bigInteger, bigInteger2, null);
    }
    
    public ECDomainParameters(final ECCurve curve, final ECPoint ecPoint, final BigInteger n, final BigInteger h, final byte[] seed) {
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
        return Arrays.clone(this.seed);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof ECDomainParameters) {
            final ECDomainParameters ecDomainParameters = (ECDomainParameters)o;
            return this.curve.equals(ecDomainParameters.curve) && this.G.equals(ecDomainParameters.G) && this.n.equals(ecDomainParameters.n) && this.h.equals(ecDomainParameters.h);
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return ((this.curve.hashCode() * 37 ^ this.G.hashCode()) * 37 ^ this.n.hashCode()) * 37 ^ this.h.hashCode();
    }
}
