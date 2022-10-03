package com.octo.captcha;

import java.util.Locale;

public interface CaptchaFactory
{
    Captcha getCaptcha();
    
    Captcha getCaptcha(final Locale p0);
}
