package com.jhlabs.image;

import java.awt.image.WritableRaster;
import java.awt.image.Raster;
import java.awt.Shape;
import java.awt.Rectangle;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Component;
import java.awt.image.RenderedImage;
import java.awt.geom.AffineTransform;
import java.awt.Graphics2D;
import java.awt.image.ImageObserver;
import java.awt.Image;
import java.awt.image.PixelGrabber;
import java.awt.image.ImageProducer;
import java.awt.image.BufferedImage;

public abstract class ImageUtils
{
    private static BufferedImage backgroundImage;
    
    public static BufferedImage createImage(final ImageProducer producer) {
        final PixelGrabber pg = new PixelGrabber(producer, 0, 0, -1, -1, null, 0, 0);
        try {
            pg.grabPixels();
        }
        catch (final InterruptedException e) {
            throw new RuntimeException("Image fetch interrupted");
        }
        if ((pg.status() & 0x80) != 0x0) {
            throw new RuntimeException("Image fetch aborted");
        }
        if ((pg.status() & 0x40) != 0x0) {
            throw new RuntimeException("Image fetch error");
        }
        final BufferedImage p = new BufferedImage(pg.getWidth(), pg.getHeight(), 2);
        p.setRGB(0, 0, pg.getWidth(), pg.getHeight(), (int[])pg.getPixels(), 0, pg.getWidth());
        return p;
    }
    
    public static BufferedImage convertImageToARGB(final Image image) {
        if (image instanceof BufferedImage && ((BufferedImage)image).getType() == 2) {
            return (BufferedImage)image;
        }
        final BufferedImage p = new BufferedImage(image.getWidth(null), image.getHeight(null), 2);
        final Graphics2D g = p.createGraphics();
        g.drawImage(image, 0, 0, null);
        g.dispose();
        return p;
    }
    
    public static BufferedImage getSubimage(final BufferedImage image, final int x, final int y, final int w, final int h) {
        final BufferedImage newImage = new BufferedImage(w, h, 2);
        final Graphics2D g = newImage.createGraphics();
        g.drawRenderedImage(image, AffineTransform.getTranslateInstance(-x, -y));
        g.dispose();
        return newImage;
    }
    
