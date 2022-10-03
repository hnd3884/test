package com.octo.captcha.component.image.wordtoimage;

import java.awt.Font;
import java.text.AttributedCharacterIterator;
import java.awt.font.TextAttribute;
import java.text.AttributedString;
import com.octo.captcha.CaptchaException;
import java.awt.image.BufferedImage;

public abstract class AbstractWordToImage implements WordToImage
{
    private boolean manageFontByCharacter;
    
    protected AbstractWordToImage() {
        this.manageFontByCharacter = true;
    }
    
    protected AbstractWordToImage(final boolean manageFontByCharacter) {
        this.manageFontByCharacter = true;
        this.manageFontByCharacter = manageFontByCharacter;
    }
    
    public BufferedImage getImage(final String s) throws CaptchaException {
        return this.pasteText(this.getBackground(), this.getAttributedString(s, this.checkWordLength(s)));
    }
    
    AttributedString getAttributedString(final String s, final int n) {
        final AttributedString attributedString = new AttributedString(s);
        Font font = this.getFont();
        for (int i = 0; i < n; ++i) {
            attributedString.addAttribute(TextAttribute.FONT, font, i, i + 1);
            if (this.manageFontByCharacter) {
                font = this.getFont();
            }
        }
        return attributedString;
    }
    
    int checkWordLength(final String s) throws CaptchaException {
        if (s == null) {
            throw new CaptchaException("null word");
        }
        final int length = s.length();
        if (length > this.getMaxAcceptedWordLength() || length < this.getMinAcceptedWordLength()) {
            throw new CaptchaException("invalid length word");
        }
        return length;
    }
    
    abstract Font getFont();
    
    abstract BufferedImage getBackground();
    
    abstract BufferedImage pasteText(final BufferedImage p0, final AttributedString p1) throws CaptchaException;
}
