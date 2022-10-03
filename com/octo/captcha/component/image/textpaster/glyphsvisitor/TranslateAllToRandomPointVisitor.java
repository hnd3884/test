package com.octo.captcha.component.image.textpaster.glyphsvisitor;

import java.awt.geom.Rectangle2D;
import com.octo.captcha.component.image.textpaster.Glyphs;
import java.security.SecureRandom;
import java.util.Random;

public class TranslateAllToRandomPointVisitor implements GlyphsVisitors
{
    private Random myRandom;
    private double horizontalMargins;
    private double verticalMargins;
    
    public TranslateAllToRandomPointVisitor() {
        this.myRandom = new SecureRandom();
        this.horizontalMargins = 0.0;
        this.verticalMargins = 0.0;
    }
    
    public TranslateAllToRandomPointVisitor(final double horizontalMargins, final double verticalMargins) {
        this.myRandom = new SecureRandom();
        this.horizontalMargins = 0.0;
        this.verticalMargins = 0.0;
        this.horizontalMargins = horizontalMargins;
        this.verticalMargins = verticalMargins;
    }
    
    public void visit(final Glyphs glyphs, final Rectangle2D rectangle2D) {
        glyphs.translate((rectangle2D.getWidth() - glyphs.getBoundsWidth() - this.horizontalMargins) * this.myRandom.nextDouble() - glyphs.getBoundsX() + this.horizontalMargins / 2.0, (rectangle2D.getHeight() - glyphs.getBoundsHeight() - this.verticalMargins) * this.myRandom.nextDouble() - glyphs.getBoundsY() + this.verticalMargins / 2.0);
    }
}
