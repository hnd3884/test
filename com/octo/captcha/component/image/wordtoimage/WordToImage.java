package com.octo.captcha.component.image.wordtoimage;

import com.octo.captcha.CaptchaException;
import java.awt.image.BufferedImage;

public interface WordToImage
{
    int getMaxAcceptedWordLength();
    
    int getMinAcceptedWordLength();
    
    int getImageHeight();
    
    int getImageWidth();
    
    int getMinFontSize();
    
    BufferedImage getImage(final String p0) throws CaptchaException;
}
