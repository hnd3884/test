package com.jhlabs.vecmath;

public class Matrix4f
{
    public float m00;
    public float m01;
    public float m02;
    public float m03;
    public float m10;
    public float m11;
    public float m12;
    public float m13;
    public float m20;
    public float m21;
    public float m22;
    public float m23;
    public float m30;
    public float m31;
    public float m32;
    public float m33;
    
    public Matrix4f() {
        this.setIdentity();
    }
    
    public Matrix4f(final Matrix4f m) {
        this.set(m);
    }
    
    public Matrix4f(final float[] m) {
        this.set(m);
    }
    
    public void set(final Matrix4f m) {
        this.m00 = m.m00;
        this.m01 = m.m01;
        this.m02 = m.m02;
        this.m03 = m.m03;
        this.m10 = m.m10;
        this.m11 = m.m11;
        this.m12 = m.m12;
        this.m13 = m.m13;
        this.m20 = m.m20;
        this.m21 = m.m21;
        this.m22 = m.m22;
        this.m23 = m.m23;
        this.m30 = m.m30;
        this.m31 = m.m31;
        this.m32 = m.m32;
        this.m33 = m.m33;
    }
    
    public void set(final float[] m) {
        this.m00 = m[0];
        this.m01 = m[1];
        this.m02 = m[2];
        this.m03 = m[3];
        this.m10 = m[4];
        this.m11 = m[5];
        this.m12 = m[6];
        this.m13 = m[7];
        this.m20 = m[8];
        this.m21 = m[9];
        this.m22 = m[10];
        this.m23 = m[11];
        this.m30 = m[12];
        this.m31 = m[13];
        this.m32 = m[14];
        this.m33 = m[15];
    }
    
    public void get(final Matrix4f m) {
        m.m00 = this.m00;
        m.m01 = this.m01;
        m.m02 = this.m02;
        m.m03 = this.m03;
        m.m10 = this.m10;
        m.m11 = this.m11;
        m.m12 = this.m12;
        m.m13 = this.m13;
        m.m20 = this.m20;
        m.m21 = this.m21;
        m.m22 = this.m22;
        m.m23 = this.m23;
        m.m30 = this.m30;
        m.m31 = this.m31;
        m.m32 = this.m32;
        m.m33 = this.m33;
    }
    
    public void get(final float[] m) {
        m[0] = this.m00;
        m[1] = this.m01;
        m[2] = this.m02;
        m[3] = this.m03;
        m[4] = this.m10;
        m[5] = this.m11;
        m[6] = this.m12;
        m[7] = this.m13;
        m[8] = this.m20;
        m[9] = this.m21;
        m[10] = this.m22;
        m[11] = this.m23;
        m[12] = this.m30;
        m[13] = this.m31;
        m[14] = this.m32;
        m[15] = this.m33;
    }
    
    public void setIdentity() {
        this.m00 = 1.0f;
        this.m01 = 0.0f;
        this.m02 = 0.0f;
        this.m03 = 0.0f;
        this.m10 = 0.0f;
        this.m11 = 1.0f;
        this.m12 = 0.0f;
        this.m13 = 0.0f;
        this.m20 = 0.0f;
        this.m21 = 0.0f;
        this.m22 = 1.0f;
        this.m23 = 0.0f;
        this.m30 = 0.0f;
        this.m31 = 0.0f;
        this.m32 = 0.0f;
        this.m33 = 1.0f;
    }
    
    public void mul(final Matrix4f m) {
        final float tm00 = this.m00;
        final float tm2 = this.m01;
        final float tm3 = this.m02;
        final float tm4 = this.m03;
        final float tm5 = this.m10;
        final float tm6 = this.m11;
        final float tm7 = this.m12;
        final float tm8 = this.m13;
        final float tm9 = this.m20;
        final float tm10 = this.m21;
        final float tm11 = this.m22;
        final float tm12 = this.m23;
        final float tm13 = this.m30;
        final float tm14 = this.m31;
        final float tm15 = this.m32;
        final float tm16 = this.m33;
        this.m00 = tm00 * m.m00 + tm5 * m.m01 + tm9 * m.m02 + tm13 * m.m03;
        this.m01 = tm2 * m.m00 + tm6 * m.m01 + tm10 * m.m02 + tm14 * m.m03;
        this.m02 = tm3 * m.m00 + tm7 * m.m01 + tm11 * m.m02 + tm15 * m.m03;
        this.m03 = tm4 * m.m00 + tm8 * m.m01 + tm12 * m.m02 + tm16 * m.m03;
        this.m10 = tm00 * m.m10 + tm5 * m.m11 + tm9 * m.m12 + tm13 * m.m13;
        this.m11 = tm2 * m.m10 + tm6 * m.m11 + tm10 * m.m12 + tm14 * m.m13;
        this.m12 = tm3 * m.m10 + tm7 * m.m11 + tm11 * m.m12 + tm15 * m.m13;
        this.m13 = tm4 * m.m10 + tm8 * m.m11 + tm12 * m.m12 + tm16 * m.m13;
        this.m20 = tm00 * m.m20 + tm5 * m.m21 + tm9 * m.m22 + tm13 * m.m23;
        this.m21 = tm2 * m.m20 + tm6 * m.m21 + tm10 * m.m22 + tm14 * m.m23;
        this.m22 = tm3 * m.m20 + tm7 * m.m21 + tm11 * m.m22 + tm15 * m.m23;
        this.m23 = tm4 * m.m20 + tm8 * m.m21 + tm12 * m.m22 + tm16 * m.m23;
        this.m30 = tm00 * m.m30 + tm5 * m.m31 + tm9 * m.m32 + tm13 * m.m33;
        this.m31 = tm2 * m.m30 + tm6 * m.m31 + tm10 * m.m32 + tm14 * m.m33;
        this.m32 = tm3 * m.m30 + tm7 * m.m31 + tm11 * m.m32 + tm15 * m.m33;
        this.m33 = tm4 * m.m30 + tm8 * m.m31 + tm12 * m.m32 + tm16 * m.m33;
    }
    
