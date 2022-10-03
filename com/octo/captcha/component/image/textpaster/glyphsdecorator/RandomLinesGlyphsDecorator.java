package com.octo.captcha.component.image.textpaster.glyphsdecorator;

import java.awt.Color;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.BasicStroke;
import java.awt.geom.QuadCurve2D;
import java.awt.Composite;
import java.awt.AlphaComposite;
import java.awt.image.BufferedImage;
import com.octo.captcha.component.image.textpaster.Glyphs;
import java.awt.Graphics2D;
import java.security.SecureRandom;
import com.octo.captcha.component.image.color.ColorGenerator;
import java.util.Random;

public class RandomLinesGlyphsDecorator implements GlyphsDecorator
{
    private static final double SQRT_2;
    private Random myRandom;
    private double numberOfLinesPerGlyph;
    private ColorGenerator linesColorGenerator;
    private double lineWidth;
    private double lineLength;
    private int alphaCompositeType;
    
    public RandomLinesGlyphsDecorator(final double numberOfLinesPerGlyph, final ColorGenerator linesColorGenerator, final double lineWidth, final double lineLength) {
        this.myRandom = new SecureRandom();
        this.numberOfLinesPerGlyph = 3.0;
        this.linesColorGenerator = null;
        this.alphaCompositeType = 3;
        this.numberOfLinesPerGlyph = numberOfLinesPerGlyph;
        this.linesColorGenerator = linesColorGenerator;
        this.lineWidth = lineWidth;
        this.lineLength = lineLength;
    }
    
    public void decorate(final Graphics2D graphics2D, final Glyphs glyphs, final BufferedImage bufferedImage) {
        final Composite composite = graphics2D.getComposite();
        final Stroke stroke = graphics2D.getStroke();
        final Color color = graphics2D.getColor();
        graphics2D.setComposite(AlphaComposite.getInstance(this.alphaCompositeType));
        for (int n = 0; n < Math.round(glyphs.size() * this.numberOfLinesPerGlyph); ++n) {
            final double n2 = this.around(this.lineLength, 0.5) / (2.0 * RandomLinesGlyphsDecorator.SQRT_2);
            final double around = this.around(this.lineWidth, 0.3);
            final double n3 = (bufferedImage.getWidth() - this.lineWidth) * this.myRandom.nextDouble();
            final double n4 = (bufferedImage.getHeight() - this.lineWidth) * this.myRandom.nextDouble();
            final double n5 = n3 + this.around(n2, 0.5) * this.nextSign();
            final double n6 = n4 + this.around(n2, 0.5) * this.nextSign();
            final QuadCurve2D.Double double1 = new QuadCurve2D.Double(n3, n4, n5, n6, n5 + this.around(n2, 0.5) * this.nextSign(), n6 + this.around(n2, 0.5) * this.nextSign());
            graphics2D.setColor(this.linesColorGenerator.getNextColor());
            graphics2D.setStroke(new BasicStroke((float)around));
            graphics2D.draw(double1);
        }
        graphics2D.setComposite(composite);
        graphics2D.setColor(color);
        graphics2D.setStroke(stroke);
    }
    
    private double around(final double n, final double n2) {
        final double n3 = n * n2;
        return 2.0 * n3 * this.myRandom.nextDouble() + n - n3;
    }
    
    private double nextSign() {
        return this.myRandom.nextBoolean() ? 1.0 : -1.0;
    }
    
    static {
        SQRT_2 = Math.sqrt(2.0);
    }
}
