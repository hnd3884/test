package org.apache.commons.math3.geometry.spherical.oned;

import org.apache.commons.math3.geometry.Space;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.MathUtils;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.apache.commons.math3.geometry.Point;

public class S1Point implements Point<Sphere1D>
{
    public static final S1Point NaN;
    private static final long serialVersionUID = 20131218L;
    private final double alpha;
    private final Vector2D vector;
    
    public S1Point(final double alpha) {
        this(MathUtils.normalizeAngle(alpha, 3.141592653589793), new Vector2D(FastMath.cos(alpha), FastMath.sin(alpha)));
    }
    
    private S1Point(final double alpha, final Vector2D vector) {
        this.alpha = alpha;
        this.vector = vector;
    }
    
    public double getAlpha() {
        return this.alpha;
    }
    
    public Vector2D getVector() {
        return this.vector;
    }
    
    public Space getSpace() {
        return Sphere1D.getInstance();
    }
    
    public boolean isNaN() {
        return Double.isNaN(this.alpha);
    }
    
    public double distance(final Point<Sphere1D> point) {
        return distance(this, (S1Point)point);
    }
    
    public static double distance(final S1Point p1, final S1Point p2) {
        return Vector2D.angle(p1.vector, p2.vector);
    }
    
    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof S1Point)) {
            return false;
        }
        final S1Point rhs = (S1Point)other;
        if (rhs.isNaN()) {
            return this.isNaN();
        }
        return this.alpha == rhs.alpha;
    }
    
    @Override
    public int hashCode() {
        if (this.isNaN()) {
            return 542;
        }
        return 1759 * MathUtils.hash(this.alpha);
    }
    
    static {
        NaN = new S1Point(Double.NaN, Vector2D.NaN);
    }
}
