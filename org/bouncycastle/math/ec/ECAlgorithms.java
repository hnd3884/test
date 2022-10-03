package org.bouncycastle.math.ec;

import org.bouncycastle.math.ec.endo.ECEndomorphism;
import org.bouncycastle.math.ec.endo.GLVEndomorphism;
import java.math.BigInteger;
import org.bouncycastle.math.field.PolynomialExtensionField;
import org.bouncycastle.math.field.FiniteField;

public class ECAlgorithms
{
    public static boolean isF2mCurve(final ECCurve ecCurve) {
        return isF2mField(ecCurve.getField());
    }
    
    public static boolean isF2mField(final FiniteField finiteField) {
        return finiteField.getDimension() > 1 && finiteField.getCharacteristic().equals(ECConstants.TWO) && finiteField instanceof PolynomialExtensionField;
    }
    
    public static boolean isFpCurve(final ECCurve ecCurve) {
        return isFpField(ecCurve.getField());
    }
    
    public static boolean isFpField(final FiniteField finiteField) {
        return finiteField.getDimension() == 1;
    }
    
    public static ECPoint sumOfMultiplies(final ECPoint[] array, final BigInteger[] array2) {
        if (array == null || array2 == null || array.length != array2.length || array.length < 1) {
            throw new IllegalArgumentException("point and scalar arrays should be non-null, and of equal, non-zero, length");
        }
        final int length = array.length;
        switch (length) {
            case 1: {
                return array[0].multiply(array2[0]);
            }
            case 2: {
                return sumOfTwoMultiplies(array[0], array2[0], array[1], array2[1]);
            }
            default: {
                final ECPoint ecPoint = array[0];
                final ECCurve curve = ecPoint.getCurve();
                final ECPoint[] array3 = new ECPoint[length];
                array3[0] = ecPoint;
                for (int i = 1; i < length; ++i) {
                    array3[i] = importPoint(curve, array[i]);
                }
                final ECEndomorphism endomorphism = curve.getEndomorphism();
                if (endomorphism instanceof GLVEndomorphism) {
                    return validatePoint(implSumOfMultipliesGLV(array3, array2, (GLVEndomorphism)endomorphism));
                }
                return validatePoint(implSumOfMultiplies(array3, array2));
            }
        }
    }
    
    public static ECPoint sumOfTwoMultiplies(final ECPoint ecPoint, final BigInteger bigInteger, ECPoint importPoint, final BigInteger bigInteger2) {
        final ECCurve curve = ecPoint.getCurve();
        importPoint = importPoint(curve, importPoint);
        if (curve instanceof ECCurve.AbstractF2m && ((ECCurve.AbstractF2m)curve).isKoblitz()) {
            return validatePoint(ecPoint.multiply(bigInteger).add(importPoint.multiply(bigInteger2)));
        }
        final ECEndomorphism endomorphism = curve.getEndomorphism();
        if (endomorphism instanceof GLVEndomorphism) {
            return validatePoint(implSumOfMultipliesGLV(new ECPoint[] { ecPoint, importPoint }, new BigInteger[] { bigInteger, bigInteger2 }, (GLVEndomorphism)endomorphism));
        }
        return validatePoint(implShamirsTrickWNaf(ecPoint, bigInteger, importPoint, bigInteger2));
    }
    
    public static ECPoint shamirsTrick(final ECPoint ecPoint, final BigInteger bigInteger, ECPoint importPoint, final BigInteger bigInteger2) {
        importPoint = importPoint(ecPoint.getCurve(), importPoint);
        return validatePoint(implShamirsTrickJsf(ecPoint, bigInteger, importPoint, bigInteger2));
    }
    
    public static ECPoint importPoint(final ECCurve ecCurve, final ECPoint ecPoint) {
        if (!ecCurve.equals(ecPoint.getCurve())) {
            throw new IllegalArgumentException("Point must be on the same curve");
        }
        return ecCurve.importPoint(ecPoint);
    }
    
    public static void montgomeryTrick(final ECFieldElement[] array, final int n, final int n2) {
        montgomeryTrick(array, n, n2, null);
    }
    
