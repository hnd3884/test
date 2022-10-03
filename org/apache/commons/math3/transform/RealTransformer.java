package org.apache.commons.math3.transform;

import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.exception.NonMonotonicSequenceException;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.exception.MathIllegalArgumentException;

public interface RealTransformer
{
    double[] transform(final double[] p0, final TransformType p1) throws MathIllegalArgumentException;
    
    double[] transform(final UnivariateFunction p0, final double p1, final double p2, final int p3, final TransformType p4) throws NonMonotonicSequenceException, NotStrictlyPositiveException, MathIllegalArgumentException;
}
