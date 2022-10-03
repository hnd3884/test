package org.apache.commons.math3.optimization.general;

import org.apache.commons.math3.analysis.MultivariateVectorFunction;
import org.apache.commons.math3.linear.EigenDecomposition;
import org.apache.commons.math3.linear.DiagonalMatrix;
import org.apache.commons.math3.optimization.InitialGuess;
import org.apache.commons.math3.optimization.Weight;
import org.apache.commons.math3.optimization.Target;
import org.apache.commons.math3.optimization.OptimizationData;
import org.apache.commons.math3.analysis.FunctionUtils;
import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.linear.DecompositionSolver;
import org.apache.commons.math3.linear.QRDecomposition;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.analysis.differentiation.DerivativeStructure;
import org.apache.commons.math3.optimization.PointVectorValuePair;
import org.apache.commons.math3.optimization.ConvergenceChecker;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.analysis.differentiation.MultivariateDifferentiableVectorFunction;
import org.apache.commons.math3.optimization.DifferentiableMultivariateVectorOptimizer;
import org.apache.commons.math3.analysis.DifferentiableMultivariateVectorFunction;
import org.apache.commons.math3.optimization.direct.BaseAbstractMultivariateVectorOptimizer;

@Deprecated
public abstract class AbstractLeastSquaresOptimizer extends BaseAbstractMultivariateVectorOptimizer<DifferentiableMultivariateVectorFunction> implements DifferentiableMultivariateVectorOptimizer
{
    @Deprecated
    private static final double DEFAULT_SINGULARITY_THRESHOLD = 1.0E-14;
    @Deprecated
    protected double[][] weightedResidualJacobian;
    @Deprecated
    protected int cols;
    @Deprecated
    protected int rows;
    @Deprecated
    protected double[] point;
    @Deprecated
    protected double[] objective;
    @Deprecated
    protected double[] weightedResiduals;
    @Deprecated
    protected double cost;
    private MultivariateDifferentiableVectorFunction jF;
    private int jacobianEvaluations;
    private RealMatrix weightMatrixSqrt;
    
    @Deprecated
    protected AbstractLeastSquaresOptimizer() {
    }
    
    protected AbstractLeastSquaresOptimizer(final ConvergenceChecker<PointVectorValuePair> checker) {
        super(checker);
    }
    
    public int getJacobianEvaluations() {
        return this.jacobianEvaluations;
    }
    
    @Deprecated
    protected void updateJacobian() {
        final RealMatrix weightedJacobian = this.computeWeightedJacobian(this.point);
        this.weightedResidualJacobian = weightedJacobian.scalarMultiply(-1.0).getData();
    }
    
    protected RealMatrix computeWeightedJacobian(final double[] params) {
        ++this.jacobianEvaluations;
        final DerivativeStructure[] dsPoint = new DerivativeStructure[params.length];
        final int nC = params.length;
        for (int i = 0; i < nC; ++i) {
            dsPoint[i] = new DerivativeStructure(nC, 1, i, params[i]);
        }
        final DerivativeStructure[] dsValue = this.jF.value(dsPoint);
        final int nR = this.getTarget().length;
        if (dsValue.length != nR) {
            throw new DimensionMismatchException(dsValue.length, nR);
        }
        final double[][] jacobianData = new double[nR][nC];
        for (int j = 0; j < nR; ++j) {
            final int[] orders = new int[nC];
            for (int k = 0; k < nC; ++k) {
                orders[k] = 1;
                jacobianData[j][k] = dsValue[j].getPartialDerivative(orders);
                orders[k] = 0;
            }
        }
        return this.weightMatrixSqrt.multiply(MatrixUtils.createRealMatrix(jacobianData));
    }
    
    @Deprecated
    protected void updateResidualsAndCost() {
        this.objective = this.computeObjectiveValue(this.point);
        final double[] res = this.computeResiduals(this.objective);
        this.cost = this.computeCost(res);
        final ArrayRealVector residuals = new ArrayRealVector(res);
        this.weightedResiduals = this.weightMatrixSqrt.operate(residuals).toArray();
    }
    
    protected double computeCost(final double[] residuals) {
        final ArrayRealVector r = new ArrayRealVector(residuals);
        return FastMath.sqrt(r.dotProduct(this.getWeight().operate(r)));
    }
    
    public double getRMS() {
        return FastMath.sqrt(this.getChiSquare() / this.rows);
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
    
    @Deprecated
    public double[][] getCovariances() {
        return this.getCovariances(1.0E-14);
    }
    
    @Deprecated
    public double[][] getCovariances(final double threshold) {
        return this.computeCovariances(this.point, threshold);
    }
    
    public double[][] computeCovariances(final double[] params, final double threshold) {
        final RealMatrix j = this.computeWeightedJacobian(params);
        final RealMatrix jTj = j.transpose().multiply(j);
        final DecompositionSolver solver = new QRDecomposition(jTj, threshold).getSolver();
        return solver.getInverse().getData();
    }
    
    @Deprecated
    public double[] guessParametersErrors() {
        if (this.rows <= this.cols) {
            throw new NumberIsTooSmallException(LocalizedFormats.NO_DEGREES_OF_FREEDOM, this.rows, this.cols, false);
        }
        final double[] errors = new double[this.cols];
        final double c = FastMath.sqrt(this.getChiSquare() / (this.rows - this.cols));
        final double[][] covar = this.computeCovariances(this.point, 1.0E-14);
        for (int i = 0; i < errors.length; ++i) {
            errors[i] = FastMath.sqrt(covar[i][i]) * c;
        }
        return errors;
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
    
    @Deprecated
    @Override
    public PointVectorValuePair optimize(final int maxEval, final DifferentiableMultivariateVectorFunction f, final double[] target, final double[] weights, final double[] startPoint) {
        return this.optimizeInternal(maxEval, FunctionUtils.toMultivariateDifferentiableVectorFunction(f), new Target(target), new Weight(weights), new InitialGuess(startPoint));
    }
    
    @Deprecated
    public PointVectorValuePair optimize(final int maxEval, final MultivariateDifferentiableVectorFunction f, final double[] target, final double[] weights, final double[] startPoint) {
        return this.optimizeInternal(maxEval, f, new Target(target), new Weight(weights), new InitialGuess(startPoint));
    }
    
    @Deprecated
    protected PointVectorValuePair optimizeInternal(final int maxEval, final MultivariateDifferentiableVectorFunction f, final OptimizationData... optData) {
        return super.optimizeInternal(maxEval, FunctionUtils.toDifferentiableMultivariateVectorFunction(f), optData);
    }
    
    @Override
    protected void setUp() {
        super.setUp();
        this.jacobianEvaluations = 0;
        this.weightMatrixSqrt = this.squareRoot(this.getWeight());
        this.jF = FunctionUtils.toMultivariateDifferentiableVectorFunction(this.getObjectiveFunction());
        this.point = this.getStartPoint();
        this.rows = this.getTarget().length;
        this.cols = this.point.length;
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
