package com.jhlabs.image;

import java.awt.image.BufferedImage;
import java.awt.geom.Point2D;

public class PinchFilter extends TransformFilter
{
    private float angle;
    private float centreX;
    private float centreY;
    private float radius;
    private float amount;
    private float radius2;
    private float icentreX;
    private float icentreY;
    private float width;
    private float height;
    
    public PinchFilter() {
        this.angle = 0.0f;
        this.centreX = 0.5f;
        this.centreY = 0.5f;
        this.radius = 100.0f;
        this.amount = 0.5f;
        this.radius2 = 0.0f;
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
    
    public void setAmount(final float amount) {
        this.amount = amount;
    }
    
    public float getAmount() {
        return this.amount;
    }
    
    @Override
    public BufferedImage filter(final BufferedImage src, final BufferedImage dst) {
        this.width = (float)src.getWidth();
        this.height = (float)src.getHeight();
        this.icentreX = this.width * this.centreX;
        this.icentreY = this.height * this.centreY;
        if (this.radius == 0.0f) {
            this.radius = Math.min(this.icentreX, this.icentreY);
        }
        this.radius2 = this.radius * this.radius;
        return super.filter(src, dst);
    }
    
    @Override
    protected void transformInverse(final int x, final int y, final float[] out) {
        float dx = x - this.icentreX;
        float dy = y - this.icentreY;
        final float distance = dx * dx + dy * dy;
        if (distance > this.radius2 || distance == 0.0f) {
            out[0] = (float)x;
            out[1] = (float)y;
        }
        else {
            final float d = (float)Math.sqrt(distance / this.radius2);
            final float t = (float)Math.pow(Math.sin(1.5707963267948966 * d), -this.amount);
            dx *= t;
            dy *= t;
            final float e = 1.0f - d;
            final float a = this.angle * e * e;
            final float s = (float)Math.sin(a);
            final float c = (float)Math.cos(a);
            out[0] = this.icentreX + c * dx - s * dy;
            out[1] = this.icentreY + s * dx + c * dy;
        }
    }
    
    @Override
    public String toString() {
        return "Distort/Pinch...";
    }
}
