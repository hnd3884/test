package org.apache.commons.math3.stat.regression;

import org.apache.commons.math3.stat.descriptive.moment.Variance;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.linear.NonSquareMatrixException;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.NoDataException;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.InsufficientDataException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.linear.RealMatrix;

public abstract class AbstractMultipleLinearRegression implements MultipleLinearRegression
{
    private RealMatrix xMatrix;
    private RealVector yVector;
    private boolean noIntercept;
    
    public AbstractMultipleLinearRegression() {
        this.noIntercept = false;
    }
    
    protected RealMatrix getX() {
        return this.xMatrix;
    }
    
    protected RealVector getY() {
        return this.yVector;
    }
    
    public boolean isNoIntercept() {
        return this.noIntercept;
    }
    
    public void setNoIntercept(final boolean noIntercept) {
        this.noIntercept = noIntercept;
    }
    
    public void newSampleData(final double[] data, final int nobs, final int nvars) {
        if (data == null) {
            throw new NullArgumentException();
        }
        if (data.length != nobs * (nvars + 1)) {
            throw new DimensionMismatchException(data.length, nobs * (nvars + 1));
        }
        if (nobs <= nvars) {
            throw new InsufficientDataException(LocalizedFormats.INSUFFICIENT_OBSERVED_POINTS_IN_SAMPLE, new Object[] { nobs, nvars + 1 });
        }
        final double[] y = new double[nobs];
        final int cols = this.noIntercept ? nvars : (nvars + 1);
        final double[][] x = new double[nobs][cols];
        int pointer = 0;
        for (int i = 0; i < nobs; ++i) {
            y[i] = data[pointer++];
            if (!this.noIntercept) {
                x[i][0] = 1.0;
            }
            for (int j = this.noIntercept ? 0 : 1; j < cols; ++j) {
                x[i][j] = data[pointer++];
            }
        }
        this.xMatrix = new Array2DRowRealMatrix(x);
        this.yVector = new ArrayRealVector(y);
    }
    
    protected void newYSampleData(final double[] y) {
        if (y == null) {
            throw new NullArgumentException();
        }
        if (y.length == 0) {
            throw new NoDataException();
        }
        this.yVector = new ArrayRealVector(y);
    }
    
    protected void newXSampleData(final double[][] x) {
        if (x == null) {
            throw new NullArgumentException();
        }
        if (x.length == 0) {
            throw new NoDataException();
        }
        if (this.noIntercept) {
            this.xMatrix = new Array2DRowRealMatrix(x, true);
        }
        else {
            final int nVars = x[0].length;
            final double[][] xAug = new double[x.length][nVars + 1];
            for (int i = 0; i < x.length; ++i) {
                if (x[i].length != nVars) {
                    throw new DimensionMismatchException(x[i].length, nVars);
                }
                xAug[i][0] = 1.0;
                System.arraycopy(x[i], 0, xAug[i], 1, nVars);
            }
            this.xMatrix = new Array2DRowRealMatrix(xAug, false);
        }
    }
    
    protected void validateSampleData(final double[][] x, final double[] y) throws MathIllegalArgumentException {
        if (x == null || y == null) {
            throw new NullArgumentException();
        }
        if (x.length != y.length) {
            throw new DimensionMismatchException(y.length, x.length);
        }
        if (x.length == 0) {
            throw new NoDataException();
        }
        if (x[0].length + 1 > x.length) {
            throw new MathIllegalArgumentException(LocalizedFormats.NOT_ENOUGH_DATA_FOR_NUMBER_OF_PREDICTORS, new Object[] { x.length, x[0].length });
        }
    }
    
    protected void validateCovarianceData(final double[][] x, final double[][] covariance) {
        if (x.length != covariance.length) {
            throw new DimensionMismatchException(x.length, covariance.length);
        }
        if (covariance.length > 0 && covariance.length != covariance[0].length) {
            throw new NonSquareMatrixException(covariance.length, covariance[0].length);
        }
    }
    
    public double[] estimateRegressionParameters() {
        final RealVector b = this.calculateBeta();
        return b.toArray();
    }
    
    public double[] estimateResiduals() {
        final RealVector b = this.calculateBeta();
        final RealVector e = this.yVector.subtract(this.xMatrix.operate(b));
        return e.toArray();
    }
    
    public double[][] estimateRegressionParametersVariance() {
        return this.calculateBetaVariance().getData();
    }
    
    public double[] estimateRegressionParametersStandardErrors() {
        final double[][] betaVariance = this.estimateRegressionParametersVariance();
        final double sigma = this.calculateErrorVariance();
        final int length = betaVariance[0].length;
        final double[] result = new double[length];
        for (int i = 0; i < length; ++i) {
            result[i] = FastMath.sqrt(sigma * betaVariance[i][i]);
        }
        return result;
    }
    
    public double estimateRegressandVariance() {
        return this.calculateYVariance();
    }
    
    public double estimateErrorVariance() {
        return this.calculateErrorVariance();
    }
    
    public double estimateRegressionStandardError() {
        return FastMath.sqrt(this.estimateErrorVariance());
    }
    
    protected abstract RealVector calculateBeta();
    
    protected abstract RealMatrix calculateBetaVariance();
    
    protected double calculateYVariance() {
        return new Variance().evaluate(this.yVector.toArray());
    }
    
    protected double calculateErrorVariance() {
        final RealVector residuals = this.calculateResiduals();
        return residuals.dotProduct(residuals) / (this.xMatrix.getRowDimension() - this.xMatrix.getColumnDimension());
    }
    
    protected RealVector calculateResiduals() {
        final RealVector b = this.calculateBeta();
        return this.yVector.subtract(this.xMatrix.operate(b));
    }
}
