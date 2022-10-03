package org.bouncycastle.pqc.math.linearalgebra;

import java.util.Random;
import java.util.Vector;
import java.security.SecureRandom;

public class GF2nPolynomialField extends GF2nField
{
    GF2Polynomial[] squaringMatrix;
    private boolean isTrinomial;
    private boolean isPentanomial;
    private int tc;
    private int[] pc;
    
    public GF2nPolynomialField(final int mDegree, final SecureRandom secureRandom) {
        super(secureRandom);
        this.isTrinomial = false;
        this.isPentanomial = false;
        this.pc = new int[3];
        if (mDegree < 3) {
            throw new IllegalArgumentException("k must be at least 3");
        }
        this.mDegree = mDegree;
        this.computeFieldPolynomial();
        this.computeSquaringMatrix();
        this.fields = new Vector();
        this.matrices = new Vector();
    }
    
    public GF2nPolynomialField(final int mDegree, final SecureRandom secureRandom, final boolean b) {
        super(secureRandom);
        this.isTrinomial = false;
        this.isPentanomial = false;
        this.pc = new int[3];
        if (mDegree < 3) {
            throw new IllegalArgumentException("k must be at least 3");
        }
        this.mDegree = mDegree;
        if (b) {
            this.computeFieldPolynomial();
        }
        else {
            this.computeFieldPolynomial2();
        }
        this.computeSquaringMatrix();
        this.fields = new Vector();
        this.matrices = new Vector();
    }
    
    public GF2nPolynomialField(final int mDegree, final SecureRandom secureRandom, final GF2Polynomial fieldPolynomial) throws RuntimeException {
        super(secureRandom);
        this.isTrinomial = false;
        this.isPentanomial = false;
        this.pc = new int[3];
        if (mDegree < 3) {
            throw new IllegalArgumentException("degree must be at least 3");
        }
        if (fieldPolynomial.getLength() != mDegree + 1) {
            throw new RuntimeException();
        }
        if (!fieldPolynomial.isIrreducible()) {
            throw new RuntimeException();
        }
        this.mDegree = mDegree;
        this.fieldPolynomial = fieldPolynomial;
        this.computeSquaringMatrix();
        int n = 2;
        for (int i = 1; i < this.fieldPolynomial.getLength() - 1; ++i) {
            if (this.fieldPolynomial.testBit(i)) {
                if (++n == 3) {
                    this.tc = i;
                }
                if (n <= 5) {
                    this.pc[n - 3] = i;
                }
            }
        }
        if (n == 3) {
            this.isTrinomial = true;
        }
        if (n == 5) {
            this.isPentanomial = true;
        }
        this.fields = new Vector();
        this.matrices = new Vector();
    }
    
    public boolean isTrinomial() {
        return this.isTrinomial;
    }
    
    public boolean isPentanomial() {
        return this.isPentanomial;
    }
    
    public int getTc() throws RuntimeException {
        if (!this.isTrinomial) {
            throw new RuntimeException();
        }
        return this.tc;
    }
    
    public int[] getPc() throws RuntimeException {
        if (!this.isPentanomial) {
            throw new RuntimeException();
        }
        final int[] array = new int[3];
        System.arraycopy(this.pc, 0, array, 0, 3);
        return array;
    }
    
    public GF2Polynomial getSquaringVector(final int n) {
        return new GF2Polynomial(this.squaringMatrix[n]);
    }
    
    @Override
    protected GF2nElement getRandomRoot(final GF2Polynomial gf2Polynomial) {
        GF2nPolynomial quotient = new GF2nPolynomial(gf2Polynomial, this);
        for (int i = quotient.getDegree(); i > 1; i = quotient.getDegree()) {
            int degree;
            int degree2;
            GF2nPolynomial gcd;
            do {
                final GF2nPolynomialElement gf2nPolynomialElement = new GF2nPolynomialElement(this, this.random);
                final GF2nPolynomial gf2nPolynomial = new GF2nPolynomial(2, GF2nPolynomialElement.ZERO(this));
                gf2nPolynomial.set(1, gf2nPolynomialElement);
                GF2nPolynomial add = new GF2nPolynomial(gf2nPolynomial);
                for (int j = 1; j <= this.mDegree - 1; ++j) {
                    add = add.multiplyAndReduce(add, quotient).add(gf2nPolynomial);
                }
                gcd = add.gcd(quotient);
                degree = gcd.getDegree();
                degree2 = quotient.getDegree();
            } while (degree == 0 || degree == degree2);
            if (degree << 1 > degree2) {
                quotient = quotient.quotient(gcd);
            }
            else {
                quotient = new GF2nPolynomial(gcd);
            }
        }
        return quotient.at(0);
    }
    
