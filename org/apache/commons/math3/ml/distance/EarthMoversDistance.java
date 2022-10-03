package org.apache.commons.math3.ml.distance;

import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.MathArrays;

public class EarthMoversDistance implements DistanceMeasure
{
    private static final long serialVersionUID = -5406732779747414922L;
    
    public double compute(final double[] a, final double[] b) throws DimensionMismatchException {
        MathArrays.checkEqualLength(a, b);
        double lastDistance = 0.0;
        double totalDistance = 0.0;
        for (int i = 0; i < a.length; ++i) {
            final double currentDistance = a[i] + lastDistance - b[i];
            totalDistance += FastMath.abs(currentDistance);
            lastDistance = currentDistance;
        }
        return totalDistance;
    }
}
