package com.jhlabs.image;

import java.awt.image.BufferedImage;
import java.awt.geom.Point2D;

public class SphereFilter extends TransformFilter
{
    private float a;
    private float b;
    private float a2;
    private float b2;
    private float centreX;
    private float centreY;
    private float refractionIndex;
    private float icentreX;
    private float icentreY;
    
    public SphereFilter() {
        this.a = 0.0f;
        this.b = 0.0f;
        this.a2 = 0.0f;
        this.b2 = 0.0f;
        this.centreX = 0.5f;
        this.centreY = 0.5f;
        this.refractionIndex = 1.5f;
        this.setEdgeAction(1);
        this.setRadius(100.0f);
    }
    
    public void setRefractionIndex(final float refractionIndex) {
        this.refractionIndex = refractionIndex;
    }
    
    public float getRefractionIndex() {
        return this.refractionIndex;
    }
    
    public void setRadius(final float r) {
        this.a = r;
        this.b = r;
    }
    
    public float getRadius() {
        return this.a;
    }
    
    public void setCentreX(final float centreX) {
        this.centreX = centreX;
    }
    
    public float getCentreX() {
        return this.centreX;
    }
    
    public void setCentreY(final float centreY) {
        this.centreY = centreY;
    }
    
    public float getCentreY() {
        return this.centreY;
    }
    
    public void setCentre(final Point2D centre) {
        this.centreX = (float)centre.getX();
        this.centreY = (float)centre.getY();
    }
    
    public Point2D getCentre() {
        return new Point2D.Float(this.centreX, this.centreY);
    }
    
    @Override
    public BufferedImage filter(final BufferedImage src, final BufferedImage dst) {
        final int width = src.getWidth();
        final int height = src.getHeight();
        this.icentreX = width * this.centreX;
        this.icentreY = height * this.centreY;
        if (this.a == 0.0f) {
            this.a = (float)(width / 2);
        }
        if (this.b == 0.0f) {
            this.b = (float)(height / 2);
        }
        this.a2 = this.a * this.a;
        this.b2 = this.b * this.b;
        return super.filter(src, dst);
    }
    
    @Override
    protected void transformInverse(final int x, final int y, final float[] out) {
        final float dx = x - this.icentreX;
        final float dy = y - this.icentreY;
        final float x2 = dx * dx;
        final float y2 = dy * dy;
        if (y2 >= this.b2 - this.b2 * x2 / this.a2) {
            out[0] = (float)x;
            out[1] = (float)y;
        }
        else {
            final float rRefraction = 1.0f / this.refractionIndex;
            final float z = (float)Math.sqrt((1.0f - x2 / this.a2 - y2 / this.b2) * (this.a * this.b));
            final float z2 = z * z;
            final float xAngle = (float)Math.acos(dx / Math.sqrt(x2 + z2));
            float angle1 = 1.5707964f - xAngle;
            float angle2 = (float)Math.asin(Math.sin(angle1) * rRefraction);
            angle2 = 1.5707964f - xAngle - angle2;
            out[0] = x - (float)Math.tan(angle2) * z;
            final float yAngle = (float)Math.acos(dy / Math.sqrt(y2 + z2));
            angle1 = 1.5707964f - yAngle;
            angle2 = (float)Math.asin(Math.sin(angle1) * rRefraction);
            angle2 = 1.5707964f - yAngle - angle2;
            out[1] = y - (float)Math.tan(angle2) * z;
        }
    }
    
    @Override
    public String toString() {
        return "Distort/Sphere...";
    }
}
