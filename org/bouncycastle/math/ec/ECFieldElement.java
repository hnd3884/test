package org.bouncycastle.math.ec;

import org.bouncycastle.math.raw.Mod;
import org.bouncycastle.math.raw.Nat;
import java.util.Random;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.BigIntegers;
import java.math.BigInteger;

public abstract class ECFieldElement implements ECConstants
{
    public abstract BigInteger toBigInteger();
    
    public abstract String getFieldName();
    
    public abstract int getFieldSize();
    
    public abstract ECFieldElement add(final ECFieldElement p0);
    
    public abstract ECFieldElement addOne();
    
    public abstract ECFieldElement subtract(final ECFieldElement p0);
    
    public abstract ECFieldElement multiply(final ECFieldElement p0);
    
    public abstract ECFieldElement divide(final ECFieldElement p0);
    
    public abstract ECFieldElement negate();
    
    public abstract ECFieldElement square();
    
    public abstract ECFieldElement invert();
    
    public abstract ECFieldElement sqrt();
    
    public int bitLength() {
        return this.toBigInteger().bitLength();
    }
    
    public boolean isOne() {
        return this.bitLength() == 1;
    }
    
    public boolean isZero() {
        return 0 == this.toBigInteger().signum();
    }
    
    public ECFieldElement multiplyMinusProduct(final ECFieldElement ecFieldElement, final ECFieldElement ecFieldElement2, final ECFieldElement ecFieldElement3) {
        return this.multiply(ecFieldElement).subtract(ecFieldElement2.multiply(ecFieldElement3));
    }
    
    public ECFieldElement multiplyPlusProduct(final ECFieldElement ecFieldElement, final ECFieldElement ecFieldElement2, final ECFieldElement ecFieldElement3) {
        return this.multiply(ecFieldElement).add(ecFieldElement2.multiply(ecFieldElement3));
    }
    
    public ECFieldElement squareMinusProduct(final ECFieldElement ecFieldElement, final ECFieldElement ecFieldElement2) {
        return this.square().subtract(ecFieldElement.multiply(ecFieldElement2));
    }
    
    public ECFieldElement squarePlusProduct(final ECFieldElement ecFieldElement, final ECFieldElement ecFieldElement2) {
        return this.square().add(ecFieldElement.multiply(ecFieldElement2));
    }
    
    public ECFieldElement squarePow(final int n) {
        ECFieldElement square = this;
        for (int i = 0; i < n; ++i) {
            square = square.square();
        }
        return square;
    }
    
    public boolean testBitZero() {
        return this.toBigInteger().testBit(0);
    }
    
    @Override
    public String toString() {
        return this.toBigInteger().toString(16);
    }
    
    public byte[] getEncoded() {
        return BigIntegers.asUnsignedByteArray((this.getFieldSize() + 7) / 8, this.toBigInteger());
    }
    
    public static class F2m extends ECFieldElement
    {
        public static final int GNB = 1;
        public static final int TPB = 2;
        public static final int PPB = 3;
        private int representation;
        private int m;
        private int[] ks;
        LongArray x;
        
        @Deprecated
        public F2m(final int m, final int n, final int n2, final int n3, final BigInteger bigInteger) {
            if (bigInteger == null || bigInteger.signum() < 0 || bigInteger.bitLength() > m) {
                throw new IllegalArgumentException("x value invalid in F2m field element");
            }
            if (n2 == 0 && n3 == 0) {
                this.representation = 2;
                this.ks = new int[] { n };
            }
            else {
                if (n2 >= n3) {
                    throw new IllegalArgumentException("k2 must be smaller than k3");
                }
                if (n2 <= 0) {
                    throw new IllegalArgumentException("k2 must be larger than 0");
                }
                this.representation = 3;
                this.ks = new int[] { n, n2, n3 };
            }
            this.m = m;
            this.x = new LongArray(bigInteger);
        }
        
