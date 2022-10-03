package org.apache.commons.math3.analysis.differentiation;

import org.apache.commons.math3.analysis.MultivariateVectorFunction;

public class GradientFunction implements MultivariateVectorFunction
{
    private final MultivariateDifferentiableFunction f;
    
    public GradientFunction(final MultivariateDifferentiableFunction f) {
        this.f = f;
    }
    
    public double[] value(final double[] point) {
        final DerivativeStructure[] dsX = new DerivativeStructure[point.length];
        for (int i = 0; i < point.length; ++i) {
            dsX[i] = new DerivativeStructure(point.length, 1, i, point[i]);
        }
        final DerivativeStructure dsY = this.f.value(dsX);
        final double[] y = new double[point.length];
        final int[] orders = new int[point.length];
        for (int j = 0; j < point.length; ++j) {
            orders[j] = 1;
            y[j] = dsY.getPartialDerivative(orders);
            orders[j] = 0;
        }
        return y;
    }
}
