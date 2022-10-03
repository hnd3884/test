package org.apache.commons.math3.analysis.integration;

import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.exception.TooManyEvaluationsException;
import org.apache.commons.math3.analysis.UnivariateFunction;

public interface UnivariateIntegrator
{
    double getRelativeAccuracy();
    
    double getAbsoluteAccuracy();
    
    int getMinimalIterationCount();
    
    int getMaximalIterationCount();
    
    double integrate(final int p0, final UnivariateFunction p1, final double p2, final double p3) throws TooManyEvaluationsException, MaxCountExceededException, MathIllegalArgumentException, NullArgumentException;
    
    int getEvaluations();
    
    int getIterations();
}
