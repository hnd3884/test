package org.apache.commons.math3.geometry.euclidean.twod;

import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.fraction.BigFraction;
import org.apache.commons.math3.geometry.Vector;
import org.apache.commons.math3.geometry.enclosing.EnclosingBall;
import java.util.List;
import org.apache.commons.math3.geometry.enclosing.SupportBallGenerator;

public class DiskGenerator implements SupportBallGenerator<Euclidean2D, Vector2D>
{
    public EnclosingBall<Euclidean2D, Vector2D> ballOnSupport(final List<Vector2D> support) {
        if (support.size() < 1) {
            return new EnclosingBall<Euclidean2D, Vector2D>(Vector2D.ZERO, Double.NEGATIVE_INFINITY, new Vector2D[0]);
        }
        final Vector2D vA = support.get(0);
        if (support.size() < 2) {
            return new EnclosingBall<Euclidean2D, Vector2D>(vA, 0.0, new Vector2D[] { vA });
        }
        final Vector2D vB = support.get(1);
        if (support.size() < 3) {
            return new EnclosingBall<Euclidean2D, Vector2D>(new Vector2D(0.5, vA, 0.5, vB), 0.5 * vA.distance(vB), new Vector2D[] { vA, vB });
        }
        final Vector2D vC = support.get(2);
        final BigFraction[] c2 = { new BigFraction(vA.getX()), new BigFraction(vB.getX()), new BigFraction(vC.getX()) };
        final BigFraction[] c3 = { new BigFraction(vA.getY()), new BigFraction(vB.getY()), new BigFraction(vC.getY()) };
        final BigFraction[] c4 = { c2[0].multiply(c2[0]).add(c3[0].multiply(c3[0])), c2[1].multiply(c2[1]).add(c3[1].multiply(c3[1])), c2[2].multiply(c2[2]).add(c3[2].multiply(c3[2])) };
        final BigFraction twoM11 = this.minor(c2, c3).multiply(2);
        final BigFraction m12 = this.minor(c4, c3);
        final BigFraction m13 = this.minor(c4, c2);
        final BigFraction centerX = m12.divide(twoM11);
        final BigFraction centerY = m13.divide(twoM11).negate();
        final BigFraction dx = c2[0].subtract(centerX);
        final BigFraction dy = c3[0].subtract(centerY);
        final BigFraction r2 = dx.multiply(dx).add(dy.multiply(dy));
        return new EnclosingBall<Euclidean2D, Vector2D>(new Vector2D(centerX.doubleValue(), centerY.doubleValue()), FastMath.sqrt(r2.doubleValue()), new Vector2D[] { vA, vB, vC });
    }
    
    private BigFraction minor(final BigFraction[] c1, final BigFraction[] c2) {
        return c2[0].multiply(c1[2].subtract(c1[1])).add(c2[1].multiply(c1[0].subtract(c1[2]))).add(c2[2].multiply(c1[1].subtract(c1[0])));
    }
}
