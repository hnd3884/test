package org.bouncycastle.jce.spec;

import org.bouncycastle.jcajce.provider.asymmetric.util.EC5Util;
import java.math.BigInteger;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.field.Polynomial;
import java.security.spec.ECFieldF2m;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.math.field.PolynomialExtensionField;
import java.security.spec.ECFieldFp;
import org.bouncycastle.math.ec.ECAlgorithms;
import java.security.spec.ECField;
import org.bouncycastle.math.field.FiniteField;
import java.security.spec.EllipticCurve;
import org.bouncycastle.math.ec.ECCurve;
import java.security.spec.ECParameterSpec;

public class ECNamedCurveSpec extends ECParameterSpec
{
    private String name;
    
    private static EllipticCurve convertCurve(final ECCurve ecCurve, final byte[] array) {
        return new EllipticCurve(convertField(ecCurve.getField()), ecCurve.getA().toBigInteger(), ecCurve.getB().toBigInteger(), array);
    }
    
    private static ECField convertField(final FiniteField finiteField) {
        if (ECAlgorithms.isFpField(finiteField)) {
            return new ECFieldFp(finiteField.getCharacteristic());
        }
        final Polynomial minimalPolynomial = ((PolynomialExtensionField)finiteField).getMinimalPolynomial();
        final int[] exponentsPresent = minimalPolynomial.getExponentsPresent();
        return new ECFieldF2m(minimalPolynomial.getDegree(), Arrays.reverse(Arrays.copyOfRange(exponentsPresent, 1, exponentsPresent.length - 1)));
    }
    
    public ECNamedCurveSpec(final String name, final ECCurve ecCurve, final ECPoint ecPoint, final BigInteger bigInteger) {
        super(convertCurve(ecCurve, null), EC5Util.convertPoint(ecPoint), bigInteger, 1);
        this.name = name;
    }
    
    public ECNamedCurveSpec(final String name, final EllipticCurve ellipticCurve, final java.security.spec.ECPoint ecPoint, final BigInteger bigInteger) {
        super(ellipticCurve, ecPoint, bigInteger, 1);
        this.name = name;
    }
    
    public ECNamedCurveSpec(final String name, final ECCurve ecCurve, final ECPoint ecPoint, final BigInteger bigInteger, final BigInteger bigInteger2) {
        super(convertCurve(ecCurve, null), EC5Util.convertPoint(ecPoint), bigInteger, bigInteger2.intValue());
        this.name = name;
    }
    
    public ECNamedCurveSpec(final String name, final EllipticCurve ellipticCurve, final java.security.spec.ECPoint ecPoint, final BigInteger bigInteger, final BigInteger bigInteger2) {
        super(ellipticCurve, ecPoint, bigInteger, bigInteger2.intValue());
        this.name = name;
    }
    
    public ECNamedCurveSpec(final String name, final ECCurve ecCurve, final ECPoint ecPoint, final BigInteger bigInteger, final BigInteger bigInteger2, final byte[] array) {
        super(convertCurve(ecCurve, array), EC5Util.convertPoint(ecPoint), bigInteger, bigInteger2.intValue());
        this.name = name;
    }
    
    public String getName() {
        return this.name;
    }
}
