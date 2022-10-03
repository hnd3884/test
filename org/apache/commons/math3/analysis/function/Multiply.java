package org.apache.commons.math3.analysis.function;

import org.apache.commons.math3.analysis.BivariateFunction;

public class Multiply implements BivariateFunction
{
    public double value(final double x, final double y) {
        return x * y;
    }
}
