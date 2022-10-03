package com.jhlabs.image;

import java.awt.geom.Point2D;
import java.awt.geom.AffineTransform;
import java.awt.image.ColorModel;
import java.awt.image.BufferedImage;

public class MotionBlurFilter extends AbstractBufferedImageOp
{
    private float angle;
    private float falloff;
    private float distance;
    private float zoom;
    private float rotation;
    private boolean wrapEdges;
    private boolean premultiplyAlpha;
    
    public MotionBlurFilter() {
        this.angle = 0.0f;
        this.falloff = 1.0f;
        this.distance = 1.0f;
        this.zoom = 0.0f;
        this.rotation = 0.0f;
        this.wrapEdges = false;
        this.premultiplyAlpha = true;
    }
    
    public MotionBlurFilter(final float distance, final float angle, final float rotation, final float zoom) {
        this.angle = 0.0f;
        this.falloff = 1.0f;
        this.distance = 1.0f;
        this.zoom = 0.0f;
        this.rotation = 0.0f;
        this.wrapEdges = false;
        this.premultiplyAlpha = true;
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
    
    public void setWrapEdges(final boolean wrapEdges) {
        this.wrapEdges = wrapEdges;
    }
    
    public boolean getWrapEdges() {
        return this.wrapEdges;
    }
    
    public void setPremultiplyAlpha(final boolean premultiplyAlpha) {
        this.premultiplyAlpha = premultiplyAlpha;
    }
    
    public boolean getPremultiplyAlpha() {
        return this.premultiplyAlpha;
    }
    
    public BufferedImage filter(final BufferedImage src, BufferedImage dst) {
        final int width = src.getWidth();
        final int height = src.getHeight();
        if (dst == null) {
            dst = this.createCompatibleDestImage(src, null);
        }
        final int[] inPixels = new int[width * height];
        final int[] outPixels = new int[width * height];
        this.getRGB(src, 0, 0, width, height, inPixels);
        final float sinAngle = (float)Math.sin(this.angle);
        final float cosAngle = (float)Math.cos(this.angle);
        final int cx = width / 2;
        final int cy = height / 2;
        int index = 0;
        final float imageRadius = (float)Math.sqrt(cx * cx + cy * cy);
        final float translateX = (float)(this.distance * Math.cos(this.angle));
        final float translateY = (float)(this.distance * -Math.sin(this.angle));
        final float maxDistance = this.distance + Math.abs(this.rotation * imageRadius) + this.zoom * imageRadius;
        final int repetitions = (int)maxDistance;
        final AffineTransform t = new AffineTransform();
        final Point2D.Float p = new Point2D.Float();
        if (this.premultiplyAlpha) {
            ImageMath.premultiply(inPixels, 0, inPixels.length);
        }
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                int a = 0;
                int r = 0;
                int g = 0;
                int b = 0;
                int count = 0;
                for (int i = 0; i < repetitions; ++i) {
                    int newX = x;
                    int newY = y;
                    final float f = i / (float)repetitions;
                    p.x = (float)x;
                    p.y = (float)y;
                    t.setToIdentity();
                    t.translate(cx + f * translateX, cy + f * translateY);
                    final float s = 1.0f - this.zoom * f;
                    t.scale(s, s);
                    if (this.rotation != 0.0f) {
                        t.rotate(-this.rotation * f);
                    }
                    t.translate(-cx, -cy);
                    t.transform(p, p);
                    newX = (int)p.x;
                    newY = (int)p.y;
                    if (newX < 0 || newX >= width) {
                        if (!this.wrapEdges) {
                            break;
                        }
                        newX = ImageMath.mod(newX, width);
                    }
                    if (newY < 0 || newY >= height) {
                        if (!this.wrapEdges) {
                            break;
                        }
                        newY = ImageMath.mod(newY, height);
                    }
                    ++count;
                    final int rgb = inPixels[newY * width + newX];
                    a += (rgb >> 24 & 0xFF);
                    r += (rgb >> 16 & 0xFF);
                    g += (rgb >> 8 & 0xFF);
                    b += (rgb & 0xFF);
                }
                if (count == 0) {
                    outPixels[index] = inPixels[index];
                }
                else {
                    a = PixelUtils.clamp(a / count);
                    r = PixelUtils.clamp(r / count);
                    g = PixelUtils.clamp(g / count);
                    b = PixelUtils.clamp(b / count);
                    outPixels[index] = (a << 24 | r << 16 | g << 8 | b);
                }
                ++index;
            }
        }
        if (this.premultiplyAlpha) {
            ImageMath.unpremultiply(outPixels, 0, inPixels.length);
        }
        this.setRGB(dst, 0, 0, width, height, outPixels);
        return dst;
    }
    
    @Override
    public String toString() {
        return "Blur/Motion Blur...";
    }
}
