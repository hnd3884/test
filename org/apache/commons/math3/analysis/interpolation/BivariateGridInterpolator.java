package org.apache.commons.math3.analysis.interpolation;

import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.exception.NonMonotonicSequenceException;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.NoDataException;
import org.apache.commons.math3.analysis.BivariateFunction;

public interface BivariateGridInterpolator
{
    BivariateFunction interpolate(final double[] p0, final double[] p1, final double[][] p2) throws NoDataException, DimensionMismatchException, NonMonotonicSequenceException, NumberIsTooSmallException;
}
