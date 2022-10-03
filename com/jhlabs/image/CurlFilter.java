package com.jhlabs.image;

import java.awt.image.WritableRaster;
import java.awt.image.ColorModel;
import java.util.Hashtable;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

public class CurlFilter extends TransformFilter
{
    private float angle;
    private float transition;
    private float width;
    private float height;
    private float radius;
    
    public CurlFilter() {
        this.angle = 0.0f;
        this.transition = 0.0f;
        this.setEdgeAction(0);
    }
    
    public void setTransition(final float transition) {
        this.transition = transition;
    }
    
    public float getTransition() {
        return this.transition;
    }
    
    public void setAngle(final float angle) {
        this.angle = angle;
    }
    
    public float getAngle() {
        return this.angle;
    }
    
    public void setRadius(final float radius) {
        this.radius = radius;
    }
    
    public float getRadius() {
        return this.radius;
    }
    
    @Override
    public BufferedImage filter(final BufferedImage src, BufferedImage dst) {
        final int width = src.getWidth();
        final int height = src.getHeight();
        this.width = (float)src.getWidth();
        this.height = (float)src.getHeight();
        final int type = src.getType();
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
        final float[] out = new float[4];
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
                int rgb = ImageMath.bilinearInterpolate(xWeight, yWeight, nw, ne, sw, se);
                int r = rgb >> 16 & 0xFF;
                int g = rgb >> 8 & 0xFF;
                int b = rgb & 0xFF;
                final float shade = out[2];
                r *= (int)shade;
                g *= (int)shade;
                b *= (int)shade;
                rgb = ((rgb & 0xFF000000) | r << 16 | g << 8 | b);
                if (out[3] != 0.0f) {
                    outPixels[x] = PixelUtils.combinePixels(rgb, inPixels[srcWidth * y + x], 1);
                }
                else {
                    outPixels[x] = rgb;
                }
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
    
    @Override
    protected void transformInverse(final int x, final int y, final float[] out) {
        final float t = this.transition;
        float px = (float)x;
        float py = (float)y;
        final float s = (float)Math.sin(this.angle);
        final float c = (float)Math.cos(this.angle);
        float tx = t * this.width;
        tx = t * (float)Math.sqrt(this.width * this.width + this.height * this.height);
        final float xoffset = (c < 0.0f) ? this.width : 0.0f;
        final float yoffset = (s < 0.0f) ? this.height : 0.0f;
        px -= xoffset;
        py -= yoffset;
        float qx = px * c + py * s;
        final float qy = -px * s + py * c;
        final boolean outside = qx < tx;
        final boolean unfolded = qx > tx * 2.0f;
        final boolean oncurl = !outside && !unfolded;
        qx = ((qx > tx * 2.0f) ? qx : (2.0f * tx - qx));
        px = qx * c - qy * s;
        py = qx * s + qy * c;
        px += xoffset;
        py += yoffset;
        final boolean offpage = px < 0.0f || py < 0.0f || px >= this.width || py >= this.height;
        if (offpage && oncurl) {
            px = (float)x;
            py = (float)y;
        }
        final float shade = (!offpage && oncurl) ? (1.9f * (1.0f - (float)Math.cos(Math.exp((qx - tx) / this.radius)))) : 0.0f;
        out[2] = 1.0f - shade;
        if (outside) {
            out[0] = (out[1] = -1.0f);
        }
        else {
            out[0] = px;
            out[1] = py;
        }
        out[3] = ((!offpage && oncurl) ? 1.0f : 0.0f);
    }
    
    @Override
    public String toString() {
        return "Distort/Curl...";
    }
    
    static class Sampler
    {
        private int edgeAction;
        private int width;
        private int height;
        private int[] inPixels;
        
        public Sampler(final BufferedImage image) {
            final int width = image.getWidth();
            final int height = image.getHeight();
            final int type = image.getType();
            this.inPixels = ImageUtils.getRGB(image, 0, 0, width, height, null);
        }
        
        public int sample(final float x, final float y) {
            final int srcX = (int)Math.floor(x);
            final int srcY = (int)Math.floor(y);
            final float xWeight = x - srcX;
            final float yWeight = y - srcY;
            int nw;
            int ne;
            int sw;
            int se;
            if (srcX >= 0 && srcX < this.width - 1 && srcY >= 0 && srcY < this.height - 1) {
                final int i = this.width * srcY + srcX;
                nw = this.inPixels[i];
                ne = this.inPixels[i + 1];
                sw = this.inPixels[i + this.width];
                se = this.inPixels[i + this.width + 1];
            }
            else {
                nw = this.getPixel(this.inPixels, srcX, srcY, this.width, this.height);
                ne = this.getPixel(this.inPixels, srcX + 1, srcY, this.width, this.height);
                sw = this.getPixel(this.inPixels, srcX, srcY + 1, this.width, this.height);
                se = this.getPixel(this.inPixels, srcX + 1, srcY + 1, this.width, this.height);
            }
            return ImageMath.bilinearInterpolate(xWeight, yWeight, nw, ne, sw, se);
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
    }
}
