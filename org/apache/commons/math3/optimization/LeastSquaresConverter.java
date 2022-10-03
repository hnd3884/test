package org.apache.commons.math3.optimization;

import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.analysis.MultivariateVectorFunction;
import org.apache.commons.math3.analysis.MultivariateFunction;

@Deprecated
public class LeastSquaresConverter implements MultivariateFunction
{
    private final MultivariateVectorFunction function;
    private final double[] observations;
    private final double[] weights;
    private final RealMatrix scale;
    
    public LeastSquaresConverter(final MultivariateVectorFunction function, final double[] observations) {
        this.function = function;
        this.observations = observations.clone();
        this.weights = null;
        this.scale = null;
    }
    
    public LeastSquaresConverter(final MultivariateVectorFunction function, final double[] observations, final double[] weights) {
        if (observations.length != weights.length) {
            throw new DimensionMismatchException(observations.length, weights.length);
        }
        this.function = function;
        this.observations = observations.clone();
        this.weights = weights.clone();
        this.scale = null;
    }
    
    public LeastSquaresConverter(final MultivariateVectorFunction function, final double[] observations, final RealMatrix scale) {
        if (observations.length != scale.getColumnDimension()) {
            throw new DimensionMismatchException(observations.length, scale.getColumnDimension());
        }
        this.function = function;
        this.observations = observations.clone();
        this.weights = null;
        this.scale = scale.copy();
    }
    
    public double value(final double[] point) {
        final double[] residuals = this.function.value(point);
        if (residuals.length != this.observations.length) {
            throw new DimensionMismatchException(residuals.length, this.observations.length);
        }
        for (int i = 0; i < residuals.length; ++i) {
            final double[] array = residuals;
            final int n = i;
            array[n] -= this.observations[i];
        }
        double sumSquares = 0.0;
        if (this.weights != null) {
            for (int j = 0; j < residuals.length; ++j) {
                final double ri = residuals[j];
                sumSquares += this.weights[j] * ri * ri;
            }
        }
        else if (this.scale != null) {
            for (final double yi : this.scale.operate(residuals)) {
                sumSquares += yi * yi;
            }
        }
        else {
            for (final double ri2 : residuals) {
                sumSquares += ri2 * ri2;
            }
        }
        return sumSquares;
    }
}
