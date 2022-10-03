package com.octo.captcha.service.multitype;

import com.octo.captcha.text.TextCaptcha;
import java.io.InputStream;
import com.octo.captcha.sound.SoundCaptcha;
import java.awt.image.ImageObserver;
import java.awt.Image;
import com.octo.captcha.image.ImageCaptcha;
import com.octo.captcha.Captcha;
import javax.sound.sampled.AudioInputStream;
import java.util.Locale;
import com.octo.captcha.service.CaptchaServiceException;
import java.awt.image.BufferedImage;
import com.octo.captcha.service.captchastore.CaptchaStore;
import com.octo.captcha.service.captchastore.FastHashMapCaptchaStore;
import com.octo.captcha.engine.CaptchaEngine;
import com.octo.captcha.service.AbstractManageableCaptchaService;

public class GenericManageableCaptchaService extends AbstractManageableCaptchaService implements MultiTypeCaptchaService
{
    public GenericManageableCaptchaService(final CaptchaEngine captchaEngine, final int n, final int n2, final int n3) {
        this(new FastHashMapCaptchaStore(), captchaEngine, n, n2, n3);
    }
    
    public GenericManageableCaptchaService(final CaptchaStore captchaStore, final CaptchaEngine captchaEngine, final int n, final int n2, final int n3) {
        super(captchaStore, captchaEngine, n, n2, n3);
    }
    
    public BufferedImage getImageChallengeForID(final String s) throws CaptchaServiceException {
        return (BufferedImage)this.getChallengeForID(s);
    }
    
    public BufferedImage getImageChallengeForID(final String s, final Locale locale) throws CaptchaServiceException {
        return (BufferedImage)this.getChallengeForID(s, locale);
    }
    
    public AudioInputStream getSoundChallengeForID(final String s) throws CaptchaServiceException {
        return (AudioInputStream)this.getChallengeForID(s);
    }
    
    public AudioInputStream getSoundChallengeForID(final String s, final Locale locale) throws CaptchaServiceException {
        return (AudioInputStream)this.getChallengeForID(s, locale);
    }
    
    public String getTextChallengeForID(final String s) throws CaptchaServiceException {
        return (String)this.getChallengeForID(s);
    }
    
    public String getTextChallengeForID(final String s, final Locale locale) throws CaptchaServiceException {
        return (String)this.getChallengeForID(s, locale);
    }
    
    @Override
    protected Object getChallengeClone(final Captcha captcha) {
        final Class<? extends Captcha> class1 = captcha.getClass();
        if (ImageCaptcha.class.isAssignableFrom(class1)) {
            final BufferedImage bufferedImage = (BufferedImage)captcha.getChallenge();
            final BufferedImage bufferedImage2 = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), bufferedImage.getType());
            bufferedImage2.getGraphics().drawImage(bufferedImage, 0, 0, bufferedImage2.getWidth(), bufferedImage2.getHeight(), null);
            bufferedImage2.getGraphics().dispose();
            return bufferedImage2;
        }
        if (SoundCaptcha.class.isAssignableFrom(class1)) {
            final AudioInputStream audioInputStream = (AudioInputStream)captcha.getChallenge();
            return new AudioInputStream(audioInputStream, audioInputStream.getFormat(), audioInputStream.getFrameLength());
        }
        if (TextCaptcha.class.isAssignableFrom(class1)) {
            return String.valueOf(captcha.getChallenge());
        }
        throw new CaptchaServiceException("Unknown captcha type, can't clone challenge captchaClass:'" + captcha.getClass() + "'");
    }
}
