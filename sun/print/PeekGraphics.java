package sun.print;

import java.util.Map;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.Paint;
import java.awt.Composite;
import java.awt.font.GlyphVector;
import java.awt.image.BufferedImageOp;
import java.awt.image.BufferedImage;
import java.awt.image.renderable.RenderableImage;
import java.awt.geom.AffineTransform;
import java.awt.image.RenderedImage;
import java.awt.Image;
import java.awt.font.TextLayout;
import java.text.AttributedCharacterIterator;
import java.awt.geom.RoundRectangle2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Line2D;
import java.awt.Shape;
import java.awt.Rectangle;
import java.awt.font.FontRenderContext;
import java.awt.FontMetrics;
import java.awt.Font;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import sun.java2d.Spans;
import java.awt.print.PrinterJob;
import java.awt.image.ImageObserver;
import java.awt.print.PrinterGraphics;
import java.awt.Graphics2D;

public class PeekGraphics extends Graphics2D implements PrinterGraphics, ImageObserver, Cloneable
{
    Graphics2D mGraphics;
    PrinterJob mPrinterJob;
    private Spans mDrawingArea;
    private PeekMetrics mPrintMetrics;
    private boolean mAWTDrawingOnly;
    
    public PeekGraphics(final Graphics2D mGraphics, final PrinterJob mPrinterJob) {
        this.mDrawingArea = new Spans();
        this.mPrintMetrics = new PeekMetrics();
        this.mAWTDrawingOnly = false;
        this.mGraphics = mGraphics;
        this.mPrinterJob = mPrinterJob;
    }
    
    public Graphics2D getDelegate() {
        return this.mGraphics;
    }
    
    public void setDelegate(final Graphics2D mGraphics) {
        this.mGraphics = mGraphics;
    }
    
    @Override
    public PrinterJob getPrinterJob() {
        return this.mPrinterJob;
    }
    
    public void setAWTDrawingOnly() {
        this.mAWTDrawingOnly = true;
    }
    
    public boolean getAWTDrawingOnly() {
        return this.mAWTDrawingOnly;
    }
    
    public Spans getDrawingArea() {
        return this.mDrawingArea;
    }
    
    @Override
    public GraphicsConfiguration getDeviceConfiguration() {
        return ((RasterPrinterJob)this.mPrinterJob).getPrinterGraphicsConfig();
    }
    
    @Override
    public Graphics create() {
        PeekGraphics peekGraphics = null;
        try {
            peekGraphics = (PeekGraphics)this.clone();
            peekGraphics.mGraphics = (Graphics2D)this.mGraphics.create();
        }
        catch (final CloneNotSupportedException ex) {}
        return peekGraphics;
    }
    
    @Override
    public void translate(final int n, final int n2) {
        this.mGraphics.translate(n, n2);
    }
    
    @Override
    public void translate(final double n, final double n2) {
        this.mGraphics.translate(n, n2);
    }
    
    @Override
    public void rotate(final double n) {
        this.mGraphics.rotate(n);
    }
    
    @Override
    public void rotate(final double n, final double n2, final double n3) {
        this.mGraphics.rotate(n, n2, n3);
    }
    
    @Override
    public void scale(final double n, final double n2) {
        this.mGraphics.scale(n, n2);
    }
    
    @Override
    public void shear(final double n, final double n2) {
        this.mGraphics.shear(n, n2);
    }
    
    @Override
    public Color getColor() {
        return this.mGraphics.getColor();
    }
    
    @Override
    public void setColor(final Color color) {
        this.mGraphics.setColor(color);
    }
    
    @Override
    public void setPaintMode() {
        this.mGraphics.setPaintMode();
    }
    
    @Override
    public void setXORMode(final Color xorMode) {
        this.mGraphics.setXORMode(xorMode);
    }
    
    @Override
    public Font getFont() {
        return this.mGraphics.getFont();
    }
    
