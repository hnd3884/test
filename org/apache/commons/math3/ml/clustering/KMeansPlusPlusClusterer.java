package org.apache.commons.math3.ml.clustering;

import org.apache.commons.math3.stat.descriptive.moment.Variance;
import java.util.Collections;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import java.util.Iterator;
import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.ConvergenceException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import java.util.ArrayList;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.util.MathUtils;
import java.util.List;
import java.util.Collection;
import org.apache.commons.math3.random.JDKRandomGenerator;
import org.apache.commons.math3.ml.distance.DistanceMeasure;
import org.apache.commons.math3.ml.distance.EuclideanDistance;
import org.apache.commons.math3.random.RandomGenerator;

public class KMeansPlusPlusClusterer<T extends Clusterable> extends Clusterer<T>
{
    private final int k;
    private final int maxIterations;
    private final RandomGenerator random;
    private final EmptyClusterStrategy emptyStrategy;
    
    public KMeansPlusPlusClusterer(final int k) {
        this(k, -1);
    }
    
    public KMeansPlusPlusClusterer(final int k, final int maxIterations) {
        this(k, maxIterations, new EuclideanDistance());
    }
    
    public KMeansPlusPlusClusterer(final int k, final int maxIterations, final DistanceMeasure measure) {
        this(k, maxIterations, measure, new JDKRandomGenerator());
    }
    
    public KMeansPlusPlusClusterer(final int k, final int maxIterations, final DistanceMeasure measure, final RandomGenerator random) {
        this(k, maxIterations, measure, random, EmptyClusterStrategy.LARGEST_VARIANCE);
    }
    
    public KMeansPlusPlusClusterer(final int k, final int maxIterations, final DistanceMeasure measure, final RandomGenerator random, final EmptyClusterStrategy emptyStrategy) {
        super(measure);
        this.k = k;
        this.maxIterations = maxIterations;
        this.random = random;
        this.emptyStrategy = emptyStrategy;
    }
    
    public int getK() {
        return this.k;
    }
    
    public int getMaxIterations() {
        return this.maxIterations;
    }
    
    public RandomGenerator getRandomGenerator() {
        return this.random;
    }
    
    public EmptyClusterStrategy getEmptyClusterStrategy() {
        return this.emptyStrategy;
    }
    
    @Override
    public List<CentroidCluster<T>> cluster(final Collection<T> points) throws MathIllegalArgumentException, ConvergenceException {
        MathUtils.checkNotNull(points);
        if (points.size() < this.k) {
            throw new NumberIsTooSmallException(points.size(), this.k, false);
        }
        List<CentroidCluster<T>> clusters = this.chooseInitialCenters(points);
        final int[] assignments = new int[points.size()];
        this.assignPointsToClusters(clusters, points, assignments);
        for (int max = (this.maxIterations < 0) ? Integer.MAX_VALUE : this.maxIterations, count = 0; count < max; ++count) {
            boolean emptyCluster = false;
            final List<CentroidCluster<T>> newClusters = new ArrayList<CentroidCluster<T>>();
            for (final CentroidCluster<T> cluster : clusters) {
                Clusterable newCenter = null;
                if (cluster.getPoints().isEmpty()) {
                    switch (this.emptyStrategy) {
                        case LARGEST_VARIANCE: {
                            newCenter = this.getPointFromLargestVarianceCluster((Collection<CentroidCluster<Clusterable>>)clusters);
                            break;
                        }
                        case LARGEST_POINTS_NUMBER: {
                            newCenter = this.getPointFromLargestNumberCluster((Collection<? extends Cluster<Clusterable>>)clusters);
                            break;
                        }
                        case FARTHEST_POINT: {
                            newCenter = this.getFarthestPoint((Collection<CentroidCluster<Clusterable>>)clusters);
                            break;
                        }
                        default: {
                            throw new ConvergenceException(LocalizedFormats.EMPTY_CLUSTER_IN_K_MEANS, new Object[0]);
                        }
                    }
                    emptyCluster = true;
                }
                else {
                    newCenter = this.centroidOf(cluster.getPoints(), cluster.getCenter().getPoint().length);
                }
                newClusters.add(new CentroidCluster<T>(newCenter));
            }
            final int changes = this.assignPointsToClusters(newClusters, points, assignments);
            clusters = newClusters;
            if (changes == 0 && !emptyCluster) {
                return clusters;
            }
        }
        return clusters;
    }
    
