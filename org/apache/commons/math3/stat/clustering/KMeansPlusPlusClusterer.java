package org.apache.commons.math3.stat.clustering;

import java.util.Collections;
import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import java.util.ArrayList;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.util.MathUtils;
import org.apache.commons.math3.exception.ConvergenceException;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import java.util.Iterator;
import org.apache.commons.math3.stat.descriptive.moment.Variance;
import java.util.List;
import java.util.Collection;
import java.util.Random;

@Deprecated
public class KMeansPlusPlusClusterer<T extends Clusterable<T>>
{
    private final Random random;
    private final EmptyClusterStrategy emptyStrategy;
    
    public KMeansPlusPlusClusterer(final Random random) {
        this(random, EmptyClusterStrategy.LARGEST_VARIANCE);
    }
    
    public KMeansPlusPlusClusterer(final Random random, final EmptyClusterStrategy emptyStrategy) {
        this.random = random;
        this.emptyStrategy = emptyStrategy;
    }
    
    public List<Cluster<T>> cluster(final Collection<T> points, final int k, final int numTrials, final int maxIterationsPerTrial) throws MathIllegalArgumentException, ConvergenceException {
        List<Cluster<T>> best = null;
        double bestVarianceSum = Double.POSITIVE_INFINITY;
        for (int i = 0; i < numTrials; ++i) {
            final List<Cluster<T>> clusters = this.cluster(points, k, maxIterationsPerTrial);
            double varianceSum = 0.0;
            for (final Cluster<T> cluster : clusters) {
                if (!cluster.getPoints().isEmpty()) {
                    final T center = cluster.getCenter();
                    final Variance stat = new Variance();
                    for (final T point : cluster.getPoints()) {
                        stat.increment(point.distanceFrom(center));
                    }
                    varianceSum += stat.getResult();
                }
            }
            if (varianceSum <= bestVarianceSum) {
                best = clusters;
                bestVarianceSum = varianceSum;
            }
        }
        return best;
    }
    
    public List<Cluster<T>> cluster(final Collection<T> points, final int k, final int maxIterations) throws MathIllegalArgumentException, ConvergenceException {
        MathUtils.checkNotNull(points);
        if (points.size() < k) {
            throw new NumberIsTooSmallException(points.size(), k, false);
        }
        List<Cluster<T>> clusters = chooseInitialCenters(points, k, this.random);
        final int[] assignments = new int[points.size()];
        assignPointsToClusters(clusters, points, assignments);
        for (int max = (maxIterations < 0) ? Integer.MAX_VALUE : maxIterations, count = 0; count < max; ++count) {
            boolean emptyCluster = false;
            final List<Cluster<T>> newClusters = new ArrayList<Cluster<T>>();
            for (final Cluster<T> cluster : clusters) {
                T newCenter = null;
                if (cluster.getPoints().isEmpty()) {
                    switch (this.emptyStrategy) {
                        case LARGEST_VARIANCE: {
                            newCenter = this.getPointFromLargestVarianceCluster(clusters);
                            break;
                        }
                        case LARGEST_POINTS_NUMBER: {
                            newCenter = this.getPointFromLargestNumberCluster(clusters);
                            break;
                        }
                        case FARTHEST_POINT: {
                            newCenter = this.getFarthestPoint(clusters);
                            break;
                        }
                        default: {
                            throw new ConvergenceException(LocalizedFormats.EMPTY_CLUSTER_IN_K_MEANS, new Object[0]);
                        }
                    }
                    emptyCluster = true;
                }
                else {
                    newCenter = cluster.getCenter().centroidOf(cluster.getPoints());
                }
                newClusters.add(new Cluster<T>(newCenter));
            }
            final int changes = assignPointsToClusters(newClusters, points, assignments);
            clusters = newClusters;
            if (changes == 0 && !emptyCluster) {
                return clusters;
            }
        }
        return clusters;
    }
    
    private static <T extends Clusterable<T>> int assignPointsToClusters(final List<Cluster<T>> clusters, final Collection<T> points, final int[] assignments) {
        int assignedDifferently = 0;
        int pointIndex = 0;
        for (final T p : points) {
            final int clusterIndex = getNearestCluster(clusters, p);
            if (clusterIndex != assignments[pointIndex]) {
                ++assignedDifferently;
            }
            final Cluster<T> cluster = clusters.get(clusterIndex);
            cluster.addPoint(p);
            assignments[pointIndex++] = clusterIndex;
        }
        return assignedDifferently;
    }
    
