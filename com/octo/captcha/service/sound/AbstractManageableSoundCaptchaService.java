package com.octo.captcha.service.sound;

import java.io.InputStream;
import com.octo.captcha.Captcha;
import java.util.Locale;
import com.octo.captcha.service.CaptchaServiceException;
import javax.sound.sampled.AudioInputStream;
import com.octo.captcha.engine.CaptchaEngine;
import com.octo.captcha.service.captchastore.CaptchaStore;
import com.octo.captcha.service.AbstractManageableCaptchaService;

public abstract class AbstractManageableSoundCaptchaService extends AbstractManageableCaptchaService implements SoundCaptchaService
{
    protected AbstractManageableSoundCaptchaService(final CaptchaStore captchaStore, final CaptchaEngine captchaEngine, final int n, final int n2, final int n3) {
        super(captchaStore, captchaEngine, n, n2, n3);
    }
    
    public AudioInputStream getSoundChallengeForID(final String s) throws CaptchaServiceException {
        return (AudioInputStream)this.getChallengeForID(s);
    }
    
    public AudioInputStream getSoundChallengeForID(final String s, final Locale locale) throws CaptchaServiceException {
        return (AudioInputStream)this.getChallengeForID(s, locale);
    }
    
    @Override
    protected Object getChallengeClone(final Captcha captcha) {
        final AudioInputStream audioInputStream = (AudioInputStream)captcha.getChallenge();
        return new AudioInputStream(audioInputStream, audioInputStream.getFormat(), audioInputStream.getFrameLength());
    }
}
