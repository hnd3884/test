package org.bouncycastle.math.ec;

import org.bouncycastle.math.raw.Nat;
import java.util.Random;
import org.bouncycastle.math.field.FiniteFields;
import org.bouncycastle.util.Integers;
import org.bouncycastle.util.BigIntegers;
import java.util.Hashtable;
import org.bouncycastle.math.ec.endo.GLVEndomorphism;
import org.bouncycastle.math.ec.endo.ECEndomorphism;
import java.math.BigInteger;
import org.bouncycastle.math.field.FiniteField;

public abstract class ECCurve
{
    public static final int COORD_AFFINE = 0;
    public static final int COORD_HOMOGENEOUS = 1;
    public static final int COORD_JACOBIAN = 2;
    public static final int COORD_JACOBIAN_CHUDNOVSKY = 3;
    public static final int COORD_JACOBIAN_MODIFIED = 4;
    public static final int COORD_LAMBDA_AFFINE = 5;
    public static final int COORD_LAMBDA_PROJECTIVE = 6;
    public static final int COORD_SKEWED = 7;
    protected FiniteField field;
    protected ECFieldElement a;
    protected ECFieldElement b;
    protected BigInteger order;
    protected BigInteger cofactor;
    protected int coord;
    protected ECEndomorphism endomorphism;
    protected ECMultiplier multiplier;
    
    public static int[] getAllCoordinateSystems() {
        return new int[] { 0, 1, 2, 3, 4, 5, 6, 7 };
    }
    
    protected ECCurve(final FiniteField field) {
        this.coord = 0;
        this.endomorphism = null;
        this.multiplier = null;
        this.field = field;
    }
    
    public abstract int getFieldSize();
    
    public abstract ECFieldElement fromBigInteger(final BigInteger p0);
    
    public abstract boolean isValidFieldElement(final BigInteger p0);
    
    public synchronized Config configure() {
        return new Config(this.coord, this.endomorphism, this.multiplier);
    }
    
    public ECPoint validatePoint(final BigInteger bigInteger, final BigInteger bigInteger2) {
        final ECPoint point = this.createPoint(bigInteger, bigInteger2);
        if (!point.isValid()) {
            throw new IllegalArgumentException("Invalid point coordinates");
        }
        return point;
    }
    
    @Deprecated
    public ECPoint validatePoint(final BigInteger bigInteger, final BigInteger bigInteger2, final boolean b) {
        final ECPoint point = this.createPoint(bigInteger, bigInteger2, b);
        if (!point.isValid()) {
            throw new IllegalArgumentException("Invalid point coordinates");
        }
        return point;
    }
    
    public ECPoint createPoint(final BigInteger bigInteger, final BigInteger bigInteger2) {
        return this.createPoint(bigInteger, bigInteger2, false);
    }
    
    @Deprecated
    public ECPoint createPoint(final BigInteger bigInteger, final BigInteger bigInteger2, final boolean b) {
        return this.createRawPoint(this.fromBigInteger(bigInteger), this.fromBigInteger(bigInteger2), b);
    }
    
    protected abstract ECCurve cloneCurve();
    
    protected abstract ECPoint createRawPoint(final ECFieldElement p0, final ECFieldElement p1, final boolean p2);
    
    protected abstract ECPoint createRawPoint(final ECFieldElement p0, final ECFieldElement p1, final ECFieldElement[] p2, final boolean p3);
    
    protected ECMultiplier createDefaultMultiplier() {
        if (this.endomorphism instanceof GLVEndomorphism) {
            return new GLVMultiplier(this, (GLVEndomorphism)this.endomorphism);
        }
        return new WNafL2RMultiplier();
    }
    
    public boolean supportsCoordinateSystem(final int n) {
        return n == 0;
    }
    
    public PreCompInfo getPreCompInfo(final ECPoint ecPoint, final String s) {
        this.checkPoint(ecPoint);
        synchronized (ecPoint) {
            final Hashtable preCompTable = ecPoint.preCompTable;
            return (preCompTable == null) ? null : ((PreCompInfo)preCompTable.get(s));
        }
    }
    
