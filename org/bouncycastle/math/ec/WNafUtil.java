package org.bouncycastle.math.ec;

import java.math.BigInteger;

public abstract class WNafUtil
{
    public static final String PRECOMP_NAME = "bc_wnaf";
    private static final int[] DEFAULT_WINDOW_SIZE_CUTOFFS;
    private static final byte[] EMPTY_BYTES;
    private static final int[] EMPTY_INTS;
    private static final ECPoint[] EMPTY_POINTS;
    
    public static int[] generateCompactNaf(final BigInteger bigInteger) {
        if (bigInteger.bitLength() >>> 16 != 0) {
            throw new IllegalArgumentException("'k' must have bitlength < 2^16");
        }
        if (bigInteger.signum() == 0) {
            return WNafUtil.EMPTY_INTS;
        }
        final BigInteger add = bigInteger.shiftLeft(1).add(bigInteger);
        final int bitLength = add.bitLength();
        int[] trim = new int[bitLength >> 1];
        final BigInteger xor = add.xor(bigInteger);
        final int n = bitLength - 1;
        int n2 = 0;
        int n3 = 0;
        for (int i = 1; i < n; ++i) {
            if (!xor.testBit(i)) {
                ++n3;
            }
            else {
                trim[n2++] = ((bigInteger.testBit(i) ? -1 : 1) << 16 | n3);
                n3 = 1;
                ++i;
            }
        }
        trim[n2++] = (0x10000 | n3);
        if (trim.length > n2) {
            trim = trim(trim, n2);
        }
        return trim;
    }
    
    public static int[] generateCompactWindowNaf(final int n, BigInteger shiftRight) {
        if (n == 2) {
            return generateCompactNaf(shiftRight);
        }
        if (n < 2 || n > 16) {
            throw new IllegalArgumentException("'width' must be in the range [2, 16]");
        }
        if (shiftRight.bitLength() >>> 16 != 0) {
            throw new IllegalArgumentException("'k' must have bitlength < 2^16");
        }
        if (shiftRight.signum() == 0) {
            return WNafUtil.EMPTY_INTS;
        }
        int[] trim = new int[shiftRight.bitLength() / n + 1];
        final int n2 = 1 << n;
        final int n3 = n2 - 1;
        final int n4 = n2 >>> 1;
        boolean b = false;
        int n5 = 0;
        int i = 0;
        while (i <= shiftRight.bitLength()) {
            if (shiftRight.testBit(i) == b) {
                ++i;
            }
            else {
                shiftRight = shiftRight.shiftRight(i);
                int n6 = shiftRight.intValue() & n3;
                if (b) {
                    ++n6;
                }
                b = ((n6 & n4) != 0x0);
                if (b) {
                    n6 -= n2;
                }
                trim[n5++] = (n6 << 16 | ((n5 > 0) ? (i - 1) : i));
                i = n;
            }
        }
        if (trim.length > n5) {
            trim = trim(trim, n5);
        }
        return trim;
    }
    
    public static byte[] generateJSF(final BigInteger bigInteger, final BigInteger bigInteger2) {
        byte[] trim = new byte[Math.max(bigInteger.bitLength(), bigInteger2.bitLength()) + 1];
        BigInteger shiftRight = bigInteger;
        BigInteger shiftRight2 = bigInteger2;
        int n = 0;
        int n2 = 0;
        int n3 = 0;
        int n4 = 0;
        while ((n2 | n3) != 0x0 || shiftRight.bitLength() > n4 || shiftRight2.bitLength() > n4) {
            final int n5 = (shiftRight.intValue() >>> n4) + n2 & 0x7;
            final int n6 = (shiftRight2.intValue() >>> n4) + n3 & 0x7;
            int n7 = n5 & 0x1;
            if (n7 != 0) {
                n7 -= (n5 & 0x2);
                if (n5 + n7 == 4 && (n6 & 0x3) == 0x2) {
                    n7 = -n7;
                }
            }
            int n8 = n6 & 0x1;
            if (n8 != 0) {
                n8 -= (n6 & 0x2);
                if (n6 + n8 == 4 && (n5 & 0x3) == 0x2) {
                    n8 = -n8;
                }
            }
            if (n2 << 1 == 1 + n7) {
                n2 ^= 0x1;
            }
            if (n3 << 1 == 1 + n8) {
                n3 ^= 0x1;
            }
            if (++n4 == 30) {
                n4 = 0;
                shiftRight = shiftRight.shiftRight(30);
                shiftRight2 = shiftRight2.shiftRight(30);
            }
            trim[n++] = (byte)(n7 << 4 | (n8 & 0xF));
        }
        if (trim.length > n) {
            trim = trim(trim, n);
        }
        return trim;
    }
    
