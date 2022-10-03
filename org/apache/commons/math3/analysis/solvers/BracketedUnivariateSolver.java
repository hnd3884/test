package org.apache.commons.math3.analysis.solvers;

import org.apache.commons.math3.analysis.UnivariateFunction;

public interface BracketedUnivariateSolver<FUNC extends UnivariateFunction> extends BaseUnivariateSolver<FUNC>
{
    double solve(final int p0, final FUNC p1, final double p2, final double p3, final AllowedSolution p4);
    
    double solve(final int p0, final FUNC p1, final double p2, final double p3, final double p4, final AllowedSolution p5);
}
