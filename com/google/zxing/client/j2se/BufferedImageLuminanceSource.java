package com.google.zxing.client.j2se;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.ImageObserver;
import java.awt.Image;
import java.awt.image.BufferedImage;
import com.google.zxing.LuminanceSource;

public final class BufferedImageLuminanceSource extends LuminanceSource
{
    private final BufferedImage image;
    private final int left;
    private final int top;
    
    public BufferedImageLuminanceSource(final BufferedImage image) {
        this(image, 0, 0, image.getWidth(), image.getHeight());
    }
    
    public BufferedImageLuminanceSource(final BufferedImage image, final int left, final int top, final int width, final int height) {
        super(width, height);
        final int sourceWidth = image.getWidth();
        final int sourceHeight = image.getHeight();
        if (left + width > sourceWidth || top + height > sourceHeight) {
            throw new IllegalArgumentException("Crop rectangle does not fit within image data.");
        }
        for (int y = top; y < top + height; ++y) {
            for (int x = left; x < left + width; ++x) {
                if ((image.getRGB(x, y) & 0xFF000000) == 0x0) {
                    image.setRGB(x, y, -1);
                }
            }
        }
        this.image = new BufferedImage(sourceWidth, sourceHeight, 10);
        this.image.getGraphics().drawImage(image, 0, 0, null);
        this.left = left;
        this.top = top;
    }
    
    public byte[] getRow(final int y, byte[] row) {
        if (y < 0 || y >= this.getHeight()) {
            throw new IllegalArgumentException("Requested row is outside the image: " + y);
        }
        final int width = this.getWidth();
        if (row == null || row.length < width) {
            row = new byte[width];
        }
        this.image.getRaster().getDataElements(this.left, this.top + y, width, 1, row);
        return row;
    }
    
    public byte[] getMatrix() {
        final int width = this.getWidth();
        final int height = this.getHeight();
        final int area = width * height;
        final byte[] matrix = new byte[area];
        this.image.getRaster().getDataElements(this.left, this.top, width, height, matrix);
        return matrix;
    }
    
    public boolean isCropSupported() {
        return true;
    }
    
    public LuminanceSource crop(final int left, final int top, final int width, final int height) {
        return new BufferedImageLuminanceSource(this.image, this.left + left, this.top + top, width, height);
    }
    
    public boolean isRotateSupported() {
        return true;
    }
    
    public LuminanceSource rotateCounterClockwise() {
        final int sourceWidth = this.image.getWidth();
        final int sourceHeight = this.image.getHeight();
        final AffineTransform transform = new AffineTransform(0.0, -1.0, 1.0, 0.0, 0.0, sourceWidth);
        final BufferedImage rotatedImage = new BufferedImage(sourceHeight, sourceWidth, 10);
        final Graphics2D g = rotatedImage.createGraphics();
        g.drawImage(this.image, transform, null);
        g.dispose();
        final int width = this.getWidth();
        return new BufferedImageLuminanceSource(rotatedImage, this.top, sourceWidth - (this.left + width), this.getHeight(), width);
    }
}
