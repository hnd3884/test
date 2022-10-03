package org.bouncycastle.pqc.math.linearalgebra;

import java.math.BigInteger;
import java.util.Random;

public class GF2nPolynomialElement extends GF2nElement
{
    private static final int[] bitMask;
    private GF2Polynomial polynomial;
    
    public GF2nPolynomialElement(final GF2nPolynomialField mField, final Random random) {
        this.mField = mField;
        this.mDegree = this.mField.getDegree();
        this.polynomial = new GF2Polynomial(this.mDegree);
        this.randomize(random);
    }
    
    public GF2nPolynomialElement(final GF2nPolynomialField mField, final GF2Polynomial gf2Polynomial) {
        this.mField = mField;
        this.mDegree = this.mField.getDegree();
        (this.polynomial = new GF2Polynomial(gf2Polynomial)).expandN(this.mDegree);
    }
    
    public GF2nPolynomialElement(final GF2nPolynomialField mField, final byte[] array) {
        this.mField = mField;
        this.mDegree = this.mField.getDegree();
        (this.polynomial = new GF2Polynomial(this.mDegree, array)).expandN(this.mDegree);
    }
    
    public GF2nPolynomialElement(final GF2nPolynomialField mField, final int[] array) {
        this.mField = mField;
        this.mDegree = this.mField.getDegree();
        (this.polynomial = new GF2Polynomial(this.mDegree, array)).expandN(mField.mDegree);
    }
    
    public GF2nPolynomialElement(final GF2nPolynomialElement gf2nPolynomialElement) {
        this.mField = gf2nPolynomialElement.mField;
        this.mDegree = gf2nPolynomialElement.mDegree;
        this.polynomial = new GF2Polynomial(gf2nPolynomialElement.polynomial);
    }
    
    @Override
    public Object clone() {
        return new GF2nPolynomialElement(this);
    }
    
    @Override
    void assignZero() {
        this.polynomial.assignZero();
    }
    
    public static GF2nPolynomialElement ZERO(final GF2nPolynomialField gf2nPolynomialField) {
        return new GF2nPolynomialElement(gf2nPolynomialField, new GF2Polynomial(gf2nPolynomialField.getDegree()));
    }
    
    public static GF2nPolynomialElement ONE(final GF2nPolynomialField gf2nPolynomialField) {
        return new GF2nPolynomialElement(gf2nPolynomialField, new GF2Polynomial(gf2nPolynomialField.getDegree(), new int[] { 1 }));
    }
    
    @Override
    void assignOne() {
        this.polynomial.assignOne();
    }
    
    private void randomize(final Random random) {
        this.polynomial.expandN(this.mDegree);
        this.polynomial.randomize(random);
    }
    
    public boolean isZero() {
        return this.polynomial.isZero();
    }
    
