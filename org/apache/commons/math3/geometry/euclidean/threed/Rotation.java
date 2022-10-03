package org.apache.commons.math3.geometry.euclidean.threed;

import org.apache.commons.math3.exception.MathArithmeticException;
import org.apache.commons.math3.util.MathArrays;
import org.apache.commons.math3.geometry.Vector;
import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.util.FastMath;
import java.io.Serializable;

public class Rotation implements Serializable
{
    public static final Rotation IDENTITY;
    private static final long serialVersionUID = -2153622329907944313L;
    private final double q0;
    private final double q1;
    private final double q2;
    private final double q3;
    
    public Rotation(double q0, double q1, double q2, double q3, final boolean needsNormalization) {
        if (needsNormalization) {
            final double inv = 1.0 / FastMath.sqrt(q0 * q0 + q1 * q1 + q2 * q2 + q3 * q3);
            q0 *= inv;
            q1 *= inv;
            q2 *= inv;
            q3 *= inv;
        }
        this.q0 = q0;
        this.q1 = q1;
        this.q2 = q2;
        this.q3 = q3;
    }
    
    @Deprecated
    public Rotation(final Vector3D axis, final double angle) throws MathIllegalArgumentException {
        this(axis, angle, RotationConvention.VECTOR_OPERATOR);
    }
    
    public Rotation(final Vector3D axis, final double angle, final RotationConvention convention) throws MathIllegalArgumentException {
        final double norm = axis.getNorm();
        if (norm == 0.0) {
            throw new MathIllegalArgumentException(LocalizedFormats.ZERO_NORM_FOR_ROTATION_AXIS, new Object[0]);
        }
        final double halfAngle = (convention == RotationConvention.VECTOR_OPERATOR) ? (-0.5 * angle) : (0.5 * angle);
        final double coeff = FastMath.sin(halfAngle) / norm;
        this.q0 = FastMath.cos(halfAngle);
        this.q1 = coeff * axis.getX();
        this.q2 = coeff * axis.getY();
        this.q3 = coeff * axis.getZ();
    }
    
    public Rotation(final double[][] m, final double threshold) throws NotARotationMatrixException {
        if (m.length != 3 || m[0].length != 3 || m[1].length != 3 || m[2].length != 3) {
            throw new NotARotationMatrixException(LocalizedFormats.ROTATION_MATRIX_DIMENSIONS, new Object[] { m.length, m[0].length });
        }
        final double[][] ort = this.orthogonalizeMatrix(m, threshold);
        final double det = ort[0][0] * (ort[1][1] * ort[2][2] - ort[2][1] * ort[1][2]) - ort[1][0] * (ort[0][1] * ort[2][2] - ort[2][1] * ort[0][2]) + ort[2][0] * (ort[0][1] * ort[1][2] - ort[1][1] * ort[0][2]);
        if (det < 0.0) {
            throw new NotARotationMatrixException(LocalizedFormats.CLOSEST_ORTHOGONAL_MATRIX_HAS_NEGATIVE_DETERMINANT, new Object[] { det });
        }
        final double[] quat = mat2quat(ort);
        this.q0 = quat[0];
        this.q1 = quat[1];
        this.q2 = quat[2];
        this.q3 = quat[3];
    }
    
    public Rotation(Vector3D u1, Vector3D u2, Vector3D v1, Vector3D v2) throws MathArithmeticException {
        final Vector3D u3 = u1.crossProduct(u2).normalize();
        u2 = u3.crossProduct(u1).normalize();
        u1 = u1.normalize();
        final Vector3D v3 = v1.crossProduct(v2).normalize();
        v2 = v3.crossProduct(v1).normalize();
        v1 = v1.normalize();
        final double[][] m = { { MathArrays.linearCombination(u1.getX(), v1.getX(), u2.getX(), v2.getX(), u3.getX(), v3.getX()), MathArrays.linearCombination(u1.getY(), v1.getX(), u2.getY(), v2.getX(), u3.getY(), v3.getX()), MathArrays.linearCombination(u1.getZ(), v1.getX(), u2.getZ(), v2.getX(), u3.getZ(), v3.getX()) }, { MathArrays.linearCombination(u1.getX(), v1.getY(), u2.getX(), v2.getY(), u3.getX(), v3.getY()), MathArrays.linearCombination(u1.getY(), v1.getY(), u2.getY(), v2.getY(), u3.getY(), v3.getY()), MathArrays.linearCombination(u1.getZ(), v1.getY(), u2.getZ(), v2.getY(), u3.getZ(), v3.getY()) }, { MathArrays.linearCombination(u1.getX(), v1.getZ(), u2.getX(), v2.getZ(), u3.getX(), v3.getZ()), MathArrays.linearCombination(u1.getY(), v1.getZ(), u2.getY(), v2.getZ(), u3.getY(), v3.getZ()), MathArrays.linearCombination(u1.getZ(), v1.getZ(), u2.getZ(), v2.getZ(), u3.getZ(), v3.getZ()) } };
        final double[] quat = mat2quat(m);
        this.q0 = quat[0];
        this.q1 = quat[1];
        this.q2 = quat[2];
        this.q3 = quat[3];
    }
    
