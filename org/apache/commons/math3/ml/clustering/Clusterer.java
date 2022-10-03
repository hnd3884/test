package org.apache.commons.math3.ml.clustering;

import org.apache.commons.math3.exception.ConvergenceException;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import java.util.List;
import java.util.Collection;
import org.apache.commons.math3.ml.distance.DistanceMeasure;

public abstract class Clusterer<T extends Clusterable>
{
    private DistanceMeasure measure;
    
    protected Clusterer(final DistanceMeasure measure) {
        this.measure = measure;
    }
    
    public abstract List<? extends Cluster<T>> cluster(final Collection<T> p0) throws MathIllegalArgumentException, ConvergenceException;
    
    public DistanceMeasure getDistanceMeasure() {
        return this.measure;
    }
    
    protected double distance(final Clusterable p1, final Clusterable p2) {
        return this.measure.compute(p1.getPoint(), p2.getPoint());
    }
}
