package org.bouncycastle.jce;

import org.bouncycastle.jcajce.provider.asymmetric.util.EC5Util;
import java.security.spec.ECFieldF2m;
import org.bouncycastle.math.ec.ECCurve;
import java.security.spec.ECFieldFp;
import java.security.spec.ECPoint;
import java.security.spec.EllipticCurve;

public class ECPointUtil
{
    public static ECPoint decodePoint(final EllipticCurve ellipticCurve, final byte[] array) {
        ECCurve ecCurve;
        if (ellipticCurve.getField() instanceof ECFieldFp) {
            ecCurve = new ECCurve.Fp(((ECFieldFp)ellipticCurve.getField()).getP(), ellipticCurve.getA(), ellipticCurve.getB());
        }
        else {
            final int[] midTermsOfReductionPolynomial = ((ECFieldF2m)ellipticCurve.getField()).getMidTermsOfReductionPolynomial();
            if (midTermsOfReductionPolynomial.length == 3) {
                ecCurve = new ECCurve.F2m(((ECFieldF2m)ellipticCurve.getField()).getM(), midTermsOfReductionPolynomial[2], midTermsOfReductionPolynomial[1], midTermsOfReductionPolynomial[0], ellipticCurve.getA(), ellipticCurve.getB());
            }
            else {
                ecCurve = new ECCurve.F2m(((ECFieldF2m)ellipticCurve.getField()).getM(), midTermsOfReductionPolynomial[0], ellipticCurve.getA(), ellipticCurve.getB());
            }
        }
        return EC5Util.convertPoint(ecCurve.decodePoint(array));
    }
}
