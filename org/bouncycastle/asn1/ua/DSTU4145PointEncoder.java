package org.bouncycastle.asn1.ua;

import org.bouncycastle.math.ec.ECPoint;
import java.math.BigInteger;
import java.util.Random;
import org.bouncycastle.math.ec.ECConstants;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECFieldElement;

public abstract class DSTU4145PointEncoder
{
    private static ECFieldElement trace(final ECFieldElement ecFieldElement) {
        ECFieldElement add = ecFieldElement;
        for (int i = 1; i < ecFieldElement.getFieldSize(); ++i) {
            add = add.square().add(ecFieldElement);
        }
        return add;
    }
    
    private static ECFieldElement solveQuadraticEquation(final ECCurve ecCurve, final ECFieldElement ecFieldElement) {
        if (ecFieldElement.isZero()) {
            return ecFieldElement;
        }
        final ECFieldElement fromBigInteger = ecCurve.fromBigInteger(ECConstants.ZERO);
        final Random random = new Random();
        final int fieldSize = ecFieldElement.getFieldSize();
        ECFieldElement add;
        do {
            final ECFieldElement fromBigInteger2 = ecCurve.fromBigInteger(new BigInteger(fieldSize, random));
            add = fromBigInteger;
            ECFieldElement add2 = ecFieldElement;
            for (int i = 1; i <= fieldSize - 1; ++i) {
                final ECFieldElement square = add2.square();
                add = add.square().add(square.multiply(fromBigInteger2));
                add2 = square.add(ecFieldElement);
            }
            if (!add2.isZero()) {
                return null;
            }
        } while (add.square().add(add).isZero());
        return add;
    }
    
    public static byte[] encodePoint(ECPoint normalize) {
        normalize = normalize.normalize();
        final ECFieldElement affineXCoord = normalize.getAffineXCoord();
        final byte[] encoded = affineXCoord.getEncoded();
        if (!affineXCoord.isZero()) {
            if (trace(normalize.getAffineYCoord().divide(affineXCoord)).isOne()) {
                final byte[] array = encoded;
                final int n = encoded.length - 1;
                array[n] |= 0x1;
            }
            else {
                final byte[] array2 = encoded;
                final int n2 = encoded.length - 1;
                array2[n2] &= (byte)254;
            }
        }
        return encoded;
    }
    
    public static ECPoint decodePoint(final ECCurve ecCurve, final byte[] array) {
        final ECFieldElement fromBigInteger = ecCurve.fromBigInteger(BigInteger.valueOf(array[array.length - 1] & 0x1));
        ECFieldElement ecFieldElement = ecCurve.fromBigInteger(new BigInteger(1, array));
        if (!trace(ecFieldElement).equals(ecCurve.getA())) {
            ecFieldElement = ecFieldElement.addOne();
        }
        ECFieldElement ecFieldElement2 = null;
        if (ecFieldElement.isZero()) {
            ecFieldElement2 = ecCurve.getB().sqrt();
        }
        else {
            ECFieldElement ecFieldElement3 = solveQuadraticEquation(ecCurve, ecFieldElement.square().invert().multiply(ecCurve.getB()).add(ecCurve.getA()).add(ecFieldElement));
            if (ecFieldElement3 != null) {
                if (!trace(ecFieldElement3).equals(fromBigInteger)) {
                    ecFieldElement3 = ecFieldElement3.addOne();
                }
                ecFieldElement2 = ecFieldElement.multiply(ecFieldElement3);
            }
        }
        if (ecFieldElement2 == null) {
            throw new IllegalArgumentException("Invalid point compression");
        }
        return ecCurve.validatePoint(ecFieldElement.toBigInteger(), ecFieldElement2.toBigInteger());
    }
}
