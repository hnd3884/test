package sun.font;

import java.awt.font.GlyphJustificationInfo;
import java.awt.geom.Rectangle2D;

public abstract class ExtendedTextLabel extends TextLabel implements TextLineComponent
{
    @Override
    public abstract int getNumCharacters();
    
    @Override
    public abstract CoreMetrics getCoreMetrics();
    
    @Override
    public abstract float getCharX(final int p0);
    
    @Override
    public abstract float getCharY(final int p0);
    
    @Override
    public abstract float getCharAdvance(final int p0);
    
    public abstract Rectangle2D getCharVisualBounds(final int p0, final float p1, final float p2);
    
    public abstract int logicalToVisual(final int p0);
    
    public abstract int visualToLogical(final int p0);
    
    @Override
    public abstract int getLineBreakIndex(final int p0, final float p1);
    
    @Override
    public abstract float getAdvanceBetween(final int p0, final int p1);
    
    @Override
    public abstract boolean caretAtOffsetIsValid(final int p0);
    
    @Override
    public Rectangle2D getCharVisualBounds(final int n) {
        return this.getCharVisualBounds(n, 0.0f, 0.0f);
    }
    
    @Override
    public abstract TextLineComponent getSubset(final int p0, final int p1, final int p2);
    
    @Override
    public abstract int getNumJustificationInfos();
    
    @Override
    public abstract void getJustificationInfos(final GlyphJustificationInfo[] p0, final int p1, final int p2, final int p3);
    
    @Override
    public abstract TextLineComponent applyJustificationDeltas(final float[] p0, final int p1, final boolean[] p2);
}
