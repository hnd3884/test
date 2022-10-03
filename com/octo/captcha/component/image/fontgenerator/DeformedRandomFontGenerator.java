package com.octo.captcha.component.image.fontgenerator;

import java.awt.geom.AffineTransform;
import java.awt.Font;

public class DeformedRandomFontGenerator extends RandomFontGenerator
{
    public DeformedRandomFontGenerator(final Integer n, final Integer n2) {
        super(n, n2);
    }
    
    @Override
    protected Font applyCustomDeformationOnGeneratedFont(final Font font) {
        final float n = (this.myRandom.nextBoolean() ? 1 : -1) * this.myRandom.nextFloat() / 3.0f;
        final AffineTransform affineTransform = new AffineTransform();
        affineTransform.rotate(n, this.myRandom.nextDouble(), this.myRandom.nextDouble());
        return font.deriveFont(affineTransform);
    }
}
