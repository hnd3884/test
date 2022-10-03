package com.octo.captcha.component.image.textpaster;

import com.octo.captcha.CaptchaException;
import java.text.AttributedString;
import java.awt.image.BufferedImage;

public interface TextPaster
{
    int getMaxAcceptedWordLength();
    
    int getMinAcceptedWordLength();
    
    BufferedImage pasteText(final BufferedImage p0, final AttributedString p1) throws CaptchaException;
}
