package org.apache.commons.math3.stat.clustering;

import java.util.Arrays;
import org.apache.commons.math3.util.MathArrays;
import java.util.Iterator;
import java.util.Collection;
import java.io.Serializable;

@Deprecated
public class EuclideanDoublePoint implements Clusterable<EuclideanDoublePoint>, Serializable
{
    private static final long serialVersionUID = 8026472786091227632L;
    private final double[] point;
    
    public EuclideanDoublePoint(final double[] point) {
        this.point = point;
    }
    
    public EuclideanDoublePoint centroidOf(final Collection<EuclideanDoublePoint> points) {
        final double[] centroid = new double[this.getPoint().length];
        for (final EuclideanDoublePoint p : points) {
            for (int i = 0; i < centroid.length; ++i) {
                final double[] array = centroid;
                final int n = i;
                array[n] += p.getPoint()[i];
            }
        }
        for (int j = 0; j < centroid.length; ++j) {
            final double[] array2 = centroid;
            final int n2 = j;
            array2[n2] /= points.size();
        }
        return new EuclideanDoublePoint(centroid);
    }
    
    public double distanceFrom(final EuclideanDoublePoint p) {
        return MathArrays.distance(this.point, p.getPoint());
    }
    
    @Override
    public boolean equals(final Object other) {
        return other instanceof EuclideanDoublePoint && Arrays.equals(this.point, ((EuclideanDoublePoint)other).point);
    }
    
    public double[] getPoint() {
        return this.point;
    }
    
    @Override
    public int hashCode() {
        return Arrays.hashCode(this.point);
    }
    
    @Override
    public String toString() {
        return Arrays.toString(this.point);
    }
}
