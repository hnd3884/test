package org.apache.commons.math3.ml.clustering;

import java.util.Set;
import java.util.HashSet;
import org.apache.commons.math3.exception.NullArgumentException;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import org.apache.commons.math3.util.MathUtils;
import java.util.List;
import java.util.Collection;
import org.apache.commons.math3.exception.NotPositiveException;
import org.apache.commons.math3.ml.distance.DistanceMeasure;
import org.apache.commons.math3.ml.distance.EuclideanDistance;

public class DBSCANClusterer<T extends Clusterable> extends Clusterer<T>
{
    private final double eps;
    private final int minPts;
    
    public DBSCANClusterer(final double eps, final int minPts) throws NotPositiveException {
        this(eps, minPts, new EuclideanDistance());
    }
    
    public DBSCANClusterer(final double eps, final int minPts, final DistanceMeasure measure) throws NotPositiveException {
        super(measure);
        if (eps < 0.0) {
            throw new NotPositiveException(eps);
        }
        if (minPts < 0) {
            throw new NotPositiveException(minPts);
        }
        this.eps = eps;
        this.minPts = minPts;
    }
    
    public double getEps() {
        return this.eps;
    }
    
    public int getMinPts() {
        return this.minPts;
    }
    
    @Override
    public List<Cluster<T>> cluster(final Collection<T> points) throws NullArgumentException {
        MathUtils.checkNotNull(points);
        final List<Cluster<T>> clusters = new ArrayList<Cluster<T>>();
        final Map<Clusterable, PointStatus> visited = new HashMap<Clusterable, PointStatus>();
        for (final T point : points) {
            if (visited.get(point) != null) {
                continue;
            }
            final List<T> neighbors = this.getNeighbors(point, points);
            if (neighbors.size() >= this.minPts) {
                final Cluster<T> cluster = new Cluster<T>();
                clusters.add(this.expandCluster(cluster, point, neighbors, points, visited));
            }
            else {
                visited.put(point, PointStatus.NOISE);
            }
        }
        return clusters;
    }
    
    private Cluster<T> expandCluster(final Cluster<T> cluster, final T point, final List<T> neighbors, final Collection<T> points, final Map<Clusterable, PointStatus> visited) {
        cluster.addPoint(point);
        visited.put(point, PointStatus.PART_OF_CLUSTER);
        List<T> seeds = new ArrayList<T>((Collection<? extends T>)neighbors);
        for (int index = 0; index < seeds.size(); ++index) {
            final T current = seeds.get(index);
            final PointStatus pStatus = visited.get(current);
            if (pStatus == null) {
                final List<T> currentNeighbors = this.getNeighbors(current, points);
                if (currentNeighbors.size() >= this.minPts) {
                    seeds = this.merge(seeds, currentNeighbors);
                }
            }
            if (pStatus != PointStatus.PART_OF_CLUSTER) {
                visited.put(current, PointStatus.PART_OF_CLUSTER);
                cluster.addPoint(current);
            }
        }
        return cluster;
    }
    
    private List<T> getNeighbors(final T point, final Collection<T> points) {
        final List<T> neighbors = new ArrayList<T>();
        for (final T neighbor : points) {
            if (point != neighbor && this.distance(neighbor, point) <= this.eps) {
                neighbors.add(neighbor);
            }
        }
        return neighbors;
    }
    
    private List<T> merge(final List<T> one, final List<T> two) {
        final Set<T> oneSet = new HashSet<T>((Collection<? extends T>)one);
        for (final T item : two) {
            if (!oneSet.contains(item)) {
                one.add(item);
            }
        }
        return one;
    }
    
    private enum PointStatus
    {
        NOISE, 
        PART_OF_CLUSTER;
    }
}
