package org.apache.commons.math3.stat.descriptive;

import org.apache.commons.math3.exception.MathIllegalArgumentException;

public interface WeightedEvaluation
{
    double evaluate(final double[] p0, final double[] p1) throws MathIllegalArgumentException;
    
    double evaluate(final double[] p0, final double[] p1, final int p2, final int p3) throws MathIllegalArgumentException;
}
