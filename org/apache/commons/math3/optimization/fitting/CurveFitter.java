package org.apache.commons.math3.optimization.fitting;

import org.apache.commons.math3.optimization.BaseMultivariateVectorOptimizer;
import org.apache.commons.math3.analysis.differentiation.DerivativeStructure;
import org.apache.commons.math3.analysis.differentiation.MultivariateDifferentiableVectorFunction;
import org.apache.commons.math3.analysis.MultivariateMatrixFunction;
import org.apache.commons.math3.analysis.DifferentiableMultivariateVectorFunction;
import org.apache.commons.math3.optimization.PointVectorValuePair;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.math3.optimization.MultivariateDifferentiableVectorOptimizer;
import org.apache.commons.math3.optimization.DifferentiableMultivariateVectorOptimizer;
import org.apache.commons.math3.analysis.ParametricUnivariateFunction;

@Deprecated
public class CurveFitter<T extends ParametricUnivariateFunction>
{
    @Deprecated
    private final DifferentiableMultivariateVectorOptimizer oldOptimizer;
    private final MultivariateDifferentiableVectorOptimizer optimizer;
    private final List<WeightedObservedPoint> observations;
    
    @Deprecated
    public CurveFitter(final DifferentiableMultivariateVectorOptimizer optimizer) {
        this.oldOptimizer = optimizer;
        this.optimizer = null;
        this.observations = new ArrayList<WeightedObservedPoint>();
    }
    
    public CurveFitter(final MultivariateDifferentiableVectorOptimizer optimizer) {
        this.oldOptimizer = null;
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
        PointVectorValuePair optimum;
        if (this.optimizer == null) {
            optimum = ((BaseMultivariateVectorOptimizer<OldTheoreticalValuesFunction>)this.oldOptimizer).optimize(maxEval, new OldTheoreticalValuesFunction(f), target, weights, initialGuess);
        }
        else {
            optimum = ((BaseMultivariateVectorOptimizer<TheoreticalValuesFunction>)this.optimizer).optimize(maxEval, new TheoreticalValuesFunction(f), target, weights, initialGuess);
        }
        return optimum.getPointRef();
    }
    
    @Deprecated
    private class OldTheoreticalValuesFunction implements DifferentiableMultivariateVectorFunction
    {
        private final ParametricUnivariateFunction f;
        
        OldTheoreticalValuesFunction(final ParametricUnivariateFunction f) {
            this.f = f;
        }
        
        public MultivariateMatrixFunction jacobian() {
            return new MultivariateMatrixFunction() {
                public double[][] value(final double[] point) {
                    final double[][] jacobian = new double[CurveFitter.this.observations.size()][];
                    int i = 0;
                    for (final WeightedObservedPoint observed : CurveFitter.this.observations) {
                        jacobian[i++] = OldTheoreticalValuesFunction.this.f.gradient(observed.getX(), point);
                    }
                    return jacobian;
                }
            };
        }
        
        public double[] value(final double[] point) {
            final double[] values = new double[CurveFitter.this.observations.size()];
            int i = 0;
            for (final WeightedObservedPoint observed : CurveFitter.this.observations) {
                values[i++] = this.f.value(observed.getX(), point);
            }
            return values;
        }
    }
    
    private class TheoreticalValuesFunction implements MultivariateDifferentiableVectorFunction
    {
        private final ParametricUnivariateFunction f;
        
        TheoreticalValuesFunction(final ParametricUnivariateFunction f) {
            this.f = f;
        }
        
        public double[] value(final double[] point) {
            final double[] values = new double[CurveFitter.this.observations.size()];
            int i = 0;
            for (final WeightedObservedPoint observed : CurveFitter.this.observations) {
                values[i++] = this.f.value(observed.getX(), point);
            }
            return values;
        }
        
        public DerivativeStructure[] value(final DerivativeStructure[] point) {
            final double[] parameters = new double[point.length];
            for (int k = 0; k < point.length; ++k) {
                parameters[k] = point[k].getValue();
            }
            final DerivativeStructure[] values = new DerivativeStructure[CurveFitter.this.observations.size()];
            int i = 0;
            for (final WeightedObservedPoint observed : CurveFitter.this.observations) {
                DerivativeStructure vi = new DerivativeStructure(point.length, 1, this.f.value(observed.getX(), parameters));
                for (int j = 0; j < point.length; ++j) {
                    vi = vi.add(new DerivativeStructure(point.length, 1, j, 0.0));
                }
                values[i++] = vi;
            }
            return values;
        }
    }
}
