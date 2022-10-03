package com.jhlabs.image;

import java.awt.Graphics2D;
import java.awt.image.ColorModel;
import java.awt.image.RenderedImage;
import java.awt.geom.AffineTransform;
import java.util.Hashtable;
import java.awt.image.BufferedImage;

public class CropFilter extends AbstractBufferedImageOp
{
    private int x;
    private int y;
    private int width;
    private int height;
    
    public CropFilter() {
        this(0, 0, 32, 32);
    }
    
    public CropFilter(final int x, final int y, final int width, final int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
    
    public void setX(final int x) {
        this.x = x;
    }
    
    public int getX() {
        return this.x;
    }
    
    public void setY(final int y) {
        this.y = y;
    }
    
    public int getY() {
        return this.y;
    }
    
    public void setWidth(final int width) {
        this.width = width;
    }
    
    public int getWidth() {
        return this.width;
    }
    
    public void setHeight(final int height) {
        this.height = height;
    }
    
    public int getHeight() {
        return this.height;
    }
    
    public BufferedImage filter(final BufferedImage src, BufferedImage dst) {
        final int w = src.getWidth();
        final int h = src.getHeight();
        if (dst == null) {
            final ColorModel dstCM = src.getColorModel();
            dst = new BufferedImage(dstCM, dstCM.createCompatibleWritableRaster(this.width, this.height), dstCM.isAlphaPremultiplied(), null);
        }
        final Graphics2D g = dst.createGraphics();
        g.drawRenderedImage(src, AffineTransform.getTranslateInstance(-this.x, -this.y));
        g.dispose();
        return dst;
    }
    
    @Override
    public String toString() {
        return "Distort/Crop";
    }
}
