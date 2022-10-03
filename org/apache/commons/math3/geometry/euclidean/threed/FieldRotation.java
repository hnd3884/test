package org.apache.commons.math3.geometry.euclidean.threed;

import org.apache.commons.math3.FieldElement;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.Field;
import org.apache.commons.math3.exception.MathArithmeticException;
import org.apache.commons.math3.util.MathArrays;
import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import java.io.Serializable;
import org.apache.commons.math3.RealFieldElement;

public class FieldRotation<T extends RealFieldElement<T>> implements Serializable
{
    private static final long serialVersionUID = 20130224L;
    private final T q0;
    private final T q1;
    private final T q2;
    private final T q3;
    
    public FieldRotation(final T q0, final T q1, final T q2, final T q3, final boolean needsNormalization) {
        if (needsNormalization) {
            final T inv = ((RealFieldElement<RealFieldElement<T>>)q0.multiply(q0).add(q1.multiply(q1)).add(q2.multiply(q2)).add(q3.multiply(q3))).sqrt().reciprocal();
            this.q0 = inv.multiply(q0);
            this.q1 = inv.multiply(q1);
            this.q2 = inv.multiply(q2);
            this.q3 = inv.multiply(q3);
        }
        else {
            this.q0 = q0;
            this.q1 = q1;
            this.q2 = q2;
            this.q3 = q3;
        }
    }
    
    @Deprecated
    public FieldRotation(final FieldVector3D<T> axis, final T angle) throws MathIllegalArgumentException {
        this((FieldVector3D<RealFieldElement>)axis, angle, RotationConvention.VECTOR_OPERATOR);
    }
    
    public FieldRotation(final FieldVector3D<T> axis, final T angle, final RotationConvention convention) throws MathIllegalArgumentException {
        final T norm = axis.getNorm();
        if (norm.getReal() == 0.0) {
            throw new MathIllegalArgumentException(LocalizedFormats.ZERO_NORM_FOR_ROTATION_AXIS, new Object[0]);
        }
        final T halfAngle = angle.multiply((convention == RotationConvention.VECTOR_OPERATOR) ? -0.5 : 0.5);
        final T coeff = ((RealFieldElement<RealFieldElement<T>>)halfAngle).sin().divide(norm);
        this.q0 = halfAngle.cos();
        this.q1 = coeff.multiply(axis.getX());
        this.q2 = coeff.multiply(axis.getY());
        this.q3 = coeff.multiply(axis.getZ());
    }
    
    public FieldRotation(final T[][] m, final double threshold) throws NotARotationMatrixException {
        if (m.length != 3 || m[0].length != 3 || m[1].length != 3 || m[2].length != 3) {
            throw new NotARotationMatrixException(LocalizedFormats.ROTATION_MATRIX_DIMENSIONS, new Object[] { m.length, m[0].length });
        }
        final T[][] ort = this.orthogonalizeMatrix(m, threshold);
        final T d0 = ort[1][1].multiply(ort[2][2]).subtract(ort[2][1].multiply(ort[1][2]));
        final T d2 = ort[0][1].multiply(ort[2][2]).subtract(ort[2][1].multiply(ort[0][2]));
        final T d3 = ort[0][1].multiply(ort[1][2]).subtract(ort[1][1].multiply(ort[0][2]));
        final T det = ort[0][0].multiply(d0).subtract(ort[1][0].multiply(d2)).add(ort[2][0].multiply(d3));
        if (det.getReal() < 0.0) {
            throw new NotARotationMatrixException(LocalizedFormats.CLOSEST_ORTHOGONAL_MATRIX_HAS_NEGATIVE_DETERMINANT, new Object[] { det });
        }
        final T[] quat = this.mat2quat(ort);
        this.q0 = quat[0];
        this.q1 = quat[1];
        this.q2 = quat[2];
        this.q3 = quat[3];
    }
    
    public FieldRotation(FieldVector3D<T> u1, FieldVector3D<T> u2, FieldVector3D<T> v1, FieldVector3D<T> v2) throws MathArithmeticException {
        final FieldVector3D<T> u3 = FieldVector3D.crossProduct(u1, u2).normalize();
        u2 = FieldVector3D.crossProduct(u3, u1).normalize();
        u1 = u1.normalize();
        final FieldVector3D<T> v3 = FieldVector3D.crossProduct(v1, v2).normalize();
        v2 = FieldVector3D.crossProduct(v3, v1).normalize();
        v1 = v1.normalize();
        final T[][] array = MathArrays.buildArray(u1.getX().getField(), 3, 3);
        array[0][0] = u1.getX().multiply(v1.getX()).add(u2.getX().multiply(v2.getX())).add(u3.getX().multiply(v3.getX()));
        array[0][1] = u1.getY().multiply(v1.getX()).add(u2.getY().multiply(v2.getX())).add(u3.getY().multiply(v3.getX()));
        array[0][2] = u1.getZ().multiply(v1.getX()).add(u2.getZ().multiply(v2.getX())).add(u3.getZ().multiply(v3.getX()));
        array[1][0] = u1.getX().multiply(v1.getY()).add(u2.getX().multiply(v2.getY())).add(u3.getX().multiply(v3.getY()));
        array[1][1] = u1.getY().multiply(v1.getY()).add(u2.getY().multiply(v2.getY())).add(u3.getY().multiply(v3.getY()));
        array[1][2] = u1.getZ().multiply(v1.getY()).add(u2.getZ().multiply(v2.getY())).add(u3.getZ().multiply(v3.getY()));
        array[2][0] = u1.getX().multiply(v1.getZ()).add(u2.getX().multiply(v2.getZ())).add(u3.getX().multiply(v3.getZ()));
        array[2][1] = u1.getY().multiply(v1.getZ()).add(u2.getY().multiply(v2.getZ())).add(u3.getY().multiply(v3.getZ()));
        array[2][2] = u1.getZ().multiply(v1.getZ()).add(u2.getZ().multiply(v2.getZ())).add(u3.getZ().multiply(v3.getZ()));
        final T[] quat = this.mat2quat(array);
        this.q0 = quat[0];
        this.q1 = quat[1];
        this.q2 = quat[2];
        this.q3 = quat[3];
    }
    
    public FieldRotation(final FieldVector3D<T> u, final FieldVector3D<T> v) throws MathArithmeticException {
        final T normProduct = u.getNorm().multiply(v.getNorm());
        if (normProduct.getReal() == 0.0) {
            throw new MathArithmeticException(LocalizedFormats.ZERO_NORM_FOR_ROTATION_DEFINING_VECTOR, new Object[0]);
        }
        final T dot = FieldVector3D.dotProduct(u, v);
        if (dot.getReal() < -0.999999999999998 * normProduct.getReal()) {
            final FieldVector3D<T> w = u.orthogonal();
            this.q0 = normProduct.getField().getZero();
            this.q1 = w.getX().negate();
            this.q2 = w.getY().negate();
            this.q3 = w.getZ().negate();
        }
        else {
            this.q0 = ((RealFieldElement<RealFieldElement<RealFieldElement>>)dot.divide(normProduct)).add(1.0).multiply(0.5).sqrt();
            final T coeff = ((RealFieldElement<RealFieldElement<T>>)this.q0.multiply(normProduct)).multiply(2.0).reciprocal();
            final FieldVector3D<T> q = FieldVector3D.crossProduct(v, u);
            this.q1 = coeff.multiply(q.getX());
            this.q2 = coeff.multiply(q.getY());
            this.q3 = coeff.multiply(q.getZ());
        }
    }
    
    @Deprecated
    public FieldRotation(final RotationOrder order, final T alpha1, final T alpha2, final T alpha3) {
        this(order, RotationConvention.VECTOR_OPERATOR, alpha1, alpha2, alpha3);
    }
    
