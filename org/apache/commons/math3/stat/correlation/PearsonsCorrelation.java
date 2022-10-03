package org.apache.commons.math3.stat.correlation;

import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.apache.commons.math3.distribution.TDistribution;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.linear.BlockRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;

public class PearsonsCorrelation
{
    private final RealMatrix correlationMatrix;
    private final int nObs;
    
    public PearsonsCorrelation() {
        this.correlationMatrix = null;
        this.nObs = 0;
    }
    
    public PearsonsCorrelation(final double[][] data) {
        this(new BlockRealMatrix(data));
    }
    
    public PearsonsCorrelation(final RealMatrix matrix) {
        this.nObs = matrix.getRowDimension();
        this.correlationMatrix = this.computeCorrelationMatrix(matrix);
    }
    
    public PearsonsCorrelation(final Covariance covariance) {
        final RealMatrix covarianceMatrix = covariance.getCovarianceMatrix();
        if (covarianceMatrix == null) {
            throw new NullArgumentException(LocalizedFormats.COVARIANCE_MATRIX, new Object[0]);
        }
        this.nObs = covariance.getN();
        this.correlationMatrix = this.covarianceToCorrelation(covarianceMatrix);
    }
    
    public PearsonsCorrelation(final RealMatrix covarianceMatrix, final int numberOfObservations) {
        this.nObs = numberOfObservations;
        this.correlationMatrix = this.covarianceToCorrelation(covarianceMatrix);
    }
    
    public RealMatrix getCorrelationMatrix() {
        return this.correlationMatrix;
    }
    
    public RealMatrix getCorrelationStandardErrors() {
        final int nVars = this.correlationMatrix.getColumnDimension();
        final double[][] out = new double[nVars][nVars];
        for (int i = 0; i < nVars; ++i) {
            for (int j = 0; j < nVars; ++j) {
                final double r = this.correlationMatrix.getEntry(i, j);
                out[i][j] = FastMath.sqrt((1.0 - r * r) / (this.nObs - 2));
            }
        }
        return new BlockRealMatrix(out);
    }
    
    public RealMatrix getCorrelationPValues() {
        final TDistribution tDistribution = new TDistribution(this.nObs - 2);
        final int nVars = this.correlationMatrix.getColumnDimension();
        final double[][] out = new double[nVars][nVars];
        for (int i = 0; i < nVars; ++i) {
            for (int j = 0; j < nVars; ++j) {
                if (i == j) {
                    out[i][j] = 0.0;
                }
                else {
                    final double r = this.correlationMatrix.getEntry(i, j);
                    final double t = FastMath.abs(r * FastMath.sqrt((this.nObs - 2) / (1.0 - r * r)));
                    out[i][j] = 2.0 * tDistribution.cumulativeProbability(-t);
                }
            }
        }
        return new BlockRealMatrix(out);
    }
    
    public RealMatrix computeCorrelationMatrix(final RealMatrix matrix) {
        this.checkSufficientData(matrix);
        final int nVars = matrix.getColumnDimension();
        final RealMatrix outMatrix = new BlockRealMatrix(nVars, nVars);
        for (int i = 0; i < nVars; ++i) {
            for (int j = 0; j < i; ++j) {
                final double corr = this.correlation(matrix.getColumn(i), matrix.getColumn(j));
                outMatrix.setEntry(i, j, corr);
                outMatrix.setEntry(j, i, corr);
            }
            outMatrix.setEntry(i, i, 1.0);
        }
        return outMatrix;
    }
    
    public RealMatrix computeCorrelationMatrix(final double[][] data) {
        return this.computeCorrelationMatrix(new BlockRealMatrix(data));
    }
    
    public double correlation(final double[] xArray, final double[] yArray) {
        final SimpleRegression regression = new SimpleRegression();
        if (xArray.length != yArray.length) {
            throw new DimensionMismatchException(xArray.length, yArray.length);
        }
        if (xArray.length < 2) {
            throw new MathIllegalArgumentException(LocalizedFormats.INSUFFICIENT_DIMENSION, new Object[] { xArray.length, 2 });
        }
        for (int i = 0; i < xArray.length; ++i) {
            regression.addData(xArray[i], yArray[i]);
        }
        return regression.getR();
    }
    
    public RealMatrix covarianceToCorrelation(final RealMatrix covarianceMatrix) {
        final int nVars = covarianceMatrix.getColumnDimension();
        final RealMatrix outMatrix = new BlockRealMatrix(nVars, nVars);
        for (int i = 0; i < nVars; ++i) {
            final double sigma = FastMath.sqrt(covarianceMatrix.getEntry(i, i));
            outMatrix.setEntry(i, i, 1.0);
            for (int j = 0; j < i; ++j) {
                final double entry = covarianceMatrix.getEntry(i, j) / (sigma * FastMath.sqrt(covarianceMatrix.getEntry(j, j)));
                outMatrix.setEntry(i, j, entry);
                outMatrix.setEntry(j, i, entry);
            }
        }
        return outMatrix;
    }
    
    private void checkSufficientData(final RealMatrix matrix) {
        final int nRows = matrix.getRowDimension();
        final int nCols = matrix.getColumnDimension();
        if (nRows < 2 || nCols < 2) {
            throw new MathIllegalArgumentException(LocalizedFormats.INSUFFICIENT_ROWS_AND_COLUMNS, new Object[] { nRows, nCols });
        }
    }
}
