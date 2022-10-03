package org.apache.commons.math3.analysis.interpolation;

import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.analysis.BivariateFunction;
import org.apache.commons.math3.exception.NonMonotonicSequenceException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math3.util.MathArrays;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.NoDataException;
import org.apache.commons.math3.optim.nonlinear.vector.MultivariateVectorOptimizer;
import org.apache.commons.math3.optim.PointVectorValuePair;
import org.apache.commons.math3.optim.ConvergenceChecker;
import org.apache.commons.math3.optim.nonlinear.vector.jacobian.GaussNewtonOptimizer;
import org.apache.commons.math3.optim.SimpleVectorValueChecker;
import org.apache.commons.math3.util.Precision;
import org.apache.commons.math3.exception.NotPositiveException;
import org.apache.commons.math3.fitting.PolynomialFitter;

@Deprecated
public class SmoothingPolynomialBicubicSplineInterpolator extends BicubicSplineInterpolator
{
    private final PolynomialFitter xFitter;
    private final int xDegree;
    private final PolynomialFitter yFitter;
    private final int yDegree;
    
    public SmoothingPolynomialBicubicSplineInterpolator() {
        this(3);
    }
    
    public SmoothingPolynomialBicubicSplineInterpolator(final int degree) throws NotPositiveException {
        this(degree, degree);
    }
    
    public SmoothingPolynomialBicubicSplineInterpolator(final int xDegree, final int yDegree) throws NotPositiveException {
        if (xDegree < 0) {
            throw new NotPositiveException(xDegree);
        }
        if (yDegree < 0) {
            throw new NotPositiveException(yDegree);
        }
        this.xDegree = xDegree;
        this.yDegree = yDegree;
        final double safeFactor = 100.0;
        final SimpleVectorValueChecker checker = new SimpleVectorValueChecker(100.0 * Precision.EPSILON, 100.0 * Precision.SAFE_MIN);
        this.xFitter = new PolynomialFitter(new GaussNewtonOptimizer(false, checker));
        this.yFitter = new PolynomialFitter(new GaussNewtonOptimizer(false, checker));
    }
    
    @Override
    public BicubicSplineInterpolatingFunction interpolate(final double[] xval, final double[] yval, final double[][] fval) throws NoDataException, NullArgumentException, DimensionMismatchException, NonMonotonicSequenceException {
        if (xval.length == 0 || yval.length == 0 || fval.length == 0) {
            throw new NoDataException();
        }
        if (xval.length != fval.length) {
            throw new DimensionMismatchException(xval.length, fval.length);
        }
        final int xLen = xval.length;
        final int yLen = yval.length;
        for (int i = 0; i < xLen; ++i) {
            if (fval[i].length != yLen) {
                throw new DimensionMismatchException(fval[i].length, yLen);
            }
        }
        MathArrays.checkOrder(xval);
        MathArrays.checkOrder(yval);
        final PolynomialFunction[] yPolyX = new PolynomialFunction[yLen];
        for (int j = 0; j < yLen; ++j) {
            this.xFitter.clearObservations();
            for (int k = 0; k < xLen; ++k) {
                this.xFitter.addObservedPoint(1.0, xval[k], fval[k][j]);
            }
            yPolyX[j] = new PolynomialFunction(this.xFitter.fit(new double[this.xDegree + 1]));
        }
        final double[][] fval_1 = new double[xLen][yLen];
        for (int l = 0; l < yLen; ++l) {
            final PolynomialFunction f = yPolyX[l];
            for (int m = 0; m < xLen; ++m) {
                fval_1[m][l] = f.value(xval[m]);
            }
        }
        final PolynomialFunction[] xPolyY = new PolynomialFunction[xLen];
        for (int i2 = 0; i2 < xLen; ++i2) {
            this.yFitter.clearObservations();
            for (int j2 = 0; j2 < yLen; ++j2) {
                this.yFitter.addObservedPoint(1.0, yval[j2], fval_1[i2][j2]);
            }
            xPolyY[i2] = new PolynomialFunction(this.yFitter.fit(new double[this.yDegree + 1]));
        }
        final double[][] fval_2 = new double[xLen][yLen];
        for (int m = 0; m < xLen; ++m) {
            final PolynomialFunction f2 = xPolyY[m];
            for (int j3 = 0; j3 < yLen; ++j3) {
                fval_2[m][j3] = f2.value(yval[j3]);
            }
        }
        return super.interpolate(xval, yval, fval_2);
    }
}
