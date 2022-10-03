package org.apache.commons.math3.ml.clustering.evaluation;

import java.util.Iterator;
import org.apache.commons.math3.ml.clustering.DoublePoint;
import org.apache.commons.math3.ml.clustering.CentroidCluster;
import org.apache.commons.math3.ml.clustering.Cluster;
import java.util.List;
import org.apache.commons.math3.ml.distance.EuclideanDistance;
import org.apache.commons.math3.ml.distance.DistanceMeasure;
import org.apache.commons.math3.ml.clustering.Clusterable;

public abstract class ClusterEvaluator<T extends Clusterable>
{
    private final DistanceMeasure measure;
    
    public ClusterEvaluator() {
        this(new EuclideanDistance());
    }
    
    public ClusterEvaluator(final DistanceMeasure measure) {
        this.measure = measure;
    }
    
    public abstract double score(final List<? extends Cluster<T>> p0);
    
    public boolean isBetterScore(final double score1, final double score2) {
        return score1 < score2;
    }
    
    protected double distance(final Clusterable p1, final Clusterable p2) {
        return this.measure.compute(p1.getPoint(), p2.getPoint());
    }
    
    protected Clusterable centroidOf(final Cluster<T> cluster) {
        final List<T> points = cluster.getPoints();
        if (points.isEmpty()) {
            return null;
        }
        if (cluster instanceof CentroidCluster) {
            return ((CentroidCluster)cluster).getCenter();
        }
        final int dimension = points.get(0).getPoint().length;
        final double[] centroid = new double[dimension];
        for (final T p : points) {
            final double[] point = p.getPoint();
            for (int i = 0; i < centroid.length; ++i) {
                final double[] array = centroid;
                final int n = i;
                array[n] += point[i];
            }
        }
        for (int j = 0; j < centroid.length; ++j) {
            final double[] array2 = centroid;
            final int n2 = j;
            array2[n2] /= points.size();
        }
        return new DoublePoint(centroid);
    }
}
