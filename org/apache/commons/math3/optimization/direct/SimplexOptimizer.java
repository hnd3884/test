package org.apache.commons.math3.optimization.direct;

import org.apache.commons.math3.util.Pair;
import java.util.Comparator;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.optimization.GoalType;
import org.apache.commons.math3.optimization.OptimizationData;
import org.apache.commons.math3.optimization.PointValuePair;
import org.apache.commons.math3.optimization.ConvergenceChecker;
import org.apache.commons.math3.optimization.SimpleValueChecker;
import org.apache.commons.math3.optimization.MultivariateOptimizer;
import org.apache.commons.math3.analysis.MultivariateFunction;

@Deprecated
public class SimplexOptimizer extends BaseAbstractMultivariateOptimizer<MultivariateFunction> implements MultivariateOptimizer
{
    private AbstractSimplex simplex;
    
    @Deprecated
    public SimplexOptimizer() {
        this(new SimpleValueChecker());
    }
    
    public SimplexOptimizer(final ConvergenceChecker<PointValuePair> checker) {
        super(checker);
    }
    
    public SimplexOptimizer(final double rel, final double abs) {
        this(new SimpleValueChecker(rel, abs));
    }
    
    @Deprecated
    public void setSimplex(final AbstractSimplex simplex) {
        this.parseOptimizationData(simplex);
    }
    
    @Override
    protected PointValuePair optimizeInternal(final int maxEval, final MultivariateFunction f, final GoalType goalType, final OptimizationData... optData) {
        this.parseOptimizationData(optData);
        return super.optimizeInternal(maxEval, f, goalType, optData);
    }
    
    private void parseOptimizationData(final OptimizationData... optData) {
        for (final OptimizationData data : optData) {
            if (data instanceof AbstractSimplex) {
                this.simplex = (AbstractSimplex)data;
            }
        }
    }
    
    @Override
    protected PointValuePair doOptimize() {
        if (this.simplex == null) {
            throw new NullArgumentException();
        }
        final MultivariateFunction evalFunc = new MultivariateFunction() {
            public double value(final double[] point) {
                return SimplexOptimizer.this.computeObjectiveValue(point);
            }
        };
        final boolean isMinim = this.getGoalType() == GoalType.MINIMIZE;
        final Comparator<PointValuePair> comparator = new Comparator<PointValuePair>() {
            public int compare(final PointValuePair o1, final PointValuePair o2) {
                final double v1 = ((Pair<K, Double>)o1).getValue();
                final double v2 = ((Pair<K, Double>)o2).getValue();
                return isMinim ? Double.compare(v1, v2) : Double.compare(v2, v1);
            }
        };
        this.simplex.build(this.getStartPoint());
        this.simplex.evaluate(evalFunc, comparator);
        PointValuePair[] previous = null;
        int iteration = 0;
        final ConvergenceChecker<PointValuePair> checker = this.getConvergenceChecker();
        while (true) {
            if (iteration > 0) {
                boolean converged = true;
                for (int i = 0; i < this.simplex.getSize(); ++i) {
                    final PointValuePair prev = previous[i];
                    converged = (converged && checker.converged(iteration, prev, this.simplex.getPoint(i)));
                }
                if (converged) {
                    break;
                }
            }
            previous = this.simplex.getPoints();
            this.simplex.iterate(evalFunc, comparator);
            ++iteration;
        }
        return this.simplex.getPoint(0);
    }
}
