package org.bouncycastle.math.ec.custom.gm;

import org.bouncycastle.math.raw.Nat256;
import org.bouncycastle.math.ec.ECLookupTable;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.util.encoders.Hex;
import org.bouncycastle.math.ec.ECFieldElement;
import java.math.BigInteger;
import org.bouncycastle.math.ec.ECCurve;

public class SM2P256V1Curve extends AbstractFp
{
    public static final BigInteger q;
    private static final int SM2P256V1_DEFAULT_COORDS = 2;
    protected SM2P256V1Point infinity;
    
    public SM2P256V1Curve() {
        super(SM2P256V1Curve.q);
        this.infinity = new SM2P256V1Point(this, null, null);
        this.a = this.fromBigInteger(new BigInteger(1, Hex.decode("FFFFFFFEFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF00000000FFFFFFFFFFFFFFFC")));
        this.b = this.fromBigInteger(new BigInteger(1, Hex.decode("28E9FA9E9D9F5E344D5A9E4BCF6509A7F39789F515AB8F92DDBCBD414D940E93")));
        this.order = new BigInteger(1, Hex.decode("FFFFFFFEFFFFFFFFFFFFFFFFFFFFFFFF7203DF6B21C6052B53BBF40939D54123"));
        this.cofactor = BigInteger.valueOf(1L);
        this.coord = 2;
    }
    
    @Override
    protected ECCurve cloneCurve() {
        return new SM2P256V1Curve();
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
        return SM2P256V1Curve.q;
    }
    
    @Override
    public int getFieldSize() {
        return SM2P256V1Curve.q.bitLength();
    }
    
    @Override
    public ECFieldElement fromBigInteger(final BigInteger bigInteger) {
        return new SM2P256V1FieldElement(bigInteger);
    }
    
    @Override
    protected ECPoint createRawPoint(final ECFieldElement ecFieldElement, final ECFieldElement ecFieldElement2, final boolean b) {
        return new SM2P256V1Point(this, ecFieldElement, ecFieldElement2, b);
    }
    
    @Override
    protected ECPoint createRawPoint(final ECFieldElement ecFieldElement, final ECFieldElement ecFieldElement2, final ECFieldElement[] array, final boolean b) {
        return new SM2P256V1Point(this, ecFieldElement, ecFieldElement2, array, b);
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
            Nat256.copy(((SM2P256V1FieldElement)ecPoint.getRawXCoord()).x, 0, array2, n3);
            n3 += 8;
            Nat256.copy(((SM2P256V1FieldElement)ecPoint.getRawYCoord()).x, 0, array2, n3);
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
                return SM2P256V1Curve.this.createRawPoint(new SM2P256V1FieldElement(create), new SM2P256V1FieldElement(create2), false);
            }
        };
    }
    
    static {
        q = new BigInteger(1, Hex.decode("FFFFFFFEFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF00000000FFFFFFFFFFFFFFFF"));
    }
}
