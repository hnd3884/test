package com.octo.captcha.engine.sound;

import com.octo.captcha.CaptchaException;
import java.util.Collection;
import java.util.Arrays;
import com.octo.captcha.CaptchaFactory;
import com.octo.captcha.sound.SoundCaptchaFactory;

public abstract class ListSoundCaptchaEngine extends SoundCaptchaEngine
{
    public ListSoundCaptchaEngine() {
        this.buildInitialFactories();
        this.checkFactoriesSize();
    }
    
    protected abstract void buildInitialFactories();
    
    public boolean addFactory(final SoundCaptchaFactory soundCaptchaFactory) {
        return soundCaptchaFactory != null && this.factories.add(soundCaptchaFactory);
    }
    
    public void addFactories(final SoundCaptchaFactory[] array) {
        this.checkNotNullOrEmpty(array);
        this.factories.addAll(Arrays.asList(array));
    }
    
    private void checkFactoriesSize() {
        if (this.factories.size() == 0) {
            throw new CaptchaException("This soundEngine has no factories. Please initialize it properly with the buildInitialFactory() called by the constructor or the addFactory() mehtod later!");
        }
    }
}
