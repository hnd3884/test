package com.octo.captcha.engine.sound;

import com.octo.captcha.sound.SoundCaptcha;
import com.octo.captcha.engine.CaptchaEngineException;
import com.octo.captcha.sound.SoundCaptchaFactory;
import com.octo.captcha.CaptchaFactory;
import java.util.Locale;
import com.octo.captcha.Captcha;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Random;
import java.util.List;
import com.octo.captcha.engine.CaptchaEngine;

public abstract class SoundCaptchaEngine implements CaptchaEngine
{
    protected List factories;
    protected Random myRandom;
    
    public SoundCaptchaEngine() {
        this.factories = new ArrayList();
        this.myRandom = new SecureRandom();
    }
    
    public final Captcha getNextCaptcha() {
        return this.getNextSoundCaptcha();
    }
    
    public final Captcha getNextCaptcha(final Locale locale) {
        return this.getNextSoundCaptcha(locale);
    }
    
    public CaptchaFactory[] getFactories() {
        return this.factories.toArray(new CaptchaFactory[this.factories.size()]);
    }
    
    public void setFactories(final CaptchaFactory[] array) throws CaptchaEngineException {
        this.checkNotNullOrEmpty(array);
        final ArrayList factories = new ArrayList();
        for (int i = 0; i < array.length; ++i) {
            if (!SoundCaptchaFactory.class.isAssignableFrom(array[i].getClass())) {
                throw new CaptchaEngineException("This factory is not an sound captcha factory " + array[i].getClass());
            }
            factories.add(array[i]);
        }
        this.factories = factories;
    }
    
    protected void checkNotNullOrEmpty(final CaptchaFactory[] array) {
        if (array == null || array.length == 0) {
            throw new CaptchaEngineException("impossible to set null or empty factories");
        }
    }
    
    public SoundCaptchaFactory getSoundCaptchaFactory() {
        return this.factories.get(this.myRandom.nextInt(this.factories.size()));
    }
    
    public SoundCaptcha getNextSoundCaptcha() {
        return this.getSoundCaptchaFactory().getSoundCaptcha();
    }
    
    public SoundCaptcha getNextSoundCaptcha(final Locale locale) {
        return this.getSoundCaptchaFactory().getSoundCaptcha(locale);
    }
}
