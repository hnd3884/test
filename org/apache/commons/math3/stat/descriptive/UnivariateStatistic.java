package org.apache.commons.math3.stat.descriptive;

import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.util.MathArrays;

public interface UnivariateStatistic extends MathArrays.Function
{
    double evaluate(final double[] p0) throws MathIllegalArgumentException;
    
    double evaluate(final double[] p0, final int p1, final int p2) throws MathIllegalArgumentException;
    
    UnivariateStatistic copy();
}
