package org.bouncycastle.math.ec.tools;

import org.bouncycastle.util.BigIntegers;
import java.security.SecureRandom;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.asn1.x9.ECNamedCurveTable;
import java.math.BigInteger;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECConstants;
import org.bouncycastle.math.ec.ECAlgorithms;
import org.bouncycastle.asn1.x9.X9ECParameters;

public class DiscoverEndomorphisms
{
    private static final int radix = 16;
    
    public static void main(final String[] array) {
        if (array.length < 1) {
            System.err.println("Expected a list of curve names as arguments");
            return;
        }
        for (int i = 0; i < array.length; ++i) {
            discoverEndomorphisms(array[i]);
        }
    }
    
    public static void discoverEndomorphisms(final X9ECParameters x9ECParameters) {
        if (x9ECParameters == null) {
            throw new NullPointerException("x9");
        }
        final ECCurve curve = x9ECParameters.getCurve();
        if (ECAlgorithms.isFpCurve(curve)) {
            final BigInteger characteristic = curve.getField().getCharacteristic();
            if (curve.getA().isZero() && characteristic.mod(ECConstants.THREE).equals(ECConstants.ONE)) {
                System.out.println("Curve has a 'GLV Type B' endomorphism with these parameters:");
                printGLVTypeBParameters(x9ECParameters);
            }
        }
    }
    
    private static void discoverEndomorphisms(final String s) {
        final X9ECParameters byName = ECNamedCurveTable.getByName(s);
        if (byName == null) {
            System.err.println("Unknown curve: " + s);
            return;
        }
        final ECCurve curve = byName.getCurve();
        if (ECAlgorithms.isFpCurve(curve)) {
            final BigInteger characteristic = curve.getField().getCharacteristic();
            if (curve.getA().isZero() && characteristic.mod(ECConstants.THREE).equals(ECConstants.ONE)) {
                System.out.println("Curve '" + s + "' has a 'GLV Type B' endomorphism with these parameters:");
                printGLVTypeBParameters(byName);
            }
        }
    }
    
    private static void printGLVTypeBParameters(final X9ECParameters x9ECParameters) {
        final BigInteger[] solveQuadraticEquation = solveQuadraticEquation(x9ECParameters.getN(), ECConstants.ONE, ECConstants.ONE);
        final ECFieldElement[] betaValues = findBetaValues(x9ECParameters.getCurve());
        printGLVTypeBParameters(x9ECParameters, solveQuadraticEquation[0], betaValues);
        System.out.println("OR");
        printGLVTypeBParameters(x9ECParameters, solveQuadraticEquation[1], betaValues);
    }
    
    private static void printGLVTypeBParameters(final X9ECParameters x9ECParameters, final BigInteger bigInteger, final ECFieldElement[] array) {
        final ECPoint normalize = x9ECParameters.getG().normalize();
        final ECPoint normalize2 = normalize.multiply(bigInteger).normalize();
        if (!normalize.getYCoord().equals(normalize2.getYCoord())) {
            throw new IllegalStateException("Derivation of GLV Type B parameters failed unexpectedly");
        }
        ECFieldElement ecFieldElement = array[0];
        if (!normalize.getXCoord().multiply(ecFieldElement).equals(normalize2.getXCoord())) {
            ecFieldElement = array[1];
            if (!normalize.getXCoord().multiply(ecFieldElement).equals(normalize2.getXCoord())) {
                throw new IllegalStateException("Derivation of GLV Type B parameters failed unexpectedly");
            }
        }
        final BigInteger n = x9ECParameters.getN();
        final BigInteger[] extEuclidGLV = extEuclidGLV(n, bigInteger);
        final BigInteger[] array2 = { extEuclidGLV[2], extEuclidGLV[3].negate() };
        BigInteger[] chooseShortest = chooseShortest(new BigInteger[] { extEuclidGLV[0], extEuclidGLV[1].negate() }, new BigInteger[] { extEuclidGLV[4], extEuclidGLV[5].negate() });
        if (!isVectorBoundedBySqrt(chooseShortest, n) && areRelativelyPrime(array2[0], array2[1])) {
            final BigInteger bigInteger2 = array2[0];
            final BigInteger bigInteger3 = array2[1];
            final BigInteger divide = bigInteger2.add(bigInteger3.multiply(bigInteger)).divide(n);
            final BigInteger[] extEuclidBezout = extEuclidBezout(new BigInteger[] { divide.abs(), bigInteger3.abs() });
            if (extEuclidBezout != null) {
                BigInteger negate = extEuclidBezout[0];
                BigInteger negate2 = extEuclidBezout[1];
                if (divide.signum() < 0) {
                    negate = negate.negate();
                }
                if (bigInteger3.signum() > 0) {
                    negate2 = negate2.negate();
                }
                if (!divide.multiply(negate).subtract(bigInteger3.multiply(negate2)).equals(ECConstants.ONE)) {
                    throw new IllegalStateException();
                }
                final BigInteger subtract = negate2.multiply(n).subtract(negate.multiply(bigInteger));
                final BigInteger negate3 = negate.negate();
                final BigInteger negate4 = subtract.negate();
                final BigInteger add = isqrt(n.subtract(ECConstants.ONE)).add(ECConstants.ONE);
                final BigInteger[] intersect = intersect(calculateRange(negate3, add, bigInteger3), calculateRange(negate4, add, bigInteger2));
                if (intersect != null) {
                    for (BigInteger add2 = intersect[0]; add2.compareTo(intersect[1]) <= 0; add2 = add2.add(ECConstants.ONE)) {
                        final BigInteger[] array3 = { subtract.add(add2.multiply(bigInteger2)), negate.add(add2.multiply(bigInteger3)) };
                        if (isShorter(array3, chooseShortest)) {
                            chooseShortest = array3;
                        }
                    }
                }
            }
        }
        final BigInteger subtract2 = array2[0].multiply(chooseShortest[1]).subtract(array2[1].multiply(chooseShortest[0]));
        final int n2 = n.bitLength() + 16 - (n.bitLength() & 0x7);
        final BigInteger roundQuotient = roundQuotient(chooseShortest[1].shiftLeft(n2), subtract2);
        final BigInteger negate5 = roundQuotient(array2[1].shiftLeft(n2), subtract2).negate();
        printProperty("Beta", ecFieldElement.toBigInteger().toString(16));
        printProperty("Lambda", bigInteger.toString(16));
        printProperty("v1", "{ " + array2[0].toString(16) + ", " + array2[1].toString(16) + " }");
        printProperty("v2", "{ " + chooseShortest[0].toString(16) + ", " + chooseShortest[1].toString(16) + " }");
        printProperty("d", subtract2.toString(16));
        printProperty("(OPT) g1", roundQuotient.toString(16));
        printProperty("(OPT) g2", negate5.toString(16));
        printProperty("(OPT) bits", Integer.toString(n2));
    }
    