    public static void montgomeryTrick(final ECFieldElement[] array, final int n, final int n2, final ECFieldElement ecFieldElement) {
        final ECFieldElement[] array2 = new ECFieldElement[n2];
        array2[0] = array[n];
        int i = 0;
        while (++i < n2) {
            array2[i] = array2[i - 1].multiply(array[n + i]);
        }
        --i;
        if (ecFieldElement != null) {
            array2[i] = array2[i].multiply(ecFieldElement);
        }
        ECFieldElement ecFieldElement2;
        int n3;
        ECFieldElement ecFieldElement3;
        for (ecFieldElement2 = array2[i].invert(); i > 0; n3 = n + i--, ecFieldElement3 = array[n3], array[n3] = array2[i].multiply(ecFieldElement2), ecFieldElement2 = ecFieldElement2.multiply(ecFieldElement3)) {}
        array[n] = ecFieldElement2;
    }
    
    public static ECPoint referenceMultiply(ECPoint twice, final BigInteger bigInteger) {
        final BigInteger abs = bigInteger.abs();
        ECPoint ecPoint = twice.getCurve().getInfinity();
        final int bitLength = abs.bitLength();
        if (bitLength > 0) {
            if (abs.testBit(0)) {
                ecPoint = twice;
            }
            for (int i = 1; i < bitLength; ++i) {
                twice = twice.twice();
                if (abs.testBit(i)) {
                    ecPoint = ecPoint.add(twice);
                }
            }
        }
        return (bigInteger.signum() < 0) ? ecPoint.negate() : ecPoint;
    }
    
    public static ECPoint validatePoint(final ECPoint ecPoint) {
        if (!ecPoint.isValid()) {
            throw new IllegalArgumentException("Invalid point");
        }
        return ecPoint;
    }
    
    static ECPoint implShamirsTrickJsf(final ECPoint ecPoint, final BigInteger bigInteger, final ECPoint ecPoint2, final BigInteger bigInteger2) {
        final ECCurve curve = ecPoint.getCurve();
        final ECPoint infinity = curve.getInfinity();
        final ECPoint[] array = { ecPoint2, ecPoint.subtract(ecPoint2), ecPoint, ecPoint.add(ecPoint2) };
        curve.normalizeAll(array);
        final ECPoint[] array2 = { array[3].negate(), array[2].negate(), array[1].negate(), array[0].negate(), infinity, array[0], array[1], array[2], array[3] };
        final byte[] generateJSF = WNafUtil.generateJSF(bigInteger, bigInteger2);
        ECPoint twicePlus = infinity;
        int length = generateJSF.length;
        while (--length >= 0) {
            final byte b = generateJSF[length];
            twicePlus = twicePlus.twicePlus(array2[4 + (b << 24 >> 28) * 3 + (b << 28 >> 28)]);
        }
        return twicePlus;
    }
    
    static ECPoint implShamirsTrickWNaf(final ECPoint ecPoint, BigInteger abs, final ECPoint ecPoint2, BigInteger abs2) {
        final boolean b = abs.signum() < 0;
        final boolean b2 = abs2.signum() < 0;
        abs = abs.abs();
        abs2 = abs2.abs();
        final int max = Math.max(2, Math.min(16, WNafUtil.getWindowSize(abs.bitLength())));
        final int max2 = Math.max(2, Math.min(16, WNafUtil.getWindowSize(abs2.bitLength())));
        final WNafPreCompInfo precompute = WNafUtil.precompute(ecPoint, max, true);
        final WNafPreCompInfo precompute2 = WNafUtil.precompute(ecPoint2, max2, true);
        return implShamirsTrickWNaf(b ? precompute.getPreCompNeg() : precompute.getPreComp(), b ? precompute.getPreComp() : precompute.getPreCompNeg(), WNafUtil.generateWindowNaf(max, abs), b2 ? precompute2.getPreCompNeg() : precompute2.getPreComp(), b2 ? precompute2.getPreComp() : precompute2.getPreCompNeg(), WNafUtil.generateWindowNaf(max2, abs2));
    }
    
