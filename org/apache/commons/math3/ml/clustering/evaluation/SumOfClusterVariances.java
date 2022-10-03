package org.apache.commons.math3.ml.clustering.evaluation;

import java.util.Iterator;
import org.apache.commons.math3.stat.descriptive.moment.Variance;
import org.apache.commons.math3.ml.clustering.Cluster;
import java.util.List;
import org.apache.commons.math3.ml.distance.DistanceMeasure;
import org.apache.commons.math3.ml.clustering.Clusterable;

public class SumOfClusterVariances<T extends Clusterable> extends ClusterEvaluator<T>
{
    public SumOfClusterVariances(final DistanceMeasure measure) {
        super(measure);
    }
    
    @Override
    public double score(final List<? extends Cluster<T>> clusters) {
        double varianceSum = 0.0;
        for (final Cluster<T> cluster : clusters) {
            if (!cluster.getPoints().isEmpty()) {
                final Clusterable center = this.centroidOf(cluster);
                final Variance stat = new Variance();
                for (final T point : cluster.getPoints()) {
                    stat.increment(this.distance(point, center));
                }
                varianceSum += stat.getResult();
            }
        }
        return varianceSum;
    }
}
