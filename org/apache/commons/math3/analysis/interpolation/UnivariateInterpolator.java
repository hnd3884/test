package org.apache.commons.math3.analysis.interpolation;

import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.analysis.UnivariateFunction;

public interface UnivariateInterpolator
{
    UnivariateFunction interpolate(final double[] p0, final double[] p1) throws MathIllegalArgumentException, DimensionMismatchException;
}