    static ECPoint implShamirsTrickWNaf(final ECPoint ecPoint, BigInteger abs, final ECPointMap ecPointMap, BigInteger abs2) {
        final boolean b = abs.signum() < 0;
        final boolean b2 = abs2.signum() < 0;
        abs = abs.abs();
        abs2 = abs2.abs();
        final int max = Math.max(2, Math.min(16, WNafUtil.getWindowSize(Math.max(abs.bitLength(), abs2.bitLength()))));
        final ECPoint mapPointWithPrecomp = WNafUtil.mapPointWithPrecomp(ecPoint, max, true, ecPointMap);
        final WNafPreCompInfo wNafPreCompInfo = WNafUtil.getWNafPreCompInfo(ecPoint);
        final WNafPreCompInfo wNafPreCompInfo2 = WNafUtil.getWNafPreCompInfo(mapPointWithPrecomp);
        return implShamirsTrickWNaf(b ? wNafPreCompInfo.getPreCompNeg() : wNafPreCompInfo.getPreComp(), b ? wNafPreCompInfo.getPreComp() : wNafPreCompInfo.getPreCompNeg(), WNafUtil.generateWindowNaf(max, abs), b2 ? wNafPreCompInfo2.getPreCompNeg() : wNafPreCompInfo2.getPreComp(), b2 ? wNafPreCompInfo2.getPreComp() : wNafPreCompInfo2.getPreCompNeg(), WNafUtil.generateWindowNaf(max, abs2));
    }
    
    private static ECPoint implShamirsTrickWNaf(final ECPoint[] array, final ECPoint[] array2, final byte[] array3, final ECPoint[] array4, final ECPoint[] array5, final byte[] array6) {
        final int max = Math.max(array3.length, array6.length);
        ECPoint ecPoint2;
        final ECPoint ecPoint = ecPoint2 = array[0].getCurve().getInfinity();
        int n = 0;
        for (int i = max - 1; i >= 0; --i) {
            final byte b = (byte)((i < array3.length) ? array3[i] : 0);
            final byte b2 = (byte)((i < array6.length) ? array6[i] : 0);
            if ((b | b2) == 0x0) {
                ++n;
            }
            else {
                ECPoint ecPoint3 = ecPoint;
                if (b != 0) {
                    ecPoint3 = ecPoint3.add(((b < 0) ? array2 : array)[Math.abs(b) >>> 1]);
                }
                if (b2 != 0) {
                    ecPoint3 = ecPoint3.add(((b2 < 0) ? array5 : array4)[Math.abs(b2) >>> 1]);
                }
                if (n > 0) {
                    ecPoint2 = ecPoint2.timesPow2(n);
                    n = 0;
                }
                ecPoint2 = ecPoint2.twicePlus(ecPoint3);
            }
        }
        if (n > 0) {
            ecPoint2 = ecPoint2.timesPow2(n);
        }
        return ecPoint2;
    }
    
    static ECPoint implSumOfMultiplies(final ECPoint[] array, final BigInteger[] array2) {
        final int length = array.length;
        final boolean[] array3 = new boolean[length];
        final WNafPreCompInfo[] array4 = new WNafPreCompInfo[length];
        final byte[][] array5 = new byte[length][];
        for (int i = 0; i < length; ++i) {
            final BigInteger bigInteger = array2[i];
            array3[i] = (bigInteger.signum() < 0);
            final BigInteger abs = bigInteger.abs();
            final int max = Math.max(2, Math.min(16, WNafUtil.getWindowSize(abs.bitLength())));
            array4[i] = WNafUtil.precompute(array[i], max, true);
            array5[i] = WNafUtil.generateWindowNaf(max, abs);
        }
        return implSumOfMultiplies(array3, array4, array5);
    }
    
