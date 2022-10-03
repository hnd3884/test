package org.bouncycastle.math.ec;

import java.math.BigInteger;

class Tnaf
{
    private static final BigInteger MINUS_ONE;
    private static final BigInteger MINUS_TWO;
    private static final BigInteger MINUS_THREE;
    public static final byte WIDTH = 4;
    public static final byte POW_2_WIDTH = 16;
    public static final ZTauElement[] alpha0;
    public static final byte[][] alpha0Tnaf;
    public static final ZTauElement[] alpha1;
    public static final byte[][] alpha1Tnaf;
    
    public static BigInteger norm(final byte b, final ZTauElement zTauElement) {
        final BigInteger multiply = zTauElement.u.multiply(zTauElement.u);
        final BigInteger multiply2 = zTauElement.u.multiply(zTauElement.v);
        final BigInteger shiftLeft = zTauElement.v.multiply(zTauElement.v).shiftLeft(1);
        BigInteger bigInteger;
        if (b == 1) {
            bigInteger = multiply.add(multiply2).add(shiftLeft);
        }
        else {
            if (b != -1) {
                throw new IllegalArgumentException("mu must be 1 or -1");
            }
            bigInteger = multiply.subtract(multiply2).add(shiftLeft);
        }
        return bigInteger;
    }
    
    public static SimpleBigDecimal norm(final byte b, final SimpleBigDecimal simpleBigDecimal, final SimpleBigDecimal simpleBigDecimal2) {
        final SimpleBigDecimal multiply = simpleBigDecimal.multiply(simpleBigDecimal);
        final SimpleBigDecimal multiply2 = simpleBigDecimal.multiply(simpleBigDecimal2);
        final SimpleBigDecimal shiftLeft = simpleBigDecimal2.multiply(simpleBigDecimal2).shiftLeft(1);
        SimpleBigDecimal simpleBigDecimal3;
        if (b == 1) {
            simpleBigDecimal3 = multiply.add(multiply2).add(shiftLeft);
        }
        else {
            if (b != -1) {
                throw new IllegalArgumentException("mu must be 1 or -1");
            }
            simpleBigDecimal3 = multiply.subtract(multiply2).add(shiftLeft);
        }
        return simpleBigDecimal3;
    }
    
    public static ZTauElement round(final SimpleBigDecimal simpleBigDecimal, final SimpleBigDecimal simpleBigDecimal2, final byte b) {
        if (simpleBigDecimal2.getScale() != simpleBigDecimal.getScale()) {
            throw new IllegalArgumentException("lambda0 and lambda1 do not have same scale");
        }
        if (b != 1 && b != -1) {
            throw new IllegalArgumentException("mu must be 1 or -1");
        }
        final BigInteger round = simpleBigDecimal.round();
        final BigInteger round2 = simpleBigDecimal2.round();
        final SimpleBigDecimal subtract = simpleBigDecimal.subtract(round);
        final SimpleBigDecimal subtract2 = simpleBigDecimal2.subtract(round2);
        final SimpleBigDecimal add = subtract.add(subtract);
        SimpleBigDecimal simpleBigDecimal3;
        if (b == 1) {
            simpleBigDecimal3 = add.add(subtract2);
        }
        else {
            simpleBigDecimal3 = add.subtract(subtract2);
        }
        final SimpleBigDecimal add2 = subtract2.add(subtract2).add(subtract2);
        final SimpleBigDecimal add3 = add2.add(subtract2);
        SimpleBigDecimal simpleBigDecimal4;
        SimpleBigDecimal simpleBigDecimal5;
        if (b == 1) {
            simpleBigDecimal4 = subtract.subtract(add2);
            simpleBigDecimal5 = subtract.add(add3);
        }
        else {
            simpleBigDecimal4 = subtract.add(add2);
            simpleBigDecimal5 = subtract.subtract(add3);
        }
        int n = 0;
        byte b2 = 0;
        if (simpleBigDecimal3.compareTo(ECConstants.ONE) >= 0) {
            if (simpleBigDecimal4.compareTo(Tnaf.MINUS_ONE) < 0) {
                b2 = b;
            }
            else {
                n = 1;
            }
        }
        else if (simpleBigDecimal5.compareTo(ECConstants.TWO) >= 0) {
            b2 = b;
        }
        if (simpleBigDecimal3.compareTo(Tnaf.MINUS_ONE) < 0) {
            if (simpleBigDecimal4.compareTo(ECConstants.ONE) >= 0) {
                b2 = (byte)(-b);
            }
            else {
                n = -1;
            }
        }
        else if (simpleBigDecimal5.compareTo(Tnaf.MINUS_TWO) < 0) {
            b2 = (byte)(-b);
        }
        return new ZTauElement(round.add(BigInteger.valueOf(n)), round2.add(BigInteger.valueOf(b2)));
    }
    