    public static BufferedImage cloneImage(final BufferedImage image) {
        final BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), 2);
        final Graphics2D g = newImage.createGraphics();
        g.drawRenderedImage(image, null);
        g.dispose();
        return newImage;
    }
    
    public static void paintCheckedBackground(final Component c, final Graphics g, int x, int y, final int width, final int height) {
        if (ImageUtils.backgroundImage == null) {
            ImageUtils.backgroundImage = new BufferedImage(64, 64, 2);
            final Graphics bg = ImageUtils.backgroundImage.createGraphics();
            for (int by = 0; by < 64; by += 8) {
                for (int bx = 0; bx < 64; bx += 8) {
                    bg.setColor((((bx ^ by) & 0x8) != 0x0) ? Color.lightGray : Color.white);
                    bg.fillRect(bx, by, 8, 8);
                }
            }
            bg.dispose();
        }
        if (ImageUtils.backgroundImage != null) {
            final Shape saveClip = g.getClip();
            Rectangle r = g.getClipBounds();
            if (r == null) {
                r = new Rectangle(c.getSize());
            }
            r = r.intersection(new Rectangle(x, y, width, height));
            g.setClip(r);
            final int w = ImageUtils.backgroundImage.getWidth();
            final int h = ImageUtils.backgroundImage.getHeight();
            if (w != -1 && h != -1) {
                final int x2 = r.x / w * w;
                final int y2 = r.y / h * h;
                final int x3 = (r.x + r.width + w - 1) / w * w;
                int y3;
                for (y3 = (r.y + r.height + h - 1) / h * h, y = y2; y < y3; y += h) {
                    for (x = x2; x < x3; x += w) {
                        g.drawImage(ImageUtils.backgroundImage, x, y, c);
                    }
                }
            }
            g.setClip(saveClip);
        }
    }
    
    public static Rectangle getSelectedBounds(final BufferedImage p) {
        final int width = p.getWidth();
        final int height = p.getHeight();
        int maxX = 0;
        int maxY = 0;
        int minX = width;
        int minY = height;
        boolean anySelected = false;
        int[] pixels = null;
        int y1;
        for (y1 = height - 1; y1 >= 0; --y1) {
            pixels = getRGB(p, 0, y1, width, 1, pixels);
            for (int x = 0; x < minX; ++x) {
                if ((pixels[x] & 0xFF000000) != 0x0) {
                    minX = x;
                    maxY = y1;
                    anySelected = true;
                    break;
                }
            }
            for (int x = width - 1; x >= maxX; --x) {
                if ((pixels[x] & 0xFF000000) != 0x0) {
                    maxX = x;
                    maxY = y1;
                    anySelected = true;
                    break;
                }
            }
            if (anySelected) {
                break;
            }
        }
        pixels = null;
        for (int y2 = 0; y2 < y1; ++y2) {
            pixels = getRGB(p, 0, y2, width, 1, pixels);
            for (int x2 = 0; x2 < minX; ++x2) {
                if ((pixels[x2] & 0xFF000000) != 0x0) {
                    minX = x2;
                    if (y2 < minY) {
                        minY = y2;
                    }
                    anySelected = true;
                    break;
                }
            }
            for (int x2 = width - 1; x2 >= maxX; --x2) {
                if ((pixels[x2] & 0xFF000000) != 0x0) {
                    maxX = x2;
                    if (y2 < minY) {
                        minY = y2;
                    }
                    anySelected = true;
                    break;
                }
            }
        }
        if (anySelected) {
            return new Rectangle(minX, minY, maxX - minX + 1, maxY - minY + 1);
        }
        return null;
    }
    
    public static void composeThroughMask(final Raster src, final WritableRaster dst, final Raster sel) {
        final int x = src.getMinX();
        int y = src.getMinY();
        final int w = src.getWidth();
        final int h = src.getHeight();
        int[] srcRGB = null;
        int[] selRGB = null;
        int[] dstRGB = null;
        for (int i = 0; i < h; ++i) {
            srcRGB = src.getPixels(x, y, w, 1, srcRGB);
            selRGB = sel.getPixels(x, y, w, 1, selRGB);
            dstRGB = dst.getPixels(x, y, w, 1, dstRGB);
            int k = x;
            for (int j = 0; j < w; ++j) {
                final int sr = srcRGB[k];
                final int dir = dstRGB[k];
                final int sg = srcRGB[k + 1];
                final int dig = dstRGB[k + 1];
                final int sb = srcRGB[k + 2];
                final int dib = dstRGB[k + 2];
                final int sa = srcRGB[k + 3];
                final int dia = dstRGB[k + 3];
                final float a = selRGB[k + 3] / 255.0f;
                final float ac = 1.0f - a;
                dstRGB[k] = (int)(a * sr + ac * dir);
                dstRGB[k + 1] = (int)(a * sg + ac * dig);
                dstRGB[k + 2] = (int)(a * sb + ac * dib);
                dstRGB[k + 3] = (int)(a * sa + ac * dia);
                k += 4;
            }
            dst.setPixels(x, y, w, 1, dstRGB);
            ++y;
        }
    }
    
    public static int[] getRGB(final BufferedImage image, final int x, final int y, final int width, final int height, final int[] pixels) {
        final int type = image.getType();
        if (type == 2 || type == 1) {
            return (int[])image.getRaster().getDataElements(x, y, width, height, pixels);
        }
        return image.getRGB(x, y, width, height, pixels, 0, width);
    }
    
    public static void setRGB(final BufferedImage image, final int x, final int y, final int width, final int height, final int[] pixels) {
        final int type = image.getType();
        if (type == 2 || type == 1) {
            image.getRaster().setDataElements(x, y, width, height, pixels);
        }
        else {
            image.setRGB(x, y, width, height, pixels, 0, width);
        }
    }
    
    static {
        ImageUtils.backgroundImage = null;
    }
}
