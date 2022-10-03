package org.bouncycastle.math.ec.custom.sec;

import org.bouncycastle.math.raw.Nat;
import org.bouncycastle.math.ec.ECLookupTable;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.util.encoders.Hex;
import org.bouncycastle.math.ec.ECFieldElement;
import java.math.BigInteger;
import org.bouncycastle.math.ec.ECCurve;

public class SecP384R1Curve extends AbstractFp
{
    public static final BigInteger q;
    private static final int SecP384R1_DEFAULT_COORDS = 2;
    protected SecP384R1Point infinity;
    
    public SecP384R1Curve() {
        super(SecP384R1Curve.q);
        this.infinity = new SecP384R1Point(this, null, null);
        this.a = this.fromBigInteger(new BigInteger(1, Hex.decode("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEFFFFFFFF0000000000000000FFFFFFFC")));
        this.b = this.fromBigInteger(new BigInteger(1, Hex.decode("B3312FA7E23EE7E4988E056BE3F82D19181D9C6EFE8141120314088F5013875AC656398D8A2ED19D2A85C8EDD3EC2AEF")));
        this.order = new BigInteger(1, Hex.decode("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFC7634D81F4372DDF581A0DB248B0A77AECEC196ACCC52973"));
        this.cofactor = BigInteger.valueOf(1L);
        this.coord = 2;
    }
    
    @Override
    protected ECCurve cloneCurve() {
        return new SecP384R1Curve();
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
        return SecP384R1Curve.q;
    }
    
    @Override
    public int getFieldSize() {
        return SecP384R1Curve.q.bitLength();
    }
    
    @Override
    public ECFieldElement fromBigInteger(final BigInteger bigInteger) {
        return new SecP384R1FieldElement(bigInteger);
    }
    
    @Override
    protected ECPoint createRawPoint(final ECFieldElement ecFieldElement, final ECFieldElement ecFieldElement2, final boolean b) {
        return new SecP384R1Point(this, ecFieldElement, ecFieldElement2, b);
    }
    
    @Override
    protected ECPoint createRawPoint(final ECFieldElement ecFieldElement, final ECFieldElement ecFieldElement2, final ECFieldElement[] array, final boolean b) {
        return new SecP384R1Point(this, ecFieldElement, ecFieldElement2, array, b);
    }
    
    @Override
    public ECPoint getInfinity() {
        return this.infinity;
    }
    
    @Override
    public ECLookupTable createCacheSafeLookupTable(final ECPoint[] array, final int n, final int n2) {
        final int[] array2 = new int[n2 * 12 * 2];
        int n3 = 0;
        for (int i = 0; i < n2; ++i) {
            final ECPoint ecPoint = array[n + i];
            Nat.copy(12, ((SecP384R1FieldElement)ecPoint.getRawXCoord()).x, 0, array2, n3);
            n3 += 12;
            Nat.copy(12, ((SecP384R1FieldElement)ecPoint.getRawYCoord()).x, 0, array2, n3);
            n3 += 12;
        }
        return new ECLookupTable() {
            public int getSize() {
                return n2;
            }
            
            public ECPoint lookup(final int n) {
                final int[] create = Nat.create(12);
                final int[] create2 = Nat.create(12);
                int n2 = 0;
                for (int i = 0; i < n2; ++i) {
                    final int n3 = (i ^ n) - 1 >> 31;
                    for (int j = 0; j < 12; ++j) {
                        final int[] array = create;
                        final int n4 = j;
                        array[n4] ^= (array2[n2 + j] & n3);
                        final int[] array2 = create2;
                        final int n5 = j;
                        array2[n5] ^= (array2[n2 + 12 + j] & n3);
                    }
                    n2 += 24;
                }
                return SecP384R1Curve.this.createRawPoint(new SecP384R1FieldElement(create), new SecP384R1FieldElement(create2), false);
            }
        };
    }
    
    static {
        q = new BigInteger(1, Hex.decode("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEFFFFFFFF0000000000000000FFFFFFFF"));
    }
}
