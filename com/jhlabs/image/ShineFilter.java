package com.jhlabs.image;

import java.awt.Graphics2D;
import com.jhlabs.composite.AddComposite;
import java.awt.geom.AffineTransform;
import java.awt.image.RenderedImage;
import java.awt.Composite;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.image.ColorModel;
import java.awt.image.BufferedImage;

public class ShineFilter extends AbstractBufferedImageOp
{
    private float radius;
    private float angle;
    private float distance;
    private float bevel;
    private boolean shadowOnly;
    private int shineColor;
    private float brightness;
    private float softness;
    
    public ShineFilter() {
        this.radius = 5.0f;
        this.angle = 5.4977875f;
        this.distance = 5.0f;
        this.bevel = 0.5f;
        this.shadowOnly = false;
        this.shineColor = -1;
        this.brightness = 0.2f;
        this.softness = 0.0f;
    }
    
    public void setAngle(final float angle) {
        this.angle = angle;
    }
    
    public float getAngle() {
        return this.angle;
    }
    
    public void setDistance(final float distance) {
        this.distance = distance;
    }
    
    public float getDistance() {
        return this.distance;
    }
    
    public void setRadius(final float radius) {
        this.radius = radius;
    }
    
    public float getRadius() {
        return this.radius;
    }
    
    public void setBevel(final float bevel) {
        this.bevel = bevel;
    }
    
    public float getBevel() {
        return this.bevel;
    }
    
    public void setShineColor(final int shineColor) {
        this.shineColor = shineColor;
    }
    
    public int getShineColor() {
        return this.shineColor;
    }
    
    public void setShadowOnly(final boolean shadowOnly) {
        this.shadowOnly = shadowOnly;
    }
    
    public boolean getShadowOnly() {
        return this.shadowOnly;
    }
    
    public void setBrightness(final float brightness) {
        this.brightness = brightness;
    }
    
    public float getBrightness() {
        return this.brightness;
    }
    
    public void setSoftness(final float softness) {
        this.softness = softness;
    }
    
    public float getSoftness() {
        return this.softness;
    }
    
    public BufferedImage filter(final BufferedImage src, BufferedImage dst) {
        final int width = src.getWidth();
        final int height = src.getHeight();
        if (dst == null) {
            dst = this.createCompatibleDestImage(src, null);
        }
        final float xOffset = this.distance * (float)Math.cos(this.angle);
        final float yOffset = -this.distance * (float)Math.sin(this.angle);
        BufferedImage matte = new BufferedImage(width, height, 2);
        final ErodeAlphaFilter s = new ErodeAlphaFilter(this.bevel * 10.0f, 0.75f, 0.1f);
        matte = s.filter(src, null);
        BufferedImage shineLayer = new BufferedImage(width, height, 2);
        Graphics2D g = shineLayer.createGraphics();
        g.setColor(new Color(this.shineColor));
        g.fillRect(0, 0, width, height);
        g.setComposite(AlphaComposite.DstIn);
        g.drawRenderedImage(matte, null);
        g.setComposite(AlphaComposite.DstOut);
        g.translate(xOffset, yOffset);
        g.drawRenderedImage(matte, null);
        g.dispose();
        shineLayer = new GaussianFilter(this.radius).filter(shineLayer, null);
        shineLayer = new RescaleFilter(3.0f * this.brightness).filter(shineLayer, shineLayer);
        g = dst.createGraphics();
        g.drawRenderedImage(src, null);
        g.setComposite(new AddComposite(1.0f));
        g.drawRenderedImage(shineLayer, null);
        g.dispose();
        return dst;
    }
    
    @Override
    public String toString() {
        return "Stylize/Shine...";
    }
}
