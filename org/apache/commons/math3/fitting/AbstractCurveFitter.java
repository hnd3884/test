package org.apache.commons.math3.fitting;

import org.apache.commons.math3.analysis.MultivariateMatrixFunction;
import org.apache.commons.math3.analysis.MultivariateVectorFunction;
import java.util.Iterator;
import org.apache.commons.math3.analysis.ParametricUnivariateFunction;
import org.apache.commons.math3.fitting.leastsquares.LeastSquaresProblem;
import org.apache.commons.math3.fitting.leastsquares.LevenbergMarquardtOptimizer;
import org.apache.commons.math3.fitting.leastsquares.LeastSquaresOptimizer;
import java.util.Collection;

public abstract class AbstractCurveFitter
{
    public double[] fit(final Collection<WeightedObservedPoint> points) {
        return this.getOptimizer().optimize(this.getProblem(points)).getPoint().toArray();
    }
    
    protected LeastSquaresOptimizer getOptimizer() {
        return new LevenbergMarquardtOptimizer();
    }
    
    protected abstract LeastSquaresProblem getProblem(final Collection<WeightedObservedPoint> p0);
    
    protected static class TheoreticalValuesFunction
    {
        private final ParametricUnivariateFunction f;
        private final double[] points;
        
        public TheoreticalValuesFunction(final ParametricUnivariateFunction f, final Collection<WeightedObservedPoint> observations) {
            this.f = f;
            final int len = observations.size();
            this.points = new double[len];
            int i = 0;
            for (final WeightedObservedPoint obs : observations) {
                this.points[i++] = obs.getX();
            }
        }
        
        public MultivariateVectorFunction getModelFunction() {
            return new MultivariateVectorFunction() {
                public double[] value(final double[] p) {
                    final int len = TheoreticalValuesFunction.this.points.length;
                    final double[] values = new double[len];
                    for (int i = 0; i < len; ++i) {
                        values[i] = TheoreticalValuesFunction.this.f.value(TheoreticalValuesFunction.this.points[i], p);
                    }
                    return values;
                }
            };
        }
        
        public MultivariateMatrixFunction getModelFunctionJacobian() {
            return new MultivariateMatrixFunction() {
                public double[][] value(final double[] p) {
                    final int len = TheoreticalValuesFunction.this.points.length;
                    final double[][] jacobian = new double[len][];
                    for (int i = 0; i < len; ++i) {
                        jacobian[i] = TheoreticalValuesFunction.this.f.gradient(TheoreticalValuesFunction.this.points[i], p);
                    }
                    return jacobian;
                }
            };
        }
    }
}
