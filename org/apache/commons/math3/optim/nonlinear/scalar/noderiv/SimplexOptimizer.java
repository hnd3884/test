package org.apache.commons.math3.optim.nonlinear.scalar.noderiv;

import org.apache.commons.math3.util.Pair;
import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.MathUnsupportedOperationException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.exception.NullArgumentException;
import java.util.Comparator;
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;
import org.apache.commons.math3.analysis.MultivariateFunction;
import org.apache.commons.math3.optim.OptimizationData;
import org.apache.commons.math3.optim.SimpleValueChecker;
import org.apache.commons.math3.optim.PointValuePair;
import org.apache.commons.math3.optim.ConvergenceChecker;
import org.apache.commons.math3.optim.nonlinear.scalar.MultivariateOptimizer;

public class SimplexOptimizer extends MultivariateOptimizer
{
    private AbstractSimplex simplex;
    
    public SimplexOptimizer(final ConvergenceChecker<PointValuePair> checker) {
        super(checker);
    }
    
    public SimplexOptimizer(final double rel, final double abs) {
        this(new SimpleValueChecker(rel, abs));
    }
    
    @Override
    public PointValuePair optimize(final OptimizationData... optData) {
        return super.optimize(optData);
    }
    
    @Override
    protected PointValuePair doOptimize() {
        this.checkParameters();
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
        final int iteration = 0;
        final ConvergenceChecker<PointValuePair> checker = this.getConvergenceChecker();
        while (true) {
            if (this.getIterations() > 0) {
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
            this.incrementIterationCount();
        }
        return this.simplex.getPoint(0);
    }
    
    @Override
    protected void parseOptimizationData(final OptimizationData... optData) {
        super.parseOptimizationData(optData);
        for (final OptimizationData data : optData) {
            if (data instanceof AbstractSimplex) {
                this.simplex = (AbstractSimplex)data;
                break;
            }
        }
    }
    
    private void checkParameters() {
        if (this.simplex == null) {
            throw new NullArgumentException();
        }
        if (this.getLowerBound() != null || this.getUpperBound() != null) {
            throw new MathUnsupportedOperationException(LocalizedFormats.CONSTRAINT, new Object[0]);
        }
    }
}
