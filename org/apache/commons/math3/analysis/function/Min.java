package org.apache.commons.math3.analysis.function;

import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.analysis.BivariateFunction;

public class Min implements BivariateFunction
{
    public double value(final double x, final double y) {
        return FastMath.min(x, y);
    }
}
