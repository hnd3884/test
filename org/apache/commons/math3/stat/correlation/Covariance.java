package org.apache.commons.math3.stat.correlation;

import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.Variance;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.linear.BlockRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;

public class Covariance
{
    private final RealMatrix covarianceMatrix;
    private final int n;
    
    public Covariance() {
        this.covarianceMatrix = null;
        this.n = 0;
    }
    
    public Covariance(final double[][] data, final boolean biasCorrected) throws MathIllegalArgumentException, NotStrictlyPositiveException {
        this(new BlockRealMatrix(data), biasCorrected);
    }
    
    public Covariance(final double[][] data) throws MathIllegalArgumentException, NotStrictlyPositiveException {
        this(data, true);
    }
    
    public Covariance(final RealMatrix matrix, final boolean biasCorrected) throws MathIllegalArgumentException {
        this.checkSufficientData(matrix);
        this.n = matrix.getRowDimension();
        this.covarianceMatrix = this.computeCovarianceMatrix(matrix, biasCorrected);
    }
    
    public Covariance(final RealMatrix matrix) throws MathIllegalArgumentException {
        this(matrix, true);
    }
    
    public RealMatrix getCovarianceMatrix() {
        return this.covarianceMatrix;
    }
    
    public int getN() {
        return this.n;
    }
    
    protected RealMatrix computeCovarianceMatrix(final RealMatrix matrix, final boolean biasCorrected) throws MathIllegalArgumentException {
        final int dimension = matrix.getColumnDimension();
        final Variance variance = new Variance(biasCorrected);
        final RealMatrix outMatrix = new BlockRealMatrix(dimension, dimension);
        for (int i = 0; i < dimension; ++i) {
            for (int j = 0; j < i; ++j) {
                final double cov = this.covariance(matrix.getColumn(i), matrix.getColumn(j), biasCorrected);
                outMatrix.setEntry(i, j, cov);
                outMatrix.setEntry(j, i, cov);
            }
            outMatrix.setEntry(i, i, variance.evaluate(matrix.getColumn(i)));
        }
        return outMatrix;
    }
    
    protected RealMatrix computeCovarianceMatrix(final RealMatrix matrix) throws MathIllegalArgumentException {
        return this.computeCovarianceMatrix(matrix, true);
    }
    
    protected RealMatrix computeCovarianceMatrix(final double[][] data, final boolean biasCorrected) throws MathIllegalArgumentException, NotStrictlyPositiveException {
        return this.computeCovarianceMatrix(new BlockRealMatrix(data), biasCorrected);
    }
    
    protected RealMatrix computeCovarianceMatrix(final double[][] data) throws MathIllegalArgumentException, NotStrictlyPositiveException {
        return this.computeCovarianceMatrix(data, true);
    }
    
    public double covariance(final double[] xArray, final double[] yArray, final boolean biasCorrected) throws MathIllegalArgumentException {
        final Mean mean = new Mean();
        double result = 0.0;
        final int length = xArray.length;
        if (length != yArray.length) {
            throw new MathIllegalArgumentException(LocalizedFormats.DIMENSIONS_MISMATCH_SIMPLE, new Object[] { length, yArray.length });
        }
        if (length < 2) {
            throw new MathIllegalArgumentException(LocalizedFormats.INSUFFICIENT_OBSERVED_POINTS_IN_SAMPLE, new Object[] { length, 2 });
        }
        final double xMean = mean.evaluate(xArray);
        final double yMean = mean.evaluate(yArray);
        for (int i = 0; i < length; ++i) {
            final double xDev = xArray[i] - xMean;
            final double yDev = yArray[i] - yMean;
            result += (xDev * yDev - result) / (i + 1);
        }
        return biasCorrected ? (result * (length / (double)(length - 1))) : result;
    }
    
    public double covariance(final double[] xArray, final double[] yArray) throws MathIllegalArgumentException {
        return this.covariance(xArray, yArray, true);
    }
    
    private void checkSufficientData(final RealMatrix matrix) throws MathIllegalArgumentException {
        final int nRows = matrix.getRowDimension();
        final int nCols = matrix.getColumnDimension();
        if (nRows < 2 || nCols < 1) {
            throw new MathIllegalArgumentException(LocalizedFormats.INSUFFICIENT_ROWS_AND_COLUMNS, new Object[] { nRows, nCols });
        }
    }
}
