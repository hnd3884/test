package org.apache.commons.math3.geometry.euclidean.twod.hull;

import org.apache.commons.math3.exception.ConvergenceException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.geometry.euclidean.twod.Line;
import org.apache.commons.math3.geometry.euclidean.twod.Euclidean2D;
import org.apache.commons.math3.geometry.Vector;
import java.util.Iterator;
import java.util.List;
import java.util.Collections;
import org.apache.commons.math3.util.Precision;
import java.util.Comparator;
import java.util.ArrayList;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import java.util.Collection;

public class MonotoneChain extends AbstractConvexHullGenerator2D
{
    public MonotoneChain() {
        this(false);
    }
    
    public MonotoneChain(final boolean includeCollinearPoints) {
        super(includeCollinearPoints);
    }
    
    public MonotoneChain(final boolean includeCollinearPoints, final double tolerance) {
        super(includeCollinearPoints, tolerance);
    }
    
    public Collection<Vector2D> findHullVertices(final Collection<Vector2D> points) {
        final List<Vector2D> pointsSortedByXAxis = new ArrayList<Vector2D>(points);
        Collections.sort(pointsSortedByXAxis, new Comparator<Vector2D>() {
            public int compare(final Vector2D o1, final Vector2D o2) {
                final double tolerance = MonotoneChain.this.getTolerance();
                final int diff = Precision.compareTo(o1.getX(), o2.getX(), tolerance);
                if (diff == 0) {
                    return Precision.compareTo(o1.getY(), o2.getY(), tolerance);
                }
                return diff;
            }
        });
        final List<Vector2D> lowerHull = new ArrayList<Vector2D>();
        for (final Vector2D p : pointsSortedByXAxis) {
            this.updateHull(p, lowerHull);
        }
        final List<Vector2D> upperHull = new ArrayList<Vector2D>();
        for (int idx = pointsSortedByXAxis.size() - 1; idx >= 0; --idx) {
            final Vector2D p2 = pointsSortedByXAxis.get(idx);
            this.updateHull(p2, upperHull);
        }
        final List<Vector2D> hullVertices = new ArrayList<Vector2D>(lowerHull.size() + upperHull.size() - 2);
        for (int idx2 = 0; idx2 < lowerHull.size() - 1; ++idx2) {
            hullVertices.add(lowerHull.get(idx2));
        }
        for (int idx2 = 0; idx2 < upperHull.size() - 1; ++idx2) {
            hullVertices.add(upperHull.get(idx2));
        }
        if (hullVertices.isEmpty() && !lowerHull.isEmpty()) {
            hullVertices.add(lowerHull.get(0));
        }
        return hullVertices;
    }
    
    private void updateHull(final Vector2D point, final List<Vector2D> hull) {
        final double tolerance = this.getTolerance();
        if (hull.size() == 1) {
            final Vector2D p1 = hull.get(0);
            if (p1.distance(point) < tolerance) {
                return;
            }
        }
        while (hull.size() >= 2) {
            final int size = hull.size();
            final Vector2D p2 = hull.get(size - 2);
            final Vector2D p3 = hull.get(size - 1);
            final double offset = new Line(p2, p3, tolerance).getOffset(point);
            if (FastMath.abs(offset) < tolerance) {
                final double distanceToCurrent = p2.distance(point);
                if (distanceToCurrent < tolerance || p3.distance(point) < tolerance) {
                    return;
                }
                final double distanceToLast = p2.distance(p3);
                if (this.isIncludeCollinearPoints()) {
                    final int index = (distanceToCurrent < distanceToLast) ? (size - 1) : size;
                    hull.add(index, point);
                }
                else if (distanceToCurrent > distanceToLast) {
                    hull.remove(size - 1);
                    hull.add(point);
                }
                return;
            }
            else {
                if (offset <= 0.0) {
                    break;
                }
                hull.remove(size - 1);
            }
        }
        hull.add(point);
    }
}
