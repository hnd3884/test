package com.octo.captcha.component.image.textpaster;

import com.octo.captcha.CaptchaException;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.text.AttributedString;
import java.awt.image.BufferedImage;
import com.octo.captcha.component.image.color.ColorGenerator;
import com.octo.captcha.component.image.textpaster.textdecorator.TextDecorator;

public class DecoratedRandomTextPaster extends AbstractTextPaster
{
    protected final int kerning = 20;
    private TextDecorator[] decorators;
    
    public DecoratedRandomTextPaster(final Integer n, final Integer n2, final ColorGenerator colorGenerator, final TextDecorator[] decorators) {
        super(n, n2, colorGenerator);
        this.decorators = decorators;
    }
    
    public DecoratedRandomTextPaster(final Integer n, final Integer n2, final ColorGenerator colorGenerator, final Boolean b, final TextDecorator[] decorators) {
        super(n, n2, colorGenerator, b);
        this.decorators = decorators;
    }
    
    public BufferedImage pasteText(final BufferedImage bufferedImage, final AttributedString attributedString) throws CaptchaException {
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
        if (this.decorators != null) {
            for (int i = 0; i < this.decorators.length; ++i) {
                this.decorators[i].decorateAttributedString(pasteBackgroundAndSetTextColor, mutableAttributedString);
            }
        }
        pasteBackgroundAndSetTextColor.dispose();
        return copyBackground;
    }
}
