package java.awt.font;

import java.awt.geom.Rectangle2D;

public final class GlyphMetrics
{
    private boolean horizontal;
    private float advanceX;
    private float advanceY;
    private Rectangle2D.Float bounds;
    private byte glyphType;
    public static final byte STANDARD = 0;
    public static final byte LIGATURE = 1;
    public static final byte COMBINING = 2;
    public static final byte COMPONENT = 3;
    public static final byte WHITESPACE = 4;
    
    public GlyphMetrics(final float advanceX, final Rectangle2D rect, final byte glyphType) {
        this.horizontal = true;
        this.advanceX = advanceX;
        this.advanceY = 0.0f;
        (this.bounds = new Rectangle2D.Float()).setRect(rect);
        this.glyphType = glyphType;
    }
    
    public GlyphMetrics(final boolean horizontal, final float advanceX, final float advanceY, final Rectangle2D rect, final byte glyphType) {
        this.horizontal = horizontal;
        this.advanceX = advanceX;
        this.advanceY = advanceY;
        (this.bounds = new Rectangle2D.Float()).setRect(rect);
        this.glyphType = glyphType;
    }
    
    public float getAdvance() {
        return this.horizontal ? this.advanceX : this.advanceY;
    }
    
    public float getAdvanceX() {
        return this.advanceX;
    }
    
    public float getAdvanceY() {
        return this.advanceY;
    }
    
    public Rectangle2D getBounds2D() {
        return new Rectangle2D.Float(this.bounds.x, this.bounds.y, this.bounds.width, this.bounds.height);
    }
    
    public float getLSB() {
        return this.horizontal ? this.bounds.x : this.bounds.y;
    }
    
    public float getRSB() {
        return this.horizontal ? (this.advanceX - this.bounds.x - this.bounds.width) : (this.advanceY - this.bounds.y - this.bounds.height);
    }
    
    public int getType() {
        return this.glyphType;
    }
    
    public boolean isStandard() {
        return (this.glyphType & 0x3) == 0x0;
    }
    
    public boolean isLigature() {
        return (this.glyphType & 0x3) == 0x1;
    }
    
    public boolean isCombining() {
        return (this.glyphType & 0x3) == 0x2;
    }
    
    public boolean isComponent() {
        return (this.glyphType & 0x3) == 0x3;
    }
    
    public boolean isWhitespace() {
        return (this.glyphType & 0x4) == 0x4;
    }
}
