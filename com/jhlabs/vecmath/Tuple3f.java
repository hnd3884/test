package com.jhlabs.vecmath;

public class Tuple3f
{
    public float x;
    public float y;
    public float z;
    
    public Tuple3f() {
        this(0.0f, 0.0f, 0.0f);
    }
    
    public Tuple3f(final float[] x) {
        this.x = x[0];
        this.y = x[1];
        this.z = x[2];
    }
    
    public Tuple3f(final float x, final float y, final float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    public Tuple3f(final Tuple3f t) {
        this.x = t.x;
        this.y = t.y;
        this.z = t.z;
    }
    
    public void absolute() {
        this.x = Math.abs(this.x);
        this.y = Math.abs(this.y);
        this.z = Math.abs(this.z);
    }
    
    public void absolute(final Tuple3f t) {
        this.x = Math.abs(t.x);
        this.y = Math.abs(t.y);
        this.z = Math.abs(t.z);
    }
    
    public void clamp(final float min, final float max) {
        if (this.x < min) {
            this.x = min;
        }
        else if (this.x > max) {
            this.x = max;
        }
        if (this.y < min) {
            this.y = min;
        }
        else if (this.y > max) {
            this.y = max;
        }
        if (this.z < min) {
            this.z = min;
        }
        else if (this.z > max) {
            this.z = max;
        }
    }
    
    public void set(final float x, final float y, final float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    public void set(final float[] x) {
        this.x = x[0];
        this.y = x[1];
        this.z = x[2];
    }
    
    public void set(final Tuple3f t) {
        this.x = t.x;
        this.y = t.y;
        this.z = t.z;
    }
    
    public void get(final Tuple3f t) {
        t.x = this.x;
        t.y = this.y;
        t.z = this.z;
    }
    
    public void get(final float[] t) {
        t[0] = this.x;
        t[1] = this.y;
        t[2] = this.z;
    }
    
    public void negate() {
        this.x = -this.x;
        this.y = -this.y;
        this.z = -this.z;
    }
    
    public void negate(final Tuple3f t) {
        this.x = -t.x;
        this.y = -t.y;
        this.z = -t.z;
    }
    
    public void interpolate(final Tuple3f t, final float alpha) {
        final float a = 1.0f - alpha;
        this.x = a * this.x + alpha * t.x;
        this.y = a * this.y + alpha * t.y;
        this.z = a * this.z + alpha * t.z;
    }
    
    public void scale(final float s) {
        this.x *= s;
        this.y *= s;
        this.z *= s;
    }
    
    public void add(final Tuple3f t) {
        this.x += t.x;
        this.y += t.y;
        this.z += t.z;
    }
    
    public void add(final Tuple3f t1, final Tuple3f t2) {
        this.x = t1.x + t2.x;
        this.y = t1.y + t2.y;
        this.z = t1.z + t2.z;
    }
    
    public void sub(final Tuple3f t) {
        this.x -= t.x;
        this.y -= t.y;
        this.z -= t.z;
    }
    
    public void sub(final Tuple3f t1, final Tuple3f t2) {
        this.x = t1.x - t2.x;
        this.y = t1.y - t2.y;
        this.z = t1.z - t2.z;
    }
    
    public void scaleAdd(final float s, final Tuple3f t) {
        this.x += s * t.x;
        this.y += s * t.y;
        this.z += s * t.z;
    }
    
    public void scaleAdd(final float s, final Tuple3f t1, final Tuple3f t2) {
        this.x = s * t1.x + t2.x;
        this.y = s * t1.y + t2.y;
        this.z = s * t1.z + t2.z;
    }
    
    @Override
    public String toString() {
        return "[" + this.x + ", " + this.y + ", " + this.z + "]";
    }
}
