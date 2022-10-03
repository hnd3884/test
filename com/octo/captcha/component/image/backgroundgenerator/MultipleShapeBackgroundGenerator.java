package com.octo.captcha.component.image.backgroundgenerator;

import java.awt.geom.Rectangle2D;
import java.awt.Shape;
import java.awt.Paint;
import java.awt.GradientPaint;
import java.awt.geom.Ellipse2D;
import java.awt.RenderingHints;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import com.octo.captcha.component.image.color.SingleColorGenerator;
import java.awt.Color;
import com.octo.captcha.component.image.color.ColorGenerator;

public class MultipleShapeBackgroundGenerator extends AbstractBackgroundGenerator
{
    private ColorGenerator firstEllipseColorGenerator;
    private ColorGenerator secondEllipseColorGenerator;
    private ColorGenerator firstRectangleColorGenerator;
    private ColorGenerator secondRectangleColorGenerator;
    private Integer spaceBetweenLine;
    private Integer spaceBetweenCircle;
    private Integer ellipseHeight;
    private Integer ellipseWidth;
    private Integer rectangleWidth;
    
    public MultipleShapeBackgroundGenerator(final Integer n, final Integer n2) {
        super(n, n2);
        this.firstEllipseColorGenerator = new SingleColorGenerator(new Color(210, 210, 210));
        this.secondEllipseColorGenerator = new SingleColorGenerator(new Color(0, 0, 0));
        this.firstRectangleColorGenerator = new SingleColorGenerator(new Color(210, 210, 210));
        this.secondRectangleColorGenerator = new SingleColorGenerator(new Color(0, 0, 0));
        this.spaceBetweenLine = new Integer(10);
        this.spaceBetweenCircle = new Integer(10);
        this.ellipseHeight = new Integer(8);
        this.ellipseWidth = new Integer(8);
        this.rectangleWidth = new Integer(3);
    }
    
    public MultipleShapeBackgroundGenerator(final Integer n, final Integer n2, final Color color, final Color color2, final Integer n3, final Integer n4, final Integer ellipseHeight, final Integer ellipseWidth, final Color color3, final Color color4, final Integer rectangleWidth) {
        super(n, n2);
        this.firstEllipseColorGenerator = new SingleColorGenerator(new Color(210, 210, 210));
        this.secondEllipseColorGenerator = new SingleColorGenerator(new Color(0, 0, 0));
        this.firstRectangleColorGenerator = new SingleColorGenerator(new Color(210, 210, 210));
        this.secondRectangleColorGenerator = new SingleColorGenerator(new Color(0, 0, 0));
        this.spaceBetweenLine = new Integer(10);
        this.spaceBetweenCircle = new Integer(10);
        this.ellipseHeight = new Integer(8);
        this.ellipseWidth = new Integer(8);
        this.rectangleWidth = new Integer(3);
        if (color != null) {
            this.firstEllipseColorGenerator = new SingleColorGenerator(color);
        }
        if (color2 != null) {
            this.secondEllipseColorGenerator = new SingleColorGenerator(color2);
        }
        if (n3 != null) {
            this.spaceBetweenLine = n4;
        }
        if (n4 != null) {
            this.spaceBetweenCircle = n4;
        }
        if (ellipseHeight != null) {
            this.ellipseHeight = ellipseHeight;
        }
        if (ellipseWidth != null) {
            this.ellipseWidth = ellipseWidth;
        }
        if (color3 != null) {
            this.firstRectangleColorGenerator = new SingleColorGenerator(color3);
        }
        if (color4 != null) {
            this.secondRectangleColorGenerator = new SingleColorGenerator(color4);
        }
        if (rectangleWidth != null) {
            this.rectangleWidth = rectangleWidth;
        }
    }
    
    public BufferedImage getBackground() {
        final BufferedImage bufferedImage = new BufferedImage(this.getImageWidth(), this.getImageHeight(), 1);
        final Graphics2D graphics2D = (Graphics2D)bufferedImage.getGraphics();
        graphics2D.setBackground(Color.white);
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        for (int i = 0; i < this.getImageWidth(); i += this.getSpaceBetweenLine()) {
            final Color nextColor = this.firstEllipseColorGenerator.getNextColor();
            final Color nextColor2 = this.secondEllipseColorGenerator.getNextColor();
            final Color nextColor3 = this.firstRectangleColorGenerator.getNextColor();
            final Color nextColor4 = this.secondRectangleColorGenerator.getNextColor();
            for (int j = 0; j < this.getImageHeight(); j += this.getSpaceBetweenCircle()) {
                final Ellipse2D.Double double1 = new Ellipse2D.Double(i, j, this.getEllipseWidth(), this.getEllipseHeight());
                graphics2D.setPaint(new GradientPaint(0.0f, (float)this.getEllipseHeight(), nextColor, (float)this.getEllipseWidth(), 0.0f, nextColor2, true));
                graphics2D.fill(double1);
            }
            graphics2D.setPaint(new GradientPaint(0.0f, (float)this.getImageHeight(), nextColor3, (float)this.getRectangleWidth(), 0.0f, nextColor4, true));
            graphics2D.fill(new Rectangle2D.Double(i, 0.0, this.getRectangleWidth(), this.getImageHeight()));
        }
        graphics2D.dispose();
        return bufferedImage;
    }
    
    protected int getSpaceBetweenLine() {
        return this.spaceBetweenLine;
    }
    
    protected int getSpaceBetweenCircle() {
        return this.spaceBetweenCircle;
    }
    
    protected int getEllipseHeight() {
        return this.ellipseHeight;
    }
    
    protected int getEllipseWidth() {
        return this.ellipseWidth;
    }
    
    protected int getRectangleWidth() {
        return this.rectangleWidth;
    }
}
