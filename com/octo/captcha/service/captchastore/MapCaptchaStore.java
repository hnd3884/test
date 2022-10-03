package com.octo.captcha.service.captchastore;

import java.util.Collection;
import java.util.Locale;
import com.octo.captcha.service.CaptchaServiceException;
import com.octo.captcha.Captcha;
import java.util.HashMap;
import java.util.Map;

public class MapCaptchaStore implements CaptchaStore
{
    Map store;
    
    public MapCaptchaStore() {
        this.store = new HashMap();
    }
    
    public boolean hasCaptcha(final String s) {
        return this.store.containsKey(s);
    }
    
    public void storeCaptcha(final String s, final Captcha captcha) throws CaptchaServiceException {
        this.store.put(s, new CaptchaAndLocale(captcha));
    }
    
    public void storeCaptcha(final String s, final Captcha captcha, final Locale locale) throws CaptchaServiceException {
        this.store.put(s, new CaptchaAndLocale(captcha, locale));
    }
    
    public Captcha getCaptcha(final String s) throws CaptchaServiceException {
        final CaptchaAndLocale value = this.store.get(s);
        return (value != null) ? value.getCaptcha() : null;
    }
    
    public Locale getLocale(final String s) throws CaptchaServiceException {
        final CaptchaAndLocale value = this.store.get(s);
        return (value != null) ? value.getLocale() : null;
    }
    
    public boolean removeCaptcha(final String s) {
        if (this.store.get(s) != null) {
            this.store.remove(s);
            return true;
        }
        return false;
    }
    
    public int getSize() {
        return this.store.size();
    }
    
    public Collection getKeys() {
        return this.store.keySet();
    }
    
    public void empty() {
        this.store = new HashMap();
    }
    
    public void initAndStart() {
    }
    
    public void cleanAndShutdown() {
        this.store.clear();
    }
}