    public static byte[] generateNaf(final BigInteger bigInteger) {
        if (bigInteger.signum() == 0) {
            return WNafUtil.EMPTY_BYTES;
        }
        final BigInteger add = bigInteger.shiftLeft(1).add(bigInteger);
        final int n = add.bitLength() - 1;
        final byte[] array = new byte[n];
        final BigInteger xor = add.xor(bigInteger);
        for (int i = 1; i < n; ++i) {
            if (xor.testBit(i)) {
                array[i - 1] = (byte)(bigInteger.testBit(i) ? -1 : 1);
                ++i;
            }
        }
        array[n - 1] = 1;
        return array;
    }
    
    public static byte[] generateWindowNaf(final int n, BigInteger shiftRight) {
        if (n == 2) {
            return generateNaf(shiftRight);
        }
        if (n < 2 || n > 8) {
            throw new IllegalArgumentException("'width' must be in the range [2, 8]");
        }
        if (shiftRight.signum() == 0) {
            return WNafUtil.EMPTY_BYTES;
        }
        byte[] trim = new byte[shiftRight.bitLength() + 1];
        final int n2 = 1 << n;
        final int n3 = n2 - 1;
        final int n4 = n2 >>> 1;
        boolean b = false;
        int n5 = 0;
        int i = 0;
        while (i <= shiftRight.bitLength()) {
            if (shiftRight.testBit(i) == b) {
                ++i;
            }
            else {
                shiftRight = shiftRight.shiftRight(i);
                int n6 = shiftRight.intValue() & n3;
                if (b) {
                    ++n6;
                }
                b = ((n6 & n4) != 0x0);
                if (b) {
                    n6 -= n2;
                }
                n5 += ((n5 > 0) ? (i - 1) : i);
                trim[n5++] = (byte)n6;
                i = n;
            }
        }
        if (trim.length > n5) {
            trim = trim(trim, n5);
        }
        return trim;
    }
    
    public static int getNafWeight(final BigInteger bigInteger) {
        if (bigInteger.signum() == 0) {
            return 0;
        }
        return bigInteger.shiftLeft(1).add(bigInteger).xor(bigInteger).bitCount();
    }
    
    public static WNafPreCompInfo getWNafPreCompInfo(final ECPoint ecPoint) {
        return getWNafPreCompInfo(ecPoint.getCurve().getPreCompInfo(ecPoint, "bc_wnaf"));
    }
    
    public static WNafPreCompInfo getWNafPreCompInfo(final PreCompInfo preCompInfo) {
        if (preCompInfo != null && preCompInfo instanceof WNafPreCompInfo) {
            return (WNafPreCompInfo)preCompInfo;
        }
        return new WNafPreCompInfo();
    }
    
    public static int getWindowSize(final int n) {
        return getWindowSize(n, WNafUtil.DEFAULT_WINDOW_SIZE_CUTOFFS);
    }
    
    public static int getWindowSize(final int n, final int[] array) {
        int n2;
        for (n2 = 0; n2 < array.length && n >= array[n2]; ++n2) {}
        return n2 + 2;
    }
    
    public static ECPoint mapPointWithPrecomp(final ECPoint ecPoint, final int n, final boolean b, final ECPointMap ecPointMap) {
        final ECCurve curve = ecPoint.getCurve();
        final WNafPreCompInfo precompute = precompute(ecPoint, n, b);
        final ECPoint map = ecPointMap.map(ecPoint);
        final WNafPreCompInfo wNafPreCompInfo = getWNafPreCompInfo(curve.getPreCompInfo(map, "bc_wnaf"));
        final ECPoint twice = precompute.getTwice();
        if (twice != null) {
            wNafPreCompInfo.setTwice(ecPointMap.map(twice));
        }
        final ECPoint[] preComp = precompute.getPreComp();
        final ECPoint[] preComp2 = new ECPoint[preComp.length];
        for (int i = 0; i < preComp.length; ++i) {
            preComp2[i] = ecPointMap.map(preComp[i]);
        }
        wNafPreCompInfo.setPreComp(preComp2);
        if (b) {
            final ECPoint[] preCompNeg = new ECPoint[preComp2.length];
            for (int j = 0; j < preCompNeg.length; ++j) {
                preCompNeg[j] = preComp2[j].negate();
            }
            wNafPreCompInfo.setPreCompNeg(preCompNeg);
        }
        curve.setPreCompInfo(map, "bc_wnaf", wNafPreCompInfo);
        return map;
    }
    