    public FieldRotation(final RotationOrder order, final RotationConvention convention, final T alpha1, final T alpha2, final T alpha3) {
        final T one = alpha1.getField().getOne();
        final FieldRotation<T> r1 = new FieldRotation<T>(new FieldVector3D<T>(one, order.getA1()), alpha1, convention);
        final FieldRotation<T> r2 = new FieldRotation<T>(new FieldVector3D<T>(one, order.getA2()), alpha2, convention);
        final FieldRotation<T> r3 = new FieldRotation<T>(new FieldVector3D<T>(one, order.getA3()), alpha3, convention);
        final FieldRotation<T> composed = r1.compose(r2.compose(r3, convention), convention);
        this.q0 = composed.q0;
        this.q1 = composed.q1;
        this.q2 = composed.q2;
        this.q3 = composed.q3;
    }
    
    private T[] mat2quat(final T[][] ort) {
        final T[] quat = MathArrays.buildArray(ort[0][0].getField(), 4);
        T s = ort[0][0].add(ort[1][1]).add(ort[2][2]);
        if (s.getReal() > -0.19) {
            quat[0] = ((RealFieldElement<RealFieldElement<RealFieldElement>>)s).add(1.0).sqrt().multiply(0.5);
            final T inv = ((RealFieldElement<RealFieldElement<T>>)quat[0]).reciprocal().multiply(0.25);
            quat[1] = inv.multiply(ort[1][2].subtract(ort[2][1]));
            quat[2] = inv.multiply(ort[2][0].subtract(ort[0][2]));
            quat[3] = inv.multiply(ort[0][1].subtract(ort[1][0]));
        }
        else {
            s = ort[0][0].subtract(ort[1][1]).subtract(ort[2][2]);
            if (s.getReal() > -0.19) {
                quat[1] = ((RealFieldElement<RealFieldElement<RealFieldElement>>)s).add(1.0).sqrt().multiply(0.5);
                final T inv = ((RealFieldElement<RealFieldElement<T>>)quat[1]).reciprocal().multiply(0.25);
                quat[0] = inv.multiply(ort[1][2].subtract(ort[2][1]));
                quat[2] = inv.multiply(ort[0][1].add(ort[1][0]));
                quat[3] = inv.multiply(ort[0][2].add(ort[2][0]));
            }
            else {
                s = ort[1][1].subtract(ort[0][0]).subtract(ort[2][2]);
                if (s.getReal() > -0.19) {
                    quat[2] = ((RealFieldElement<RealFieldElement<RealFieldElement>>)s).add(1.0).sqrt().multiply(0.5);
                    final T inv = ((RealFieldElement<RealFieldElement<T>>)quat[2]).reciprocal().multiply(0.25);
                    quat[0] = inv.multiply(ort[2][0].subtract(ort[0][2]));
                    quat[1] = inv.multiply(ort[0][1].add(ort[1][0]));
                    quat[3] = inv.multiply(ort[2][1].add(ort[1][2]));
                }
                else {
                    s = ort[2][2].subtract(ort[0][0]).subtract(ort[1][1]);
                    quat[3] = ((RealFieldElement<RealFieldElement<RealFieldElement>>)s).add(1.0).sqrt().multiply(0.5);
                    final T inv = ((RealFieldElement<RealFieldElement<T>>)quat[3]).reciprocal().multiply(0.25);
                    quat[0] = inv.multiply(ort[0][1].subtract(ort[1][0]));
                    quat[1] = inv.multiply(ort[0][2].add(ort[2][0]));
                    quat[2] = inv.multiply(ort[2][1].add(ort[1][2]));
                }
            }
        }
        return quat;
    }
    
    public FieldRotation<T> revert() {
        return new FieldRotation<T>(this.q0.negate(), this.q1, this.q2, this.q3, false);
    }
    
    public T getQ0() {
        return this.q0;
    }
    
    public T getQ1() {
        return this.q1;
    }
    
    public T getQ2() {
        return this.q2;
    }
    
    public T getQ3() {
        return this.q3;
    }
    
    @Deprecated
    public FieldVector3D<T> getAxis() {
        return this.getAxis(RotationConvention.VECTOR_OPERATOR);
    }
    
    public FieldVector3D<T> getAxis(final RotationConvention convention) {
        final T squaredSine = this.q1.multiply(this.q1).add(this.q2.multiply(this.q2)).add(this.q3.multiply(this.q3));
        if (squaredSine.getReal() == 0.0) {
            final Field<T> field = squaredSine.getField();
            return new FieldVector3D<T>((convention == RotationConvention.VECTOR_OPERATOR) ? field.getOne() : field.getOne().negate(), field.getZero(), field.getZero());
        }
        final double sgn = (convention == RotationConvention.VECTOR_OPERATOR) ? 1.0 : -1.0;
        if (this.q0.getReal() < 0.0) {
            final T inverse = ((RealFieldElement<RealFieldElement<RealFieldElement>>)squaredSine).sqrt().reciprocal().multiply(sgn);
            return new FieldVector3D<T>(this.q1.multiply(inverse), this.q2.multiply(inverse), this.q3.multiply(inverse));
        }
        final T inverse = ((RealFieldElement<RealFieldElement<RealFieldElement>>)squaredSine).sqrt().reciprocal().negate().multiply(sgn);
        return new FieldVector3D<T>(this.q1.multiply(inverse), this.q2.multiply(inverse), this.q3.multiply(inverse));
    }
    
    public T getAngle() {
        if (this.q0.getReal() < -0.1 || this.q0.getReal() > 0.1) {
            return ((RealFieldElement<RealFieldElement<RealFieldElement>>)this.q1.multiply(this.q1).add(this.q2.multiply(this.q2)).add(this.q3.multiply(this.q3))).sqrt().asin().multiply(2);
        }
        if (this.q0.getReal() < 0.0) {
            return ((FieldElement<RealFieldElement<RealFieldElement>>)this.q0).negate().acos().multiply(2);
        }
        return ((RealFieldElement<RealFieldElement<T>>)this.q0).acos().multiply(2);
    }
    
    @Deprecated
    public T[] getAngles(final RotationOrder order) throws CardanEulerSingularityException {
        return this.getAngles(order, RotationConvention.VECTOR_OPERATOR);
    }
    
