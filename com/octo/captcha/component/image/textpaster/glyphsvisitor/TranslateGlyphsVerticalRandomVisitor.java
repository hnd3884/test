package com.octo.captcha.component.image.textpaster.glyphsvisitor;

import java.awt.geom.Rectangle2D;
import com.octo.captcha.component.image.textpaster.Glyphs;
import java.security.SecureRandom;
import java.util.Random;

public class TranslateGlyphsVerticalRandomVisitor implements GlyphsVisitors
{
    private Random myRandom;
    private double verticalRange;
    
    public TranslateGlyphsVerticalRandomVisitor(final double verticalRange) {
        this.myRandom = new SecureRandom();
        this.verticalRange = 1.0;
        this.verticalRange = verticalRange;
    }
    
    public void visit(final Glyphs glyphs, final Rectangle2D rectangle2D) {
        for (int i = 0; i < glyphs.size(); ++i) {
            glyphs.translate(i, 0.0, this.verticalRange * this.myRandom.nextGaussian());
        }
    }
}
