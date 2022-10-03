package org.bouncycastle.math.ec.custom.sec;

import org.bouncycastle.math.raw.Nat256;
import org.bouncycastle.math.ec.ECLookupTable;
import org.bouncycastle.math.ec.ECPoint;
import java.math.BigInteger;
import org.bouncycastle.util.encoders.Hex;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.ec.ECCurve;

public class SecT193R1Curve extends AbstractF2m
{
    private static final int SecT193R1_DEFAULT_COORDS = 6;
    protected SecT193R1Point infinity;
    
    public SecT193R1Curve() {
        super(193, 15, 0, 0);
        this.infinity = new SecT193R1Point(this, null, null);
        this.a = this.fromBigInteger(new BigInteger(1, Hex.decode("0017858FEB7A98975169E171F77B4087DE098AC8A911DF7B01")));
        this.b = this.fromBigInteger(new BigInteger(1, Hex.decode("00FDFB49BFE6C3A89FACADAA7A1E5BBC7CC1C2E5D831478814")));
        this.order = new BigInteger(1, Hex.decode("01000000000000000000000000C7F34A778F443ACC920EBA49"));
        this.cofactor = BigInteger.valueOf(2L);
        this.coord = 6;
    }
    
    @Override
    protected ECCurve cloneCurve() {
        return new SecT193R1Curve();
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
        return 193;
    }
    
    @Override
    public ECFieldElement fromBigInteger(final BigInteger bigInteger) {
        return new SecT193FieldElement(bigInteger);
    }
    
    @Override
    protected ECPoint createRawPoint(final ECFieldElement ecFieldElement, final ECFieldElement ecFieldElement2, final boolean b) {
        return new SecT193R1Point(this, ecFieldElement, ecFieldElement2, b);
    }
    
    @Override
    protected ECPoint createRawPoint(final ECFieldElement ecFieldElement, final ECFieldElement ecFieldElement2, final ECFieldElement[] array, final boolean b) {
        return new SecT193R1Point(this, ecFieldElement, ecFieldElement2, array, b);
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
        return 193;
    }
    
    public boolean isTrinomial() {
        return true;
    }
    
    public int getK1() {
        return 15;
    }
    
    public int getK2() {
        return 0;
    }
    
    public int getK3() {
        return 0;
    }
    
    @Override
    public ECLookupTable createCacheSafeLookupTable(final ECPoint[] array, final int n, final int n2) {
        final long[] array2 = new long[n2 * 4 * 2];
        int n3 = 0;
        for (int i = 0; i < n2; ++i) {
            final ECPoint ecPoint = array[n + i];
            Nat256.copy64(((SecT193FieldElement)ecPoint.getRawXCoord()).x, 0, array2, n3);
            n3 += 4;
            Nat256.copy64(((SecT193FieldElement)ecPoint.getRawYCoord()).x, 0, array2, n3);
            n3 += 4;
        }
        return new ECLookupTable() {
            public int getSize() {
                return n2;
            }
            
            public ECPoint lookup(final int n) {
                final long[] create64 = Nat256.create64();
                final long[] create65 = Nat256.create64();
                int n2 = 0;
                for (int i = 0; i < n2; ++i) {
                    final long n3 = (i ^ n) - 1 >> 31;
                    for (int j = 0; j < 4; ++j) {
                        final long[] array = create64;
                        final int n4 = j;
                        array[n4] ^= (array2[n2 + j] & n3);
                        final long[] array2 = create65;
                        final int n5 = j;
                        array2[n5] ^= (array2[n2 + 4 + j] & n3);
                    }
                    n2 += 8;
                }
                return SecT193R1Curve.this.createRawPoint(new SecT193FieldElement(create64), new SecT193FieldElement(create65), false);
            }
        };
    }
}
