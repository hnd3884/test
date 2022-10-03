package sun.font;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Font;
import java.util.Map;
import java.awt.geom.AffineTransform;
import java.awt.Rectangle;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.Rectangle2D;

public class TextSourceLabel extends TextLabel
{
    TextSource source;
    Rectangle2D lb;
    Rectangle2D ab;
    Rectangle2D vb;
    Rectangle2D ib;
    GlyphVector gv;
    
    public TextSourceLabel(final TextSource textSource) {
        this(textSource, null, null, null);
    }
    
    public TextSourceLabel(final TextSource source, final Rectangle2D lb, final Rectangle2D ab, final GlyphVector gv) {
        this.source = source;
        this.lb = lb;
        this.ab = ab;
        this.gv = gv;
    }
    
    public TextSource getSource() {
        return this.source;
    }
    
    @Override
    public final Rectangle2D getLogicalBounds(final float n, final float n2) {
        if (this.lb == null) {
            this.lb = this.createLogicalBounds();
        }
        return new Rectangle2D.Float((float)(this.lb.getX() + n), (float)(this.lb.getY() + n2), (float)this.lb.getWidth(), (float)this.lb.getHeight());
    }
    
    @Override
    public final Rectangle2D getVisualBounds(final float n, final float n2) {
        if (this.vb == null) {
            this.vb = this.createVisualBounds();
        }
        return new Rectangle2D.Float((float)(this.vb.getX() + n), (float)(this.vb.getY() + n2), (float)this.vb.getWidth(), (float)this.vb.getHeight());
    }
    
    @Override
    public final Rectangle2D getAlignBounds(final float n, final float n2) {
        if (this.ab == null) {
            this.ab = this.createAlignBounds();
        }
        return new Rectangle2D.Float((float)(this.ab.getX() + n), (float)(this.ab.getY() + n2), (float)this.ab.getWidth(), (float)this.ab.getHeight());
    }
    
    @Override
    public Rectangle2D getItalicBounds(final float n, final float n2) {
        if (this.ib == null) {
            this.ib = this.createItalicBounds();
        }
        return new Rectangle2D.Float((float)(this.ib.getX() + n), (float)(this.ib.getY() + n2), (float)this.ib.getWidth(), (float)this.ib.getHeight());
    }
    
    public Rectangle getPixelBounds(final FontRenderContext fontRenderContext, final float n, final float n2) {
        return this.getGV().getPixelBounds(fontRenderContext, n, n2);
    }
    
    public AffineTransform getBaselineTransform() {
        final Font font = this.source.getFont();
        if (font.hasLayoutAttributes()) {
            return AttributeValues.getBaselineTransform(font.getAttributes());
        }
        return null;
    }
    
    @Override
    public Shape getOutline(final float n, final float n2) {
        return this.getGV().getOutline(n, n2);
    }
    
    @Override
    public void draw(final Graphics2D graphics2D, final float n, final float n2) {
        graphics2D.drawGlyphVector(this.getGV(), n, n2);
    }
    
    protected Rectangle2D createLogicalBounds() {
        return this.getGV().getLogicalBounds();
    }
    
    protected Rectangle2D createVisualBounds() {
        return this.getGV().getVisualBounds();
    }
    
    protected Rectangle2D createItalicBounds() {
        return this.getGV().getLogicalBounds();
    }
    
    protected Rectangle2D createAlignBounds() {
        return this.createLogicalBounds();
    }
    
    private final GlyphVector getGV() {
        if (this.gv == null) {
            this.gv = this.createGV();
        }
        return this.gv;
    }
    
    protected GlyphVector createGV() {
        final Font font = this.source.getFont();
        final FontRenderContext frc = this.source.getFRC();
        final int layoutFlags = this.source.getLayoutFlags();
        final char[] chars = this.source.getChars();
        final int start = this.source.getStart();
        final int length = this.source.getLength();
        final GlyphLayout value = GlyphLayout.get(null);
        final StandardGlyphVector layout = value.layout(font, frc, chars, start, length, layoutFlags, null);
        GlyphLayout.done(value);
        return layout;
    }
}
