package org.apache.commons.math3.analysis.differentiation;

import org.apache.commons.math3.analysis.UnivariateMatrixFunction;
import org.apache.commons.math3.analysis.UnivariateVectorFunction;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.exception.NumberIsTooLargeException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.exception.NotPositiveException;
import java.io.Serializable;

public class FiniteDifferencesDifferentiator implements UnivariateFunctionDifferentiator, UnivariateVectorFunctionDifferentiator, UnivariateMatrixFunctionDifferentiator, Serializable
{
    private static final long serialVersionUID = 20120917L;
    private final int nbPoints;
    private final double stepSize;
    private final double halfSampleSpan;
    private final double tMin;
    private final double tMax;
    
    public FiniteDifferencesDifferentiator(final int nbPoints, final double stepSize) throws NotPositiveException, NumberIsTooSmallException {
        this(nbPoints, stepSize, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
    }
    
    public FiniteDifferencesDifferentiator(final int nbPoints, final double stepSize, final double tLower, final double tUpper) throws NotPositiveException, NumberIsTooSmallException, NumberIsTooLargeException {
        if (nbPoints <= 1) {
            throw new NumberIsTooSmallException(stepSize, 1, false);
        }
        this.nbPoints = nbPoints;
        if (stepSize <= 0.0) {
            throw new NotPositiveException(stepSize);
        }
        this.stepSize = stepSize;
        this.halfSampleSpan = 0.5 * stepSize * (nbPoints - 1);
        if (2.0 * this.halfSampleSpan >= tUpper - tLower) {
            throw new NumberIsTooLargeException(2.0 * this.halfSampleSpan, tUpper - tLower, false);
        }
        final double safety = FastMath.ulp(this.halfSampleSpan);
        this.tMin = tLower + this.halfSampleSpan + safety;
        this.tMax = tUpper - this.halfSampleSpan - safety;
    }
    
    public int getNbPoints() {
        return this.nbPoints;
    }
    
    public double getStepSize() {
        return this.stepSize;
    }
    
    private DerivativeStructure evaluate(final DerivativeStructure t, final double t0, final double[] y) throws NumberIsTooLargeException {
        final double[] top = new double[this.nbPoints];
        final double[] bottom = new double[this.nbPoints];
        for (int i = 0; i < this.nbPoints; ++i) {
            bottom[i] = y[i];
            for (int j = 1; j <= i; ++j) {
                bottom[i - j] = (bottom[i - j + 1] - bottom[i - j]) / (j * this.stepSize);
            }
            top[i] = bottom[0];
        }
        final int order = t.getOrder();
        final int parameters = t.getFreeParameters();
        final double[] derivatives = t.getAllDerivatives();
        final double dt0 = t.getValue() - t0;
        DerivativeStructure interpolation = new DerivativeStructure(parameters, order, 0.0);
        DerivativeStructure monomial = null;
        for (int k = 0; k < this.nbPoints; ++k) {
            if (k == 0) {
                monomial = new DerivativeStructure(parameters, order, 1.0);
            }
            else {
                derivatives[0] = dt0 - (k - 1) * this.stepSize;
                final DerivativeStructure deltaX = new DerivativeStructure(parameters, order, derivatives);
                monomial = monomial.multiply(deltaX);
            }
            interpolation = interpolation.add(monomial.multiply(top[k]));
        }
        return interpolation;
    }
    
    public UnivariateDifferentiableFunction differentiate(final UnivariateFunction function) {
        return new UnivariateDifferentiableFunction() {
            public double value(final double x) throws MathIllegalArgumentException {
                return function.value(x);
            }
            
            public DerivativeStructure value(final DerivativeStructure t) throws MathIllegalArgumentException {
                if (t.getOrder() >= FiniteDifferencesDifferentiator.this.nbPoints) {
                    throw new NumberIsTooLargeException(t.getOrder(), FiniteDifferencesDifferentiator.this.nbPoints, false);
                }
                final double t2 = FastMath.max(FastMath.min(t.getValue(), FiniteDifferencesDifferentiator.this.tMax), FiniteDifferencesDifferentiator.this.tMin) - FiniteDifferencesDifferentiator.this.halfSampleSpan;
                final double[] y = new double[FiniteDifferencesDifferentiator.this.nbPoints];
                for (int i = 0; i < FiniteDifferencesDifferentiator.this.nbPoints; ++i) {
                    y[i] = function.value(t2 + i * FiniteDifferencesDifferentiator.this.stepSize);
                }
                return FiniteDifferencesDifferentiator.this.evaluate(t, t2, y);
            }
        };
    }
    
    public UnivariateDifferentiableVectorFunction differentiate(final UnivariateVectorFunction function) {
        return new UnivariateDifferentiableVectorFunction() {
            public double[] value(final double x) throws MathIllegalArgumentException {
                return function.value(x);
            }
            
            public DerivativeStructure[] value(final DerivativeStructure t) throws MathIllegalArgumentException {
                if (t.getOrder() >= FiniteDifferencesDifferentiator.this.nbPoints) {
                    throw new NumberIsTooLargeException(t.getOrder(), FiniteDifferencesDifferentiator.this.nbPoints, false);
                }
                final double t2 = FastMath.max(FastMath.min(t.getValue(), FiniteDifferencesDifferentiator.this.tMax), FiniteDifferencesDifferentiator.this.tMin) - FiniteDifferencesDifferentiator.this.halfSampleSpan;
                double[][] y = null;
                for (int i = 0; i < FiniteDifferencesDifferentiator.this.nbPoints; ++i) {
                    final double[] v = function.value(t2 + i * FiniteDifferencesDifferentiator.this.stepSize);
                    if (i == 0) {
                        y = new double[v.length][FiniteDifferencesDifferentiator.this.nbPoints];
                    }
                    for (int j = 0; j < v.length; ++j) {
                        y[j][i] = v[j];
                    }
                }
                final DerivativeStructure[] value = new DerivativeStructure[y.length];
                for (int k = 0; k < value.length; ++k) {
                    value[k] = FiniteDifferencesDifferentiator.this.evaluate(t, t2, y[k]);
                }
                return value;
            }
        };
    }
    
    public UnivariateDifferentiableMatrixFunction differentiate(final UnivariateMatrixFunction function) {
        return new UnivariateDifferentiableMatrixFunction() {
            public double[][] value(final double x) throws MathIllegalArgumentException {
                return function.value(x);
            }
            
            public DerivativeStructure[][] value(final DerivativeStructure t) throws MathIllegalArgumentException {
                if (t.getOrder() >= FiniteDifferencesDifferentiator.this.nbPoints) {
                    throw new NumberIsTooLargeException(t.getOrder(), FiniteDifferencesDifferentiator.this.nbPoints, false);
                }
                final double t2 = FastMath.max(FastMath.min(t.getValue(), FiniteDifferencesDifferentiator.this.tMax), FiniteDifferencesDifferentiator.this.tMin) - FiniteDifferencesDifferentiator.this.halfSampleSpan;
                double[][][] y = null;
                for (int i = 0; i < FiniteDifferencesDifferentiator.this.nbPoints; ++i) {
                    final double[][] v = function.value(t2 + i * FiniteDifferencesDifferentiator.this.stepSize);
                    if (i == 0) {
                        y = new double[v.length][v[0].length][FiniteDifferencesDifferentiator.this.nbPoints];
                    }
                    for (int j = 0; j < v.length; ++j) {
                        for (int k = 0; k < v[j].length; ++k) {
                            y[j][k][i] = v[j][k];
                        }
                    }
                }
                final DerivativeStructure[][] value = new DerivativeStructure[y.length][y[0].length];
                for (int l = 0; l < value.length; ++l) {
                    for (int m = 0; m < y[l].length; ++m) {
                        value[l][m] = FiniteDifferencesDifferentiator.this.evaluate(t, t2, y[l][m]);
                    }
                }
                return value;
            }
        };
    }
}
