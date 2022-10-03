package com.octo.captcha.engine.sound;

import java.util.Arrays;
import com.octo.captcha.CaptchaFactory;
import com.octo.captcha.sound.SoundCaptchaFactory;

public class DefaultSoundCaptchaEngine extends SoundCaptchaEngine
{
    public DefaultSoundCaptchaEngine(final SoundCaptchaFactory[] array) {
        this.checkNotNullOrEmpty(array);
        this.factories = Arrays.asList(array);
    }
}