    @Override
    public void setFont(final Font font) {
        this.mGraphics.setFont(font);
    }
    
    @Override
    public FontMetrics getFontMetrics(final Font font) {
        return this.mGraphics.getFontMetrics(font);
    }
    
    @Override
    public FontRenderContext getFontRenderContext() {
        return this.mGraphics.getFontRenderContext();
    }
    
    @Override
    public Rectangle getClipBounds() {
        return this.mGraphics.getClipBounds();
    }
    
    @Override
    public void clipRect(final int n, final int n2, final int n3, final int n4) {
        this.mGraphics.clipRect(n, n2, n3, n4);
    }
    
    @Override
    public void setClip(final int n, final int n2, final int n3, final int n4) {
        this.mGraphics.setClip(n, n2, n3, n4);
    }
    
    @Override
    public Shape getClip() {
        return this.mGraphics.getClip();
    }
    
    @Override
    public void setClip(final Shape clip) {
        this.mGraphics.setClip(clip);
    }
    
    @Override
    public void copyArea(final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
    }
    
    @Override
    public void drawLine(final int n, final int n2, final int n3, final int n4) {
        this.addStrokeShape(new Line2D.Float((float)n, (float)n2, (float)n3, (float)n4));
        this.mPrintMetrics.draw(this);
    }
    
    @Override
    public void fillRect(final int n, final int n2, final int n3, final int n4) {
        this.addDrawingRect(new Rectangle2D.Float((float)n, (float)n2, (float)n3, (float)n4));
        this.mPrintMetrics.fill(this);
    }
    
    @Override
    public void clearRect(final int n, final int n2, final int n3, final int n4) {
        this.addDrawingRect(new Rectangle2D.Float((float)n, (float)n2, (float)n3, (float)n4));
        this.mPrintMetrics.clear(this);
    }
    
    @Override
    public void drawRoundRect(final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
        this.addStrokeShape(new RoundRectangle2D.Float((float)n, (float)n2, (float)n3, (float)n4, (float)n5, (float)n6));
        this.mPrintMetrics.draw(this);
    }
    
    @Override
    public void fillRoundRect(final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
        this.addDrawingRect(new Rectangle2D.Float((float)n, (float)n2, (float)n3, (float)n4));
        this.mPrintMetrics.fill(this);
    }
    
    @Override
    public void drawOval(final int n, final int n2, final int n3, final int n4) {
        this.addStrokeShape(new Rectangle2D.Float((float)n, (float)n2, (float)n3, (float)n4));
        this.mPrintMetrics.draw(this);
    }
    
    @Override
    public void fillOval(final int n, final int n2, final int n3, final int n4) {
        this.addDrawingRect(new Rectangle2D.Float((float)n, (float)n2, (float)n3, (float)n4));
        this.mPrintMetrics.fill(this);
    }
    
    @Override
    public void drawArc(final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
        this.addStrokeShape(new Rectangle2D.Float((float)n, (float)n2, (float)n3, (float)n4));
        this.mPrintMetrics.draw(this);
    }
    
    @Override
    public void fillArc(final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
        this.addDrawingRect(new Rectangle2D.Float((float)n, (float)n2, (float)n3, (float)n4));
        this.mPrintMetrics.fill(this);
    }
    
    @Override
    public void drawPolyline(final int[] array, final int[] array2, final int n) {
        if (n > 0) {
            int n2 = array[0];
            int n3 = array2[0];
            for (int i = 1; i < n; ++i) {
                this.drawLine(n2, n3, array[i], array2[i]);
                n2 = array[i];
                n3 = array2[i];
            }
        }
    }
    
    @Override
    public void drawPolygon(final int[] array, final int[] array2, final int n) {
        if (n > 0) {
            this.drawPolyline(array, array2, n);
            this.drawLine(array[n - 1], array2[n - 1], array[0], array2[0]);
        }
    }
    
