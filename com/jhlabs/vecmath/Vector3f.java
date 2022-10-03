package com.jhlabs.vecmath;

public class Vector3f extends Tuple3f
{
    public Vector3f() {
        this(0.0f, 0.0f, 0.0f);
    }
    
    public Vector3f(final float[] x) {
        this.x = x[0];
        this.y = x[1];
        this.z = x[2];
    }
    
    public Vector3f(final float x, final float y, final float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    public Vector3f(final Vector3f t) {
        this.x = t.x;
        this.y = t.y;
        this.z = t.z;
    }
    
    public Vector3f(final Tuple3f t) {
        this.x = t.x;
        this.y = t.y;
        this.z = t.z;
    }
    
    public float angle(final Vector3f v) {
        return (float)Math.acos(this.dot(v) / (this.length() * v.length()));
    }
    
    public float dot(final Vector3f v) {
        return v.x * this.x + v.y * this.y + v.z * this.z;
    }
    
    public void cross(final Vector3f v1, final Vector3f v2) {
        this.x = v1.y * v2.z - v1.z * v2.y;
        this.y = v1.z * v2.x - v1.x * v2.z;
        this.z = v1.x * v2.y - v1.y * v2.x;
    }
    
    public float length() {
        return (float)Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
    }
    
    public void normalize() {
        final float d = 1.0f / (float)Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
        this.x *= d;
        this.y *= d;
        this.z *= d;
    }
}