        @Deprecated
        public F2m(final int n, final int n2, final BigInteger bigInteger) {
            this(n, n2, 0, 0, bigInteger);
        }
        
        F2m(final int m, final int[] ks, final LongArray x) {
            this.m = m;
            this.representation = ((ks.length == 1) ? 2 : 3);
            this.ks = ks;
            this.x = x;
        }
        
        @Override
        public int bitLength() {
            return this.x.degree();
        }
        
        @Override
        public boolean isOne() {
            return this.x.isOne();
        }
        
        @Override
        public boolean isZero() {
            return this.x.isZero();
        }
        
        @Override
        public boolean testBitZero() {
            return this.x.testBitZero();
        }
        
        @Override
        public BigInteger toBigInteger() {
            return this.x.toBigInteger();
        }
        
        @Override
        public String getFieldName() {
            return "F2m";
        }
        
        @Override
        public int getFieldSize() {
            return this.m;
        }
        
        public static void checkFieldElements(final ECFieldElement ecFieldElement, final ECFieldElement ecFieldElement2) {
            if (!(ecFieldElement instanceof F2m) || !(ecFieldElement2 instanceof F2m)) {
                throw new IllegalArgumentException("Field elements are not both instances of ECFieldElement.F2m");
            }
            final F2m f2m = (F2m)ecFieldElement;
            final F2m f2m2 = (F2m)ecFieldElement2;
            if (f2m.representation != f2m2.representation) {
                throw new IllegalArgumentException("One of the F2m field elements has incorrect representation");
            }
            if (f2m.m != f2m2.m || !Arrays.areEqual(f2m.ks, f2m2.ks)) {
                throw new IllegalArgumentException("Field elements are not elements of the same field F2m");
            }
        }
        
        @Override
        public ECFieldElement add(final ECFieldElement ecFieldElement) {
            final LongArray longArray = (LongArray)this.x.clone();
            longArray.addShiftedByWords(((F2m)ecFieldElement).x, 0);
            return new F2m(this.m, this.ks, longArray);
        }
        
        @Override
        public ECFieldElement addOne() {
            return new F2m(this.m, this.ks, this.x.addOne());
        }
        
        @Override
        public ECFieldElement subtract(final ECFieldElement ecFieldElement) {
            return this.add(ecFieldElement);
        }
        
        @Override
        public ECFieldElement multiply(final ECFieldElement ecFieldElement) {
            return new F2m(this.m, this.ks, this.x.modMultiply(((F2m)ecFieldElement).x, this.m, this.ks));
        }
        
        @Override
        public ECFieldElement multiplyMinusProduct(final ECFieldElement ecFieldElement, final ECFieldElement ecFieldElement2, final ECFieldElement ecFieldElement3) {
            return this.multiplyPlusProduct(ecFieldElement, ecFieldElement2, ecFieldElement3);
        }
        
        @Override
        public ECFieldElement multiplyPlusProduct(final ECFieldElement ecFieldElement, final ECFieldElement ecFieldElement2, final ECFieldElement ecFieldElement3) {
            final LongArray x = this.x;
            final LongArray x2 = ((F2m)ecFieldElement).x;
            final LongArray x3 = ((F2m)ecFieldElement2).x;
            final LongArray x4 = ((F2m)ecFieldElement3).x;
            LongArray multiply = x.multiply(x2, this.m, this.ks);
            final LongArray multiply2 = x3.multiply(x4, this.m, this.ks);
            if (multiply == x || multiply == x2) {
                multiply = (LongArray)multiply.clone();
            }
            multiply.addShiftedByWords(multiply2, 0);
            multiply.reduce(this.m, this.ks);
            return new F2m(this.m, this.ks, multiply);
        }
        
        @Override
        public ECFieldElement divide(final ECFieldElement ecFieldElement) {
            return this.multiply(ecFieldElement.invert());
        }
        
        @Override
        public ECFieldElement negate() {
            return this;
        }
        
