package com.octo.captcha.component.image.wordtoimage;

import com.octo.captcha.CaptchaException;
import java.text.AttributedString;
import java.awt.image.BufferedImage;
import java.awt.Font;
import com.octo.captcha.component.image.textpaster.TextPaster;
import com.octo.captcha.component.image.backgroundgenerator.BackgroundGenerator;
import com.octo.captcha.component.image.fontgenerator.FontGenerator;

public class ComposedWordToImage extends AbstractWordToImage
{
    private FontGenerator fontGenerator;
    private BackgroundGenerator background;
    private TextPaster textPaster;
    
    public ComposedWordToImage(final FontGenerator fontGenerator, final BackgroundGenerator background, final TextPaster textPaster) {
        this.background = background;
        this.fontGenerator = fontGenerator;
        this.textPaster = textPaster;
    }
    
    public ComposedWordToImage(final boolean b, final FontGenerator fontGenerator, final BackgroundGenerator background, final TextPaster textPaster) {
        super(b);
        this.fontGenerator = fontGenerator;
        this.background = background;
        this.textPaster = textPaster;
    }
    
    @Deprecated
    public int getMaxAcceptedWordLenght() {
        return this.textPaster.getMaxAcceptedWordLength();
    }
    
    @Deprecated
    public int getMinAcceptedWordLenght() {
        return this.textPaster.getMinAcceptedWordLength();
    }
    
    public int getMaxAcceptedWordLength() {
        return this.textPaster.getMaxAcceptedWordLength();
    }
    
    public int getMinAcceptedWordLength() {
        return this.textPaster.getMinAcceptedWordLength();
    }
    
    public int getImageHeight() {
        return this.background.getImageHeight();
    }
    
    public int getImageWidth() {
        return this.background.getImageWidth();
    }
    
    public int getMinFontSize() {
        return this.fontGenerator.getMinFontSize();
    }
    
    @Override
    Font getFont() {
        return this.fontGenerator.getFont();
    }
    
    @Override
    BufferedImage getBackground() {
        return this.background.getBackground();
    }
    
    @Override
    BufferedImage pasteText(final BufferedImage bufferedImage, final AttributedString attributedString) throws CaptchaException {
        return this.textPaster.pasteText(bufferedImage, attributedString);
    }
}
