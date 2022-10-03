package com.jhlabs.vecmath;

public class Point4f extends Tuple4f
{
    public Point4f() {
        this(0.0f, 0.0f, 0.0f, 0.0f);
    }
    
    public Point4f(final float[] x) {
        this.x = x[0];
        this.y = x[1];
        this.z = x[2];
        this.w = x[3];
    }
    
    public Point4f(final float x, final float y, final float z, final float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }
    
    public Point4f(final Point4f t) {
        this.x = t.x;
        this.y = t.y;
        this.z = t.z;
        this.w = t.w;
    }
    
    public Point4f(final Tuple4f t) {
        this.x = t.x;
        this.y = t.y;
        this.z = t.z;
        this.w = t.w;
    }
    
    public float distanceL1(final Point4f p) {
        return Math.abs(this.x - p.x) + Math.abs(this.y - p.y) + Math.abs(this.z - p.z) + Math.abs(this.w - p.w);
    }
    
    public float distanceSquared(final Point4f p) {
        final float dx = this.x - p.x;
        final float dy = this.y - p.y;
        final float dz = this.z - p.z;
        final float dw = this.w - p.w;
        return dx * dx + dy * dy + dz * dz + dw * dw;
    }
    
    public float distance(final Point4f p) {
        final float dx = this.x - p.x;
        final float dy = this.y - p.y;
        final float dz = this.z - p.z;
        final float dw = this.w - p.w;
        return (float)Math.sqrt(dx * dx + dy * dy + dz * dz + dw * dw);
    }
}
