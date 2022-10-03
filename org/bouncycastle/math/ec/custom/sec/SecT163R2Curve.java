package org.bouncycastle.math.ec.custom.sec;

import org.bouncycastle.math.raw.Nat192;
import org.bouncycastle.math.ec.ECLookupTable;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.util.encoders.Hex;
import java.math.BigInteger;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.ec.ECCurve;

public class SecT163R2Curve extends AbstractF2m
{
    private static final int SecT163R2_DEFAULT_COORDS = 6;
    protected SecT163R2Point infinity;
    
    public SecT163R2Curve() {
        super(163, 3, 6, 7);
        this.infinity = new SecT163R2Point(this, null, null);
        this.a = this.fromBigInteger(BigInteger.valueOf(1L));
        this.b = this.fromBigInteger(new BigInteger(1, Hex.decode("020A601907B8C953CA1481EB10512F78744A3205FD")));
        this.order = new BigInteger(1, Hex.decode("040000000000000000000292FE77E70C12A4234C33"));
        this.cofactor = BigInteger.valueOf(2L);
        this.coord = 6;
    }
    
    @Override
    protected ECCurve cloneCurve() {
        return new SecT163R2Curve();
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
        return 163;
    }
    
    @Override
    public ECFieldElement fromBigInteger(final BigInteger bigInteger) {
        return new SecT163FieldElement(bigInteger);
    }
    
    @Override
    protected ECPoint createRawPoint(final ECFieldElement ecFieldElement, final ECFieldElement ecFieldElement2, final boolean b) {
        return new SecT163R2Point(this, ecFieldElement, ecFieldElement2, b);
    }
    
    @Override
    protected ECPoint createRawPoint(final ECFieldElement ecFieldElement, final ECFieldElement ecFieldElement2, final ECFieldElement[] array, final boolean b) {
        return new SecT163R2Point(this, ecFieldElement, ecFieldElement2, array, b);
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
        return 163;
    }
    
    public boolean isTrinomial() {
        return false;
    }
    
    public int getK1() {
        return 3;
    }
    
    public int getK2() {
        return 6;
    }
    
    public int getK3() {
        return 7;
    }
    
    @Override
    public ECLookupTable createCacheSafeLookupTable(final ECPoint[] array, final int n, final int n2) {
        final long[] array2 = new long[n2 * 3 * 2];
        int n3 = 0;
        for (int i = 0; i < n2; ++i) {
            final ECPoint ecPoint = array[n + i];
            Nat192.copy64(((SecT163FieldElement)ecPoint.getRawXCoord()).x, 0, array2, n3);
            n3 += 3;
            Nat192.copy64(((SecT163FieldElement)ecPoint.getRawYCoord()).x, 0, array2, n3);
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
                return SecT163R2Curve.this.createRawPoint(new SecT163FieldElement(create64), new SecT163FieldElement(create65), false);
            }
        };
    }
}
