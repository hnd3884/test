package com.jhlabs.image;

import java.awt.image.BufferedImage;
import java.awt.geom.Point2D;

public class WaterFilter extends TransformFilter
{
    private float wavelength;
    private float amplitude;
    private float phase;
    private float centreX;
    private float centreY;
    private float radius;
    private float radius2;
    private float icentreX;
    private float icentreY;
    
    public WaterFilter() {
        this.wavelength = 16.0f;
        this.amplitude = 10.0f;
        this.phase = 0.0f;
        this.centreX = 0.5f;
        this.centreY = 0.5f;
        this.radius = 50.0f;
        this.radius2 = 0.0f;
        this.setEdgeAction(1);
    }
    
    public void setWavelength(final float wavelength) {
        this.wavelength = wavelength;
    }
    
    public float getWavelength() {
        return this.wavelength;
    }
    
    public void setAmplitude(final float amplitude) {
        this.amplitude = amplitude;
    }
    
    public float getAmplitude() {
        return this.amplitude;
    }
    
    public void setPhase(final float phase) {
        this.phase = phase;
    }
    
    public float getPhase() {
        return this.phase;
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
    
    private boolean inside(final int v, final int a, final int b) {
        return a <= v && v <= b;
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
        final float distance2 = dx * dx + dy * dy;
        if (distance2 > this.radius2) {
            out[0] = (float)x;
            out[1] = (float)y;
        }
        else {
            final float distance3 = (float)Math.sqrt(distance2);
            float amount = this.amplitude * (float)Math.sin(distance3 / this.wavelength * 6.2831855f - this.phase);
            amount *= (this.radius - distance3) / this.radius;
            if (distance3 != 0.0f) {
                amount *= this.wavelength / distance3;
            }
            out[0] = x + dx * amount;
            out[1] = y + dy * amount;
        }
    }
    
    @Override
    public String toString() {
        return "Distort/Water Ripples...";
    }
}