    public Rotation(final Vector3D u, final Vector3D v) throws MathArithmeticException {
        final double normProduct = u.getNorm() * v.getNorm();
        if (normProduct == 0.0) {
            throw new MathArithmeticException(LocalizedFormats.ZERO_NORM_FOR_ROTATION_DEFINING_VECTOR, new Object[0]);
        }
        final double dot = u.dotProduct(v);
        if (dot < -0.999999999999998 * normProduct) {
            final Vector3D w = u.orthogonal();
            this.q0 = 0.0;
            this.q1 = -w.getX();
            this.q2 = -w.getY();
            this.q3 = -w.getZ();
        }
        else {
            this.q0 = FastMath.sqrt(0.5 * (1.0 + dot / normProduct));
            final double coeff = 1.0 / (2.0 * this.q0 * normProduct);
            final Vector3D q = v.crossProduct(u);
            this.q1 = coeff * q.getX();
            this.q2 = coeff * q.getY();
            this.q3 = coeff * q.getZ();
        }
    }
    
    @Deprecated
    public Rotation(final RotationOrder order, final double alpha1, final double alpha2, final double alpha3) {
        this(order, RotationConvention.VECTOR_OPERATOR, alpha1, alpha2, alpha3);
    }
    
    public Rotation(final RotationOrder order, final RotationConvention convention, final double alpha1, final double alpha2, final double alpha3) {
        final Rotation r1 = new Rotation(order.getA1(), alpha1, convention);
        final Rotation r2 = new Rotation(order.getA2(), alpha2, convention);
        final Rotation r3 = new Rotation(order.getA3(), alpha3, convention);
        final Rotation composed = r1.compose(r2.compose(r3, convention), convention);
        this.q0 = composed.q0;
        this.q1 = composed.q1;
        this.q2 = composed.q2;
        this.q3 = composed.q3;
    }
    
    private static double[] mat2quat(final double[][] ort) {
        final double[] quat = new double[4];
        double s = ort[0][0] + ort[1][1] + ort[2][2];
        if (s > -0.19) {
            quat[0] = 0.5 * FastMath.sqrt(s + 1.0);
            final double inv = 0.25 / quat[0];
            quat[1] = inv * (ort[1][2] - ort[2][1]);
            quat[2] = inv * (ort[2][0] - ort[0][2]);
            quat[3] = inv * (ort[0][1] - ort[1][0]);
        }
        else {
            s = ort[0][0] - ort[1][1] - ort[2][2];
            if (s > -0.19) {
                quat[1] = 0.5 * FastMath.sqrt(s + 1.0);
                final double inv = 0.25 / quat[1];
                quat[0] = inv * (ort[1][2] - ort[2][1]);
                quat[2] = inv * (ort[0][1] + ort[1][0]);
                quat[3] = inv * (ort[0][2] + ort[2][0]);
            }
            else {
                s = ort[1][1] - ort[0][0] - ort[2][2];
                if (s > -0.19) {
                    quat[2] = 0.5 * FastMath.sqrt(s + 1.0);
                    final double inv = 0.25 / quat[2];
                    quat[0] = inv * (ort[2][0] - ort[0][2]);
                    quat[1] = inv * (ort[0][1] + ort[1][0]);
                    quat[3] = inv * (ort[2][1] + ort[1][2]);
                }
                else {
                    s = ort[2][2] - ort[0][0] - ort[1][1];
                    quat[3] = 0.5 * FastMath.sqrt(s + 1.0);
                    final double inv = 0.25 / quat[3];
                    quat[0] = inv * (ort[0][1] - ort[1][0]);
                    quat[1] = inv * (ort[0][2] + ort[2][0]);
                    quat[2] = inv * (ort[2][1] + ort[1][2]);
                }
            }
        }
        return quat;
    }
    
