package com.jhlabs.image;

import java.awt.image.BufferedImage;
import java.awt.geom.Point2D;

public class CircleFilter extends TransformFilter
{
    private float radius;
    private float height;
    private float angle;
    private float spreadAngle;
    private float centreX;
    private float centreY;
    private float icentreX;
    private float icentreY;
    private float iWidth;
    private float iHeight;
    
    public CircleFilter() {
        this.radius = 10.0f;
        this.height = 20.0f;
        this.angle = 0.0f;
        this.spreadAngle = 3.1415927f;
        this.centreX = 0.5f;
        this.centreY = 0.5f;
        this.setEdgeAction(0);
    }
    
    public void setHeight(final float height) {
        this.height = height;
    }
    
    public float getHeight() {
        return this.height;
    }
    
    public void setAngle(final float angle) {
        this.angle = angle;
    }
    
    public float getAngle() {
        return this.angle;
    }
    
    public void setSpreadAngle(final float spreadAngle) {
        this.spreadAngle = spreadAngle;
    }
    
    public float getSpreadAngle() {
        return this.spreadAngle;
    }
    
    public void setRadius(final float radius) {
        this.radius = radius;
    }
    
    public float getRadius() {
        return this.radius;
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
        this.iWidth = (float)src.getWidth();
        this.iHeight = (float)src.getHeight();
        this.icentreX = this.iWidth * this.centreX;
        this.icentreY = this.iHeight * this.centreY;
        --this.iWidth;
        return super.filter(src, dst);
    }
    
    @Override
    protected void transformInverse(final int x, final int y, final float[] out) {
        final float dx = x - this.icentreX;
        final float dy = y - this.icentreY;
        float theta = (float)Math.atan2(-dy, -dx) + this.angle;
        final float r = (float)Math.sqrt(dx * dx + dy * dy);
        theta = ImageMath.mod(theta, 6.2831855f);
        out[0] = this.iWidth * theta / (this.spreadAngle + 1.0E-5f);
        out[1] = this.iHeight * (1.0f - (r - this.radius) / (this.height + 1.0E-5f));
    }
    
    @Override
    public String toString() {
        return "Distort/Circle...";
    }
}
