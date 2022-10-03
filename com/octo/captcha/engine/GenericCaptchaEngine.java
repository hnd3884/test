package com.octo.captcha.engine;

import java.util.Locale;
import com.octo.captcha.Captcha;
import com.octo.captcha.CaptchaException;
import java.security.SecureRandom;
import java.util.Random;
import com.octo.captcha.CaptchaFactory;

public class GenericCaptchaEngine implements CaptchaEngine
{
    private CaptchaFactory[] factories;
    private Random myRandom;
    
    public GenericCaptchaEngine(final CaptchaFactory[] factories) {
        this.myRandom = new SecureRandom();
        this.factories = factories;
        if (this.factories == null || this.factories.length == 0) {
            throw new CaptchaException("GenericCaptchaEngine cannot be constructed with a null or empty factories array");
        }
    }
    
    public CaptchaFactory[] getFactories() {
        return this.factories;
    }
    
    public void setFactories(final CaptchaFactory[] factories) throws CaptchaEngineException {
        if (factories == null || factories.length == 0) {
            throw new CaptchaEngineException("impossible to set null or empty factories");
        }
        this.factories = factories;
    }
    
    public Captcha getNextCaptcha() {
        return this.factories[this.myRandom.nextInt(this.factories.length)].getCaptcha();
    }
    
    public Captcha getNextCaptcha(final Locale locale) {
        return this.factories[this.myRandom.nextInt(this.factories.length)].getCaptcha(locale);
    }
}
