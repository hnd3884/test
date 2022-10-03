package com.octo.captcha.component.image.textpaster.glyphsvisitor;

import java.awt.geom.Rectangle2D;
import com.octo.captcha.component.image.textpaster.Glyphs;

public class OverlapGlyphsVisitor implements GlyphsVisitors
{
    private double overlapPixels;
    
    public OverlapGlyphsVisitor(final double overlapPixels) {
        this.overlapPixels = 0.0;
        this.overlapPixels = overlapPixels;
    }
    
    public void visit(final Glyphs glyphs, final Rectangle2D rectangle2D) {
        for (int i = 1; i < glyphs.size(); ++i) {
            glyphs.translate(i, glyphs.getBoundsX(i - 1) + glyphs.getBoundsWidth(i - 1) - glyphs.getBoundsX(i) - Math.abs(glyphs.getRSB(i - 1)) - Math.abs(glyphs.getLSB(i)) - this.overlapPixels, 0.0);
        }
    }
}
