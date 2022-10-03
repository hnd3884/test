package com.jhlabs.image;

import java.awt.Shape;
import java.awt.Graphics2D;
import java.awt.Composite;
import java.awt.AlphaComposite;
import java.awt.Paint;
import java.awt.GradientPaint;
import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.awt.image.RenderedImage;
import java.awt.image.ColorModel;
import java.awt.image.BufferedImage;

public class MirrorFilter extends AbstractBufferedImageOp
{
    private float opacity;
    private float centreY;
    private float distance;
    private float angle;
    private float rotation;
    private float gap;
    
    public MirrorFilter() {
        this.opacity = 1.0f;
        this.centreY = 0.5f;
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
    
    public void setRotation(final float rotation) {
        this.rotation = rotation;
    }
    
    public float getRotation() {
        return this.rotation;
    }
    
    public void setGap(final float gap) {
        this.gap = gap;
    }
    
    public float getGap() {
        return this.gap;
    }
    
    public void setOpacity(final float opacity) {
        this.opacity = opacity;
    }
    
    public float getOpacity() {
        return this.opacity;
    }
    
    public void setCentreY(final float centreY) {
        this.centreY = centreY;
    }
    
    public float getCentreY() {
        return this.centreY;
    }
    
    public BufferedImage filter(final BufferedImage src, BufferedImage dst) {
        if (dst == null) {
            dst = this.createCompatibleDestImage(src, null);
        }
        final BufferedImage tsrc = src;
        final int width = src.getWidth();
        final int height = src.getHeight();
        final int h = (int)(this.centreY * height);
        final int d = (int)(this.gap * height);
        final Graphics2D g = dst.createGraphics();
        final Shape clip = g.getClip();
        g.clipRect(0, 0, width, h);
        g.drawRenderedImage(src, null);
        g.setClip(clip);
        g.clipRect(0, h + d, width, height - h - d);
        g.translate(0, 2 * h + d);
        g.scale(1.0, -1.0);
        g.drawRenderedImage(src, null);
        g.setPaint(new GradientPaint(0.0f, 0.0f, new Color(1.0f, 0.0f, 0.0f, 0.0f), 0.0f, (float)h, new Color(0.0f, 1.0f, 0.0f, this.opacity)));
        g.setComposite(AlphaComposite.getInstance(6));
        g.fillRect(0, 0, width, h);
        g.setClip(clip);
        g.dispose();
        return dst;
    }
    
    @Override
    public String toString() {
        return "Effects/Mirror...";
    }
}
