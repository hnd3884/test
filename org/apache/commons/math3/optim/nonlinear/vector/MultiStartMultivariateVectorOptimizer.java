package org.apache.commons.math3.optim.nonlinear.vector;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import java.util.Comparator;
import java.util.Collections;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.exception.NullArgumentException;
import java.util.ArrayList;
import org.apache.commons.math3.optim.BaseMultivariateOptimizer;
import org.apache.commons.math3.random.RandomVectorGenerator;
import java.util.List;
import org.apache.commons.math3.optim.PointVectorValuePair;
import org.apache.commons.math3.optim.BaseMultiStartMultivariateOptimizer;

@Deprecated
public class MultiStartMultivariateVectorOptimizer extends BaseMultiStartMultivariateOptimizer<PointVectorValuePair>
{
    private final MultivariateVectorOptimizer optimizer;
    private final List<PointVectorValuePair> optima;
    
    public MultiStartMultivariateVectorOptimizer(final MultivariateVectorOptimizer optimizer, final int starts, final RandomVectorGenerator generator) throws NullArgumentException, NotStrictlyPositiveException {
        super(optimizer, starts, generator);
        this.optima = new ArrayList<PointVectorValuePair>();
        this.optimizer = optimizer;
    }
    
    @Override
    public PointVectorValuePair[] getOptima() {
        Collections.sort(this.optima, this.getPairComparator());
        return this.optima.toArray(new PointVectorValuePair[0]);
    }
    
    @Override
    protected void store(final PointVectorValuePair optimum) {
        this.optima.add(optimum);
    }
    
    @Override
    protected void clear() {
        this.optima.clear();
    }
    
    private Comparator<PointVectorValuePair> getPairComparator() {
        return new Comparator<PointVectorValuePair>() {
            private final RealVector target = new ArrayRealVector(MultiStartMultivariateVectorOptimizer.this.optimizer.getTarget(), false);
            private final RealMatrix weight = MultiStartMultivariateVectorOptimizer.this.optimizer.getWeight();
            
            public int compare(final PointVectorValuePair o1, final PointVectorValuePair o2) {
                if (o1 == null) {
                    return (o2 != null) ? 1 : 0;
                }
                if (o2 == null) {
                    return -1;
                }
                return Double.compare(this.weightedResidual(o1), this.weightedResidual(o2));
            }
            
            private double weightedResidual(final PointVectorValuePair pv) {
                final RealVector v = new ArrayRealVector(pv.getValueRef(), false);
                final RealVector r = this.target.subtract(v);
                return r.dotProduct(this.weight.operate(r));
            }
        };
    }
}
