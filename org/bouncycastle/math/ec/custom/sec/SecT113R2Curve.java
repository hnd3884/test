package org.bouncycastle.math.ec.custom.sec;

import org.bouncycastle.math.raw.Nat128;
import org.bouncycastle.math.ec.ECLookupTable;
import org.bouncycastle.math.ec.ECPoint;
import java.math.BigInteger;
import org.bouncycastle.util.encoders.Hex;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.ec.ECCurve;

public class SecT113R2Curve extends AbstractF2m
{
    private static final int SecT113R2_DEFAULT_COORDS = 6;
    protected SecT113R2Point infinity;
    
    public SecT113R2Curve() {
        super(113, 9, 0, 0);
        this.infinity = new SecT113R2Point(this, null, null);
        this.a = this.fromBigInteger(new BigInteger(1, Hex.decode("00689918DBEC7E5A0DD6DFC0AA55C7")));
        this.b = this.fromBigInteger(new BigInteger(1, Hex.decode("0095E9A9EC9B297BD4BF36E059184F")));
        this.order = new BigInteger(1, Hex.decode("010000000000000108789B2496AF93"));
        this.cofactor = BigInteger.valueOf(2L);
        this.coord = 6;
    }
    
    @Override
    protected ECCurve cloneCurve() {
        return new SecT113R2Curve();
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
        return 113;
    }
    
    @Override
    public ECFieldElement fromBigInteger(final BigInteger bigInteger) {
        return new SecT113FieldElement(bigInteger);
    }
    
    @Override
    protected ECPoint createRawPoint(final ECFieldElement ecFieldElement, final ECFieldElement ecFieldElement2, final boolean b) {
        return new SecT113R2Point(this, ecFieldElement, ecFieldElement2, b);
    }
    
    @Override
    protected ECPoint createRawPoint(final ECFieldElement ecFieldElement, final ECFieldElement ecFieldElement2, final ECFieldElement[] array, final boolean b) {
        return new SecT113R2Point(this, ecFieldElement, ecFieldElement2, array, b);
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
        return 113;
    }
    
    public boolean isTrinomial() {
        return true;
    }
    
    public int getK1() {
        return 9;
    }
    
    public int getK2() {
        return 0;
    }
    
    public int getK3() {
        return 0;
    }
    
    @Override
    public ECLookupTable createCacheSafeLookupTable(final ECPoint[] array, final int n, final int n2) {
        final long[] array2 = new long[n2 * 2 * 2];
        int n3 = 0;
        for (int i = 0; i < n2; ++i) {
            final ECPoint ecPoint = array[n + i];
            Nat128.copy64(((SecT113FieldElement)ecPoint.getRawXCoord()).x, 0, array2, n3);
            n3 += 2;
            Nat128.copy64(((SecT113FieldElement)ecPoint.getRawYCoord()).x, 0, array2, n3);
            n3 += 2;
        }
        return new ECLookupTable() {
            public int getSize() {
                return n2;
            }
            
            public ECPoint lookup(final int n) {
                final long[] create64 = Nat128.create64();
                final long[] create65 = Nat128.create64();
                int n2 = 0;
                for (int i = 0; i < n2; ++i) {
                    final long n3 = (i ^ n) - 1 >> 31;
                    for (int j = 0; j < 2; ++j) {
                        final long[] array = create64;
                        final int n4 = j;
                        array[n4] ^= (array2[n2 + j] & n3);
                        final long[] array2 = create65;
                        final int n5 = j;
                        array2[n5] ^= (array2[n2 + 2 + j] & n3);
                    }
                    n2 += 4;
                }
                return SecT113R2Curve.this.createRawPoint(new SecT113FieldElement(create64), new SecT113FieldElement(create65), false);
            }
        };
    }
}
