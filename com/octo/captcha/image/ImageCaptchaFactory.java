package com.octo.captcha.image;

import java.util.Locale;
import com.octo.captcha.Captcha;
import com.octo.captcha.CaptchaFactory;

public abstract class ImageCaptchaFactory implements CaptchaFactory
{
    public final Captcha getCaptcha() {
        return this.getImageCaptcha();
    }
    
    public final Captcha getCaptcha(final Locale locale) {
        return this.getImageCaptcha(locale);
    }
    
    public abstract ImageCaptcha getImageCaptcha();
    
    public abstract ImageCaptcha getImageCaptcha(final Locale p0);
}
