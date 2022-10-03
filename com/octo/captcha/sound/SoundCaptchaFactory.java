package com.octo.captcha.sound;

import java.util.Locale;
import com.octo.captcha.Captcha;
import com.octo.captcha.CaptchaFactory;

public abstract class SoundCaptchaFactory implements CaptchaFactory
{
    public Captcha getCaptcha() {
        return this.getSoundCaptcha();
    }
    
    public Captcha getCaptcha(final Locale locale) {
        return this.getSoundCaptcha(locale);
    }
    
    public abstract SoundCaptcha getSoundCaptcha();
    
    public abstract SoundCaptcha getSoundCaptcha(final Locale p0);
}