    public void setPreCompInfo(final ECPoint ecPoint, final String s, final PreCompInfo preCompInfo) {
        this.checkPoint(ecPoint);
        synchronized (ecPoint) {
            Hashtable preCompTable = ecPoint.preCompTable;
            if (null == preCompTable) {
                preCompTable = (ecPoint.preCompTable = new Hashtable(4));
            }
            preCompTable.put(s, preCompInfo);
        }
    }
    
    public ECPoint importPoint(ECPoint normalize) {
        if (this == normalize.getCurve()) {
            return normalize;
        }
        if (normalize.isInfinity()) {
            return this.getInfinity();
        }
        normalize = normalize.normalize();
        return this.validatePoint(normalize.getXCoord().toBigInteger(), normalize.getYCoord().toBigInteger(), normalize.withCompression);
    }
    
    public void normalizeAll(final ECPoint[] array) {
        this.normalizeAll(array, 0, array.length, null);
    }
    
    public void normalizeAll(final ECPoint[] array, final int n, final int n2, final ECFieldElement ecFieldElement) {
        this.checkPoints(array, n, n2);
        switch (this.getCoordinateSystem()) {
            case 0:
            case 5: {
                if (ecFieldElement != null) {
                    throw new IllegalArgumentException("'iso' not valid for affine coordinates");
                }
                return;
            }
            default: {
                final ECFieldElement[] array2 = new ECFieldElement[n2];
                final int[] array3 = new int[n2];
                int n3 = 0;
                for (int i = 0; i < n2; ++i) {
                    final ECPoint ecPoint = array[n + i];
                    if (null != ecPoint && (ecFieldElement != null || !ecPoint.isNormalized())) {
                        array2[n3] = ecPoint.getZCoord(0);
                        array3[n3++] = n + i;
                    }
                }
                if (n3 == 0) {
                    return;
                }
                ECAlgorithms.montgomeryTrick(array2, 0, n3, ecFieldElement);
                for (int j = 0; j < n3; ++j) {
                    final int n4 = array3[j];
                    array[n4] = array[n4].normalize(array2[j]);
                }
            }
        }
    }
    
    public abstract ECPoint getInfinity();
    
    public FiniteField getField() {
        return this.field;
    }
    
    public ECFieldElement getA() {
        return this.a;
    }
    
    public ECFieldElement getB() {
        return this.b;
    }
    
    public BigInteger getOrder() {
        return this.order;
    }
    
    public BigInteger getCofactor() {
        return this.cofactor;
    }
    
    public int getCoordinateSystem() {
        return this.coord;
    }
    
    protected abstract ECPoint decompressPoint(final int p0, final BigInteger p1);
    
    public ECEndomorphism getEndomorphism() {
        return this.endomorphism;
    }
    
    public synchronized ECMultiplier getMultiplier() {
        if (this.multiplier == null) {
            this.multiplier = this.createDefaultMultiplier();
        }
        return this.multiplier;
    }
    
    public ECPoint decodePoint(final byte[] array) {
        final int n = (this.getFieldSize() + 7) / 8;
        final byte b = array[0];
        ECPoint ecPoint = null;
        switch (b) {
            case 0: {
                if (array.length != 1) {
                    throw new IllegalArgumentException("Incorrect length for infinity encoding");
                }
                ecPoint = this.getInfinity();
                break;
            }
            case 2:
            case 3: {
                if (array.length != n + 1) {
                    throw new IllegalArgumentException("Incorrect length for compressed encoding");
                }
                ecPoint = this.decompressPoint(b & 0x1, BigIntegers.fromUnsignedByteArray(array, 1, n));
                if (!ecPoint.satisfiesCofactor()) {
                    throw new IllegalArgumentException("Invalid point");
                }
                break;
            }
            case 4: {
                if (array.length != 2 * n + 1) {
                    throw new IllegalArgumentException("Incorrect length for uncompressed encoding");
                }
                ecPoint = this.validatePoint(BigIntegers.fromUnsignedByteArray(array, 1, n), BigIntegers.fromUnsignedByteArray(array, 1 + n, n));
                break;
            }
            case 6:
            case 7: {
                if (array.length != 2 * n + 1) {
                    throw new IllegalArgumentException("Incorrect length for hybrid encoding");
                }
                final BigInteger fromUnsignedByteArray = BigIntegers.fromUnsignedByteArray(array, 1, n);
                final BigInteger fromUnsignedByteArray2 = BigIntegers.fromUnsignedByteArray(array, 1 + n, n);
                if (fromUnsignedByteArray2.testBit(0) != (b == 7)) {
                    throw new IllegalArgumentException("Inconsistent Y coordinate in hybrid encoding");
                }
                ecPoint = this.validatePoint(fromUnsignedByteArray, fromUnsignedByteArray2);
                break;
            }
            default: {
                throw new IllegalArgumentException("Invalid point encoding 0x" + Integer.toString(b, 16));
            }
        }
        if (b != 0 && ecPoint.isInfinity()) {
            throw new IllegalArgumentException("Invalid infinity encoding");
        }
        return ecPoint;
    }
    
