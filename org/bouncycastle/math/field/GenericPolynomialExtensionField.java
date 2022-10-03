package org.bouncycastle.math.field;

import org.bouncycastle.util.Integers;
import java.math.BigInteger;

class GenericPolynomialExtensionField implements PolynomialExtensionField
{
    protected final FiniteField subfield;
    protected final Polynomial minimalPolynomial;
    
    GenericPolynomialExtensionField(final FiniteField subfield, final Polynomial minimalPolynomial) {
        this.subfield = subfield;
        this.minimalPolynomial = minimalPolynomial;
    }
    
    public BigInteger getCharacteristic() {
        return this.subfield.getCharacteristic();
    }
    
    public int getDimension() {
        return this.subfield.getDimension() * this.minimalPolynomial.getDegree();
    }
    
    public FiniteField getSubfield() {
        return this.subfield;
    }
    
    public int getDegree() {
        return this.minimalPolynomial.getDegree();
    }
    
    public Polynomial getMinimalPolynomial() {
        return this.minimalPolynomial;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof GenericPolynomialExtensionField)) {
            return false;
        }
        final GenericPolynomialExtensionField genericPolynomialExtensionField = (GenericPolynomialExtensionField)o;
        return this.subfield.equals(genericPolynomialExtensionField.subfield) && this.minimalPolynomial.equals(genericPolynomialExtensionField.minimalPolynomial);
    }
    
    @Override
    public int hashCode() {
        return this.subfield.hashCode() ^ Integers.rotateLeft(this.minimalPolynomial.hashCode(), 16);
    }
}
