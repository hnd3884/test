package com.octo.captcha.sound.speller;

import javax.sound.sampled.AudioInputStream;
import com.octo.captcha.sound.SoundCaptcha;

public class SpellerSound extends SoundCaptcha
{
    private String response;
    
    public SpellerSound(final String s, final AudioInputStream audioInputStream, final String response) {
        super(s, audioInputStream);
        this.response = response;
    }
    
    public Boolean validateResponse(final Object o) {
        if (o != null && o instanceof String) {
            return this.validateResponse((String)o);
        }
        return Boolean.FALSE;
    }
    
    public Boolean validateResponse(final String s) {
        return this.response.equalsIgnoreCase(s);
    }
}