    public static WNafPreCompInfo precompute(final ECPoint ecPoint, final int n, final boolean b) {
        final ECCurve curve = ecPoint.getCurve();
        final WNafPreCompInfo wNafPreCompInfo = getWNafPreCompInfo(curve.getPreCompInfo(ecPoint, "bc_wnaf"));
        int length = 0;
        final int n2 = 1 << Math.max(0, n - 2);
        ECPoint[] preComp = wNafPreCompInfo.getPreComp();
        if (preComp == null) {
            preComp = WNafUtil.EMPTY_POINTS;
        }
        else {
            length = preComp.length;
        }
        if (length < n2) {
            preComp = resizeTable(preComp, n2);
            if (n2 == 1) {
                preComp[0] = ecPoint.normalize();
            }
            else {
                int i = length;
                if (i == 0) {
                    preComp[0] = ecPoint;
                    i = 1;
                }
                ECFieldElement zCoord = null;
                if (n2 == 2) {
                    preComp[1] = ecPoint.threeTimes();
                }
                else {
                    ECPoint twice = wNafPreCompInfo.getTwice();
                    ECPoint scaleY = preComp[i - 1];
                    if (twice == null) {
                        twice = preComp[0].twice();
                        wNafPreCompInfo.setTwice(twice);
                        if (!twice.isInfinity() && ECAlgorithms.isFpCurve(curve) && curve.getFieldSize() >= 64) {
                            switch (curve.getCoordinateSystem()) {
                                case 2:
                                case 3:
                                case 4: {
                                    zCoord = twice.getZCoord(0);
                                    twice = curve.createPoint(twice.getXCoord().toBigInteger(), twice.getYCoord().toBigInteger());
                                    final ECFieldElement square = zCoord.square();
                                    scaleY = scaleY.scaleX(square).scaleY(square.multiply(zCoord));
                                    if (length == 0) {
                                        preComp[0] = scaleY;
                                        break;
                                    }
                                    break;
                                }
                            }
                        }
                    }
                    while (i < n2) {
                        scaleY = (preComp[i++] = scaleY.add(twice));
                    }
                }
                curve.normalizeAll(preComp, length, n2 - length, zCoord);
            }
        }
        wNafPreCompInfo.setPreComp(preComp);
        if (b) {
            ECPoint[] preCompNeg = wNafPreCompInfo.getPreCompNeg();
            int j;
            if (preCompNeg == null) {
                j = 0;
                preCompNeg = new ECPoint[n2];
            }
            else {
                j = preCompNeg.length;
                if (j < n2) {
                    preCompNeg = resizeTable(preCompNeg, n2);
                }
            }
            while (j < n2) {
                preCompNeg[j] = preComp[j].negate();
                ++j;
            }
            wNafPreCompInfo.setPreCompNeg(preCompNeg);
        }
        curve.setPreCompInfo(ecPoint, "bc_wnaf", wNafPreCompInfo);
        return wNafPreCompInfo;
    }
    
    private static byte[] trim(final byte[] array, final int n) {
        final byte[] array2 = new byte[n];
        System.arraycopy(array, 0, array2, 0, array2.length);
        return array2;
    }
    
    private static int[] trim(final int[] array, final int n) {
        final int[] array2 = new int[n];
        System.arraycopy(array, 0, array2, 0, array2.length);
        return array2;
    }
    
    private static ECPoint[] resizeTable(final ECPoint[] array, final int n) {
        final ECPoint[] array2 = new ECPoint[n];
        System.arraycopy(array, 0, array2, 0, array.length);
        return array2;
    }
    
    static {
        DEFAULT_WINDOW_SIZE_CUTOFFS = new int[] { 13, 41, 121, 337, 897, 2305 };
        EMPTY_BYTES = new byte[0];
        EMPTY_INTS = new int[0];
        EMPTY_POINTS = new ECPoint[0];
    }
}
