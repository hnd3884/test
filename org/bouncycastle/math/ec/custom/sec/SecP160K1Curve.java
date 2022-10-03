package org.bouncycastle.math.ec.custom.sec;

import org.bouncycastle.math.raw.Nat160;
import org.bouncycastle.math.ec.ECLookupTable;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.util.encoders.Hex;
import org.bouncycastle.math.ec.ECConstants;
import org.bouncycastle.math.ec.ECFieldElement;
import java.math.BigInteger;
import org.bouncycastle.math.ec.ECCurve;

public class SecP160K1Curve extends AbstractFp
{
    public static final BigInteger q;
    private static final int SECP160K1_DEFAULT_COORDS = 2;
    protected SecP160K1Point infinity;
    
    public SecP160K1Curve() {
        super(SecP160K1Curve.q);
        this.infinity = new SecP160K1Point(this, null, null);
        this.a = this.fromBigInteger(ECConstants.ZERO);
        this.b = this.fromBigInteger(BigInteger.valueOf(7L));
        this.order = new BigInteger(1, Hex.decode("0100000000000000000001B8FA16DFAB9ACA16B6B3"));
        this.cofactor = BigInteger.valueOf(1L);
        this.coord = 2;
    }
    
    @Override
    protected ECCurve cloneCurve() {
        return new SecP160K1Curve();
    }
    
    @Override
    public boolean supportsCoordinateSystem(final int n) {
        switch (n) {
            case 2: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    public BigInteger getQ() {
        return SecP160K1Curve.q;
    }
    
    @Override
    public int getFieldSize() {
        return SecP160K1Curve.q.bitLength();
    }
    
    @Override
    public ECFieldElement fromBigInteger(final BigInteger bigInteger) {
        return new SecP160R2FieldElement(bigInteger);
    }
    
    @Override
    protected ECPoint createRawPoint(final ECFieldElement ecFieldElement, final ECFieldElement ecFieldElement2, final boolean b) {
        return new SecP160K1Point(this, ecFieldElement, ecFieldElement2, b);
    }
    
    @Override
    protected ECPoint createRawPoint(final ECFieldElement ecFieldElement, final ECFieldElement ecFieldElement2, final ECFieldElement[] array, final boolean b) {
        return new SecP160K1Point(this, ecFieldElement, ecFieldElement2, array, b);
    }
    
    @Override
    public ECPoint getInfinity() {
        return this.infinity;
    }
    
    @Override
    public ECLookupTable createCacheSafeLookupTable(final ECPoint[] array, final int n, final int n2) {
        final int[] array2 = new int[n2 * 5 * 2];
        int n3 = 0;
        for (int i = 0; i < n2; ++i) {
            final ECPoint ecPoint = array[n + i];
            Nat160.copy(((SecP160R2FieldElement)ecPoint.getRawXCoord()).x, 0, array2, n3);
            n3 += 5;
            Nat160.copy(((SecP160R2FieldElement)ecPoint.getRawYCoord()).x, 0, array2, n3);
            n3 += 5;
        }
        return new ECLookupTable() {
            public int getSize() {
                return n2;
            }
            
            public ECPoint lookup(final int n) {
                final int[] create = Nat160.create();
                final int[] create2 = Nat160.create();
                int n2 = 0;
                for (int i = 0; i < n2; ++i) {
                    final int n3 = (i ^ n) - 1 >> 31;
                    for (int j = 0; j < 5; ++j) {
                        final int[] array = create;
                        final int n4 = j;
                        array[n4] ^= (array2[n2 + j] & n3);
                        final int[] array2 = create2;
                        final int n5 = j;
                        array2[n5] ^= (array2[n2 + 5 + j] & n3);
                    }
                    n2 += 10;
                }
                return SecP160K1Curve.this.createRawPoint(new SecP160R2FieldElement(create), new SecP160R2FieldElement(create2), false);
            }
        };
    }
    
    static {
        q = SecP160R2Curve.q;
    }
}