    public Rotation revert() {
        return new Rotation(-this.q0, this.q1, this.q2, this.q3, false);
    }
    
    public double getQ0() {
        return this.q0;
    }
    
    public double getQ1() {
        return this.q1;
    }
    
    public double getQ2() {
        return this.q2;
    }
    
    public double getQ3() {
        return this.q3;
    }
    
    @Deprecated
    public Vector3D getAxis() {
        return this.getAxis(RotationConvention.VECTOR_OPERATOR);
    }
    
    public Vector3D getAxis(final RotationConvention convention) {
        final double squaredSine = this.q1 * this.q1 + this.q2 * this.q2 + this.q3 * this.q3;
        if (squaredSine == 0.0) {
            return (convention == RotationConvention.VECTOR_OPERATOR) ? Vector3D.PLUS_I : Vector3D.MINUS_I;
        }
        final double sgn = (convention == RotationConvention.VECTOR_OPERATOR) ? 1.0 : -1.0;
        if (this.q0 < 0.0) {
            final double inverse = sgn / FastMath.sqrt(squaredSine);
            return new Vector3D(this.q1 * inverse, this.q2 * inverse, this.q3 * inverse);
        }
        final double inverse = -sgn / FastMath.sqrt(squaredSine);
        return new Vector3D(this.q1 * inverse, this.q2 * inverse, this.q3 * inverse);
    }
    
    public double getAngle() {
        if (this.q0 < -0.1 || this.q0 > 0.1) {
            return 2.0 * FastMath.asin(FastMath.sqrt(this.q1 * this.q1 + this.q2 * this.q2 + this.q3 * this.q3));
        }
        if (this.q0 < 0.0) {
            return 2.0 * FastMath.acos(-this.q0);
        }
        return 2.0 * FastMath.acos(this.q0);
    }
    
    @Deprecated
    public double[] getAngles(final RotationOrder order) throws CardanEulerSingularityException {
        return this.getAngles(order, RotationConvention.VECTOR_OPERATOR);
    }
    