    public static SimpleBigDecimal approximateDivisionByN(final BigInteger bigInteger, final BigInteger bigInteger2, final BigInteger bigInteger3, final byte b, final int n, final int n2) {
        final int n3 = (n + 5) / 2 + n2;
        final BigInteger multiply = bigInteger2.multiply(bigInteger.shiftRight(n - n3 - 2 + b));
        final BigInteger add = multiply.add(bigInteger3.multiply(multiply.shiftRight(n)));
        BigInteger bigInteger4 = add.shiftRight(n3 - n2);
        if (add.testBit(n3 - n2 - 1)) {
            bigInteger4 = bigInteger4.add(ECConstants.ONE);
        }
        return new SimpleBigDecimal(bigInteger4, n2);
    }
    
    public static byte[] tauAdicNaf(final byte b, final ZTauElement zTauElement) {
        if (b != 1 && b != -1) {
            throw new IllegalArgumentException("mu must be 1 or -1");
        }
        final int bitLength = norm(b, zTauElement).bitLength();
        final byte[] array = new byte[(bitLength > 30) ? (bitLength + 4) : 34];
        int n = 0;
        int n2 = 0;
        BigInteger bigInteger3;
        for (BigInteger bigInteger = zTauElement.u, bigInteger2 = zTauElement.v; !bigInteger.equals(ECConstants.ZERO) || !bigInteger2.equals(ECConstants.ZERO); bigInteger2 = bigInteger3.shiftRight(1).negate(), ++n) {
            if (bigInteger.testBit(0)) {
                array[n] = (byte)ECConstants.TWO.subtract(bigInteger.subtract(bigInteger2.shiftLeft(1)).mod(ECConstants.FOUR)).intValue();
                if (array[n] == 1) {
                    bigInteger = bigInteger.clearBit(0);
                }
                else {
                    bigInteger = bigInteger.add(ECConstants.ONE);
                }
                n2 = n;
            }
            else {
                array[n] = 0;
            }
            bigInteger3 = bigInteger;
            final BigInteger shiftRight = bigInteger.shiftRight(1);
            if (b == 1) {
                bigInteger = bigInteger2.add(shiftRight);
            }
            else {
                bigInteger = bigInteger2.subtract(shiftRight);
            }
        }
        final byte[] array2 = new byte[++n2];
        System.arraycopy(array, 0, array2, 0, n2);
        return array2;
    }
    
    public static ECPoint.AbstractF2m tau(final ECPoint.AbstractF2m abstractF2m) {
        return abstractF2m.tau();
    }
    
    public static byte getMu(final ECCurve.AbstractF2m abstractF2m) {
        if (!abstractF2m.isKoblitz()) {
            throw new IllegalArgumentException("No Koblitz curve (ABC), TNAF multiplication not possible");
        }
        if (abstractF2m.getA().isZero()) {
            return -1;
        }
        return 1;
    }
    
    public static byte getMu(final ECFieldElement ecFieldElement) {
        return (byte)(ecFieldElement.isZero() ? -1 : 1);
    }
    
    public static byte getMu(final int n) {
        return (byte)((n == 0) ? -1 : 1);
    }
    
    public static BigInteger[] getLucas(final byte b, final int n, final boolean b2) {
        if (b != 1 && b != -1) {
            throw new IllegalArgumentException("mu must be 1 or -1");
        }
        BigInteger bigInteger;
        BigInteger bigInteger2;
        if (b2) {
            bigInteger = ECConstants.TWO;
            bigInteger2 = BigInteger.valueOf(b);
        }
        else {
            bigInteger = ECConstants.ZERO;
            bigInteger2 = ECConstants.ONE;
        }
        for (int i = 1; i < n; ++i) {
            BigInteger negate;
            if (b == 1) {
                negate = bigInteger2;
            }
            else {
                negate = bigInteger2.negate();
            }
            final BigInteger subtract = negate.subtract(bigInteger.shiftLeft(1));
            bigInteger = bigInteger2;
            bigInteger2 = subtract;
        }
        return new BigInteger[] { bigInteger, bigInteger2 };
    }
    
    public static BigInteger getTw(final byte b, final int bit) {
        if (bit != 4) {
            final BigInteger[] lucas = getLucas(b, bit, false);
            final BigInteger setBit = ECConstants.ZERO.setBit(bit);
            return ECConstants.TWO.multiply(lucas[0]).multiply(lucas[1].modInverse(setBit)).mod(setBit);
        }
        if (b == 1) {
            return BigInteger.valueOf(6L);
        }
        return BigInteger.valueOf(10L);
    }
    
