package com.octo.captcha.component.image.textpaster;

import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.Graphics;
import java.awt.Font;
import com.octo.captcha.CaptchaException;
import java.awt.Graphics2D;
import java.text.AttributedCharacterIterator;
import java.awt.font.TextAttribute;
import java.text.AttributedString;
import java.awt.image.BufferedImage;
import com.octo.captcha.component.image.color.ColorGenerator;
import java.util.HashMap;
import java.awt.Color;
import java.util.Map;

public class NonLinearRandomAngleTextPaster extends AbstractTextPaster
{
    private Map renderingHints;
    
    public NonLinearRandomAngleTextPaster(final Integer n, final Integer n2, final Color color) {
        super(n, n2, color);
        this.renderingHints = new HashMap();
    }
    
    public NonLinearRandomAngleTextPaster(final Integer n, final Integer n2, final ColorGenerator colorGenerator) {
        super(n, n2, colorGenerator);
        this.renderingHints = new HashMap();
    }
    
    public NonLinearRandomAngleTextPaster(final Integer n, final Integer n2, final ColorGenerator colorGenerator, final Boolean b) {
        super(n, n2, colorGenerator, b);
        this.renderingHints = new HashMap();
    }
    
    public BufferedImage pasteText(final BufferedImage bufferedImage, final AttributedString attributedString) throws CaptchaException {
        final BufferedImage copyBackground = this.copyBackground(bufferedImage);
        final Graphics2D pasteBackgroundAndSetTextColor = this.pasteBackgroundAndSetTextColor(copyBackground, bufferedImage);
        pasteBackgroundAndSetTextColor.setRenderingHints(this.renderingHints);
        pasteBackgroundAndSetTextColor.translate(10, bufferedImage.getHeight() / 2);
        final AttributedCharacterIterator iterator = attributedString.getIterator();
        while (iterator.getIndex() != iterator.getEndIndex()) {
            final AttributedString attributedString2 = new AttributedString(String.valueOf(iterator.current()));
            attributedString2.addAttribute(TextAttribute.FONT, iterator.getAttribute(TextAttribute.FONT));
            this.pasteCharacter(pasteBackgroundAndSetTextColor, attributedString2);
            iterator.next();
        }
        pasteBackgroundAndSetTextColor.dispose();
        return copyBackground;
    }
    
    protected void pasteCharacter(final Graphics2D graphics2D, final AttributedString attributedString) {
        final Font font = (Font)attributedString.getIterator().getAttribute(TextAttribute.FONT);
        final Rectangle2D stringBounds = graphics2D.getFontMetrics(font).getStringBounds(String.valueOf(attributedString.getIterator().current()), graphics2D);
        final double randomAngle = this.getRandomAngle();
        final int n = (int)graphics2D.getTransform().getTranslateY();
        final double n2 = this.myRandom.nextBoolean() ? this.myRandom.nextInt(n) : ((double)(-this.myRandom.nextInt(n - (int)stringBounds.getHeight())));
        graphics2D.setFont(font);
        graphics2D.translate(0.0, n2);
        if (randomAngle >= 1.5707963267948966 || randomAngle <= -1.5707963267948966) {
            attributedString.addAttribute(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_LOW_TWO_PIXEL);
        }
        graphics2D.rotate(randomAngle, stringBounds.getX() + stringBounds.getWidth() / 2.0, stringBounds.getY() + stringBounds.getHeight() / 2.0);
        graphics2D.drawString(attributedString.getIterator(), 0, 0);
        graphics2D.rotate(-randomAngle, stringBounds.getX() + stringBounds.getWidth() / 2.0, stringBounds.getY() + stringBounds.getHeight() / 2.0);
        graphics2D.translate(stringBounds.getHeight(), -n2);
    }
    
    protected double getRandomAngle() {
        final double n = 3.141592653589793 / (this.myRandom.nextDouble() * this.myRandom.nextInt(10) + 1.0);
        return this.myRandom.nextBoolean() ? n : (-n);
    }
    
    public void addRenderingHints(final RenderingHints.Key key, final Object o) {
        this.renderingHints.put(key, o);
    }
}
