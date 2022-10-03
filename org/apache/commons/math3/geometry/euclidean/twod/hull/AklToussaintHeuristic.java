package org.apache.commons.math3.geometry.euclidean.twod.hull;

import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import java.util.Collection;

public final class AklToussaintHeuristic
{
    private AklToussaintHeuristic() {
    }
    
    public static Collection<Vector2D> reducePoints(final Collection<Vector2D> points) {
        int size = 0;
        Vector2D minX = null;
        Vector2D maxX = null;
        Vector2D minY = null;
        Vector2D maxY = null;
        for (final Vector2D p : points) {
            if (minX == null || p.getX() < minX.getX()) {
                minX = p;
            }
            if (maxX == null || p.getX() > maxX.getX()) {
                maxX = p;
            }
            if (minY == null || p.getY() < minY.getY()) {
                minY = p;
            }
            if (maxY == null || p.getY() > maxY.getY()) {
                maxY = p;
            }
            ++size;
        }
        if (size < 4) {
            return points;
        }
        final List<Vector2D> quadrilateral = buildQuadrilateral(minY, maxX, maxY, minX);
        if (quadrilateral.size() < 3) {
            return points;
        }
        final List<Vector2D> reducedPoints = new ArrayList<Vector2D>(quadrilateral);
        for (final Vector2D p2 : points) {
            if (!insideQuadrilateral(p2, quadrilateral)) {
                reducedPoints.add(p2);
            }
        }
        return reducedPoints;
    }
    
    private static List<Vector2D> buildQuadrilateral(final Vector2D... points) {
        final List<Vector2D> quadrilateral = new ArrayList<Vector2D>();
        for (final Vector2D p : points) {
            if (!quadrilateral.contains(p)) {
                quadrilateral.add(p);
            }
        }
        return quadrilateral;
    }
    
    private static boolean insideQuadrilateral(final Vector2D point, final List<Vector2D> quadrilateralPoints) {
        Vector2D p1 = quadrilateralPoints.get(0);
        Vector2D p2 = quadrilateralPoints.get(1);
        if (point.equals(p1) || point.equals(p2)) {
            return true;
        }
        final double last = point.crossProduct(p1, p2);
        for (int size = quadrilateralPoints.size(), i = 1; i < size; ++i) {
            p1 = p2;
            p2 = quadrilateralPoints.get((i + 1 == size) ? 0 : (i + 1));
            if (point.equals(p1) || point.equals(p2)) {
                return true;
            }
            if (last * point.crossProduct(p1, p2) < 0.0) {
                return false;
            }
        }
        return true;
    }
}