        @Override
        public ECFieldElement square() {
            return new F2m(this.m, this.ks, this.x.modSquare(this.m, this.ks));
        }
        
        @Override
        public ECFieldElement squareMinusProduct(final ECFieldElement ecFieldElement, final ECFieldElement ecFieldElement2) {
            return this.squarePlusProduct(ecFieldElement, ecFieldElement2);
        }
        
        @Override
        public ECFieldElement squarePlusProduct(final ECFieldElement ecFieldElement, final ECFieldElement ecFieldElement2) {
            final LongArray x = this.x;
            final LongArray x2 = ((F2m)ecFieldElement).x;
            final LongArray x3 = ((F2m)ecFieldElement2).x;
            LongArray square = x.square(this.m, this.ks);
            final LongArray multiply = x2.multiply(x3, this.m, this.ks);
            if (square == x) {
                square = (LongArray)square.clone();
            }
            square.addShiftedByWords(multiply, 0);
            square.reduce(this.m, this.ks);
            return new F2m(this.m, this.ks, square);
        }
        
        @Override
        public ECFieldElement squarePow(final int n) {
            return (n < 1) ? this : new F2m(this.m, this.ks, this.x.modSquareN(n, this.m, this.ks));
        }
        
        @Override
        public ECFieldElement invert() {
            return new F2m(this.m, this.ks, this.x.modInverse(this.m, this.ks));
        }
        
        @Override
        public ECFieldElement sqrt() {
            return (this.x.isZero() || this.x.isOne()) ? this : this.squarePow(this.m - 1);
        }
        
        public int getRepresentation() {
            return this.representation;
        }
        
        public int getM() {
            return this.m;
        }
        
        public int getK1() {
            return this.ks[0];
        }
        
        public int getK2() {
            return (this.ks.length >= 2) ? this.ks[1] : 0;
        }
        
        public int getK3() {
            return (this.ks.length >= 3) ? this.ks[2] : 0;
        }
        