    public ECLookupTable createCacheSafeLookupTable(final ECPoint[] array, final int n, final int n2) {
        final int n3 = this.getFieldSize() + 7 >>> 3;
        final byte[] array2 = new byte[n2 * n3 * 2];
        int n4 = 0;
        for (int i = 0; i < n2; ++i) {
            final ECPoint ecPoint = array[n + i];
            final byte[] byteArray = ecPoint.getRawXCoord().toBigInteger().toByteArray();
            final byte[] byteArray2 = ecPoint.getRawYCoord().toBigInteger().toByteArray();
            final int n5 = (byteArray.length > n3) ? 1 : 0;
            final int n6 = byteArray.length - n5;
            final int n7 = (byteArray2.length > n3) ? 1 : 0;
            final int n8 = byteArray2.length - n7;
            System.arraycopy(byteArray, n5, array2, n4 + n3 - n6, n6);
            final int n9 = n4 + n3;
            System.arraycopy(byteArray2, n7, array2, n9 + n3 - n8, n8);
            n4 = n9 + n3;
        }
        return new ECLookupTable() {
            public int getSize() {
                return n2;
            }
            
            public ECPoint lookup(final int n) {
                final byte[] array = new byte[n3];
                final byte[] array2 = new byte[n3];
                int n2 = 0;
                for (int i = 0; i < n2; ++i) {
                    final int n3 = (i ^ n) - 1 >> 31;
                    for (int j = 0; j < n3; ++j) {
                        final byte[] array3 = array;
                        final int n4 = j;
                        array3[n4] ^= (byte)(array2[n2 + j] & n3);
                        final byte[] array4 = array2;
                        final int n5 = j;
                        array4[n5] ^= (byte)(array2[n2 + n3 + j] & n3);
                    }
                    n2 += n3 * 2;
                }
                return ECCurve.this.createRawPoint(ECCurve.this.fromBigInteger(new BigInteger(1, array)), ECCurve.this.fromBigInteger(new BigInteger(1, array2)), false);
            }
        };
    }
    
    protected void checkPoint(final ECPoint ecPoint) {
        if (null == ecPoint || this != ecPoint.getCurve()) {
            throw new IllegalArgumentException("'point' must be non-null and on this curve");
        }
    }
    
    protected void checkPoints(final ECPoint[] array) {
        this.checkPoints(array, 0, array.length);
    }
    
    protected void checkPoints(final ECPoint[] array, final int n, final int n2) {
        if (array == null) {
            throw new IllegalArgumentException("'points' cannot be null");
        }
        if (n < 0 || n2 < 0 || n > array.length - n2) {
            throw new IllegalArgumentException("invalid range specified for 'points'");
        }
        for (int i = 0; i < n2; ++i) {
            final ECPoint ecPoint = array[n + i];
            if (null != ecPoint && this != ecPoint.getCurve()) {
                throw new IllegalArgumentException("'points' entries must be null or on this curve");
            }
        }
    }
    
    public boolean equals(final ECCurve ecCurve) {
        return this == ecCurve || (null != ecCurve && this.getField().equals(ecCurve.getField()) && this.getA().toBigInteger().equals(ecCurve.getA().toBigInteger()) && this.getB().toBigInteger().equals(ecCurve.getB().toBigInteger()));
    }
    
    @Override
    public boolean equals(final Object o) {
        return this == o || (o instanceof ECCurve && this.equals((ECCurve)o));
    }
    