    public double[] getAngles(final RotationOrder order, final RotationConvention convention) throws CardanEulerSingularityException {
        if (convention == RotationConvention.VECTOR_OPERATOR) {
            if (order == RotationOrder.XYZ) {
                final Vector3D v1 = this.applyTo(Vector3D.PLUS_K);
                final Vector3D v2 = this.applyInverseTo(Vector3D.PLUS_I);
                if (v2.getZ() < -0.9999999999 || v2.getZ() > 0.9999999999) {
                    throw new CardanEulerSingularityException(true);
                }
                return new double[] { FastMath.atan2(-v1.getY(), v1.getZ()), FastMath.asin(v2.getZ()), FastMath.atan2(-v2.getY(), v2.getX()) };
            }
            else if (order == RotationOrder.XZY) {
                final Vector3D v1 = this.applyTo(Vector3D.PLUS_J);
                final Vector3D v2 = this.applyInverseTo(Vector3D.PLUS_I);
                if (v2.getY() < -0.9999999999 || v2.getY() > 0.9999999999) {
                    throw new CardanEulerSingularityException(true);
                }
                return new double[] { FastMath.atan2(v1.getZ(), v1.getY()), -FastMath.asin(v2.getY()), FastMath.atan2(v2.getZ(), v2.getX()) };
            }
            else if (order == RotationOrder.YXZ) {
                final Vector3D v1 = this.applyTo(Vector3D.PLUS_K);
                final Vector3D v2 = this.applyInverseTo(Vector3D.PLUS_J);
                if (v2.getZ() < -0.9999999999 || v2.getZ() > 0.9999999999) {
                    throw new CardanEulerSingularityException(true);
                }
                return new double[] { FastMath.atan2(v1.getX(), v1.getZ()), -FastMath.asin(v2.getZ()), FastMath.atan2(v2.getX(), v2.getY()) };
            }
            else if (order == RotationOrder.YZX) {
                final Vector3D v1 = this.applyTo(Vector3D.PLUS_I);
                final Vector3D v2 = this.applyInverseTo(Vector3D.PLUS_J);
                if (v2.getX() < -0.9999999999 || v2.getX() > 0.9999999999) {
                    throw new CardanEulerSingularityException(true);
                }
                return new double[] { FastMath.atan2(-v1.getZ(), v1.getX()), FastMath.asin(v2.getX()), FastMath.atan2(-v2.getZ(), v2.getY()) };
            }
            else if (order == RotationOrder.ZXY) {
                final Vector3D v1 = this.applyTo(Vector3D.PLUS_J);
                final Vector3D v2 = this.applyInverseTo(Vector3D.PLUS_K);
                if (v2.getY() < -0.9999999999 || v2.getY() > 0.9999999999) {
                    throw new CardanEulerSingularityException(true);
                }
                return new double[] { FastMath.atan2(-v1.getX(), v1.getY()), FastMath.asin(v2.getY()), FastMath.atan2(-v2.getX(), v2.getZ()) };
            }
            else if (order == RotationOrder.ZYX) {
                final Vector3D v1 = this.applyTo(Vector3D.PLUS_I);
                final Vector3D v2 = this.applyInverseTo(Vector3D.PLUS_K);
                if (v2.getX() < -0.9999999999 || v2.getX() > 0.9999999999) {
                    throw new CardanEulerSingularityException(true);
                }
                return new double[] { FastMath.atan2(v1.getY(), v1.getX()), -FastMath.asin(v2.getX()), FastMath.atan2(v2.getY(), v2.getZ()) };
            }
            else if (order == RotationOrder.XYX) {
                final Vector3D v1 = this.applyTo(Vector3D.PLUS_I);
                final Vector3D v2 = this.applyInverseTo(Vector3D.PLUS_I);
                if (v2.getX() < -0.9999999999 || v2.getX() > 0.9999999999) {
                    throw new CardanEulerSingularityException(false);
                }
                return new double[] { FastMath.atan2(v1.getY(), -v1.getZ()), FastMath.acos(v2.getX()), FastMath.atan2(v2.getY(), v2.getZ()) };
            }
            else if (order == RotationOrder.XZX) {
                final Vector3D v1 = this.applyTo(Vector3D.PLUS_I);
                final Vector3D v2 = this.applyInverseTo(Vector3D.PLUS_I);
                if (v2.getX() < -0.9999999999 || v2.getX() > 0.9999999999) {
                    throw new CardanEulerSingularityException(false);
                }
                return new double[] { FastMath.atan2(v1.getZ(), v1.getY()), FastMath.acos(v2.getX()), FastMath.atan2(v2.getZ(), -v2.getY()) };
            }
            else if (order == RotationOrder.YXY) {
                final Vector3D v1 = this.applyTo(Vector3D.PLUS_J);
                final Vector3D v2 = this.applyInverseTo(Vector3D.PLUS_J);
                if (v2.getY() < -0.9999999999 || v2.getY() > 0.9999999999) {
                    throw new CardanEulerSingularityException(false);
                }
                return new double[] { FastMath.atan2(v1.getX(), v1.getZ()), FastMath.acos(v2.getY()), FastMath.atan2(v2.getX(), -v2.getZ()) };
            }
            else if (order == RotationOrder.YZY) {
                final Vector3D v1 = this.applyTo(Vector3D.PLUS_J);
                final Vector3D v2 = this.applyInverseTo(Vector3D.PLUS_J);
                if (v2.getY() < -0.9999999999 || v2.getY() > 0.9999999999) {
                    throw new CardanEulerSingularityException(false);
                }
                return new double[] { FastMath.atan2(v1.getZ(), -v1.getX()), FastMath.acos(v2.getY()), FastMath.atan2(v2.getZ(), v2.getX()) };
            }
            else if (order == RotationOrder.ZXZ) {
                final Vector3D v1 = this.applyTo(Vector3D.PLUS_K);
                final Vector3D v2 = this.applyInverseTo(Vector3D.PLUS_K);
                if (v2.getZ() < -0.9999999999 || v2.getZ() > 0.9999999999) {
                    throw new CardanEulerSingularityException(false);
                }
                return new double[] { FastMath.atan2(v1.getX(), -v1.getY()), FastMath.acos(v2.getZ()), FastMath.atan2(v2.getX(), v2.getY()) };
            }
            else {
                final Vector3D v1 = this.applyTo(Vector3D.PLUS_K);
                final Vector3D v2 = this.applyInverseTo(Vector3D.PLUS_K);
                if (v2.getZ() < -0.9999999999 || v2.getZ() > 0.9999999999) {
                    throw new CardanEulerSingularityException(false);
                }
                return new double[] { FastMath.atan2(v1.getY(), v1.getX()), FastMath.acos(v2.getZ()), FastMath.atan2(v2.getY(), -v2.getX()) };
            }
        }
        else if (order == RotationOrder.XYZ) {
            final Vector3D v1 = this.applyTo(Vector3D.PLUS_I);
            final Vector3D v2 = this.applyInverseTo(Vector3D.PLUS_K);
            if (v2.getX() < -0.9999999999 || v2.getX() > 0.9999999999) {
                throw new CardanEulerSingularityException(true);
            }
            return new double[] { FastMath.atan2(-v2.getY(), v2.getZ()), FastMath.asin(v2.getX()), FastMath.atan2(-v1.getY(), v1.getX()) };
        }
        else if (order == RotationOrder.XZY) {
            final Vector3D v1 = this.applyTo(Vector3D.PLUS_I);
            final Vector3D v2 = this.applyInverseTo(Vector3D.PLUS_J);
            if (v2.getX() < -0.9999999999 || v2.getX() > 0.9999999999) {
                throw new CardanEulerSingularityException(true);
            }
            return new double[] { FastMath.atan2(v2.getZ(), v2.getY()), -FastMath.asin(v2.getX()), FastMath.atan2(v1.getZ(), v1.getX()) };
        }
        else if (order == RotationOrder.YXZ) {
            final Vector3D v1 = this.applyTo(Vector3D.PLUS_J);
            final Vector3D v2 = this.applyInverseTo(Vector3D.PLUS_K);
            if (v2.getY() < -0.9999999999 || v2.getY() > 0.9999999999) {
                throw new CardanEulerSingularityException(true);
            }
            return new double[] { FastMath.atan2(v2.getX(), v2.getZ()), -FastMath.asin(v2.getY()), FastMath.atan2(v1.getX(), v1.getY()) };
        }
        else if (order == RotationOrder.YZX) {
            final Vector3D v1 = this.applyTo(Vector3D.PLUS_J);
            final Vector3D v2 = this.applyInverseTo(Vector3D.PLUS_I);
            if (v2.getY() < -0.9999999999 || v2.getY() > 0.9999999999) {
                throw new CardanEulerSingularityException(true);
            }
            return new double[] { FastMath.atan2(-v2.getZ(), v2.getX()), FastMath.asin(v2.getY()), FastMath.atan2(-v1.getZ(), v1.getY()) };
        }
        else if (order == RotationOrder.ZXY) {
            final Vector3D v1 = this.applyTo(Vector3D.PLUS_K);
            final Vector3D v2 = this.applyInverseTo(Vector3D.PLUS_J);
            if (v2.getZ() < -0.9999999999 || v2.getZ() > 0.9999999999) {
                throw new CardanEulerSingularityException(true);
            }
            return new double[] { FastMath.atan2(-v2.getX(), v2.getY()), FastMath.asin(v2.getZ()), FastMath.atan2(-v1.getX(), v1.getZ()) };
        }
        else if (order == RotationOrder.ZYX) {
            final Vector3D v1 = this.applyTo(Vector3D.PLUS_K);
            final Vector3D v2 = this.applyInverseTo(Vector3D.PLUS_I);
            if (v2.getZ() < -0.9999999999 || v2.getZ() > 0.9999999999) {
                throw new CardanEulerSingularityException(true);
            }
            return new double[] { FastMath.atan2(v2.getY(), v2.getX()), -FastMath.asin(v2.getZ()), FastMath.atan2(v1.getY(), v1.getZ()) };
        }
        else if (order == RotationOrder.XYX) {
            final Vector3D v1 = this.applyTo(Vector3D.PLUS_I);
            final Vector3D v2 = this.applyInverseTo(Vector3D.PLUS_I);
            if (v2.getX() < -0.9999999999 || v2.getX() > 0.9999999999) {
                throw new CardanEulerSingularityException(false);
            }
            return new double[] { FastMath.atan2(v2.getY(), -v2.getZ()), FastMath.acos(v2.getX()), FastMath.atan2(v1.getY(), v1.getZ()) };
        }
        else if (order == RotationOrder.XZX) {
            final Vector3D v1 = this.applyTo(Vector3D.PLUS_I);
            final Vector3D v2 = this.applyInverseTo(Vector3D.PLUS_I);
            if (v2.getX() < -0.9999999999 || v2.getX() > 0.9999999999) {
                throw new CardanEulerSingularityException(false);
            }
            return new double[] { FastMath.atan2(v2.getZ(), v2.getY()), FastMath.acos(v2.getX()), FastMath.atan2(v1.getZ(), -v1.getY()) };
        }
        else if (order == RotationOrder.YXY) {
            final Vector3D v1 = this.applyTo(Vector3D.PLUS_J);
            final Vector3D v2 = this.applyInverseTo(Vector3D.PLUS_J);
            if (v2.getY() < -0.9999999999 || v2.getY() > 0.9999999999) {
                throw new CardanEulerSingularityException(false);
            }
            return new double[] { FastMath.atan2(v2.getX(), v2.getZ()), FastMath.acos(v2.getY()), FastMath.atan2(v1.getX(), -v1.getZ()) };
        }
        else if (order == RotationOrder.YZY) {
            final Vector3D v1 = this.applyTo(Vector3D.PLUS_J);
            final Vector3D v2 = this.applyInverseTo(Vector3D.PLUS_J);
            if (v2.getY() < -0.9999999999 || v2.getY() > 0.9999999999) {
                throw new CardanEulerSingularityException(false);
            }
            return new double[] { FastMath.atan2(v2.getZ(), -v2.getX()), FastMath.acos(v2.getY()), FastMath.atan2(v1.getZ(), v1.getX()) };
        }
        else if (order == RotationOrder.ZXZ) {
            final Vector3D v1 = this.applyTo(Vector3D.PLUS_K);
            final Vector3D v2 = this.applyInverseTo(Vector3D.PLUS_K);
            if (v2.getZ() < -0.9999999999 || v2.getZ() > 0.9999999999) {
                throw new CardanEulerSingularityException(false);
            }
            return new double[] { FastMath.atan2(v2.getX(), -v2.getY()), FastMath.acos(v2.getZ()), FastMath.atan2(v1.getX(), v1.getY()) };
        }
        else {
            final Vector3D v1 = this.applyTo(Vector3D.PLUS_K);
            final Vector3D v2 = this.applyInverseTo(Vector3D.PLUS_K);
            if (v2.getZ() < -0.9999999999 || v2.getZ() > 0.9999999999) {
                throw new CardanEulerSingularityException(false);
            }
            return new double[] { FastMath.atan2(v2.getY(), v2.getX()), FastMath.acos(v2.getZ()), FastMath.atan2(v1.getY(), -v1.getX()) };
        }
    }
    
