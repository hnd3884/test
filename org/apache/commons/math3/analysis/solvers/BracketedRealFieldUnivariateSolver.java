package org.apache.commons.math3.analysis.solvers;

import org.apache.commons.math3.analysis.RealFieldUnivariateFunction;
import org.apache.commons.math3.RealFieldElement;

public interface BracketedRealFieldUnivariateSolver<T extends RealFieldElement<T>>
{
    int getMaxEvaluations();
    
    int getEvaluations();
    
    T getAbsoluteAccuracy();
    
    T getRelativeAccuracy();
    
    T getFunctionValueAccuracy();
    
    T solve(final int p0, final RealFieldUnivariateFunction<T> p1, final T p2, final T p3, final AllowedSolution p4);
    
    T solve(final int p0, final RealFieldUnivariateFunction<T> p1, final T p2, final T p3, final T p4, final AllowedSolution p5);
}