    @Override
    public int hashCode() {
        return this.getField().hashCode() ^ Integers.rotateLeft(this.getA().toBigInteger().hashCode(), 8) ^ Integers.rotateLeft(this.getB().toBigInteger().hashCode(), 16);
    }
    
    public abstract static class AbstractF2m extends ECCurve
    {
        private BigInteger[] si;
        
        public static BigInteger inverse(final int n, final int[] array, final BigInteger bigInteger) {
            return new LongArray(bigInteger).modInverse(n, array).toBigInteger();
        }
        
        private static FiniteField buildField(final int n, final int n2, final int n3, final int n4) {
            if (n2 == 0) {
                throw new IllegalArgumentException("k1 must be > 0");
            }
            if (n3 == 0) {
                if (n4 != 0) {
                    throw new IllegalArgumentException("k3 must be 0 if k2 == 0");
                }
                return FiniteFields.getBinaryExtensionField(new int[] { 0, n2, n });
            }
            else {
                if (n3 <= n2) {
                    throw new IllegalArgumentException("k2 must be > k1");
                }
                if (n4 <= n3) {
                    throw new IllegalArgumentException("k3 must be > k2");
                }
                return FiniteFields.getBinaryExtensionField(new int[] { 0, n2, n3, n4, n });
            }
        }
        
        protected AbstractF2m(final int n, final int n2, final int n3, final int n4) {
            super(buildField(n, n2, n3, n4));
            this.si = null;
        }
        
        @Override
        public boolean isValidFieldElement(final BigInteger bigInteger) {
            return bigInteger != null && bigInteger.signum() >= 0 && bigInteger.bitLength() <= this.getFieldSize();
        }
        
        @Override
        public ECPoint createPoint(final BigInteger bigInteger, final BigInteger bigInteger2, final boolean b) {
            final ECFieldElement fromBigInteger = this.fromBigInteger(bigInteger);
            ECFieldElement ecFieldElement = this.fromBigInteger(bigInteger2);
            switch (this.getCoordinateSystem()) {
                case 5:
                case 6: {
                    if (!fromBigInteger.isZero()) {
                        ecFieldElement = ecFieldElement.divide(fromBigInteger).add(fromBigInteger);
                        break;
                    }
                    if (!ecFieldElement.square().equals(this.getB())) {
                        throw new IllegalArgumentException();
                    }
                    break;
                }
            }
            return this.createRawPoint(fromBigInteger, ecFieldElement, b);
        }
        
        @Override
        protected ECPoint decompressPoint(final int n, final BigInteger bigInteger) {
            final ECFieldElement fromBigInteger = this.fromBigInteger(bigInteger);
            ECFieldElement ecFieldElement = null;
            if (fromBigInteger.isZero()) {
                ecFieldElement = this.getB().sqrt();
            }
            else {
                ECFieldElement ecFieldElement2 = this.solveQuadraticEquation(fromBigInteger.square().invert().multiply(this.getB()).add(this.getA()).add(fromBigInteger));
                if (ecFieldElement2 != null) {
                    if (ecFieldElement2.testBitZero() != (n == 1)) {
                        ecFieldElement2 = ecFieldElement2.addOne();
                    }
                    switch (this.getCoordinateSystem()) {
                        case 5:
                        case 6: {
                            ecFieldElement = ecFieldElement2.add(fromBigInteger);
                            break;
                        }
                        default: {
                            ecFieldElement = ecFieldElement2.multiply(fromBigInteger);
                            break;
                        }
                    }
                }
            }
            if (ecFieldElement == null) {
                throw new IllegalArgumentException("Invalid point compression");
            }
            return this.createRawPoint(fromBigInteger, ecFieldElement, true);
        }
        
