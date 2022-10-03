package com.octo.captcha.component.image.textpaster.textdecorator;

import java.awt.geom.Rectangle2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.BasicStroke;
import java.awt.geom.QuadCurve2D;
import java.awt.geom.AffineTransform;
import java.awt.Composite;
import java.awt.AlphaComposite;
import com.octo.captcha.component.image.textpaster.MutableAttributedString;
import java.awt.Graphics2D;
import com.octo.captcha.component.image.color.SingleColorGenerator;
import java.security.SecureRandom;
import java.awt.Color;
import com.octo.captcha.component.image.color.ColorGenerator;
import java.util.Random;

public class LineTextDecorator implements TextDecorator
{
    private Random myRandom;
    private Integer numberOfLinesPerGlyph;
    private ColorGenerator linesColorGenerator;
    private int alphaCompositeType;
    
    public LineTextDecorator(final Integer n, final Color color) {
        this.myRandom = new SecureRandom();
        this.numberOfLinesPerGlyph = new Integer(3);
        this.linesColorGenerator = null;
        this.alphaCompositeType = 3;
        this.numberOfLinesPerGlyph = ((n != null) ? n : this.numberOfLinesPerGlyph);
        this.linesColorGenerator = new SingleColorGenerator((color != null) ? color : Color.white);
    }
    
    public LineTextDecorator(final Integer n, final ColorGenerator colorGenerator) {
        this.myRandom = new SecureRandom();
        this.numberOfLinesPerGlyph = new Integer(3);
        this.linesColorGenerator = null;
        this.alphaCompositeType = 3;
        this.numberOfLinesPerGlyph = ((n != null) ? n : this.numberOfLinesPerGlyph);
        this.linesColorGenerator = ((colorGenerator != null) ? colorGenerator : new SingleColorGenerator(Color.white));
    }
    
    public LineTextDecorator(final Integer n, final ColorGenerator colorGenerator, final Integer n2) {
        this(n, colorGenerator);
        this.alphaCompositeType = ((n2 != null) ? n2 : this.alphaCompositeType);
    }
    
    public void decorateAttributedString(final Graphics2D graphics2D, final MutableAttributedString mutableAttributedString) {
        final Color color = graphics2D.getColor();
        final Composite composite = graphics2D.getComposite();
        graphics2D.setComposite(AlphaComposite.getInstance(this.alphaCompositeType));
        for (int i = 0; i < mutableAttributedString.length(); ++i) {
            graphics2D.setColor(this.linesColorGenerator.getNextColor());
            final Rectangle2D frame = mutableAttributedString.getBounds(i).getFrame();
            for (int j = 0; j < this.numberOfLinesPerGlyph; ++j) {
                final double n = frame.getMinX() + frame.getWidth() * 0.7 * this.myRandom.nextDouble();
                final double n2 = frame.getMinY() - frame.getHeight() * 0.5 * this.myRandom.nextDouble();
                final double n3 = 5 + this.myRandom.nextInt(25);
                final double n4 = 5 + this.myRandom.nextInt(25);
                final double n5 = 3.141592653589793 * this.myRandom.nextDouble();
                final AffineTransform affineTransform = new AffineTransform(Math.cos(n5), -Math.sin(n5), Math.sin(n5), Math.cos(n5), n, n2);
                final QuadCurve2D.Double double1 = new QuadCurve2D.Double();
                double1.setCurve(0.0, 0.0, n4 / 2.0 + 15.0 * this.myRandom.nextDouble() * (this.myRandom.nextBoolean() ? -1 : 1), n3 / 2.0 + 15.0 * this.myRandom.nextDouble() * (this.myRandom.nextBoolean() ? -1 : 1), n4, n3);
                graphics2D.setStroke(new BasicStroke((float)(2 + this.myRandom.nextInt(4))));
                graphics2D.draw(affineTransform.createTransformedShape(double1));
            }
        }
        graphics2D.setComposite(composite);
        graphics2D.setColor(color);
    }
}
