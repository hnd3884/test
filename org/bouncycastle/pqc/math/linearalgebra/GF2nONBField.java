package org.bouncycastle.pqc.math.linearalgebra;

import java.util.Random;
import java.util.Vector;
import java.security.SecureRandom;

public class GF2nONBField extends GF2nField
{
    private static final int MAXLONG = 64;
    private int mLength;
    private int mBit;
    private int mType;
    int[][] mMult;
    
    public GF2nONBField(final int mDegree, final SecureRandom secureRandom) throws RuntimeException {
        super(secureRandom);
        if (mDegree < 3) {
            throw new IllegalArgumentException("k must be at least 3");
        }
        this.mDegree = mDegree;
        this.mLength = this.mDegree / 64;
        this.mBit = (this.mDegree & 0x3F);
        if (this.mBit == 0) {
            this.mBit = 64;
        }
        else {
            ++this.mLength;
        }
        this.computeType();
        if (this.mType < 3) {
            this.mMult = new int[this.mDegree][2];
            for (int i = 0; i < this.mDegree; ++i) {
                this.mMult[i][0] = -1;
                this.mMult[i][1] = -1;
            }
            this.computeMultMatrix();
            this.computeFieldPolynomial();
            this.fields = new Vector();
            this.matrices = new Vector();
            return;
        }
        throw new RuntimeException("\nThe type of this field is " + this.mType);
    }
    
    int getONBLength() {
        return this.mLength;
    }
    
    int getONBBit() {
        return this.mBit;
    }
    