    static ECPoint implSumOfMultipliesGLV(final ECPoint[] array, final BigInteger[] array2, final GLVEndomorphism glvEndomorphism) {
        final BigInteger order = array[0].getCurve().getOrder();
        final int length = array.length;
        final BigInteger[] array3 = new BigInteger[length << 1];
        int i = 0;
        int n = 0;
        while (i < length) {
            final BigInteger[] decomposeScalar = glvEndomorphism.decomposeScalar(array2[i].mod(order));
            array3[n++] = decomposeScalar[0];
            array3[n++] = decomposeScalar[1];
            ++i;
        }
        final ECPointMap pointMap = glvEndomorphism.getPointMap();
        if (glvEndomorphism.hasEfficientPointMap()) {
            return implSumOfMultiplies(array, pointMap, array3);
        }
        final ECPoint[] array4 = new ECPoint[length << 1];
        int j = 0;
        int n2 = 0;
        while (j < length) {
            final ECPoint ecPoint = array[j];
            final ECPoint map = pointMap.map(ecPoint);
            array4[n2++] = ecPoint;
            array4[n2++] = map;
            ++j;
        }
        return implSumOfMultiplies(array4, array3);
    }
    
    static ECPoint implSumOfMultiplies(final ECPoint[] array, final ECPointMap ecPointMap, final BigInteger[] array2) {
        final int length = array.length;
        final int n = length << 1;
        final boolean[] array3 = new boolean[n];
        final WNafPreCompInfo[] array4 = new WNafPreCompInfo[n];
        final byte[][] array5 = new byte[n][];
        for (int i = 0; i < length; ++i) {
            final int n2 = i << 1;
            final int n3 = n2 + 1;
            final BigInteger bigInteger = array2[n2];
            array3[n2] = (bigInteger.signum() < 0);
            final BigInteger abs = bigInteger.abs();
            final BigInteger bigInteger2 = array2[n3];
            array3[n3] = (bigInteger2.signum() < 0);
            final BigInteger abs2 = bigInteger2.abs();
            final int max = Math.max(2, Math.min(16, WNafUtil.getWindowSize(Math.max(abs.bitLength(), abs2.bitLength()))));
            final ECPoint ecPoint = array[i];
            final ECPoint mapPointWithPrecomp = WNafUtil.mapPointWithPrecomp(ecPoint, max, true, ecPointMap);
            array4[n2] = WNafUtil.getWNafPreCompInfo(ecPoint);
            array4[n3] = WNafUtil.getWNafPreCompInfo(mapPointWithPrecomp);
            array5[n2] = WNafUtil.generateWindowNaf(max, abs);
            array5[n3] = WNafUtil.generateWindowNaf(max, abs2);
        }
        return implSumOfMultiplies(array3, array4, array5);
    }
    
    private static ECPoint implSumOfMultiplies(final boolean[] array, final WNafPreCompInfo[] array2, final byte[][] array3) {
        int max = 0;
        final int length = array3.length;
        for (int i = 0; i < length; ++i) {
            max = Math.max(max, array3[i].length);
        }
        ECPoint ecPoint2;
        final ECPoint ecPoint = ecPoint2 = array2[0].getPreComp()[0].getCurve().getInfinity();
        int n = 0;
        for (int j = max - 1; j >= 0; --j) {
            ECPoint add = ecPoint;
            for (int k = 0; k < length; ++k) {
                final byte[] array4 = array3[k];
                final byte b = (byte)((j < array4.length) ? array4[j] : 0);
                if (b != 0) {
                    final int abs = Math.abs(b);
                    final WNafPreCompInfo wNafPreCompInfo = array2[k];
                    add = add.add(((b < 0 == array[k]) ? wNafPreCompInfo.getPreComp() : wNafPreCompInfo.getPreCompNeg())[abs >>> 1]);
                }
            }
            if (add == ecPoint) {
                ++n;
            }
            else {
                if (n > 0) {
                    ecPoint2 = ecPoint2.timesPow2(n);
                    n = 0;
                }
                ecPoint2 = ecPoint2.twicePlus(add);
            }
        }
        if (n > 0) {
            ecPoint2 = ecPoint2.timesPow2(n);
        }
        return ecPoint2;
    }
}
