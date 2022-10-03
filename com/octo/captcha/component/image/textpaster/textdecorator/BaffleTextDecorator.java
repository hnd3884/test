package com.octo.captcha.component.image.textpaster.textdecorator;

import java.awt.geom.Rectangle2D;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.Composite;
import java.awt.AlphaComposite;
import com.octo.captcha.component.image.textpaster.MutableAttributedString;
import java.awt.Graphics2D;
import com.octo.captcha.component.image.color.SingleColorGenerator;
import java.security.SecureRandom;
import java.awt.Color;
import com.octo.captcha.component.image.color.ColorGenerator;
import java.util.Random;

public class BaffleTextDecorator implements TextDecorator
{
    private Random myRandom;
    private static double circleXRatio;
    private static double circleYRatio;
    private Integer numberOfHolesPerGlyph;
    private ColorGenerator holesColorGenerator;
    private int alphaCompositeType;
    
    public BaffleTextDecorator(final Integer n, final Color color) {
        this.myRandom = new SecureRandom();
        this.numberOfHolesPerGlyph = new Integer(3);
        this.holesColorGenerator = null;
        this.alphaCompositeType = 3;
        this.numberOfHolesPerGlyph = ((n != null) ? n : this.numberOfHolesPerGlyph);
        this.holesColorGenerator = new SingleColorGenerator((color != null) ? color : Color.white);
    }
    
    public BaffleTextDecorator(final Integer n, final ColorGenerator colorGenerator) {
        this.myRandom = new SecureRandom();
        this.numberOfHolesPerGlyph = new Integer(3);
        this.holesColorGenerator = null;
        this.alphaCompositeType = 3;
        this.numberOfHolesPerGlyph = ((n != null) ? n : this.numberOfHolesPerGlyph);
        this.holesColorGenerator = ((colorGenerator != null) ? colorGenerator : new SingleColorGenerator(Color.white));
    }
    
    public BaffleTextDecorator(final Integer n, final ColorGenerator colorGenerator, final Integer n2) {
        this(n, colorGenerator);
        this.alphaCompositeType = ((n2 != null) ? n2 : this.alphaCompositeType);
    }
    
    public void decorateAttributedString(final Graphics2D graphics2D, final MutableAttributedString mutableAttributedString) {
        final Color color = graphics2D.getColor();
        final Composite composite = graphics2D.getComposite();
        graphics2D.setComposite(AlphaComposite.getInstance(this.alphaCompositeType));
        for (int i = 0; i < mutableAttributedString.length(); ++i) {
            graphics2D.setColor(this.holesColorGenerator.getNextColor());
            final Rectangle2D frame = mutableAttributedString.getBounds(i).getFrame();
            final double n = frame.getWidth() / 2.0;
            for (int j = 0; j < this.numberOfHolesPerGlyph; ++j) {
                final double n2 = n * (1.0 + this.myRandom.nextDouble()) / 2.0;
                graphics2D.fill(new Ellipse2D.Double(frame.getMinX() + frame.getWidth() * BaffleTextDecorator.circleXRatio * this.myRandom.nextDouble(), frame.getMinY() - frame.getHeight() * BaffleTextDecorator.circleYRatio * this.myRandom.nextDouble(), n2, n2));
            }
        }
        graphics2D.setColor(color);
        graphics2D.setComposite(composite);
    }
    
    static {
        BaffleTextDecorator.circleXRatio = 0.7;
        BaffleTextDecorator.circleYRatio = 0.5;
    }
}