    public T[] getAngles(final RotationOrder order, final RotationConvention convention) throws CardanEulerSingularityException {
        if (convention == RotationConvention.VECTOR_OPERATOR) {
            if (order == RotationOrder.XYZ) {
                final FieldVector3D<T> v1 = this.applyTo(this.vector(0.0, 0.0, 1.0));
                final FieldVector3D<T> v2 = this.applyInverseTo(this.vector(1.0, 0.0, 0.0));
                if (v2.getZ().getReal() < -0.9999999999 || v2.getZ().getReal() > 0.9999999999) {
                    throw new CardanEulerSingularityException(true);
                }
                return this.buildArray(((FieldElement<RealFieldElement<T>>)v1.getY()).negate().atan2(v1.getZ()), v2.getZ().asin(), ((FieldElement<RealFieldElement<T>>)v2.getY()).negate().atan2(v2.getX()));
            }
            else if (order == RotationOrder.XZY) {
                final FieldVector3D<T> v1 = this.applyTo(this.vector(0.0, 1.0, 0.0));
                final FieldVector3D<T> v2 = this.applyInverseTo(this.vector(1.0, 0.0, 0.0));
                if (v2.getY().getReal() < -0.9999999999 || v2.getY().getReal() > 0.9999999999) {
                    throw new CardanEulerSingularityException(true);
                }
                return this.buildArray(v1.getZ().atan2(v1.getY()), ((RealFieldElement<RealFieldElement<T>>)v2.getY()).asin().negate(), v2.getZ().atan2(v2.getX()));
            }
            else if (order == RotationOrder.YXZ) {
                final FieldVector3D<T> v1 = this.applyTo(this.vector(0.0, 0.0, 1.0));
                final FieldVector3D<T> v2 = this.applyInverseTo(this.vector(0.0, 1.0, 0.0));
                if (v2.getZ().getReal() < -0.9999999999 || v2.getZ().getReal() > 0.9999999999) {
                    throw new CardanEulerSingularityException(true);
                }
                return this.buildArray(v1.getX().atan2(v1.getZ()), ((RealFieldElement<RealFieldElement<T>>)v2.getZ()).asin().negate(), v2.getX().atan2(v2.getY()));
            }
            else if (order == RotationOrder.YZX) {
                final FieldVector3D<T> v1 = this.applyTo(this.vector(1.0, 0.0, 0.0));
                final FieldVector3D<T> v2 = this.applyInverseTo(this.vector(0.0, 1.0, 0.0));
                if (v2.getX().getReal() < -0.9999999999 || v2.getX().getReal() > 0.9999999999) {
                    throw new CardanEulerSingularityException(true);
                }
                return this.buildArray(((FieldElement<RealFieldElement<T>>)v1.getZ()).negate().atan2(v1.getX()), v2.getX().asin(), ((FieldElement<RealFieldElement<T>>)v2.getZ()).negate().atan2(v2.getY()));
            }
            else if (order == RotationOrder.ZXY) {
                final FieldVector3D<T> v1 = this.applyTo(this.vector(0.0, 1.0, 0.0));
                final FieldVector3D<T> v2 = this.applyInverseTo(this.vector(0.0, 0.0, 1.0));
                if (v2.getY().getReal() < -0.9999999999 || v2.getY().getReal() > 0.9999999999) {
                    throw new CardanEulerSingularityException(true);
                }
                return this.buildArray(((FieldElement<RealFieldElement<T>>)v1.getX()).negate().atan2(v1.getY()), v2.getY().asin(), ((FieldElement<RealFieldElement<T>>)v2.getX()).negate().atan2(v2.getZ()));
            }
            else if (order == RotationOrder.ZYX) {
                final FieldVector3D<T> v1 = this.applyTo(this.vector(1.0, 0.0, 0.0));
                final FieldVector3D<T> v2 = this.applyInverseTo(this.vector(0.0, 0.0, 1.0));
                if (v2.getX().getReal() < -0.9999999999 || v2.getX().getReal() > 0.9999999999) {
                    throw new CardanEulerSingularityException(true);
                }
                return this.buildArray(v1.getY().atan2(v1.getX()), ((RealFieldElement<RealFieldElement<T>>)v2.getX()).asin().negate(), v2.getY().atan2(v2.getZ()));
            }
            else if (order == RotationOrder.XYX) {
                final FieldVector3D<T> v1 = this.applyTo(this.vector(1.0, 0.0, 0.0));
                final FieldVector3D<T> v2 = this.applyInverseTo(this.vector(1.0, 0.0, 0.0));
                if (v2.getX().getReal() < -0.9999999999 || v2.getX().getReal() > 0.9999999999) {
                    throw new CardanEulerSingularityException(false);
                }
                return this.buildArray(v1.getY().atan2(v1.getZ().negate()), v2.getX().acos(), v2.getY().atan2(v2.getZ()));
            }
            else if (order == RotationOrder.XZX) {
                final FieldVector3D<T> v1 = this.applyTo(this.vector(1.0, 0.0, 0.0));
                final FieldVector3D<T> v2 = this.applyInverseTo(this.vector(1.0, 0.0, 0.0));
                if (v2.getX().getReal() < -0.9999999999 || v2.getX().getReal() > 0.9999999999) {
                    throw new CardanEulerSingularityException(false);
                }
                return this.buildArray(v1.getZ().atan2(v1.getY()), v2.getX().acos(), v2.getZ().atan2(v2.getY().negate()));
            }
            else if (order == RotationOrder.YXY) {
                final FieldVector3D<T> v1 = this.applyTo(this.vector(0.0, 1.0, 0.0));
                final FieldVector3D<T> v2 = this.applyInverseTo(this.vector(0.0, 1.0, 0.0));
                if (v2.getY().getReal() < -0.9999999999 || v2.getY().getReal() > 0.9999999999) {
                    throw new CardanEulerSingularityException(false);
                }
                return this.buildArray(v1.getX().atan2(v1.getZ()), v2.getY().acos(), v2.getX().atan2(v2.getZ().negate()));
            }
            else if (order == RotationOrder.YZY) {
                final FieldVector3D<T> v1 = this.applyTo(this.vector(0.0, 1.0, 0.0));
                final FieldVector3D<T> v2 = this.applyInverseTo(this.vector(0.0, 1.0, 0.0));
                if (v2.getY().getReal() < -0.9999999999 || v2.getY().getReal() > 0.9999999999) {
                    throw new CardanEulerSingularityException(false);
                }
                return this.buildArray(v1.getZ().atan2(v1.getX().negate()), v2.getY().acos(), v2.getZ().atan2(v2.getX()));
            }
            else if (order == RotationOrder.ZXZ) {
                final FieldVector3D<T> v1 = this.applyTo(this.vector(0.0, 0.0, 1.0));
                final FieldVector3D<T> v2 = this.applyInverseTo(this.vector(0.0, 0.0, 1.0));
                if (v2.getZ().getReal() < -0.9999999999 || v2.getZ().getReal() > 0.9999999999) {
                    throw new CardanEulerSingularityException(false);
                }
                return this.buildArray(v1.getX().atan2(v1.getY().negate()), v2.getZ().acos(), v2.getX().atan2(v2.getY()));
            }
            else {
                final FieldVector3D<T> v1 = this.applyTo(this.vector(0.0, 0.0, 1.0));
                final FieldVector3D<T> v2 = this.applyInverseTo(this.vector(0.0, 0.0, 1.0));
                if (v2.getZ().getReal() < -0.9999999999 || v2.getZ().getReal() > 0.9999999999) {
                    throw new CardanEulerSingularityException(false);
                }
                return this.buildArray(v1.getY().atan2(v1.getX()), v2.getZ().acos(), v2.getY().atan2(v2.getX().negate()));
            }
        }
        else if (order == RotationOrder.XYZ) {
            final FieldVector3D<T> v1 = this.applyTo(Vector3D.PLUS_I);
            final FieldVector3D<T> v2 = this.applyInverseTo(Vector3D.PLUS_K);
            if (v2.getX().getReal() < -0.9999999999 || v2.getX().getReal() > 0.9999999999) {
                throw new CardanEulerSingularityException(true);
            }
            return this.buildArray(((FieldElement<RealFieldElement<T>>)v2.getY()).negate().atan2(v2.getZ()), v2.getX().asin(), ((FieldElement<RealFieldElement<T>>)v1.getY()).negate().atan2(v1.getX()));
        }
        else if (order == RotationOrder.XZY) {
            final FieldVector3D<T> v1 = this.applyTo(Vector3D.PLUS_I);
            final FieldVector3D<T> v2 = this.applyInverseTo(Vector3D.PLUS_J);
            if (v2.getX().getReal() < -0.9999999999 || v2.getX().getReal() > 0.9999999999) {
                throw new CardanEulerSingularityException(true);
            }
            return this.buildArray(v2.getZ().atan2(v2.getY()), ((RealFieldElement<RealFieldElement<T>>)v2.getX()).asin().negate(), v1.getZ().atan2(v1.getX()));
        }
        else if (order == RotationOrder.YXZ) {
            final FieldVector3D<T> v1 = this.applyTo(Vector3D.PLUS_J);
            final FieldVector3D<T> v2 = this.applyInverseTo(Vector3D.PLUS_K);
            if (v2.getY().getReal() < -0.9999999999 || v2.getY().getReal() > 0.9999999999) {
                throw new CardanEulerSingularityException(true);
            }
            return this.buildArray(v2.getX().atan2(v2.getZ()), ((RealFieldElement<RealFieldElement<T>>)v2.getY()).asin().negate(), v1.getX().atan2(v1.getY()));
        }
        else if (order == RotationOrder.YZX) {
            final FieldVector3D<T> v1 = this.applyTo(Vector3D.PLUS_J);
            final FieldVector3D<T> v2 = this.applyInverseTo(Vector3D.PLUS_I);
            if (v2.getY().getReal() < -0.9999999999 || v2.getY().getReal() > 0.9999999999) {
                throw new CardanEulerSingularityException(true);
            }
            return this.buildArray(((FieldElement<RealFieldElement<T>>)v2.getZ()).negate().atan2(v2.getX()), v2.getY().asin(), ((FieldElement<RealFieldElement<T>>)v1.getZ()).negate().atan2(v1.getY()));
        }
        else if (order == RotationOrder.ZXY) {
            final FieldVector3D<T> v1 = this.applyTo(Vector3D.PLUS_K);
            final FieldVector3D<T> v2 = this.applyInverseTo(Vector3D.PLUS_J);
            if (v2.getZ().getReal() < -0.9999999999 || v2.getZ().getReal() > 0.9999999999) {
                throw new CardanEulerSingularityException(true);
            }
            return this.buildArray(((FieldElement<RealFieldElement<T>>)v2.getX()).negate().atan2(v2.getY()), v2.getZ().asin(), ((FieldElement<RealFieldElement<T>>)v1.getX()).negate().atan2(v1.getZ()));
        }
        else if (order == RotationOrder.ZYX) {
            final FieldVector3D<T> v1 = this.applyTo(Vector3D.PLUS_K);
            final FieldVector3D<T> v2 = this.applyInverseTo(Vector3D.PLUS_I);
            if (v2.getZ().getReal() < -0.9999999999 || v2.getZ().getReal() > 0.9999999999) {
                throw new CardanEulerSingularityException(true);
            }
            return this.buildArray(v2.getY().atan2(v2.getX()), ((RealFieldElement<RealFieldElement<T>>)v2.getZ()).asin().negate(), v1.getY().atan2(v1.getZ()));
        }
        else if (order == RotationOrder.XYX) {
            final FieldVector3D<T> v1 = this.applyTo(Vector3D.PLUS_I);
            final FieldVector3D<T> v2 = this.applyInverseTo(Vector3D.PLUS_I);
            if (v2.getX().getReal() < -0.9999999999 || v2.getX().getReal() > 0.9999999999) {
                throw new CardanEulerSingularityException(false);
            }
            return this.buildArray(v2.getY().atan2(v2.getZ().negate()), v2.getX().acos(), v1.getY().atan2(v1.getZ()));
        }
        else if (order == RotationOrder.XZX) {
            final FieldVector3D<T> v1 = this.applyTo(Vector3D.PLUS_I);
            final FieldVector3D<T> v2 = this.applyInverseTo(Vector3D.PLUS_I);
            if (v2.getX().getReal() < -0.9999999999 || v2.getX().getReal() > 0.9999999999) {
                throw new CardanEulerSingularityException(false);
            }
            return this.buildArray(v2.getZ().atan2(v2.getY()), v2.getX().acos(), v1.getZ().atan2(v1.getY().negate()));
        }
        else if (order == RotationOrder.YXY) {
            final FieldVector3D<T> v1 = this.applyTo(Vector3D.PLUS_J);
            final FieldVector3D<T> v2 = this.applyInverseTo(Vector3D.PLUS_J);
            if (v2.getY().getReal() < -0.9999999999 || v2.getY().getReal() > 0.9999999999) {
                throw new CardanEulerSingularityException(false);
            }
            return this.buildArray(v2.getX().atan2(v2.getZ()), v2.getY().acos(), v1.getX().atan2(v1.getZ().negate()));
        }
        else if (order == RotationOrder.YZY) {
            final FieldVector3D<T> v1 = this.applyTo(Vector3D.PLUS_J);
            final FieldVector3D<T> v2 = this.applyInverseTo(Vector3D.PLUS_J);
            if (v2.getY().getReal() < -0.9999999999 || v2.getY().getReal() > 0.9999999999) {
                throw new CardanEulerSingularityException(false);
            }
            return this.buildArray(v2.getZ().atan2(v2.getX().negate()), v2.getY().acos(), v1.getZ().atan2(v1.getX()));
        }
        else if (order == RotationOrder.ZXZ) {
            final FieldVector3D<T> v1 = this.applyTo(Vector3D.PLUS_K);
            final FieldVector3D<T> v2 = this.applyInverseTo(Vector3D.PLUS_K);
            if (v2.getZ().getReal() < -0.9999999999 || v2.getZ().getReal() > 0.9999999999) {
                throw new CardanEulerSingularityException(false);
            }
            return this.buildArray(v2.getX().atan2(v2.getY().negate()), v2.getZ().acos(), v1.getX().atan2(v1.getY()));
        }
        else {
            final FieldVector3D<T> v1 = this.applyTo(Vector3D.PLUS_K);
            final FieldVector3D<T> v2 = this.applyInverseTo(Vector3D.PLUS_K);
            if (v2.getZ().getReal() < -0.9999999999 || v2.getZ().getReal() > 0.9999999999) {
                throw new CardanEulerSingularityException(false);
            }
            return this.buildArray(v2.getY().atan2(v2.getX()), v2.getZ().acos(), v1.getY().atan2(v1.getX().negate()));
        }
    }
    
