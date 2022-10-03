package com.octo.captcha.component.image.fontgenerator;

import java.awt.geom.AffineTransform;
import java.awt.Font;

public class TwistedAndShearedRandomFontGenerator extends TwistedRandomFontGenerator
{
    public TwistedAndShearedRandomFontGenerator(final Integer n, final Integer n2) {
        super(n, n2);
    }
    
    @Override
    protected Font applyCustomDeformationOnGeneratedFont(Font applyCustomDeformationOnGeneratedFont) {
        applyCustomDeformationOnGeneratedFont = super.applyCustomDeformationOnGeneratedFont(applyCustomDeformationOnGeneratedFont);
        return applyCustomDeformationOnGeneratedFont.deriveFont(AffineTransform.getShearInstance(this.myRandom.nextDouble() / 3.0, this.myRandom.nextDouble() / 3.0));
    }
}