    public double[][] getMatrix() {
        final double q0q0 = this.q0 * this.q0;
        final double q0q2 = this.q0 * this.q1;
        final double q0q3 = this.q0 * this.q2;
        final double q0q4 = this.q0 * this.q3;
        final double q1q1 = this.q1 * this.q1;
        final double q1q2 = this.q1 * this.q2;
        final double q1q3 = this.q1 * this.q3;
        final double q2q2 = this.q2 * this.q2;
        final double q2q3 = this.q2 * this.q3;
        final double q3q3 = this.q3 * this.q3;
        final double[][] m = { new double[3], new double[3], new double[3] };
        m[0][0] = 2.0 * (q0q0 + q1q1) - 1.0;
        m[1][0] = 2.0 * (q1q2 - q0q4);
        m[2][0] = 2.0 * (q1q3 + q0q3);
        m[0][1] = 2.0 * (q1q2 + q0q4);
        m[1][1] = 2.0 * (q0q0 + q2q2) - 1.0;
        m[2][1] = 2.0 * (q2q3 - q0q2);
        m[0][2] = 2.0 * (q1q3 - q0q3);
        m[1][2] = 2.0 * (q2q3 + q0q2);
        m[2][2] = 2.0 * (q0q0 + q3q3) - 1.0;
        return m;
    }
    
