package com.octo.captcha.component.image.backgroundgenerator;

import java.awt.Paint;
import java.awt.GradientPaint;
import java.awt.RenderingHints;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import com.octo.captcha.component.image.color.SingleColorGenerator;
import com.octo.captcha.CaptchaException;
import java.awt.Color;
import com.octo.captcha.component.image.color.ColorGenerator;

public class GradientBackgroundGenerator extends AbstractBackgroundGenerator
{
    ColorGenerator firstColor;
    ColorGenerator secondColor;
    
    public GradientBackgroundGenerator(final Integer n, final Integer n2, final Color color, final Color color2) {
        super(n, n2);
        this.firstColor = null;
        this.secondColor = null;
        if (color == null || color2 == null) {
            throw new CaptchaException("Color is null");
        }
        this.firstColor = new SingleColorGenerator(color);
        this.secondColor = new SingleColorGenerator(color2);
    }
    
    public GradientBackgroundGenerator(final Integer n, final Integer n2, final ColorGenerator firstColor, final ColorGenerator secondColor) {
        super(n, n2);
        this.firstColor = null;
        this.secondColor = null;
        if (firstColor == null || secondColor == null) {
            throw new CaptchaException("ColorGenerator is null");
        }
        this.firstColor = firstColor;
        this.secondColor = secondColor;
    }
    
    public BufferedImage getBackground() {
        final BufferedImage bufferedImage = new BufferedImage(this.getImageWidth(), this.getImageHeight(), 1);
        final Graphics2D graphics2D = (Graphics2D)bufferedImage.getGraphics();
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics2D.setPaint(new GradientPaint(0.0f, (float)this.getImageHeight(), this.firstColor.getNextColor(), (float)this.getImageWidth(), 0.0f, this.secondColor.getNextColor()));
        graphics2D.fillRect(0, 0, this.getImageWidth(), this.getImageHeight());
        graphics2D.dispose();
        return bufferedImage;
    }
}