        @Override
        public boolean equals(final Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof F2m)) {
                return false;
            }
            final F2m f2m = (F2m)o;
            return this.m == f2m.m && this.representation == f2m.representation && Arrays.areEqual(this.ks, f2m.ks) && this.x.equals(f2m.x);
        }
        
        @Override
        public int hashCode() {
            return this.x.hashCode() ^ this.m ^ Arrays.hashCode(this.ks);
        }
    }
    
    public static class Fp extends ECFieldElement
    {
        BigInteger q;
        BigInteger r;
        BigInteger x;
        
        static BigInteger calculateResidue(final BigInteger bigInteger) {
            final int bitLength = bigInteger.bitLength();
            if (bitLength >= 96 && bigInteger.shiftRight(bitLength - 64).longValue() == -1L) {
                return Fp.ONE.shiftLeft(bitLength).subtract(bigInteger);
            }
            return null;
        }
        
        @Deprecated
        public Fp(final BigInteger bigInteger, final BigInteger bigInteger2) {
            this(bigInteger, calculateResidue(bigInteger), bigInteger2);
        }
        
        Fp(final BigInteger q, final BigInteger r, final BigInteger x) {
            if (x == null || x.signum() < 0 || x.compareTo(q) >= 0) {
                throw new IllegalArgumentException("x value invalid in Fp field element");
            }
            this.q = q;
            this.r = r;
            this.x = x;
        }
        
        @Override
        public BigInteger toBigInteger() {
            return this.x;
        }
        
        @Override
        public String getFieldName() {
            return "Fp";
        }
        
        @Override
        public int getFieldSize() {
            return this.q.bitLength();
        }
        
        public BigInteger getQ() {
            return this.q;
        }
        
        @Override
        public ECFieldElement add(final ECFieldElement ecFieldElement) {
            return new Fp(this.q, this.r, this.modAdd(this.x, ecFieldElement.toBigInteger()));
        }
        
        @Override
        public ECFieldElement addOne() {
            BigInteger bigInteger = this.x.add(ECConstants.ONE);
            if (bigInteger.compareTo(this.q) == 0) {
                bigInteger = ECConstants.ZERO;
            }
            return new Fp(this.q, this.r, bigInteger);
        }
        
        @Override
        public ECFieldElement subtract(final ECFieldElement ecFieldElement) {
            return new Fp(this.q, this.r, this.modSubtract(this.x, ecFieldElement.toBigInteger()));
        }
        
        @Override
        public ECFieldElement multiply(final ECFieldElement ecFieldElement) {
            return new Fp(this.q, this.r, this.modMult(this.x, ecFieldElement.toBigInteger()));
        }
        
        @Override
        public ECFieldElement multiplyMinusProduct(final ECFieldElement ecFieldElement, final ECFieldElement ecFieldElement2, final ECFieldElement ecFieldElement3) {
            return new Fp(this.q, this.r, this.modReduce(this.x.multiply(ecFieldElement.toBigInteger()).subtract(ecFieldElement2.toBigInteger().multiply(ecFieldElement3.toBigInteger()))));
        }
        
        @Override
        public ECFieldElement multiplyPlusProduct(final ECFieldElement ecFieldElement, final ECFieldElement ecFieldElement2, final ECFieldElement ecFieldElement3) {
            return new Fp(this.q, this.r, this.modReduce(this.x.multiply(ecFieldElement.toBigInteger()).add(ecFieldElement2.toBigInteger().multiply(ecFieldElement3.toBigInteger()))));
        }
        
        @Override
        public ECFieldElement divide(final ECFieldElement ecFieldElement) {
            return new Fp(this.q, this.r, this.modMult(this.x, this.modInverse(ecFieldElement.toBigInteger())));
        }
        
        @Override
        public ECFieldElement negate() {
            return (this.x.signum() == 0) ? this : new Fp(this.q, this.r, this.q.subtract(this.x));
        }
        
        @Override
        public ECFieldElement square() {
            return new Fp(this.q, this.r, this.modMult(this.x, this.x));
        }
        
        @Override
        public ECFieldElement squareMinusProduct(final ECFieldElement ecFieldElement, final ECFieldElement ecFieldElement2) {
            final BigInteger x = this.x;
            return new Fp(this.q, this.r, this.modReduce(x.multiply(x).subtract(ecFieldElement.toBigInteger().multiply(ecFieldElement2.toBigInteger()))));
        }
        
        @Override
        public ECFieldElement squarePlusProduct(final ECFieldElement ecFieldElement, final ECFieldElement ecFieldElement2) {
            final BigInteger x = this.x;
            return new Fp(this.q, this.r, this.modReduce(x.multiply(x).add(ecFieldElement.toBigInteger().multiply(ecFieldElement2.toBigInteger()))));
        }
        
        @Override
        public ECFieldElement invert() {
            return new Fp(this.q, this.r, this.modInverse(this.x));
        }
        
        @Override
        public ECFieldElement sqrt() {
            if (this.isZero() || this.isOne()) {
                return this;
            }
            if (!this.q.testBit(0)) {
                throw new RuntimeException("not done yet");
            }
            if (this.q.testBit(1)) {
                return this.checkSqrt(new Fp(this.q, this.r, this.x.modPow(this.q.shiftRight(2).add(ECConstants.ONE), this.q)));
            }
            if (this.q.testBit(2)) {
                final BigInteger modPow = this.x.modPow(this.q.shiftRight(3), this.q);
                final BigInteger modMult = this.modMult(modPow, this.x);
                if (this.modMult(modMult, modPow).equals(ECConstants.ONE)) {
                    return this.checkSqrt(new Fp(this.q, this.r, modMult));
                }
                return this.checkSqrt(new Fp(this.q, this.r, this.modMult(modMult, ECConstants.TWO.modPow(this.q.shiftRight(2), this.q))));
            }
            else {
                final BigInteger shiftRight = this.q.shiftRight(1);
                if (!this.x.modPow(shiftRight, this.q).equals(ECConstants.ONE)) {
                    return null;
                }
                final BigInteger x = this.x;
                final BigInteger modDouble = this.modDouble(this.modDouble(x));
                final BigInteger add = shiftRight.add(ECConstants.ONE);
                final BigInteger subtract = this.q.subtract(ECConstants.ONE);
                final Random random = new Random();
                while (true) {
                    final BigInteger bigInteger = new BigInteger(this.q.bitLength(), random);
                    if (bigInteger.compareTo(this.q) < 0 && this.modReduce(bigInteger.multiply(bigInteger).subtract(modDouble)).modPow(shiftRight, this.q).equals(subtract)) {
                        final BigInteger[] lucasSequence = this.lucasSequence(bigInteger, x, add);
                        final BigInteger bigInteger2 = lucasSequence[0];
                        final BigInteger bigInteger3 = lucasSequence[1];
                        if (this.modMult(bigInteger3, bigInteger3).equals(modDouble)) {
                            return new Fp(this.q, this.r, this.modHalfAbs(bigInteger3));
                        }
                        if (!bigInteger2.equals(ECConstants.ONE) && !bigInteger2.equals(subtract)) {
                            return null;
                        }
                        continue;
                    }
                }
            }
        }
        
        private ECFieldElement checkSqrt(final ECFieldElement ecFieldElement) {
            return ecFieldElement.square().equals(this) ? ecFieldElement : null;
        }
        
        private BigInteger[] lucasSequence(final BigInteger bigInteger, final BigInteger bigInteger2, final BigInteger bigInteger3) {
            final int bitLength = bigInteger3.bitLength();
            final int lowestSetBit = bigInteger3.getLowestSetBit();
            BigInteger bigInteger4 = ECConstants.ONE;
            BigInteger bigInteger5 = ECConstants.TWO;
            BigInteger bigInteger6 = bigInteger;
            BigInteger bigInteger7 = ECConstants.ONE;
            BigInteger bigInteger8 = ECConstants.ONE;
            for (int i = bitLength - 1; i >= lowestSetBit + 1; --i) {
                bigInteger7 = this.modMult(bigInteger7, bigInteger8);
                if (bigInteger3.testBit(i)) {
                    bigInteger8 = this.modMult(bigInteger7, bigInteger2);
                    bigInteger4 = this.modMult(bigInteger4, bigInteger6);
                    bigInteger5 = this.modReduce(bigInteger6.multiply(bigInteger5).subtract(bigInteger.multiply(bigInteger7)));
                    bigInteger6 = this.modReduce(bigInteger6.multiply(bigInteger6).subtract(bigInteger8.shiftLeft(1)));
                }
                else {
                    bigInteger8 = bigInteger7;
                    bigInteger4 = this.modReduce(bigInteger4.multiply(bigInteger5).subtract(bigInteger7));
                    bigInteger6 = this.modReduce(bigInteger6.multiply(bigInteger5).subtract(bigInteger.multiply(bigInteger7)));
                    bigInteger5 = this.modReduce(bigInteger5.multiply(bigInteger5).subtract(bigInteger7.shiftLeft(1)));
                }
            }
            final BigInteger modMult = this.modMult(bigInteger7, bigInteger8);
            final BigInteger modMult2 = this.modMult(modMult, bigInteger2);
            BigInteger bigInteger9 = this.modReduce(bigInteger4.multiply(bigInteger5).subtract(modMult));
            BigInteger bigInteger10 = this.modReduce(bigInteger6.multiply(bigInteger5).subtract(bigInteger.multiply(modMult)));
            BigInteger bigInteger11 = this.modMult(modMult, modMult2);
            for (int j = 1; j <= lowestSetBit; ++j) {
                bigInteger9 = this.modMult(bigInteger9, bigInteger10);
                bigInteger10 = this.modReduce(bigInteger10.multiply(bigInteger10).subtract(bigInteger11.shiftLeft(1)));
                bigInteger11 = this.modMult(bigInteger11, bigInteger11);
            }
            return new BigInteger[] { bigInteger9, bigInteger10 };
        }
        
        protected BigInteger modAdd(final BigInteger bigInteger, final BigInteger bigInteger2) {
            BigInteger bigInteger3 = bigInteger.add(bigInteger2);
            if (bigInteger3.compareTo(this.q) >= 0) {
                bigInteger3 = bigInteger3.subtract(this.q);
            }
            return bigInteger3;
        }
        
        protected BigInteger modDouble(final BigInteger bigInteger) {
            BigInteger bigInteger2 = bigInteger.shiftLeft(1);
            if (bigInteger2.compareTo(this.q) >= 0) {
                bigInteger2 = bigInteger2.subtract(this.q);
            }
            return bigInteger2;
        }
        
        protected BigInteger modHalf(BigInteger add) {
            if (add.testBit(0)) {
                add = this.q.add(add);
            }
            return add.shiftRight(1);
        }
        
        protected BigInteger modHalfAbs(BigInteger subtract) {
            if (subtract.testBit(0)) {
                subtract = this.q.subtract(subtract);
            }
            return subtract.shiftRight(1);
        }
        
        protected BigInteger modInverse(final BigInteger bigInteger) {
            final int fieldSize = this.getFieldSize();
            final int n = fieldSize + 31 >> 5;
            final int[] fromBigInteger = Nat.fromBigInteger(fieldSize, this.q);
            final int[] fromBigInteger2 = Nat.fromBigInteger(fieldSize, bigInteger);
            final int[] create = Nat.create(n);
            Mod.invert(fromBigInteger, fromBigInteger2, create);
            return Nat.toBigInteger(n, create);
        }
        
        protected BigInteger modMult(final BigInteger bigInteger, final BigInteger bigInteger2) {
            return this.modReduce(bigInteger.multiply(bigInteger2));
        }
        
        protected BigInteger modReduce(BigInteger bigInteger) {
            if (this.r != null) {
                final boolean b = bigInteger.signum() < 0;
                if (b) {
                    bigInteger = bigInteger.abs();
                }
                final int bitLength = this.q.bitLength();
                final boolean equals = this.r.equals(ECConstants.ONE);
                while (bigInteger.bitLength() > bitLength + 1) {
                    BigInteger bigInteger2 = bigInteger.shiftRight(bitLength);
                    final BigInteger subtract = bigInteger.subtract(bigInteger2.shiftLeft(bitLength));
                    if (!equals) {
                        bigInteger2 = bigInteger2.multiply(this.r);
                    }
                    bigInteger = bigInteger2.add(subtract);
                }
                while (bigInteger.compareTo(this.q) >= 0) {
                    bigInteger = bigInteger.subtract(this.q);
                }
                if (b && bigInteger.signum() != 0) {
                    bigInteger = this.q.subtract(bigInteger);
                }
            }
            else {
                bigInteger = bigInteger.mod(this.q);
            }
            return bigInteger;
        }
        
        protected BigInteger modSubtract(final BigInteger bigInteger, final BigInteger bigInteger2) {
            BigInteger bigInteger3 = bigInteger.subtract(bigInteger2);
            if (bigInteger3.signum() < 0) {
                bigInteger3 = bigInteger3.add(this.q);
            }
            return bigInteger3;
        }
        
        @Override
        public boolean equals(final Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof Fp)) {
                return false;
            }
            final Fp fp = (Fp)o;
            return this.q.equals(fp.q) && this.x.equals(fp.x);
        }
        
        @Override
        public int hashCode() {
            return this.q.hashCode() ^ this.x.hashCode();
        }
    }
}
