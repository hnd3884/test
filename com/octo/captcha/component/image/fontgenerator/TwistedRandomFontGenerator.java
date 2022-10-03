package com.octo.captcha.component.image.fontgenerator;

import java.awt.geom.AffineTransform;
import java.awt.Font;

public class TwistedRandomFontGenerator extends RandomFontGenerator
{
    public TwistedRandomFontGenerator(final Integer n, final Integer n2) {
        super(n, n2);
    }
    
    @Override
    protected Font applyCustomDeformationOnGeneratedFont(final Font font) {
        final AffineTransform affineTransform = new AffineTransform();
        final float n = this.myRandom.nextFloat() / 3.0f;
        affineTransform.rotate(this.myRandom.nextBoolean() ? ((double)n) : ((double)(-n)));
        return font.deriveFont(affineTransform);
    }
}
