package com.jhlabs.image;

import java.awt.image.BufferedImage;
import java.awt.Point;

public class FieldWarpFilter extends TransformFilter
{
    private float amount;
    private float power;
    private float strength;
    private Line[] inLines;
    private Line[] outLines;
    private Line[] intermediateLines;
    private float width;
    private float height;
    
    public FieldWarpFilter() {
        this.amount = 1.0f;
        this.power = 1.0f;
        this.strength = 2.0f;
    }
    
    public void setAmount(final float amount) {
        this.amount = amount;
    }
    
    public float getAmount() {
        return this.amount;
    }
    
    public void setPower(final float power) {
        this.power = power;
    }
    
    public float getPower() {
        return this.power;
    }
    
    public void setStrength(final float strength) {
        this.strength = strength;
    }
    
    public float getStrength() {
        return this.strength;
    }
    
    public void setInLines(final Line[] inLines) {
        this.inLines = inLines;
    }
    
    public Line[] getInLines() {
        return this.inLines;
    }
    
    public void setOutLines(final Line[] outLines) {
        this.outLines = outLines;
    }
    
    public Line[] getOutLines() {
        return this.outLines;
    }
    
    protected void transform(final int x, final int y, final Point out) {
    }
    
    @Override
    protected void transformInverse(final int x, final int y, final float[] out) {
        float u = 0.0f;
        float v = 0.0f;
        float fraction = 0.0f;
        final float a = 0.001f;
        final float b = 1.5f * this.strength + 0.5f;
        final float p = this.power;
        float totalWeight = 0.0f;
        float sumX = 0.0f;
        float sumY = 0.0f;
        for (int line = 0; line < this.inLines.length; ++line) {
            final Line l1 = this.inLines[line];
            final Line i = this.intermediateLines[line];
            float dx = (float)(x - i.x1);
            float dy = (float)(y - i.y1);
            fraction = (dx * i.dx + dy * i.dy) / i.lengthSquared;
            final float fdist = (dy * i.dx - dx * i.dy) / i.length;
            float distance;
            if (fraction <= 0.0f) {
                distance = (float)Math.sqrt(dx * dx + dy * dy);
            }
            else if (fraction >= 1.0f) {
                dx = (float)(x - i.x2);
                dy = (float)(y - i.y2);
                distance = (float)Math.sqrt(dx * dx + dy * dy);
            }
            else if (fdist >= 0.0f) {
                distance = fdist;
            }
            else {
                distance = -fdist;
            }
            u = l1.x1 + fraction * l1.dx - fdist * l1.dy / l1.length;
            v = l1.y1 + fraction * l1.dy + fdist * l1.dx / l1.length;
            final float weight = (float)Math.pow(Math.pow(i.length, p) / (a + distance), b);
            sumX += (u - x) * weight;
            sumY += (v - y) * weight;
            totalWeight += weight;
        }
        out[0] = x + sumX / totalWeight + 0.5f;
        out[1] = y + sumY / totalWeight + 0.5f;
    }
    
    @Override
    public BufferedImage filter(final BufferedImage src, BufferedImage dst) {
        this.width = this.width;
        this.height = this.height;
        if (this.inLines != null && this.outLines != null) {
            this.intermediateLines = new Line[this.inLines.length];
            for (int line = 0; line < this.inLines.length; ++line) {
                final Line[] intermediateLines = this.intermediateLines;
                final int n = line;
                final Line line2 = new Line(ImageMath.lerp(this.amount, this.inLines[line].x1, this.outLines[line].x1), ImageMath.lerp(this.amount, this.inLines[line].y1, this.outLines[line].y1), ImageMath.lerp(this.amount, this.inLines[line].x2, this.outLines[line].x2), ImageMath.lerp(this.amount, this.inLines[line].y2, this.outLines[line].y2));
                intermediateLines[n] = line2;
                final Line l = line2;
                l.setup();
                this.inLines[line].setup();
            }
            dst = super.filter(src, dst);
            this.intermediateLines = null;
            return dst;
        }
        return src;
    }
    
    @Override
    public String toString() {
        return "Distort/Field Warp...";
    }
    
    public static class Line
    {
        public int x1;
        public int y1;
        public int x2;
        public int y2;
        public int dx;
        public int dy;
        public float length;
        public float lengthSquared;
        
        public Line(final int x1, final int y1, final int x2, final int y2) {
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
        }
        
        public void setup() {
            this.dx = this.x2 - this.x1;
            this.dy = this.y2 - this.y1;
            this.lengthSquared = (float)(this.dx * this.dx + this.dy * this.dy);
            this.length = (float)Math.sqrt(this.lengthSquared);
        }
    }
}
