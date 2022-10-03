package java.awt.font;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

public abstract class GraphicAttribute
{
    private int fAlignment;
    public static final int TOP_ALIGNMENT = -1;
    public static final int BOTTOM_ALIGNMENT = -2;
    public static final int ROMAN_BASELINE = 0;
    public static final int CENTER_BASELINE = 1;
    public static final int HANGING_BASELINE = 2;
    
    protected GraphicAttribute(final int fAlignment) {
        if (fAlignment < -2 || fAlignment > 2) {
            throw new IllegalArgumentException("bad alignment");
        }
        this.fAlignment = fAlignment;
    }
    
    public abstract float getAscent();
    
    public abstract float getDescent();
    
    public abstract float getAdvance();
    
    public Rectangle2D getBounds() {
        final float ascent = this.getAscent();
        return new Rectangle2D.Float(0.0f, -ascent, this.getAdvance(), ascent + this.getDescent());
    }
    
    public Shape getOutline(final AffineTransform affineTransform) {
        Shape shape = this.getBounds();
        if (affineTransform != null) {
            shape = affineTransform.createTransformedShape(shape);
        }
        return shape;
    }
    
    public abstract void draw(final Graphics2D p0, final float p1, final float p2);
    
    public final int getAlignment() {
        return this.fAlignment;
    }
    
    public GlyphJustificationInfo getJustificationInfo() {
        final float advance = this.getAdvance();
        return new GlyphJustificationInfo(advance, false, 2, advance / 3.0f, advance / 3.0f, false, 1, 0.0f, 0.0f);
    }
}