    private static void printProperty(final String s, final Object o) {
        final StringBuffer sb = new StringBuffer("  ");
        sb.append(s);
        while (sb.length() < 20) {
            sb.append(' ');
        }
        sb.append("= ");
        sb.append(o.toString());
        System.out.println(sb.toString());
    }
    
    private static boolean areRelativelyPrime(final BigInteger bigInteger, final BigInteger bigInteger2) {
        return bigInteger.gcd(bigInteger2).equals(ECConstants.ONE);
    }
    
    private static BigInteger[] calculateRange(final BigInteger bigInteger, final BigInteger bigInteger2, final BigInteger bigInteger3) {
        return order(bigInteger.subtract(bigInteger2).divide(bigInteger3), bigInteger.add(bigInteger2).divide(bigInteger3));
    }
    
    private static BigInteger[] extEuclidBezout(final BigInteger[] array) {
        final boolean b = array[0].compareTo(array[1]) < 0;
        if (b) {
            swap(array);
        }
        BigInteger bigInteger = array[0];
        BigInteger bigInteger2 = array[1];
        BigInteger one = ECConstants.ONE;
        BigInteger zero = ECConstants.ZERO;
        BigInteger zero2 = ECConstants.ZERO;
        BigInteger one2;
        BigInteger bigInteger4;
        BigInteger subtract;
        BigInteger subtract2;
        for (one2 = ECConstants.ONE; bigInteger2.compareTo(ECConstants.ONE) > 0; bigInteger2 = bigInteger4, one = zero, zero = subtract, zero2 = one2, one2 = subtract2) {
            final BigInteger[] divideAndRemainder = bigInteger.divideAndRemainder(bigInteger2);
            final BigInteger bigInteger3 = divideAndRemainder[0];
            bigInteger4 = divideAndRemainder[1];
            subtract = one.subtract(bigInteger3.multiply(zero));
            subtract2 = zero2.subtract(bigInteger3.multiply(one2));
            bigInteger = bigInteger2;
        }
        if (bigInteger2.signum() <= 0) {
            return null;
        }
        final BigInteger[] array2 = { zero, one2 };
        if (b) {
            swap(array2);
        }
        return array2;
    }
    
    private static BigInteger[] extEuclidGLV(final BigInteger bigInteger, final BigInteger bigInteger2) {
        BigInteger bigInteger3 = bigInteger;
        BigInteger bigInteger4 = bigInteger2;
        BigInteger zero = ECConstants.ZERO;
        BigInteger one = ECConstants.ONE;
        BigInteger bigInteger6;
        BigInteger subtract;
        while (true) {
            final BigInteger[] divideAndRemainder = bigInteger3.divideAndRemainder(bigInteger4);
            final BigInteger bigInteger5 = divideAndRemainder[0];
            bigInteger6 = divideAndRemainder[1];
            subtract = zero.subtract(bigInteger5.multiply(one));
            if (isLessThanSqrt(bigInteger4, bigInteger)) {
                break;
            }
            bigInteger3 = bigInteger4;
            bigInteger4 = bigInteger6;
            zero = one;
            one = subtract;
        }
        return new BigInteger[] { bigInteger3, zero, bigInteger4, one, bigInteger6, subtract };
    }
    
