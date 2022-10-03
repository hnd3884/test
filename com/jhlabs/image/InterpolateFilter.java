package com.jhlabs.image;

import java.awt.image.WritableRaster;
import java.awt.image.ColorModel;
import java.awt.image.BufferedImage;

public class InterpolateFilter extends AbstractBufferedImageOp
{
    private BufferedImage destination;
    private float interpolation;
    
    public void setDestination(final BufferedImage destination) {
        this.destination = destination;
    }
    
    public BufferedImage getDestination() {
        return this.destination;
    }
    
    public void setInterpolation(final float interpolation) {
        this.interpolation = interpolation;
    }
    
    public float getInterpolation() {
        return this.interpolation;
    }
    
    public BufferedImage filter(final BufferedImage src, BufferedImage dst) {
        int width = src.getWidth();
        int height = src.getHeight();
        final int type = src.getType();
        final WritableRaster srcRaster = src.getRaster();
        if (dst == null) {
            dst = this.createCompatibleDestImage(src, null);
        }
        final WritableRaster dstRaster = dst.getRaster();
        if (this.destination != null) {
            width = Math.min(width, this.destination.getWidth());
            height = Math.min(height, this.destination.getWidth());
            int[] pixels1 = null;
            int[] pixels2 = null;
            for (int y = 0; y < height; ++y) {
                pixels1 = this.getRGB(src, 0, y, width, 1, pixels1);
                pixels2 = this.getRGB(this.destination, 0, y, width, 1, pixels2);
                for (int x = 0; x < width; ++x) {
                    final int rgb1 = pixels1[x];
                    final int rgb2 = pixels2[x];
                    final int a1 = rgb1 >> 24 & 0xFF;
                    int r1 = rgb1 >> 16 & 0xFF;
                    int g1 = rgb1 >> 8 & 0xFF;
                    int b1 = rgb1 & 0xFF;
                    final int a2 = rgb2 >> 24 & 0xFF;
                    final int r2 = rgb2 >> 16 & 0xFF;
                    final int g2 = rgb2 >> 8 & 0xFF;
                    final int b2 = rgb2 & 0xFF;
                    r1 = PixelUtils.clamp(ImageMath.lerp(this.interpolation, r1, r2));
                    g1 = PixelUtils.clamp(ImageMath.lerp(this.interpolation, g1, g2));
                    b1 = PixelUtils.clamp(ImageMath.lerp(this.interpolation, b1, b2));
                    pixels1[x] = (a1 << 24 | r1 << 16 | g1 << 8 | b1);
                }
                this.setRGB(dst, 0, y, width, 1, pixels1);
            }
        }
        return dst;
    }
    
    @Override
    public String toString() {
        return "Effects/Interpolate...";
    }
}
