package org.apache.poi.sl.draw;

import org.apache.poi.util.POILogFactory;
import java.awt.image.RenderedImage;
import java.awt.Shape;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.image.RescaleOp;
import java.awt.geom.Rectangle2D;
import java.awt.image.AffineTransformOp;
import java.awt.geom.AffineTransform;
import java.awt.geom.Dimension2D;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.imageio.ImageReadParam;
import java.util.Iterator;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.ImageObserver;
import java.awt.Image;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageReader;
import javax.imageio.ImageIO;
import javax.imageio.stream.MemoryCacheImageInputStream;
import java.io.OutputStream;
import org.apache.poi.util.IOUtils;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.poi.sl.usermodel.PictureData;
import java.awt.image.BufferedImage;
import org.apache.poi.util.POILogger;

public class BitmapImageRenderer implements ImageRenderer
{
    private static final POILogger LOG;
    protected BufferedImage img;
    
    @Override
    public boolean canRender(final String contentType) {
        final PictureData.PictureType[] array;
        final PictureData.PictureType[] pts = array = new PictureData.PictureType[] { PictureData.PictureType.JPEG, PictureData.PictureType.PNG, PictureData.PictureType.BMP, PictureData.PictureType.GIF };
        for (final PictureData.PictureType pt : array) {
            if (pt.contentType.equalsIgnoreCase(contentType)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public void loadImage(final InputStream data, final String contentType) throws IOException {
        this.img = readImage(data, contentType);
    }
    
    @Override
    public void loadImage(final byte[] data, final String contentType) throws IOException {
        this.img = readImage(new ByteArrayInputStream(data), contentType);
    }
    
    private static BufferedImage readImage(final InputStream data, final String contentType) throws IOException {
        IOException lastException = null;
        BufferedImage img = null;
        ByteArrayInputStream bis;
        if (data instanceof ByteArrayInputStream) {
            bis = (ByteArrayInputStream)data;
        }
        else {
            final ByteArrayOutputStream bos = new ByteArrayOutputStream(262143);
            IOUtils.copy(data, bos);
            bis = new ByteArrayInputStream(bos.toByteArray());
        }
        ImageInputStream iis = new MemoryCacheImageInputStream(bis);
        try {
            final Iterator<ImageReader> iter = ImageIO.getImageReaders(iis);
            while (img == null && iter.hasNext()) {
                final ImageReader reader = iter.next();
                final ImageReadParam param = reader.getDefaultReadParam();
                for (int mode = 0; img == null && mode < 3; ++mode) {
                    lastException = null;
                    if (mode > 0) {
                        bis.reset();
                        iis.close();
                        iis = new MemoryCacheImageInputStream(bis);
                    }
                    try {
                        switch (mode) {
                            case 0: {
                                reader.setInput(iis, false, true);
                                img = reader.read(0, param);
                                break;
                            }
                            case 1: {
                                final Iterator<ImageTypeSpecifier> imageTypes = reader.getImageTypes(0);
                                while (imageTypes.hasNext()) {
                                    final ImageTypeSpecifier imageTypeSpecifier = imageTypes.next();
                                    final int bufferedImageType = imageTypeSpecifier.getBufferedImageType();
                                    if (bufferedImageType == 10) {
                                        param.setDestinationType(imageTypeSpecifier);
                                        break;
                                    }
                                }
                                reader.setInput(iis, false, true);
                                img = reader.read(0, param);
                                break;
                            }
                            case 2: {
                                reader.setInput(iis, false, true);
                                final int height = reader.getHeight(0);
                                final int width = reader.getWidth(0);
                                final Iterator<ImageTypeSpecifier> imageTypes2 = reader.getImageTypes(0);
                                if (imageTypes2.hasNext()) {
                                    final ImageTypeSpecifier imageTypeSpecifier2 = imageTypes2.next();
                                    img = imageTypeSpecifier2.createBufferedImage(width, height);
                                    param.setDestination(img);
                                    try {
                                        reader.read(0, param);
                                    }
                                    finally {
                                        if (img.getType() != 2) {
                                            final int y = findTruncatedBlackBox(img, width, height);
                                            if (y < height) {
                                                final BufferedImage argbImg = new BufferedImage(width, height, 2);
                                                final Graphics2D g = argbImg.createGraphics();
                                                g.clipRect(0, 0, width, y);
                                                g.drawImage(img, 0, 0, null);
                                                g.dispose();
                                                img.flush();
                                                img = argbImg;
                                            }
                                        }
                                    }
                                    break;
                                }
                                lastException = new IOException("unable to load even a truncated version of the image.");
                                break;
                            }
                        }
                    }
                    catch (final IOException e) {
                        if (mode < 2) {
                            lastException = e;
                        }
                    }
                    catch (final RuntimeException e2) {
                        if (mode < 2) {
                            lastException = new IOException("ImageIO runtime exception - " + ((mode == 0) ? "normal" : "fallback"), e2);
                        }
                    }
                }
                reader.dispose();
            }
        }
        finally {
            iis.close();
        }
        if (img == null) {
            if (lastException != null) {
                throw lastException;
            }
            BitmapImageRenderer.LOG.log(5, "Content-type: " + contentType + " is not support. Image ignored.");
            return null;
        }
        else {
            if (img.getType() != 2) {
                final BufferedImage argbImg2 = new BufferedImage(img.getWidth(), img.getHeight(), 2);
                final Graphics g2 = argbImg2.getGraphics();
                g2.drawImage(img, 0, 0, null);
                g2.dispose();
                return argbImg2;
            }
            return img;
        }
    }
    
    private static int findTruncatedBlackBox(final BufferedImage img, final int width, final int height) {
        for (int h = height - 1; h > 0; --h) {
            for (int w = width - 1; w > 0; w -= width / 10) {
                final int p = img.getRGB(w, h);
                if (p != -16777216) {
                    return h + 1;
                }
            }
        }
        return 0;
    }
    
    @Override
    public BufferedImage getImage() {
        return this.img;
    }
    
    @Override
    public BufferedImage getImage(final Dimension2D dim) {
        if (this.img == null) {
            return this.img;
        }
        final double w_old = this.img.getWidth();
        final double h_old = this.img.getHeight();
        final double w_new = dim.getWidth();
        final double h_new = dim.getHeight();
        if (w_old == w_new && h_old == h_new) {
            return this.img;
        }
        final BufferedImage scaled = new BufferedImage((int)w_new, (int)h_new, 2);
        final AffineTransform at = new AffineTransform();
        at.scale(w_new / w_old, h_new / h_old);
        final AffineTransformOp scaleOp = new AffineTransformOp(at, 2);
        scaleOp.filter(this.img, scaled);
        return scaled;
    }
    
    @Override
    public Rectangle2D getBounds() {
        return (this.img == null) ? new Rectangle2D.Double() : new Rectangle2D.Double(0.0, 0.0, this.img.getWidth(), this.img.getHeight());
    }
    
    @Override
    public void setAlpha(final double alpha) {
        this.img = setAlpha(this.img, alpha);
    }
    
    public static BufferedImage setAlpha(final BufferedImage image, final double alpha) {
        if (image == null) {
            return new BufferedImage(1, 1, 2);
        }
        if (alpha == 0.0) {
            return image;
        }
        final float[] scalefactors = { 1.0f, 1.0f, 1.0f, (float)alpha };
        final float[] offsets = { 0.0f, 0.0f, 0.0f, 0.0f };
        final RescaleOp op = new RescaleOp(scalefactors, offsets, null);
        return op.filter(image, null);
    }
    
    @Override
    public boolean drawImage(final Graphics2D graphics, final Rectangle2D anchor) {
        return this.drawImage(graphics, anchor, null);
    }
    
    @Override
    public boolean drawImage(final Graphics2D graphics, final Rectangle2D anchor, Insets clip) {
        if (this.img == null) {
            return false;
        }
        boolean isClipped = true;
        if (clip == null) {
            isClipped = false;
            clip = new Insets(0, 0, 0, 0);
        }
        final int iw = this.img.getWidth();
        final int ih = this.img.getHeight();
        final double cw = (100000 - clip.left - clip.right) / 100000.0;
        final double ch = (100000 - clip.top - clip.bottom) / 100000.0;
        final double sx = anchor.getWidth() / (iw * cw);
        final double sy = anchor.getHeight() / (ih * ch);
        final double tx = anchor.getX() - iw * sx * clip.left / 100000.0;
        final double ty = anchor.getY() - ih * sy * clip.top / 100000.0;
        final AffineTransform at = new AffineTransform(sx, 0.0, 0.0, sy, tx, ty);
        final Shape clipOld = graphics.getClip();
        if (isClipped) {
            graphics.clip(anchor.getBounds2D());
        }
        graphics.drawRenderedImage(this.img, at);
        graphics.setClip(clipOld);
        return true;
    }
    
    @Override
    public Rectangle2D getNativeBounds() {
        return new Rectangle2D.Double(0.0, 0.0, this.img.getWidth(), this.img.getHeight());
    }
    
    static {
        LOG = POILogFactory.getLogger(BitmapImageRenderer.class);
    }
}
