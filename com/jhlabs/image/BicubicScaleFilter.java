package com.jhlabs.image;

import java.awt.Graphics2D;
import java.awt.image.ColorModel;
import java.awt.image.ImageObserver;
import java.awt.Image;
import java.awt.RenderingHints;
import java.util.Hashtable;
import java.awt.image.BufferedImage;

public class BicubicScaleFilter extends AbstractBufferedImageOp
{
    private int width;
    private int height;
    
    public BicubicScaleFilter() {
        this(32, 32);
    }
    
    public BicubicScaleFilter(final int width, final int height) {
        this.width = width;
        this.height = height;
    }
    
    public BufferedImage filter(final BufferedImage src, BufferedImage dst) {
        final int w = src.getWidth();
        final int h = src.getHeight();
        if (dst == null) {
            final ColorModel dstCM = src.getColorModel();
            dst = new BufferedImage(dstCM, dstCM.createCompatibleWritableRaster(this.width, this.height), dstCM.isAlphaPremultiplied(), null);
        }
        final Graphics2D g = dst.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.drawImage(src, 0, 0, this.width, this.height, null);
        g.dispose();
        return dst;
    }
    
    @Override
    public String toString() {
        return "Distort/Bicubic Scale";
    }
}
