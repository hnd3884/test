package com.octo.captcha.component.image.backgroundgenerator;

import java.awt.Graphics2D;
import com.octo.captcha.component.image.color.SingleColorGenerator;
import java.awt.Color;
import com.octo.captcha.component.image.color.ColorGenerator;
import java.awt.image.BufferedImage;

public class UniColorBackgroundGenerator extends AbstractBackgroundGenerator
{
    private BufferedImage backround;
    private ColorGenerator colorGenerator;
    
    public UniColorBackgroundGenerator(final Integer n, final Integer n2) {
        this(n, n2, Color.white);
    }
    
    public UniColorBackgroundGenerator(final Integer n, final Integer n2, final Color color) {
        super(n, n2);
        this.colorGenerator = null;
        this.colorGenerator = new SingleColorGenerator(color);
    }
    
    public UniColorBackgroundGenerator(final Integer n, final Integer n2, final ColorGenerator colorGenerator) {
        super(n, n2);
        this.colorGenerator = null;
        this.colorGenerator = colorGenerator;
    }
    
    public BufferedImage getBackground() {
        this.backround = new BufferedImage(this.getImageWidth(), this.getImageHeight(), 1);
        final Graphics2D graphics2D = (Graphics2D)this.backround.getGraphics();
        final Color nextColor = this.colorGenerator.getNextColor();
        graphics2D.setColor((nextColor != null) ? nextColor : Color.white);
        graphics2D.setBackground((nextColor != null) ? nextColor : Color.white);
        graphics2D.fillRect(0, 0, this.getImageWidth(), this.getImageHeight());
        graphics2D.dispose();
        return this.backround;
    }
}