    public static BigInteger[] getSi(final ECCurve.AbstractF2m abstractF2m) {
        if (!abstractF2m.isKoblitz()) {
            throw new IllegalArgumentException("si is defined for Koblitz curves only");
        }
        final int fieldSize = abstractF2m.getFieldSize();
        final int intValue = abstractF2m.getA().toBigInteger().intValue();
        final byte mu = getMu(intValue);
        final int shiftsForCofactor = getShiftsForCofactor(abstractF2m.getCofactor());
        final BigInteger[] lucas = getLucas(mu, fieldSize + 3 - intValue, false);
        if (mu == 1) {
            lucas[0] = lucas[0].negate();
            lucas[1] = lucas[1].negate();
        }
        return new BigInteger[] { ECConstants.ONE.add(lucas[1]).shiftRight(shiftsForCofactor), ECConstants.ONE.add(lucas[0]).shiftRight(shiftsForCofactor).negate() };
    }
    
    public static BigInteger[] getSi(final int n, final int n2, final BigInteger bigInteger) {
        final byte mu = getMu(n2);
        final int shiftsForCofactor = getShiftsForCofactor(bigInteger);
        final BigInteger[] lucas = getLucas(mu, n + 3 - n2, false);
        if (mu == 1) {
            lucas[0] = lucas[0].negate();
            lucas[1] = lucas[1].negate();
        }
        return new BigInteger[] { ECConstants.ONE.add(lucas[1]).shiftRight(shiftsForCofactor), ECConstants.ONE.add(lucas[0]).shiftRight(shiftsForCofactor).negate() };
    }
    
    protected static int getShiftsForCofactor(final BigInteger bigInteger) {
        if (bigInteger != null) {
            if (bigInteger.equals(ECConstants.TWO)) {
                return 1;
            }
            if (bigInteger.equals(ECConstants.FOUR)) {
                return 2;
            }
        }
        throw new IllegalArgumentException("h (Cofactor) must be 2 or 4");
    }
    
    public static ZTauElement partModReduction(final BigInteger bigInteger, final int n, final byte b, final BigInteger[] array, final byte b2, final byte b3) {
        BigInteger bigInteger2;
        if (b2 == 1) {
            bigInteger2 = array[0].add(array[1]);
        }
        else {
            bigInteger2 = array[0].subtract(array[1]);
        }
        final BigInteger bigInteger3 = getLucas(b2, n, true)[1];
        final ZTauElement round = round(approximateDivisionByN(bigInteger, array[0], bigInteger3, b, n, b3), approximateDivisionByN(bigInteger, array[1], bigInteger3, b, n, b3), b2);
        return new ZTauElement(bigInteger.subtract(bigInteger2.multiply(round.u)).subtract(BigInteger.valueOf(2L).multiply(array[1]).multiply(round.v)), array[1].multiply(round.u).subtract(array[0].multiply(round.v)));
    }
    
    public static ECPoint.AbstractF2m multiplyRTnaf(final ECPoint.AbstractF2m abstractF2m, final BigInteger bigInteger) {
        final ECCurve.AbstractF2m abstractF2m2 = (ECCurve.AbstractF2m)abstractF2m.getCurve();
        final int fieldSize = abstractF2m2.getFieldSize();
        final int intValue = abstractF2m2.getA().toBigInteger().intValue();
        return multiplyTnaf(abstractF2m, partModReduction(bigInteger, fieldSize, (byte)intValue, abstractF2m2.getSi(), getMu(intValue), (byte)10));
    }
    
    public static ECPoint.AbstractF2m multiplyTnaf(final ECPoint.AbstractF2m abstractF2m, final ZTauElement zTauElement) {
        return multiplyFromTnaf(abstractF2m, tauAdicNaf(getMu(abstractF2m.getCurve().getA()), zTauElement));
    }
    
    public static ECPoint.AbstractF2m multiplyFromTnaf(final ECPoint.AbstractF2m abstractF2m, final byte[] array) {
        ECPoint.AbstractF2m tauPow = (ECPoint.AbstractF2m)abstractF2m.getCurve().getInfinity();
        final ECPoint.AbstractF2m abstractF2m2 = (ECPoint.AbstractF2m)abstractF2m.negate();
        int n = 0;
        for (int i = array.length - 1; i >= 0; --i) {
            ++n;
            final byte b = array[i];
            if (b != 0) {
                final ECPoint.AbstractF2m tauPow2 = tauPow.tauPow(n);
                n = 0;
                tauPow = (ECPoint.AbstractF2m)tauPow2.add((b > 0) ? abstractF2m : abstractF2m2);
            }
        }
        if (n > 0) {
            tauPow = tauPow.tauPow(n);
        }
        return tauPow;
    }
    
