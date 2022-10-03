package org.bouncycastle.math.ec.custom.sec;

import org.bouncycastle.math.raw.Nat576;
import org.bouncycastle.math.ec.ECLookupTable;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.ec.WTauNafMultiplier;
import org.bouncycastle.math.ec.ECMultiplier;
import org.bouncycastle.util.encoders.Hex;
import java.math.BigInteger;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.ec.ECCurve;

public class SecT571K1Curve extends AbstractF2m
{
    private static final int SecT571K1_DEFAULT_COORDS = 6;
    protected SecT571K1Point infinity;
    
    public SecT571K1Curve() {
        super(571, 2, 5, 10);
        this.infinity = new SecT571K1Point(this, null, null);
        this.a = this.fromBigInteger(BigInteger.valueOf(0L));
        this.b = this.fromBigInteger(BigInteger.valueOf(1L));
        this.order = new BigInteger(1, Hex.decode("020000000000000000000000000000000000000000000000000000000000000000000000131850E1F19A63E4B391A8DB917F4138B630D84BE5D639381E91DEB45CFE778F637C1001"));
        this.cofactor = BigInteger.valueOf(4L);
        this.coord = 6;
    }
    
    @Override
    protected ECCurve cloneCurve() {
        return new SecT571K1Curve();
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
    protected ECMultiplier createDefaultMultiplier() {
        return new WTauNafMultiplier();
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
        return new SecT571K1Point(this, ecFieldElement, ecFieldElement2, b);
    }
    
    @Override
    protected ECPoint createRawPoint(final ECFieldElement ecFieldElement, final ECFieldElement ecFieldElement2, final ECFieldElement[] array, final boolean b) {
        return new SecT571K1Point(this, ecFieldElement, ecFieldElement2, array, b);
    }
    
    @Override
    public ECPoint getInfinity() {
        return this.infinity;
    }
    
    @Override
    public boolean isKoblitz() {
        return true;
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
                return SecT571K1Curve.this.createRawPoint(new SecT571FieldElement(create64), new SecT571FieldElement(create65), false);
            }
        };
    }
}
