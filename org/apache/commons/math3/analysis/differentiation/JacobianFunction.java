package org.apache.commons.math3.analysis.differentiation;

import org.apache.commons.math3.analysis.MultivariateMatrixFunction;

public class JacobianFunction implements MultivariateMatrixFunction
{
    private final MultivariateDifferentiableVectorFunction f;
    
    public JacobianFunction(final MultivariateDifferentiableVectorFunction f) {
        this.f = f;
    }
    
    public double[][] value(final double[] point) {
        final DerivativeStructure[] dsX = new DerivativeStructure[point.length];
        for (int i = 0; i < point.length; ++i) {
            dsX[i] = new DerivativeStructure(point.length, 1, i, point[i]);
        }
        final DerivativeStructure[] dsY = this.f.value(dsX);
        final double[][] y = new double[dsY.length][point.length];
        final int[] orders = new int[point.length];
        for (int j = 0; j < dsY.length; ++j) {
            for (int k = 0; k < point.length; ++k) {
                orders[k] = 1;
                y[j][k] = dsY[j].getPartialDerivative(orders);
                orders[k] = 0;
            }
        }
        return y;
    }
}
