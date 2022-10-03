package com.jhlabs.math;

import com.jhlabs.image.PixelUtils;
import com.jhlabs.image.ImageMath;
import java.awt.image.PixelGrabber;
import java.awt.Image;
import java.awt.image.BufferedImage;

public class ImageFunction2D implements Function2D
{
    public static final int ZERO = 0;
    public static final int CLAMP = 1;
    public static final int WRAP = 2;
    protected int[] pixels;
    protected int width;
    protected int height;
    protected int edgeAction;
    protected boolean alpha;
    
    public ImageFunction2D(final BufferedImage image) {
        this(image, false);
    }
    
    public ImageFunction2D(final BufferedImage image, final boolean alpha) {
        this(image, 0, alpha);
    }
    
    public ImageFunction2D(final BufferedImage image, final int edgeAction, final boolean alpha) {
        this.edgeAction = 0;
        this.alpha = false;
        this.init(this.getRGB(image, 0, 0, image.getWidth(), image.getHeight(), null), image.getWidth(), image.getHeight(), edgeAction, alpha);
    }
    
    public ImageFunction2D(final int[] pixels, final int width, final int height, final int edgeAction, final boolean alpha) {
        this.edgeAction = 0;
        this.alpha = false;
        this.init(pixels, width, height, edgeAction, alpha);
    }
    
    public ImageFunction2D(final Image image) {
        this(image, 0, false);
    }
    
    public ImageFunction2D(final Image image, final int edgeAction, final boolean alpha) {
        this.edgeAction = 0;
        this.alpha = false;
        final PixelGrabber pg = new PixelGrabber(image, 0, 0, -1, -1, null, 0, -1);
        try {
            pg.grabPixels();
        }
        catch (final InterruptedException e) {
            throw new RuntimeException("interrupted waiting for pixels!");
        }
        if ((pg.status() & 0x80) != 0x0) {
            throw new RuntimeException("image fetch aborted");
        }
        this.init((int[])pg.getPixels(), pg.getWidth(), pg.getHeight(), edgeAction, alpha);
    }
    
    public int[] getRGB(final BufferedImage image, final int x, final int y, final int width, final int height, final int[] pixels) {
        final int type = image.getType();
        if (type == 2 || type == 1) {
            return (int[])image.getRaster().getDataElements(x, y, width, height, pixels);
        }
        return image.getRGB(x, y, width, height, pixels, 0, width);
    }
    
    public void init(final int[] pixels, final int width, final int height, final int edgeAction, final boolean alpha) {
        this.pixels = pixels;
        this.width = width;
        this.height = height;
        this.edgeAction = edgeAction;
        this.alpha = alpha;
    }
    
    public float evaluate(final float x, final float y) {
        int ix = (int)x;
        int iy = (int)y;
        if (this.edgeAction == 2) {
            ix = ImageMath.mod(ix, this.width);
            iy = ImageMath.mod(iy, this.height);
        }
        else if (ix < 0 || iy < 0 || ix >= this.width || iy >= this.height) {
            if (this.edgeAction == 0) {
                return 0.0f;
            }
            if (ix < 0) {
                ix = 0;
            }
            else if (ix >= this.width) {
                ix = this.width - 1;
            }
            if (iy < 0) {
                iy = 0;
            }
            else if (iy >= this.height) {
                iy = this.height - 1;
            }
        }
        return this.alpha ? ((this.pixels[iy * this.width + ix] >> 24 & 0xFF) / 255.0f) : (PixelUtils.brightness(this.pixels[iy * this.width + ix]) / 255.0f);
    }
    
    public void setEdgeAction(final int edgeAction) {
        this.edgeAction = edgeAction;
    }
    
    public int getEdgeAction() {
        return this.edgeAction;
    }
    
    public int getWidth() {
        return this.width;
    }
    
    public int getHeight() {
        return this.height;
    }
    
    public int[] getPixels() {
        return this.pixels;
    }
}
