package com.jhlabs.image;

import java.awt.image.BufferedImage;
import java.awt.geom.Point2D;

public class TwirlFilter extends TransformFilter
{
    private float angle;
    private float centreX;
    private float centreY;
    private float radius;
    private float radius2;
    private float icentreX;
    private float icentreY;
    
    public TwirlFilter() {
        this.angle = 0.0f;
        this.centreX = 0.5f;
        this.centreY = 0.5f;
        this.radius = 100.0f;
        this.radius2 = 0.0f;
        this.setEdgeAction(1);
    }
    
    public void setAngle(final float angle) {
        this.angle = angle;
    }
    
    public float getAngle() {
        return this.angle;
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
        if (this.radius == 0.0f) {
            this.radius = Math.min(this.icentreX, this.icentreY);
        }
        this.radius2 = this.radius * this.radius;
        return super.filter(src, dst);
    }
    
    @Override
    protected void transformInverse(final int x, final int y, final float[] out) {
        final float dx = x - this.icentreX;
        final float dy = y - this.icentreY;
        float distance = dx * dx + dy * dy;
        if (distance > this.radius2) {
            out[0] = (float)x;
            out[1] = (float)y;
        }
        else {
            distance = (float)Math.sqrt(distance);
            final float a = (float)Math.atan2(dy, dx) + this.angle * (this.radius - distance) / this.radius;
            out[0] = this.icentreX + distance * (float)Math.cos(a);
            out[1] = this.icentreY + distance * (float)Math.sin(a);
        }
    }
    
    @Override
    public String toString() {
        return "Distort/Twirl...";
    }
}
