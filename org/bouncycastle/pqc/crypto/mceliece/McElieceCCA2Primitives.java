package org.bouncycastle.pqc.crypto.mceliece;

import org.bouncycastle.pqc.math.linearalgebra.GF2Matrix;
import org.bouncycastle.pqc.math.linearalgebra.PolynomialGF2mSmallM;
import org.bouncycastle.pqc.math.linearalgebra.GF2mField;
import org.bouncycastle.pqc.math.linearalgebra.Permutation;
import org.bouncycastle.pqc.math.linearalgebra.GoppaCode;
import org.bouncycastle.pqc.math.linearalgebra.Vector;
import org.bouncycastle.pqc.math.linearalgebra.GF2Vector;

final class McElieceCCA2Primitives
{
    private McElieceCCA2Primitives() {
    }
    
    public static GF2Vector encryptionPrimitive(final McElieceCCA2PublicKeyParameters mcElieceCCA2PublicKeyParameters, final GF2Vector gf2Vector, final GF2Vector gf2Vector2) {
        return (GF2Vector)mcElieceCCA2PublicKeyParameters.getG().leftMultiplyLeftCompactForm(gf2Vector).add(gf2Vector2);
    }
    
    public static GF2Vector[] decryptionPrimitive(final McElieceCCA2PrivateKeyParameters mcElieceCCA2PrivateKeyParameters, final GF2Vector gf2Vector) {
        final int k = mcElieceCCA2PrivateKeyParameters.getK();
        final Permutation p2 = mcElieceCCA2PrivateKeyParameters.getP();
        final GF2mField field = mcElieceCCA2PrivateKeyParameters.getField();
        final PolynomialGF2mSmallM goppaPoly = mcElieceCCA2PrivateKeyParameters.getGoppaPoly();
        final GF2Matrix h = mcElieceCCA2PrivateKeyParameters.getH();
        final PolynomialGF2mSmallM[] qInv = mcElieceCCA2PrivateKeyParameters.getQInv();
        final GF2Vector gf2Vector2 = (GF2Vector)gf2Vector.multiply(p2.computeInverse());
        final GF2Vector syndromeDecode = GoppaCode.syndromeDecode((GF2Vector)h.rightMultiply(gf2Vector2), field, goppaPoly, qInv);
        return new GF2Vector[] { ((GF2Vector)((GF2Vector)gf2Vector2.add(syndromeDecode)).multiply(p2)).extractRightVector(k), (GF2Vector)syndromeDecode.multiply(p2) };
    }
}