    private static BigInteger[] chooseShortest(final BigInteger[] array, final BigInteger[] array2) {
        return isShorter(array, array2) ? array : array2;
    }
    
    private static BigInteger[] intersect(final BigInteger[] array, final BigInteger[] array2) {
        final BigInteger max = array[0].max(array2[0]);
        final BigInteger min = array[1].min(array2[1]);
        if (max.compareTo(min) > 0) {
            return null;
        }
        return new BigInteger[] { max, min };
    }
    
    private static boolean isLessThanSqrt(BigInteger abs, BigInteger abs2) {
        abs = abs.abs();
        abs2 = abs2.abs();
        final int bitLength = abs2.bitLength();
        final int n = abs.bitLength() * 2;
        return n - 1 <= bitLength && (n < bitLength || abs.multiply(abs).compareTo(abs2) < 0);
    }
    
    private static boolean isShorter(final BigInteger[] array, final BigInteger[] array2) {
        final BigInteger abs = array[0].abs();
        final BigInteger abs2 = array[1].abs();
        final BigInteger abs3 = array2[0].abs();
        final BigInteger abs4 = array2[1].abs();
        final boolean b = abs.compareTo(abs3) < 0;
        if (b == abs2.compareTo(abs4) < 0) {
            return b;
        }
        return abs.multiply(abs).add(abs2.multiply(abs2)).compareTo(abs3.multiply(abs3).add(abs4.multiply(abs4))) < 0;
    }
    
    private static boolean isVectorBoundedBySqrt(final BigInteger[] array, final BigInteger bigInteger) {
        return isLessThanSqrt(array[0].abs().max(array[1].abs()), bigInteger);
    }
    
    private static BigInteger[] order(final BigInteger bigInteger, final BigInteger bigInteger2) {
        if (bigInteger.compareTo(bigInteger2) <= 0) {
            return new BigInteger[] { bigInteger, bigInteger2 };
        }
        return new BigInteger[] { bigInteger2, bigInteger };
    }
    
    private static BigInteger roundQuotient(BigInteger abs, BigInteger abs2) {
        final boolean b = abs.signum() != abs2.signum();
        abs = abs.abs();
        abs2 = abs2.abs();
        final BigInteger divide = abs.add(abs2.shiftRight(1)).divide(abs2);
        return b ? divide.negate() : divide;
    }
    
    private static BigInteger[] solveQuadraticEquation(final BigInteger bigInteger, final BigInteger bigInteger2, final BigInteger bigInteger3) {
        BigInteger bigInteger4 = new ECFieldElement.Fp(bigInteger, bigInteger2.multiply(bigInteger2).subtract(bigInteger3.shiftLeft(2)).mod(bigInteger)).sqrt().toBigInteger();
        BigInteger bigInteger5 = bigInteger.subtract(bigInteger4);
        if (bigInteger4.testBit(0)) {
            bigInteger5 = bigInteger5.add(bigInteger);
        }
        else {
            bigInteger4 = bigInteger4.add(bigInteger);
        }
        return new BigInteger[] { bigInteger4.shiftRight(1), bigInteger5.shiftRight(1) };
    }
    
    private static ECFieldElement[] findBetaValues(final ECCurve ecCurve) {
        final BigInteger characteristic = ecCurve.getField().getCharacteristic();
        final BigInteger divide = characteristic.divide(ECConstants.THREE);
        final SecureRandom secureRandom = new SecureRandom();
        BigInteger modPow;
        do {
            modPow = BigIntegers.createRandomInRange(ECConstants.TWO, characteristic.subtract(ECConstants.TWO), secureRandom).modPow(divide, characteristic);
        } while (modPow.equals(ECConstants.ONE));
        final ECFieldElement fromBigInteger = ecCurve.fromBigInteger(modPow);
        return new ECFieldElement[] { fromBigInteger, fromBigInteger.square() };
    }
    
    private static BigInteger isqrt(final BigInteger bigInteger) {
        BigInteger shiftRight = bigInteger.shiftRight(bigInteger.bitLength() / 2);
        BigInteger shiftRight2;
        while (true) {
            shiftRight2 = shiftRight.add(bigInteger.divide(shiftRight)).shiftRight(1);
            if (shiftRight2.equals(shiftRight)) {
                break;
            }
            shiftRight = shiftRight2;
        }
        return shiftRight2;
    }
    
    private static void swap(final BigInteger[] array) {
        final BigInteger bigInteger = array[0];
        array[0] = array[1];
        array[1] = bigInteger;
    }
}
