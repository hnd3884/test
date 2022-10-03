package com.octo.captcha.component.image.textpaster;

import com.octo.captcha.CaptchaException;
import java.awt.Graphics2D;
import java.text.AttributedString;
import java.awt.image.BufferedImage;
import com.octo.captcha.component.image.color.ColorGenerator;
import java.awt.Color;

public class SimpleTextPaster extends AbstractTextPaster
{
    public SimpleTextPaster(final Integer n, final Integer n2, final Color color) {
        super(n, n2, color);
    }
    
    public SimpleTextPaster(final Integer n, final Integer n2, final ColorGenerator colorGenerator) {
        super(n, n2, colorGenerator);
    }
    
    public SimpleTextPaster(final Integer n, final Integer n2, final ColorGenerator colorGenerator, final Boolean b) {
        super(n, n2, colorGenerator, b);
    }
    
    public BufferedImage pasteText(final BufferedImage bufferedImage, final AttributedString attributedString) throws CaptchaException {
        final int n = bufferedImage.getWidth() / 20;
        final int n2 = bufferedImage.getHeight() / 2;
        final BufferedImage copyBackground = this.copyBackground(bufferedImage);
        final Graphics2D pasteBackgroundAndSetTextColor = this.pasteBackgroundAndSetTextColor(copyBackground, bufferedImage);
        final MutableAttributedString mutableAttributedString = new MutableAttributedString(pasteBackgroundAndSetTextColor, attributedString, 2);
        mutableAttributedString.useMinimumSpacing(1.0);
        mutableAttributedString.moveTo(n, n2);
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
