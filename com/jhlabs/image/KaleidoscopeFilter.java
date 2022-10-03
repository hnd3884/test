package com.jhlabs.image;

import java.awt.image.BufferedImage;
import java.awt.geom.Point2D;

public class KaleidoscopeFilter extends TransformFilter
{
    private float angle;
    private float angle2;
    private float centreX;
    private float centreY;
    private int sides;
    private float radius;
    private float icentreX;
    private float icentreY;
    
    public KaleidoscopeFilter() {
        this.angle = 0.0f;
        this.angle2 = 0.0f;
        this.centreX = 0.5f;
        this.centreY = 0.5f;
        this.sides = 3;
        this.radius = 0.0f;
        this.setEdgeAction(1);
    }
    
    public void setSides(final int sides) {
        this.sides = sides;
    }
    
    public int getSides() {
        return this.sides;
    }
    
    public void setAngle(final float angle) {
        this.angle = angle;
    }
    
    public float getAngle() {
        return this.angle;
    }
    
    public void setAngle2(final float angle2) {
        this.angle2 = angle2;
    }
    
    public float getAngle2() {
        return this.angle2;
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
    
    public void setRadius(final float radius) {
        this.radius = radius;
    }
    
    public float getRadius() {
        return this.radius;
    }
    
    @Override
    public BufferedImage filter(final BufferedImage src, final BufferedImage dst) {
        this.icentreX = src.getWidth() * this.centreX;
        this.icentreY = src.getHeight() * this.centreY;
        return super.filter(src, dst);
    }
    
    @Override
    protected void transformInverse(final int x, final int y, final float[] out) {
        final double dx = x - this.icentreX;
        final double dy = y - this.icentreY;
        double r = Math.sqrt(dx * dx + dy * dy);
        double theta = Math.atan2(dy, dx) - this.angle - this.angle2;
        theta = ImageMath.triangle((float)(theta / 3.141592653589793 * this.sides * 0.5));
        if (this.radius != 0.0f) {
            final double c = Math.cos(theta);
            final double radiusc = this.radius / c;
            r = radiusc * ImageMath.triangle((float)(r / radiusc));
        }
        theta += this.angle;
        out[0] = (float)(this.icentreX + r * Math.cos(theta));
        out[1] = (float)(this.icentreY + r * Math.sin(theta));
    }
    
    @Override
    public String toString() {
        return "Distort/Kaleidoscope...";
    }
}