    @Override
    public void fillPolygon(final int[] array, final int[] array2, final int n) {
        if (n > 0) {
            int n2 = array[0];
            int n3 = array2[0];
            int n4 = array[0];
            int n5 = array2[0];
            for (int i = 1; i < n; ++i) {
                if (array[i] < n2) {
                    n2 = array[i];
                }
                else if (array[i] > n4) {
                    n4 = array[i];
                }
                if (array2[i] < n3) {
                    n3 = array2[i];
                }
                else if (array2[i] > n5) {
                    n5 = array2[i];
                }
            }
            this.addDrawingRect((float)n2, (float)n3, (float)(n4 - n2), (float)(n5 - n3));
        }
        this.mPrintMetrics.fill(this);
    }
    
    @Override
    public void drawString(final String s, final int n, final int n2) {
        this.drawString(s, (float)n, (float)n2);
    }
    
    @Override
    public void drawString(final AttributedCharacterIterator attributedCharacterIterator, final int n, final int n2) {
        this.drawString(attributedCharacterIterator, (float)n, (float)n2);
    }
    
    @Override
    public void drawString(final AttributedCharacterIterator attributedCharacterIterator, final float n, final float n2) {
        if (attributedCharacterIterator == null) {
            throw new NullPointerException("AttributedCharacterIterator is null");
        }
        new TextLayout(attributedCharacterIterator, this.getFontRenderContext()).draw(this, n, n2);
    }
    
    @Override
    public boolean drawImage(final Image image, final int n, final int n2, final ImageObserver imageObserver) {
        if (image == null) {
            return true;
        }
        final ImageWaiter imageWaiter = new ImageWaiter(image);
        this.addDrawingRect((float)n, (float)n2, (float)imageWaiter.getWidth(), (float)imageWaiter.getHeight());
        this.mPrintMetrics.drawImage(this, image);
        return this.mGraphics.drawImage(image, n, n2, imageObserver);
    }
    
    @Override
    public boolean drawImage(final Image image, final int n, final int n2, final int n3, final int n4, final ImageObserver imageObserver) {
        if (image == null) {
            return true;
        }
        this.addDrawingRect((float)n, (float)n2, (float)n3, (float)n4);
        this.mPrintMetrics.drawImage(this, image);
        return this.mGraphics.drawImage(image, n, n2, n3, n4, imageObserver);
    }
    
    @Override
    public boolean drawImage(final Image image, final int n, final int n2, final Color color, final ImageObserver imageObserver) {
        if (image == null) {
            return true;
        }
        final ImageWaiter imageWaiter = new ImageWaiter(image);
        this.addDrawingRect((float)n, (float)n2, (float)imageWaiter.getWidth(), (float)imageWaiter.getHeight());
        this.mPrintMetrics.drawImage(this, image);
        return this.mGraphics.drawImage(image, n, n2, color, imageObserver);
    }
    
    @Override
    public boolean drawImage(final Image image, final int n, final int n2, final int n3, final int n4, final Color color, final ImageObserver imageObserver) {
        if (image == null) {
            return true;
        }
        this.addDrawingRect((float)n, (float)n2, (float)n3, (float)n4);
        this.mPrintMetrics.drawImage(this, image);
        return this.mGraphics.drawImage(image, n, n2, n3, n4, color, imageObserver);
    }
    
    @Override
    public boolean drawImage(final Image image, final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final int n7, final int n8, final ImageObserver imageObserver) {
        if (image == null) {
            return true;
        }
        this.addDrawingRect((float)n, (float)n2, (float)(n3 - n), (float)(n4 - n2));
        this.mPrintMetrics.drawImage(this, image);
        return this.mGraphics.drawImage(image, n, n2, n3, n4, n5, n6, n7, n8, imageObserver);
    }
    
    @Override
    public boolean drawImage(final Image image, final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final int n7, final int n8, final Color color, final ImageObserver imageObserver) {
        if (image == null) {
            return true;
        }
        this.addDrawingRect((float)n, (float)n2, (float)(n3 - n), (float)(n4 - n2));
        this.mPrintMetrics.drawImage(this, image);
        return this.mGraphics.drawImage(image, n, n2, n3, n4, n5, n6, n7, n8, color, imageObserver);
    }
    
