package com.octo.captcha.component.image.backgroundgenerator;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import com.octo.captcha.component.image.color.SingleColorGenerator;
import java.awt.Color;
import com.octo.captcha.component.image.color.ColorGenerator;

public class FunkyBackgroundGenerator extends AbstractBackgroundGenerator
{
    ColorGenerator colorGeneratorLeftUp;
    ColorGenerator colorGeneratorLeftDown;
    ColorGenerator colorGeneratorRightUp;
    ColorGenerator colorGeneratorRightDown;
    float perturbationlevel;
    
    public FunkyBackgroundGenerator(final Integer n, final Integer n2) {
        this(n, n2, new SingleColorGenerator(Color.yellow), new SingleColorGenerator(Color.red), new SingleColorGenerator(Color.yellow), new SingleColorGenerator(Color.green), 0.5f);
    }
    
    public FunkyBackgroundGenerator(final Integer n, final Integer n2, final ColorGenerator colorGenerator) {
        this(n, n2, colorGenerator, colorGenerator, colorGenerator, colorGenerator, 0.5f);
    }
    
    public FunkyBackgroundGenerator(final Integer n, final Integer n2, final ColorGenerator colorGeneratorLeftUp, final ColorGenerator colorGeneratorLeftDown, final ColorGenerator colorGeneratorRightUp, final ColorGenerator colorGeneratorRightDown, final float perturbationlevel) {
        super(n, n2);
        this.colorGeneratorLeftUp = null;
        this.colorGeneratorLeftDown = null;
        this.colorGeneratorRightUp = null;
        this.colorGeneratorRightDown = null;
        this.perturbationlevel = 0.1f;
        this.colorGeneratorLeftUp = colorGeneratorLeftUp;
        this.colorGeneratorLeftDown = colorGeneratorLeftDown;
        this.colorGeneratorRightDown = colorGeneratorRightDown;
        this.colorGeneratorRightUp = colorGeneratorRightUp;
        this.perturbationlevel = perturbationlevel;
    }
    
    public BufferedImage getBackground() {
        final Color nextColor = this.colorGeneratorLeftUp.getNextColor();
        final Color nextColor2 = this.colorGeneratorLeftDown.getNextColor();
        final Color nextColor3 = this.colorGeneratorRightUp.getNextColor();
        final Color nextColor4 = this.colorGeneratorRightDown.getNextColor();
        final BufferedImage bufferedImage = new BufferedImage(this.getImageWidth(), this.getImageHeight(), 4);
        final Graphics2D graphics = bufferedImage.createGraphics();
        graphics.setColor(Color.white);
        graphics.fillRect(0, 0, this.getImageHeight(), this.getImageWidth());
        final float n = (float)this.getImageHeight();
        final float n2 = (float)this.getImageWidth();
        for (int i = 0; i < this.getImageHeight(); ++i) {
            for (int j = 0; j < this.getImageWidth(); ++j) {
                final float n3 = (1.0f - j / n2) * (1.0f - i / n);
                final float n4 = (1.0f - j / n2) * (i / n);
                final float n5 = j / n2 * (1.0f - i / n);
                final float n6 = j / n2 * (i / n);
                final float n7 = nextColor.getRed() / 255.0f * n3 + nextColor2.getRed() / 255.0f * n4 + nextColor3.getRed() / 255.0f * n5 + nextColor4.getRed() / 255.0f * n6;
                final float n8 = nextColor.getGreen() / 255.0f * n3 + nextColor2.getGreen() / 255.0f * n4 + nextColor3.getGreen() / 255.0f * n5 + nextColor4.getGreen() / 255.0f * n6;
                final float n9 = nextColor.getBlue() / 255.0f * n3 + nextColor2.getBlue() / 255.0f * n4 + nextColor3.getBlue() / 255.0f * n5 + nextColor4.getBlue() / 255.0f * n6;
                if (this.myRandom.nextFloat() > this.perturbationlevel) {
                    graphics.setColor(new Color(n7, n8, n9, 1.0f));
                }
                else {
                    graphics.setColor(new Color(this.compute(n7), this.compute(n8), this.compute(n9), 1.0f));
                }
                graphics.drawLine(j, i, j, i);
            }
        }
        graphics.dispose();
        return bufferedImage;
    }
    
    private float compute(final float n) {
        final float n2 = (1.0f - n < n) ? (1.0f - n) : n;
        if (this.myRandom.nextFloat() > 0.5f) {
            return n - this.myRandom.nextFloat() * n2;
        }
        return n + this.myRandom.nextFloat() * n2;
    }
}
