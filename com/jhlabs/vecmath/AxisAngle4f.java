package com.jhlabs.vecmath;

public class AxisAngle4f
{
    public float x;
    public float y;
    public float z;
    public float angle;
    
    public AxisAngle4f() {
        this(0.0f, 0.0f, 0.0f, 0.0f);
    }
    
    public AxisAngle4f(final float[] x) {
        this.x = x[0];
        this.y = x[1];
        this.z = x[2];
        this.angle = x[2];
    }
    
    public AxisAngle4f(final float x, final float y, final float z, final float angle) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.angle = angle;
    }
    
    public AxisAngle4f(final AxisAngle4f t) {
        this.x = t.x;
        this.y = t.y;
        this.z = t.z;
        this.angle = t.angle;
    }
    
    public AxisAngle4f(final Vector3f v, final float angle) {
        this.x = v.x;
        this.y = v.y;
        this.z = v.z;
        this.angle = angle;
    }
    
    public void set(final float x, final float y, final float z, final float angle) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.angle = angle;
    }
    
    public void set(final AxisAngle4f t) {
        this.x = t.x;
        this.y = t.y;
        this.z = t.z;
        this.angle = t.angle;
    }
    
    public void get(final AxisAngle4f t) {
        t.x = this.x;
        t.y = this.y;
        t.z = this.z;
        t.angle = this.angle;
    }
    
    public void get(final float[] t) {
        t[0] = this.x;
        t[1] = this.y;
        t[2] = this.z;
        t[3] = this.angle;
    }
    
    @Override
    public String toString() {
        return "[" + this.x + ", " + this.y + ", " + this.z + ", " + this.angle + "]";
    }
}
