package org.apache.commons.math3.analysis.interpolation;

import java.util.Arrays;
import org.apache.commons.math3.analysis.differentiation.DerivativeStructure;
import org.apache.commons.math3.exception.NoDataException;
import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math3.exception.MathArithmeticException;
import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.ZeroException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.util.CombinatoricsUtils;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.math3.analysis.differentiation.UnivariateDifferentiableVectorFunction;

public class HermiteInterpolator implements UnivariateDifferentiableVectorFunction
{
    private final List<Double> abscissae;
    private final List<double[]> topDiagonal;
    private final List<double[]> bottomDiagonal;
    
    public HermiteInterpolator() {
        this.abscissae = new ArrayList<Double>();
        this.topDiagonal = new ArrayList<double[]>();
        this.bottomDiagonal = new ArrayList<double[]>();
    }
    
    public void addSamplePoint(final double x, final double[]... value) throws ZeroException, MathArithmeticException {
        for (int i = 0; i < value.length; ++i) {
            final double[] y = value[i].clone();
            if (i > 1) {
                final double inv = 1.0 / CombinatoricsUtils.factorial(i);
                for (int j = 0; j < y.length; ++j) {
                    final double[] array = y;
                    final int n2 = j;
                    array[n2] *= inv;
                }
            }
            final int n = this.abscissae.size();
            this.bottomDiagonal.add(n - i, y);
            double[] bottom0 = y;
            for (int j = i; j < n; ++j) {
                final double[] bottom2 = this.bottomDiagonal.get(n - (j + 1));
                final double inv2 = 1.0 / (x - this.abscissae.get(n - (j + 1)));
                if (Double.isInfinite(inv2)) {
                    throw new ZeroException(LocalizedFormats.DUPLICATED_ABSCISSA_DIVISION_BY_ZERO, new Object[] { x });
                }
                for (int k = 0; k < y.length; ++k) {
                    bottom2[k] = inv2 * (bottom0[k] - bottom2[k]);
                }
                bottom0 = bottom2;
            }
            this.topDiagonal.add(bottom0.clone());
            this.abscissae.add(x);
        }
    }
    
    public PolynomialFunction[] getPolynomials() throws NoDataException {
        this.checkInterpolation();
        final PolynomialFunction zero = this.polynomial(0.0);
        final PolynomialFunction[] polynomials = new PolynomialFunction[this.topDiagonal.get(0).length];
        for (int i = 0; i < polynomials.length; ++i) {
            polynomials[i] = zero;
        }
        PolynomialFunction coeff = this.polynomial(1.0);
        for (int j = 0; j < this.topDiagonal.size(); ++j) {
            final double[] tdi = this.topDiagonal.get(j);
            for (int k = 0; k < polynomials.length; ++k) {
                polynomials[k] = polynomials[k].add(coeff.multiply(this.polynomial(tdi[k])));
            }
            coeff = coeff.multiply(this.polynomial(-this.abscissae.get(j), 1.0));
        }
        return polynomials;
    }
    
    public double[] value(final double x) throws NoDataException {
        this.checkInterpolation();
        final double[] value = new double[this.topDiagonal.get(0).length];
        double valueCoeff = 1.0;
        for (int i = 0; i < this.topDiagonal.size(); ++i) {
            final double[] dividedDifference = this.topDiagonal.get(i);
            for (int k = 0; k < value.length; ++k) {
                final double[] array = value;
                final int n = k;
                array[n] += dividedDifference[k] * valueCoeff;
            }
            final double deltaX = x - this.abscissae.get(i);
            valueCoeff *= deltaX;
        }
        return value;
    }
    
    public DerivativeStructure[] value(final DerivativeStructure x) throws NoDataException {
        this.checkInterpolation();
        final DerivativeStructure[] value = new DerivativeStructure[this.topDiagonal.get(0).length];
        Arrays.fill(value, x.getField().getZero());
        DerivativeStructure valueCoeff = x.getField().getOne();
        for (int i = 0; i < this.topDiagonal.size(); ++i) {
            final double[] dividedDifference = this.topDiagonal.get(i);
            for (int k = 0; k < value.length; ++k) {
                value[k] = value[k].add(valueCoeff.multiply(dividedDifference[k]));
            }
            final DerivativeStructure deltaX = x.subtract((double)this.abscissae.get(i));
            valueCoeff = valueCoeff.multiply(deltaX);
        }
        return value;
    }
    
    private void checkInterpolation() throws NoDataException {
        if (this.abscissae.isEmpty()) {
            throw new NoDataException(LocalizedFormats.EMPTY_INTERPOLATION_SAMPLE);
        }
    }
    
    private PolynomialFunction polynomial(final double... c) {
        return new PolynomialFunction(c);
    }
}