    @Override
    public void drawRenderedImage(final RenderedImage renderedImage, final AffineTransform affineTransform) {
        if (renderedImage == null) {
            return;
        }
        this.mPrintMetrics.drawImage(this, renderedImage);
        this.mDrawingArea.addInfinite();
    }
    
    @Override
    public void drawRenderableImage(final RenderableImage renderableImage, final AffineTransform affineTransform) {
        if (renderableImage == null) {
            return;
        }
        this.mPrintMetrics.drawImage(this, renderableImage);
        this.mDrawingArea.addInfinite();
    }
    
    @Override
    public void dispose() {
        this.mGraphics.dispose();
    }
    
    @Override
    public void finalize() {
    }
    
    @Override
    public void draw(final Shape shape) {
        this.addStrokeShape(shape);
        this.mPrintMetrics.draw(this);
    }
    
    @Override
    public boolean drawImage(final Image image, final AffineTransform affineTransform, final ImageObserver imageObserver) {
        if (image == null) {
            return true;
        }
        this.mDrawingArea.addInfinite();
        this.mPrintMetrics.drawImage(this, image);
        return this.mGraphics.drawImage(image, affineTransform, imageObserver);
    }
    
    @Override
    public void drawImage(final BufferedImage bufferedImage, final BufferedImageOp bufferedImageOp, final int n, final int n2) {
        if (bufferedImage == null) {
            return;
        }
        this.mPrintMetrics.drawImage(this, (RenderedImage)bufferedImage);
        this.mDrawingArea.addInfinite();
    }
    
    @Override
    public void drawString(final String s, final float n, final float n2) {
        if (s.length() == 0) {
            return;
        }
        this.addDrawingRect(this.getFont().getStringBounds(s, this.getFontRenderContext()), n, n2);
        this.mPrintMetrics.drawText(this);
    }
    
    @Override
    public void drawGlyphVector(final GlyphVector glyphVector, final float n, final float n2) {
        this.addDrawingRect(glyphVector.getLogicalBounds(), n, n2);
        this.mPrintMetrics.drawText(this);
    }
    
    @Override
    public void fill(final Shape shape) {
        this.addDrawingRect(shape.getBounds());
        this.mPrintMetrics.fill(this);
    }
    
    @Override
    public boolean hit(final Rectangle rectangle, final Shape shape, final boolean b) {
        return this.mGraphics.hit(rectangle, shape, b);
    }
    
    @Override
    public void setComposite(final Composite composite) {
        this.mGraphics.setComposite(composite);
    }
    
    @Override
    public void setPaint(final Paint paint) {
        this.mGraphics.setPaint(paint);
    }
    
    @Override
    public void setStroke(final Stroke stroke) {
        this.mGraphics.setStroke(stroke);
    }
    
    @Override
    public void setRenderingHint(final RenderingHints.Key key, final Object o) {
        this.mGraphics.setRenderingHint(key, o);
    }
    
    @Override
    public Object getRenderingHint(final RenderingHints.Key key) {
        return this.mGraphics.getRenderingHint(key);
    }
    
    @Override
    public void setRenderingHints(final Map<?, ?> renderingHints) {
        this.mGraphics.setRenderingHints(renderingHints);
    }
    
    @Override
    public void addRenderingHints(final Map<?, ?> map) {
        this.mGraphics.addRenderingHints(map);
    }
    
    @Override
    public RenderingHints getRenderingHints() {
        return this.mGraphics.getRenderingHints();
    }
    
    @Override
    public void transform(final AffineTransform affineTransform) {
        this.mGraphics.transform(affineTransform);
    }
    
    @Override
    public void setTransform(final AffineTransform transform) {
        this.mGraphics.setTransform(transform);
    }
    