    private T[] buildArray(final T a0, final T a1, final T a2) {
        final T[] array = MathArrays.buildArray(a0.getField(), 3);
        array[0] = a0;
        array[1] = a1;
        array[2] = a2;
        return array;
    }
    
    private FieldVector3D<T> vector(final double x, final double y, final double z) {
        final T zero = this.q0.getField().getZero();
        return new FieldVector3D<T>(zero.add(x), zero.add(y), zero.add(z));
    }
    
    public T[][] getMatrix() {
        final T q0q0 = this.q0.multiply(this.q0);
        final T q0q2 = this.q0.multiply(this.q1);
        final T q0q3 = this.q0.multiply(this.q2);
        final T q0q4 = this.q0.multiply(this.q3);
        final T q1q1 = this.q1.multiply(this.q1);
        final T q1q2 = this.q1.multiply(this.q2);
        final T q1q3 = this.q1.multiply(this.q3);
        final T q2q2 = this.q2.multiply(this.q2);
        final T q2q3 = this.q2.multiply(this.q3);
        final T q3q3 = this.q3.multiply(this.q3);
        final T[][] m = MathArrays.buildArray(this.q0.getField(), 3, 3);
        m[0][0] = ((FieldElement<RealFieldElement<T>>)q0q0.add(q1q1)).multiply(2).subtract(1.0);
        m[1][0] = q1q2.subtract(q0q4).multiply(2);
        m[2][0] = q1q3.add(q0q3).multiply(2);
        m[0][1] = q1q2.add(q0q4).multiply(2);
        m[1][1] = ((FieldElement<RealFieldElement<T>>)q0q0.add(q2q2)).multiply(2).subtract(1.0);
        m[2][1] = q2q3.subtract(q0q2).multiply(2);
        m[0][2] = q1q3.subtract(q0q3).multiply(2);
        m[1][2] = q2q3.add(q0q2).multiply(2);
        m[2][2] = ((FieldElement<RealFieldElement<T>>)q0q0.add(q3q3)).multiply(2).subtract(1.0);
        return m;
    }
    
