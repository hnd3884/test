package com.octo.captcha.engine;

import com.octo.captcha.CaptchaFactory;
import java.util.Locale;
import com.octo.captcha.Captcha;

public interface CaptchaEngine
{
    Captcha getNextCaptcha();
    
    Captcha getNextCaptcha(final Locale p0);
    
    CaptchaFactory[] getFactories();
    
    void setFactories(final CaptchaFactory[] p0) throws CaptchaEngineException;
}
