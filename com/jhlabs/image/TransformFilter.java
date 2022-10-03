package com.jhlabs.image;

import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.util.Hashtable;
import java.awt.image.BufferedImage;
import java.awt.Rectangle;

public abstract class TransformFilter extends AbstractBufferedImageOp
{
    public static final int ZERO = 0;
    public static final int CLAMP = 1;
    public static final int WRAP = 2;
    public static final int NEAREST_NEIGHBOUR = 0;
    public static final int BILINEAR = 1;
    protected int edgeAction;
    protected int interpolation;
    protected Rectangle transformedSpace;
    protected Rectangle originalSpace;
    
    public TransformFilter() {
        this.edgeAction = 0;
        this.interpolation = 1;
    }
    
    public void setEdgeAction(final int edgeAction) {
        this.edgeAction = edgeAction;
    }
    
    public int getEdgeAction() {
        return this.edgeAction;
    }
    
    public void setInterpolation(final int interpolation) {
        this.interpolation = interpolation;
    }
    
    public int getInterpolation() {
        return this.interpolation;
    }
    
    protected abstract void transformInverse(final int p0, final int p1, final float[] p2);
    
    protected void transformSpace(final Rectangle rect) {
    }
    
    public BufferedImage filter(final BufferedImage src, BufferedImage dst) {
        final int width = src.getWidth();
        final int height = src.getHeight();
        final int type = src.getType();
        final WritableRaster srcRaster = src.getRaster();
        this.originalSpace = new Rectangle(0, 0, width, height);
        this.transformSpace(this.transformedSpace = new Rectangle(0, 0, width, height));
        if (dst == null) {
            final ColorModel dstCM = src.getColorModel();
            dst = new BufferedImage(dstCM, dstCM.createCompatibleWritableRaster(this.transformedSpace.width, this.transformedSpace.height), dstCM.isAlphaPremultiplied(), null);
        }
        final WritableRaster dstRaster = dst.getRaster();
        final int[] inPixels = this.getRGB(src, 0, 0, width, height, null);
        if (this.interpolation == 0) {
            return this.filterPixelsNN(dst, width, height, inPixels, this.transformedSpace);
        }
        final int srcWidth = width;
        final int srcHeight = height;
        final int srcWidth2 = width - 1;
        final int srcHeight2 = height - 1;
        final int outWidth = this.transformedSpace.width;
        final int outHeight = this.transformedSpace.height;
        final int index = 0;
        final int[] outPixels = new int[outWidth];
        final int outX = this.transformedSpace.x;
        final int outY = this.transformedSpace.y;
        final float[] out = new float[2];
        for (int y = 0; y < outHeight; ++y) {
            for (int x = 0; x < outWidth; ++x) {
                this.transformInverse(outX + x, outY + y, out);
                final int srcX = (int)Math.floor(out[0]);
                final int srcY = (int)Math.floor(out[1]);
                final float xWeight = out[0] - srcX;
                final float yWeight = out[1] - srcY;
                int nw;
                int ne;
                int sw;
                int se;
                if (srcX >= 0 && srcX < srcWidth2 && srcY >= 0 && srcY < srcHeight2) {
                    final int i = srcWidth * srcY + srcX;
                    nw = inPixels[i];
                    ne = inPixels[i + 1];
                    sw = inPixels[i + srcWidth];
                    se = inPixels[i + srcWidth + 1];
                }
                else {
                    nw = this.getPixel(inPixels, srcX, srcY, srcWidth, srcHeight);
                    ne = this.getPixel(inPixels, srcX + 1, srcY, srcWidth, srcHeight);
                    sw = this.getPixel(inPixels, srcX, srcY + 1, srcWidth, srcHeight);
                    se = this.getPixel(inPixels, srcX + 1, srcY + 1, srcWidth, srcHeight);
                }
                outPixels[x] = ImageMath.bilinearInterpolate(xWeight, yWeight, nw, ne, sw, se);
            }
            this.setRGB(dst, 0, y, this.transformedSpace.width, 1, outPixels);
        }
        return dst;
    }
    
    private final int getPixel(final int[] pixels, final int x, final int y, final int width, final int height) {
        if (x >= 0 && x < width && y >= 0 && y < height) {
            return pixels[y * width + x];
        }
        switch (this.edgeAction) {
            default: {
                return 0;
            }
            case 2: {
                return pixels[ImageMath.mod(y, height) * width + ImageMath.mod(x, width)];
            }
            case 1: {
                return pixels[ImageMath.clamp(y, 0, height - 1) * width + ImageMath.clamp(x, 0, width - 1)];
            }
        }
    }
    
    protected BufferedImage filterPixelsNN(final BufferedImage dst, final int width, final int height, final int[] inPixels, final Rectangle transformedSpace) {
        final int srcWidth = width;
        final int srcHeight = height;
        final int outWidth = transformedSpace.width;
        final int outHeight = transformedSpace.height;
        final int[] outPixels = new int[outWidth];
        final int outX = transformedSpace.x;
        final int outY = transformedSpace.y;
        final int[] rgb = new int[4];
        final float[] out = new float[2];
        for (int y = 0; y < outHeight; ++y) {
            for (int x = 0; x < outWidth; ++x) {
                this.transformInverse(outX + x, outY + y, out);
                final int srcX = (int)out[0];
                final int srcY = (int)out[1];
                if (out[0] < 0.0f || srcX >= srcWidth || out[1] < 0.0f || srcY >= srcHeight) {
                    int p = 0;
                    switch (this.edgeAction) {
                        default: {
                            p = 0;
                            break;
                        }
                        case 2: {
                            p = inPixels[ImageMath.mod(srcY, srcHeight) * srcWidth + ImageMath.mod(srcX, srcWidth)];
                            break;
                        }
                        case 1: {
                            p = inPixels[ImageMath.clamp(srcY, 0, srcHeight - 1) * srcWidth + ImageMath.clamp(srcX, 0, srcWidth - 1)];
                            break;
                        }
                    }
                    outPixels[x] = p;
                }
                else {
                    final int i = srcWidth * srcY + srcX;
                    rgb[0] = inPixels[i];
                    outPixels[x] = inPixels[i];
                }
            }
            this.setRGB(dst, 0, y, transformedSpace.width, 1, outPixels);
        }
        return dst;
    }
}