    private static <T extends Clusterable<T>> List<Cluster<T>> chooseInitialCenters(final Collection<T> points, final int k, final Random random) {
        final List<T> pointList = Collections.unmodifiableList((List<? extends T>)new ArrayList<T>((Collection<? extends T>)points));
        final int numPoints = pointList.size();
        final boolean[] taken = new boolean[numPoints];
        final List<Cluster<T>> resultSet = new ArrayList<Cluster<T>>();
        final int firstPointIndex = random.nextInt(numPoints);
        final T firstPoint = pointList.get(firstPointIndex);
        resultSet.add(new Cluster<T>(firstPoint));
        taken[firstPointIndex] = true;
        final double[] minDistSquared = new double[numPoints];
        for (int i = 0; i < numPoints; ++i) {
            if (i != firstPointIndex) {
                final double d = firstPoint.distanceFrom(pointList.get(i));
                minDistSquared[i] = d * d;
            }
        }
        while (resultSet.size() < k) {
            double distSqSum = 0.0;
            for (int j = 0; j < numPoints; ++j) {
                if (!taken[j]) {
                    distSqSum += minDistSquared[j];
                }
            }
            final double r = random.nextDouble() * distSqSum;
            int nextPointIndex = -1;
            double sum = 0.0;
            for (int l = 0; l < numPoints; ++l) {
                if (!taken[l]) {
                    sum += minDistSquared[l];
                    if (sum >= r) {
                        nextPointIndex = l;
                        break;
                    }
                }
            }
            if (nextPointIndex == -1) {
                for (int l = numPoints - 1; l >= 0; --l) {
                    if (!taken[l]) {
                        nextPointIndex = l;
                        break;
                    }
                }
            }
            if (nextPointIndex < 0) {
                break;
            }
            final T p = pointList.get(nextPointIndex);
            resultSet.add(new Cluster<T>(p));
            taken[nextPointIndex] = true;
            if (resultSet.size() >= k) {
                continue;
            }
            for (int m = 0; m < numPoints; ++m) {
                if (!taken[m]) {
                    final double d2 = p.distanceFrom(pointList.get(m));
                    final double d3 = d2 * d2;
                    if (d3 < minDistSquared[m]) {
                        minDistSquared[m] = d3;
                    }
                }
            }
        }
        return resultSet;
    }
    
    private T getPointFromLargestVarianceCluster(final Collection<Cluster<T>> clusters) throws ConvergenceException {
        double maxVariance = Double.NEGATIVE_INFINITY;
        Cluster<T> selected = null;
        for (final Cluster<T> cluster : clusters) {
            if (!cluster.getPoints().isEmpty()) {
                final T center = cluster.getCenter();
                final Variance stat = new Variance();
                for (final T point : cluster.getPoints()) {
                    stat.increment(point.distanceFrom(center));
                }
                final double variance = stat.getResult();
                if (variance <= maxVariance) {
                    continue;
                }
                maxVariance = variance;
                selected = cluster;
            }
        }
        if (selected == null) {
            throw new ConvergenceException(LocalizedFormats.EMPTY_CLUSTER_IN_K_MEANS, new Object[0]);
        }
        final List<T> selectedPoints = selected.getPoints();
        return selectedPoints.remove(this.random.nextInt(selectedPoints.size()));
    }
    
    private T getPointFromLargestNumberCluster(final Collection<Cluster<T>> clusters) throws ConvergenceException {
        int maxNumber = 0;
        Cluster<T> selected = null;
        for (final Cluster<T> cluster : clusters) {
            final int number = cluster.getPoints().size();
            if (number > maxNumber) {
                maxNumber = number;
                selected = cluster;
            }
        }
        if (selected == null) {
            throw new ConvergenceException(LocalizedFormats.EMPTY_CLUSTER_IN_K_MEANS, new Object[0]);
        }
        final List<T> selectedPoints = selected.getPoints();
        return selectedPoints.remove(this.random.nextInt(selectedPoints.size()));
    }
    
    private T getFarthestPoint(final Collection<Cluster<T>> clusters) throws ConvergenceException {
        double maxDistance = Double.NEGATIVE_INFINITY;
        Cluster<T> selectedCluster = null;
        int selectedPoint = -1;
        for (final Cluster<T> cluster : clusters) {
            final T center = cluster.getCenter();
            final List<T> points = cluster.getPoints();
            for (int i = 0; i < points.size(); ++i) {
                final double distance = points.get(i).distanceFrom(center);
                if (distance > maxDistance) {
                    maxDistance = distance;
                    selectedCluster = cluster;
                    selectedPoint = i;
                }
            }
        }
        if (selectedCluster == null) {
            throw new ConvergenceException(LocalizedFormats.EMPTY_CLUSTER_IN_K_MEANS, new Object[0]);
        }
        return selectedCluster.getPoints().remove(selectedPoint);
    }
    
    private static <T extends Clusterable<T>> int getNearestCluster(final Collection<Cluster<T>> clusters, final T point) {
        double minDistance = Double.MAX_VALUE;
        int clusterIndex = 0;
        int minCluster = 0;
        for (final Cluster<T> c : clusters) {
            final double distance = point.distanceFrom(c.getCenter());
            if (distance < minDistance) {
                minDistance = distance;
                minCluster = clusterIndex;
            }
            ++clusterIndex;
        }
        return minCluster;
    }
    
    public enum EmptyClusterStrategy
    {
        LARGEST_VARIANCE, 
        LARGEST_POINTS_NUMBER, 
        FARTHEST_POINT, 
        ERROR;
    }
}
