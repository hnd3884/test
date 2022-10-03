package org.apache.commons.math3.optimization.general;

import org.apache.commons.math3.linear.DecompositionSolver;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.exception.MathInternalError;
import org.apache.commons.math3.linear.SingularMatrixException;
import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.ConvergenceException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.QRDecomposition;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.BlockRealMatrix;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.optimization.SimpleVectorValueChecker;
import org.apache.commons.math3.optimization.PointVectorValuePair;
import org.apache.commons.math3.optimization.ConvergenceChecker;

@Deprecated
public class GaussNewtonOptimizer extends AbstractLeastSquaresOptimizer
{
    private final boolean useLU;
    
    @Deprecated
    public GaussNewtonOptimizer() {
        this(true);
    }
    
    public GaussNewtonOptimizer(final ConvergenceChecker<PointVectorValuePair> checker) {
        this(true, checker);
    }
    
    @Deprecated
    public GaussNewtonOptimizer(final boolean useLU) {
        this(useLU, new SimpleVectorValueChecker());
    }
    
    public GaussNewtonOptimizer(final boolean useLU, final ConvergenceChecker<PointVectorValuePair> checker) {
        super(checker);
        this.useLU = useLU;
    }
    
    public PointVectorValuePair doOptimize() {
        final ConvergenceChecker<PointVectorValuePair> checker = this.getConvergenceChecker();
        if (checker == null) {
            throw new NullArgumentException();
        }
        final double[] targetValues = this.getTarget();
        final int nR = targetValues.length;
        final RealMatrix weightMatrix = this.getWeight();
        final double[] residualsWeights = new double[nR];
        for (int i = 0; i < nR; ++i) {
            residualsWeights[i] = weightMatrix.getEntry(i, i);
        }
        final double[] currentPoint = this.getStartPoint();
        final int nC = currentPoint.length;
        PointVectorValuePair current = null;
        int iter = 0;
        boolean converged = false;
        while (!converged) {
            ++iter;
            final PointVectorValuePair previous = current;
            final double[] currentObjective = this.computeObjectiveValue(currentPoint);
            final double[] currentResiduals = this.computeResiduals(currentObjective);
            final RealMatrix weightedJacobian = this.computeWeightedJacobian(currentPoint);
            current = new PointVectorValuePair(currentPoint, currentObjective);
            final double[] b = new double[nC];
            final double[][] a = new double[nC][nC];
            for (int j = 0; j < nR; ++j) {
                final double[] grad = weightedJacobian.getRow(j);
                final double weight = residualsWeights[j];
                final double residual = currentResiduals[j];
                final double wr = weight * residual;
                for (int k = 0; k < nC; ++k) {
                    final double[] array = b;
                    final int n = k;
                    array[n] += wr * grad[k];
                }
                for (int l = 0; l < nC; ++l) {
                    final double[] ak = a[l];
                    final double wgk = weight * grad[l];
                    for (int m = 0; m < nC; ++m) {
                        final double[] array2 = ak;
                        final int n2 = m;
                        array2[n2] += wgk * grad[m];
                    }
                }
            }
            try {
                final RealMatrix mA = new BlockRealMatrix(a);
                final DecompositionSolver solver = this.useLU ? new LUDecomposition(mA).getSolver() : new QRDecomposition(mA).getSolver();
                final double[] dX = solver.solve(new ArrayRealVector(b, false)).toArray();
                for (int i2 = 0; i2 < nC; ++i2) {
                    final double[] array3 = currentPoint;
                    final int n3 = i2;
                    array3[n3] += dX[i2];
                }
            }
            catch (final SingularMatrixException e) {
                throw new ConvergenceException(LocalizedFormats.UNABLE_TO_SOLVE_SINGULAR_PROBLEM, new Object[0]);
            }
            if (previous != null) {
                converged = checker.converged(iter, previous, current);
                if (converged) {
                    this.cost = this.computeCost(currentResiduals);
                    this.point = current.getPoint();
                    return current;
                }
                continue;
            }
        }
        throw new MathInternalError();
    }
}
