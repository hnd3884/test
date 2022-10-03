package com.octo.captcha.service.captchastore;

import java.util.Locale;
import com.octo.captcha.Captcha;
import java.io.Serializable;

public class CaptchaAndLocale implements Serializable
{
    private Captcha captcha;
    private Locale locale;
    
    public CaptchaAndLocale(final Captcha captcha) {
        this.captcha = captcha;
    }
    
    public CaptchaAndLocale(final Captcha captcha, final Locale locale) {
        this.captcha = captcha;
        this.locale = locale;
    }
    
    public Captcha getCaptcha() {
        return this.captcha;
    }
    
    public void setCaptcha(final Captcha captcha) {
        this.captcha = captcha;
    }
    
    public Locale getLocale() {
        return this.locale;
    }
    
    public void setLocale(final Locale locale) {
        this.locale = locale;
    }
}
