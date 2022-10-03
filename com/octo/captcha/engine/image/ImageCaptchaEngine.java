package com.octo.captcha.engine.image;

import com.octo.captcha.engine.CaptchaEngineException;
import com.octo.captcha.CaptchaFactory;
import com.octo.captcha.Captcha;
import java.util.Locale;
import com.octo.captcha.image.ImageCaptcha;
import com.octo.captcha.image.ImageCaptchaFactory;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Random;
import java.util.List;
import com.octo.captcha.engine.CaptchaEngine;

public abstract class ImageCaptchaEngine implements CaptchaEngine
{
    protected List factories;
    protected Random myRandom;
    
    public ImageCaptchaEngine() {
        this.factories = new ArrayList();
        this.myRandom = new SecureRandom();
    }
    
    public ImageCaptchaFactory getImageCaptchaFactory() {
        return this.factories.get(this.myRandom.nextInt(this.factories.size()));
    }
    
    public final ImageCaptcha getNextImageCaptcha() {
        return this.getImageCaptchaFactory().getImageCaptcha();
    }
    
    public ImageCaptcha getNextImageCaptcha(final Locale locale) {
        return this.getImageCaptchaFactory().getImageCaptcha(locale);
    }
    
    public final Captcha getNextCaptcha() {
        return this.getImageCaptchaFactory().getImageCaptcha();
    }
    
    public Captcha getNextCaptcha(final Locale locale) {
        return this.getImageCaptchaFactory().getImageCaptcha(locale);
    }
    
    public CaptchaFactory[] getFactories() {
        return this.factories.toArray(new CaptchaFactory[this.factories.size()]);
    }
    
    public void setFactories(final CaptchaFactory[] array) throws CaptchaEngineException {
        this.checkNotNullOrEmpty(array);
        final ArrayList factories = new ArrayList();
        for (int i = 0; i < array.length; ++i) {
            if (!ImageCaptchaFactory.class.isAssignableFrom(array[i].getClass())) {
                throw new CaptchaEngineException("This factory is not an image captcha factory " + array[i].getClass());
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
}