    public Rotation toRotation() {
        return new Rotation(this.q0.getReal(), this.q1.getReal(), this.q2.getReal(), this.q3.getReal(), false);
    }
    
    public FieldVector3D<T> applyTo(final FieldVector3D<T> u) {
        final T x = u.getX();
        final T y = u.getY();
        final T z = u.getZ();
        final T s = this.q1.multiply(x).add(this.q2.multiply(y)).add(this.q3.multiply(z));
        return new FieldVector3D<T>(((FieldElement<RealFieldElement<T>>)this.q0.multiply(x.multiply(this.q0).subtract(this.q2.multiply(z).subtract(this.q3.multiply(y)))).add(s.multiply(this.q1))).multiply(2).subtract(x), ((FieldElement<RealFieldElement<T>>)this.q0.multiply(y.multiply(this.q0).subtract(this.q3.multiply(x).subtract(this.q1.multiply(z)))).add(s.multiply(this.q2))).multiply(2).subtract(y), ((FieldElement<RealFieldElement<T>>)this.q0.multiply(z.multiply(this.q0).subtract(this.q1.multiply(y).subtract(this.q2.multiply(x)))).add(s.multiply(this.q3))).multiply(2).subtract(z));
    }
    
    public FieldVector3D<T> applyTo(final Vector3D u) {
        final double x = u.getX();
        final double y = u.getY();
        final double z = u.getZ();
        final T s = (T)((RealFieldElement<RealFieldElement<RealFieldElement>>)this.q1).multiply(x).add(((RealFieldElement<RealFieldElement>)this.q2).multiply(y)).add(((RealFieldElement<Object>)this.q3).multiply(z));
        return new FieldVector3D<T>((T)((FieldElement<RealFieldElement<RealFieldElement>>)this.q0.multiply(((RealFieldElement<RealFieldElement<T>>)this.q0).multiply(x).subtract((T)((RealFieldElement<RealFieldElement<Object>>)this.q2).multiply(z).subtract(((RealFieldElement<Object>)this.q3).multiply(y)))).add(s.multiply(this.q1))).multiply(2).subtract(x), (T)((FieldElement<RealFieldElement<RealFieldElement>>)this.q0.multiply(((RealFieldElement<RealFieldElement<T>>)this.q0).multiply(y).subtract((T)((RealFieldElement<RealFieldElement<Object>>)this.q3).multiply(x).subtract(((RealFieldElement<Object>)this.q1).multiply(z)))).add(s.multiply(this.q2))).multiply(2).subtract(y), (T)((FieldElement<RealFieldElement<RealFieldElement>>)this.q0.multiply(((RealFieldElement<RealFieldElement<T>>)this.q0).multiply(z).subtract((T)((RealFieldElement<RealFieldElement<Object>>)this.q1).multiply(y).subtract(((RealFieldElement<Object>)this.q2).multiply(x)))).add(s.multiply(this.q3))).multiply(2).subtract(z));
    }
    
    public void applyTo(final T[] in, final T[] out) {
        final T x = in[0];
        final T y = in[1];
        final T z = in[2];
        final T s = this.q1.multiply(x).add(this.q2.multiply(y)).add(this.q3.multiply(z));
        out[0] = ((FieldElement<RealFieldElement<T>>)this.q0.multiply(x.multiply(this.q0).subtract(this.q2.multiply(z).subtract(this.q3.multiply(y)))).add(s.multiply(this.q1))).multiply(2).subtract(x);
        out[1] = ((FieldElement<RealFieldElement<T>>)this.q0.multiply(y.multiply(this.q0).subtract(this.q3.multiply(x).subtract(this.q1.multiply(z)))).add(s.multiply(this.q2))).multiply(2).subtract(y);
        out[2] = ((FieldElement<RealFieldElement<T>>)this.q0.multiply(z.multiply(this.q0).subtract(this.q1.multiply(y).subtract(this.q2.multiply(x)))).add(s.multiply(this.q3))).multiply(2).subtract(z);
    }
    
    public void applyTo(final double[] in, final T[] out) {
        final double x = in[0];
        final double y = in[1];
        final double z = in[2];
        final T s = (T)((RealFieldElement<RealFieldElement<RealFieldElement>>)this.q1).multiply(x).add(((RealFieldElement<RealFieldElement>)this.q2).multiply(y)).add(((RealFieldElement<Object>)this.q3).multiply(z));
        out[0] = ((FieldElement<RealFieldElement<T>>)this.q0.multiply(((RealFieldElement<RealFieldElement<T>>)this.q0).multiply(x).subtract((T)((RealFieldElement<RealFieldElement<Object>>)this.q2).multiply(z).subtract(((RealFieldElement<Object>)this.q3).multiply(y)))).add(s.multiply(this.q1))).multiply(2).subtract(x);
        out[1] = ((FieldElement<RealFieldElement<T>>)this.q0.multiply(((RealFieldElement<RealFieldElement<T>>)this.q0).multiply(y).subtract((T)((RealFieldElement<RealFieldElement<Object>>)this.q3).multiply(x).subtract(((RealFieldElement<Object>)this.q1).multiply(z)))).add(s.multiply(this.q2))).multiply(2).subtract(y);
        out[2] = ((FieldElement<RealFieldElement<T>>)this.q0.multiply(((RealFieldElement<RealFieldElement<T>>)this.q0).multiply(z).subtract((T)((RealFieldElement<RealFieldElement<Object>>)this.q1).multiply(y).subtract(((RealFieldElement<Object>)this.q2).multiply(x)))).add(s.multiply(this.q3))).multiply(2).subtract(z);
    }
    
    public static <T extends RealFieldElement<T>> FieldVector3D<T> applyTo(final Rotation r, final FieldVector3D<T> u) {
        final T x = u.getX();
        final T y = u.getY();
        final T z = u.getZ();
        final T s = (T)((RealFieldElement<RealFieldElement<RealFieldElement>>)x).multiply(r.getQ1()).add(((RealFieldElement<RealFieldElement>)y).multiply(r.getQ2())).add(((RealFieldElement<Object>)z).multiply(r.getQ3()));
        return new FieldVector3D<T>((T)((RealFieldElement)((RealFieldElement)((RealFieldElement)((RealFieldElement<RealFieldElement<RealFieldElement>>)x).multiply(r.getQ0()).subtract(((RealFieldElement<RealFieldElement<Object>>)z).multiply(r.getQ2()).subtract(((RealFieldElement<RealFieldElement>)y).multiply(r.getQ3()))).multiply(r.getQ0())).add(((RealFieldElement<Object>)s).multiply(r.getQ1()))).multiply(2)).subtract(x), (T)((RealFieldElement)((RealFieldElement)((RealFieldElement)((RealFieldElement<RealFieldElement<RealFieldElement>>)y).multiply(r.getQ0()).subtract(((RealFieldElement<RealFieldElement<Object>>)x).multiply(r.getQ3()).subtract(((RealFieldElement<RealFieldElement>)z).multiply(r.getQ1()))).multiply(r.getQ0())).add(((RealFieldElement<Object>)s).multiply(r.getQ2()))).multiply(2)).subtract(y), (T)((RealFieldElement)((RealFieldElement)((RealFieldElement)((RealFieldElement<RealFieldElement<RealFieldElement>>)z).multiply(r.getQ0()).subtract(((RealFieldElement<RealFieldElement<Object>>)y).multiply(r.getQ1()).subtract(((RealFieldElement<RealFieldElement>)x).multiply(r.getQ2()))).multiply(r.getQ0())).add(((RealFieldElement<Object>)s).multiply(r.getQ3()))).multiply(2)).subtract(z));
    }
    
