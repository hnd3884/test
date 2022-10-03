package com.jhlabs.image;

import java.util.Random;

public class SparkleFilter extends PointFilter
{
    private int rays;
    private int radius;
    private int amount;
    private int color;
    private int randomness;
    private int width;
    private int height;
    private int centreX;
    private int centreY;
    private long seed;
    private float[] rayLengths;
    private Random randomNumbers;
    
    public SparkleFilter() {
        this.rays = 50;
        this.radius = 25;
        this.amount = 50;
        this.color = -1;
        this.randomness = 25;
        this.seed = 371L;
        this.randomNumbers = new Random();
    }
    
    public void setColor(final int color) {
        this.color = color;
    }
    
    public int getColor() {
        return this.color;
    }
    
    public void setRandomness(final int randomness) {
        this.randomness = randomness;
    }
    
    public int getRandomness() {
        return this.randomness;
    }
    
    public void setAmount(final int amount) {
        this.amount = amount;
    }
    
    public int getAmount() {
        return this.amount;
    }
    
    public void setRays(final int rays) {
        this.rays = rays;
    }
    
    public int getRays() {
        return this.rays;
    }
    
    public void setRadius(final int radius) {
        this.radius = radius;
    }
    
    public int getRadius() {
        return this.radius;
    }
    
    @Override
    public void setDimensions(final int width, final int height) {
        this.width = width;
        this.height = height;
        this.centreX = width / 2;
        this.centreY = height / 2;
        super.setDimensions(width, height);
        this.randomNumbers.setSeed(this.seed);
        this.rayLengths = new float[this.rays];
        for (int i = 0; i < this.rays; ++i) {
            this.rayLengths[i] = this.radius + this.randomness / 100.0f * this.radius * (float)this.randomNumbers.nextGaussian();
        }
    }
    
    @Override
    public int filterRGB(final int x, final int y, final int rgb) {
        final float dx = (float)(x - this.centreX);
        final float dy = (float)(y - this.centreY);
        final float distance = dx * dx + dy * dy;
        final float angle = (float)Math.atan2(dy, dx);
        final float d = (angle + 3.1415927f) / 6.2831855f * this.rays;
        final int i = (int)d;
        float f = d - i;
        if (this.radius != 0) {
            final float length = ImageMath.lerp(f, this.rayLengths[i % this.rays], this.rayLengths[(i + 1) % this.rays]);
            float g = length * length / (distance + 1.0E-4f);
            g = (float)Math.pow(g, (100 - this.amount) / 50.0);
            f -= 0.5f;
            f = 1.0f - f * f;
            f *= g;
        }
        f = ImageMath.clamp(f, 0.0f, 1.0f);
        return ImageMath.mixColors(f, rgb, this.color);
    }
    
    @Override
    public String toString() {
        return "Stylize/Sparkle...";
    }
}
