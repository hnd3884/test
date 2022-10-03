package org.bouncycastle.math.ec.custom.djb;

import org.bouncycastle.math.raw.Nat256;
import org.bouncycastle.math.ec.ECLookupTable;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.util.encoders.Hex;
import org.bouncycastle.math.ec.ECFieldElement;
import java.math.BigInteger;
import org.bouncycastle.math.ec.ECCurve;

public class Curve25519 extends AbstractFp
{
    public static final BigInteger q;
    private static final int Curve25519_DEFAULT_COORDS = 4;
    protected Curve25519Point infinity;
    
    public Curve25519() {
        super(Curve25519.q);
        this.infinity = new Curve25519Point(this, null, null);
        this.a = this.fromBigInteger(new BigInteger(1, Hex.decode("2AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA984914A144")));
        this.b = this.fromBigInteger(new BigInteger(1, Hex.decode("7B425ED097B425ED097B425ED097B425ED097B425ED097B4260B5E9C7710C864")));
        this.order = new BigInteger(1, Hex.decode("1000000000000000000000000000000014DEF9DEA2F79CD65812631A5CF5D3ED"));
        this.cofactor = BigInteger.valueOf(8L);
        this.coord = 4;
    }
    
    @Override
    protected ECCurve cloneCurve() {
        return new Curve25519();
    }
    
    @Override
    public boolean supportsCoordinateSystem(final int n) {
        switch (n) {
            case 4: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    public BigInteger getQ() {
        return Curve25519.q;
    }
    
    @Override
    public int getFieldSize() {
        return Curve25519.q.bitLength();
    }
    
    @Override
    public ECFieldElement fromBigInteger(final BigInteger bigInteger) {
        return new Curve25519FieldElement(bigInteger);
    }
    
    @Override
    protected ECPoint createRawPoint(final ECFieldElement ecFieldElement, final ECFieldElement ecFieldElement2, final boolean b) {
        return new Curve25519Point(this, ecFieldElement, ecFieldElement2, b);
    }
    
    @Override
    protected ECPoint createRawPoint(final ECFieldElement ecFieldElement, final ECFieldElement ecFieldElement2, final ECFieldElement[] array, final boolean b) {
        return new Curve25519Point(this, ecFieldElement, ecFieldElement2, array, b);
    }
    
    @Override
    public ECPoint getInfinity() {
        return this.infinity;
    }
    
    @Override
    public ECLookupTable createCacheSafeLookupTable(final ECPoint[] array, final int n, final int n2) {
        final int[] array2 = new int[n2 * 8 * 2];
        int n3 = 0;
        for (int i = 0; i < n2; ++i) {
            final ECPoint ecPoint = array[n + i];
            Nat256.copy(((Curve25519FieldElement)ecPoint.getRawXCoord()).x, 0, array2, n3);
            n3 += 8;
            Nat256.copy(((Curve25519FieldElement)ecPoint.getRawYCoord()).x, 0, array2, n3);
            n3 += 8;
        }
        return new ECLookupTable() {
            public int getSize() {
                return n2;
            }
            
            public ECPoint lookup(final int n) {
                final int[] create = Nat256.create();
                final int[] create2 = Nat256.create();
                int n2 = 0;
                for (int i = 0; i < n2; ++i) {
                    final int n3 = (i ^ n) - 1 >> 31;
                    for (int j = 0; j < 8; ++j) {
                        final int[] array = create;
                        final int n4 = j;
                        array[n4] ^= (array2[n2 + j] & n3);
                        final int[] array2 = create2;
                        final int n5 = j;
                        array2[n5] ^= (array2[n2 + 8 + j] & n3);
                    }
                    n2 += 16;
                }
                return Curve25519.this.createRawPoint(new Curve25519FieldElement(create), new Curve25519FieldElement(create2), false);
            }
        };
    }
    
    static {
        q = Nat256.toBigInteger(Curve25519Field.P);
    }
}
