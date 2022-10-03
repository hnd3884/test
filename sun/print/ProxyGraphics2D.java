package sun.print;

import java.util.Map;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.Paint;
import java.awt.Composite;
import java.awt.font.GlyphVector;
import java.awt.image.BufferedImageOp;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.image.renderable.RenderContext;
import java.awt.image.renderable.RenderableImage;
import java.awt.geom.AffineTransform;
import java.awt.image.RenderedImage;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.Image;
import java.text.AttributedCharacterIterator;
import java.awt.Shape;
import java.awt.Rectangle;
import java.awt.font.FontRenderContext;
import java.awt.FontMetrics;
import java.awt.Font;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.print.PrinterJob;
import java.awt.print.PrinterGraphics;
import java.awt.Graphics2D;

public class ProxyGraphics2D extends Graphics2D implements PrinterGraphics
{
    Graphics2D mGraphics;
    PrinterJob mPrinterJob;
    
    public ProxyGraphics2D(final Graphics2D mGraphics, final PrinterJob mPrinterJob) {
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
    
    @Override
    public GraphicsConfiguration getDeviceConfiguration() {
        return ((RasterPrinterJob)this.mPrinterJob).getPrinterGraphicsConfig();
    }
    
    @Override
    public Graphics create() {
        return new ProxyGraphics2D((Graphics2D)this.mGraphics.create(), this.mPrinterJob);
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
        this.mGraphics.copyArea(n, n2, n3, n4, n5, n6);
    }
    
    @Override
    public void drawLine(final int n, final int n2, final int n3, final int n4) {
        this.mGraphics.drawLine(n, n2, n3, n4);
    }
    
    @Override
    public void fillRect(final int n, final int n2, final int n3, final int n4) {
        this.mGraphics.fillRect(n, n2, n3, n4);
    }
    
    @Override
    public void clearRect(final int n, final int n2, final int n3, final int n4) {
        this.mGraphics.clearRect(n, n2, n3, n4);
    }
    
    @Override
    public void drawRoundRect(final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
        this.mGraphics.drawRoundRect(n, n2, n3, n4, n5, n6);
    }
    
    @Override
    public void fillRoundRect(final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
        this.mGraphics.fillRoundRect(n, n2, n3, n4, n5, n6);
    }
    
    @Override
    public void drawOval(final int n, final int n2, final int n3, final int n4) {
        this.mGraphics.drawOval(n, n2, n3, n4);
    }
    
    @Override
    public void fillOval(final int n, final int n2, final int n3, final int n4) {
        this.mGraphics.fillOval(n, n2, n3, n4);
    }
    
    @Override
    public void drawArc(final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
        this.mGraphics.drawArc(n, n2, n3, n4, n5, n6);
    }
    
    @Override
    public void fillArc(final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
        this.mGraphics.fillArc(n, n2, n3, n4, n5, n6);
    }
    
    @Override
    public void drawPolyline(final int[] array, final int[] array2, final int n) {
        this.mGraphics.drawPolyline(array, array2, n);
    }
    
    @Override
    public void drawPolygon(final int[] array, final int[] array2, final int n) {
        this.mGraphics.drawPolygon(array, array2, n);
    }
    
    @Override
    public void fillPolygon(final int[] array, final int[] array2, final int n) {
        this.mGraphics.fillPolygon(array, array2, n);
    }
    
    @Override
    public void drawString(final String s, final int n, final int n2) {
        this.mGraphics.drawString(s, n, n2);
    }
    
    @Override
    public void drawString(final AttributedCharacterIterator attributedCharacterIterator, final int n, final int n2) {
        this.mGraphics.drawString(attributedCharacterIterator, n, n2);
    }
    
    @Override
    public void drawString(final AttributedCharacterIterator attributedCharacterIterator, final float n, final float n2) {
        this.mGraphics.drawString(attributedCharacterIterator, n, n2);
    }
    
    @Override
    public boolean drawImage(final Image image, final int n, final int n2, final ImageObserver imageObserver) {
        return this.mGraphics.drawImage(image, n, n2, imageObserver);
    }
    
    @Override
    public boolean drawImage(final Image image, final int n, final int n2, final int n3, final int n4, final ImageObserver imageObserver) {
        return this.mGraphics.drawImage(image, n, n2, n3, n4, imageObserver);
    }
    
    @Override
    public boolean drawImage(final Image image, final int n, final int n2, final Color color, final ImageObserver imageObserver) {
        if (image == null) {
            return true;
        }
        boolean b;
        if (this.needToCopyBgColorImage(image)) {
            b = this.mGraphics.drawImage(this.getBufferedImageCopy(image, color), n, n2, null);
        }
        else {
            b = this.mGraphics.drawImage(image, n, n2, color, imageObserver);
        }
        return b;
    }
    
    @Override
    public boolean drawImage(final Image image, final int n, final int n2, final int n3, final int n4, final Color color, final ImageObserver imageObserver) {
        if (image == null) {
            return true;
        }
        boolean b;
        if (this.needToCopyBgColorImage(image)) {
            b = this.mGraphics.drawImage(this.getBufferedImageCopy(image, color), n, n2, n3, n4, null);
        }
        else {
            b = this.mGraphics.drawImage(image, n, n2, n3, n4, color, imageObserver);
        }
        return b;
    }
    
    @Override
    public boolean drawImage(final Image image, final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final int n7, final int n8, final ImageObserver imageObserver) {
        return this.mGraphics.drawImage(image, n, n2, n3, n4, n5, n6, n7, n8, imageObserver);
    }
    
    @Override
    public boolean drawImage(final Image image, final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final int n7, final int n8, final Color color, final ImageObserver imageObserver) {
        if (image == null) {
            return true;
        }
        boolean b;
        if (this.needToCopyBgColorImage(image)) {
            b = this.mGraphics.drawImage(this.getBufferedImageCopy(image, color), n, n2, n3, n4, n6, n6, n7, n8, null);
        }
        else {
            b = this.mGraphics.drawImage(image, n, n2, n3, n4, n6, n6, n7, n8, color, imageObserver);
        }
        return b;
    }
    
    private boolean needToCopyBgColorImage(final Image image) {
        return (this.getTransform().getType() & 0x30) != 0x0;
    }
    
    private BufferedImage getBufferedImageCopy(final Image image, final Color color) {
        final int width = image.getWidth(null);
        final int height = image.getHeight(null);
        BufferedImage bufferedImage;
        if (width > 0 && height > 0) {
            int type;
            if (image instanceof BufferedImage) {
                type = ((BufferedImage)image).getType();
            }
            else {
                type = 2;
            }
            bufferedImage = new BufferedImage(width, height, type);
            final Graphics2D graphics = bufferedImage.createGraphics();
            graphics.drawImage(image, 0, 0, color, null);
            graphics.dispose();
        }
        else {
            bufferedImage = null;
        }
        return bufferedImage;
    }
    
    @Override
    public void drawRenderedImage(final RenderedImage renderedImage, final AffineTransform affineTransform) {
        this.mGraphics.drawRenderedImage(renderedImage, affineTransform);
    }
    
    @Override
    public void drawRenderableImage(final RenderableImage renderableImage, final AffineTransform affineTransform) {
        if (renderableImage == null) {
            return;
        }
        final AffineTransform transform = this.getTransform();
        final AffineTransform affineTransform2 = new AffineTransform(affineTransform);
        affineTransform2.concatenate(transform);
        RenderContext renderContext = new RenderContext(affineTransform2);
        AffineTransform inverse;
        try {
            inverse = transform.createInverse();
        }
        catch (final NoninvertibleTransformException ex) {
            renderContext = new RenderContext(transform);
            inverse = new AffineTransform();
        }
        this.drawRenderedImage(renderableImage.createRendering(renderContext), inverse);
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
        this.mGraphics.draw(shape);
    }
    
    @Override
    public boolean drawImage(final Image image, final AffineTransform affineTransform, final ImageObserver imageObserver) {
        return this.mGraphics.drawImage(image, affineTransform, imageObserver);
    }
    
    @Override
    public void drawImage(final BufferedImage bufferedImage, final BufferedImageOp bufferedImageOp, final int n, final int n2) {
        this.mGraphics.drawImage(bufferedImage, bufferedImageOp, n, n2);
    }
    
    @Override
    public void drawString(final String s, final float n, final float n2) {
        this.mGraphics.drawString(s, n, n2);
    }
    
    @Override
    public void drawGlyphVector(final GlyphVector glyphVector, final float n, final float n2) {
        this.mGraphics.drawGlyphVector(glyphVector, n, n2);
    }
    
    @Override
    public void fill(final Shape shape) {
        this.mGraphics.fill(shape);
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
}
