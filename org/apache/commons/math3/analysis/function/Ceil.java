package org.apache.commons.math3.analysis.function;

import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.analysis.UnivariateFunction;

public class Ceil implements UnivariateFunction
{
    public double value(final double x) {
        return FastMath.ceil(x);
    }
}
