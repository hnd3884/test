package org.bouncycastle.pqc.math.linearalgebra;

import java.util.Vector;
import java.security.SecureRandom;

public abstract class GF2nField
{
    protected final SecureRandom random;
    protected int mDegree;
    protected GF2Polynomial fieldPolynomial;
    protected Vector fields;
    protected Vector matrices;
    
    protected GF2nField(final SecureRandom random) {
        this.random = random;
    }
    
    public final int getDegree() {
        return this.mDegree;
    }
    
    public final GF2Polynomial getFieldPolynomial() {
        if (this.fieldPolynomial == null) {
            this.computeFieldPolynomial();
        }
        return new GF2Polynomial(this.fieldPolynomial);
    }
    
    @Override
    public final boolean equals(final Object o) {
        if (o == null || !(o instanceof GF2nField)) {
            return false;
        }
        final GF2nField gf2nField = (GF2nField)o;
        return gf2nField.mDegree == this.mDegree && this.fieldPolynomial.equals(gf2nField.fieldPolynomial) && (!(this instanceof GF2nPolynomialField) || gf2nField instanceof GF2nPolynomialField) && (!(this instanceof GF2nONBField) || gf2nField instanceof GF2nONBField);
    }
    
    @Override
    public int hashCode() {
        return this.mDegree + this.fieldPolynomial.hashCode();
    }
    
    protected abstract GF2nElement getRandomRoot(final GF2Polynomial p0);
    
    protected abstract void computeCOBMatrix(final GF2nField p0);
    
    protected abstract void computeFieldPolynomial();
    
    protected final GF2Polynomial[] invertMatrix(final GF2Polynomial[] array) {
        final GF2Polynomial[] array2 = new GF2Polynomial[array.length];
        final GF2Polynomial[] array3 = new GF2Polynomial[array.length];
        for (int i = 0; i < this.mDegree; ++i) {
            try {
                array2[i] = new GF2Polynomial(array[i]);
                (array3[i] = new GF2Polynomial(this.mDegree)).setBit(this.mDegree - 1 - i);
            }
            catch (final RuntimeException ex) {
                ex.printStackTrace();
            }
        }
        for (int j = 0; j < this.mDegree - 1; ++j) {
            int n;
            for (n = j; n < this.mDegree && !array2[n].testBit(this.mDegree - 1 - j); ++n) {}
            if (n >= this.mDegree) {
                throw new RuntimeException("GF2nField.invertMatrix: Matrix cannot be inverted!");
            }
            if (j != n) {
                final GF2Polynomial gf2Polynomial = array2[j];
                array2[j] = array2[n];
                array2[n] = gf2Polynomial;
                final GF2Polynomial gf2Polynomial2 = array3[j];
                array3[j] = array3[n];
                array3[n] = gf2Polynomial2;
            }
            for (int k = j + 1; k < this.mDegree; ++k) {
                if (array2[k].testBit(this.mDegree - 1 - j)) {
                    array2[k].addToThis(array2[j]);
                    array3[k].addToThis(array3[j]);
                }
            }
        }
        for (int l = this.mDegree - 1; l > 0; --l) {
            for (int n2 = l - 1; n2 >= 0; --n2) {
                if (array2[n2].testBit(this.mDegree - 1 - l)) {
                    array2[n2].addToThis(array2[l]);
                    array3[n2].addToThis(array3[l]);
                }
            }
        }
        return array3;
    }
    
    public final GF2nElement convert(final GF2nElement gf2nElement, final GF2nField gf2nField) throws RuntimeException {
        if (gf2nField == this) {
            return (GF2nElement)gf2nElement.clone();
        }
        if (this.fieldPolynomial.equals(gf2nField.fieldPolynomial)) {
            return (GF2nElement)gf2nElement.clone();
        }
        if (this.mDegree != gf2nField.mDegree) {
            throw new RuntimeException("GF2nField.convert: B1 has a different degree and thus cannot be coverted to!");
        }
        int n = this.fields.indexOf(gf2nField);
        if (n == -1) {
            this.computeCOBMatrix(gf2nField);
            n = this.fields.indexOf(gf2nField);
        }
        final GF2Polynomial[] array = this.matrices.elementAt(n);
        final GF2nElement gf2nElement2 = (GF2nElement)gf2nElement.clone();
        if (gf2nElement2 instanceof GF2nONBElement) {
            ((GF2nONBElement)gf2nElement2).reverseOrder();
        }
        final GF2Polynomial gf2Polynomial = new GF2Polynomial(this.mDegree, gf2nElement2.toFlexiBigInt());
        gf2Polynomial.expandN(this.mDegree);
        final GF2Polynomial gf2Polynomial2 = new GF2Polynomial(this.mDegree);
        for (int i = 0; i < this.mDegree; ++i) {
            if (gf2Polynomial.vectorMult(array[i])) {
                gf2Polynomial2.setBit(this.mDegree - 1 - i);
            }
        }
        if (gf2nField instanceof GF2nPolynomialField) {
            return new GF2nPolynomialElement((GF2nPolynomialField)gf2nField, gf2Polynomial2);
        }
        if (gf2nField instanceof GF2nONBField) {
            final GF2nONBElement gf2nONBElement = new GF2nONBElement((GF2nONBField)gf2nField, gf2Polynomial2.toFlexiBigInt());
            gf2nONBElement.reverseOrder();
            return gf2nONBElement;
        }
        throw new RuntimeException("GF2nField.convert: B1 must be an instance of GF2nPolynomialField or GF2nONBField!");
    }
}
