package org.apache.commons.math3.fitting.leastsquares;

import org.apache.commons.math3.util.Precision;
import org.apache.commons.math3.optim.ConvergenceChecker;

public class EvaluationRmsChecker implements ConvergenceChecker<LeastSquaresProblem.Evaluation>
{
    private final double relTol;
    private final double absTol;
    
    public EvaluationRmsChecker(final double tol) {
        this(tol, tol);
    }
    
    public EvaluationRmsChecker(final double relTol, final double absTol) {
        this.relTol = relTol;
        this.absTol = absTol;
    }
    
    public boolean converged(final int iteration, final LeastSquaresProblem.Evaluation previous, final LeastSquaresProblem.Evaluation current) {
        final double prevRms = previous.getRMS();
        final double currRms = current.getRMS();
        return Precision.equals(prevRms, currRms, this.absTol) || Precision.equalsWithRelativeTolerance(prevRms, currRms, this.relTol);
    }
}
