package com.jhlabs.image;

import java.awt.Graphics2D;
import java.awt.Composite;
import java.awt.AlphaComposite;
import java.awt.RenderingHints;
import java.awt.image.ImageObserver;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.RenderedImage;
import java.awt.image.ColorModel;
import java.awt.image.BufferedImage;
import java.awt.geom.Point2D;

public class FeedbackFilter extends AbstractBufferedImageOp
{
    private float centreX;
    private float centreY;
    private float distance;
    private float angle;
    private float rotation;
    private float zoom;
    private float startAlpha;
    private float endAlpha;
    private int iterations;
    
    public FeedbackFilter() {
        this.centreX = 0.5f;
        this.centreY = 0.5f;
        this.startAlpha = 1.0f;
        this.endAlpha = 1.0f;
    }
    
    public FeedbackFilter(final float distance, final float angle, final float rotation, final float zoom) {
        this.centreX = 0.5f;
        this.centreY = 0.5f;
        this.startAlpha = 1.0f;
        this.endAlpha = 1.0f;
        this.distance = distance;
        this.angle = angle;
        this.rotation = rotation;
        this.zoom = zoom;
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
    
    public void setZoom(final float zoom) {
        this.zoom = zoom;
    }
    
    public float getZoom() {
        return this.zoom;
    }
    
    public void setStartAlpha(final float startAlpha) {
        this.startAlpha = startAlpha;
    }
    
    public float getStartAlpha() {
        return this.startAlpha;
    }
    
    public void setEndAlpha(final float endAlpha) {
        this.endAlpha = endAlpha;
    }
    
    public float getEndAlpha() {
        return this.endAlpha;
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
    
    public void setIterations(final int iterations) {
        this.iterations = iterations;
    }
    
    public int getIterations() {
        return this.iterations;
    }
    
    public BufferedImage filter(final BufferedImage src, BufferedImage dst) {
        if (dst == null) {
            dst = this.createCompatibleDestImage(src, null);
        }
        final float cx = src.getWidth() * this.centreX;
        final float cy = src.getHeight() * this.centreY;
        final float imageRadius = (float)Math.sqrt(cx * cx + cy * cy);
        final float translateX = (float)(this.distance * Math.cos(this.angle));
        final float translateY = (float)(this.distance * -Math.sin(this.angle));
        final float scale = (float)Math.exp(this.zoom);
        final float rotate = this.rotation;
        if (this.iterations == 0) {
            final Graphics2D g = dst.createGraphics();
            g.drawRenderedImage(src, null);
            g.dispose();
            return dst;
        }
        final Graphics2D g = dst.createGraphics();
        g.drawImage(src, null, null);
        for (int i = 0; i < this.iterations; ++i) {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g.setComposite(AlphaComposite.getInstance(3, ImageMath.lerp(i / (float)(this.iterations - 1), this.startAlpha, this.endAlpha)));
            g.translate(cx + translateX, cy + translateY);
            g.scale(scale, scale);
            if (this.rotation != 0.0f) {
                g.rotate(rotate);
            }
            g.translate(-cx, -cy);
            g.drawImage(src, null, null);
        }
        g.dispose();
        return dst;
    }
    
    @Override
    public String toString() {
        return "Effects/Feedback...";
    }
}
