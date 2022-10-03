package org.apache.commons.math3.optim.nonlinear.scalar;

import org.apache.commons.math3.util.Pair;
import java.util.Comparator;
import java.util.Collections;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.exception.NullArgumentException;
import java.util.ArrayList;
import org.apache.commons.math3.optim.BaseMultivariateOptimizer;
import org.apache.commons.math3.random.RandomVectorGenerator;
import java.util.List;
import org.apache.commons.math3.optim.PointValuePair;
import org.apache.commons.math3.optim.BaseMultiStartMultivariateOptimizer;

public class MultiStartMultivariateOptimizer extends BaseMultiStartMultivariateOptimizer<PointValuePair>
{
    private final MultivariateOptimizer optimizer;
    private final List<PointValuePair> optima;
    
    public MultiStartMultivariateOptimizer(final MultivariateOptimizer optimizer, final int starts, final RandomVectorGenerator generator) throws NullArgumentException, NotStrictlyPositiveException {
        super(optimizer, starts, generator);
        this.optima = new ArrayList<PointValuePair>();
        this.optimizer = optimizer;
    }
    
    @Override
    public PointValuePair[] getOptima() {
        Collections.sort(this.optima, this.getPairComparator());
        return this.optima.toArray(new PointValuePair[0]);
    }
    
    @Override
    protected void store(final PointValuePair optimum) {
        this.optima.add(optimum);
    }
    
    @Override
    protected void clear() {
        this.optima.clear();
    }
    
    private Comparator<PointValuePair> getPairComparator() {
        return new Comparator<PointValuePair>() {
            public int compare(final PointValuePair o1, final PointValuePair o2) {
                if (o1 == null) {
                    return (o2 != null) ? 1 : 0;
                }
                if (o2 == null) {
                    return -1;
                }
                final double v1 = ((Pair<K, Double>)o1).getValue();
                final double v2 = ((Pair<K, Double>)o2).getValue();
                return (MultiStartMultivariateOptimizer.this.optimizer.getGoalType() == GoalType.MINIMIZE) ? Double.compare(v1, v2) : Double.compare(v2, v1);
            }
        };
    }
}
