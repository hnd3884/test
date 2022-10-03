package com.jhlabs.image;

import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.util.Hashtable;
import java.awt.image.BufferedImage;

public class FlipFilter extends AbstractBufferedImageOp
{
    public static final int FLIP_H = 1;
    public static final int FLIP_V = 2;
    public static final int FLIP_HV = 3;
    public static final int FLIP_90CW = 4;
    public static final int FLIP_90CCW = 5;
    public static final int FLIP_180 = 6;
    private int operation;
    private int width;
    private int height;
    private int newWidth;
    private int newHeight;
    
    public FlipFilter() {
        this(3);
    }
    
    public FlipFilter(final int operation) {
        this.operation = operation;
    }
    
    public void setOperation(final int operation) {
        this.operation = operation;
    }
    
    public int getOperation() {
        return this.operation;
    }
    
    public BufferedImage filter(final BufferedImage src, BufferedImage dst) {
        final int width = src.getWidth();
        final int height = src.getHeight();
        final int type = src.getType();
        final WritableRaster srcRaster = src.getRaster();
        final int[] inPixels = this.getRGB(src, 0, 0, width, height, null);
        final int x = 0;
        final int y = 0;
        final int w = width;
        final int h = height;
        int newX = 0;
        int newY = 0;
        int newW = w;
        int newH = h;
        switch (this.operation) {
            case 1: {
                newX = width - (x + w);
                break;
            }
            case 2: {
                newY = height - (y + h);
                break;
            }
            case 3: {
                newW = h;
                newH = w;
                newX = y;
                newY = x;
                break;
            }
            case 4: {
                newW = h;
                newH = w;
                newX = height - (y + h);
                newY = x;
                break;
            }
            case 5: {
                newW = h;
                newH = w;
                newX = y;
                newY = width - (x + w);
                break;
            }
            case 6: {
                newX = width - (x + w);
                newY = height - (y + h);
                break;
            }
        }
        final int[] newPixels = new int[newW * newH];
        for (int row = 0; row < h; ++row) {
            for (int col = 0; col < w; ++col) {
                final int index = row * width + col;
                int newRow = row;
                int newCol = col;
                switch (this.operation) {
                    case 1: {
                        newCol = w - col - 1;
                        break;
                    }
                    case 2: {
                        newRow = h - row - 1;
                        break;
                    }
                    case 3: {
                        newRow = col;
                        newCol = row;
                        break;
                    }
                    case 4: {
                        newRow = col;
                        newCol = h - row - 1;
                        break;
                    }
                    case 5: {
                        newRow = w - col - 1;
                        newCol = row;
                        break;
                    }
                    case 6: {
                        newRow = h - row - 1;
                        newCol = w - col - 1;
                        break;
                    }
                }
                final int newIndex = newRow * newW + newCol;
                newPixels[newIndex] = inPixels[index];
            }
        }
        if (dst == null) {
            final ColorModel dstCM = src.getColorModel();
            dst = new BufferedImage(dstCM, dstCM.createCompatibleWritableRaster(newW, newH), dstCM.isAlphaPremultiplied(), null);
        }
        final WritableRaster dstRaster = dst.getRaster();
        this.setRGB(dst, 0, 0, newW, newH, newPixels);
        return dst;
    }
    
    @Override
    public String toString() {
        switch (this.operation) {
            case 1: {
                return "Flip Horizontal";
            }
            case 2: {
                return "Flip Vertical";
            }
            case 3: {
                return "Flip Diagonal";
            }
            case 4: {
                return "Rotate 90";
            }
            case 5: {
                return "Rotate -90";
            }
            case 6: {
                return "Rotate 180";
            }
            default: {
                return "Flip";
            }
        }
    }
}
