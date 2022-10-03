package org.apache.commons.math3.geometry.euclidean.threed;

import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.fraction.BigFraction;
import org.apache.commons.math3.geometry.euclidean.twod.Euclidean2D;
import java.util.Arrays;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.apache.commons.math3.geometry.euclidean.twod.DiskGenerator;
import org.apache.commons.math3.geometry.Vector;
import org.apache.commons.math3.geometry.enclosing.EnclosingBall;
import java.util.List;
import org.apache.commons.math3.geometry.enclosing.SupportBallGenerator;

public class SphereGenerator implements SupportBallGenerator<Euclidean3D, Vector3D>
{
    public EnclosingBall<Euclidean3D, Vector3D> ballOnSupport(final List<Vector3D> support) {
        if (support.size() < 1) {
            return new EnclosingBall<Euclidean3D, Vector3D>(Vector3D.ZERO, Double.NEGATIVE_INFINITY, new Vector3D[0]);
        }
        final Vector3D vA = support.get(0);
        if (support.size() < 2) {
            return new EnclosingBall<Euclidean3D, Vector3D>(vA, 0.0, new Vector3D[] { vA });
        }
        final Vector3D vB = support.get(1);
        if (support.size() < 3) {
            return new EnclosingBall<Euclidean3D, Vector3D>(new Vector3D(0.5, vA, 0.5, vB), 0.5 * vA.distance(vB), new Vector3D[] { vA, vB });
        }
        final Vector3D vC = support.get(2);
        if (support.size() < 4) {
            final Plane p = new Plane(vA, vB, vC, 1.0E-10 * (vA.getNorm1() + vB.getNorm1() + vC.getNorm1()));
            final EnclosingBall<Euclidean2D, Vector2D> disk = new DiskGenerator().ballOnSupport(Arrays.asList(p.toSubSpace(vA), p.toSubSpace(vB), p.toSubSpace(vC)));
            return new EnclosingBall<Euclidean3D, Vector3D>(p.toSpace(disk.getCenter()), disk.getRadius(), new Vector3D[] { vA, vB, vC });
        }
        final Vector3D vD = support.get(3);
        final BigFraction[] c2 = { new BigFraction(vA.getX()), new BigFraction(vB.getX()), new BigFraction(vC.getX()), new BigFraction(vD.getX()) };
        final BigFraction[] c3 = { new BigFraction(vA.getY()), new BigFraction(vB.getY()), new BigFraction(vC.getY()), new BigFraction(vD.getY()) };
        final BigFraction[] c4 = { new BigFraction(vA.getZ()), new BigFraction(vB.getZ()), new BigFraction(vC.getZ()), new BigFraction(vD.getZ()) };
        final BigFraction[] c5 = { c2[0].multiply(c2[0]).add(c3[0].multiply(c3[0])).add(c4[0].multiply(c4[0])), c2[1].multiply(c2[1]).add(c3[1].multiply(c3[1])).add(c4[1].multiply(c4[1])), c2[2].multiply(c2[2]).add(c3[2].multiply(c3[2])).add(c4[2].multiply(c4[2])), c2[3].multiply(c2[3]).add(c3[3].multiply(c3[3])).add(c4[3].multiply(c4[3])) };
        final BigFraction twoM11 = this.minor(c2, c3, c4).multiply(2);
        final BigFraction m12 = this.minor(c5, c3, c4);
        final BigFraction m13 = this.minor(c5, c2, c4);
        final BigFraction m14 = this.minor(c5, c2, c3);
        final BigFraction centerX = m12.divide(twoM11);
        final BigFraction centerY = m13.divide(twoM11).negate();
        final BigFraction centerZ = m14.divide(twoM11);
        final BigFraction dx = c2[0].subtract(centerX);
        final BigFraction dy = c3[0].subtract(centerY);
        final BigFraction dz = c4[0].subtract(centerZ);
        final BigFraction r2 = dx.multiply(dx).add(dy.multiply(dy)).add(dz.multiply(dz));
        return new EnclosingBall<Euclidean3D, Vector3D>(new Vector3D(centerX.doubleValue(), centerY.doubleValue(), centerZ.doubleValue()), FastMath.sqrt(r2.doubleValue()), new Vector3D[] { vA, vB, vC, vD });
    }
    
    private BigFraction minor(final BigFraction[] c1, final BigFraction[] c2, final BigFraction[] c3) {
        return c2[0].multiply(c3[1]).multiply(c1[2].subtract(c1[3])).add(c2[0].multiply(c3[2]).multiply(c1[3].subtract(c1[1]))).add(c2[0].multiply(c3[3]).multiply(c1[1].subtract(c1[2]))).add(c2[1].multiply(c3[0]).multiply(c1[3].subtract(c1[2]))).add(c2[1].multiply(c3[2]).multiply(c1[0].subtract(c1[3]))).add(c2[1].multiply(c3[3]).multiply(c1[2].subtract(c1[0]))).add(c2[2].multiply(c3[0]).multiply(c1[1].subtract(c1[3]))).add(c2[2].multiply(c3[1]).multiply(c1[3].subtract(c1[0]))).add(c2[2].multiply(c3[3]).multiply(c1[0].subtract(c1[1]))).add(c2[3].multiply(c3[0]).multiply(c1[2].subtract(c1[1]))).add(c2[3].multiply(c3[1]).multiply(c1[0].subtract(c1[2]))).add(c2[3].multiply(c3[2]).multiply(c1[1].subtract(c1[0])));
    }
}
