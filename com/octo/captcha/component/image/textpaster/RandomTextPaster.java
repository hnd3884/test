package com.octo.captcha.component.image.textpaster;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.text.AttributedString;
import java.awt.image.BufferedImage;
import com.octo.captcha.component.image.color.ColorGenerator;
import java.awt.Color;

public class RandomTextPaster extends AbstractTextPaster
{
    protected final int kerning = 20;
    protected Color[] textColors;
    
    public RandomTextPaster(final Integer n, final Integer n2, final Color color) {
        super(n, n2, color);
        this.textColors = null;
    }
    
    public RandomTextPaster(final Integer n, final Integer n2, final Color[] textColors) {
        super(n, n2);
        this.textColors = null;
        this.textColors = textColors;
    }
    
    public RandomTextPaster(final Integer n, final Integer n2, final ColorGenerator colorGenerator) {
        super(n, n2, colorGenerator);
        this.textColors = null;
    }
    
    public RandomTextPaster(final Integer n, final Integer n2, final ColorGenerator colorGenerator, final Boolean b) {
        super(n, n2, colorGenerator, b);
        this.textColors = null;
    }
    
    public BufferedImage pasteText(final BufferedImage bufferedImage, final AttributedString attributedString) {
        final BufferedImage copyBackground = this.copyBackground(bufferedImage);
        final Graphics2D pasteBackgroundAndSetTextColor = this.pasteBackgroundAndSetTextColor(copyBackground, bufferedImage);
        pasteBackgroundAndSetTextColor.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        pasteBackgroundAndSetTextColor.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        pasteBackgroundAndSetTextColor.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        final MutableAttributedString mutableAttributedString = new MutableAttributedString(pasteBackgroundAndSetTextColor, attributedString, 20);
        mutableAttributedString.useMinimumSpacing(20.0);
        mutableAttributedString.moveToRandomSpot(bufferedImage);
        if (this.isManageColorPerGlyph()) {
            mutableAttributedString.drawString(pasteBackgroundAndSetTextColor, this.getColorGenerator());
        }
        else {
            mutableAttributedString.drawString(pasteBackgroundAndSetTextColor);
        }
        pasteBackgroundAndSetTextColor.dispose();
        return copyBackground;
    }
}
