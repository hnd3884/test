package org.apache.commons.math3.optim;

public interface ConvergenceChecker<PAIR>
{
    boolean converged(final int p0, final PAIR p1, final PAIR p2);
}