    public boolean isOne() {
        return this.polynomial.isOne();
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == null || !(o instanceof GF2nPolynomialElement)) {
            return false;
        }
        final GF2nPolynomialElement gf2nPolynomialElement = (GF2nPolynomialElement)o;
        return (this.mField == gf2nPolynomialElement.mField || this.mField.getFieldPolynomial().equals(gf2nPolynomialElement.mField.getFieldPolynomial())) && this.polynomial.equals(gf2nPolynomialElement.polynomial);
    }
    
    @Override
    public int hashCode() {
        return this.mField.hashCode() + this.polynomial.hashCode();
    }
    
    private GF2Polynomial getGF2Polynomial() {
        return new GF2Polynomial(this.polynomial);
    }
    
    @Override
    boolean testBit(final int n) {
        return this.polynomial.testBit(n);
    }
    
    @Override
    public boolean testRightmostBit() {
        return this.polynomial.testBit(0);
    }
    
    public GFElement add(final GFElement gfElement) throws RuntimeException {
        final GF2nPolynomialElement gf2nPolynomialElement = new GF2nPolynomialElement(this);
        gf2nPolynomialElement.addToThis(gfElement);
        return gf2nPolynomialElement;
    }
    
    public void addToThis(final GFElement gfElement) throws RuntimeException {
        if (!(gfElement instanceof GF2nPolynomialElement)) {
            throw new RuntimeException();
        }
        if (!this.mField.equals(((GF2nPolynomialElement)gfElement).mField)) {
            throw new RuntimeException();
        }
        this.polynomial.addToThis(((GF2nPolynomialElement)gfElement).polynomial);
    }
    
    @Override
    public GF2nElement increase() {
        final GF2nPolynomialElement gf2nPolynomialElement = new GF2nPolynomialElement(this);
        gf2nPolynomialElement.increaseThis();
        return gf2nPolynomialElement;
    }
    
    @Override
    public void increaseThis() {
        this.polynomial.increaseThis();
    }
    
    public GFElement multiply(final GFElement gfElement) throws RuntimeException {
        final GF2nPolynomialElement gf2nPolynomialElement = new GF2nPolynomialElement(this);
        gf2nPolynomialElement.multiplyThisBy(gfElement);
        return gf2nPolynomialElement;
    }
    
    public void multiplyThisBy(final GFElement gfElement) throws RuntimeException {
        if (!(gfElement instanceof GF2nPolynomialElement)) {
            throw new RuntimeException();
        }
        if (!this.mField.equals(((GF2nPolynomialElement)gfElement).mField)) {
            throw new RuntimeException();
        }
        if (this.equals(gfElement)) {
            this.squareThis();
            return;
        }
        this.polynomial = this.polynomial.multiply(((GF2nPolynomialElement)gfElement).polynomial);
        this.reduceThis();
    }
    
    public GFElement invert() throws ArithmeticException {
        return this.invertMAIA();
    }
    
    public GF2nPolynomialElement invertEEA() throws ArithmeticException {
        if (this.isZero()) {
            throw new ArithmeticException();
        }
        GF2Polynomial gf2Polynomial = new GF2Polynomial(this.mDegree + 32, "ONE");
        gf2Polynomial.reduceN();
        GF2Polynomial gf2Polynomial2 = new GF2Polynomial(this.mDegree + 32);
        gf2Polynomial2.reduceN();
        GF2Polynomial gf2Polynomial3 = this.getGF2Polynomial();
        GF2Polynomial fieldPolynomial = this.mField.getFieldPolynomial();
        gf2Polynomial3.reduceN();
        while (!gf2Polynomial3.isOne()) {
            gf2Polynomial3.reduceN();
            fieldPolynomial.reduceN();
            int n = gf2Polynomial3.getLength() - fieldPolynomial.getLength();
            if (n < 0) {
                final GF2Polynomial gf2Polynomial4 = gf2Polynomial3;
                gf2Polynomial3 = fieldPolynomial;
                fieldPolynomial = gf2Polynomial4;
                final GF2Polynomial gf2Polynomial5 = gf2Polynomial;
                gf2Polynomial = gf2Polynomial2;
                gf2Polynomial2 = gf2Polynomial5;
                n = -n;
                gf2Polynomial2.reduceN();
            }
            gf2Polynomial3.shiftLeftAddThis(fieldPolynomial, n);
            gf2Polynomial.shiftLeftAddThis(gf2Polynomial2, n);
        }
        gf2Polynomial.reduceN();
        return new GF2nPolynomialElement((GF2nPolynomialField)this.mField, gf2Polynomial);
    }
    
    public GF2nPolynomialElement invertSquare() throws ArithmeticException {
        if (this.isZero()) {
            throw new ArithmeticException();
        }
        final int n = this.mField.getDegree() - 1;
        final GF2nPolynomialElement gf2nPolynomialElement = new GF2nPolynomialElement(this);
        gf2nPolynomialElement.polynomial.expandN((this.mDegree << 1) + 32);
        gf2nPolynomialElement.polynomial.reduceN();
        int n2 = 1;
        for (int i = IntegerFunctions.floorLog(n) - 1; i >= 0; --i) {
            final GF2nPolynomialElement gf2nPolynomialElement2 = new GF2nPolynomialElement(gf2nPolynomialElement);
            for (int j = 1; j <= n2; ++j) {
                gf2nPolynomialElement2.squareThisPreCalc();
            }
            gf2nPolynomialElement.multiplyThisBy(gf2nPolynomialElement2);
            n2 <<= 1;
            if ((n & GF2nPolynomialElement.bitMask[i]) != 0x0) {
                gf2nPolynomialElement.squareThisPreCalc();
                gf2nPolynomialElement.multiplyThisBy(this);
                ++n2;
            }
        }
        gf2nPolynomialElement.squareThisPreCalc();
        return gf2nPolynomialElement;
    }
    
    public GF2nPolynomialElement invertMAIA() throws ArithmeticException {
        if (this.isZero()) {
            throw new ArithmeticException();
        }
        GF2Polynomial gf2Polynomial = new GF2Polynomial(this.mDegree, "ONE");
        GF2Polynomial gf2Polynomial2 = new GF2Polynomial(this.mDegree);
        GF2Polynomial gf2Polynomial3 = this.getGF2Polynomial();
        GF2Polynomial fieldPolynomial = this.mField.getFieldPolynomial();
        while (true) {
            if (!gf2Polynomial3.testBit(0)) {
                gf2Polynomial3.shiftRightThis();
                if (!gf2Polynomial.testBit(0)) {
                    gf2Polynomial.shiftRightThis();
                }
                else {
                    gf2Polynomial.addToThis(this.mField.getFieldPolynomial());
                    gf2Polynomial.shiftRightThis();
                }
            }
            else {
                if (gf2Polynomial3.isOne()) {
                    break;
                }
                gf2Polynomial3.reduceN();
                fieldPolynomial.reduceN();
                if (gf2Polynomial3.getLength() < fieldPolynomial.getLength()) {
                    final GF2Polynomial gf2Polynomial4 = gf2Polynomial3;
                    gf2Polynomial3 = fieldPolynomial;
                    fieldPolynomial = gf2Polynomial4;
                    final GF2Polynomial gf2Polynomial5 = gf2Polynomial;
                    gf2Polynomial = gf2Polynomial2;
                    gf2Polynomial2 = gf2Polynomial5;
                }
                gf2Polynomial3.addToThis(fieldPolynomial);
                gf2Polynomial.addToThis(gf2Polynomial2);
            }
        }
        return new GF2nPolynomialElement((GF2nPolynomialField)this.mField, gf2Polynomial);
    }
    
    @Override
    public GF2nElement square() {
        return this.squarePreCalc();
    }
    
    @Override
    public void squareThis() {
        this.squareThisPreCalc();
    }
    
    public GF2nPolynomialElement squareMatrix() {
        final GF2nPolynomialElement gf2nPolynomialElement = new GF2nPolynomialElement(this);
        gf2nPolynomialElement.squareThisMatrix();
        gf2nPolynomialElement.reduceThis();
        return gf2nPolynomialElement;
    }
    
    public void squareThisMatrix() {
        final GF2Polynomial polynomial = new GF2Polynomial(this.mDegree);
        for (int i = 0; i < this.mDegree; ++i) {
            if (this.polynomial.vectorMult(((GF2nPolynomialField)this.mField).squaringMatrix[this.mDegree - i - 1])) {
                polynomial.setBit(i);
            }
        }
        this.polynomial = polynomial;
    }
    
    public GF2nPolynomialElement squareBitwise() {
        final GF2nPolynomialElement gf2nPolynomialElement = new GF2nPolynomialElement(this);
        gf2nPolynomialElement.squareThisBitwise();
        gf2nPolynomialElement.reduceThis();
        return gf2nPolynomialElement;
    }
    
    public void squareThisBitwise() {
        this.polynomial.squareThisBitwise();
        this.reduceThis();
    }
    
    public GF2nPolynomialElement squarePreCalc() {
        final GF2nPolynomialElement gf2nPolynomialElement = new GF2nPolynomialElement(this);
        gf2nPolynomialElement.squareThisPreCalc();
        gf2nPolynomialElement.reduceThis();
        return gf2nPolynomialElement;
    }
    
    public void squareThisPreCalc() {
        this.polynomial.squareThisPreCalc();
        this.reduceThis();
    }
    
    public GF2nPolynomialElement power(final int n) {
        if (n == 1) {
            return new GF2nPolynomialElement(this);
        }
        final GF2nPolynomialElement one = ONE((GF2nPolynomialField)this.mField);
        if (n == 0) {
            return one;
        }
        final GF2nPolynomialElement gf2nPolynomialElement = new GF2nPolynomialElement(this);
        gf2nPolynomialElement.polynomial.expandN((gf2nPolynomialElement.mDegree << 1) + 32);
        gf2nPolynomialElement.polynomial.reduceN();
        for (int i = 0; i < this.mDegree; ++i) {
            if ((n & 1 << i) != 0x0) {
                one.multiplyThisBy(gf2nPolynomialElement);
            }
            gf2nPolynomialElement.square();
        }
        return one;
    }
    
    @Override
    public GF2nElement squareRoot() {
        final GF2nPolynomialElement gf2nPolynomialElement = new GF2nPolynomialElement(this);
        gf2nPolynomialElement.squareRootThis();
        return gf2nPolynomialElement;
    }
    
    @Override
    public void squareRootThis() {
        this.polynomial.expandN((this.mDegree << 1) + 32);
        this.polynomial.reduceN();
        for (int i = 0; i < this.mField.getDegree() - 1; ++i) {
            this.squareThis();
        }
    }
    
    @Override
    public GF2nElement solveQuadraticEquation() throws RuntimeException {
        if (this.isZero()) {
            return ZERO((GF2nPolynomialField)this.mField);
        }
        if ((this.mDegree & 0x1) == 0x1) {
            return this.halfTrace();
        }
        GF2nPolynomialElement gf2nPolynomialElement;
        GF2nPolynomialElement zero;
        do {
            final GF2nPolynomialElement gf2nPolynomialElement2 = new GF2nPolynomialElement((GF2nPolynomialField)this.mField, new Random());
            zero = ZERO((GF2nPolynomialField)this.mField);
            gf2nPolynomialElement = (GF2nPolynomialElement)gf2nPolynomialElement2.clone();
            for (int i = 1; i < this.mDegree; ++i) {
                zero.squareThis();
                gf2nPolynomialElement.squareThis();
                zero.addToThis(gf2nPolynomialElement.multiply(this));
                gf2nPolynomialElement.addToThis(gf2nPolynomialElement2);
            }
        } while (gf2nPolynomialElement.isZero());
        if (!this.equals(zero.square().add(zero))) {
            throw new RuntimeException();
        }
        return zero;
    }
    
    @Override
    public int trace() {
        final GF2nPolynomialElement gf2nPolynomialElement = new GF2nPolynomialElement(this);
        for (int i = 1; i < this.mDegree; ++i) {
            gf2nPolynomialElement.squareThis();
            gf2nPolynomialElement.addToThis(this);
        }
        if (gf2nPolynomialElement.isOne()) {
            return 1;
        }
        return 0;
    }
    
    private GF2nPolynomialElement halfTrace() throws RuntimeException {
        if ((this.mDegree & 0x1) == 0x0) {
            throw new RuntimeException();
        }
        final GF2nPolynomialElement gf2nPolynomialElement = new GF2nPolynomialElement(this);
        for (int i = 1; i <= this.mDegree - 1 >> 1; ++i) {
            gf2nPolynomialElement.squareThis();
            gf2nPolynomialElement.squareThis();
            gf2nPolynomialElement.addToThis(this);
        }
        return gf2nPolynomialElement;
    }
    
    private void reduceThis() {
        if (this.polynomial.getLength() <= this.mDegree) {
            if (this.polynomial.getLength() < this.mDegree) {
                this.polynomial.expandN(this.mDegree);
            }
            return;
        }
        if (((GF2nPolynomialField)this.mField).isTrinomial()) {
            int tc;
            try {
                tc = ((GF2nPolynomialField)this.mField).getTc();
            }
            catch (final RuntimeException ex) {
                throw new RuntimeException("GF2nPolynomialElement.reduce: the field polynomial is not a trinomial");
            }
            if (this.mDegree - tc <= 32 || this.polynomial.getLength() > this.mDegree << 1) {
                this.reduceTrinomialBitwise(tc);
                return;
            }
            this.polynomial.reduceTrinomial(this.mDegree, tc);
        }
        else {
            if (!((GF2nPolynomialField)this.mField).isPentanomial()) {
                (this.polynomial = this.polynomial.remainder(this.mField.getFieldPolynomial())).expandN(this.mDegree);
                return;
            }
            int[] pc;
            try {
                pc = ((GF2nPolynomialField)this.mField).getPc();
            }
            catch (final RuntimeException ex2) {
                throw new RuntimeException("GF2nPolynomialElement.reduce: the field polynomial is not a pentanomial");
            }
            if (this.mDegree - pc[2] <= 32 || this.polynomial.getLength() > this.mDegree << 1) {
                this.reducePentanomialBitwise(pc);
                return;
            }
            this.polynomial.reducePentanomial(this.mDegree, pc);
        }
    }
    
    private void reduceTrinomialBitwise(final int n) {
        final int n2 = this.mDegree - n;
        for (int i = this.polynomial.getLength() - 1; i >= this.mDegree; --i) {
            if (this.polynomial.testBit(i)) {
                this.polynomial.xorBit(i);
                this.polynomial.xorBit(i - n2);
                this.polynomial.xorBit(i - this.mDegree);
            }
        }
        this.polynomial.reduceN();
        this.polynomial.expandN(this.mDegree);
    }
    
    private void reducePentanomialBitwise(final int[] array) {
        final int n = this.mDegree - array[2];
        final int n2 = this.mDegree - array[1];
        final int n3 = this.mDegree - array[0];
        for (int i = this.polynomial.getLength() - 1; i >= this.mDegree; --i) {
            if (this.polynomial.testBit(i)) {
                this.polynomial.xorBit(i);
                this.polynomial.xorBit(i - n);
                this.polynomial.xorBit(i - n2);
                this.polynomial.xorBit(i - n3);
                this.polynomial.xorBit(i - this.mDegree);
            }
        }
        this.polynomial.reduceN();
        this.polynomial.expandN(this.mDegree);
    }
    
    @Override
    public String toString() {
        return this.polynomial.toString(16);
    }
    
    public String toString(final int n) {
        return this.polynomial.toString(n);
    }
    
    public byte[] toByteArray() {
        return this.polynomial.toByteArray();
    }
    
    public BigInteger toFlexiBigInt() {
        return this.polynomial.toFlexiBigInt();
    }
    
    static {
        bitMask = new int[] { 1, 2, 4, 8, 16, 32, 64, 128, 256, 512, 1024, 2048, 4096, 8192, 16384, 32768, 65536, 131072, 262144, 524288, 1048576, 2097152, 4194304, 8388608, 16777216, 33554432, 67108864, 134217728, 268435456, 536870912, 1073741824, Integer.MIN_VALUE, 0 };
    }
}
