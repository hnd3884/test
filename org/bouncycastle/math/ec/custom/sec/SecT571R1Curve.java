package org.bouncycastle.math.ec.custom.sec;

import org.bouncycastle.math.raw.Nat576;
import org.bouncycastle.math.ec.ECLookupTable;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.util.encoders.Hex;
import java.math.BigInteger;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.ec.ECCurve;

public class SecT571R1Curve extends AbstractF2m
{
    private static final int SecT571R1_DEFAULT_COORDS = 6;
    protected SecT571R1Point infinity;
    static final SecT571FieldElement SecT571R1_B;
    static final SecT571FieldElement SecT571R1_B_SQRT;
    
    public SecT571R1Curve() {
        super(571, 2, 5, 10);
        this.infinity = new SecT571R1Point(this, null, null);
        this.a = this.fromBigInteger(BigInteger.valueOf(1L));
        this.b = SecT571R1Curve.SecT571R1_B;
        this.order = new BigInteger(1, Hex.decode("03FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFE661CE18FF55987308059B186823851EC7DD9CA1161DE93D5174D66E8382E9BB2FE84E47"));
        this.cofactor = BigInteger.valueOf(2L);
        this.coord = 6;
    }
    
    @Override
    protected ECCurve cloneCurve() {
        return new SecT571R1Curve();
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
        return 571;
    }
    
    @Override
    public ECFieldElement fromBigInteger(final BigInteger bigInteger) {
        return new SecT571FieldElement(bigInteger);
    }
    
    @Override
    protected ECPoint createRawPoint(final ECFieldElement ecFieldElement, final ECFieldElement ecFieldElement2, final boolean b) {
        return new SecT571R1Point(this, ecFieldElement, ecFieldElement2, b);
    }
    
    @Override
    protected ECPoint createRawPoint(final ECFieldElement ecFieldElement, final ECFieldElement ecFieldElement2, final ECFieldElement[] array, final boolean b) {
        return new SecT571R1Point(this, ecFieldElement, ecFieldElement2, array, b);
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
        return 571;
    }
    
    public boolean isTrinomial() {
        return false;
    }
    
    public int getK1() {
        return 2;
    }
    
    public int getK2() {
        return 5;
    }
    
    public int getK3() {
        return 10;
    }
    
    @Override
    public ECLookupTable createCacheSafeLookupTable(final ECPoint[] array, final int n, final int n2) {
        final long[] array2 = new long[n2 * 9 * 2];
        int n3 = 0;
        for (int i = 0; i < n2; ++i) {
            final ECPoint ecPoint = array[n + i];
            Nat576.copy64(((SecT571FieldElement)ecPoint.getRawXCoord()).x, 0, array2, n3);
            n3 += 9;
            Nat576.copy64(((SecT571FieldElement)ecPoint.getRawYCoord()).x, 0, array2, n3);
            n3 += 9;
        }
        return new ECLookupTable() {
            public int getSize() {
                return n2;
            }
            
            public ECPoint lookup(final int n) {
                final long[] create64 = Nat576.create64();
                final long[] create65 = Nat576.create64();
                int n2 = 0;
                for (int i = 0; i < n2; ++i) {
                    final long n3 = (i ^ n) - 1 >> 31;
                    for (int j = 0; j < 9; ++j) {
                        final long[] array = create64;
                        final int n4 = j;
                        array[n4] ^= (array2[n2 + j] & n3);
                        final long[] array2 = create65;
                        final int n5 = j;
                        array2[n5] ^= (array2[n2 + 9 + j] & n3);
                    }
                    n2 += 18;
                }
                return SecT571R1Curve.this.createRawPoint(new SecT571FieldElement(create64), new SecT571FieldElement(create65), false);
            }
        };
    }
    
    static {
        SecT571R1_B = new SecT571FieldElement(new BigInteger(1, Hex.decode("02F40E7E2221F295DE297117B7F3D62F5C6A97FFCB8CEFF1CD6BA8CE4A9A18AD84FFABBD8EFA59332BE7AD6756A66E294AFD185A78FF12AA520E4DE739BACA0C7FFEFF7F2955727A")));
        SecT571R1_B_SQRT = (SecT571FieldElement)SecT571R1Curve.SecT571R1_B.sqrt();
    }
}
