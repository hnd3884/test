package com.octo.captcha.text;

import java.util.Locale;
import com.octo.captcha.Captcha;
import com.octo.captcha.CaptchaFactory;

public abstract class TextCaptchaFactory implements CaptchaFactory
{
    public final Captcha getCaptcha() {
        return this.getTextCaptcha();
    }
    
    public final Captcha getCaptcha(final Locale locale) {
        return this.getTextCaptcha(locale);
    }
    
    public abstract TextCaptcha getTextCaptcha();
    
    public abstract TextCaptcha getTextCaptcha(final Locale p0);
}
