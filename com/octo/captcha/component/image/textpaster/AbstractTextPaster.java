package com.octo.captcha.component.image.textpaster;

import java.awt.RenderingHints;
import java.awt.image.ImageObserver;
import java.awt.Image;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import com.octo.captcha.CaptchaException;
import com.octo.captcha.component.image.color.SingleColorGenerator;
import java.awt.Color;
import java.security.SecureRandom;
import com.octo.captcha.component.image.color.ColorGenerator;
import java.util.Random;

public abstract class AbstractTextPaster implements TextPaster
{
    public Random myRandom;
    private int max;
    private int min;
    private ColorGenerator colorGenerator;
    private boolean manageColorPerGlyph;
    
    AbstractTextPaster(final Integer n, final Integer n2) {
        this.myRandom = new SecureRandom();
        this.max = 20;
        this.min = 6;
        this.colorGenerator = new SingleColorGenerator(Color.black);
        this.manageColorPerGlyph = false;
        this.max = ((n2 != null) ? n2 : this.max);
        this.min = ((n != null && n <= this.max) ? n : Math.min(this.min, this.max - 1));
    }
    
    AbstractTextPaster(final Integer n, final Integer n2, final Color color) {
        this(n, n2);
        if (color != null) {
            this.colorGenerator = new SingleColorGenerator(color);
        }
    }
    
    AbstractTextPaster(final Integer n, final Integer n2, final ColorGenerator colorGenerator) {
        this(n, n2);
        if (colorGenerator == null) {
            throw new CaptchaException("ColorGenerator is null");
        }
        this.colorGenerator = colorGenerator;
    }
    
    AbstractTextPaster(final Integer n, final Integer n2, final ColorGenerator colorGenerator, final Boolean b) {
        this(n, n2, colorGenerator);
        this.manageColorPerGlyph = ((b != null) ? b : this.manageColorPerGlyph);
    }
    
    @Deprecated
    public int getMaxAcceptedWordLenght() {
        return this.max;
    }
    
    @Deprecated
    public int getMinAcceptedWordLenght() {
        return this.min;
    }
    
    public int getMaxAcceptedWordLength() {
        return this.max;
    }
    
    public int getMinAcceptedWordLength() {
        return this.min;
    }
    
    protected ColorGenerator getColorGenerator() {
        return this.colorGenerator;
    }
    
    BufferedImage copyBackground(final BufferedImage bufferedImage) {
        return new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), bufferedImage.getType());
    }
    
    Graphics2D pasteBackgroundAndSetTextColor(final BufferedImage bufferedImage, final BufferedImage bufferedImage2) {
        final Graphics2D graphics2D = (Graphics2D)bufferedImage.getGraphics();
        graphics2D.drawImage(bufferedImage2, 0, 0, bufferedImage.getWidth(), bufferedImage.getHeight(), null);
        graphics2D.setColor(this.colorGenerator.getNextColor());
        return graphics2D;
    }
    
    void customizeGraphicsRenderingHints(final Graphics2D graphics2D) {
        graphics2D.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        graphics2D.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        graphics2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    }
    
    public boolean isManageColorPerGlyph() {
        return this.manageColorPerGlyph;
    }
    
    public void setColorGenerator(final ColorGenerator colorGenerator) {
        this.colorGenerator = colorGenerator;
    }
}