    public Vector3D applyTo(final Vector3D u) {
        final double x = u.getX();
        final double y = u.getY();
        final double z = u.getZ();
        final double s = this.q1 * x + this.q2 * y + this.q3 * z;
        return new Vector3D(2.0 * (this.q0 * (x * this.q0 - (this.q2 * z - this.q3 * y)) + s * this.q1) - x, 2.0 * (this.q0 * (y * this.q0 - (this.q3 * x - this.q1 * z)) + s * this.q2) - y, 2.0 * (this.q0 * (z * this.q0 - (this.q1 * y - this.q2 * x)) + s * this.q3) - z);
    }
    
    public void applyTo(final double[] in, final double[] out) {
        final double x = in[0];
        final double y = in[1];
        final double z = in[2];
        final double s = this.q1 * x + this.q2 * y + this.q3 * z;
        out[0] = 2.0 * (this.q0 * (x * this.q0 - (this.q2 * z - this.q3 * y)) + s * this.q1) - x;
        out[1] = 2.0 * (this.q0 * (y * this.q0 - (this.q3 * x - this.q1 * z)) + s * this.q2) - y;
        out[2] = 2.0 * (this.q0 * (z * this.q0 - (this.q1 * y - this.q2 * x)) + s * this.q3) - z;
    }
    
