package com.jhlabs.vecmath;

public class Point3f extends Tuple3f
{
    public Point3f() {
        this(0.0f, 0.0f, 0.0f);
    }
    
    public Point3f(final float[] x) {
        this.x = x[0];
        this.y = x[1];
        this.z = x[2];
    }
    
    public Point3f(final float x, final float y, final float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    public Point3f(final Point3f t) {
        this.x = t.x;
        this.y = t.y;
        this.z = t.z;
    }
    
    public Point3f(final Tuple3f t) {
        this.x = t.x;
        this.y = t.y;
        this.z = t.z;
    }
    
    public float distanceL1(final Point3f p) {
        return Math.abs(this.x - p.x) + Math.abs(this.y - p.y) + Math.abs(this.z - p.z);
    }
    
    public float distanceSquared(final Point3f p) {
        final float dx = this.x - p.x;
        final float dy = this.y - p.y;
        final float dz = this.z - p.z;
        return dx * dx + dy * dy + dz * dz;
    }
    
    public float distance(final Point3f p) {
        final float dx = this.x - p.x;
        final float dy = this.y - p.y;
        final float dz = this.z - p.z;
        return (float)Math.sqrt(dx * dx + dy * dy + dz * dz);
    }
}
