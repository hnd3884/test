package org.bouncycastle.pqc.math.linearalgebra;

public class GF2nPolynomial
{
    private GF2nElement[] coeff;
    private int size;
    
    public GF2nPolynomial(final int size, final GF2nElement gf2nElement) {
        this.size = size;
        this.coeff = new GF2nElement[this.size];
        for (int i = 0; i < this.size; ++i) {
            this.coeff[i] = (GF2nElement)gf2nElement.clone();
        }
    }
    
    private GF2nPolynomial(final int size) {
        this.size = size;
        this.coeff = new GF2nElement[this.size];
    }
    
    public GF2nPolynomial(final GF2nPolynomial gf2nPolynomial) {
        this.coeff = new GF2nElement[gf2nPolynomial.size];
        this.size = gf2nPolynomial.size;
        for (int i = 0; i < this.size; ++i) {
            this.coeff[i] = (GF2nElement)gf2nPolynomial.coeff[i].clone();
        }
    }
    
    public GF2nPolynomial(final GF2Polynomial gf2Polynomial, final GF2nField gf2nField) {
        this.size = gf2nField.getDegree() + 1;
        this.coeff = new GF2nElement[this.size];
        if (gf2nField instanceof GF2nONBField) {
            for (int i = 0; i < this.size; ++i) {
                if (gf2Polynomial.testBit(i)) {
                    this.coeff[i] = GF2nONBElement.ONE((GF2nONBField)gf2nField);
                }
                else {
                    this.coeff[i] = GF2nONBElement.ZERO((GF2nONBField)gf2nField);
                }
            }
        }
        else {
            if (!(gf2nField instanceof GF2nPolynomialField)) {
                throw new IllegalArgumentException("PolynomialGF2n(Bitstring, GF2nField): B1 must be an instance of GF2nONBField or GF2nPolynomialField!");
            }
            for (int j = 0; j < this.size; ++j) {
                if (gf2Polynomial.testBit(j)) {
                    this.coeff[j] = GF2nPolynomialElement.ONE((GF2nPolynomialField)gf2nField);
                }
                else {
                    this.coeff[j] = GF2nPolynomialElement.ZERO((GF2nPolynomialField)gf2nField);
                }
            }
        }
    }
    
    public final void assignZeroToElements() {
        for (int i = 0; i < this.size; ++i) {
            this.coeff[i].assignZero();
        }
    }
    
    public final int size() {
        return this.size;
    }
    
    public final int getDegree() {
        for (int i = this.size - 1; i >= 0; --i) {
            if (!this.coeff[i].isZero()) {
                return i;
            }
        }
        return -1;
    }
    
    public final void enlarge(final int size) {
        if (size <= this.size) {
            return;
        }
        final GF2nElement[] coeff = new GF2nElement[size];
        System.arraycopy(this.coeff, 0, coeff, 0, this.size);
        final GF2nField field = this.coeff[0].getField();
        if (this.coeff[0] instanceof GF2nPolynomialElement) {
            for (int i = this.size; i < size; ++i) {
                coeff[i] = GF2nPolynomialElement.ZERO((GF2nPolynomialField)field);
            }
        }
        else if (this.coeff[0] instanceof GF2nONBElement) {
            for (int j = this.size; j < size; ++j) {
                coeff[j] = GF2nONBElement.ZERO((GF2nONBField)field);
            }
        }
        this.size = size;
        this.coeff = coeff;
    }
    
    public final void shrink() {
        int size;
        for (size = this.size - 1; this.coeff[size].isZero() && size > 0; --size) {}
        if (++size < this.size) {
            final GF2nElement[] coeff = new GF2nElement[size];
            System.arraycopy(this.coeff, 0, coeff, 0, size);
            this.coeff = coeff;
            this.size = size;
        }
    }
    
    public final void set(final int n, final GF2nElement gf2nElement) {
        if (!(gf2nElement instanceof GF2nPolynomialElement) && !(gf2nElement instanceof GF2nONBElement)) {
            throw new IllegalArgumentException("PolynomialGF2n.set f must be an instance of either GF2nPolynomialElement or GF2nONBElement!");
        }
        this.coeff[n] = (GF2nElement)gf2nElement.clone();
    }
    
