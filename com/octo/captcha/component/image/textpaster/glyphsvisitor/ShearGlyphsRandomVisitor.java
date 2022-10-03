package com.octo.captcha.component.image.textpaster.glyphsvisitor;

import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import com.octo.captcha.component.image.textpaster.Glyphs;
import java.security.SecureRandom;
import java.util.Random;

public class ShearGlyphsRandomVisitor implements GlyphsVisitors
{
    private double maxShearX;
    private double maxShearY;
    private Random myRandom;
    
    public ShearGlyphsRandomVisitor(final double maxShearX, final double maxShearY) {
        this.myRandom = new SecureRandom();
        this.maxShearX = maxShearX;
        this.maxShearY = maxShearY;
    }
    
    public void visit(final Glyphs glyphs, final Rectangle2D rectangle2D) {
        for (int i = 0; i < glyphs.size(); ++i) {
            final AffineTransform affineTransform = new AffineTransform();
            affineTransform.setToShear(this.maxShearX * this.myRandom.nextGaussian(), this.maxShearY * this.myRandom.nextGaussian());
            glyphs.addAffineTransform(i, affineTransform);
        }
    }
}