    public FieldVector3D<T> applyInverseTo(final FieldVector3D<T> u) {
        final T x = u.getX();
        final T y = u.getY();
        final T z = u.getZ();
        final T s = this.q1.multiply(x).add(this.q2.multiply(y)).add(this.q3.multiply(z));
        final T m0 = this.q0.negate();
        return new FieldVector3D<T>(((FieldElement<RealFieldElement<T>>)m0.multiply(x.multiply(m0).subtract(this.q2.multiply(z).subtract(this.q3.multiply(y)))).add(s.multiply(this.q1))).multiply(2).subtract(x), ((FieldElement<RealFieldElement<T>>)m0.multiply(y.multiply(m0).subtract(this.q3.multiply(x).subtract(this.q1.multiply(z)))).add(s.multiply(this.q2))).multiply(2).subtract(y), ((FieldElement<RealFieldElement<T>>)m0.multiply(z.multiply(m0).subtract(this.q1.multiply(y).subtract(this.q2.multiply(x)))).add(s.multiply(this.q3))).multiply(2).subtract(z));
    }
    
    public FieldVector3D<T> applyInverseTo(final Vector3D u) {
        final double x = u.getX();
        final double y = u.getY();
        final double z = u.getZ();
        final T s = (T)((RealFieldElement<RealFieldElement<RealFieldElement>>)this.q1).multiply(x).add(((RealFieldElement<RealFieldElement>)this.q2).multiply(y)).add(((RealFieldElement<Object>)this.q3).multiply(z));
        final T m0 = this.q0.negate();
        return new FieldVector3D<T>((T)((FieldElement<RealFieldElement<RealFieldElement>>)m0.multiply(((RealFieldElement<RealFieldElement<T>>)m0).multiply(x).subtract((T)((RealFieldElement<RealFieldElement<Object>>)this.q2).multiply(z).subtract(((RealFieldElement<Object>)this.q3).multiply(y)))).add(s.multiply(this.q1))).multiply(2).subtract(x), (T)((FieldElement<RealFieldElement<RealFieldElement>>)m0.multiply(((RealFieldElement<RealFieldElement<T>>)m0).multiply(y).subtract((T)((RealFieldElement<RealFieldElement<Object>>)this.q3).multiply(x).subtract(((RealFieldElement<Object>)this.q1).multiply(z)))).add(s.multiply(this.q2))).multiply(2).subtract(y), (T)((FieldElement<RealFieldElement<RealFieldElement>>)m0.multiply(((RealFieldElement<RealFieldElement<T>>)m0).multiply(z).subtract((T)((RealFieldElement<RealFieldElement<Object>>)this.q1).multiply(y).subtract(((RealFieldElement<Object>)this.q2).multiply(x)))).add(s.multiply(this.q3))).multiply(2).subtract(z));
    }
    
    public void applyInverseTo(final T[] in, final T[] out) {
        final T x = in[0];
        final T y = in[1];
        final T z = in[2];
        final T s = this.q1.multiply(x).add(this.q2.multiply(y)).add(this.q3.multiply(z));
        final T m0 = this.q0.negate();
        out[0] = ((FieldElement<RealFieldElement<T>>)m0.multiply(x.multiply(m0).subtract(this.q2.multiply(z).subtract(this.q3.multiply(y)))).add(s.multiply(this.q1))).multiply(2).subtract(x);
        out[1] = ((FieldElement<RealFieldElement<T>>)m0.multiply(y.multiply(m0).subtract(this.q3.multiply(x).subtract(this.q1.multiply(z)))).add(s.multiply(this.q2))).multiply(2).subtract(y);
        out[2] = ((FieldElement<RealFieldElement<T>>)m0.multiply(z.multiply(m0).subtract(this.q1.multiply(y).subtract(this.q2.multiply(x)))).add(s.multiply(this.q3))).multiply(2).subtract(z);
    }
    
    public void applyInverseTo(final double[] in, final T[] out) {
        final double x = in[0];
        final double y = in[1];
        final double z = in[2];
        final T s = (T)((RealFieldElement<RealFieldElement<RealFieldElement>>)this.q1).multiply(x).add(((RealFieldElement<RealFieldElement>)this.q2).multiply(y)).add(((RealFieldElement<Object>)this.q3).multiply(z));
        final T m0 = this.q0.negate();
        out[0] = ((FieldElement<RealFieldElement<T>>)m0.multiply(((RealFieldElement<RealFieldElement<T>>)m0).multiply(x).subtract((T)((RealFieldElement<RealFieldElement<Object>>)this.q2).multiply(z).subtract(((RealFieldElement<Object>)this.q3).multiply(y)))).add(s.multiply(this.q1))).multiply(2).subtract(x);
        out[1] = ((FieldElement<RealFieldElement<T>>)m0.multiply(((RealFieldElement<RealFieldElement<T>>)m0).multiply(y).subtract((T)((RealFieldElement<RealFieldElement<Object>>)this.q3).multiply(x).subtract(((RealFieldElement<Object>)this.q1).multiply(z)))).add(s.multiply(this.q2))).multiply(2).subtract(y);
        out[2] = ((FieldElement<RealFieldElement<T>>)m0.multiply(((RealFieldElement<RealFieldElement<T>>)m0).multiply(z).subtract((T)((RealFieldElement<RealFieldElement<Object>>)this.q1).multiply(y).subtract(((RealFieldElement<Object>)this.q2).multiply(x)))).add(s.multiply(this.q3))).multiply(2).subtract(z);
    }
    
    public static <T extends RealFieldElement<T>> FieldVector3D<T> applyInverseTo(final Rotation r, final FieldVector3D<T> u) {
        final T x = u.getX();
        final T y = u.getY();
        final T z = u.getZ();
        final T s = (T)((RealFieldElement<RealFieldElement<RealFieldElement>>)x).multiply(r.getQ1()).add(((RealFieldElement<RealFieldElement>)y).multiply(r.getQ2())).add(((RealFieldElement<Object>)z).multiply(r.getQ3()));
        final double m0 = -r.getQ0();
        return new FieldVector3D<T>((T)((RealFieldElement)((RealFieldElement)((RealFieldElement)((RealFieldElement<RealFieldElement<RealFieldElement>>)x).multiply(m0).subtract(((RealFieldElement<RealFieldElement<Object>>)z).multiply(r.getQ2()).subtract(((RealFieldElement<RealFieldElement>)y).multiply(r.getQ3()))).multiply(m0)).add(((RealFieldElement<Object>)s).multiply(r.getQ1()))).multiply(2)).subtract(x), (T)((RealFieldElement)((RealFieldElement)((RealFieldElement)((RealFieldElement<RealFieldElement<RealFieldElement>>)y).multiply(m0).subtract(((RealFieldElement<RealFieldElement<Object>>)x).multiply(r.getQ3()).subtract(((RealFieldElement<RealFieldElement>)z).multiply(r.getQ1()))).multiply(m0)).add(((RealFieldElement<Object>)s).multiply(r.getQ2()))).multiply(2)).subtract(y), (T)((RealFieldElement)((RealFieldElement)((RealFieldElement)((RealFieldElement<RealFieldElement<RealFieldElement>>)z).multiply(m0).subtract(((RealFieldElement<RealFieldElement<Object>>)y).multiply(r.getQ1()).subtract(((RealFieldElement<RealFieldElement>)x).multiply(r.getQ2()))).multiply(m0)).add(((RealFieldElement<Object>)s).multiply(r.getQ3()))).multiply(2)).subtract(z));
    }
    
    public FieldRotation<T> applyTo(final FieldRotation<T> r) {
        return this.compose(r, RotationConvention.VECTOR_OPERATOR);
    }
    
    public FieldRotation<T> compose(final FieldRotation<T> r, final RotationConvention convention) {
        return (convention == RotationConvention.VECTOR_OPERATOR) ? this.composeInternal(r) : r.composeInternal(this);
    }
    
