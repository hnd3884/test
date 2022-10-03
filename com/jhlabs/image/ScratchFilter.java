package com.jhlabs.image;

import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.Stroke;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.RenderingHints;
import java.util.Random;
import java.util.ArrayList;
import java.awt.image.ColorModel;
import java.awt.image.BufferedImage;

public class ScratchFilter extends AbstractBufferedImageOp
{
    private float density;
    private float angle;
    private float angleVariation;
    private float width;
    private float length;
    private int color;
    private int seed;
    
    public ScratchFilter() {
        this.density = 0.1f;
        this.angleVariation = 1.0f;
        this.width = 0.5f;
        this.length = 0.5f;
        this.color = -1;
        this.seed = 0;
    }
    
    public void setAngle(final float angle) {
        this.angle = angle;
    }
    
    public float getAngle() {
        return this.angle;
    }
    
    public void setAngleVariation(final float angleVariation) {
        this.angleVariation = angleVariation;
    }
    
    public float getAngleVariation() {
        return this.angleVariation;
    }
    
    public void setDensity(final float density) {
        this.density = density;
    }
    
    public float getDensity() {
        return this.density;
    }
    
    public void setLength(final float length) {
        this.length = length;
    }
    
    public float getLength() {
        return this.length;
    }
    
    public void setWidth(final float width) {
        this.width = width;
    }
    
    public float getWidth() {
        return this.width;
    }
    
    public void setColor(final int color) {
        this.color = color;
    }
    
    public int getColor() {
        return this.color;
    }
    
    public void setSeed(final int seed) {
        this.seed = seed;
    }
    
    public int getSeed() {
        return this.seed;
    }
    
    public BufferedImage filter(final BufferedImage src, BufferedImage dst) {
        if (dst == null) {
            dst = this.createCompatibleDestImage(src, null);
        }
        final int width = src.getWidth();
        final int height = src.getHeight();
        final int numScratches = (int)(this.density * width * height / 100.0f);
        final ArrayList lines = new ArrayList();
        final float l = this.length * width;
        final Random random = new Random(this.seed);
        final Graphics2D g = dst.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(new Color(this.color));
        g.setStroke(new BasicStroke(this.width));
        for (int i = 0; i < numScratches; ++i) {
            final float x = width * random.nextFloat();
            final float y = height * random.nextFloat();
            final float a = this.angle + 6.2831855f * (this.angleVariation * (random.nextFloat() - 0.5f));
            final float s = (float)Math.sin(a) * l;
            final float c = (float)Math.cos(a) * l;
            final float x2 = x - c;
            final float y2 = y - s;
            final float x3 = x + c;
            final float y3 = y + s;
            g.drawLine((int)x2, (int)y2, (int)x3, (int)y3);
            lines.add(new Line2D.Float(x2, y2, x3, y3));
        }
        g.dispose();
        return dst;
    }
    
    @Override
    public String toString() {
        return "Render/Scratches...";
    }
}
