package com.octo.captcha.component.image.backgroundgenerator;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Arc2D;
import java.awt.Composite;
import java.awt.AlphaComposite;
import java.awt.Stroke;
import java.awt.BasicStroke;
import java.awt.image.BufferedImage;

public class EllipseBackgroundGenerator extends AbstractBackgroundGenerator
{
    public EllipseBackgroundGenerator(final Integer n, final Integer n2) {
        super(n, n2);
    }
    
    public BufferedImage getBackground() {
        final BufferedImage bufferedImage = new BufferedImage(this.getImageWidth(), this.getImageHeight(), 1);
        final Graphics2D graphics = bufferedImage.createGraphics();
        graphics.setStroke(new BasicStroke(2.0f, 0, 0, 2.0f, new float[] { 2.0f, 2.0f }, 0.0f));
        graphics.setComposite(AlphaComposite.getInstance(3, 0.75f));
        graphics.translate(this.getImageWidth() * -1.0, 0.0);
        final double n = 5.0;
        double n2 = 0.0;
        for (double n3 = 0.0; n3 < 2.0 * this.getImageWidth(); n3 += n) {
            graphics.draw(new Arc2D.Double(0.0, 0.0, this.getImageWidth(), this.getImageHeight(), 0.0, 360.0, 0));
            graphics.translate(n, 0.0);
            n2 += n;
        }
        return bufferedImage;
    }
}
