package org.apache.commons.math3.analysis.polynomials;

import org.apache.commons.math3.analysis.ParametricUnivariateFunction;
import java.util.Arrays;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.analysis.differentiation.DerivativeStructure;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.NoDataException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.util.MathUtils;
import java.io.Serializable;
import org.apache.commons.math3.analysis.DifferentiableUnivariateFunction;
import org.apache.commons.math3.analysis.differentiation.UnivariateDifferentiableFunction;

public class PolynomialFunction implements UnivariateDifferentiableFunction, DifferentiableUnivariateFunction, Serializable
{
    private static final long serialVersionUID = -7726511984200295583L;
    private final double[] coefficients;
    
    public PolynomialFunction(final double[] c) throws NullArgumentException, NoDataException {
        MathUtils.checkNotNull(c);
        int n = c.length;
        if (n == 0) {
            throw new NoDataException(LocalizedFormats.EMPTY_POLYNOMIALS_COEFFICIENTS_ARRAY);
        }
        while (n > 1 && c[n - 1] == 0.0) {
            --n;
        }
        System.arraycopy(c, 0, this.coefficients = new double[n], 0, n);
    }
    
    public double value(final double x) {
        return evaluate(this.coefficients, x);
    }
    
    public int degree() {
        return this.coefficients.length - 1;
    }
    
    public double[] getCoefficients() {
        return this.coefficients.clone();
    }
    
    protected static double evaluate(final double[] coefficients, final double argument) throws NullArgumentException, NoDataException {
        MathUtils.checkNotNull(coefficients);
        final int n = coefficients.length;
        if (n == 0) {
            throw new NoDataException(LocalizedFormats.EMPTY_POLYNOMIALS_COEFFICIENTS_ARRAY);
        }
        double result = coefficients[n - 1];
        for (int j = n - 2; j >= 0; --j) {
            result = argument * result + coefficients[j];
        }
        return result;
    }
    
    public DerivativeStructure value(final DerivativeStructure t) throws NullArgumentException, NoDataException {
        MathUtils.checkNotNull(this.coefficients);
        final int n = this.coefficients.length;
        if (n == 0) {
            throw new NoDataException(LocalizedFormats.EMPTY_POLYNOMIALS_COEFFICIENTS_ARRAY);
        }
        DerivativeStructure result = new DerivativeStructure(t.getFreeParameters(), t.getOrder(), this.coefficients[n - 1]);
        for (int j = n - 2; j >= 0; --j) {
            result = result.multiply(t).add(this.coefficients[j]);
        }
        return result;
    }
    
    public PolynomialFunction add(final PolynomialFunction p) {
        final int lowLength = FastMath.min(this.coefficients.length, p.coefficients.length);
        final int highLength = FastMath.max(this.coefficients.length, p.coefficients.length);
        final double[] newCoefficients = new double[highLength];
        for (int i = 0; i < lowLength; ++i) {
            newCoefficients[i] = this.coefficients[i] + p.coefficients[i];
        }
        System.arraycopy((this.coefficients.length < p.coefficients.length) ? p.coefficients : this.coefficients, lowLength, newCoefficients, lowLength, highLength - lowLength);
        return new PolynomialFunction(newCoefficients);
    }
    
    public PolynomialFunction subtract(final PolynomialFunction p) {
        final int lowLength = FastMath.min(this.coefficients.length, p.coefficients.length);
        final int highLength = FastMath.max(this.coefficients.length, p.coefficients.length);
        final double[] newCoefficients = new double[highLength];
        for (int i = 0; i < lowLength; ++i) {
            newCoefficients[i] = this.coefficients[i] - p.coefficients[i];
        }
        if (this.coefficients.length < p.coefficients.length) {
            for (int i = lowLength; i < highLength; ++i) {
                newCoefficients[i] = -p.coefficients[i];
            }
        }
        else {
            System.arraycopy(this.coefficients, lowLength, newCoefficients, lowLength, highLength - lowLength);
        }
        return new PolynomialFunction(newCoefficients);
    }
    
    public PolynomialFunction negate() {
        final double[] newCoefficients = new double[this.coefficients.length];
        for (int i = 0; i < this.coefficients.length; ++i) {
            newCoefficients[i] = -this.coefficients[i];
        }
        return new PolynomialFunction(newCoefficients);
    }
    
    public PolynomialFunction multiply(final PolynomialFunction p) {
        final double[] newCoefficients = new double[this.coefficients.length + p.coefficients.length - 1];
        for (int i = 0; i < newCoefficients.length; ++i) {
            newCoefficients[i] = 0.0;
            for (int j = FastMath.max(0, i + 1 - p.coefficients.length); j < FastMath.min(this.coefficients.length, i + 1); ++j) {
                final double[] array = newCoefficients;
                final int n = i;
                array[n] += this.coefficients[j] * p.coefficients[i - j];
            }
        }
        return new PolynomialFunction(newCoefficients);
    }
    
    protected static double[] differentiate(final double[] coefficients) throws NullArgumentException, NoDataException {
        MathUtils.checkNotNull(coefficients);
        final int n = coefficients.length;
        if (n == 0) {
            throw new NoDataException(LocalizedFormats.EMPTY_POLYNOMIALS_COEFFICIENTS_ARRAY);
        }
        if (n == 1) {
            return new double[] { 0.0 };
        }
        final double[] result = new double[n - 1];
        for (int i = n - 1; i > 0; --i) {
            result[i - 1] = i * coefficients[i];
        }
        return result;
    }
    
    public PolynomialFunction polynomialDerivative() {
        return new PolynomialFunction(differentiate(this.coefficients));
    }
    
    public UnivariateFunction derivative() {
        return this.polynomialDerivative();
    }
    
    @Override
    public String toString() {
        final StringBuilder s = new StringBuilder();
        if (this.coefficients[0] == 0.0) {
            if (this.coefficients.length == 1) {
                return "0";
            }
        }
        else {
            s.append(toString(this.coefficients[0]));
        }
        for (int i = 1; i < this.coefficients.length; ++i) {
            if (this.coefficients[i] != 0.0) {
                if (s.length() > 0) {
                    if (this.coefficients[i] < 0.0) {
                        s.append(" - ");
                    }
                    else {
                        s.append(" + ");
                    }
                }
                else if (this.coefficients[i] < 0.0) {
                    s.append("-");
                }
                final double absAi = FastMath.abs(this.coefficients[i]);
                if (absAi - 1.0 != 0.0) {
                    s.append(toString(absAi));
                    s.append(' ');
                }
                s.append("x");
                if (i > 1) {
                    s.append('^');
                    s.append(Integer.toString(i));
                }
            }
        }
        return s.toString();
    }
    
    private static String toString(final double coeff) {
        final String c = Double.toString(coeff);
        if (c.endsWith(".0")) {
            return c.substring(0, c.length() - 2);
        }
        return c;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = 31 * result + Arrays.hashCode(this.coefficients);
        return result;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof PolynomialFunction)) {
            return false;
        }
        final PolynomialFunction other = (PolynomialFunction)obj;
        return Arrays.equals(this.coefficients, other.coefficients);
    }
    
    public static class Parametric implements ParametricUnivariateFunction
    {
        public double[] gradient(final double x, final double... parameters) {
            final double[] gradient = new double[parameters.length];
            double xn = 1.0;
            for (int i = 0; i < parameters.length; ++i) {
                gradient[i] = xn;
                xn *= x;
            }
            return gradient;
        }
        
        public double value(final double x, final double... parameters) throws NoDataException {
            return PolynomialFunction.evaluate(parameters, x);
        }
    }
}
