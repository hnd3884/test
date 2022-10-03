package com.jhlabs.image;

import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.Hashtable;
import java.awt.image.ColorModel;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;

public abstract class AbstractBufferedImageOp implements BufferedImageOp, Cloneable
{
    public BufferedImage createCompatibleDestImage(final BufferedImage src, ColorModel dstCM) {
        if (dstCM == null) {
            dstCM = src.getColorModel();
        }
        return new BufferedImage(dstCM, dstCM.createCompatibleWritableRaster(src.getWidth(), src.getHeight()), dstCM.isAlphaPremultiplied(), null);
    }
    
    public Rectangle2D getBounds2D(final BufferedImage src) {
        return new Rectangle(0, 0, src.getWidth(), src.getHeight());
    }
    
    public Point2D getPoint2D(final Point2D srcPt, Point2D dstPt) {
        if (dstPt == null) {
            dstPt = new Point2D.Double();
        }
        dstPt.setLocation(srcPt.getX(), srcPt.getY());
        return dstPt;
    }
    
    public RenderingHints getRenderingHints() {
        return null;
    }
    
    public int[] getRGB(final BufferedImage image, final int x, final int y, final int width, final int height, final int[] pixels) {
        final int type = image.getType();
        if (type == 2 || type == 1) {
            return (int[])image.getRaster().getDataElements(x, y, width, height, pixels);
        }
        return image.getRGB(x, y, width, height, pixels, 0, width);
    }
    
    public void setRGB(final BufferedImage image, final int x, final int y, final int width, final int height, final int[] pixels) {
        final int type = image.getType();
        if (type == 2 || type == 1) {
            image.getRaster().setDataElements(x, y, width, height, pixels);
        }
        else {
            image.setRGB(x, y, width, height, pixels, 0, width);
        }
    }
    
    public Object clone() {
        try {
            return super.clone();
        }
        catch (final CloneNotSupportedException e) {
            return null;
        }
    }
}
