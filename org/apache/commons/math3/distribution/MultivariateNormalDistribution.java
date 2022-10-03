package org.apache.commons.math3.distribution;

import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.linear.EigenDecomposition;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.util.MathArrays;
import org.apache.commons.math3.linear.NonPositiveDefiniteMatrixException;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.linear.SingularMatrixException;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.random.Well19937c;
import org.apache.commons.math3.linear.RealMatrix;

public class MultivariateNormalDistribution extends AbstractMultivariateRealDistribution
{
    private final double[] means;
    private final RealMatrix covarianceMatrix;
    private final RealMatrix covarianceMatrixInverse;
    private final double covarianceMatrixDeterminant;
    private final RealMatrix samplingMatrix;
    
    public MultivariateNormalDistribution(final double[] means, final double[][] covariances) throws SingularMatrixException, DimensionMismatchException, NonPositiveDefiniteMatrixException {
        this(new Well19937c(), means, covariances);
    }
    
    public MultivariateNormalDistribution(final RandomGenerator rng, final double[] means, final double[][] covariances) throws SingularMatrixException, DimensionMismatchException, NonPositiveDefiniteMatrixException {
        super(rng, means.length);
        final int dim = means.length;
        if (covariances.length != dim) {
            throw new DimensionMismatchException(covariances.length, dim);
        }
        for (int i = 0; i < dim; ++i) {
            if (dim != covariances[i].length) {
                throw new DimensionMismatchException(covariances[i].length, dim);
            }
        }
        this.means = MathArrays.copyOf(means);
        this.covarianceMatrix = new Array2DRowRealMatrix(covariances);
        final EigenDecomposition covMatDec = new EigenDecomposition(this.covarianceMatrix);
        this.covarianceMatrixInverse = covMatDec.getSolver().getInverse();
        this.covarianceMatrixDeterminant = covMatDec.getDeterminant();
        final double[] covMatEigenvalues = covMatDec.getRealEigenvalues();
        for (int j = 0; j < covMatEigenvalues.length; ++j) {
            if (covMatEigenvalues[j] < 0.0) {
                throw new NonPositiveDefiniteMatrixException(covMatEigenvalues[j], j, 0.0);
            }
        }
        final Array2DRowRealMatrix covMatEigenvectors = new Array2DRowRealMatrix(dim, dim);
        for (int v = 0; v < dim; ++v) {
            final double[] evec = covMatDec.getEigenvector(v).toArray();
            covMatEigenvectors.setColumn(v, evec);
        }
        final RealMatrix tmpMatrix = covMatEigenvectors.transpose();
        for (int row = 0; row < dim; ++row) {
            final double factor = FastMath.sqrt(covMatEigenvalues[row]);
            for (int col = 0; col < dim; ++col) {
                tmpMatrix.multiplyEntry(row, col, factor);
            }
        }
        this.samplingMatrix = covMatEigenvectors.multiply(tmpMatrix);
    }
    
    public double[] getMeans() {
        return MathArrays.copyOf(this.means);
    }
    
    public RealMatrix getCovariances() {
        return this.covarianceMatrix.copy();
    }
    
    public double density(final double[] vals) throws DimensionMismatchException {
        final int dim = this.getDimension();
        if (vals.length != dim) {
            throw new DimensionMismatchException(vals.length, dim);
        }
        return FastMath.pow(6.283185307179586, -0.5 * dim) * FastMath.pow(this.covarianceMatrixDeterminant, -0.5) * this.getExponentTerm(vals);
    }
    
    public double[] getStandardDeviations() {
        final int dim = this.getDimension();
        final double[] std = new double[dim];
        final double[][] s = this.covarianceMatrix.getData();
        for (int i = 0; i < dim; ++i) {
            std[i] = FastMath.sqrt(s[i][i]);
        }
        return std;
    }
    
    @Override
    public double[] sample() {
        final int dim = this.getDimension();
        final double[] normalVals = new double[dim];
        for (int i = 0; i < dim; ++i) {
            normalVals[i] = this.random.nextGaussian();
        }
        final double[] vals = this.samplingMatrix.operate(normalVals);
        for (int j = 0; j < dim; ++j) {
            final double[] array = vals;
            final int n = j;
            array[n] += this.means[j];
        }
        return vals;
    }
    
    private double getExponentTerm(final double[] values) {
        final double[] centered = new double[values.length];
        for (int i = 0; i < centered.length; ++i) {
            centered[i] = values[i] - this.getMeans()[i];
        }
        final double[] preMultiplied = this.covarianceMatrixInverse.preMultiply(centered);
        double sum = 0.0;
        for (int j = 0; j < preMultiplied.length; ++j) {
            sum += preMultiplied[j] * centered[j];
        }
        return FastMath.exp(-0.5 * sum);
    }
}
