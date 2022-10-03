package com.octo.captcha.component.image.textpaster.glyphsvisitor;

import java.awt.geom.Rectangle2D;
import com.octo.captcha.component.image.textpaster.Glyphs;
import java.security.SecureRandom;
import java.util.Random;

public class RotateGlyphsRandomVisitor implements GlyphsVisitors
{
    private double maxAngle;
    private Random myRandom;
    
    public RotateGlyphsRandomVisitor() {
        this.maxAngle = 0.39269908169872414;
        this.myRandom = new SecureRandom();
    }
    
    public RotateGlyphsRandomVisitor(final double maxAngle) {
        this.maxAngle = 0.39269908169872414;
        this.myRandom = new SecureRandom();
        this.maxAngle = maxAngle;
    }
    
    public void visit(final Glyphs glyphs, final Rectangle2D rectangle2D) {
        for (int i = 0; i < glyphs.size(); ++i) {
            glyphs.rotate(i, this.maxAngle * this.myRandom.nextGaussian());
        }
    }
}
