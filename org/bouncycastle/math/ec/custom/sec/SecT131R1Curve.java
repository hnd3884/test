package org.bouncycastle.math.ec.custom.sec;

import org.bouncycastle.math.raw.Nat192;
import org.bouncycastle.math.ec.ECLookupTable;
import org.bouncycastle.math.ec.ECPoint;
import java.math.BigInteger;
import org.bouncycastle.util.encoders.Hex;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.ec.ECCurve;

public class SecT131R1Curve extends AbstractF2m
{
    private static final int SecT131R1_DEFAULT_COORDS = 6;
    protected SecT131R1Point infinity;
    
    public SecT131R1Curve() {
        super(131, 2, 3, 8);
        this.infinity = new SecT131R1Point(this, null, null);
        this.a = this.fromBigInteger(new BigInteger(1, Hex.decode("07A11B09A76B562144418FF3FF8C2570B8")));
        this.b = this.fromBigInteger(new BigInteger(1, Hex.decode("0217C05610884B63B9C6C7291678F9D341")));
        this.order = new BigInteger(1, Hex.decode("0400000000000000023123953A9464B54D"));
        this.cofactor = BigInteger.valueOf(2L);
        this.coord = 6;
    }
    
    @Override
    protected ECCurve cloneCurve() {
        return new SecT131R1Curve();
    }
    
    @Override
    public boolean supportsCoordinateSystem(final int n) {
        switch (n) {
            case 6: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    @Override
    public int getFieldSize() {
        return 131;
    }
    
    @Override
    public ECFieldElement fromBigInteger(final BigInteger bigInteger) {
        return new SecT131FieldElement(bigInteger);
    }
    
    @Override
    protected ECPoint createRawPoint(final ECFieldElement ecFieldElement, final ECFieldElement ecFieldElement2, final boolean b) {
        return new SecT131R1Point(this, ecFieldElement, ecFieldElement2, b);
    }
    
    @Override
    protected ECPoint createRawPoint(final ECFieldElement ecFieldElement, final ECFieldElement ecFieldElement2, final ECFieldElement[] array, final boolean b) {
        return new SecT131R1Point(this, ecFieldElement, ecFieldElement2, array, b);
    }
    
    @Override
    public ECPoint getInfinity() {
        return this.infinity;
    }
    
    @Override
    public boolean isKoblitz() {
        return false;
    }
    
    public int getM() {
        return 131;
    }
    
    public boolean isTrinomial() {
        return false;
    }
    
    public int getK1() {
        return 2;
    }
    
    public int getK2() {
        return 3;
    }
    
    public int getK3() {
        return 8;
    }
    
    @Override
    public ECLookupTable createCacheSafeLookupTable(final ECPoint[] array, final int n, final int n2) {
        final long[] array2 = new long[n2 * 3 * 2];
        int n3 = 0;
        for (int i = 0; i < n2; ++i) {
            final ECPoint ecPoint = array[n + i];
            Nat192.copy64(((SecT131FieldElement)ecPoint.getRawXCoord()).x, 0, array2, n3);
            n3 += 3;
            Nat192.copy64(((SecT131FieldElement)ecPoint.getRawYCoord()).x, 0, array2, n3);
            n3 += 3;
        }
        return new ECLookupTable() {
            public int getSize() {
                return n2;
            }
            
            public ECPoint lookup(final int n) {
                final long[] create64 = Nat192.create64();
                final long[] create65 = Nat192.create64();
                int n2 = 0;
                for (int i = 0; i < n2; ++i) {
                    final long n3 = (i ^ n) - 1 >> 31;
                    for (int j = 0; j < 3; ++j) {
                        final long[] array = create64;
                        final int n4 = j;
                        array[n4] ^= (array2[n2 + j] & n3);
                        final long[] array2 = create65;
                        final int n5 = j;
                        array2[n5] ^= (array2[n2 + 3 + j] & n3);
                    }
                    n2 += 6;
                }
                return SecT131R1Curve.this.createRawPoint(new SecT131FieldElement(create64), new SecT131FieldElement(create65), false);
            }
        };
    }
}
