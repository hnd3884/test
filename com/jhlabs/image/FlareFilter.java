package com.jhlabs.image;

import com.jhlabs.math.Noise;
import java.awt.geom.Point2D;

public class FlareFilter extends PointFilter
{
    private int rays;
    private float radius;
    private float baseAmount;
    private float ringAmount;
    private float rayAmount;
    private int color;
    private int width;
    private int height;
    private float centreX;
    private float centreY;
    private float ringWidth;
    private float linear;
    private float gauss;
    private float mix;
    private float falloff;
    private float sigma;
    private float icentreX;
    private float icentreY;
    
    public FlareFilter() {
        this.rays = 50;
        this.baseAmount = 1.0f;
        this.ringAmount = 0.2f;
        this.rayAmount = 0.1f;
        this.color = -1;
        this.centreX = 0.5f;
        this.centreY = 0.5f;
        this.ringWidth = 1.6f;
        this.linear = 0.03f;
        this.gauss = 0.006f;
        this.mix = 0.5f;
        this.falloff = 6.0f;
        this.setRadius(50.0f);
    }
    
    public void setColor(final int color) {
        this.color = color;
    }
    
    public int getColor() {
        return this.color;
    }
    
    public void setRingWidth(final float ringWidth) {
        this.ringWidth = ringWidth;
    }
    
    public float getRingWidth() {
        return this.ringWidth;
    }
    
    public void setBaseAmount(final float baseAmount) {
        this.baseAmount = baseAmount;
    }
    
    public float getBaseAmount() {
        return this.baseAmount;
    }
    
    public void setRingAmount(final float ringAmount) {
        this.ringAmount = ringAmount;
    }
    
    public float getRingAmount() {
        return this.ringAmount;
    }
    
    public void setRayAmount(final float rayAmount) {
        this.rayAmount = rayAmount;
    }
    
    public float getRayAmount() {
        return this.rayAmount;
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
        this.sigma = radius / 3.0f;
    }
    
    public float getRadius() {
        return this.radius;
    }
    
    @Override
    public void setDimensions(final int width, final int height) {
        this.width = width;
        this.height = height;
        this.icentreX = this.centreX * width;
        this.icentreY = this.centreY * height;
        super.setDimensions(width, height);
    }
    
    @Override
    public int filterRGB(final int x, final int y, final int rgb) {
        final float dx = x - this.icentreX;
        final float dy = y - this.icentreY;
        final float distance = (float)Math.sqrt(dx * dx + dy * dy);
        float a = (float)Math.exp(-distance * distance * this.gauss) * this.mix + (float)Math.exp(-distance * this.linear) * (1.0f - this.mix);
        a *= this.baseAmount;
        if (distance > this.radius + this.ringWidth) {
            a = ImageMath.lerp((distance - (this.radius + this.ringWidth)) / this.falloff, a, 0.0f);
        }
        float ring;
        if (distance < this.radius - this.ringWidth || distance > this.radius + this.ringWidth) {
            ring = 0.0f;
        }
        else {
            ring = Math.abs(distance - this.radius) / this.ringWidth;
            ring = 1.0f - ring * ring * (3.0f - 2.0f * ring);
            ring *= this.ringAmount;
        }
        a += ring;
        float angle = (float)Math.atan2(dx, dy) + 3.1415927f;
        angle = (ImageMath.mod(angle / 3.1415927f * 17.0f + 1.0f + Noise.noise1(angle * 10.0f), 1.0f) - 0.5f) * 2.0f;
        angle = Math.abs(angle);
        angle = (float)Math.pow(angle, 5.0);
        final float b = this.rayAmount * angle / (1.0f + distance * 0.1f);
        a += b;
        a = ImageMath.clamp(a, 0.0f, 1.0f);
        return ImageMath.mixColors(a, rgb, this.color);
    }
    
    @Override
    public String toString() {
        return "Stylize/Flare...";
    }
}
