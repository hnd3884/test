package sun.font;

import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.awt.Rectangle;
import java.awt.geom.Point2D;

public abstract class FontStrike
{
    protected FontStrikeDisposer disposer;
    protected FontStrikeDesc desc;
    protected StrikeMetrics strikeMetrics;
    protected boolean algoStyle;
    protected float boldness;
    protected float italic;
    
    public FontStrike() {
        this.algoStyle = false;
        this.boldness = 1.0f;
        this.italic = 0.0f;
    }
    
    public abstract int getNumGlyphs();
    
    abstract StrikeMetrics getFontMetrics();
    
    abstract void getGlyphImagePtrs(final int[] p0, final long[] p1, final int p2);
    
    abstract long getGlyphImagePtr(final int p0);
    
    abstract void getGlyphImageBounds(final int p0, final Point2D.Float p1, final Rectangle p2);
    
    abstract Point2D.Float getGlyphMetrics(final int p0);
    
    abstract Point2D.Float getCharMetrics(final char p0);
    
    abstract float getGlyphAdvance(final int p0);
    
    abstract float getCodePointAdvance(final int p0);
    
    abstract Rectangle2D.Float getGlyphOutlineBounds(final int p0);
    
    abstract GeneralPath getGlyphOutline(final int p0, final float p1, final float p2);
    
    abstract GeneralPath getGlyphVectorOutline(final int[] p0, final float p1, final float p2);
}