        private ECFieldElement solveQuadraticEquation(final ECFieldElement ecFieldElement) {
            if (ecFieldElement.isZero()) {
                return ecFieldElement;
            }
            final ECFieldElement fromBigInteger = this.fromBigInteger(ECConstants.ZERO);
            final int fieldSize = this.getFieldSize();
            final Random random = new Random();
            ECFieldElement add;
            do {
                final ECFieldElement fromBigInteger2 = this.fromBigInteger(new BigInteger(fieldSize, random));
                add = fromBigInteger;
                ECFieldElement add2 = ecFieldElement;
                for (int i = 1; i < fieldSize; ++i) {
                    final ECFieldElement square = add2.square();
                    add = add.square().add(square.multiply(fromBigInteger2));
                    add2 = square.add(ecFieldElement);
                }
                if (!add2.isZero()) {
                    return null;
                }
            } while (add.square().add(add).isZero());
            return add;
        }
        
        synchronized BigInteger[] getSi() {
            if (this.si == null) {
                this.si = Tnaf.getSi(this);
            }
            return this.si;
        }
        
        public boolean isKoblitz() {
            return this.order != null && this.cofactor != null && this.b.isOne() && (this.a.isZero() || this.a.isOne());
        }
    }
    
    public abstract static class AbstractFp extends ECCurve
    {
        protected AbstractFp(final BigInteger bigInteger) {
            super(FiniteFields.getPrimeField(bigInteger));
        }
        
        @Override
        public boolean isValidFieldElement(final BigInteger bigInteger) {
            return bigInteger != null && bigInteger.signum() >= 0 && bigInteger.compareTo(this.getField().getCharacteristic()) < 0;
        }
        
        @Override
        protected ECPoint decompressPoint(final int n, final BigInteger bigInteger) {
            final ECFieldElement fromBigInteger = this.fromBigInteger(bigInteger);
            ECFieldElement ecFieldElement = fromBigInteger.square().add(this.a).multiply(fromBigInteger).add(this.b).sqrt();
            if (ecFieldElement == null) {
                throw new IllegalArgumentException("Invalid point compression");
            }
            if (ecFieldElement.testBitZero() != (n == 1)) {
                ecFieldElement = ecFieldElement.negate();
            }
            return this.createRawPoint(fromBigInteger, ecFieldElement, true);
        }
    }
    
    public class Config
    {
        protected int coord;
        protected ECEndomorphism endomorphism;
        protected ECMultiplier multiplier;
        
        Config(final int coord, final ECEndomorphism endomorphism, final ECMultiplier multiplier) {
            this.coord = coord;
            this.endomorphism = endomorphism;
            this.multiplier = multiplier;
        }
        
        public Config setCoordinateSystem(final int coord) {
            this.coord = coord;
            return this;
        }
        
        public Config setEndomorphism(final ECEndomorphism endomorphism) {
            this.endomorphism = endomorphism;
            return this;
        }
        
        public Config setMultiplier(final ECMultiplier multiplier) {
            this.multiplier = multiplier;
            return this;
        }
        
        public ECCurve create() {
            if (!ECCurve.this.supportsCoordinateSystem(this.coord)) {
                throw new IllegalStateException("unsupported coordinate system");
            }
            final ECCurve cloneCurve = ECCurve.this.cloneCurve();
            if (cloneCurve == ECCurve.this) {
                throw new IllegalStateException("implementation returned current curve");
            }
            synchronized (cloneCurve) {
                cloneCurve.coord = this.coord;
                cloneCurve.endomorphism = this.endomorphism;
                cloneCurve.multiplier = this.multiplier;
            }
            return cloneCurve;
        }
    }
    
    public static class F2m extends AbstractF2m
    {
        private static final int F2M_DEFAULT_COORDS = 6;
        private int m;
        private int k1;
        private int k2;
        private int k3;
        private ECPoint.F2m infinity;
        
        public F2m(final int n, final int n2, final BigInteger bigInteger, final BigInteger bigInteger2) {
            this(n, n2, 0, 0, bigInteger, bigInteger2, null, null);
        }
        
        public F2m(final int n, final int n2, final BigInteger bigInteger, final BigInteger bigInteger2, final BigInteger bigInteger3, final BigInteger bigInteger4) {
            this(n, n2, 0, 0, bigInteger, bigInteger2, bigInteger3, bigInteger4);
        }
        
        public F2m(final int n, final int n2, final int n3, final int n4, final BigInteger bigInteger, final BigInteger bigInteger2) {
            this(n, n2, n3, n4, bigInteger, bigInteger2, null, null);
        }
        