    @Override
    protected GF2nElement getRandomRoot(final GF2Polynomial gf2Polynomial) {
        GF2nPolynomial quotient = new GF2nPolynomial(gf2Polynomial, this);
        for (int i = quotient.getDegree(); i > 1; i = quotient.getDegree()) {
            int degree;
            int degree2;
            GF2nPolynomial gcd;
            do {
                final GF2nONBElement gf2nONBElement = new GF2nONBElement(this, this.random);
                final GF2nPolynomial gf2nPolynomial = new GF2nPolynomial(2, GF2nONBElement.ZERO(this));
                gf2nPolynomial.set(1, gf2nONBElement);
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
            throw new IllegalArgumentException("GF2nField.computeCOBMatrix: B1 has a different degree and thus cannot be coverted to!");
        }
        final GF2Polynomial[] array = new GF2Polynomial[this.mDegree];
        for (int i = 0; i < this.mDegree; ++i) {
            array[i] = new GF2Polynomial(this.mDegree);
        }
        GF2nElement randomRoot;
        do {
            randomRoot = gf2nField.getRandomRoot(this.fieldPolynomial);
        } while (randomRoot.isZero());
        final GF2nPolynomialElement[] array2 = new GF2nPolynomialElement[this.mDegree];
        array2[0] = (GF2nPolynomialElement)randomRoot.clone();
        for (int j = 1; j < this.mDegree; ++j) {
            array2[j] = (GF2nPolynomialElement)array2[j - 1].square();
        }
        for (int k = 0; k < this.mDegree; ++k) {
            for (int l = 0; l < this.mDegree; ++l) {
                if (array2[k].testBit(l)) {
                    array[this.mDegree - l - 1].setBit(this.mDegree - k - 1);
                }
            }
        }
        this.fields.addElement(gf2nField);
        this.matrices.addElement(array);
        gf2nField.fields.addElement(this);
        gf2nField.matrices.addElement(this.invertMatrix(array));
    }
    
    @Override
    protected void computeFieldPolynomial() {
        if (this.mType == 1) {
            this.fieldPolynomial = new GF2Polynomial(this.mDegree + 1, "ALL");
        }
        else if (this.mType == 2) {
            GF2Polynomial gf2Polynomial = new GF2Polynomial(this.mDegree + 1, "ONE");
            GF2Polynomial shiftLeft = new GF2Polynomial(this.mDegree + 1, "X");
            shiftLeft.addToThis(gf2Polynomial);
            for (int i = 1; i < this.mDegree; ++i) {
                final GF2Polynomial gf2Polynomial2 = gf2Polynomial;
                gf2Polynomial = shiftLeft;
                shiftLeft = gf2Polynomial.shiftLeft();
                shiftLeft.addToThis(gf2Polynomial2);
            }
            this.fieldPolynomial = shiftLeft;
        }
    }
    
    int[][] invMatrix(final int[][] array) {
        final int[][] array2 = new int[this.mDegree][this.mDegree];
        final int[][] array3 = new int[this.mDegree][this.mDegree];
        for (int i = 0; i < this.mDegree; ++i) {
            array3[i][i] = 1;
        }
        for (int j = 0; j < this.mDegree; ++j) {
            for (int k = j; k < this.mDegree; ++k) {
                array[this.mDegree - 1 - j][k] = array[j][j];
            }
        }
        return null;
    }
    
    private void computeType() throws RuntimeException {
        if ((this.mDegree & 0x7) == 0x0) {
            throw new RuntimeException("The extension degree is divisible by 8!");
        }
        this.mType = 1;
        int i = 0;
        while (i != 1) {
            final int n = this.mType * this.mDegree + 1;
            if (IntegerFunctions.isPrime(n)) {
                i = IntegerFunctions.gcd(this.mType * this.mDegree / IntegerFunctions.order(2, n), this.mDegree);
            }
            ++this.mType;
        }
        --this.mType;
        if (this.mType == 1) {
            final int n2 = (this.mDegree << 1) + 1;
            if (IntegerFunctions.isPrime(n2) && IntegerFunctions.gcd((this.mDegree << 1) / IntegerFunctions.order(2, n2), this.mDegree) == 1) {
                ++this.mType;
            }
        }
    }
    
    private void computeMultMatrix() {
        if ((this.mType & 0x7) != 0x0) {
            final int n = this.mType * this.mDegree + 1;
            final int[] array = new int[n];
            int elementOfOrder;
            if (this.mType == 1) {
                elementOfOrder = 1;
            }
            else if (this.mType == 2) {
                elementOfOrder = n - 1;
            }
            else {
                elementOfOrder = this.elementOfOrder(this.mType, n);
            }
            int n2 = 1;
            for (int i = 0; i < this.mType; ++i) {
                int n3 = n2;
                for (int j = 0; j < this.mDegree; ++j) {
                    array[n3] = j;
                    n3 = (n3 << 1) % n;
                    if (n3 < 0) {
                        n3 += n;
                    }
                }
                n2 = elementOfOrder * n2 % n;
                if (n2 < 0) {
                    n2 += n;
                }
            }
            if (this.mType == 1) {
                for (int k = 1; k < n - 1; ++k) {
                    if (this.mMult[array[k + 1]][0] == -1) {
                        this.mMult[array[k + 1]][0] = array[n - k];
                    }
                    else {
                        this.mMult[array[k + 1]][1] = array[n - k];
                    }
                }
                for (int n4 = this.mDegree >> 1, l = 1; l <= n4; ++l) {
                    if (this.mMult[l - 1][0] == -1) {
                        this.mMult[l - 1][0] = n4 + l - 1;
                    }
                    else {
                        this.mMult[l - 1][1] = n4 + l - 1;
                    }
                    if (this.mMult[n4 + l - 1][0] == -1) {
                        this.mMult[n4 + l - 1][0] = l - 1;
                    }
                    else {
                        this.mMult[n4 + l - 1][1] = l - 1;
                    }
                }
            }
            else {
                if (this.mType != 2) {
                    throw new RuntimeException("only type 1 or type 2 implemented");
                }
                for (int n5 = 1; n5 < n - 1; ++n5) {
                    if (this.mMult[array[n5 + 1]][0] == -1) {
                        this.mMult[array[n5 + 1]][0] = array[n - n5];
                    }
                    else {
                        this.mMult[array[n5 + 1]][1] = array[n - n5];
                    }
                }
            }
            return;
        }
        throw new RuntimeException("bisher nur fuer Gausssche Normalbasen implementiert");
    }
    
    private int elementOfOrder(final int n, final int n2) {
        final Random random = new Random();
        int i;
        for (i = 0; i == 0; i += n2 - 1) {
            i = random.nextInt() % (n2 - 1);
            if (i < 0) {}
        }
        int n3;
        for (n3 = IntegerFunctions.order(i, n2); n3 % n != 0 || n3 == 0; n3 = IntegerFunctions.order(i, n2)) {
            while (i == 0) {
                i = random.nextInt() % (n2 - 1);
                if (i < 0) {
                    i += n2 - 1;
                }
            }
        }
        int n4 = i;
        for (int n5 = n / n3, j = 2; j <= n5; ++j) {
            n4 *= i;
        }
        return n4;
    }
}
