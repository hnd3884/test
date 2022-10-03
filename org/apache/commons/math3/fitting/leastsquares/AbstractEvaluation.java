package org.apache.commons.math3.fitting.leastsquares;

import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.linear.DecompositionSolver;
import org.apache.commons.math3.linear.QRDecomposition;
import org.apache.commons.math3.linear.RealMatrix;

public abstract class AbstractEvaluation implements LeastSquaresProblem.Evaluation
{
    private final int observationSize;
    
    AbstractEvaluation(final int observationSize) {
        this.observationSize = observationSize;
    }
    
    public RealMatrix getCovariances(final double threshold) {
        final RealMatrix j = this.getJacobian();
        final RealMatrix jTj = j.transpose().multiply(j);
        final DecompositionSolver solver = new QRDecomposition(jTj, threshold).getSolver();
        return solver.getInverse();
    }
    
    public RealVector getSigma(final double covarianceSingularityThreshold) {
        final RealMatrix cov = this.getCovariances(covarianceSingularityThreshold);
        final int nC = cov.getColumnDimension();
        final RealVector sig = new ArrayRealVector(nC);
        for (int i = 0; i < nC; ++i) {
            sig.setEntry(i, FastMath.sqrt(cov.getEntry(i, i)));
        }
        return sig;
    }
    
    public double getRMS() {
        final double cost = this.getCost();
        return FastMath.sqrt(cost * cost / this.observationSize);
    }
    
    public double getCost() {
        final ArrayRealVector r = new ArrayRealVector(this.getResiduals());
        return FastMath.sqrt(r.dotProduct(r));
    }
}
