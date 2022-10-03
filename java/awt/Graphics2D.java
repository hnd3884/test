package java.awt;

import java.awt.font.FontRenderContext;
import java.util.Map;
import java.awt.font.GlyphVector;
import java.text.AttributedCharacterIterator;
import java.awt.image.renderable.RenderableImage;
import java.awt.image.RenderedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.geom.AffineTransform;

public abstract class Graphics2D extends Graphics
{
    protected Graphics2D() {
    }
    
    @Override
    public void draw3DRect(final int n, final int n2, final int n3, final int n4, final boolean b) {
        final Paint paint = this.getPaint();
        final Color color = this.getColor();
        final Color brighter = color.brighter();
        final Color darker = color.darker();
        this.setColor(b ? brighter : darker);
        this.fillRect(n, n2, 1, n4 + 1);
        this.fillRect(n + 1, n2, n3 - 1, 1);
        this.setColor(b ? darker : brighter);
        this.fillRect(n + 1, n2 + n4, n3, 1);
        this.fillRect(n + n3, n2, 1, n4);
        this.setPaint(paint);
    }
    
    @Override
    public void fill3DRect(final int n, final int n2, final int n3, final int n4, final boolean b) {
        final Paint paint = this.getPaint();
        final Color color = this.getColor();
        final Color brighter = color.brighter();
        final Color darker = color.darker();
        if (!b) {
            this.setColor(darker);
        }
        else if (paint != color) {
            this.setColor(color);
        }
        this.fillRect(n + 1, n2 + 1, n3 - 2, n4 - 2);
        this.setColor(b ? brighter : darker);
        this.fillRect(n, n2, 1, n4);
        this.fillRect(n + 1, n2, n3 - 2, 1);
        this.setColor(b ? darker : brighter);
        this.fillRect(n + 1, n2 + n4 - 1, n3 - 1, 1);
        this.fillRect(n + n3 - 1, n2, 1, n4 - 1);
        this.setPaint(paint);
    }
    
    public abstract void draw(final Shape p0);
    
    public abstract boolean drawImage(final Image p0, final AffineTransform p1, final ImageObserver p2);
    
    public abstract void drawImage(final BufferedImage p0, final BufferedImageOp p1, final int p2, final int p3);
    
    public abstract void drawRenderedImage(final RenderedImage p0, final AffineTransform p1);
    
    public abstract void drawRenderableImage(final RenderableImage p0, final AffineTransform p1);
    
    @Override
    public abstract void drawString(final String p0, final int p1, final int p2);
    
    public abstract void drawString(final String p0, final float p1, final float p2);
    
    @Override
    public abstract void drawString(final AttributedCharacterIterator p0, final int p1, final int p2);
    
    public abstract void drawString(final AttributedCharacterIterator p0, final float p1, final float p2);
    
    public abstract void drawGlyphVector(final GlyphVector p0, final float p1, final float p2);
    
    public abstract void fill(final Shape p0);
    
    public abstract boolean hit(final Rectangle p0, final Shape p1, final boolean p2);
    
    public abstract GraphicsConfiguration getDeviceConfiguration();
    
    public abstract void setComposite(final Composite p0);
    
    public abstract void setPaint(final Paint p0);
    
    public abstract void setStroke(final Stroke p0);
    
    public abstract void setRenderingHint(final RenderingHints.Key p0, final Object p1);
    
    public abstract Object getRenderingHint(final RenderingHints.Key p0);
    
    public abstract void setRenderingHints(final Map<?, ?> p0);
    
    public abstract void addRenderingHints(final Map<?, ?> p0);
    
    public abstract RenderingHints getRenderingHints();
    
    @Override
    public abstract void translate(final int p0, final int p1);
    
    public abstract void translate(final double p0, final double p1);
    
    public abstract void rotate(final double p0);
    
    public abstract void rotate(final double p0, final double p1, final double p2);
    
    public abstract void scale(final double p0, final double p1);
    
    public abstract void shear(final double p0, final double p1);
    
    public abstract void transform(final AffineTransform p0);
    
    public abstract void setTransform(final AffineTransform p0);
    
    public abstract AffineTransform getTransform();
    
    public abstract Paint getPaint();
    
    public abstract Composite getComposite();
    
    public abstract void setBackground(final Color p0);
    
    public abstract Color getBackground();
    
    public abstract Stroke getStroke();
    
    public abstract void clip(final Shape p0);
    
    public abstract FontRenderContext getFontRenderContext();
}
