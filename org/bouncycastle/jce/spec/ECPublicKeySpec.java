package org.bouncycastle.jce.spec;

import org.bouncycastle.math.ec.ECPoint;

public class ECPublicKeySpec extends ECKeySpec
{
    private ECPoint q;
    
    public ECPublicKeySpec(final ECPoint q, final ECParameterSpec ecParameterSpec) {
        super(ecParameterSpec);
        if (q.getCurve() != null) {
            this.q = q.normalize();
        }
        else {
            this.q = q;
        }
    }
    
    public ECPoint getQ() {
        return this.q;
    }
}
