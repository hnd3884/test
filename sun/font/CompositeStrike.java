package sun.font;

import java.awt.geom.Path2D;
import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.awt.Rectangle;
import java.awt.geom.Point2D;

public final class CompositeStrike extends FontStrike
{
    static final int SLOTMASK = 16777215;
    private CompositeFont compFont;
    private PhysicalStrike[] strikes;
    int numGlyphs;
    
    CompositeStrike(final CompositeFont compFont, final FontStrikeDesc desc) {
        this.numGlyphs = 0;
        this.compFont = compFont;
        this.desc = desc;
        this.disposer = new FontStrikeDisposer(this.compFont, desc);
        if (desc.style != this.compFont.style) {
            this.algoStyle = true;
            if ((desc.style & 0x1) == 0x1 && (this.compFont.style & 0x1) == 0x0) {
                this.boldness = 1.33f;
            }
            if ((desc.style & 0x2) == 0x2 && (this.compFont.style & 0x2) == 0x0) {
                this.italic = 0.7f;
            }
        }
        this.strikes = new PhysicalStrike[this.compFont.numSlots];
    }
    
    PhysicalStrike getStrikeForGlyph(final int n) {
        return this.getStrikeForSlot(n >>> 24);
    }
    
    PhysicalStrike getStrikeForSlot(int n) {
        if (n >= this.strikes.length) {
            n = 0;
        }
        PhysicalStrike physicalStrike = this.strikes[n];
        if (physicalStrike == null) {
            physicalStrike = (PhysicalStrike)this.compFont.getSlotFont(n).getStrike(this.desc);
            this.strikes[n] = physicalStrike;
        }
        return physicalStrike;
    }
    
    @Override
    public int getNumGlyphs() {
        return this.compFont.getNumGlyphs();
    }
    
    @Override
    StrikeMetrics getFontMetrics() {
        if (this.strikeMetrics == null) {
            final StrikeMetrics strikeMetrics = new StrikeMetrics();
            for (int i = 0; i < this.compFont.numMetricsSlots; ++i) {
                strikeMetrics.merge(this.getStrikeForSlot(i).getFontMetrics());
            }
            this.strikeMetrics = strikeMetrics;
        }
        return this.strikeMetrics;
    }
    
    @Override
    void getGlyphImagePtrs(final int[] array, final long[] array2, final int n) {
        final int slot0GlyphImagePtrs = this.getStrikeForSlot(0).getSlot0GlyphImagePtrs(array, array2, n);
        if (slot0GlyphImagePtrs == n) {
            return;
        }
        for (int i = slot0GlyphImagePtrs; i < n; ++i) {
            array2[i] = this.getStrikeForGlyph(array[i]).getGlyphImagePtr(array[i] & 0xFFFFFF);
        }
    }
    
    @Override
    long getGlyphImagePtr(final int n) {
        return this.getStrikeForGlyph(n).getGlyphImagePtr(n & 0xFFFFFF);
    }
    
    @Override
    void getGlyphImageBounds(final int n, final Point2D.Float float1, final Rectangle rectangle) {
        this.getStrikeForGlyph(n).getGlyphImageBounds(n & 0xFFFFFF, float1, rectangle);
    }
    
    @Override
    Point2D.Float getGlyphMetrics(final int n) {
        return this.getStrikeForGlyph(n).getGlyphMetrics(n & 0xFFFFFF);
    }
    
    @Override
    Point2D.Float getCharMetrics(final char c) {
        return this.getGlyphMetrics(this.compFont.getMapper().charToGlyph(c));
    }
    
    @Override
    float getGlyphAdvance(final int n) {
        return this.getStrikeForGlyph(n).getGlyphAdvance(n & 0xFFFFFF);
    }
    
    @Override
    float getCodePointAdvance(final int n) {
        return this.getGlyphAdvance(this.compFont.getMapper().charToGlyph(n));
    }
    
    @Override
    Rectangle2D.Float getGlyphOutlineBounds(final int n) {
        return this.getStrikeForGlyph(n).getGlyphOutlineBounds(n & 0xFFFFFF);
    }
    
    @Override
    GeneralPath getGlyphOutline(final int n, final float n2, final float n3) {
        final GeneralPath glyphOutline = this.getStrikeForGlyph(n).getGlyphOutline(n & 0xFFFFFF, n2, n3);
        if (glyphOutline == null) {
            return new GeneralPath();
        }
        return glyphOutline;
    }
    
    @Override
    GeneralPath getGlyphVectorOutline(final int[] array, final float n, final float n2) {
        Path2D path2D = null;
        int i = 0;
        while (i < array.length) {
            final int n3 = i;
            int n4;
            for (n4 = array[i] >>> 24; i < array.length && array[i + 1] >>> 24 == n4; ++i) {}
            final int n5 = i - n3 + 1;
            final int[] array2 = new int[n5];
            for (int j = 0; j < n5; ++j) {
                array2[j] = (array[j] & 0xFFFFFF);
            }
            final GeneralPath glyphVectorOutline = this.getStrikeForSlot(n4).getGlyphVectorOutline(array2, n, n2);
            if (path2D == null) {
                path2D = glyphVectorOutline;
            }
            else {
                if (glyphVectorOutline == null) {
                    continue;
                }
                path2D.append(glyphVectorOutline, false);
            }
        }
        if (path2D == null) {
            return new GeneralPath();
        }
        return (GeneralPath)path2D;
    }
}