    public void invert() {
        final Matrix4f t = new Matrix4f(this);
        this.invert(t);
    }
    
    public void invert(final Matrix4f t) {
        this.m00 = t.m00;
        this.m01 = t.m10;
        this.m02 = t.m20;
        this.m03 = t.m03;
        this.m10 = t.m01;
        this.m11 = t.m11;
        this.m12 = t.m21;
        this.m13 = t.m13;
        this.m20 = t.m02;
        this.m21 = t.m12;
        this.m22 = t.m22;
        this.m23 = t.m23;
        this.m30 *= -1.0f;
        this.m31 *= -1.0f;
        this.m32 *= -1.0f;
        this.m33 = t.m33;
    }
    
    public void set(final AxisAngle4f a) {
        final float halfTheta = a.angle * 0.5f;
        final float cosHalfTheta = (float)Math.cos(halfTheta);
        final float sinHalfTheta = (float)Math.sin(halfTheta);
        this.set(new Quat4f(a.x * sinHalfTheta, a.y * sinHalfTheta, a.z * sinHalfTheta, cosHalfTheta));
    }
    
    public void set(final Quat4f q) {
        final float x2 = q.x + q.x;
        final float y2 = q.y + q.y;
        final float z2 = q.z + q.z;
        final float xx = q.x * x2;
        final float xy = q.x * y2;
        final float xz = q.x * z2;
        final float yy = q.y * y2;
        final float yz = q.y * z2;
        final float zz = q.z * z2;
        final float wx = q.w * x2;
        final float wy = q.w * y2;
        final float wz = q.w * z2;
        this.m00 = 1.0f - (yy + zz);
        this.m01 = xy - wz;
        this.m02 = xz + wy;
        this.m03 = 0.0f;
        this.m10 = xy + wz;
        this.m11 = 1.0f - (xx + zz);
        this.m12 = yz - wx;
        this.m13 = 0.0f;
        this.m20 = xz - wy;
        this.m21 = yz + wx;
        this.m22 = 1.0f - (xx + yy);
        this.m23 = 0.0f;
        this.m30 = 0.0f;
        this.m31 = 0.0f;
        this.m32 = 0.0f;
        this.m33 = 1.0f;
    }
    
    public void transform(final Point3f v) {
        final float x = v.x * this.m00 + v.y * this.m10 + v.z * this.m20 + this.m30;
        final float y = v.x * this.m01 + v.y * this.m11 + v.z * this.m21 + this.m31;
        final float z = v.x * this.m02 + v.y * this.m12 + v.z * this.m22 + this.m32;
        v.x = x;
        v.y = y;
        v.z = z;
    }
    
    public void transform(final Vector3f v) {
        final float x = v.x * this.m00 + v.y * this.m10 + v.z * this.m20;
        final float y = v.x * this.m01 + v.y * this.m11 + v.z * this.m21;
        final float z = v.x * this.m02 + v.y * this.m12 + v.z * this.m22;
        v.x = x;
        v.y = y;
        v.z = z;
    }
    
    public void setTranslation(final Vector3f v) {
        this.m30 = v.x;
        this.m31 = v.y;
        this.m32 = v.z;
    }
    
    public void set(final float scale) {
        this.m00 = scale;
        this.m11 = scale;
        this.m22 = scale;
    }
    
    public void rotX(final float angle) {
        final float s = (float)Math.sin(angle);
        final float c = (float)Math.cos(angle);
        this.m00 = 1.0f;
        this.m01 = 0.0f;
        this.m02 = 0.0f;
        this.m03 = 0.0f;
        this.m10 = 0.0f;
        this.m11 = c;
        this.m12 = s;
        this.m13 = 0.0f;
        this.m20 = 0.0f;
        this.m21 = -s;
        this.m22 = c;
        this.m23 = 0.0f;
        this.m30 = 0.0f;
        this.m31 = 0.0f;
        this.m32 = 0.0f;
        this.m33 = 1.0f;
    }
    
    public void rotY(final float angle) {
        final float s = (float)Math.sin(angle);
        final float c = (float)Math.cos(angle);
        this.m00 = c;
        this.m01 = 0.0f;
        this.m02 = -s;
        this.m03 = 0.0f;
        this.m10 = 0.0f;
        this.m11 = 1.0f;
        this.m12 = 0.0f;
        this.m13 = 0.0f;
        this.m20 = s;
        this.m21 = 0.0f;
        this.m22 = c;
        this.m23 = 0.0f;
        this.m30 = 0.0f;
        this.m31 = 0.0f;
        this.m32 = 0.0f;
        this.m33 = 1.0f;
    }
    
    public void rotZ(final float angle) {
        final float s = (float)Math.sin(angle);
        final float c = (float)Math.cos(angle);
        this.m00 = c;
        this.m01 = s;
        this.m02 = 0.0f;
        this.m03 = 0.0f;
        this.m10 = -s;
        this.m11 = c;
        this.m12 = 0.0f;
        this.m13 = 0.0f;
        this.m20 = 0.0f;
        this.m21 = 0.0f;
        this.m22 = 1.0f;
        this.m23 = 0.0f;
        this.m30 = 0.0f;
        this.m31 = 0.0f;
        this.m32 = 0.0f;
        this.m33 = 1.0f;
    }
}
