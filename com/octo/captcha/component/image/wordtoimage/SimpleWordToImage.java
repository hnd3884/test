package com.octo.captcha.component.image.wordtoimage;

import com.octo.captcha.CaptchaException;
import java.awt.Graphics;
import java.text.AttributedString;
import java.awt.image.BufferedImage;
import java.awt.GraphicsEnvironment;
import java.awt.Font;

public class SimpleWordToImage extends AbstractWordToImage
{
    public int getMaxAcceptedWordLength() {
        return 10;
    }
    
    public int getMinAcceptedWordLength() {
        return 1;
    }
    
    @Deprecated
    public int getMaxAcceptedWordLenght() {
        return 10;
    }
    
    @Deprecated
    public int getMinAcceptedWordLenght() {
        return 1;
    }
    
    public int getImageHeight() {
        return 50;
    }
    
    public int getImageWidth() {
        return 100;
    }
    
    public int getMinFontSize() {
        return 10;
    }
    
    public Font getFont() {
        return GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts()[0];
    }
    
    public BufferedImage getBackground() {
        return new BufferedImage(this.getImageWidth(), this.getImageHeight(), 1);
    }
    
    @Override
    BufferedImage pasteText(final BufferedImage bufferedImage, final AttributedString attributedString) throws CaptchaException {
        final Graphics graphics = bufferedImage.getGraphics();
        graphics.drawString(attributedString.getIterator(), (this.getImageWidth() - this.getMaxAcceptedWordLength()) / 2, (this.getImageHeight() - this.getMinFontSize()) / 2);
        graphics.dispose();
        return bufferedImage;
    }
}