    private FieldRotation<T> composeInternal(final FieldRotation<T> r) {
        return new FieldRotation<T>(r.q0.multiply(this.q0).subtract(r.q1.multiply(this.q1).add(r.q2.multiply(this.q2)).add(r.q3.multiply(this.q3))), r.q1.multiply(this.q0).add(r.q0.multiply(this.q1)).add(r.q2.multiply(this.q3).subtract(r.q3.multiply(this.q2))), r.q2.multiply(this.q0).add(r.q0.multiply(this.q2)).add(r.q3.multiply(this.q1).subtract(r.q1.multiply(this.q3))), r.q3.multiply(this.q0).add(r.q0.multiply(this.q3)).add(r.q1.multiply(this.q2).subtract(r.q2.multiply(this.q1))), false);
    }
    
    public FieldRotation<T> applyTo(final Rotation r) {
        return this.compose(r, RotationConvention.VECTOR_OPERATOR);
    }
    
    public FieldRotation<T> compose(final Rotation r, final RotationConvention convention) {
        return (convention == RotationConvention.VECTOR_OPERATOR) ? this.composeInternal(r) : applyTo(r, this);
    }
    
    private FieldRotation<T> composeInternal(final Rotation r) {
        return new FieldRotation<T>((T)((RealFieldElement<RealFieldElement<RealFieldElement>>)this.q0).multiply(r.getQ0()).subtract((RealFieldElement)((RealFieldElement<RealFieldElement<RealFieldElement>>)this.q1).multiply(r.getQ1()).add(((RealFieldElement<RealFieldElement>)this.q2).multiply(r.getQ2())).add(((RealFieldElement<Object>)this.q3).multiply(r.getQ3()))), (T)((RealFieldElement<RealFieldElement<RealFieldElement>>)this.q0).multiply(r.getQ1()).add(((RealFieldElement<RealFieldElement>)this.q1).multiply(r.getQ0())).add(((RealFieldElement<RealFieldElement<Object>>)this.q3).multiply(r.getQ2()).subtract(((RealFieldElement<Object>)this.q2).multiply(r.getQ3()))), (T)((RealFieldElement<RealFieldElement<RealFieldElement>>)this.q0).multiply(r.getQ2()).add(((RealFieldElement<RealFieldElement>)this.q2).multiply(r.getQ0())).add(((RealFieldElement<RealFieldElement<Object>>)this.q1).multiply(r.getQ3()).subtract(((RealFieldElement<Object>)this.q3).multiply(r.getQ1()))), (T)((RealFieldElement<RealFieldElement<RealFieldElement>>)this.q0).multiply(r.getQ3()).add(((RealFieldElement<RealFieldElement>)this.q3).multiply(r.getQ0())).add(((RealFieldElement<RealFieldElement<Object>>)this.q2).multiply(r.getQ1()).subtract(((RealFieldElement<Object>)this.q1).multiply(r.getQ2()))), false);
    }
    
    public static <T extends RealFieldElement<T>> FieldRotation<T> applyTo(final Rotation r1, final FieldRotation<T> rInner) {
        return new FieldRotation<T>((T)((RealFieldElement<RealFieldElement<RealFieldElement>>)rInner.q0).multiply(r1.getQ0()).subtract((RealFieldElement)((RealFieldElement<RealFieldElement<RealFieldElement>>)rInner.q1).multiply(r1.getQ1()).add(((RealFieldElement<RealFieldElement>)rInner.q2).multiply(r1.getQ2())).add(((RealFieldElement<Object>)rInner.q3).multiply(r1.getQ3()))), (T)((RealFieldElement<RealFieldElement<RealFieldElement>>)rInner.q1).multiply(r1.getQ0()).add(((RealFieldElement<RealFieldElement>)rInner.q0).multiply(r1.getQ1())).add(((RealFieldElement<RealFieldElement<Object>>)rInner.q2).multiply(r1.getQ3()).subtract(((RealFieldElement<Object>)rInner.q3).multiply(r1.getQ2()))), (T)((RealFieldElement<RealFieldElement<RealFieldElement>>)rInner.q2).multiply(r1.getQ0()).add(((RealFieldElement<RealFieldElement>)rInner.q0).multiply(r1.getQ2())).add(((RealFieldElement<RealFieldElement<Object>>)rInner.q3).multiply(r1.getQ1()).subtract(((RealFieldElement<Object>)rInner.q1).multiply(r1.getQ3()))), (T)((RealFieldElement<RealFieldElement<RealFieldElement>>)rInner.q3).multiply(r1.getQ0()).add(((RealFieldElement<RealFieldElement>)rInner.q0).multiply(r1.getQ3())).add(((RealFieldElement<RealFieldElement<Object>>)rInner.q1).multiply(r1.getQ2()).subtract(((RealFieldElement<Object>)rInner.q2).multiply(r1.getQ1()))), false);
    }
    
    public FieldRotation<T> applyInverseTo(final FieldRotation<T> r) {
        return this.composeInverse(r, RotationConvention.VECTOR_OPERATOR);
    }
    
    public FieldRotation<T> composeInverse(final FieldRotation<T> r, final RotationConvention convention) {
        return (convention == RotationConvention.VECTOR_OPERATOR) ? this.composeInverseInternal(r) : r.composeInternal(this.revert());
    }
    
    private FieldRotation<T> composeInverseInternal(final FieldRotation<T> r) {
        return new FieldRotation<T>(r.q0.multiply(this.q0).add(r.q1.multiply(this.q1).add(r.q2.multiply(this.q2)).add(r.q3.multiply(this.q3))).negate(), r.q0.multiply(this.q1).add(r.q2.multiply(this.q3).subtract(r.q3.multiply(this.q2))).subtract(r.q1.multiply(this.q0)), r.q0.multiply(this.q2).add(r.q3.multiply(this.q1).subtract(r.q1.multiply(this.q3))).subtract(r.q2.multiply(this.q0)), r.q0.multiply(this.q3).add(r.q1.multiply(this.q2).subtract(r.q2.multiply(this.q1))).subtract(r.q3.multiply(this.q0)), false);
    }
    
    public FieldRotation<T> applyInverseTo(final Rotation r) {
        return this.composeInverse(r, RotationConvention.VECTOR_OPERATOR);
    }
    
    public FieldRotation<T> composeInverse(final Rotation r, final RotationConvention convention) {
        return (convention == RotationConvention.VECTOR_OPERATOR) ? this.composeInverseInternal(r) : applyTo(r, this.revert());
    }
    
    private FieldRotation<T> composeInverseInternal(final Rotation r) {
        return new FieldRotation<T>((T)((RealFieldElement<RealFieldElement<RealFieldElement>>)this.q0).multiply(r.getQ0()).add((RealFieldElement<RealFieldElement>)((RealFieldElement<RealFieldElement<RealFieldElement>>)this.q1).multiply(r.getQ1()).add(((RealFieldElement<RealFieldElement>)this.q2).multiply(r.getQ2())).add(((RealFieldElement<Object>)this.q3).multiply(r.getQ3()))).negate(), (T)((RealFieldElement<RealFieldElement<RealFieldElement>>)this.q1).multiply(r.getQ0()).add(((RealFieldElement<RealFieldElement<Object>>)this.q3).multiply(r.getQ2()).subtract(((RealFieldElement<RealFieldElement>)this.q2).multiply(r.getQ3()))).subtract(((RealFieldElement<Object>)this.q0).multiply(r.getQ1())), (T)((RealFieldElement<RealFieldElement<RealFieldElement>>)this.q2).multiply(r.getQ0()).add(((RealFieldElement<RealFieldElement<Object>>)this.q1).multiply(r.getQ3()).subtract(((RealFieldElement<RealFieldElement>)this.q3).multiply(r.getQ1()))).subtract(((RealFieldElement<Object>)this.q0).multiply(r.getQ2())), (T)((RealFieldElement<RealFieldElement<RealFieldElement>>)this.q3).multiply(r.getQ0()).add(((RealFieldElement<RealFieldElement<Object>>)this.q2).multiply(r.getQ1()).subtract(((RealFieldElement<RealFieldElement>)this.q1).multiply(r.getQ2()))).subtract(((RealFieldElement<Object>)this.q0).multiply(r.getQ3())), false);
    }
    