    public Vector3D applyInverseTo(final Vector3D u) {
        final double x = u.getX();
        final double y = u.getY();
        final double z = u.getZ();
        final double s = this.q1 * x + this.q2 * y + this.q3 * z;
        final double m0 = -this.q0;
        return new Vector3D(2.0 * (m0 * (x * m0 - (this.q2 * z - this.q3 * y)) + s * this.q1) - x, 2.0 * (m0 * (y * m0 - (this.q3 * x - this.q1 * z)) + s * this.q2) - y, 2.0 * (m0 * (z * m0 - (this.q1 * y - this.q2 * x)) + s * this.q3) - z);
    }
    
    public void applyInverseTo(final double[] in, final double[] out) {
        final double x = in[0];
        final double y = in[1];
        final double z = in[2];
        final double s = this.q1 * x + this.q2 * y + this.q3 * z;
        final double m0 = -this.q0;
        out[0] = 2.0 * (m0 * (x * m0 - (this.q2 * z - this.q3 * y)) + s * this.q1) - x;
        out[1] = 2.0 * (m0 * (y * m0 - (this.q3 * x - this.q1 * z)) + s * this.q2) - y;
        out[2] = 2.0 * (m0 * (z * m0 - (this.q1 * y - this.q2 * x)) + s * this.q3) - z;
    }
    
    public Rotation applyTo(final Rotation r) {
        return this.compose(r, RotationConvention.VECTOR_OPERATOR);
    }
    
    public Rotation compose(final Rotation r, final RotationConvention convention) {
        return (convention == RotationConvention.VECTOR_OPERATOR) ? this.composeInternal(r) : r.composeInternal(this);
    }
    
    private Rotation composeInternal(final Rotation r) {
        return new Rotation(r.q0 * this.q0 - (r.q1 * this.q1 + r.q2 * this.q2 + r.q3 * this.q3), r.q1 * this.q0 + r.q0 * this.q1 + (r.q2 * this.q3 - r.q3 * this.q2), r.q2 * this.q0 + r.q0 * this.q2 + (r.q3 * this.q1 - r.q1 * this.q3), r.q3 * this.q0 + r.q0 * this.q3 + (r.q1 * this.q2 - r.q2 * this.q1), false);
    }
    
    public Rotation applyInverseTo(final Rotation r) {
        return this.composeInverse(r, RotationConvention.VECTOR_OPERATOR);
    }
    
    public Rotation composeInverse(final Rotation r, final RotationConvention convention) {
        return (convention == RotationConvention.VECTOR_OPERATOR) ? this.composeInverseInternal(r) : r.composeInternal(this.revert());
    }
    
    private Rotation composeInverseInternal(final Rotation r) {
        return new Rotation(-r.q0 * this.q0 - (r.q1 * this.q1 + r.q2 * this.q2 + r.q3 * this.q3), -r.q1 * this.q0 + r.q0 * this.q1 + (r.q2 * this.q3 - r.q3 * this.q2), -r.q2 * this.q0 + r.q0 * this.q2 + (r.q3 * this.q1 - r.q1 * this.q3), -r.q3 * this.q0 + r.q0 * this.q3 + (r.q1 * this.q2 - r.q2 * this.q1), false);
    }
    
