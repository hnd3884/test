package org.apache.commons.math3.dfp;

import org.apache.commons.math3.RealFieldElement;
import org.apache.commons.math3.analysis.RealFieldUnivariateFunction;
import org.apache.commons.math3.util.MathUtils;
import org.apache.commons.math3.exception.NoBracketingException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.analysis.solvers.AllowedSolution;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.analysis.solvers.FieldBracketingNthOrderBrentSolver;

@Deprecated
public class BracketingNthOrderBrentSolverDFP extends FieldBracketingNthOrderBrentSolver<Dfp>
{
    public BracketingNthOrderBrentSolverDFP(final Dfp relativeAccuracy, final Dfp absoluteAccuracy, final Dfp functionValueAccuracy, final int maximalOrder) throws NumberIsTooSmallException {
        super(relativeAccuracy, absoluteAccuracy, functionValueAccuracy, maximalOrder);
    }
    
    @Override
    public Dfp getAbsoluteAccuracy() {
        return super.getAbsoluteAccuracy();
    }
    
    @Override
    public Dfp getRelativeAccuracy() {
        return super.getRelativeAccuracy();
    }
    
    @Override
    public Dfp getFunctionValueAccuracy() {
        return super.getFunctionValueAccuracy();
    }
    
    public Dfp solve(final int maxEval, final UnivariateDfpFunction f, final Dfp min, final Dfp max, final AllowedSolution allowedSolution) throws NullArgumentException, NoBracketingException {
        return this.solve(maxEval, f, min, max, min.add(max).divide(2), allowedSolution);
    }
    
    public Dfp solve(final int maxEval, final UnivariateDfpFunction f, final Dfp min, final Dfp max, final Dfp startValue, final AllowedSolution allowedSolution) throws NullArgumentException, NoBracketingException {
        MathUtils.checkNotNull(f);
        final RealFieldUnivariateFunction<Dfp> fieldF = new RealFieldUnivariateFunction<Dfp>() {
            public Dfp value(final Dfp x) {
                return f.value(x);
            }
        };
        return this.solve(maxEval, fieldF, min, max, startValue, allowedSolution);
    }
}
