package org.apache.commons.math3.analysis.interpolation;

import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.exception.NoDataException;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.analysis.MultivariateFunction;

public interface MultivariateInterpolator
{
    MultivariateFunction interpolate(final double[][] p0, final double[] p1) throws MathIllegalArgumentException, DimensionMismatchException, NoDataException, NullArgumentException;
}
