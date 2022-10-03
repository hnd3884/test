package com.octo.captcha.engine.image;

import java.util.Arrays;
import com.octo.captcha.CaptchaFactory;
import com.octo.captcha.image.ImageCaptchaFactory;

public abstract class DefaultImageCaptchaEngine extends ImageCaptchaEngine
{
    public DefaultImageCaptchaEngine(final ImageCaptchaFactory[] array) {
        this.checkNotNullOrEmpty(array);
        this.factories = Arrays.asList(array);
    }
}