    @Override
    protected void computeCOBMatrix(final GF2nField gf2nField) {
        if (this.mDegree != gf2nField.mDegree) {
            throw new IllegalArgumentException("GF2nPolynomialField.computeCOBMatrix: B1 has a different degree and thus cannot be coverted to!");
        }
        if (gf2nField instanceof GF2nONBField) {
            gf2nField.computeCOBMatrix(this);
            return;
        }
        final GF2Polynomial[] array = new GF2Polynomial[this.mDegree];
        for (int i = 0; i < this.mDegree; ++i) {
            array[i] = new GF2Polynomial(this.mDegree);
        }
        GF2nElement randomRoot;
        do {
            randomRoot = gf2nField.getRandomRoot(this.fieldPolynomial);
        } while (randomRoot.isZero());
        GF2nElement[] array2;
        if (randomRoot instanceof GF2nONBElement) {
            array2 = new GF2nONBElement[this.mDegree];
            array2[this.mDegree - 1] = GF2nONBElement.ONE((GF2nONBField)gf2nField);
        }
        else {
            array2 = new GF2nPolynomialElement[this.mDegree];
            array2[this.mDegree - 1] = GF2nPolynomialElement.ONE((GF2nPolynomialField)gf2nField);
        }
        array2[this.mDegree - 2] = randomRoot;
        for (int j = this.mDegree - 3; j >= 0; --j) {
            array2[j] = (GF2nElement)array2[j + 1].multiply(randomRoot);
        }
        if (gf2nField instanceof GF2nONBField) {
            for (int k = 0; k < this.mDegree; ++k) {
                for (int l = 0; l < this.mDegree; ++l) {
                    if (array2[k].testBit(this.mDegree - l - 1)) {
                        array[this.mDegree - l - 1].setBit(this.mDegree - k - 1);
                    }
                }
            }
        }
        else {
            for (int n = 0; n < this.mDegree; ++n) {
                for (int n2 = 0; n2 < this.mDegree; ++n2) {
                    if (array2[n].testBit(n2)) {
                        array[this.mDegree - n2 - 1].setBit(this.mDegree - n - 1);
                    }
                }
            }
        }
        this.fields.addElement(gf2nField);
        this.matrices.addElement(array);
        gf2nField.fields.addElement(this);
        gf2nField.matrices.addElement(this.invertMatrix(array));
    }
    
    private void computeSquaringMatrix() {
        final GF2Polynomial[] array = new GF2Polynomial[this.mDegree - 1];
        this.squaringMatrix = new GF2Polynomial[this.mDegree];
        for (int i = 0; i < this.squaringMatrix.length; ++i) {
            this.squaringMatrix[i] = new GF2Polynomial(this.mDegree, "ZERO");
        }
        for (int j = 0; j < this.mDegree - 1; ++j) {
            array[j] = new GF2Polynomial(1, "ONE").shiftLeft(this.mDegree + j).remainder(this.fieldPolynomial);
        }
        for (int k = 1; k <= Math.abs(this.mDegree >> 1); ++k) {
            for (int l = 1; l <= this.mDegree; ++l) {
                if (array[this.mDegree - (k << 1)].testBit(this.mDegree - l)) {
                    this.squaringMatrix[l - 1].setBit(this.mDegree - k);
                }
            }
        }
        for (int n = Math.abs(this.mDegree >> 1) + 1; n <= this.mDegree; ++n) {
            this.squaringMatrix[(n << 1) - this.mDegree - 1].setBit(this.mDegree - n);
        }
    }
    
    @Override
    protected void computeFieldPolynomial() {
        if (this.testTrinomials()) {
            return;
        }
        if (this.testPentanomials()) {
            return;
        }
        this.testRandom();
    }
    
    protected void computeFieldPolynomial2() {
        if (this.testTrinomials()) {
            return;
        }
        if (this.testPentanomials()) {
            return;
        }
        this.testRandom();
    }
    
    private boolean testTrinomials() {
        boolean irreducible = false;
        int n = 0;
        (this.fieldPolynomial = new GF2Polynomial(this.mDegree + 1)).setBit(0);
        this.fieldPolynomial.setBit(this.mDegree);
        for (int n2 = 1; n2 < this.mDegree && !irreducible; irreducible = this.fieldPolynomial.isIrreducible(), ++n2) {
            this.fieldPolynomial.setBit(n2);
            final boolean irreducible2 = this.fieldPolynomial.isIrreducible();
            ++n;
            if (irreducible2) {
                this.isTrinomial = true;
                this.tc = n2;
                return irreducible2;
            }
            this.fieldPolynomial.resetBit(n2);
        }
        return irreducible;
    }
    
    private boolean testPentanomials() {
        boolean irreducible = false;
        int n = 0;
        (this.fieldPolynomial = new GF2Polynomial(this.mDegree + 1)).setBit(0);
        this.fieldPolynomial.setBit(this.mDegree);
        for (int bit = 1; bit <= this.mDegree - 3 && !irreducible; ++bit) {
            this.fieldPolynomial.setBit(bit);
            for (int bit2 = bit + 1; bit2 <= this.mDegree - 2 && !irreducible; ++bit2) {
                this.fieldPolynomial.setBit(bit2);
                for (int bit3 = bit2 + 1; bit3 <= this.mDegree - 1 && !irreducible; ++bit3) {
                    this.fieldPolynomial.setBit(bit3);
                    if ((this.mDegree & 0x1) != 0x0 | (bit & 0x1) != 0x0 | (bit2 & 0x1) != 0x0 | (bit3 & 0x1) != 0x0) {
                        irreducible = this.fieldPolynomial.isIrreducible();
                        ++n;
                        if (irreducible) {
                            this.isPentanomial = true;
                            this.pc[0] = bit;
                            this.pc[1] = bit2;
                            this.pc[2] = bit3;
                            return irreducible;
                        }
                    }
                    this.fieldPolynomial.resetBit(bit3);
                }
                this.fieldPolynomial.resetBit(bit2);
            }
            this.fieldPolynomial.resetBit(bit);
        }
        return irreducible;
    }
    
    private boolean testRandom() {
        final boolean b = false;
        this.fieldPolynomial = new GF2Polynomial(this.mDegree + 1);
        int n = 0;
        while (!b) {
            ++n;
            this.fieldPolynomial.randomize();
            this.fieldPolynomial.setBit(this.mDegree);
            this.fieldPolynomial.setBit(0);
            if (this.fieldPolynomial.isIrreducible()) {
                return true;
            }
        }
        return b;
    }
}
