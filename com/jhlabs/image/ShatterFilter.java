package com.jhlabs.image;

import java.awt.geom.AffineTransform;
import java.awt.Shape;
import java.awt.Graphics2D;
import java.awt.image.ImageObserver;
import java.awt.Image;
import java.awt.Color;
import java.awt.Rectangle;
import java.util.Random;
import java.awt.image.ColorModel;
import java.awt.image.BufferedImage;
import java.awt.geom.Point2D;

public class ShatterFilter extends AbstractBufferedImageOp
{
    private float centreX;
    private float centreY;
    private float distance;
    private float transition;
    private float rotation;
    private float zoom;
    private float startAlpha;
    private float endAlpha;
    private int iterations;
    private int tile;
    
    public ShatterFilter() {
        this.centreX = 0.5f;
        this.centreY = 0.5f;
        this.startAlpha = 1.0f;
        this.endAlpha = 1.0f;
        this.iterations = 5;
    }
    
    public void setTransition(final float transition) {
        this.transition = transition;
    }
    
    public float getTransition() {
        return this.transition;
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
    
    public void setTile(final int tile) {
        this.tile = tile;
    }
    
    public int getTile() {
        return this.tile;
    }
    
    public BufferedImage filter(final BufferedImage src, BufferedImage dst) {
        if (dst == null) {
            dst = this.createCompatibleDestImage(src, null);
        }
        final float width = (float)src.getWidth();
        final float height = (float)src.getHeight();
        final float cx = src.getWidth() * this.centreX;
        final float cy = src.getHeight() * this.centreY;
        final float imageRadius = (float)Math.sqrt(cx * cx + cy * cy);
        final int numTiles = this.iterations * this.iterations;
        final Tile[] shapes = new Tile[numTiles];
        final float[] rx = new float[numTiles];
        final float[] ry = new float[numTiles];
        final float[] rz = new float[numTiles];
        final Graphics2D g = dst.createGraphics();
        final Random random = new Random(0L);
        final float lastx = 0.0f;
        final float lasty = 0.0f;
        for (int y = 0; y < this.iterations; ++y) {
            final int y2 = (int)height * y / this.iterations;
            final int y3 = (int)height * (y + 1) / this.iterations;
            for (int x = 0; x < this.iterations; ++x) {
                final int i = y * this.iterations + x;
                final int x2 = (int)width * x / this.iterations;
                final int x3 = (int)width * (x + 1) / this.iterations;
                rx[i] = this.tile * random.nextFloat();
                ry[i] = this.tile * random.nextFloat();
                ry[i] = (rx[i] = 0.0f);
                rz[i] = this.tile * (2.0f * random.nextFloat() - 1.0f);
                final Shape p = new Rectangle(x2, y2, x3 - x2, y3 - y2);
                shapes[i] = new Tile();
                shapes[i].shape = p;
                shapes[i].x = (x2 + x3) * 0.5f;
                shapes[i].y = (y2 + y3) * 0.5f;
                shapes[i].vx = width - (cx - x);
                shapes[i].vy = height - (cy - y);
                shapes[i].w = (float)(x3 - x2);
                shapes[i].h = (float)(y3 - y2);
            }
        }
        for (int j = 0; j < numTiles; ++j) {
            final float h = j / (float)numTiles;
            final double angle = h * 2.0f * 3.141592653589793;
            float x4 = this.transition * width * (float)Math.cos(angle);
            float y4 = this.transition * height * (float)Math.sin(angle);
            final Tile tile = shapes[j];
            final Rectangle r = tile.shape.getBounds();
            final AffineTransform t = g.getTransform();
            x4 = tile.x + this.transition * tile.vx;
            y4 = tile.y + this.transition * tile.vy;
            g.translate(x4, y4);
            g.rotate(this.transition * rz[j]);
            g.setColor(Color.getHSBColor(h, 1.0f, 1.0f));
            final Shape clip = g.getClip();
            g.clip(tile.shape);
            g.drawImage(src, 0, 0, null);
            g.setClip(clip);
            g.setTransform(t);
        }
        g.dispose();
        return dst;
    }
    
    @Override
    public String toString() {
        return "Transition/Shatter...";
    }
    
    static class Tile
    {
        float x;
        float y;
        float vx;
        float vy;
        float w;
        float h;
        float rotation;
        Shape shape;
    }
}
