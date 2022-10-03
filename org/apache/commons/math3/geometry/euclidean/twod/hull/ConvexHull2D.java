package org.apache.commons.math3.geometry.euclidean.twod.hull;

import org.apache.commons.math3.geometry.Point;
import org.apache.commons.math3.geometry.partitioning.Hyperplane;
import org.apache.commons.math3.geometry.partitioning.RegionFactory;
import org.apache.commons.math3.exception.InsufficientDataException;
import org.apache.commons.math3.geometry.partitioning.Region;
import org.apache.commons.math3.geometry.euclidean.twod.Line;
import org.apache.commons.math3.util.Precision;
import org.apache.commons.math3.util.MathArrays;
import org.apache.commons.math3.geometry.Vector;
import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.geometry.euclidean.twod.Segment;
import java.io.Serializable;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.apache.commons.math3.geometry.euclidean.twod.Euclidean2D;
import org.apache.commons.math3.geometry.hull.ConvexHull;

public class ConvexHull2D implements ConvexHull<Euclidean2D, Vector2D>, Serializable
{
    private static final long serialVersionUID = 20140129L;
    private final Vector2D[] vertices;
    private final double tolerance;
    private transient Segment[] lineSegments;
    
    public ConvexHull2D(final Vector2D[] vertices, final double tolerance) throws MathIllegalArgumentException {
        this.tolerance = tolerance;
        if (!this.isConvex(vertices)) {
            throw new MathIllegalArgumentException(LocalizedFormats.NOT_CONVEX, new Object[0]);
        }
        this.vertices = vertices.clone();
    }
    
    private boolean isConvex(final Vector2D[] hullVertices) {
        if (hullVertices.length < 3) {
            return true;
        }
        int sign = 0;
        for (int i = 0; i < hullVertices.length; ++i) {
            final Vector2D p1 = hullVertices[(i == 0) ? (hullVertices.length - 1) : (i - 1)];
            final Vector2D p2 = hullVertices[i];
            final Vector2D p3 = hullVertices[(i == hullVertices.length - 1) ? 0 : (i + 1)];
            final Vector2D d1 = p2.subtract((Vector<Euclidean2D>)p1);
            final Vector2D d2 = p3.subtract((Vector<Euclidean2D>)p2);
            final double crossProduct = MathArrays.linearCombination(d1.getX(), d2.getY(), -d1.getY(), d2.getX());
            final int cmp = Precision.compareTo(crossProduct, 0.0, this.tolerance);
            if (cmp != 0.0) {
                if (sign != 0.0 && cmp != sign) {
                    return false;
                }
                sign = cmp;
            }
        }
        return true;
    }
    
    public Vector2D[] getVertices() {
        return this.vertices.clone();
    }
    
    public Segment[] getLineSegments() {
        return this.retrieveLineSegments().clone();
    }
    
    private Segment[] retrieveLineSegments() {
        if (this.lineSegments == null) {
            final int size = this.vertices.length;
            if (size <= 1) {
                this.lineSegments = new Segment[0];
            }
            else if (size == 2) {
                this.lineSegments = new Segment[1];
                final Vector2D p1 = this.vertices[0];
                final Vector2D p2 = this.vertices[1];
                this.lineSegments[0] = new Segment(p1, p2, new Line(p1, p2, this.tolerance));
            }
            else {
                this.lineSegments = new Segment[size];
                Vector2D firstPoint = null;
                Vector2D lastPoint = null;
                int index = 0;
                for (final Vector2D point : this.vertices) {
                    if (lastPoint == null) {
                        firstPoint = point;
                        lastPoint = point;
                    }
                    else {
                        this.lineSegments[index++] = new Segment(lastPoint, point, new Line(lastPoint, point, this.tolerance));
                        lastPoint = point;
                    }
                }
                this.lineSegments[index] = new Segment(lastPoint, firstPoint, new Line(lastPoint, firstPoint, this.tolerance));
            }
        }
        return this.lineSegments;
    }
    
    public Region<Euclidean2D> createRegion() throws InsufficientDataException {
        if (this.vertices.length < 3) {
            throw new InsufficientDataException();
        }
        final RegionFactory<Euclidean2D> factory = new RegionFactory<Euclidean2D>();
        final Segment[] segments = this.retrieveLineSegments();
        final Line[] lineArray = new Line[segments.length];
        for (int i = 0; i < segments.length; ++i) {
            lineArray[i] = segments[i].getLine();
        }
        return factory.buildConvex((Hyperplane<Euclidean2D>[])lineArray);
    }
}
