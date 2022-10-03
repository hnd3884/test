package org.apache.commons.math3.ml.clustering;

import org.apache.commons.math3.exception.ConvergenceException;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import java.util.List;
import java.util.Collection;
import org.apache.commons.math3.ml.clustering.evaluation.SumOfClusterVariances;
import org.apache.commons.math3.ml.clustering.evaluation.ClusterEvaluator;

public class MultiKMeansPlusPlusClusterer<T extends Clusterable> extends Clusterer<T>
{
    private final KMeansPlusPlusClusterer<T> clusterer;
    private final int numTrials;
    private final ClusterEvaluator<T> evaluator;
    
    public MultiKMeansPlusPlusClusterer(final KMeansPlusPlusClusterer<T> clusterer, final int numTrials) {
        this(clusterer, numTrials, (ClusterEvaluator)new SumOfClusterVariances(clusterer.getDistanceMeasure()));
    }
    
    public MultiKMeansPlusPlusClusterer(final KMeansPlusPlusClusterer<T> clusterer, final int numTrials, final ClusterEvaluator<T> evaluator) {
        super(clusterer.getDistanceMeasure());
        this.clusterer = clusterer;
        this.numTrials = numTrials;
        this.evaluator = evaluator;
    }
    
    public KMeansPlusPlusClusterer<T> getClusterer() {
        return this.clusterer;
    }
    
    public int getNumTrials() {
        return this.numTrials;
    }
    
    public ClusterEvaluator<T> getClusterEvaluator() {
        return this.evaluator;
    }
    
    @Override
    public List<CentroidCluster<T>> cluster(final Collection<T> points) throws MathIllegalArgumentException, ConvergenceException {
        List<CentroidCluster<T>> best = null;
        double bestVarianceSum = Double.POSITIVE_INFINITY;
        for (int i = 0; i < this.numTrials; ++i) {
            final List<CentroidCluster<T>> clusters = this.clusterer.cluster(points);
            final double varianceSum = this.evaluator.score(clusters);
            if (this.evaluator.isBetterScore(varianceSum, bestVarianceSum)) {
                best = clusters;
                bestVarianceSum = varianceSum;
            }
        }
        return best;
    }
}