    private double[][] orthogonalizeMatrix(final double[][] m, final double threshold) throws NotARotationMatrixException {
        final double[] m2 = m[0];
        final double[] m3 = m[1];
        final double[] m4 = m[2];
        double x00 = m2[0];
        double x2 = m2[1];
        double x3 = m2[2];
        double x4 = m3[0];
        double x5 = m3[1];
        double x6 = m3[2];
        double x7 = m4[0];
        double x8 = m4[1];
        double x9 = m4[2];
        double fn = 0.0;
        final double[][] o = new double[3][3];
        final double[] o2 = o[0];
        final double[] o3 = o[1];
        final double[] o4 = o[2];
        int i = 0;
        while (++i < 11) {
            final double mx00 = m2[0] * x00 + m3[0] * x4 + m4[0] * x7;
            final double mx2 = m2[1] * x00 + m3[1] * x4 + m4[1] * x7;
            final double mx3 = m2[2] * x00 + m3[2] * x4 + m4[2] * x7;
            final double mx4 = m2[0] * x2 + m3[0] * x5 + m4[0] * x8;
            final double mx5 = m2[1] * x2 + m3[1] * x5 + m4[1] * x8;
            final double mx6 = m2[2] * x2 + m3[2] * x5 + m4[2] * x8;
            final double mx7 = m2[0] * x3 + m3[0] * x6 + m4[0] * x9;
            final double mx8 = m2[1] * x3 + m3[1] * x6 + m4[1] * x9;
            final double mx9 = m2[2] * x3 + m3[2] * x6 + m4[2] * x9;
            o2[0] = x00 - 0.5 * (x00 * mx00 + x2 * mx2 + x3 * mx3 - m2[0]);
            o2[1] = x2 - 0.5 * (x00 * mx4 + x2 * mx5 + x3 * mx6 - m2[1]);
            o2[2] = x3 - 0.5 * (x00 * mx7 + x2 * mx8 + x3 * mx9 - m2[2]);
            o3[0] = x4 - 0.5 * (x4 * mx00 + x5 * mx2 + x6 * mx3 - m3[0]);
            o3[1] = x5 - 0.5 * (x4 * mx4 + x5 * mx5 + x6 * mx6 - m3[1]);
            o3[2] = x6 - 0.5 * (x4 * mx7 + x5 * mx8 + x6 * mx9 - m3[2]);
            o4[0] = x7 - 0.5 * (x7 * mx00 + x8 * mx2 + x9 * mx3 - m4[0]);
            o4[1] = x8 - 0.5 * (x7 * mx4 + x8 * mx5 + x9 * mx6 - m4[1]);
            o4[2] = x9 - 0.5 * (x7 * mx7 + x8 * mx8 + x9 * mx9 - m4[2]);
            final double corr00 = o2[0] - m2[0];
            final double corr2 = o2[1] - m2[1];
            final double corr3 = o2[2] - m2[2];
            final double corr4 = o3[0] - m3[0];
            final double corr5 = o3[1] - m3[1];
            final double corr6 = o3[2] - m3[2];
            final double corr7 = o4[0] - m4[0];
            final double corr8 = o4[1] - m4[1];
            final double corr9 = o4[2] - m4[2];
            final double fn2 = corr00 * corr00 + corr2 * corr2 + corr3 * corr3 + corr4 * corr4 + corr5 * corr5 + corr6 * corr6 + corr7 * corr7 + corr8 * corr8 + corr9 * corr9;
            if (FastMath.abs(fn2 - fn) <= threshold) {
                return o;
            }
            x00 = o2[0];
            x2 = o2[1];
            x3 = o2[2];
            x4 = o3[0];
            x5 = o3[1];
            x6 = o3[2];
            x7 = o4[0];
            x8 = o4[1];
            x9 = o4[2];
            fn = fn2;
        }
        throw new NotARotationMatrixException(LocalizedFormats.UNABLE_TO_ORTHOGONOLIZE_MATRIX, new Object[] { i - 1 });
    }
    
    public static double distance(final Rotation r1, final Rotation r2) {
        return r1.composeInverseInternal(r2).getAngle();
    }
    
    static {
        IDENTITY = new Rotation(1.0, 0.0, 0.0, 0.0, false);
    }
}
