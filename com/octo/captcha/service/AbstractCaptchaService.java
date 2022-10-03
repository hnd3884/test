package com.octo.captcha.service;

import com.octo.captcha.Captcha;
import java.util.Locale;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;
import com.octo.captcha.engine.CaptchaEngine;
import com.octo.captcha.service.captchastore.CaptchaStore;

public abstract class AbstractCaptchaService implements CaptchaService
{
    protected CaptchaStore store;
    protected CaptchaEngine engine;
    protected Log logger;
    
    protected AbstractCaptchaService(final CaptchaStore store, final CaptchaEngine engine) {
        if (engine == null || store == null) {
            throw new IllegalArgumentException("Store or gimpy can't be null");
        }
        this.engine = engine;
        this.store = store;
        (this.logger = LogFactory.getLog((Class)this.getClass())).info((Object)("Init " + this.store.getClass().getName()));
        this.store.initAndStart();
    }
    
    public Object getChallengeForID(final String s) throws CaptchaServiceException {
        return this.getChallengeForID(s, Locale.getDefault());
    }
    
    public Object getChallengeForID(final String s, final Locale locale) throws CaptchaServiceException {
        Captcha captcha;
        if (!this.store.hasCaptcha(s)) {
            captcha = this.generateAndStoreCaptcha(locale, s);
        }
        else {
            captcha = this.store.getCaptcha(s);
            if (captcha == null) {
                captcha = this.generateAndStoreCaptcha(locale, s);
            }
            else if (captcha.hasGetChalengeBeenCalled()) {
                captcha = this.generateAndStoreCaptcha(locale, s);
            }
        }
        final Object challengeClone = this.getChallengeClone(captcha);
        captcha.disposeChallenge();
        return challengeClone;
    }
    
    public String getQuestionForID(final String s, final Locale locale) throws CaptchaServiceException {
        Captcha captcha;
        if (!this.store.hasCaptcha(s)) {
            captcha = this.generateAndStoreCaptcha(locale, s);
        }
        else {
            captcha = this.store.getCaptcha(s);
            if (captcha == null) {
                captcha = this.generateAndStoreCaptcha(locale, s);
            }
            else if (locale != null && !locale.equals(this.store.getLocale(s))) {
                captcha = this.generateAndStoreCaptcha(locale, s);
            }
        }
        return captcha.getQuestion();
    }
    
    public String getQuestionForID(final String s) throws CaptchaServiceException {
        return this.getQuestionForID(s, Locale.getDefault());
    }
    
    public Boolean validateResponseForID(final String s, final Object o) throws CaptchaServiceException {
        if (!this.store.hasCaptcha(s)) {
            throw new CaptchaServiceException("Invalid ID, could not validate unexisting or already validated captcha");
        }
        final Boolean validateResponse = this.store.getCaptcha(s).validateResponse(o);
        this.store.removeCaptcha(s);
        return validateResponse;
    }
    
    protected Captcha generateAndStoreCaptcha(final Locale locale, final String s) {
        final Captcha nextCaptcha = this.engine.getNextCaptcha(locale);
        this.store.storeCaptcha(s, nextCaptcha, locale);
        return nextCaptcha;
    }
    
    protected abstract Object getChallengeClone(final Captcha p0);
}
