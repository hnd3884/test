package org.apache.commons.math3.geometry.euclidean.twod.hull;

import org.apache.commons.math3.geometry.hull.ConvexHull;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.ConvergenceException;
import org.apache.commons.math3.util.MathUtils;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import java.util.Collection;

abstract class AbstractConvexHullGenerator2D implements ConvexHullGenerator2D
{
    private static final double DEFAULT_TOLERANCE = 1.0E-10;
    private final double tolerance;
    private final boolean includeCollinearPoints;
    
    protected AbstractConvexHullGenerator2D(final boolean includeCollinearPoints) {
        this(includeCollinearPoints, 1.0E-10);
    }
    
    protected AbstractConvexHullGenerator2D(final boolean includeCollinearPoints, final double tolerance) {
        this.includeCollinearPoints = includeCollinearPoints;
        this.tolerance = tolerance;
    }
    
    public double getTolerance() {
        return this.tolerance;
    }
    
    public boolean isIncludeCollinearPoints() {
        return this.includeCollinearPoints;
    }
    
    public ConvexHull2D generate(final Collection<Vector2D> points) throws NullArgumentException, ConvergenceException {
        MathUtils.checkNotNull(points);
        Collection<Vector2D> hullVertices = null;
        if (points.size() < 2) {
            hullVertices = points;
        }
        else {
            hullVertices = this.findHullVertices(points);
        }
        try {
            return new ConvexHull2D(hullVertices.toArray(new Vector2D[hullVertices.size()]), this.tolerance);
        }
        catch (final MathIllegalArgumentException e) {
            throw new ConvergenceException();
        }
    }
    
    protected abstract Collection<Vector2D> findHullVertices(final Collection<Vector2D> p0);
}
