package com.jhlabs.image;

import java.awt.Graphics2D;
import java.awt.image.ColorModel;
import java.awt.image.BufferedImageOp;
import java.util.Hashtable;
import java.awt.image.BufferedImage;

public class TileImageFilter extends AbstractBufferedImageOp
{
    private int width;
    private int height;
    private int tileWidth;
    private int tileHeight;
    
    public TileImageFilter() {
        this(32, 32);
    }
    
    public TileImageFilter(final int width, final int height) {
        this.width = width;
        this.height = height;
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
        final int tileWidth = src.getWidth();
        final int tileHeight = src.getHeight();
        if (dst == null) {
            final ColorModel dstCM = src.getColorModel();
            dst = new BufferedImage(dstCM, dstCM.createCompatibleWritableRaster(this.width, this.height), dstCM.isAlphaPremultiplied(), null);
        }
        final Graphics2D g = dst.createGraphics();
        for (int y = 0; y < this.height; y += tileHeight) {
            for (int x = 0; x < this.width; x += tileWidth) {
                g.drawImage(src, null, x, y);
            }
        }
        g.dispose();
        return dst;
    }
    
    @Override
    public String toString() {
        return "Tile";
    }
}