    public final GF2nElement at(final int n) {
        return this.coeff[n];
    }
    
    public final boolean isZero() {
        for (int i = 0; i < this.size; ++i) {
            if (this.coeff[i] != null && !this.coeff[i].isZero()) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public final boolean equals(final Object o) {
        if (o == null || !(o instanceof GF2nPolynomial)) {
            return false;
        }
        final GF2nPolynomial gf2nPolynomial = (GF2nPolynomial)o;
        if (this.getDegree() != gf2nPolynomial.getDegree()) {
            return false;
        }
        for (int i = 0; i < this.size; ++i) {
            if (!this.coeff[i].equals(gf2nPolynomial.coeff[i])) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        return this.getDegree() + this.coeff.hashCode();
    }
    
    public final GF2nPolynomial add(final GF2nPolynomial gf2nPolynomial) {
        GF2nPolynomial gf2nPolynomial2;
        if (this.size() >= gf2nPolynomial.size()) {
            gf2nPolynomial2 = new GF2nPolynomial(this.size());
            int i;
            for (i = 0; i < gf2nPolynomial.size(); ++i) {
                gf2nPolynomial2.coeff[i] = (GF2nElement)this.coeff[i].add(gf2nPolynomial.coeff[i]);
            }
            while (i < this.size()) {
                gf2nPolynomial2.coeff[i] = this.coeff[i];
                ++i;
            }
        }
        else {
            gf2nPolynomial2 = new GF2nPolynomial(gf2nPolynomial.size());
            int j;
            for (j = 0; j < this.size(); ++j) {
                gf2nPolynomial2.coeff[j] = (GF2nElement)this.coeff[j].add(gf2nPolynomial.coeff[j]);
            }
            while (j < gf2nPolynomial.size()) {
                gf2nPolynomial2.coeff[j] = gf2nPolynomial.coeff[j];
                ++j;
            }
        }
        return gf2nPolynomial2;
    }
    
    public final GF2nPolynomial scalarMultiply(final GF2nElement gf2nElement) {
        final GF2nPolynomial gf2nPolynomial = new GF2nPolynomial(this.size());
        for (int i = 0; i < this.size(); ++i) {
            gf2nPolynomial.coeff[i] = (GF2nElement)this.coeff[i].multiply(gf2nElement);
        }
        return gf2nPolynomial;
    }
    
    public final GF2nPolynomial multiply(final GF2nPolynomial gf2nPolynomial) {
        final int size = this.size();
        if (size != gf2nPolynomial.size()) {
            throw new IllegalArgumentException("PolynomialGF2n.multiply: this and b must have the same size!");
        }
        final GF2nPolynomial gf2nPolynomial2 = new GF2nPolynomial((size << 1) - 1);
        for (int i = 0; i < this.size(); ++i) {
            for (int j = 0; j < gf2nPolynomial.size(); ++j) {
                if (gf2nPolynomial2.coeff[i + j] == null) {
                    gf2nPolynomial2.coeff[i + j] = (GF2nElement)this.coeff[i].multiply(gf2nPolynomial.coeff[j]);
                }
                else {
                    gf2nPolynomial2.coeff[i + j] = (GF2nElement)gf2nPolynomial2.coeff[i + j].add(this.coeff[i].multiply(gf2nPolynomial.coeff[j]));
                }
            }
        }
        return gf2nPolynomial2;
    }
    
    public final GF2nPolynomial multiplyAndReduce(final GF2nPolynomial gf2nPolynomial, final GF2nPolynomial gf2nPolynomial2) {
        return this.multiply(gf2nPolynomial).reduce(gf2nPolynomial2);
    }
    
    public final GF2nPolynomial reduce(final GF2nPolynomial gf2nPolynomial) throws RuntimeException, ArithmeticException {
        return this.remainder(gf2nPolynomial);
    }
    
    public final void shiftThisLeft(final int n) {
        if (n > 0) {
            final int size = this.size;
            final GF2nField field = this.coeff[0].getField();
            this.enlarge(this.size + n);
            for (int i = size - 1; i >= 0; --i) {
                this.coeff[i + n] = this.coeff[i];
            }
            if (this.coeff[0] instanceof GF2nPolynomialElement) {
                for (int j = n - 1; j >= 0; --j) {
                    this.coeff[j] = GF2nPolynomialElement.ZERO((GF2nPolynomialField)field);
                }
            }
            else if (this.coeff[0] instanceof GF2nONBElement) {
                for (int k = n - 1; k >= 0; --k) {
                    this.coeff[k] = GF2nONBElement.ZERO((GF2nONBField)field);
                }
            }
        }
    }
    
    public final GF2nPolynomial shiftLeft(final int n) {
        if (n <= 0) {
            return new GF2nPolynomial(this);
        }
        final GF2nPolynomial gf2nPolynomial = new GF2nPolynomial(this.size + n, this.coeff[0]);
        gf2nPolynomial.assignZeroToElements();
        for (int i = 0; i < this.size; ++i) {
            gf2nPolynomial.coeff[i + n] = this.coeff[i];
        }
        return gf2nPolynomial;
    }
    
    public final GF2nPolynomial[] divide(final GF2nPolynomial gf2nPolynomial) {
        final GF2nPolynomial[] array = new GF2nPolynomial[2];
        GF2nPolynomial add = new GF2nPolynomial(this);
        add.shrink();
        final int degree = gf2nPolynomial.getDegree();
        final GF2nElement gf2nElement = (GF2nElement)gf2nPolynomial.coeff[degree].invert();
        if (add.getDegree() < degree) {
            (array[0] = new GF2nPolynomial(this)).assignZeroToElements();
            array[0].shrink();
            (array[1] = new GF2nPolynomial(this)).shrink();
            return array;
        }
        (array[0] = new GF2nPolynomial(this)).assignZeroToElements();
        for (int i = add.getDegree() - degree; i >= 0; i = add.getDegree() - degree) {
            final GF2nElement gf2nElement2 = (GF2nElement)add.coeff[add.getDegree()].multiply(gf2nElement);
            final GF2nPolynomial scalarMultiply = gf2nPolynomial.scalarMultiply(gf2nElement2);
            scalarMultiply.shiftThisLeft(i);
            add = add.add(scalarMultiply);
            add.shrink();
            array[0].coeff[i] = (GF2nElement)gf2nElement2.clone();
        }
        array[1] = add;
        array[0].shrink();
        return array;
    }
    
    public final GF2nPolynomial remainder(final GF2nPolynomial gf2nPolynomial) throws RuntimeException, ArithmeticException {
        final GF2nPolynomial[] array = new GF2nPolynomial[2];
        return this.divide(gf2nPolynomial)[1];
    }
    
    public final GF2nPolynomial quotient(final GF2nPolynomial gf2nPolynomial) throws RuntimeException, ArithmeticException {
        final GF2nPolynomial[] array = new GF2nPolynomial[2];
        return this.divide(gf2nPolynomial)[0];
    }
    
    public final GF2nPolynomial gcd(final GF2nPolynomial gf2nPolynomial) {
        GF2nPolynomial gf2nPolynomial2 = new GF2nPolynomial(this);
        GF2nPolynomial gf2nPolynomial3 = new GF2nPolynomial(gf2nPolynomial);
        gf2nPolynomial2.shrink();
        gf2nPolynomial3.shrink();
        while (!gf2nPolynomial3.isZero()) {
            final GF2nPolynomial remainder = gf2nPolynomial2.remainder(gf2nPolynomial3);
            gf2nPolynomial2 = gf2nPolynomial3;
            gf2nPolynomial3 = remainder;
        }
        return gf2nPolynomial2.scalarMultiply((GF2nElement)gf2nPolynomial2.coeff[gf2nPolynomial2.getDegree()].invert());
    }
}
