package com.lowagie.text.pdf.parser;

public class Vector
{
    public static final int I1 = 0;
    public static final int I2 = 1;
    public static final int I3 = 2;
    private final float[] vals;
    
    public Vector(final float x, final float y, final float z) {
        (this.vals = new float[] { 0.0f, 0.0f, 0.0f })[0] = x;
        this.vals[1] = y;
        this.vals[2] = z;
    }
    
    public float get(final int index) {
        return this.vals[index];
    }
    
    public Vector cross(final Matrix by) {
        final float x = this.vals[0] * by.get(0) + this.vals[1] * by.get(3) + this.vals[2] * by.get(6);
        final float y = this.vals[0] * by.get(1) + this.vals[1] * by.get(4) + this.vals[2] * by.get(7);
        final float z = this.vals[0] * by.get(2) + this.vals[1] * by.get(5) + this.vals[2] * by.get(8);
        return new Vector(x, y, z);
    }
    
    public Vector subtract(final Vector v) {
        final float x = this.vals[0] - v.vals[0];
        final float y = this.vals[1] - v.vals[1];
        final float z = this.vals[2] - v.vals[2];
        return new Vector(x, y, z);
    }
    
    public Vector add(final Vector v) {
        final float x = this.vals[0] + v.vals[0];
        final float y = this.vals[1] + v.vals[1];
        final float z = this.vals[2] + v.vals[2];
        return new Vector(x, y, z);
    }
    
    public Vector cross(final Vector with) {
        final float x = this.vals[1] * with.vals[2] - this.vals[2] * with.vals[1];
        final float y = this.vals[2] * with.vals[0] - this.vals[0] * with.vals[2];
        final float z = this.vals[0] * with.vals[1] - this.vals[1] * with.vals[0];
        return new Vector(x, y, z);
    }
    
    public float dot(final Vector with) {
        return this.vals[0] * with.vals[0] + this.vals[1] * with.vals[1] + this.vals[2] * with.vals[2];
    }
    
    public float length() {
        return (float)Math.sqrt(this.lengthSquared());
    }
    
    public float lengthSquared() {
        return this.vals[0] * this.vals[0] + this.vals[1] * this.vals[1] + this.vals[2] * this.vals[2];
    }
    
    @Override
    public String toString() {
        return this.vals[0] + "," + this.vals[1] + "," + this.vals[2];
    }
}