    public static <T extends RealFieldElement<T>> FieldRotation<T> applyInverseTo(final Rotation rOuter, final FieldRotation<T> rInner) {
        return new FieldRotation<T>((T)((RealFieldElement<RealFieldElement<RealFieldElement>>)rInner.q0).multiply(rOuter.getQ0()).add((RealFieldElement<RealFieldElement>)((RealFieldElement<RealFieldElement<RealFieldElement>>)rInner.q1).multiply(rOuter.getQ1()).add(((RealFieldElement<RealFieldElement>)rInner.q2).multiply(rOuter.getQ2())).add(((RealFieldElement<Object>)rInner.q3).multiply(rOuter.getQ3()))).negate(), (T)((RealFieldElement<RealFieldElement<RealFieldElement>>)rInner.q0).multiply(rOuter.getQ1()).add(((RealFieldElement<RealFieldElement<Object>>)rInner.q2).multiply(rOuter.getQ3()).subtract(((RealFieldElement<RealFieldElement>)rInner.q3).multiply(rOuter.getQ2()))).subtract(((RealFieldElement<Object>)rInner.q1).multiply(rOuter.getQ0())), (T)((RealFieldElement<RealFieldElement<RealFieldElement>>)rInner.q0).multiply(rOuter.getQ2()).add(((RealFieldElement<RealFieldElement<Object>>)rInner.q3).multiply(rOuter.getQ1()).subtract(((RealFieldElement<RealFieldElement>)rInner.q1).multiply(rOuter.getQ3()))).subtract(((RealFieldElement<Object>)rInner.q2).multiply(rOuter.getQ0())), (T)((RealFieldElement<RealFieldElement<RealFieldElement>>)rInner.q0).multiply(rOuter.getQ3()).add(((RealFieldElement<RealFieldElement<Object>>)rInner.q1).multiply(rOuter.getQ2()).subtract(((RealFieldElement<RealFieldElement>)rInner.q2).multiply(rOuter.getQ1()))).subtract(((RealFieldElement<Object>)rInner.q3).multiply(rOuter.getQ0())), false);
    }
    
    private T[][] orthogonalizeMatrix(final T[][] m, final double threshold) throws NotARotationMatrixException {
        T x00 = m[0][0];
        T x2 = m[0][1];
        T x3 = m[0][2];
        T x4 = m[1][0];
        T x5 = m[1][1];
        T x6 = m[1][2];
        T x7 = m[2][0];
        T x8 = m[2][1];
        T x9 = m[2][2];
        double fn = 0.0;
        final T[][] o = MathArrays.buildArray(m[0][0].getField(), 3, 3);
        int i = 0;
        while (++i < 11) {
            final T mx00 = m[0][0].multiply(x00).add(m[1][0].multiply(x4)).add(m[2][0].multiply(x7));
            final T mx2 = m[0][1].multiply(x00).add(m[1][1].multiply(x4)).add(m[2][1].multiply(x7));
            final T mx3 = m[0][2].multiply(x00).add(m[1][2].multiply(x4)).add(m[2][2].multiply(x7));
            final T mx4 = m[0][0].multiply(x2).add(m[1][0].multiply(x5)).add(m[2][0].multiply(x8));
            final T mx5 = m[0][1].multiply(x2).add(m[1][1].multiply(x5)).add(m[2][1].multiply(x8));
            final T mx6 = m[0][2].multiply(x2).add(m[1][2].multiply(x5)).add(m[2][2].multiply(x8));
            final T mx7 = m[0][0].multiply(x3).add(m[1][0].multiply(x6)).add(m[2][0].multiply(x9));
            final T mx8 = m[0][1].multiply(x3).add(m[1][1].multiply(x6)).add(m[2][1].multiply(x9));
            final T mx9 = m[0][2].multiply(x3).add(m[1][2].multiply(x6)).add(m[2][2].multiply(x9));
            o[0][0] = x00.subtract(x00.multiply(mx00).add(x2.multiply(mx2)).add(x3.multiply(mx3)).subtract(m[0][0]).multiply(0.5));
            o[0][1] = x2.subtract(x00.multiply(mx4).add(x2.multiply(mx5)).add(x3.multiply(mx6)).subtract(m[0][1]).multiply(0.5));
            o[0][2] = x3.subtract(x00.multiply(mx7).add(x2.multiply(mx8)).add(x3.multiply(mx9)).subtract(m[0][2]).multiply(0.5));
            o[1][0] = x4.subtract(x4.multiply(mx00).add(x5.multiply(mx2)).add(x6.multiply(mx3)).subtract(m[1][0]).multiply(0.5));
            o[1][1] = x5.subtract(x4.multiply(mx4).add(x5.multiply(mx5)).add(x6.multiply(mx6)).subtract(m[1][1]).multiply(0.5));
            o[1][2] = x6.subtract(x4.multiply(mx7).add(x5.multiply(mx8)).add(x6.multiply(mx9)).subtract(m[1][2]).multiply(0.5));
            o[2][0] = x7.subtract(x7.multiply(mx00).add(x8.multiply(mx2)).add(x9.multiply(mx3)).subtract(m[2][0]).multiply(0.5));
            o[2][1] = x8.subtract(x7.multiply(mx4).add(x8.multiply(mx5)).add(x9.multiply(mx6)).subtract(m[2][1]).multiply(0.5));
            o[2][2] = x9.subtract(x7.multiply(mx7).add(x8.multiply(mx8)).add(x9.multiply(mx9)).subtract(m[2][2]).multiply(0.5));
            final double corr00 = o[0][0].getReal() - m[0][0].getReal();
            final double corr2 = o[0][1].getReal() - m[0][1].getReal();
            final double corr3 = o[0][2].getReal() - m[0][2].getReal();
            final double corr4 = o[1][0].getReal() - m[1][0].getReal();
            final double corr5 = o[1][1].getReal() - m[1][1].getReal();
            final double corr6 = o[1][2].getReal() - m[1][2].getReal();
            final double corr7 = o[2][0].getReal() - m[2][0].getReal();
            final double corr8 = o[2][1].getReal() - m[2][1].getReal();
            final double corr9 = o[2][2].getReal() - m[2][2].getReal();
            final double fn2 = corr00 * corr00 + corr2 * corr2 + corr3 * corr3 + corr4 * corr4 + corr5 * corr5 + corr6 * corr6 + corr7 * corr7 + corr8 * corr8 + corr9 * corr9;
            if (FastMath.abs(fn2 - fn) <= threshold) {
                return o;
            }
            x00 = o[0][0];
            x2 = o[0][1];
            x3 = o[0][2];
            x4 = o[1][0];
            x5 = o[1][1];
            x6 = o[1][2];
            x7 = o[2][0];
            x8 = o[2][1];
            x9 = o[2][2];
            fn = fn2;
        }
        throw new NotARotationMatrixException(LocalizedFormats.UNABLE_TO_ORTHOGONOLIZE_MATRIX, new Object[] { i - 1 });
    }
    
    public static <T extends RealFieldElement<T>> T distance(final FieldRotation<T> r1, final FieldRotation<T> r2) {
        return r1.composeInverseInternal(r2).getAngle();
    }
}
