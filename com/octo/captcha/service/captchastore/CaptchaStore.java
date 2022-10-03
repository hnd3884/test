package com.octo.captcha.service.captchastore;

import java.util.Collection;
import java.util.Locale;
import com.octo.captcha.service.CaptchaServiceException;
import com.octo.captcha.Captcha;

public interface CaptchaStore
{
    boolean hasCaptcha(final String p0);
    
    @Deprecated
    void storeCaptcha(final String p0, final Captcha p1) throws CaptchaServiceException;
    
    void storeCaptcha(final String p0, final Captcha p1, final Locale p2) throws CaptchaServiceException;
    
    boolean removeCaptcha(final String p0);
    
    Captcha getCaptcha(final String p0) throws CaptchaServiceException;
    
    Locale getLocale(final String p0) throws CaptchaServiceException;
    
    int getSize();
    
    Collection getKeys();
    
    void empty();
    
    void initAndStart();
    
    void cleanAndShutdown();
}
