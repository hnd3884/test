package org.apache.commons.math3.ml.distance;

import org.apache.commons.math3.exception.DimensionMismatchException;
import java.io.Serializable;

public interface DistanceMeasure extends Serializable
{
    double compute(final double[] p0, final double[] p1) throws DimensionMismatchException;
}
