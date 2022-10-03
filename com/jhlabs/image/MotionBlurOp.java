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

public class MotionBlurOp extends AbstractBufferedImageOp
{
    private float centreX;
    private float centreY;
    private float distance;
    private float angle;
    private float rotation;
    private float zoom;
    
    public MotionBlurOp() {
        this.centreX = 0.5f;
        this.centreY = 0.5f;
    }
    
    public MotionBlurOp(final float distance, final float angle, final float rotation, final float zoom) {
        this.centreX = 0.5f;
        this.centreY = 0.5f;
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
    
    private int log2(final int n) {
        int m;
        int log2n;
        for (m = 1, log2n = 0; m < n; m *= 2, ++log2n) {}
        return log2n;
    }
    
    public BufferedImage filter(final BufferedImage src, BufferedImage dst) {
        if (dst == null) {
            dst = this.createCompatibleDestImage(src, null);
        }
        BufferedImage tsrc = src;
        final float cx = src.getWidth() * this.centreX;
        final float cy = src.getHeight() * this.centreY;
        final float imageRadius = (float)Math.sqrt(cx * cx + cy * cy);
        float translateX = (float)(this.distance * Math.cos(this.angle));
        float translateY = (float)(this.distance * -Math.sin(this.angle));
        float scale = this.zoom;
        float rotate = this.rotation;
        final float maxDistance = this.distance + Math.abs(this.rotation * imageRadius) + this.zoom * imageRadius;
        final int steps = this.log2((int)maxDistance);
        translateX /= maxDistance;
        translateY /= maxDistance;
        scale /= maxDistance;
        rotate /= maxDistance;
        if (steps == 0) {
            final Graphics2D g = dst.createGraphics();
            g.drawRenderedImage(src, null);
            g.dispose();
            return dst;
        }
        BufferedImage tmp = this.createCompatibleDestImage(src, null);
        for (int i = 0; i < steps; ++i) {
            final Graphics2D g2 = tmp.createGraphics();
            g2.drawImage(tsrc, null, null);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.setComposite(AlphaComposite.getInstance(3, 0.5f));
            g2.translate(cx + translateX, cy + translateY);
            g2.scale(1.0001 + scale, 1.0001 + scale);
            if (this.rotation != 0.0f) {
                g2.rotate(rotate);
            }
            g2.translate(-cx, -cy);
            g2.drawImage(dst, null, null);
            g2.dispose();
            final BufferedImage ti = dst;
            dst = tmp;
            tmp = ti;
            tsrc = dst;
            translateX *= 2.0f;
            translateY *= 2.0f;
            scale *= 2.0f;
            rotate *= 2.0f;
        }
        return dst;
    }
    
    @Override
    public String toString() {
        return "Blur/Faster Motion Blur...";
    }
}
