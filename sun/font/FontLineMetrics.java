package sun.font;

import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;

public final class FontLineMetrics extends LineMetrics implements Cloneable
{
    public int numchars;
    public final CoreMetrics cm;
    public final FontRenderContext frc;
    
    public FontLineMetrics(final int numchars, final CoreMetrics cm, final FontRenderContext frc) {
        this.numchars = numchars;
        this.cm = cm;
        this.frc = frc;
    }
    
    @Override
    public final int getNumChars() {
        return this.numchars;
    }
    
    @Override
    public final float getAscent() {
        return this.cm.ascent;
    }
    
    @Override
    public final float getDescent() {
        return this.cm.descent;
    }
    
    @Override
    public final float getLeading() {
        return this.cm.leading;
    }
    
    @Override
    public final float getHeight() {
        return this.cm.height;
    }
    
    @Override
    public final int getBaselineIndex() {
        return this.cm.baselineIndex;
    }
    
    @Override
    public final float[] getBaselineOffsets() {
        return this.cm.baselineOffsets.clone();
    }
    
    @Override
    public final float getStrikethroughOffset() {
        return this.cm.strikethroughOffset;
    }
    
    @Override
    public final float getStrikethroughThickness() {
        return this.cm.strikethroughThickness;
    }
    
    @Override
    public final float getUnderlineOffset() {
        return this.cm.underlineOffset;
    }
    
    @Override
    public final float getUnderlineThickness() {
        return this.cm.underlineThickness;
    }
    
    @Override
    public final int hashCode() {
        return this.cm.hashCode();
    }
    
    @Override
    public final boolean equals(final Object o) {
        try {
            return this.cm.equals(((FontLineMetrics)o).cm);
        }
        catch (final ClassCastException ex) {
            return false;
        }
    }
    
    public final Object clone() {
        try {
            return super.clone();
        }
        catch (final CloneNotSupportedException ex) {
            throw new InternalError(ex);
        }
    }
}