    private int assignPointsToClusters(final List<CentroidCluster<T>> clusters, final Collection<T> points, final int[] assignments) {
        int assignedDifferently = 0;
        int pointIndex = 0;
        for (final T p : points) {
            final int clusterIndex = this.getNearestCluster(clusters, p);
            if (clusterIndex != assignments[pointIndex]) {
                ++assignedDifferently;
            }
            final CentroidCluster<T> cluster = clusters.get(clusterIndex);
            cluster.addPoint(p);
            assignments[pointIndex++] = clusterIndex;
        }
        return assignedDifferently;
    }
    
    private List<CentroidCluster<T>> chooseInitialCenters(final Collection<T> points) {
        final List<T> pointList = Collections.unmodifiableList((List<? extends T>)new ArrayList<T>((Collection<? extends T>)points));
        final int numPoints = pointList.size();
        final boolean[] taken = new boolean[numPoints];
        final List<CentroidCluster<T>> resultSet = new ArrayList<CentroidCluster<T>>();
        final int firstPointIndex = this.random.nextInt(numPoints);
        final T firstPoint = pointList.get(firstPointIndex);
        resultSet.add(new CentroidCluster<T>(firstPoint));
        taken[firstPointIndex] = true;
        final double[] minDistSquared = new double[numPoints];
        for (int i = 0; i < numPoints; ++i) {
            if (i != firstPointIndex) {
                final double d = this.distance(firstPoint, pointList.get(i));
                minDistSquared[i] = d * d;
            }
        }
        while (resultSet.size() < this.k) {
            double distSqSum = 0.0;
            for (int j = 0; j < numPoints; ++j) {
                if (!taken[j]) {
                    distSqSum += minDistSquared[j];
                }
            }
            final double r = this.random.nextDouble() * distSqSum;
            int nextPointIndex = -1;
            double sum = 0.0;
            for (int k = 0; k < numPoints; ++k) {
                if (!taken[k]) {
                    sum += minDistSquared[k];
                    if (sum >= r) {
                        nextPointIndex = k;
                        break;
                    }
                }
            }
            if (nextPointIndex == -1) {
                for (int k = numPoints - 1; k >= 0; --k) {
                    if (!taken[k]) {
                        nextPointIndex = k;
                        break;
                    }
                }
            }
            if (nextPointIndex < 0) {
                break;
            }
            final T p = pointList.get(nextPointIndex);
            resultSet.add(new CentroidCluster<T>(p));
            taken[nextPointIndex] = true;
            if (resultSet.size() >= this.k) {
                continue;
            }
            for (int l = 0; l < numPoints; ++l) {
                if (!taken[l]) {
                    final double d2 = this.distance(p, pointList.get(l));
                    final double d3 = d2 * d2;
                    if (d3 < minDistSquared[l]) {
                        minDistSquared[l] = d3;
                    }
                }
            }
        }
        return resultSet;
    }
    
    private T getPointFromLargestVarianceCluster(final Collection<CentroidCluster<T>> clusters) throws ConvergenceException {
        double maxVariance = Double.NEGATIVE_INFINITY;
        Cluster<T> selected = null;
        for (final CentroidCluster<T> cluster : clusters) {
            if (!cluster.getPoints().isEmpty()) {
                final Clusterable center = cluster.getCenter();
                final Variance stat = new Variance();
                for (final T point : cluster.getPoints()) {
                    stat.increment(this.distance(point, center));
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
    
    private T getPointFromLargestNumberCluster(final Collection<? extends Cluster<T>> clusters) throws ConvergenceException {
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
    
    private T getFarthestPoint(final Collection<CentroidCluster<T>> clusters) throws ConvergenceException {
        double maxDistance = Double.NEGATIVE_INFINITY;
        Cluster<T> selectedCluster = null;
        int selectedPoint = -1;
        for (final CentroidCluster<T> cluster : clusters) {
            final Clusterable center = cluster.getCenter();
            final List<T> points = cluster.getPoints();
            for (int i = 0; i < points.size(); ++i) {
                final double distance = this.distance(points.get(i), center);
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
    
    private int getNearestCluster(final Collection<CentroidCluster<T>> clusters, final T point) {
        double minDistance = Double.MAX_VALUE;
        int clusterIndex = 0;
        int minCluster = 0;
        for (final CentroidCluster<T> c : clusters) {
            final double distance = this.distance(point, c.getCenter());
            if (distance < minDistance) {
                minDistance = distance;
                minCluster = clusterIndex;
            }
            ++clusterIndex;
        }
        return minCluster;
    }
    
    private Clusterable centroidOf(final Collection<T> points, final int dimension) {
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
    
    public enum EmptyClusterStrategy
    {
        LARGEST_VARIANCE, 
        LARGEST_POINTS_NUMBER, 
        FARTHEST_POINT, 
        ERROR;
    }
}
