package com.octo.captcha.component.sound.wordtosound;

import java.util.Locale;
import com.octo.captcha.CaptchaException;
import javax.sound.sampled.AudioInputStream;

public interface WordToSound
{
    @Deprecated
    int getMaxAcceptedWordLenght();
    
    @Deprecated
    int getMinAcceptedWordLenght();
    
    int getMaxAcceptedWordLength();
    
    int getMinAcceptedWordLength();
    
    AudioInputStream getSound(final String p0) throws CaptchaException;
    
    AudioInputStream getSound(final String p0, final Locale p1) throws CaptchaException;
}