        public F2m(final int m, final int k1, final int k2, final int k3, final BigInteger bigInteger, final BigInteger bigInteger2, final BigInteger order, final BigInteger cofactor) {
            super(m, k1, k2, k3);
            this.m = m;
            this.k1 = k1;
            this.k2 = k2;
            this.k3 = k3;
            this.order = order;
            this.cofactor = cofactor;
            this.infinity = new ECPoint.F2m(this, null, null);
            this.a = this.fromBigInteger(bigInteger);
            this.b = this.fromBigInteger(bigInteger2);
            this.coord = 6;
        }
        
        protected F2m(final int m, final int k1, final int k2, final int k3, final ECFieldElement a, final ECFieldElement b, final BigInteger order, final BigInteger cofactor) {
            super(m, k1, k2, k3);
            this.m = m;
            this.k1 = k1;
            this.k2 = k2;
            this.k3 = k3;
            this.order = order;
            this.cofactor = cofactor;
            this.infinity = new ECPoint.F2m(this, null, null);
            this.a = a;
            this.b = b;
            this.coord = 6;
        }
        
        @Override
        protected ECCurve cloneCurve() {
            return new F2m(this.m, this.k1, this.k2, this.k3, this.a, this.b, this.order, this.cofactor);
        }
        
        @Override
        public boolean supportsCoordinateSystem(final int n) {
            switch (n) {
                case 0:
                case 1:
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
            if (this.isKoblitz()) {
                return new WTauNafMultiplier();
            }
            return super.createDefaultMultiplier();
        }
        
        @Override
        public int getFieldSize() {
            return this.m;
        }
        
        @Override
        public ECFieldElement fromBigInteger(final BigInteger bigInteger) {
            return new ECFieldElement.F2m(this.m, this.k1, this.k2, this.k3, bigInteger);
        }
        
        @Override
        protected ECPoint createRawPoint(final ECFieldElement ecFieldElement, final ECFieldElement ecFieldElement2, final boolean b) {
            return new ECPoint.F2m(this, ecFieldElement, ecFieldElement2, b);
        }
        
        @Override
        protected ECPoint createRawPoint(final ECFieldElement ecFieldElement, final ECFieldElement ecFieldElement2, final ECFieldElement[] array, final boolean b) {
            return new ECPoint.F2m(this, ecFieldElement, ecFieldElement2, array, b);
        }
        
        @Override
        public ECPoint getInfinity() {
            return this.infinity;
        }
        
        public int getM() {
            return this.m;
        }
        
        public boolean isTrinomial() {
            return this.k2 == 0 && this.k3 == 0;
        }
        
        public int getK1() {
            return this.k1;
        }
        
        public int getK2() {
            return this.k2;
        }
        
        public int getK3() {
            return this.k3;
        }
        
        @Deprecated
        public BigInteger getN() {
            return this.order;
        }
        
        @Deprecated
        public BigInteger getH() {
            return this.cofactor;
        }
        
        @Override
        public ECLookupTable createCacheSafeLookupTable(final ECPoint[] array, final int n, final int n2) {
            final int n3 = this.m + 63 >>> 6;
            final int[] array2 = this.isTrinomial() ? new int[] { this.k1 } : new int[] { this.k1, this.k2, this.k3 };
            final long[] array3 = new long[n2 * n3 * 2];
            int n4 = 0;
            for (int i = 0; i < n2; ++i) {
                final ECPoint ecPoint = array[n + i];
                ((ECFieldElement.F2m)ecPoint.getRawXCoord()).x.copyTo(array3, n4);
                final int n5 = n4 + n3;
                ((ECFieldElement.F2m)ecPoint.getRawYCoord()).x.copyTo(array3, n5);
                n4 = n5 + n3;
            }
            return new ECLookupTable() {
                public int getSize() {
                    return n2;
                }
                
                public ECPoint lookup(final int n) {
                    final long[] create64 = Nat.create64(n3);
                    final long[] create65 = Nat.create64(n3);
                    int n2 = 0;
                    for (int i = 0; i < n2; ++i) {
                        final long n3 = (i ^ n) - 1 >> 31;
                        for (int j = 0; j < n3; ++j) {
                            final long[] array = create64;
                            final int n4 = j;
                            array[n4] ^= (array3[n2 + j] & n3);
                            final long[] array2 = create65;
                            final int n5 = j;
                            array2[n5] ^= (array3[n2 + n3 + j] & n3);
                        }
                        n2 += n3 * 2;
                    }
                    return F2m.this.createRawPoint(new ECFieldElement.F2m(F2m.this.m, array2, new LongArray(create64)), new ECFieldElement.F2m(F2m.this.m, array2, new LongArray(create65)), false);
                }
            };
        }
    }
    
