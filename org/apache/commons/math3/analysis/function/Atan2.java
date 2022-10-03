package org.apache.commons.math3.analysis.function;

import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.analysis.BivariateFunction;

public class Atan2 implements BivariateFunction
{
    public double value(final double x, final double y) {
        return FastMath.atan2(x, y);
    }
}
