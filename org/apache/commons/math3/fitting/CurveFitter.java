package org.apache.commons.math3.fitting;

import org.apache.commons.math3.analysis.MultivariateMatrixFunction;
import org.apache.commons.math3.optim.nonlinear.vector.ModelFunctionJacobian;
import org.apache.commons.math3.analysis.MultivariateVectorFunction;
import org.apache.commons.math3.optim.nonlinear.vector.ModelFunction;
import org.apache.commons.math3.optim.PointVectorValuePair;
import java.util.Iterator;
import org.apache.commons.math3.optim.InitialGuess;
import org.apache.commons.math3.optim.nonlinear.vector.Weight;
import org.apache.commons.math3.optim.nonlinear.vector.Target;
import org.apache.commons.math3.optim.MaxEval;
import org.apache.commons.math3.optim.OptimizationData;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.math3.optim.nonlinear.vector.MultivariateVectorOptimizer;
import org.apache.commons.math3.analysis.ParametricUnivariateFunction;

@Deprecated
public class CurveFitter<T extends ParametricUnivariateFunction>
{
    private final MultivariateVectorOptimizer optimizer;
    private final List<WeightedObservedPoint> observations;
    
    public CurveFitter(final MultivariateVectorOptimizer optimizer) {
        this.optimizer = optimizer;
        this.observations = new ArrayList<WeightedObservedPoint>();
    }
    
    public void addObservedPoint(final double x, final double y) {
        this.addObservedPoint(1.0, x, y);
    }
    
    public void addObservedPoint(final double weight, final double x, final double y) {
        this.observations.add(new WeightedObservedPoint(weight, x, y));
    }
    
    public void addObservedPoint(final WeightedObservedPoint observed) {
        this.observations.add(observed);
    }
    
    public WeightedObservedPoint[] getObservations() {
        return this.observations.toArray(new WeightedObservedPoint[this.observations.size()]);
    }
    
    public void clearObservations() {
        this.observations.clear();
    }
    
    public double[] fit(final T f, final double[] initialGuess) {
        return this.fit(Integer.MAX_VALUE, f, initialGuess);
    }
    
    public double[] fit(final int maxEval, final T f, final double[] initialGuess) {
        final double[] target = new double[this.observations.size()];
        final double[] weights = new double[this.observations.size()];
        int i = 0;
        for (final WeightedObservedPoint point : this.observations) {
            target[i] = point.getY();
            weights[i] = point.getWeight();
            ++i;
        }
        final TheoreticalValuesFunction model = new TheoreticalValuesFunction(f);
        final PointVectorValuePair optimum = this.optimizer.optimize(new MaxEval(maxEval), model.getModelFunction(), model.getModelFunctionJacobian(), new Target(target), new Weight(weights), new InitialGuess(initialGuess));
        return optimum.getPointRef();
    }
    
    private class TheoreticalValuesFunction
    {
        private final ParametricUnivariateFunction f;
        
        TheoreticalValuesFunction(final ParametricUnivariateFunction f) {
            this.f = f;
        }
        
        public ModelFunction getModelFunction() {
            return new ModelFunction(new MultivariateVectorFunction() {
                public double[] value(final double[] point) {
                    final double[] values = new double[CurveFitter.this.observations.size()];
                    int i = 0;
                    for (final WeightedObservedPoint observed : CurveFitter.this.observations) {
                        values[i++] = TheoreticalValuesFunction.this.f.value(observed.getX(), point);
                    }
                    return values;
                }
            });
        }
        
        public ModelFunctionJacobian getModelFunctionJacobian() {
            return new ModelFunctionJacobian(new MultivariateMatrixFunction() {
                public double[][] value(final double[] point) {
                    final double[][] jacobian = new double[CurveFitter.this.observations.size()][];
                    int i = 0;
                    for (final WeightedObservedPoint observed : CurveFitter.this.observations) {
                        jacobian[i++] = TheoreticalValuesFunction.this.f.gradient(observed.getX(), point);
                    }
                    return jacobian;
                }
            });
        }
    }
}