    public static class Fp extends AbstractFp
    {
        private static final int FP_DEFAULT_COORDS = 4;
        BigInteger q;
        BigInteger r;
        ECPoint.Fp infinity;
        
        public Fp(final BigInteger bigInteger, final BigInteger bigInteger2, final BigInteger bigInteger3) {
            this(bigInteger, bigInteger2, bigInteger3, null, null);
        }
        
        public Fp(final BigInteger q, final BigInteger bigInteger, final BigInteger bigInteger2, final BigInteger order, final BigInteger cofactor) {
            super(q);
            this.q = q;
            this.r = ECFieldElement.Fp.calculateResidue(q);
            this.infinity = new ECPoint.Fp(this, null, null);
            this.a = this.fromBigInteger(bigInteger);
            this.b = this.fromBigInteger(bigInteger2);
            this.order = order;
            this.cofactor = cofactor;
            this.coord = 4;
        }
        
        protected Fp(final BigInteger bigInteger, final BigInteger bigInteger2, final ECFieldElement ecFieldElement, final ECFieldElement ecFieldElement2) {
            this(bigInteger, bigInteger2, ecFieldElement, ecFieldElement2, null, null);
        }
        
        protected Fp(final BigInteger q, final BigInteger r, final ECFieldElement a, final ECFieldElement b, final BigInteger order, final BigInteger cofactor) {
            super(q);
            this.q = q;
            this.r = r;
            this.infinity = new ECPoint.Fp(this, null, null);
            this.a = a;
            this.b = b;
            this.order = order;
            this.cofactor = cofactor;
            this.coord = 4;
        }
        
        @Override
        protected ECCurve cloneCurve() {
            return new Fp(this.q, this.r, this.a, this.b, this.order, this.cofactor);
        }
        
        @Override
        public boolean supportsCoordinateSystem(final int n) {
            switch (n) {
                case 0:
                case 1:
                case 2:
                case 4: {
                    return true;
                }
                default: {
                    return false;
                }
            }
        }
        
        public BigInteger getQ() {
            return this.q;
        }
        
        @Override
        public int getFieldSize() {
            return this.q.bitLength();
        }
        
        @Override
        public ECFieldElement fromBigInteger(final BigInteger bigInteger) {
            return new ECFieldElement.Fp(this.q, this.r, bigInteger);
        }
        
        @Override
        protected ECPoint createRawPoint(final ECFieldElement ecFieldElement, final ECFieldElement ecFieldElement2, final boolean b) {
            return new ECPoint.Fp(this, ecFieldElement, ecFieldElement2, b);
        }
        
        @Override
        protected ECPoint createRawPoint(final ECFieldElement ecFieldElement, final ECFieldElement ecFieldElement2, final ECFieldElement[] array, final boolean b) {
            return new ECPoint.Fp(this, ecFieldElement, ecFieldElement2, array, b);
        }
        
        @Override
        public ECPoint importPoint(final ECPoint ecPoint) {
            if (this != ecPoint.getCurve() && this.getCoordinateSystem() == 2 && !ecPoint.isInfinity()) {
                switch (ecPoint.getCurve().getCoordinateSystem()) {
                    case 2:
                    case 3:
                    case 4: {
                        return new ECPoint.Fp(this, this.fromBigInteger(ecPoint.x.toBigInteger()), this.fromBigInteger(ecPoint.y.toBigInteger()), new ECFieldElement[] { this.fromBigInteger(ecPoint.zs[0].toBigInteger()) }, ecPoint.withCompression);
                    }
                }
            }
            return super.importPoint(ecPoint);
        }
        
        @Override
        public ECPoint getInfinity() {
            return this.infinity;
        }
    }
}
