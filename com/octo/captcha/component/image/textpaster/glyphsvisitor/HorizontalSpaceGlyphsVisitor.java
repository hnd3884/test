package com.octo.captcha.component.image.textpaster.glyphsvisitor;

import java.awt.geom.Rectangle2D;
import com.octo.captcha.component.image.textpaster.Glyphs;

public class HorizontalSpaceGlyphsVisitor implements GlyphsVisitors
{
    private int spaceBetweenGlyphs;
    
    public HorizontalSpaceGlyphsVisitor(final int spaceBetweenGlyphs) {
        this.spaceBetweenGlyphs = 0;
        this.spaceBetweenGlyphs = spaceBetweenGlyphs;
    }
    
    public void visit(final Glyphs glyphs, final Rectangle2D rectangle2D) {
        for (int i = 1; i < glyphs.size(); ++i) {
            glyphs.translate(i, glyphs.getBoundsX(i - 1) + glyphs.getBoundsWidth(i - 1) - glyphs.getBoundsX(i) + this.spaceBetweenGlyphs, 0.0);
        }
    }
}
