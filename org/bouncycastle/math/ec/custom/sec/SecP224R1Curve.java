package org.bouncycastle.math.ec.custom.sec;

import org.bouncycastle.math.raw.Nat224;
import org.bouncycastle.math.ec.ECLookupTable;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.util.encoders.Hex;
import org.bouncycastle.math.ec.ECFieldElement;
import java.math.BigInteger;
import org.bouncycastle.math.ec.ECCurve;

public class SecP224R1Curve extends AbstractFp
{
    public static final BigInteger q;
    private static final int SecP224R1_DEFAULT_COORDS = 2;
    protected SecP224R1Point infinity;
    
    public SecP224R1Curve() {
        super(SecP224R1Curve.q);
        this.infinity = new SecP224R1Point(this, null, null);
        this.a = this.fromBigInteger(new BigInteger(1, Hex.decode("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEFFFFFFFFFFFFFFFFFFFFFFFE")));
        this.b = this.fromBigInteger(new BigInteger(1, Hex.decode("B4050A850C04B3ABF54132565044B0B7D7BFD8BA270B39432355FFB4")));
        this.order = new BigInteger(1, Hex.decode("FFFFFFFFFFFFFFFFFFFFFFFFFFFF16A2E0B8F03E13DD29455C5C2A3D"));
        this.cofactor = BigInteger.valueOf(1L);
        this.coord = 2;
    }
    
    @Override
    protected ECCurve cloneCurve() {
        return new SecP224R1Curve();
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
        return SecP224R1Curve.q;
    }
    
    @Override
    public int getFieldSize() {
        return SecP224R1Curve.q.bitLength();
    }
    
    @Override
    public ECFieldElement fromBigInteger(final BigInteger bigInteger) {
        return new SecP224R1FieldElement(bigInteger);
    }
    
    @Override
    protected ECPoint createRawPoint(final ECFieldElement ecFieldElement, final ECFieldElement ecFieldElement2, final boolean b) {
        return new SecP224R1Point(this, ecFieldElement, ecFieldElement2, b);
    }
    
    @Override
    protected ECPoint createRawPoint(final ECFieldElement ecFieldElement, final ECFieldElement ecFieldElement2, final ECFieldElement[] array, final boolean b) {
        return new SecP224R1Point(this, ecFieldElement, ecFieldElement2, array, b);
    }
    
    @Override
    public ECPoint getInfinity() {
        return this.infinity;
    }
    
    @Override
    public ECLookupTable createCacheSafeLookupTable(final ECPoint[] array, final int n, final int n2) {
        final int[] array2 = new int[n2 * 7 * 2];
        int n3 = 0;
        for (int i = 0; i < n2; ++i) {
            final ECPoint ecPoint = array[n + i];
            Nat224.copy(((SecP224R1FieldElement)ecPoint.getRawXCoord()).x, 0, array2, n3);
            n3 += 7;
            Nat224.copy(((SecP224R1FieldElement)ecPoint.getRawYCoord()).x, 0, array2, n3);
            n3 += 7;
        }
        return new ECLookupTable() {
            public int getSize() {
                return n2;
            }
            
            public ECPoint lookup(final int n) {
                final int[] create = Nat224.create();
                final int[] create2 = Nat224.create();
                int n2 = 0;
                for (int i = 0; i < n2; ++i) {
                    final int n3 = (i ^ n) - 1 >> 31;
                    for (int j = 0; j < 7; ++j) {
                        final int[] array = create;
                        final int n4 = j;
                        array[n4] ^= (array2[n2 + j] & n3);
                        final int[] array2 = create2;
                        final int n5 = j;
                        array2[n5] ^= (array2[n2 + 7 + j] & n3);
                    }
                    n2 += 14;
                }
                return SecP224R1Curve.this.createRawPoint(new SecP224R1FieldElement(create), new SecP224R1FieldElement(create2), false);
            }
        };
    }
    
    static {
        q = new BigInteger(1, Hex.decode("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF000000000000000000000001"));
    }
}
