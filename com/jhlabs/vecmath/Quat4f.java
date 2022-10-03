package com.jhlabs.vecmath;

public class Quat4f extends Tuple4f
{
    public Quat4f() {
        this(0.0f, 0.0f, 0.0f, 0.0f);
    }
    
    public Quat4f(final float[] x) {
        this.x = x[0];
        this.y = x[1];
        this.z = x[2];
        this.w = x[3];
    }
    
    public Quat4f(final float x, final float y, final float z, final float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }
    
    public Quat4f(final Quat4f t) {
        this.x = t.x;
        this.y = t.y;
        this.z = t.z;
        this.w = t.w;
    }
    
    public Quat4f(final Tuple4f t) {
        this.x = t.x;
        this.y = t.y;
        this.z = t.z;
        this.w = t.w;
    }
    
    public void set(final AxisAngle4f a) {
        final float halfTheta = a.angle * 0.5f;
        final float cosHalfTheta = (float)Math.cos(halfTheta);
        final float sinHalfTheta = (float)Math.sin(halfTheta);
        this.x = a.x * sinHalfTheta;
        this.y = a.y * sinHalfTheta;
        this.z = a.z * sinHalfTheta;
        this.w = cosHalfTheta;
    }
    
    public void normalize() {
        final float d = 1.0f / (this.x * this.x + this.y * this.y + this.z * this.z + this.w * this.w);
        this.x *= d;
        this.y *= d;
        this.z *= d;
        this.w *= d;
    }
    
    public void set(final Matrix4f m) {
        final float tr = m.m00 + m.m11 + m.m22;
        if (tr > 0.0) {
            float s = (float)Math.sqrt(tr + 1.0f);
            this.w = s / 2.0f;
            s = 0.5f / s;
            this.x = (m.m12 - m.m21) * s;
            this.y = (m.m20 - m.m02) * s;
            this.z = (m.m01 - m.m10) * s;
        }
        else {
            int i = 0;
            if (m.m11 > m.m00) {
                i = 1;
                if (m.m22 > m.m11) {
                    i = 2;
                }
            }
            else if (m.m22 > m.m00) {
                i = 2;
            }
            switch (i) {
                case 0: {
                    float s = (float)Math.sqrt(m.m00 - (m.m11 + m.m22) + 1.0f);
                    this.x = s * 0.5f;
                    if (s != 0.0) {
                        s = 0.5f / s;
                    }
                    this.w = (m.m12 - m.m21) * s;
                    this.y = (m.m01 + m.m10) * s;
                    this.z = (m.m02 + m.m20) * s;
                    break;
                }
                case 1: {
                    float s = (float)Math.sqrt(m.m11 - (m.m22 + m.m00) + 1.0f);
                    this.y = s * 0.5f;
                    if (s != 0.0) {
                        s = 0.5f / s;
                    }
                    this.w = (m.m20 - m.m02) * s;
                    this.z = (m.m12 + m.m21) * s;
                    this.x = (m.m10 + m.m01) * s;
                    break;
                }
                case 2: {
                    float s = (float)Math.sqrt(m.m00 - (m.m11 + m.m22) + 1.0f);
                    this.z = s * 0.5f;
                    if (s != 0.0) {
                        s = 0.5f / s;
                    }
                    this.w = (m.m01 - m.m10) * s;
                    this.x = (m.m20 + m.m02) * s;
                    this.y = (m.m21 + m.m12) * s;
                    break;
                }
            }
        }
    }
}
