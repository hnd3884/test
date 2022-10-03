package com.octo.captcha.engine.image;

import com.octo.captcha.CaptchaException;
import java.util.Collection;
import java.util.Arrays;
import com.octo.captcha.CaptchaFactory;
import com.octo.captcha.image.ImageCaptchaFactory;

public abstract class ListImageCaptchaEngine extends ImageCaptchaEngine
{
    public ListImageCaptchaEngine() {
        this.buildInitialFactories();
        this.checkFactoriesSize();
    }
    
    protected abstract void buildInitialFactories();
    
    public boolean addFactory(final ImageCaptchaFactory imageCaptchaFactory) {
        return imageCaptchaFactory != null && this.factories.add(imageCaptchaFactory);
    }
    
    public void addFactories(final ImageCaptchaFactory[] array) {
        this.checkNotNullOrEmpty(array);
        this.factories.addAll(Arrays.asList(array));
    }
    
    private void checkFactoriesSize() {
        if (this.factories.size() == 0) {
            throw new CaptchaException("This gimpy has no factories. Please initialize it properly with the buildInitialFactory() called by the constructor or the addFactory() mehtod later!");
        }
    }
}
