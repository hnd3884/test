package org.apache.commons.math3.optim.nonlinear.vector.jacobian;

import org.apache.commons.math3.linear.EigenDecomposition;
import org.apache.commons.math3.linear.DiagonalMatrix;
import org.apache.commons.math3.optim.nonlinear.vector.Weight;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.TooManyEvaluationsException;
import org.apache.commons.math3.optim.OptimizationData;
import org.apache.commons.math3.linear.DecompositionSolver;
import org.apache.commons.math3.linear.QRDecomposition;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.optim.PointVectorValuePair;
import org.apache.commons.math3.optim.ConvergenceChecker;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.optim.nonlinear.vector.JacobianMultivariateVectorOptimizer;

@Deprecated
public abstract class AbstractLeastSquaresOptimizer extends JacobianMultivariateVectorOptimizer
{
    private RealMatrix weightMatrixSqrt;
    private double cost;
    
    protected AbstractLeastSquaresOptimizer(final ConvergenceChecker<PointVectorValuePair> checker) {
        super(checker);
    }
    
    protected RealMatrix computeWeightedJacobian(final double[] params) {
        return this.weightMatrixSqrt.multiply(MatrixUtils.createRealMatrix(this.computeJacobian(params)));
    }
    
    protected double computeCost(final double[] residuals) {
        final ArrayRealVector r = new ArrayRealVector(residuals);
        return FastMath.sqrt(r.dotProduct(this.getWeight().operate(r)));
    }
    
    public double getRMS() {
        return FastMath.sqrt(this.getChiSquare() / this.getTargetSize());
    }
    
    public double getChiSquare() {
        return this.cost * this.cost;
    }
    
    public RealMatrix getWeightSquareRoot() {
        return this.weightMatrixSqrt.copy();
    }
    
    protected void setCost(final double cost) {
        this.cost = cost;
    }
    
    public double[][] computeCovariances(final double[] params, final double threshold) {
        final RealMatrix j = this.computeWeightedJacobian(params);
        final RealMatrix jTj = j.transpose().multiply(j);
        final DecompositionSolver solver = new QRDecomposition(jTj, threshold).getSolver();
        return solver.getInverse().getData();
    }
    
    public double[] computeSigma(final double[] params, final double covarianceSingularityThreshold) {
        final int nC = params.length;
        final double[] sig = new double[nC];
        final double[][] cov = this.computeCovariances(params, covarianceSingularityThreshold);
        for (int i = 0; i < nC; ++i) {
            sig[i] = FastMath.sqrt(cov[i][i]);
        }
        return sig;
    }
    
    @Override
    public PointVectorValuePair optimize(final OptimizationData... optData) throws TooManyEvaluationsException {
        return super.optimize(optData);
    }
    
    protected double[] computeResiduals(final double[] objectiveValue) {
        final double[] target = this.getTarget();
        if (objectiveValue.length != target.length) {
            throw new DimensionMismatchException(target.length, objectiveValue.length);
        }
        final double[] residuals = new double[target.length];
        for (int i = 0; i < target.length; ++i) {
            residuals[i] = target[i] - objectiveValue[i];
        }
        return residuals;
    }
    
    @Override
    protected void parseOptimizationData(final OptimizationData... optData) {
        super.parseOptimizationData(optData);
        for (final OptimizationData data : optData) {
            if (data instanceof Weight) {
                this.weightMatrixSqrt = this.squareRoot(((Weight)data).getWeight());
                break;
            }
        }
    }
    
    private RealMatrix squareRoot(final RealMatrix m) {
        if (m instanceof DiagonalMatrix) {
            final int dim = m.getRowDimension();
            final RealMatrix sqrtM = new DiagonalMatrix(dim);
            for (int i = 0; i < dim; ++i) {
                sqrtM.setEntry(i, i, FastMath.sqrt(m.getEntry(i, i)));
            }
            return sqrtM;
        }
        final EigenDecomposition dec = new EigenDecomposition(m);
        return dec.getSquareRoot();
    }
}