    @Override
    public AffineTransform getTransform() {
        return this.mGraphics.getTransform();
    }
    
    @Override
    public Paint getPaint() {
        return this.mGraphics.getPaint();
    }
    
    @Override
    public Composite getComposite() {
        return this.mGraphics.getComposite();
    }
    
    @Override
    public void setBackground(final Color background) {
        this.mGraphics.setBackground(background);
    }
    
    @Override
    public Color getBackground() {
        return this.mGraphics.getBackground();
    }
    
    @Override
    public Stroke getStroke() {
        return this.mGraphics.getStroke();
    }
    
    @Override
    public void clip(final Shape shape) {
        this.mGraphics.clip(shape);
    }
    
    public boolean hitsDrawingArea(final Rectangle rectangle) {
        return this.mDrawingArea.intersects((float)rectangle.getMinY(), (float)rectangle.getMaxY());
    }
    
    public PeekMetrics getMetrics() {
        return this.mPrintMetrics;
    }
    
    private void addDrawingRect(final Rectangle2D rectangle2D, final float n, final float n2) {
        this.addDrawingRect((float)(rectangle2D.getX() + n), (float)(rectangle2D.getY() + n2), (float)rectangle2D.getWidth(), (float)rectangle2D.getHeight());
    }
    
    private void addDrawingRect(final float n, final float n2, final float n3, final float n4) {
        this.addDrawingRect(new Rectangle2D.Float(n, n2, n3, n4));
    }
    
    private void addDrawingRect(final Rectangle2D rectangle2D) {
        final Rectangle2D bounds2D = this.getTransform().createTransformedShape(rectangle2D).getBounds2D();
        this.mDrawingArea.add((float)bounds2D.getMinY(), (float)bounds2D.getMaxY());
    }
    
    private void addStrokeShape(final Shape shape) {
        this.addDrawingRect(this.getStroke().createStrokedShape(shape).getBounds2D());
    }
    
    @Override
    public synchronized boolean imageUpdate(final Image image, final int n, final int n2, final int n3, final int n4, final int n5) {
        boolean b = false;
        if ((n & 0x3) != 0x0) {
            b = true;
            this.notify();
        }
        return b;
    }
    
    private synchronized int getImageWidth(final Image image) {
        while (image.getWidth(this) == -1) {
            try {
                this.wait();
            }
            catch (final InterruptedException ex) {}
        }
        return image.getWidth(this);
    }
    
    private synchronized int getImageHeight(final Image image) {
        while (image.getHeight(this) == -1) {
            try {
                this.wait();
            }
            catch (final InterruptedException ex) {}
        }
        return image.getHeight(this);
    }
    
    protected class ImageWaiter implements ImageObserver
    {
        private int mWidth;
        private int mHeight;
        private boolean badImage;
        
        ImageWaiter(final Image image) {
            this.badImage = false;
            this.waitForDimensions(image);
        }
        
        public int getWidth() {
            return this.mWidth;
        }
        
        public int getHeight() {
            return this.mHeight;
        }
        
        private synchronized void waitForDimensions(final Image image) {
            this.mHeight = image.getHeight(this);
            this.mWidth = image.getWidth(this);
            while (!this.badImage) {
                if (this.mWidth >= 0) {
                    if (this.mHeight >= 0) {
                        break;
                    }
                }
                try {
                    Thread.sleep(50L);
                }
                catch (final InterruptedException ex) {}
                this.mHeight = image.getHeight(this);
                this.mWidth = image.getWidth(this);
            }
            if (this.badImage) {
                this.mHeight = 0;
                this.mWidth = 0;
            }
        }
        
        @Override
        public synchronized boolean imageUpdate(final Image image, final int n, final int n2, final int n3, final int n4, final int n5) {
            final boolean b = (n & 0xC2) != 0x0;
            this.badImage = ((n & 0xC0) != 0x0);
            return b;
        }
    }
}
