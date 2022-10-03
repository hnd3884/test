package com.google.zxing.common.reedsolomon;

final class GenericGFPoly
{
    private final GenericGF field;
    private final int[] coefficients;
    
    GenericGFPoly(final GenericGF field, final int[] coefficients) {
        if (coefficients.length == 0) {
            throw new IllegalArgumentException();
        }
        this.field = field;
        final int coefficientsLength = coefficients.length;
        if (coefficientsLength > 1 && coefficients[0] == 0) {
            int firstNonZero;
            for (firstNonZero = 1; firstNonZero < coefficientsLength && coefficients[firstNonZero] == 0; ++firstNonZero) {}
            if (firstNonZero == coefficientsLength) {
                this.coefficients = field.getZero().coefficients;
            }
            else {
                System.arraycopy(coefficients, firstNonZero, this.coefficients = new int[coefficientsLength - firstNonZero], 0, this.coefficients.length);
            }
        }
        else {
            this.coefficients = coefficients;
        }
    }
    
    int[] getCoefficients() {
        return this.coefficients;
    }
    
    int getDegree() {
        return this.coefficients.length - 1;
    }
    
    boolean isZero() {
        return this.coefficients[0] == 0;
    }
    
    int getCoefficient(final int degree) {
        return this.coefficients[this.coefficients.length - 1 - degree];
    }
    
    int evaluateAt(final int a) {
        if (a == 0) {
            return this.getCoefficient(0);
        }
        final int size = this.coefficients.length;
        if (a == 1) {
            int result = 0;
            for (int i = 0; i < size; ++i) {
                result = GenericGF.addOrSubtract(result, this.coefficients[i]);
            }
            return result;
        }
        int result = this.coefficients[0];
        for (int i = 1; i < size; ++i) {
            result = GenericGF.addOrSubtract(this.field.multiply(a, result), this.coefficients[i]);
        }
        return result;
    }
    
    GenericGFPoly addOrSubtract(final GenericGFPoly other) {
        if (!this.field.equals(other.field)) {
            throw new IllegalArgumentException("GenericGFPolys do not have same GenericGF field");
        }
        if (this.isZero()) {
            return other;
        }
        if (other.isZero()) {
            return this;
        }
        int[] smallerCoefficients = this.coefficients;
        int[] largerCoefficients = other.coefficients;
        if (smallerCoefficients.length > largerCoefficients.length) {
            final int[] temp = smallerCoefficients;
            smallerCoefficients = largerCoefficients;
            largerCoefficients = temp;
        }
        final int[] sumDiff = new int[largerCoefficients.length];
        final int lengthDiff = largerCoefficients.length - smallerCoefficients.length;
        System.arraycopy(largerCoefficients, 0, sumDiff, 0, lengthDiff);
        for (int i = lengthDiff; i < largerCoefficients.length; ++i) {
            sumDiff[i] = GenericGF.addOrSubtract(smallerCoefficients[i - lengthDiff], largerCoefficients[i]);
        }
        return new GenericGFPoly(this.field, sumDiff);
    }
    
    GenericGFPoly multiply(final GenericGFPoly other) {
        if (!this.field.equals(other.field)) {
            throw new IllegalArgumentException("GenericGFPolys do not have same GenericGF field");
        }
        if (this.isZero() || other.isZero()) {
            return this.field.getZero();
        }
        final int[] aCoefficients = this.coefficients;
        final int aLength = aCoefficients.length;
        final int[] bCoefficients = other.coefficients;
        final int bLength = bCoefficients.length;
        final int[] product = new int[aLength + bLength - 1];
        for (int i = 0; i < aLength; ++i) {
            final int aCoeff = aCoefficients[i];
            for (int j = 0; j < bLength; ++j) {
                product[i + j] = GenericGF.addOrSubtract(product[i + j], this.field.multiply(aCoeff, bCoefficients[j]));
            }
        }
        return new GenericGFPoly(this.field, product);
    }
    
    GenericGFPoly multiply(final int scalar) {
        if (scalar == 0) {
            return this.field.getZero();
        }
        if (scalar == 1) {
            return this;
        }
        final int size = this.coefficients.length;
        final int[] product = new int[size];
        for (int i = 0; i < size; ++i) {
            product[i] = this.field.multiply(this.coefficients[i], scalar);
        }
        return new GenericGFPoly(this.field, product);
    }
    
    GenericGFPoly multiplyByMonomial(final int degree, final int coefficient) {
        if (degree < 0) {
            throw new IllegalArgumentException();
        }
        if (coefficient == 0) {
            return this.field.getZero();
        }
        final int size = this.coefficients.length;
        final int[] product = new int[size + degree];
        for (int i = 0; i < size; ++i) {
            product[i] = this.field.multiply(this.coefficients[i], coefficient);
        }
        return new GenericGFPoly(this.field, product);
    }
    
    GenericGFPoly[] divide(final GenericGFPoly other) {
        if (!this.field.equals(other.field)) {
            throw new IllegalArgumentException("GenericGFPolys do not have same GenericGF field");
        }
        if (other.isZero()) {
            throw new IllegalArgumentException("Divide by 0");
        }
        GenericGFPoly quotient = this.field.getZero();
        GenericGFPoly remainder = this;
        final int denominatorLeadingTerm = other.getCoefficient(other.getDegree());
        final int inverseDenominatorLeadingTerm = this.field.inverse(denominatorLeadingTerm);
        while (remainder.getDegree() >= other.getDegree() && !remainder.isZero()) {
            final int degreeDifference = remainder.getDegree() - other.getDegree();
            final int scale = this.field.multiply(remainder.getCoefficient(remainder.getDegree()), inverseDenominatorLeadingTerm);
            final GenericGFPoly term = other.multiplyByMonomial(degreeDifference, scale);
            final GenericGFPoly iterationQuotient = this.field.buildMonomial(degreeDifference, scale);
            quotient = quotient.addOrSubtract(iterationQuotient);
            remainder = remainder.addOrSubtract(term);
        }
        return new GenericGFPoly[] { quotient, remainder };
    }
    
    @Override
    public String toString() {
        final StringBuilder result = new StringBuilder(8 * this.getDegree());
        for (int degree = this.getDegree(); degree >= 0; --degree) {
            int coefficient = this.getCoefficient(degree);
            if (coefficient != 0) {
                if (coefficient < 0) {
                    result.append(" - ");
                    coefficient = -coefficient;
                }
                else if (result.length() > 0) {
                    result.append(" + ");
                }
                if (degree == 0 || coefficient != 1) {
                    final int alphaPower = this.field.log(coefficient);
                    if (alphaPower == 0) {
                        result.append('1');
                    }
                    else if (alphaPower == 1) {
                        result.append('a');
                    }
                    else {
                        result.append("a^");
                        result.append(alphaPower);
                    }
                }
                if (degree != 0) {
                    if (degree == 1) {
                        result.append('x');
                    }
                    else {
                        result.append("x^");
                        result.append(degree);
                    }
                }
            }
        }
        return result.toString();
    }
}
