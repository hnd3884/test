package com.octo.captcha.component.image.textpaster.glyphsvisitor;

import java.awt.geom.Rectangle2D;
import com.octo.captcha.component.image.textpaster.Glyphs;

public class MoveAllGlyphsToOriginVisitor implements GlyphsVisitors
{
    public void visit(final Glyphs glyphs, final Rectangle2D rectangle2D) {
        for (int i = 0; i < glyphs.size(); ++i) {
            glyphs.translate(i, -glyphs.getX(i), -glyphs.getY(i));
        }
    }
}