    public static byte[] tauAdicWNaf(final byte b, final ZTauElement zTauElement, final byte b2, final BigInteger bigInteger, final BigInteger bigInteger2, final ZTauElement[] array) {
        if (b != 1 && b != -1) {
            throw new IllegalArgumentException("mu must be 1 or -1");
        }
        final int bitLength = norm(b, zTauElement).bitLength();
        final byte[] array2 = new byte[(bitLength > 30) ? (bitLength + 4 + b2) : (34 + b2)];
        final BigInteger shiftRight = bigInteger.shiftRight(1);
        BigInteger bigInteger3 = zTauElement.u;
        BigInteger bigInteger4 = zTauElement.v;
        BigInteger bigInteger5;
        for (int n = 0; !bigInteger3.equals(ECConstants.ZERO) || !bigInteger4.equals(ECConstants.ZERO); bigInteger4 = bigInteger5.shiftRight(1).negate(), ++n) {
            if (bigInteger3.testBit(0)) {
                final BigInteger mod = bigInteger3.add(bigInteger4.multiply(bigInteger2)).mod(bigInteger);
                byte b3;
                if (mod.compareTo(shiftRight) >= 0) {
                    b3 = (byte)mod.subtract(bigInteger).intValue();
                }
                else {
                    b3 = (byte)mod.intValue();
                }
                array2[n] = b3;
                boolean b4 = true;
                if (b3 < 0) {
                    b4 = false;
                    b3 = (byte)(-b3);
                }
                if (b4) {
                    bigInteger3 = bigInteger3.subtract(array[b3].u);
                    bigInteger4 = bigInteger4.subtract(array[b3].v);
                }
                else {
                    bigInteger3 = bigInteger3.add(array[b3].u);
                    bigInteger4 = bigInteger4.add(array[b3].v);
                }
            }
            else {
                array2[n] = 0;
            }
            bigInteger5 = bigInteger3;
            if (b == 1) {
                bigInteger3 = bigInteger4.add(bigInteger3.shiftRight(1));
            }
            else {
                bigInteger3 = bigInteger4.subtract(bigInteger3.shiftRight(1));
            }
        }
        return array2;
    }
    
    public static ECPoint.AbstractF2m[] getPreComp(final ECPoint.AbstractF2m abstractF2m, final byte b) {
        final byte[][] array = (b == 0) ? Tnaf.alpha0Tnaf : Tnaf.alpha1Tnaf;
        final ECPoint.AbstractF2m[] array2 = new ECPoint.AbstractF2m[array.length + 1 >>> 1];
        array2[0] = abstractF2m;
        for (int length = array.length, i = 3; i < length; i += 2) {
            array2[i >>> 1] = multiplyFromTnaf(abstractF2m, array[i]);
        }
        abstractF2m.getCurve().normalizeAll(array2);
        return array2;
    }
    
    static {
        MINUS_ONE = ECConstants.ONE.negate();
        MINUS_TWO = ECConstants.TWO.negate();
        MINUS_THREE = ECConstants.THREE.negate();
        alpha0 = new ZTauElement[] { null, new ZTauElement(ECConstants.ONE, ECConstants.ZERO), null, new ZTauElement(Tnaf.MINUS_THREE, Tnaf.MINUS_ONE), null, new ZTauElement(Tnaf.MINUS_ONE, Tnaf.MINUS_ONE), null, new ZTauElement(ECConstants.ONE, Tnaf.MINUS_ONE), null };
        alpha0Tnaf = new byte[][] { null, { 1 }, null, { -1, 0, 1 }, null, { 1, 0, 1 }, null, { -1, 0, 0, 1 } };
        alpha1 = new ZTauElement[] { null, new ZTauElement(ECConstants.ONE, ECConstants.ZERO), null, new ZTauElement(Tnaf.MINUS_THREE, ECConstants.ONE), null, new ZTauElement(Tnaf.MINUS_ONE, ECConstants.ONE), null, new ZTauElement(ECConstants.ONE, ECConstants.ONE), null };
        alpha1Tnaf = new byte[][] { null, { 1 }, null, { -1, 0, 1 }, null, { 1, 0, 1 }, null, { -1, 0, 0, -1 } };
    }
}
