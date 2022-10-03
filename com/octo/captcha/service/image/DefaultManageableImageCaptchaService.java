package com.octo.captcha.service.image;

import com.octo.captcha.engine.CaptchaEngine;
import com.octo.captcha.service.captchastore.CaptchaStore;
import com.octo.captcha.engine.image.gimpy.DefaultGimpyEngine;
import com.octo.captcha.service.captchastore.FastHashMapCaptchaStore;

public class DefaultManageableImageCaptchaService extends AbstractManageableImageCaptchaService implements ImageCaptchaService
{
    public DefaultManageableImageCaptchaService() {
        super(new FastHashMapCaptchaStore(), new DefaultGimpyEngine(), 180, 100000, 75000);
    }
    
    public DefaultManageableImageCaptchaService(final int n, final int n2, final int n3) {
        super(new FastHashMapCaptchaStore(), new DefaultGimpyEngine(), n, n2, n3);
    }
    
    public DefaultManageableImageCaptchaService(final CaptchaStore captchaStore, final CaptchaEngine captchaEngine, final int n, final int n2, final int n3) {
        super(captchaStore, captchaEngine, n, n2, n3);
    }
}
