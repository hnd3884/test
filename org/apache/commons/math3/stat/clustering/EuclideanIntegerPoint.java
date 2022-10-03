package org.apache.commons.math3.stat.clustering;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Collection;
import org.apache.commons.math3.util.MathArrays;
import java.io.Serializable;

@Deprecated
public class EuclideanIntegerPoint implements Clusterable<EuclideanIntegerPoint>, Serializable
{
    private static final long serialVersionUID = 3946024775784901369L;
    private final int[] point;
    
    public EuclideanIntegerPoint(final int[] point) {
        this.point = point;
    }
    
    public int[] getPoint() {
        return this.point;
    }
    
    public double distanceFrom(final EuclideanIntegerPoint p) {
        return MathArrays.distance(this.point, p.getPoint());
    }
    
    public EuclideanIntegerPoint centroidOf(final Collection<EuclideanIntegerPoint> points) {
        final int[] centroid = new int[this.getPoint().length];
        for (final EuclideanIntegerPoint p : points) {
            for (int i = 0; i < centroid.length; ++i) {
                final int[] array = centroid;
                final int n = i;
                array[n] += p.getPoint()[i];
            }
        }
        for (int j = 0; j < centroid.length; ++j) {
            final int[] array2 = centroid;
            final int n2 = j;
            array2[n2] /= points.size();
        }
        return new EuclideanIntegerPoint(centroid);
    }
    
    @Override
    public boolean equals(final Object other) {
        return other instanceof EuclideanIntegerPoint && Arrays.equals(this.point, ((EuclideanIntegerPoint)other).point);
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
