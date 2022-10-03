package org.bouncycastle.crypto.params;

import org.bouncycastle.math.ec.ECPoint;

public class ECPublicKeyParameters extends ECKeyParameters
{
    private final ECPoint Q;
    
    public ECPublicKeyParameters(final ECPoint ecPoint, final ECDomainParameters ecDomainParameters) {
        super(false, ecDomainParameters);
        this.Q = this.validate(ecPoint);
    }
    
    private ECPoint validate(ECPoint normalize) {
        if (normalize == null) {
            throw new IllegalArgumentException("point has null value");
        }
        if (normalize.isInfinity()) {
            throw new IllegalArgumentException("point at infinity");
        }
        normalize = normalize.normalize();
        if (!normalize.isValid()) {
            throw new IllegalArgumentException("point not on curve");
        }
        return normalize;
    }
    
    public ECPoint getQ() {
        return this.Q;
    }
}
